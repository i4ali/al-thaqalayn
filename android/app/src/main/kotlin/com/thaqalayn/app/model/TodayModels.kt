package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

// MARK: - Daily message (Today reminder banner)

@Serializable
data class DailyMessage(
    val id: Int,
    val arabic: String? = null,
    val english: String,
    val surah: Int,
    val verse: Int
)

@Serializable
data class DailyMessagesData(val messages: List<DailyMessage>)

// MARK: - Daily duas

@Serializable
data class DailyDuasData(val duas: List<DailyDua>)

@Serializable
data class DailyDua(
    val id: String,
    val situationEn: String,
    val situationAr: String,
    val situationUr: String,
    val arabic: String,
    val transliteration: String,
    val translationEn: String,
    val translationUr: String,
    val source: String,
    val category: String,
    /** Set for duas drawn from the Qur'an, so the source can link to the verse. */
    val surahNumber: Int? = null,
    val verseNumber: Int? = null
) {
    fun situation(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> situationAr
        CommentaryLanguage.URDU -> situationUr
        else -> situationEn
    }

    fun translation(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> translationUr
        else -> translationEn
    }
}

// MARK: - Localized text (en authored; ur/ar filled by translator agents; English fallback)

@Serializable
data class LocalizedText(
    val en: String,
    val ur: String? = null,
    val ar: String? = null
) {
    fun text(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> if (!ur.isNullOrEmpty()) ur else en
        CommentaryLanguage.ARABIC -> if (!ar.isNullOrEmpty()) ar else en
        else -> en
    }
}

// MARK: - Daily challenge

@Serializable
enum class DailyChallengeFormat {
    multipleChoice, trueFalse, flashcard, fillInBlank
}

@Serializable
data class DailyChallenge(
    val id: String,
    val format: DailyChallengeFormat,
    val topic: String,
    val prompt: LocalizedText,
    val options: List<LocalizedText>? = null,
    val correctIndex: Int? = null,
    val answer: LocalizedText? = null,
    val explanation: LocalizedText? = null,
    val arabicText: String? = null,
    val source: String? = null
) {
    /** True/false convenience. Convention: correctIndex 1 = true, 0 = false. */
    val trueFalseAnswer: Boolean?
        get() = if (format == DailyChallengeFormat.trueFalse) correctIndex?.let { it == 1 } else null
}

@Serializable
data class DailyChallengeCompletion(
    val dayKey: String,
    val challengeId: String,
    val format: DailyChallengeFormat,
    val wasCorrect: Boolean,
    val completedAt: Long
)

@Serializable
data class DailyChallengeStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDayKey: String? = null
)

// MARK: - Daily crossword

@Serializable
data class CrosswordEntry(
    val num: Int,
    val dir: String,            // "A" or "D"
    val answer: String,         // A-Z solution, uppercased
    val clue: LocalizedText,
    val cells: List<List<Int>>  // [[row,col], ...], length == answer.length
) {
    val id: String get() = "$num$dir"
    val isAcross: Boolean get() = dir == "A"
    fun cellAt(i: Int): CellPos = CellPos(cells[i][0], cells[i][1])
}

@Serializable
data class CellPos(val r: Int, val c: Int)

@Serializable
data class DailyCrossword(
    val id: String,
    val rows: Int,
    val cols: Int,
    val entries: List<CrosswordEntry>,
    val cellNumbers: Map<String, Int> = emptyMap()  // "r,c" -> number
) {
    /** Solution letter for every filled cell, rebuilt from entries. */
    val solution: Map<CellPos, Char>
        get() {
            val m = mutableMapOf<CellPos, Char>()
            for (e in entries) {
                e.answer.forEachIndexed { i, ch -> m[e.cellAt(i)] = ch }
            }
            return m
        }

    fun numberAt(p: CellPos): Int? = cellNumbers["${p.r},${p.c}"]
}

@Serializable
data class DailyCrosswordCompletion(
    val dayKey: String,
    val puzzleId: String,
    val seconds: Int,
    val usedHint: Boolean,
    val completedAt: Long
)

@Serializable
data class DailyCrosswordStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDayKey: String? = null
)
