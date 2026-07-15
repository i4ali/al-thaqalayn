package com.thaqalayn.app.notifications

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thaqalayn.app.MainActivity
import com.thaqalayn.app.R
import com.thaqalayn.app.model.NotificationItem
import com.thaqalayn.app.model.NotificationType

/**
 * Builds and posts the actual system notifications, and mirrors every post
 * into the in-app inbox. iOS splits this across UNNotificationRequest content
 * builders + the UNUserNotificationCenterDelegate; on Android one post path
 * covers foreground and background alike.
 */
object NotificationPoster {
    const val CHANNEL_DAILY_VERSE = "daily_verse"
    const val CHANNEL_PROGRESS = "progress"
    const val CHANNEL_JOURNEYS = "journeys"

    // Intent extras read back by MainActivity for deep links.
    const val EXTRA_TYPE = "notification_type"
    const val EXTRA_SURAH = "notification_surah"
    const val EXTRA_VERSE = "notification_verse"
    const val EXTRA_JOURNEY = "notification_journey"

    fun createChannels(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_DAILY_VERSE,
                "Daily Verse",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Verse of the day from the Islamic calendar" }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_PROGRESS,
                "Progress & Reminders",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Streaks, milestones and reading reminders" }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_JOURNEYS,
                "Journeys & Seasons",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Sacred season journeys and the Day of Arafah" }
        )
    }

    fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun channelFor(type: NotificationType): String = when (type) {
        NotificationType.dailyVerse -> CHANNEL_DAILY_VERSE
        NotificationType.journey -> CHANNEL_JOURNEYS
        else -> CHANNEL_PROGRESS
    }

    /**
     * Post a notification and record it into the inbox. [identifier] mirrors the
     * iOS request identifier (daily_verse_0, streak_reminder, journey_start_x...)
     * and keeps re-posts of the same identifier as a single system notification.
     */
    fun post(
        context: Context,
        identifier: String,
        type: NotificationType,
        title: String,
        body: String,
        surah: Int? = null,
        verse: Int? = null,
        journeyId: String? = null
    ) {
        val now = System.currentTimeMillis()

        // Inbox first - it must not depend on the permission still being granted.
        NotificationInboxStore.record(
            NotificationItem(
                id = "$identifier|${now / 1000}",
                title = title,
                message = body,
                type = type,
                timestamp = now,
                isRead = false,
                surahNumber = surah,
                verseNumber = verse,
                journeyId = journeyId
            )
        )

        if (!hasPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TYPE, type.name)
            surah?.let { putExtra(EXTRA_SURAH, it) }
            verse?.let { putExtra(EXTRA_VERSE, it) }
            journeyId?.let { putExtra(EXTRA_JOURNEY, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            identifier.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelFor(type))
            .setSmallIcon(R.drawable.ph_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(identifier.hashCode(), notification)
        } catch (e: SecurityException) {
            // Permission revoked between the check and the post - inbox still has it.
        }
    }
}
