package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.Bookmark
import com.thaqalayn.app.model.BookmarkSortOrder
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Bookmarks, stored locally (the iOS Supabase sync layer is dropped on Android).
 * Same 10-bookmark limit as iOS.
 */
object BookmarkManager {
    private const val BOOKMARKS_KEY = "localBookmarks"
    const val BOOKMARK_LIMIT = 10

    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    var bookmarks by mutableStateOf<List<Bookmark>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_bookmarks", Context.MODE_PRIVATE)
        bookmarks = prefs.getString(BOOKMARKS_KEY, null)?.let {
            try {
                json.decodeFromString<List<Bookmark>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    private fun save() {
        prefs.edit().putString(BOOKMARKS_KEY, json.encodeToString(bookmarks)).apply()
    }

    fun addBookmark(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String,
        verseText: String,
        verseTranslation: String,
        notes: String? = null,
        tags: List<String> = emptyList()
    ): Boolean {
        if (isBookmarked(surahNumber, verseNumber)) return false
        if (bookmarks.size >= BOOKMARK_LIMIT) {
            errorMessage = "You've reached your bookmark limit ($BOOKMARK_LIMIT bookmarks)."
            return false
        }
        val now = System.currentTimeMillis()
        bookmarks = bookmarks + Bookmark(
            id = UUID.randomUUID().toString(),
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            surahName = surahName,
            verseText = verseText,
            verseTranslation = verseTranslation,
            notes = notes,
            tags = tags,
            createdAt = now,
            updatedAt = now
        )
        save()
        return true
    }

    fun removeBookmark(id: String) {
        bookmarks = bookmarks.filterNot { it.id == id }
        save()
    }

    fun toggleBookmark(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String,
        verseText: String,
        verseTranslation: String
    ): Boolean {
        val existing = getBookmark(surahNumber, verseNumber)
        return if (existing != null) {
            removeBookmark(existing.id)
            true
        } else {
            addBookmark(surahNumber, verseNumber, surahName, verseText, verseTranslation)
        }
    }

    fun isBookmarked(surahNumber: Int, verseNumber: Int): Boolean =
        bookmarks.any { it.surahNumber == surahNumber && it.verseNumber == verseNumber }

    fun getBookmark(surahNumber: Int, verseNumber: Int): Bookmark? =
        bookmarks.firstOrNull { it.surahNumber == surahNumber && it.verseNumber == verseNumber }

    fun sortedBookmarks(order: BookmarkSortOrder = BookmarkSortOrder.DATE_DESCENDING): List<Bookmark> =
        when (order) {
            BookmarkSortOrder.DATE_ASCENDING -> bookmarks.sortedBy { it.createdAt }
            BookmarkSortOrder.DATE_DESCENDING -> bookmarks.sortedByDescending { it.createdAt }
            BookmarkSortOrder.SURAH_ORDER -> bookmarks.sortedWith(
                compareBy({ it.surahNumber }, { it.verseNumber })
            )
            BookmarkSortOrder.ALPHABETICAL -> bookmarks.sortedBy { it.surahName }
        }

    fun clearError() {
        errorMessage = null
    }
}
