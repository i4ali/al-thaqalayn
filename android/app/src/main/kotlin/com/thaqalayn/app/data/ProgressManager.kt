package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.BadgeAward
import com.thaqalayn.app.model.BadgeType
import com.thaqalayn.app.model.LastReadInfo
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.ProgressPreferences
import com.thaqalayn.app.model.ProgressStats
import com.thaqalayn.app.model.ReadingStreak
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.VerseProgress
import com.thaqalayn.app.notifications.NotificationManager
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
    private const val PREFERENCES_KEY = "progressPreferences"

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
    var preferences by mutableStateOf(ProgressPreferences())
        private set

    /** Surah metadata needed for completion checks, set once data loads. */
    private var surahsByNumber: Map<Int, Surah> = emptyMap()

    /** Start-of-day the streak/nudge reminders were last armed for (in-memory, iOS parity). */
    private var remindersScheduledForDay: Long? = null

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
        preferences = decode(PREFERENCES_KEY) ?: ProgressPreferences()
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
            .putString(PREFERENCES_KEY, json.encodeToString(preferences))
            .apply()
    }

    /** Stats alone; the full save also encodes every verse record, too much while scrolling. */
    private fun saveStats() {
        prefs.edit().putString(STATS_KEY, json.encodeToString(stats)).apply()
    }

    // MARK: - Preferences

    fun updatePreferences(newPreferences: ProgressPreferences) {
        val wasEnabled = preferences.notificationsEnabled
        preferences = newPreferences
        saveProgress()
        if (wasEnabled && !newPreferences.notificationsEnabled) {
            NotificationManager.cancelProgressNotifications()
        }
    }

    // MARK: - Reading

    fun markVerseAsRead(surahNumber: Int, verseNumber: Int): Boolean {
        if (surahNumber !in 1..114) return false
        recordVerseRead(surahNumber, verseNumber)
        finishMarkingRead(surahNumber)
        return true
    }

    /**
     * Upserts one verse as read (refreshing its read date) and awards its 10 sawab
     * when it was not read before. Touches no totals, streak, badges, persistence
     * or reminders; the caller runs [finishMarkingRead] once after one or many verses.
     */
    private fun recordVerseRead(surahNumber: Int, verseNumber: Int): Boolean {
        val verseKey = "$surahNumber:$verseNumber"
        val now = System.currentTimeMillis()
        val existing = verseProgress.indexOfFirst { it.verseKey == verseKey }
        if (existing >= 0) {
            verseProgress = verseProgress.toMutableList().also {
                it[existing] = it[existing].copy(readDate = now, isRead = true)
            }
            return false
        }
        verseProgress = verseProgress + VerseProgress(
            id = UUID.randomUUID().toString(),
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            readDate = now
        )
        // 10 sawab per newly read verse, based on hadith
        stats = stats.copy(totalSawab = stats.totalSawab + 10)
        return true
    }

    /**
     * Everything that follows one or more verses of [surahNumber] being recorded
     * read: totals, streak, surah completion and badges, save, reminders. Runs once
     * per marking, not once per verse (iOS finishMarkingRead).
     */
    private fun finishMarkingRead(surahNumber: Int) {
        val now = System.currentTimeMillis()
        stats = stats.copy(
            totalVersesRead = verseProgress.count { it.isRead },
            versesReadToday = versesReadToday(now),
            lastReadDate = now
        )
        updateStreak(now)
        // Surah completion count, then any badge the completion earns.
        recomputeSurahsCompleted()
        checkSurahCompletion(surahNumber)
        saveProgress()
        // Re-arm engagement notifications now that progress changed.
        updateEngagementNotifications(surahNumber)
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
        recomputeSurahsCompleted()
        saveProgress()
        return true
    }

    fun isVerseRead(surahNumber: Int, verseNumber: Int): Boolean =
        verseProgress.any { it.verseKey == "$surahNumber:$verseNumber" && it.isRead }

    // MARK: - Passage progress (iOS 8.6)

    private var readKeysSource: List<VerseProgress>? = null
    private var readKeysCache: Set<String> = emptySet()

    /**
     * Keys "surah:verse" of every verse marked read. Cached per progress list, so
     * a screen can read it on every recomposition without rebuilding the set.
     */
    val readVerseKeys: Set<String>
        get() {
            val source = verseProgress
            if (source !== readKeysSource) {
                readKeysCache = source.asSequence().filter { it.isRead }.map { it.verseKey }.toHashSet()
                readKeysSource = source
            }
            return readKeysCache
        }

    fun isPassageRead(ref: PassageRef): Boolean = PassageProgress.isRead(ref, readVerseKeys)

    /**
     * Marks every verse in a passage read, once. Each new verse still earns its 10
     * sawab, but the streak, badge check, save and reminder re-arm run once for the
     * whole passage. Verses already read are left untouched.
     */
    fun markPassageRead(ref: PassageRef) {
        if (ref.surah !in 1..114) return
        val unread = ref.verses.filter { !isVerseRead(ref.surah, it) }
        if (unread.isEmpty()) return

        for (verse in unread) recordVerseRead(ref.surah, verse)
        finishMarkingRead(ref.surah)

        // Finishing the passage the reader is in moves Continue Reading on to the
        // next one. A passage marked while the reader is elsewhere leaves it alone.
        if (isPositionInside(ref)) {
            DataManager.shared.passageIndex?.next(ref)?.let {
                updateReadingPosition(it.surah, it.start)
            }
        }
    }

    /**
     * The reverse of [markPassageRead]: drops every read verse in the passage and
     * takes back its sawab. The streak and earned badges stay, as they do for a
     * single verse; the surah completion count is recomputed.
     */
    fun unmarkPassageRead(ref: PassageRef) {
        if (ref.surah !in 1..114) return
        val keys = ref.verses.map { "${ref.surah}:$it" }.toSet()
        val before = verseProgress.size
        verseProgress = verseProgress.filterNot { it.verseKey in keys }
        val removed = before - verseProgress.size
        if (removed == 0) return

        stats = stats.copy(
            totalVersesRead = verseProgress.count { it.isRead },
            versesReadToday = versesReadToday(System.currentTimeMillis()),
            totalSawab = maxOf(0, stats.totalSawab - 10 * removed)
        )
        recomputeSurahsCompleted()
        saveProgress()
    }

    // MARK: - Reading position

    /**
     * Records where the reader is: [verseNumber] is the verse at the top of the
     * passage screen. Saves the stats alone, so it is cheap while scrolling.
     */
    fun updateReadingPosition(surahNumber: Int, verseNumber: Int) {
        if (surahNumber !in 1..114 || verseNumber <= 0) return
        if (stats.lastReadSurah != surahNumber || stats.lastReadVerse != verseNumber) {
            stats = stats.copy(
                lastReadSurah = surahNumber,
                lastReadVerse = verseNumber,
                lastReadDate = System.currentTimeMillis()
            )
            saveStats()
        }
    }

    /** Opening a passage puts the position at its first verse, unless the reader is already inside it. */
    fun enterPassage(ref: PassageRef) {
        if (isPositionInside(ref)) return
        updateReadingPosition(ref.surah, ref.start)
    }

    /**
     * Leaving a passage records the verse at the top of the screen, unless the
     * position has already moved on (the passage was just marked read).
     */
    fun leavePassage(ref: PassageRef, topVerse: Int) {
        if (!isPositionInside(ref)) return
        updateReadingPosition(ref.surah, topVerse)
    }

    private fun isPositionInside(ref: PassageRef): Boolean {
        val verse = stats.lastReadVerse ?: return false
        return stats.lastReadSurah == ref.surah && verse in ref.start..ref.end
    }

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

    /**
     * [ProgressStats.totalSurahsCompleted] is the number of surahs whose every verse
     * is read right now, derived on every mark and unmark so the Progress rings
     * follow unmarking too. Badges stay as a record of what was earned.
     */
    private fun recomputeSurahsCompleted() {
        val readBySurah = verseProgress.filter { it.isRead }.groupingBy { it.surahNumber }.eachCount()
        val completed = readBySurah.count { (surahNumber, read) ->
            val total = surahsByNumber[surahNumber]?.versesCount ?: 0
            total > 0 && read >= total
        }
        stats = stats.copy(totalSurahsCompleted = completed)
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
        stats = stats.copy(totalSawab = stats.totalSawab + badge.badgeType.sawabValue)
        if (preferences.celebrationsEnabled) pendingBadge = badge
        notifyBadgeAwarded(badge)
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
                if (preferences.celebrationsEnabled) pendingBadge = badge
                notifyBadgeAwarded(badge)
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
                if (preferences.celebrationsEnabled) pendingBadge = badge
                notifyBadgeAwarded(badge)
            }
        }
    }

    // MARK: - Engagement notifications (iOS updateEngagementNotifications/notifyBadgeAwarded)

    /**
     * Re-arm the schedule-ahead reminders (streak + nudge, once per day) and
     * fire near-completion encouragement when a long surah gets close.
     */
    private fun updateEngagementNotifications(surahNumber: Int) {
        val (read, total) = getSurahCompletion(surahNumber)
        val remaining = total - read
        val surahName = surahsByNumber[surahNumber]?.englishName

        val today = startOfDay(System.currentTimeMillis())
        val shouldArmDailyReminders = remindersScheduledForDay != today
        if (shouldArmDailyReminders) remindersScheduledForDay = today

        if (remaining == 0) {
            NotificationManager.cancelNearCompletion(surahNumber)
        } else if (remaining == 5 && total >= 20 && surahName != null) {
            NotificationManager.scheduleNearCompletionEncouragement(
                surahNumber = surahNumber,
                surahName = surahName,
                versesRemaining = remaining
            )
        }

        if (shouldArmDailyReminders) {
            NotificationManager.scheduleStreakReminder()
            NotificationManager.scheduleGentleNudge()
        }
    }

    /** Send a milestone celebration notification for a freshly awarded badge. */
    private fun notifyBadgeAwarded(badge: BadgeAward) {
        val message = when (badge.badgeType) {
            BadgeType.SURAH_COMPLETION ->
                "You've completed Surah ${badge.surahName}! Badge earned: ${badge.badgeType.title}."
            BadgeType.DAILY_CHALLENGE_FIRST,
            BadgeType.DAILY_CHALLENGE_STREAK_7,
            BadgeType.DAILY_CHALLENGE_STREAK_30,
            BadgeType.DAILY_CHALLENGE_STREAK_100 ->
                "Daily Challenge badge earned: ${badge.badgeType.title} - ${badge.badgeType.description}"
            else ->
                "Badge earned: ${badge.badgeType.title} - ${badge.badgeType.description}"
        }
        NotificationManager.scheduleMilestoneCelebration(message)
    }

    // MARK: - Reset

    /** Clear all reading progress, badges and stats (Settings -> Reset Progress). */
    fun resetProgress() {
        verseProgress = emptyList()
        streak = ReadingStreak()
        badges = emptyList()
        stats = ProgressStats(startDate = System.currentTimeMillis())
        preferences = ProgressPreferences()
        pendingBadge = null

        // Pending reminders reference the cleared streak/progress - drop them.
        remindersScheduledForDay = null
        NotificationManager.cancelProgressNotifications()

        saveProgress()
    }

    // MARK: - Journey badges (iOS awardRamadanBadge/awardHajjBadge)

    /** Award the Ramadan Champion badge; once per Islamic year. */
    fun awardRamadanBadge(islamicYear: Int) =
        awardJourneyBadge(BadgeType.RAMADAN_COMPLETION, "Ramadan Champion", "بطل رمضان", islamicYear)

    /** Award the Hajj Champion badge; once per Islamic year. */
    fun awardHajjBadge(islamicYear: Int) =
        awardJourneyBadge(BadgeType.HAJJ_COMPLETION, "Hajj Champion", "بطل الحج", islamicYear)

    private fun awardJourneyBadge(type: BadgeType, name: String, arabicName: String, islamicYear: Int) {
        val alreadyAwarded = badges.any {
            it.badgeType == type && IslamicCalendarManager.islamicYearOf(it.awardedDate) == islamicYear
        }
        if (alreadyAwarded) return
        val badge = BadgeAward(
            id = UUID.randomUUID().toString(),
            surahNumber = 0,
            surahName = name,
            arabicName = arabicName,
            awardedDate = System.currentTimeMillis(),
            badgeType = type
        )
        badges = badges + badge
        stats = stats.copy(totalSawab = stats.totalSawab + type.sawabValue)
        pendingBadge = badge
        saveProgress()
    }

    private fun versesReadToday(now: Long): Int {
        val today = startOfDay(now)
        return verseProgress.count { it.isRead && it.readDate >= today }
    }

    // MARK: - Last read

    /**
     * Where Continue Reading points: the recorded reading position, or, for
     * progress saved before positions existed, the most recently read verse. Null
     * for new users. Progress is passages read in that surah; verses read stand in
     * until the passage index has loaded.
     */
    val lastReadInfo: LastReadInfo?
        get() {
            val surahNumber: Int
            val verseNumber: Int
            val updatedAt: Long
            val positionSurah = stats.lastReadSurah
            val positionVerse = stats.lastReadVerse
            if (positionSurah != null && positionVerse != null) {
                surahNumber = positionSurah
                verseNumber = positionVerse
                updatedAt = stats.lastReadDate ?: System.currentTimeMillis()
            } else {
                val latest = verseProgress.filter { it.isRead }.maxByOrNull { it.readDate } ?: return null
                surahNumber = latest.surahNumber
                verseNumber = latest.verseNumber
                updatedAt = latest.readDate
            }

            val index = DataManager.shared.passageIndex
            val passages = index?.passages(surahNumber).orEmpty()
            val ref = index?.passageContaining(surahNumber, verseNumber)
            val passagesRead = PassageProgress.readCount(passages, readVerseKeys)
            val progress = if (passages.isEmpty()) {
                val (read, total) = getSurahCompletion(surahNumber)
                if (total > 0) read.toDouble() / total else 0.0
            } else {
                passagesRead.toDouble() / passages.size
            }
            return LastReadInfo(
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                passageIndex = ref?.index,
                passageTitle = ref?.let { PassageStore.title(it) },
                passagesRead = passagesRead,
                passagesTotal = passages.size,
                progress = progress,
                updatedAt = updatedAt
            )
        }

    fun dismissPendingBadge() {
        pendingBadge = null
    }
}
