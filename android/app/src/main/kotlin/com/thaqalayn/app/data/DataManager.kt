package com.thaqalayn.app.data

import android.content.Context
import com.thaqalayn.app.model.QuranData
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.SurahWithTafsir
import com.thaqalayn.app.model.TafsirData
import com.thaqalayn.app.model.TafsirVerse
import com.thaqalayn.app.model.VerseWithTafsir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads Quran + tafsir JSON from assets, mirroring iOS DataManager.
 * quran_data.json holds all surahs/verses; tafsir_<n>.json holds per-surah tafsir.
 */
class DataManager private constructor(private val appContext: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var quranData: QuranData? = null
    private val quranMutex = Mutex()

    private val tafsirCache = mutableMapOf<Int, TafsirData?>()
    private val tafsirMutex = Mutex()

    @Volatile
    private var searchIndex: QuranSearchIndex? = null
    private val searchIndexMutex = Mutex()

    suspend fun loadQuranData(): QuranData {
        quranData?.let { return it }
        return quranMutex.withLock {
            quranData?.let { return it }
            val loaded = withContext(Dispatchers.IO) {
                val text = appContext.assets.open("quran_data.json")
                    .bufferedReader().use { it.readText() }
                json.decodeFromString<QuranData>(text)
            }
            quranData = loaded
            loaded
        }
    }

    suspend fun surahs(): List<Surah> = loadQuranData().surahs

    suspend fun surah(number: Int): Surah? =
        loadQuranData().surahs.firstOrNull { it.number == number }

    /** Load a surah with its verses and any bundled tafsir (access control stays at UI level). */
    suspend fun loadSurahWithTafsir(surah: Surah): SurahWithTafsir {
        val data = loadQuranData()
        val tafsirData = loadTafsirData(surah.number)
        val surahVerses = data.verses[surah.number.toString()].orEmpty()
        val verses = (1..surah.versesCount).mapNotNull { verseNumber ->
            surahVerses[verseNumber.toString()]?.let { verse ->
                VerseWithTafsir(
                    number = verseNumber,
                    verse = verse,
                    tafsir = tafsirData?.verses?.get(verseNumber.toString())
                )
            }
        }
        return SurahWithTafsir(surah = surah, verses = verses)
    }

    suspend fun loadTafsirData(surahNumber: Int): TafsirData? {
        tafsirMutex.withLock {
            if (tafsirCache.containsKey(surahNumber)) return tafsirCache[surahNumber]
        }
        val loaded = withContext(Dispatchers.IO) {
            try {
                val text = appContext.assets.open("tafsir_$surahNumber.json")
                    .bufferedReader().use { it.readText() }
                TafsirData(verses = json.decodeFromString<Map<String, TafsirVerse>>(text))
            } catch (e: Exception) {
                null
            }
        }
        tafsirMutex.withLock { tafsirCache[surahNumber] = loaded }
        return loaded
    }

    /** Flat search index over surahs, verse translations, and theme concepts. */
    suspend fun searchIndex(): QuranSearchIndex {
        searchIndex?.let { return it }
        val data = loadQuranData()
        return searchIndexMutex.withLock {
            searchIndex?.let { return it }
            val built = QuranSearchIndex.build(appContext, data)
            searchIndex = built
            built
        }
    }

    companion object {
        @Volatile
        private var instance: DataManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) instance = DataManager(context.applicationContext)
                }
            }
        }

        val shared: DataManager
            get() = instance ?: error("DataManager.init(context) must be called first")
    }
}
