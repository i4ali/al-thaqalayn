package com.thaqalayn.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// MARK: - Quiz data (quiz_<n>.json; iOS QuizModels.swift)

@Serializable
data class SurahQuiz(
    val surahNumber: Int,
    val questions: List<QuizQuestion>
)

@Serializable
data class QuizQuestion(
    val id: String,
    val type: QuizQuestionType,
    /** 1-5: which tafsir layer this tests. */
    val layer: Int,
    val verseNumber: Int? = null,
    val question: String,
    /** Multiple choice only; null for true/false. */
    val options: List<String>? = null,
    /** "A"/"B"/"C"/"D" or "true"/"false". */
    val correctAnswer: String,
    val explanation: String
)

@Serializable
enum class QuizQuestionType {
    @SerialName("multipleChoice") MULTIPLE_CHOICE,
    @SerialName("trueFalse") TRUE_FALSE
}

// MARK: - Quiz results

@Serializable
data class QuizResult(
    val id: String,
    val surahNumber: Int,
    val score: Int,
    val totalQuestions: Int,
    val level: UnderstandingLevel,
    val sawabEarned: Int,
    val completedAt: Long
) {
    companion object {
        fun create(surahNumber: Int, score: Int, totalQuestions: Int): QuizResult = QuizResult(
            id = java.util.UUID.randomUUID().toString(),
            surahNumber = surahNumber,
            score = score,
            totalQuestions = totalQuestions,
            level = UnderstandingLevel.fromScore(score, totalQuestions),
            sawabEarned = calculateSawab(score, totalQuestions),
            completedAt = System.currentTimeMillis()
        )

        /** +50 base for completing, +10 per correct, +100 bonus for perfect. */
        fun calculateSawab(score: Int, total: Int): Int {
            var sawab = 50
            sawab += score * 10
            if (score == total) sawab += 100
            return sawab
        }
    }
}

@Serializable
enum class UnderstandingLevel {
    @SerialName("hafiz") HAFIZ,
    @SerialName("scholar") SCHOLAR,
    @SerialName("student") STUDENT,
    @SerialName("seeker") SEEKER,
    @SerialName("beginner") BEGINNER;

    val title: String
        get() = when (this) {
            HAFIZ -> "Hafiz Level"
            SCHOLAR -> "Scholar Level"
            STUDENT -> "Student Level"
            SEEKER -> "Seeker Level"
            BEGINNER -> "Beginner Level"
        }

    val arabicTitle: String
        get() = when (this) {
            HAFIZ -> "حافظ"
            SCHOLAR -> "عالم"
            STUDENT -> "طالب"
            SEEKER -> "باحث"
            BEGINNER -> "مبتدئ"
        }

    val message: String
        get() = when (this) {
            HAFIZ -> "MashAllah! You have mastered this surah's wisdom!"
            SCHOLAR -> "Excellent understanding! You've grasped the deep meanings."
            STUDENT -> "Good progress! Review the highlighted areas to deepen understanding."
            SEEKER -> "Keep learning! The commentary holds many treasures for you."
            BEGINNER -> "Every journey begins with a step. Review the tafsir and try again!"
        }

    companion object {
        fun fromScore(score: Int, total: Int): UnderstandingLevel {
            if (total <= 0) return BEGINNER
            val percentage = score.toDouble() / total
            return when {
                percentage >= 1.0 -> HAFIZ
                percentage >= 0.8 -> SCHOLAR
                percentage >= 0.6 -> STUDENT
                percentage >= 0.4 -> SEEKER
                else -> BEGINNER
            }
        }
    }
}

// MARK: - Quiz badges (awarded silently, iOS parity)

enum class QuizBadgeType(val rawValue: String) {
    FIRST_QUIZ("first_quiz"),
    PERFECT_SCORE("perfect_score"),
    QUIZ_MASTER_10("quiz_master_10"),
    QUIZ_MASTER_50("quiz_master_50"),
    SCHOLAR_AVERAGE("scholar_average")
}
