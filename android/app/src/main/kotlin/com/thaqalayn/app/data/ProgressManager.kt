package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.BadgeAward
import com.thaqalayn.app.model.BadgeType
import com.thaqalayn.app.model.LastReadInfo
import com.thaqalayn.app.model.ProgressStats
import com.thaqalayn.app.model.ReadingStreak
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.VerseProgress
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Reading progress: read verses, streaks, badges, sawab. Local-only port of the
 * iOS ProgressManager (the Supabase sync layer is intentionally dropped on Android).
 */
object ProgressManager {
    private const val VERSE_PROGRESS_KEY = "verseProgress"
    private const val STREAK_KEY = "readingStreak"
    private const val BADGES_KEY = "badgeAwards"
    private const val STATS_KEY = "progressStats"

    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    var verseProgress by mutableStateOf<List<VerseProgress>>(emptyList())
        private set
    var streak by mutableStateOf(ReadingStreak())
        private set
    var badges by mutableStateOf<List<BadgeAward>>(emptyList())
        private set
    var stats by mutableStateOf(ProgressStats())
        private set
    var pendingBadge by mutableStateOf<BadgeAward?>(null)
        private set

    /** Surah metadata needed for completion checks, set once data loads. */
    private var surahsByNumber: Map<Int, Surah> = emptyMap()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_progress", Context.MODE_PRIVATE)
        loadProgress()
        updateStreakOnLoad()
    }

    fun attachSurahs(surahs: List<Surah>) {
        surahsByNumber = surahs.associateBy { it.number }
    }

    private fun loadProgress() {
        verseProgress = decode(VERSE_PROGRESS_KEY) ?: emptyList()
        streak = decode(STREAK_KEY) ?: ReadingStreak()
        badges = decode(BADGES_KEY) ?: emptyList()
        stats = decode(STATS_KEY) ?: ProgressStats(startDate = System.currentTimeMillis())
    }

    private inline fun <reified T> decode(key: String): T? =
        prefs.getString(key, null)?.let {
            try {
                json.decodeFromString<T>(it)
            } catch (e: Exception) {
                null
            }
        }

    private fun saveProgress() {
        prefs.edit()
            .putString(VERSE_PROGRESS_KEY, json.encodeToString(verseProgress))
            .putString(STREAK_KEY, json.encodeToString(streak))
            .putString(BADGES_KEY, json.encodeToString(badges))
            .putString(STATS_KEY, json.encodeToString(stats))
            .apply()
    }

    // MARK: - Reading

    fun markVerseAsRead(surahNumber: Int, verseNumber: Int): Boolean {
        if (surahNumber !in 1..114) return false
        val verseKey = "$surahNumber:$verseNumber"
        val now = System.currentTimeMillis()

        var isNewRead = false
        val existing = verseProgress.indexOfFirst { it.verseKey == verseKey }
        verseProgress = if (existing >= 0) {
            verseProgress.toMutableList().also {
                it[existing] = it[existing].copy(readDate = now, isRead = true)
            }
        } else {
            isNewRead = true
            verseProgress + VerseProgress(
                id = UUID.randomUUID().toString(),
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                readDate = now
            )
        }

        var newStats = stats.copy(
            totalVersesRead = verseProgress.count { it.isRead },
            versesReadToday = versesReadToday(now),
            lastReadDate = now
        )
        // 10 sawab per newly read verse, based on hadith
        if (isNewRead) newStats = newStats.copy(totalSawab = newStats.totalSawab + 10)
        stats = newStats

        updateStreak(now)
        checkSurahCompletion(surahNumber)
        saveProgress()
        return true
    }

    fun unmarkVerseAsRead(surahNumber: Int, verseNumber: Int): Boolean {
        val verseKey = "$surahNumber:$verseNumber"
        val index = verseProgress.indexOfFirst { it.verseKey == verseKey }
        if (index < 0) return false
        verseProgress = verseProgress.toMutableList().also { it.removeAt(index) }
        stats = stats.copy(
            totalVersesRead = verseProgress.count { it.isRead },
            versesReadToday = versesReadToday(System.currentTimeMillis()),
            totalSawab = maxOf(0, stats.totalSawab - 10)
        )
        saveProgress()
        return true
    }

    fun isVerseRead(surahNumber: Int, verseNumber: Int): Boolean =
        verseProgress.any { it.verseKey == "$surahNumber:$verseNumber" && it.isRead }

    fun addSawab(amount: Int, reason: String) {
        if (amount <= 0) return
        stats = stats.copy(totalSawab = stats.totalSawab + amount)
        saveProgress()
    }

    // MARK: - Surah completion

    fun getSurahCompletion(surahNumber: Int): Pair<Int, Int> {
        val read = verseProgress.count { it.surahNumber == surahNumber && it.isRead }
        val total = surahsByNumber[surahNumber]?.versesCount ?: 0
        return read to total
    }

    fun isSurahCompleted(surahNumber: Int): Boolean {
        val (read, total) = getSurahCompletion(surahNumber)
        return total > 0 && read == total
    }

    private fun checkSurahCompletion(surahNumber: Int) {
        if (!isSurahCompleted(surahNumber)) return
        val alreadyAwarded = badges.any {
            it.surahNumber == surahNumber && it.badgeType == BadgeType.SURAH_COMPLETION
        }
        if (alreadyAwarded) return
        val surah = surahsByNumber[surahNumber] ?: return

        val badge = BadgeAward(
            id = UUID.randomUUID().toString(),
            surahNumber = surahNumber,
            surahName = surah.englishName,
            arabicName = surah.arabicName,
            awardedDate = System.currentTimeMillis(),
            badgeType = BadgeType.SURAH_COMPLETION
        )
        badges = badges + badge
        stats = stats.copy(
            totalSurahsCompleted = stats.totalSurahsCompleted + 1,
            totalSawab = stats.totalSawab + badge.badgeType.sawabValue
        )
        pendingBadge = badge
        checkMilestoneBadges()
    }

    private fun checkMilestoneBadges() {
        val milestones = listOf(
            10 to BadgeType.MILESTONE_10,
            25 to BadgeType.MILESTONE_25,
            50 to BadgeType.MILESTONE_50,
            114 to BadgeType.ALL_SURAHS
        )
        for ((count, type) in milestones) {
            if (stats.totalSurahsCompleted == count && badges.none { it.badgeType == type }) {
                val badge = BadgeAward(
                    id = UUID.randomUUID().toString(),
                    surahNumber = 0,
                    surahName = type.title,
                    arabicName = type.subtitle,
                    awardedDate = System.currentTimeMillis(),
                    badgeType = type
                )
                badges = badges + badge
                stats = stats.copy(totalSawab = stats.totalSawab + type.sawabValue)
                pendingBadge = badge
            }
        }
    }

    // MARK: - Streaks

    private fun daysBetween(from: Long, to: Long): Int {
        val fromDay = TimeUnit.MILLISECONDS.toDays(startOfDay(from))
        val toDay = TimeUnit.MILLISECONDS.toDays(startOfDay(to))
        return (toDay - fromDay).toInt()
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun updateStreakOnLoad() {
        val lastRead = streak.lastReadDate ?: return
        if (daysBetween(lastRead, System.currentTimeMillis()) > 1) {
            streak = streak.copy(currentStreak = 0, streakStartDate = null)
        }
        stats = stats.copy(currentStreak = streak.currentStreak, longestStreak = streak.longestStreak)
        saveProgress()
    }

    private fun updateStreak(now: Long) {
        val lastRead = streak.lastReadDate
        if (lastRead != null) {
            val days = daysBetween(lastRead, now)
            when {
                days == 0 -> return
                days == 1 -> {
                    val current = streak.currentStreak + 1
                    streak = streak.copy(
                        currentStreak = current,
                        longestStreak = maxOf(current, streak.longestStreak)
                    )
                    checkStreakBadges()
                }
                else -> streak = streak.copy(currentStreak = 1, streakStartDate = now)
            }
        } else {
            streak = streak.copy(currentStreak = 1, longestStreak = 1, streakStartDate = now)
        }
        streak = streak.copy(lastReadDate = now)
        stats = stats.copy(
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
            lastReadDate = now
        )
    }

    private fun checkStreakBadges() {
        val milestones = listOf(7 to BadgeType.STREAK_7, 30 to BadgeType.STREAK_30, 100 to BadgeType.STREAK_100)
        for ((days, type) in milestones) {
            if (streak.currentStreak == days && badges.none { it.badgeType == type }) {
                val badge = BadgeAward(
                    id = UUID.randomUUID().toString(),
                    surahNumber = 0,
                    surahName = type.title,
                    arabicName = type.subtitle,
                    awardedDate = System.currentTimeMillis(),
                    badgeType = type
                )
                badges = badges + badge
                stats = stats.copy(totalSawab = stats.totalSawab + type.sawabValue)
                pendingBadge = badge
            }
        }
    }

    private fun versesReadToday(now: Long): Int {
        val today = startOfDay(now)
        return verseProgress.count { it.isRead && it.readDate >= today }
    }

    // MARK: - Last read

    /** Most recent read verse, with completion progress for its surah. Null for new users. */
    val lastReadInfo: LastReadInfo?
        get() {
            val latest = verseProgress.filter { it.isRead }.maxByOrNull { it.readDate } ?: return null
            val (read, total) = getSurahCompletion(latest.surahNumber)
            return LastReadInfo(
                surahNumber = latest.surahNumber,
                verseNumber = latest.verseNumber,
                progress = if (total > 0) read.toDouble() / total else 0.0,
                updatedAt = latest.readDate
            )
        }

    fun dismissPendingBadge() {
        pendingBadge = null
    }
}
