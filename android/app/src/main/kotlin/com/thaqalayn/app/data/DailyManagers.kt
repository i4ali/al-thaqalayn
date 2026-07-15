package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.DailyChallenge
import com.thaqalayn.app.model.DailyChallengeCompletion
import com.thaqalayn.app.model.DailyChallengeStreak
import com.thaqalayn.app.model.DailyCrosswordCompletion
import com.thaqalayn.app.model.DailyCrosswordStreak
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val json = Json { ignoreUnknownKeys = true }

private fun dayKey(date: Date = Date()): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)

private fun yesterdayKey(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    return dayKey(cal.time)
}

/**
 * Daily-challenge completion + streak tracking. Streak-only: no reward points
 * and no badges (iOS DailyChallengeManager).
 */
object DailyChallengeManager {
    private const val STREAK_KEY = "dailyChallengeStreak"
    private const val COMPLETION_KEY = "dailyChallengeLastCompletion"

    private lateinit var prefs: SharedPreferences

    var streak by mutableStateOf(DailyChallengeStreak())
        private set
    var lastCompletion by mutableStateOf<DailyChallengeCompletion?>(null)
        private set

    val isCompletedToday: Boolean get() = lastCompletion?.dayKey == dayKey()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        prefs.getString(STREAK_KEY, null)?.let {
            streak = try { json.decodeFromString(it) } catch (e: Exception) { DailyChallengeStreak() }
        }
        prefs.getString(COMPLETION_KEY, null)?.let {
            lastCompletion = try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
    }

    /** Pure streak transition (same rules as iOS nextStreak). */
    fun nextStreak(s: DailyChallengeStreak, todayKey: String, yesterdayKey: String): DailyChallengeStreak {
        if (s.lastCompletedDayKey == todayKey) return s
        val current = if (s.lastCompletedDayKey == yesterdayKey) s.currentStreak + 1 else 1
        return DailyChallengeStreak(
            currentStreak = current,
            longestStreak = maxOf(s.longestStreak, current),
            lastCompletedDayKey = todayKey
        )
    }

    fun complete(challenge: DailyChallenge, wasCorrect: Boolean) {
        if (isCompletedToday) return
        val key = dayKey()
        lastCompletion = DailyChallengeCompletion(
            dayKey = key,
            challengeId = challenge.id,
            format = challenge.format,
            wasCorrect = wasCorrect,
            completedAt = System.currentTimeMillis()
        )
        streak = nextStreak(streak, key, yesterdayKey())
        save()
    }

    /** Flashcards are self-graded - always counts as done. */
    fun completeFlashcard(challenge: DailyChallenge, gotIt: Boolean) = complete(challenge, gotIt)

    private fun save() {
        val edit = prefs.edit().putString(STREAK_KEY, json.encodeToString(streak))
        lastCompletion?.let { edit.putString(COMPLETION_KEY, json.encodeToString(it)) }
        edit.apply()
    }
}

/**
 * Daily-crossword completion + streak tracking; mirrors DailyChallengeManager
 * (iOS DailyCrosswordManager).
 */
object DailyCrosswordManager {
    private const val STREAK_KEY = "dcw_streak"
    private const val COMPLETION_KEY = "dcw_lastCompletion"
    private const val COMPLETED_DAY_KEY = "dcw_completedDayKey"

    private lateinit var prefs: SharedPreferences

    var streak by mutableStateOf(DailyCrosswordStreak())
        private set
    var lastCompletion by mutableStateOf<DailyCrosswordCompletion?>(null)
        private set
    var isCompletedToday by mutableStateOf(false)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        prefs.getString(STREAK_KEY, null)?.let {
            streak = try { json.decodeFromString(it) } catch (e: Exception) { DailyCrosswordStreak() }
        }
        prefs.getString(COMPLETION_KEY, null)?.let {
            lastCompletion = try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
        refreshForToday()
    }

    fun complete(puzzleId: String, seconds: Int, usedHint: Boolean) {
        if (isCompletedToday) return
        val today = dayKey()
        val current = if (streak.lastCompletedDayKey == yesterdayKey()) streak.currentStreak + 1 else 1
        streak = if (streak.lastCompletedDayKey == today) streak else DailyCrosswordStreak(
            currentStreak = current,
            longestStreak = maxOf(streak.longestStreak, current),
            lastCompletedDayKey = today
        )
        lastCompletion = DailyCrosswordCompletion(
            dayKey = today,
            puzzleId = puzzleId,
            seconds = seconds,
            usedHint = usedHint,
            completedAt = System.currentTimeMillis()
        )
        isCompletedToday = true
        prefs.edit()
            .putString(STREAK_KEY, json.encodeToString(streak))
            .putString(COMPLETION_KEY, json.encodeToString(lastCompletion))
            .putString(COMPLETED_DAY_KEY, today)
            .apply()
    }

    /** Recompute isCompletedToday so the card clears when the day rolls over. */
    fun refreshForToday() {
        isCompletedToday = prefs.getString(COMPLETED_DAY_KEY, null) == dayKey()
    }
}
