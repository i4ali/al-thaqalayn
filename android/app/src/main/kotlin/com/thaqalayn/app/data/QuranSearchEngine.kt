package com.thaqalayn.app.data

import android.content.Context
import com.thaqalayn.app.model.QuranData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// MARK: - Index

class QuranSearchIndex(
    val surahEntries: List<SurahEntry>,
    val verseEntries: List<VerseEntry>,
    val themeEntries: List<ThemeEntry>
) {
    data class SurahEntry(
        val surahNumber: Int,
        val nameLower: String,
        val translationLower: String,
        val arabicName: String
    )

    data class VerseEntry(
        val surahNumber: Int,
        val verseNumber: Int,
        val surahEnglishName: String,
        val translation: String,
        val translationLower: String
    )

    @Serializable
    data class ThemeEntry(
        val conceptId: String,
        val title: String,
        val colorHex: String,
        val surahNumber: Int,
        val verseNumber: Int,
        val surahEnglishName: String
    ) {
        val titleLower: String get() = title.lowercase()
    }

    @Serializable
    data class ThemeIndexFile(val themes: List<ThemeEntry>)

    companion object {
        /**
         * Build the flat index: surah/verse entries from quran_data, theme entries
         * from the precomputed themes_index.json asset (Android keeps tafsir
         * lazy-loaded, unlike iOS which preloads all 114 files).
         */
        suspend fun build(context: Context, quranData: QuranData): QuranSearchIndex =
            withContext(Dispatchers.Default) {
                val surahE = quranData.surahs.map {
                    SurahEntry(
                        surahNumber = it.number,
                        nameLower = it.englishName.lowercase(),
                        translationLower = it.englishNameTranslation.lowercase(),
                        arabicName = it.arabicName
                    )
                }
                val verseE = mutableListOf<VerseEntry>()
                for (surah in quranData.surahs.sortedBy { it.number }) {
                    val verses = quranData.verses[surah.number.toString()].orEmpty()
                    for (n in 1..surah.versesCount) {
                        val verse = verses[n.toString()] ?: continue
                        verseE.add(
                            VerseEntry(
                                surahNumber = surah.number,
                                verseNumber = n,
                                surahEnglishName = surah.englishName,
                                translation = verse.translation,
                                translationLower = verse.translation.lowercase()
                            )
                        )
                    }
                }
                val themeE = try {
                    val text = context.assets.open("themes_index.json")
                        .bufferedReader().use { it.readText() }
                    Json { ignoreUnknownKeys = true }
                        .decodeFromString<ThemeIndexFile>(text).themes
                } catch (e: Exception) {
                    emptyList()
                }
                QuranSearchIndex(surahE, verseE, themeE)
            }
    }
}

// MARK: - Results

data class VerseHit(
    val surahNumber: Int,
    val verseNumber: Int,
    val surahEnglishName: String,
    val snippet: String,
    val matchRange: IntRange?
) {
    val id: String get() = "$surahNumber:$verseNumber"
}

data class ThemeHit(
    val conceptId: String,
    val title: String,
    val colorHex: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val surahEnglishName: String
)

data class QuranSearchResults(
    val surahs: List<Int> = emptyList(),   // surah numbers
    val verses: List<VerseHit> = emptyList(),
    val themes: List<ThemeHit> = emptyList(),
    val verseTotal: Int = 0,
    val themeTotal: Int = 0
) {
    val isEmpty: Boolean get() = surahs.isEmpty() && verses.isEmpty() && themes.isEmpty()
}

// MARK: - Engine

object QuranSearchEngine {
    const val MIN_TEXT_QUERY_LENGTH = 2
    const val VERSE_LIMIT = 25
    const val THEME_LIMIT = 40

    fun search(rawQuery: String, index: QuranSearchIndex): QuranSearchResults {
        val trimmed = rawQuery.trim()
        if (trimmed.isEmpty()) return QuranSearchResults()
        val q = trimmed.lowercase()

        // Surahs - match from 1 char (name / English meaning / Arabic name).
        val surahs = index.surahEntries
            .filter { it.nameLower.contains(q) || it.translationLower.contains(q) || it.arabicName.contains(trimmed) }
            .map { it.surahNumber }

        // Verses + themes only kick in at >= 2 chars to avoid noise.
        if (q.length < MIN_TEXT_QUERY_LENGTH) {
            return QuranSearchResults(surahs = surahs)
        }

        val verseMatches = index.verseEntries.filter { it.translationLower.contains(q) }
        val verses = verseMatches.take(VERSE_LIMIT).map { entry ->
            val (snippet, range) = snippet(entry.translation, trimmed)
            VerseHit(
                surahNumber = entry.surahNumber,
                verseNumber = entry.verseNumber,
                surahEnglishName = entry.surahEnglishName,
                snippet = snippet,
                matchRange = range
            )
        }

        // Themes - rank exact title, then prefix, then contains; canonical tiebreak.
        val themeMatches = index.themeEntries.filter { it.titleLower.contains(q) }
        val ranked = themeMatches.sortedWith(
            compareBy(
                { entry ->
                    when {
                        entry.titleLower == q -> 0
                        entry.titleLower.startsWith(q) -> 1
                        else -> 2
                    }
                },
                { it.surahNumber },
                { it.verseNumber }
            )
        )
        val themes = ranked.take(THEME_LIMIT).map {
            ThemeHit(
                conceptId = it.conceptId,
                title = it.title,
                colorHex = it.colorHex,
                surahNumber = it.surahNumber,
                verseNumber = it.verseNumber,
                surahEnglishName = it.surahEnglishName
            )
        }

        return QuranSearchResults(
            surahs = surahs,
            verses = verses,
            themes = themes,
            verseTotal = verseMatches.size,
            themeTotal = themeMatches.size
        )
    }

    /**
     * Windowed snippet around the first case-insensitive match, with the match's
     * range within the returned snippet (for highlighting). Adds ellipses when clipped.
     */
    fun snippet(text: String, query: String, before: Int = 40, after: Int = 60): Pair<String, IntRange?> {
        val matchStart = text.indexOf(query, ignoreCase = true)
        if (matchStart < 0) return text to null

        val start = maxOf(0, matchStart - before)
        val end = minOf(text.length, matchStart + query.length + after)
        val core = text.substring(start, end)

        val prefix = if (start > 0) "…" else ""
        val suffix = if (end < text.length) "…" else ""
        val snippet = prefix + core + suffix
        val rangeStart = matchStart - start + prefix.length
        val range = rangeStart until (rangeStart + query.length)
        if (range.last >= snippet.length) return snippet to null
        return snippet to range
    }
}
