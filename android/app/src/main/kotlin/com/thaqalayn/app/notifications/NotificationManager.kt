package com.thaqalayn.app.notifications

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.thaqalayn.app.data.IslamicCalendarManager
import com.thaqalayn.app.data.JourneyAnnouncement
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.journeyScheduleDecision
import com.thaqalayn.app.model.DailyVerseEntry
import com.thaqalayn.app.model.IslamicMonth
import com.thaqalayn.app.model.IslamicMonthVerseData
import com.thaqalayn.app.model.NotificationPreferences
import com.thaqalayn.app.model.NotificationType
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Daily verse + seasonal + progress notifications - port of iOS NotificationManager.
 * iOS pre-schedules a rolling 7-day window of UNNotificationRequests because
 * content must be baked at schedule time; Android instead runs a WorkManager
 * worker at the preferred time that builds the day's content fresh and re-arms
 * itself for the next day (same UX, one moving part).
 */
object NotificationManager {
    private const val PREFERENCES_KEY = "notificationPreferences"
    private const val JOURNEY_HANDLED_YEARS_KEY = "journeyStartHandledYears"

    // Unique work names (mirror the iOS notification identifiers).
    const val WORK_DAILY_VERSE = "daily_verse"
    const val WORK_ARAFAH = "arafah_reminder"
    const val WORK_STREAK = "streak_reminder"
    const val WORK_NUDGE = "gentle_nudge"
    const val TAG_PROGRESS = "progress_notification"

    private lateinit var appContext: Context
    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    var preferences by mutableStateOf(NotificationPreferences())
        private set

