package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

// MARK: - Daily Verse Notification Models (iOS QuranModels.swift parity)

@Serializable
data class IslamicMonthVerseData(
    val months: List<IslamicMonth>
)

@Serializable
data class IslamicMonth(
    val month: Int,
    val name: String,
    val arabicName: String,
    val theme: String,
    val significance: String,
    val verses: List<DailyVerseEntry>
)

@Serializable
data class DailyVerseEntry(
    val surah: Int,
    val verse: Int,
    val relevance: String,
    val theme: String
) {
    val id: String get() = "$surah:$verse"
}

/**
 * iOS NotificationPreferences stores `time: Date`; on Android the wall-clock
 * pair is stored directly (same default 9:00 AM).
 */
@Serializable
data class NotificationPreferences(
    val enabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0,
    val language: CommentaryLanguage = CommentaryLanguage.ENGLISH,
    val includeTafsir: Boolean = true
)

// MARK: - Notification Inbox (iOS NotificationInboxStore.swift parity)

@Serializable
enum class NotificationType {
    dailyVerse,
    streak,
    milestone,
    nudge,
    nearCompletion,
    journey
}

@Serializable
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    /** Epoch millis (iOS stores Date). */
    val timestamp: Long,
    val isRead: Boolean = false,
    val surahNumber: Int? = null,
    val verseNumber: Int? = null,
    val journeyId: String? = null
)
