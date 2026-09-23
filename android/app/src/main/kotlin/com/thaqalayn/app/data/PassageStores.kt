package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.Passage
import com.thaqalayn.app.model.PassageQuiz
import com.thaqalayn.app.model.PassageQuizResult
import com.thaqalayn.app.model.PassageRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

private val storeJson = Json { ignoreUnknownKeys = true }

/**
 * Per-surah JSON asset cache shared by the passage and quiz stores. A surah's file
 * is parsed once, off the main thread, into an observable map; a surah with no
 * file (or one that fails to decode) caches as empty, so "not generated yet" and
 * "loaded" are both answered by [isLoaded] and screens recompose when it flips.
 */
private class SurahAssetCache<T>(
    private val prefix: String,
    private val decode: (String) -> Map<String, T>
) {
    private val cache = mutableStateMapOf<Int, Map<String, T>>()
    private val mutex = Mutex()

    fun isLoaded(surah: Int): Boolean = cache.containsKey(surah)

    fun peek(surah: Int): Map<String, T> = cache[surah].orEmpty()

    suspend fun load(context: Context, surah: Int): Map<String, T> {
        cache[surah]?.let { return it }
        return mutex.withLock {
            cache[surah]?.let { return@withLock it }
            val parsed = withContext(Dispatchers.IO) {
                try {
                    val text = context.assets.open("${prefix}_$surah.json").bufferedReader().use { it.readText() }
                    decode(text)
                } catch (e: FileNotFoundException) {
                    emptyMap()
                } catch (e: Exception) {
                    // A shipped file that fails to decode is a data bug, not "none yet".
                    Log.e("SurahAssetCache", "${prefix}_$surah.json failed to decode", e)
                    emptyMap()
                }
            }
            cache[surah] = parsed
            parsed
        }
    }
}

/**
 * Lazily loads passages_<surah>.json (iOS PassageStore). A missing file means no
 * commentary has been generated for that surah yet.
 */
object PassageStore {
    private lateinit var appContext: Context
    private val files = SurahAssetCache("passages") { storeJson.decodeFromString<Map<String, Passage>>(it) }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun load(surah: Int) {
        files.load(appContext, surah)
    }

    fun isLoaded(surah: Int): Boolean = files.isLoaded(surah)

    fun passage(surah: Int, index: Int): Passage? = files.peek(surah)[index.toString()]

    fun hasCommentary(surah: Int, index: Int): Boolean = passage(surah, index) != null

    /**
     * Display title: the commentary title when it has shipped, else "Verses X to Y".
     * Every list, card and Continue Reading line uses this.
     */
    fun title(ref: PassageRef): String =
        passage(ref.surah, ref.index)?.title?.en ?: "Verses ${ref.rangeLabel}"
}

/**
 * Lazily loads quiz_<surah>.json once per surah (iOS QuizStore). A surah with no
 * file, or one that fails to decode, simply has no quizzes.
 */
object QuizStore {
    private lateinit var appContext: Context
    private val files = SurahAssetCache("quiz") { storeJson.decodeFromString<Map<String, PassageQuiz>>(it) }

    /** Surahs that ship a quiz file, from one listing of the asset root. */
    private val shippedSurahs: Set<Int> by lazy {
        val names = appContext.assets.list("").orEmpty()
        names.mapNotNull { name ->
            if (name.startsWith("quiz_") && name.endsWith(".json")) {
                name.removePrefix("quiz_").removeSuffix(".json").toIntOrNull()
            } else {
                null
            }
        }.toSet()
    }

    var shippedQuizCount by mutableStateOf<Int?>(null)
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun load(surah: Int) {
        files.load(appContext, surah)
    }

    fun isLoaded(surah: Int): Boolean = files.isLoaded(surah)

    fun quiz(ref: PassageRef): PassageQuiz? = files.peek(ref.surah)[ref.index.toString()]

    fun hasQuiz(ref: PassageRef): Boolean = quiz(ref) != null