    /** Parsed islamic_month_verses.json; loaded on the app's background init thread. */
    @Volatile
    private var verseData: IslamicMonthVerseData? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences("thaqalayn_notifications", Context.MODE_PRIVATE)
        NotificationInboxStore.init(appContext)
        NotificationPoster.createChannels(appContext)
        preferences = prefs.getString(PREFERENCES_KEY, null)?.let {
            try {
                json.decodeFromString<NotificationPreferences>(it)
            } catch (e: Exception) {
                null
            }
        } ?: NotificationPreferences()
    }

    /** Workers may fire before the Application background thread finishes parsing. */
    fun loadVerseDataIfNeeded(context: Context) {
        if (verseData == null) loadVerseData(context)
    }

    /** Heavy part of init - JSON parse; call from the Application background thread. */
    fun loadVerseData(context: Context) {
        verseData = try {
            val text = context.assets.open("islamic_month_verses.json")
                .bufferedReader().use { it.readText() }
            json.decodeFromString<IslamicMonthVerseData>(text)
        } catch (e: Exception) {
            null
        }
    }

    // MARK: - Preferences

    /** iOS preferences.didSet: persist, then re-time dailies AND seasonal one-shots. */
    fun updatePreferences(newPreferences: NotificationPreferences) {
        preferences = newPreferences
        prefs.edit().putString(PREFERENCES_KEY, json.encodeToString(newPreferences)).apply()
        refreshAllSchedules()
    }

    fun hasPermission(): Boolean = NotificationPoster.hasPermission(appContext)

    // MARK: - Verse Selection

    /** Select today's verse based on the Islamic calendar. */
    fun selectTodayVerse(): DailyVerseEntry? {
        val data = verseData ?: return null
        val monthNumber = IslamicCalendarManager.currentIslamicMonth()
        val dayOfMonth = IslamicCalendarManager.currentIslamicDay()
        val monthData = data.months.firstOrNull { it.month == monthNumber } ?: return null
        // Rotate through verses using day of month.
        return monthData.verses[(dayOfMonth - 1) % monthData.verses.size]
    }

    /** Islamic month data for the current month. */
    fun currentMonthData(): IslamicMonth? {
        val data = verseData ?: return null
        val monthNumber = IslamicCalendarManager.currentIslamicMonth()
        return data.months.firstOrNull { it.month == monthNumber }
    }

    // MARK: - Lifecycle Refresh

    /**
     * Single entry point for app activation (cold launch + every foregrounding):
     * refresh every schedule. iOS also sweeps delivered notifications here; on
     * Android the inbox is written at post time so there is nothing to sweep.
     */
    fun handleAppBecameActive() {
        refreshAllSchedules()
    }

    /**
     * Safe to call from any thread; WorkManager enqueues are idempotent via
     * unique-work REPLACE, so overlapping refreshes cannot double-schedule.
     */
    fun refreshAllSchedules() {
        if (!hasPermission()) return

        if (preferences.enabled) {
            scheduleDailyVerse()
        } else {
            WorkManager.getInstance(appContext).cancelUniqueWork(WORK_DAILY_VERSE)
        }

        // Seasonal one-shots are idempotent (fixed identifiers, handledYears
        // dedup for journey catch-ups) - safe to re-arm on every refresh.
        if (IslamicCalendarManager.isHajjSeason()) {
            scheduleArafahReminder()
        }
        scheduleJourneyStartNotifications()
    }

    // MARK: - Daily Verse Scheduling

    /** Millis until the next occurrence of the preferred time, strictly in the future. */
    private fun delayToPreferredTime(fromMillis: Long, dayOffset: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fromMillis
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        cal.set(Calendar.HOUR_OF_DAY, preferences.hour)
        cal.set(Calendar.MINUTE, preferences.minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        var target = cal.timeInMillis
        if (target <= fromMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            target = cal.timeInMillis
        }
        return target - fromMillis
    }

    /** (Re)arm the daily-verse worker for the next preferred-time firing. */
    fun scheduleDailyVerse() {
        val request = OneTimeWorkRequestBuilder<DailyVerseWorker>()
            .setInitialDelay(delayToPreferredTime(System.currentTimeMillis()), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(WORK_DAILY_VERSE, ExistingWorkPolicy.REPLACE, request)
    }

    // MARK: - Hajj Season

    /**
     * Single reminder for the Day of Arafah (9 Dhul-Hijjah) at the preferred time.
     * Deep-links to Quran 2:198 (the verse naming Arafat). Never requests
     * permission - that is owned by the daily-verse opt-in flow.
     */
    fun scheduleArafahReminder() {
        if (!IslamicCalendarManager.isHajjSeason()) return
        if (!hasPermission()) return

        val arafahMillis = IslamicCalendarManager.hijriDateMillis(
            IslamicCalendarManager.currentIslamicYear(), 12, 9
        )
        val cal = Calendar.getInstance()
        cal.timeInMillis = arafahMillis
        cal.set(Calendar.HOUR_OF_DAY, preferences.hour)
        cal.set(Calendar.MINUTE, preferences.minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val fireMillis = cal.timeInMillis
        val now = System.currentTimeMillis()
        // Skip if Arafah has already passed this Islamic year.
        if (fireMillis <= now) return

        enqueueNotify(
            workName = WORK_ARAFAH,
            delayMillis = fireMillis - now,
            identifier = WORK_ARAFAH,
            type = NotificationType.dailyVerse, // iOS files ARAFAH_REMINDER under the verse deep-link type
            title = "Day of Arafah 🤲",
            body = "Today is the Day of Arafah, the greatest day of supplication. " +
                "Recite the Du'a of Imam al-Husayn (AS) and seek Allah's mercy. " +
                "Tap to continue your Dhul-Hijjah Journey.",
            surah = 2,
            verse = 198
        )
    }

    // MARK: - Journey-Start Notifications

    private fun loadJourneyHandledYears(): Map<String, Int> =
        prefs.getString(JOURNEY_HANDLED_YEARS_KEY, null)?.let {
            try {
                json.decodeFromString<Map<String, Int>>(it)
            } catch (e: Exception) {
                null
            }
        } ?: emptyMap()

    private fun saveJourneyHandledYears(map: Map<String, Int>) {
        prefs.edit().putString(JOURNEY_HANDLED_YEARS_KEY, json.encodeToString(map)).apply()
    }

    /**
     * Schedule "the Journey is open" notifications for all journeys. Idempotent
     * (fixed work names; handledYears dedups catch-ups) - safe on every refresh.
     */
    fun scheduleJourneyStartNotifications() {
        if (!hasPermission()) return

        val now = System.currentTimeMillis()
        val (iYear, iMonth, iDay) = IslamicCalendarManager.currentIslamicDate()
        val handled = loadJourneyHandledYears().toMutableMap()

        for (journey in JourneyAnnouncement.all) {
            val decision = journeyScheduleDecision(
                journey = journey,
                nowMillis = now,
                islamicYear = iYear,
                islamicMonth = iMonth,
                islamicDay = iDay,
                preferredHour = preferences.hour,
                preferredMinute = preferences.minute,
                handledCycleYear = handled[journey.id]
            )

            val identifier = "journey_start_${journey.id}"

            decision.calendarFireMillis?.let { fireMillis ->
                enqueueNotify(
                    workName = identifier,
                    delayMillis = fireMillis - now,
                    identifier = identifier,
                    type = NotificationType.journey,
                    title = journey.title,
                    body = journey.body,
                    journeyId = journey.id
                )
            }

            if (decision.fireCatchUpNow) {
                enqueueNotify(
                    workName = identifier,
                    delayMillis = TimeUnit.SECONDS.toMillis(5),
                    identifier = identifier,
                    type = NotificationType.journey,
                    title = journey.title,
                    body = journey.body,
                    journeyId = journey.id
                )
            }

            decision.markHandledCycleYear?.let { handled[journey.id] = it }
        }

        saveJourneyHandledYears(handled)
    }

    // MARK: - Progress Notifications

    /** Streak reminder for tomorrow at the preferred time. */
    fun scheduleStreakReminder() {
        if (!ProgressManager.preferences.notificationsEnabled) return
        val currentStreak = ProgressManager.streak.currentStreak
        if (currentStreak <= 0) return
        if (!hasPermission()) return

        enqueueNotify(
            workName = WORK_STREAK,
            delayMillis = delayToPreferredTime(System.currentTimeMillis(), dayOffset = 1),
            identifier = WORK_STREAK,
            type = NotificationType.streak,
            title = "Keep Your Streak Going! 🔥",
            body = "You're on a $currentStreak-day reading streak. Don't break it today!",
            tag = TAG_PROGRESS
        )
    }

    /** Milestone celebration ~5s from now (immediate celebration). */
    fun scheduleMilestoneCelebration(milestone: String) {
        if (!ProgressManager.preferences.notificationsEnabled) return
        if (!hasPermission()) return

        val identifier = "milestone_${UUID.randomUUID()}"
        enqueueNotify(
            workName = identifier,
            delayMillis = TimeUnit.SECONDS.toMillis(5),
            identifier = identifier,
            type = NotificationType.milestone,
            title = "Congratulations! 🎉",
            body = milestone,
            tag = TAG_PROGRESS
        )
    }

    /**
     * Schedule-ahead re-engagement nudge: armed on every read for +2 days at
     * the preferred time (fixed work name replaces the previous one), so it
     * only ever fires if the user actually stays away.
     */
    fun scheduleGentleNudge() {
        if (!ProgressManager.preferences.notificationsEnabled) return
        if (!hasPermission()) return

        enqueueNotify(
            workName = WORK_NUDGE,
            delayMillis = delayToPreferredTime(System.currentTimeMillis(), dayOffset = 2),
            identifier = WORK_NUDGE,
            type = NotificationType.nudge,
            title = "We miss you! 📖",
            body = "It's been 2 days since your last reading. " +
                "Come back to continue your journey through the Quran.",
            tag = TAG_PROGRESS
        )
    }

    /** Encouragement for a nearly completed surah, 1 hour from now. */
    fun scheduleNearCompletionEncouragement(surahNumber: Int, surahName: String, versesRemaining: Int) {
        if (!ProgressManager.preferences.notificationsEnabled) return
        if (!hasPermission()) return

        enqueueNotify(
            workName = "near_completion_$surahNumber",
            delayMillis = TimeUnit.HOURS.toMillis(1),
            identifier = "near_completion_$surahNumber",
            type = NotificationType.nearCompletion,
            title = "Almost There! 🌟",
            body = "You're almost done with Surah $surahName! Only $versesRemaining verses remaining.",
            tag = TAG_PROGRESS
        )
    }

    /** Cancel all progress-related notifications (streak, nudge, near-completion, milestone). */
    fun cancelProgressNotifications() {
        WorkManager.getInstance(appContext).cancelAllWorkByTag(TAG_PROGRESS)
    }

    /**
     * Cancel the pending near-completion encouragement for a surah
     * (called when the surah is completed before the trigger fires).
     */
    fun cancelNearCompletion(surahNumber: Int) {
        WorkManager.getInstance(appContext).cancelUniqueWork("near_completion_$surahNumber")
    }

    // MARK: - Helpers

    private fun enqueueNotify(
        workName: String,
        delayMillis: Long,
        identifier: String,
        type: NotificationType,
        title: String,
        body: String,
        surah: Int? = null,
        verse: Int? = null,
        journeyId: String? = null,
        tag: String? = null
    ) {
        val builder = OneTimeWorkRequestBuilder<NotifyWorker>()
            .setInitialDelay(maxOf(0L, delayMillis), TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    NotifyWorker.KEY_IDENTIFIER to identifier,
                    NotifyWorker.KEY_TYPE to type.name,
                    NotifyWorker.KEY_TITLE to title,
                    NotifyWorker.KEY_BODY to body,
                    NotifyWorker.KEY_SURAH to (surah ?: -1),
                    NotifyWorker.KEY_VERSE to (verse ?: -1),
                    NotifyWorker.KEY_JOURNEY to journeyId
                )
            )
        tag?.let { builder.addTag(it) }
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, builder.build())
    }
}
