package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.DailyChallenge
import com.thaqalayn.app.model.DailyCrossword
import com.thaqalayn.app.model.DailyDua
import com.thaqalayn.app.model.DailyDuasData
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val json = Json { ignoreUnknownKeys = true }

internal fun todayDateString(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

internal fun dayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

/**
 * Deterministic rotation-of-the-day pick with a per-day cache, mirroring the iOS
 * providers: the same item stays pinned for a calendar day even if the list grows.
 */
private fun resolveDailyIndex(prefs: SharedPreferences, cacheKey: String, count: Int): Int {
    val dateString = todayDateString()
    val cachedDate = prefs.getString("$cacheKey.date", null)
    val cachedIndex = prefs.getInt("$cacheKey.index", -1)
    if (cachedDate == dateString && cachedIndex in 0 until count) return cachedIndex

    val index = dayOfYear() % count
    prefs.edit()
        .putString("$cacheKey.date", dateString)
        .putInt("$cacheKey.index", index)
        .apply()
    return index
}

/**
 * Today's verse from the 365-verse themed pool (iOS DailyVerseProvider). Selection
 * is a pure function of the date (see DailyVerseSelector), so the Today hero and
 * the daily-verse notification always agree and nothing needs caching.
 */
object DailyVerseProvider {
    private val lock = Any()
    @Volatile
    private var selector: DailyVerseSelector? = null

    var today by mutableStateOf<DailyVerseSelection?>(null)
        private set

    fun init(context: Context) {
        loadIfNeeded(context)
        refreshIfDayChanged()
    }

    /** Parse daily_verses.json once; workers may call this before the app's init thread has. */
    fun loadIfNeeded(context: Context) {
        if (selector != null) return
        synchronized(lock) {
            if (selector != null) return
            selector = try {
                val text = context.assets.open("daily_verses.json").bufferedReader().use { it.readText() }
                DailyVerseSelector(json.decodeFromString<DailyVersePool>(text))
            } catch (e: Exception) {
                null
            }
        }
    }

    /** The verse for any date; null only if the pool failed to load. */
    fun verseFor(date: Date): DailyVerseSelection? {
        val s = selector ?: return null
        return synchronized(lock) { s.verseFor(date) }
    }

    /** Called when the Today tab appears, so a day boundary crossed in the background shows. */
    fun refreshIfDayChanged() {
        val resolved = verseFor(Date()) ?: return
        if (resolved != today) today = resolved
    }
}

/** Loads daily_challenges.json and returns today's challenge deterministically. */
object DailyChallengeProvider {
    private lateinit var prefs: SharedPreferences
    private var all: List<DailyChallenge> = emptyList()

    var today by mutableStateOf<DailyChallenge?>(null)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        all = try {
            val text = context.assets.open("daily_challenges.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<DailyChallenge>>(text)
        } catch (e: Exception) {
            emptyList()
        }
        refreshIfDayChanged()
    }

    fun refreshIfDayChanged() {
        if (all.isEmpty()) return
        val index = resolveDailyIndex(prefs, "ThaqalaynDailyChallengeCache", all.size)
        val resolved = all[index]
        if (resolved.id != today?.id) today = resolved
    }
}

/** Loads daily_crosswords.json and returns today's puzzle deterministically. */
object DailyCrosswordProvider {
    private lateinit var prefs: SharedPreferences
    private var all: List<DailyCrossword> = emptyList()

    var today by mutableStateOf<DailyCrossword?>(null)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        all = try {
            val text = context.assets.open("daily_crosswords.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<DailyCrossword>>(text)
        } catch (e: Exception) {
            emptyList()
        }
        refreshIfDayChanged()
    }

    fun refreshIfDayChanged() {
        if (all.isEmpty()) return
        val index = resolveDailyIndex(prefs, "ThaqalaynDailyCrosswordCache", all.size)
        val resolved = all[index]
        if (resolved.id != today?.id) today = resolved
    }
}

/** Loads the daily_duas.json bundle; dua-of-the-day rotates by day of year. */
object DuasManager {
    var duas by mutableStateOf<List<DailyDua>>(emptyList())
        private set

    fun init(context: Context) {
        duas = try {
            val text = context.assets.open("daily_duas.json").bufferedReader().use { it.readText() }
            json.decodeFromString<DailyDuasData>(text).duas
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun duaOfTheDay(): DailyDua? {
        if (duas.isEmpty()) return null
        return duas[dayOfYear() % duas.size]
    }

    fun byId(id: String): DailyDua? = duas.firstOrNull { it.id == id }
}