    /** Passage indices of this surah that have a quiz. */
    fun quizIndices(surah: Int): Set<Int> = files.peek(surah).keys.mapNotNull { it.toIntOrNull() }.toSet()

    /** Quizzes shipped across the whole Quran, the denominator on the Progress tab. */
    suspend fun loadShippedQuizCount(): Int {
        shippedQuizCount?.let { return it }
        var total = 0
        for (surah in withContext(Dispatchers.IO) { shippedSurahs }.sorted()) {
            total += files.load(appContext, surah).size
        }
        shippedQuizCount = total
        return total
    }
}

/**
 * Best quiz score per passage (iOS QuizResultsStore). Local only, on purpose: the
 * quiz is a self-check, not reading progress.
 */
object QuizResultsStore {
    private const val STORAGE_KEY = "passageQuizBest"
    private lateinit var prefs: SharedPreferences

    var best by mutableStateOf<Map<String, PassageQuizResult>>(emptyMap())
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_passages", Context.MODE_PRIVATE)
        best = prefs.getString(STORAGE_KEY, null)?.let {
            try {
                storeJson.decodeFromString<Map<String, PassageQuizResult>>(it)
            } catch (e: Exception) {
                null
            }
        } ?: emptyMap()
    }

    fun best(ref: PassageRef): PassageQuizResult? = best[ref.id]

    /** Passage index to best score, for the list rows. */
    fun bestScores(surah: Int): Map<Int, Int> =
        best.values.filter { it.surah == surah }.associate { it.index to it.score }

    /** Passages with at least one completed attempt. */
    val takenCount: Int get() = best.size

    /** Passages whose best attempt answered every question. */
    val fullMarksCount: Int get() = best.values.count { it.score == it.total }

    /** Mean best score per passage, null until a quiz has been taken. */
    val averageBestScore: Double?
        get() = if (best.isEmpty()) null else best.values.sumOf { it.score }.toDouble() / best.size

    /** Keeps the higher score; an equal score keeps the newer attempt. */
    fun record(result: PassageQuizResult) {
        val old = best[result.key]
        if (old != null && old.score > result.score) return
        best = best + (result.key to result)
        prefs.edit().putString(STORAGE_KEY, storeJson.encodeToString(best)).apply()
    }
}

/**
 * Which passages the reader has finished Understanding (iOS PassageStageStore).
 * Local only, like the quiz scores.
 */
object PassageStageStore {
    private const val STORAGE_KEY = "passageUnderstood"
    private lateinit var prefs: SharedPreferences

    /** Passage ids ("surah:index") whose Understanding has been finished. */
    var understood by mutableStateOf<Set<String>>(emptySet())
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_passages", Context.MODE_PRIVATE)
        understood = prefs.getStringSet(STORAGE_KEY, null)?.toSet() ?: emptySet()
    }

    fun isUnderstood(ref: PassageRef): Boolean = ref.id in understood

    /** Passage indices understood in a surah, for the list rings. */
    fun understoodIndices(surah: Int): Set<Int> {
        val prefix = "$surah:"
        return understood.mapNotNull { if (it.startsWith(prefix)) it.removePrefix(prefix).toIntOrNull() else null }.toSet()
    }

    fun markUnderstood(ref: PassageRef) {
        if (ref.id in understood) return
        understood = understood + ref.id
        save()
    }

    fun unmarkUnderstood(ref: PassageRef) {
        if (ref.id !in understood) return
        understood = understood - ref.id
        save()
    }

    private fun save() {
        prefs.edit().putStringSet(STORAGE_KEY, understood).apply()
    }
}

/**
 * Passage read state derived from the per-verse progress ProgressManager already
 * stores (iOS PassageProgress). A passage is read when every verse in it is read.
 */
object PassageProgress {
    fun isRead(ref: PassageRef, readVerseKeys: Set<String>): Boolean =
        ref.verses.all { "${ref.surah}:$it" in readVerseKeys }

    fun readCount(refs: List<PassageRef>, readVerseKeys: Set<String>): Int =
        refs.count { isRead(it, readVerseKeys) }
}
