package com.thaqalayn.app.notifications

import android.content.Context
import android.content.SharedPreferences
import com.thaqalayn.app.model.NotificationItem
import kotlinx.serialization.json.Json

/**
 * Persists delivered notifications for the in-app inbox (iOS NotificationInboxStore).
 * On Android every notification is posted by our own workers, so items are
 * recorded at post time - no delegate/sweep split like iOS needs.
 */
object NotificationInboxStore {
    private const val STORAGE_KEY = "notificationHistoryV2"

    /** Cap inbox growth - oldest entries fall off. */
    private const val MAX_ITEMS = 50

    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_notifications", Context.MODE_PRIVATE)
    }

    fun load(): List<NotificationItem> {
        val stored = prefs.getString(STORAGE_KEY, null) ?: return emptyList()
        val decoded = try {
            json.decodeFromString<List<NotificationItem>>(stored)
        } catch (e: Exception) {
            emptyList()
        }
        return decoded.sortedByDescending { it.timestamp }
    }

    fun save(items: List<NotificationItem>) {
        prefs.edit().putString(STORAGE_KEY, json.encodeToString(items)).apply()
    }

    /** Record a single delivered notification (deduped by id). */
    fun record(item: NotificationItem) {
        val items = load()
        if (items.any { it.id == item.id }) return
        save((listOf(item) + items).take(MAX_ITEMS))
    }
}
