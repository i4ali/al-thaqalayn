package com.thaqalayn.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DailyVerseProvider
import com.thaqalayn.app.model.NotificationType
import java.util.Date

/**
 * Fires a pre-built notification (progress, journey-start, Arafah). The content
 * was baked at schedule time, mirroring iOS's UNNotificationRequest payloads.
 */
class NotifyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val identifier = inputData.getString(KEY_IDENTIFIER) ?: return Result.success()
        val type = inputData.getString(KEY_TYPE)?.let { name ->
            NotificationType.entries.firstOrNull { it.name == name }
        } ?: NotificationType.dailyVerse
        val title = inputData.getString(KEY_TITLE) ?: return Result.success()
        val body = inputData.getString(KEY_BODY) ?: return Result.success()

        NotificationPoster.post(
            context = applicationContext,
            identifier = identifier,
            type = type,
            title = title,
            body = body,
            surah = inputData.getInt(KEY_SURAH, -1).takeIf { it > 0 },
            verse = inputData.getInt(KEY_VERSE, -1).takeIf { it > 0 },
            journeyId = inputData.getString(KEY_JOURNEY)
        )
        return Result.success()
    }

    companion object {
        const val KEY_IDENTIFIER = "identifier"
        const val KEY_TYPE = "type"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_SURAH = "surah"
        const val KEY_VERSE = "verse"
        const val KEY_JOURNEY = "journey"
    }
}

/**
 * Fires at the user's preferred time, builds today's verse notification fresh
 * (iOS bakes 7 days ahead instead), posts it, then re-arms for tomorrow.
 */
class DailyVerseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val manager = NotificationManager
        // If the user turned dailies off after this was enqueued, do nothing and
        // don't re-arm; the next refreshAllSchedules() re-arms when re-enabled.
        if (!manager.preferences.enabled) return Result.success()

        postTodayVerse()

        manager.scheduleDailyVerse() // tomorrow's firing
        return Result.success()
    }

    private suspend fun postTodayVerse() {
        DailyVerseProvider.loadIfNeeded(applicationContext)
        val selection = DailyVerseProvider.verseFor(Date()) ?: return

        val verse = DataManager.shared.loadQuranData()
            .verses[selection.surah.toString()]
            ?.get(selection.verse.toString())
            ?: return

        // The body is nothing but the verse (iOS 7.4): Arabic, translation, and
        // optionally the first gem's core insight in place of the old commentary.
        var body = verse.arabicText
        if (verse.translation.isNotEmpty()) body += "\n\n" + verse.translation
        if (NotificationManager.preferences.includeTafsir) {
            val insight = DataManager.shared.loadTafsirData(selection.surah)
                ?.verses?.get(selection.verse.toString())
                ?.quickOverview?.concepts?.firstOrNull()?.coreInsight
            if (!insight.isNullOrEmpty()) body += "\n\n💡 " + insight.take(150) + "..."
        }

        // On a sacred day the occasion replaces the generic title; the theme
        // follows it (iOS shows the theme as the notification subtitle).
        val title = (selection.occasionEn ?: "Verse of the Day") + " · " + selection.themeEn

        NotificationPoster.post(
            context = applicationContext,
            identifier = NotificationManager.WORK_DAILY_VERSE,
            type = NotificationType.dailyVerse,
            title = title,
            body = body,
            surah = selection.surah,
            verse = selection.verse
        )
    }
}
