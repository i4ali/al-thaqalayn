package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.QuizBadgeType
import com.thaqalayn.app.model.QuizResult
import com.thaqalayn.app.model.SurahQuiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Surah quizzes: loading quiz_<n>.json from assets, scoring, results and quiz
 * badges. Local-only port of the iOS QuizManager (same UserDefaults key names).
 */
object QuizManager {
    private const val RESULTS_KEY = "quizResults"
    private const val BADGES_KEY = "awardedQuizBadges"

    private lateinit var prefs: SharedPreferences
    private lateinit var appContext: Context
    private val json = Json { ignoreUnknownKeys = true }

    var quizResults by mutableStateOf<List<QuizResult>>(emptyList())
        private set

    private val quizCache = mutableMapOf<Int, SurahQuiz?>()
    private val quizMutex = Mutex()

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = context.getSharedPreferences("thaqalayn_quiz", Context.MODE_PRIVATE)
        quizResults = prefs.getString(RESULTS_KEY, null)?.let {
            try {
                json.decodeFromString<List<QuizResult>>(it)
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    }

    // MARK: - Quiz loading

    suspend fun loadQuiz(surahNumber: Int): SurahQuiz? {
        quizMutex.withLock {
            if (quizCache.containsKey(surahNumber)) return quizCache[surahNumber]
        }
        val loaded = withContext(Dispatchers.IO) {
            try {
                val text = appContext.assets.open("quiz_$surahNumber.json")
                    .bufferedReader().use { it.readText() }
                json.decodeFromString<SurahQuiz>(text)
            } catch (e: Exception) {
                null
            }
        }
        quizMutex.withLock { quizCache[surahNumber] = loaded }
        return loaded
    }

    /** Whether a quiz asset exists for this surah. */
    suspend fun hasQuiz(surahNumber: Int): Boolean {
        quizMutex.withLock {
            if (quizCache[surahNumber] != null) return true
        }
        return withContext(Dispatchers.IO) {
            try {
                appContext.assets.open("quiz_$surahNumber.json").use { }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // MARK: - Scoring

    fun calculateScore(quiz: SurahQuiz, answers: Map<String, String>): Int =
        quiz.questions.count { question ->
            answers[question.id]?.equals(question.correctAnswer, ignoreCase = true) == true
        }

    // MARK: - Results

    fun saveResult(result: QuizResult) {
        quizResults = quizResults + result
        prefs.edit().putString(RESULTS_KEY, json.encodeToString(quizResults)).apply()
        checkForBadges(result)
    }

    fun bestResult(surahNumber: Int): QuizResult? =
        quizResults.filter { it.surahNumber == surahNumber }.maxByOrNull { it.score }

    /** Unique surahs with at least one completed quiz (drives the Progress ring/stat). */
    val completedSurahCount: Int
        get() = quizResults.map { it.surahNumber }.toSet().size

    val averageScorePercentage: Double
        get() {
            if (quizResults.isEmpty()) return 0.0
            return quizResults.sumOf { it.score.toDouble() / it.totalQuestions } / quizResults.size
        }

    // MARK: - Badges (stored, no UI surface yet - iOS parity)

    private fun checkForBadges(result: QuizResult) {
        if (quizResults.size == 1) awardBadge(QuizBadgeType.FIRST_QUIZ)
        if (result.score == result.totalQuestions) awardBadge(QuizBadgeType.PERFECT_SCORE)
        if (completedSurahCount >= 10) awardBadge(QuizBadgeType.QUIZ_MASTER_10)
        if (completedSurahCount >= 50) awardBadge(QuizBadgeType.QUIZ_MASTER_50)
        if (averageScorePercentage >= 0.8 && quizResults.size >= 5) awardBadge(QuizBadgeType.SCHOLAR_AVERAGE)
    }

    private fun awardBadge(badgeType: QuizBadgeType) {
        val awarded = prefs.getStringSet(BADGES_KEY, emptySet()).orEmpty()
        if (badgeType.rawValue in awarded) return
        prefs.edit().putStringSet(BADGES_KEY, awarded + badgeType.rawValue).apply()
    }
}
