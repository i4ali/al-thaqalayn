package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.Bookmark
import com.thaqalayn.app.model.BookmarkSortOrder
import com.thaqalayn.app.model.PassageRef
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
        if (isBookmarked(surahNumber, verseNumber)) {
            errorMessage = "This verse is already bookmarked"
            return false
        }
        if (!hasRoomForBookmark()) return false
        val now = System.currentTimeMillis()
        commit(
            Bookmark(
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
        )
        return true
    }

    // MARK: - Passage bookmarks (iOS 9.0)

    /** The saved record for one passage (ruku) of a surah, if any. */
    fun passageBookmark(surah: Int, index: Int): Bookmark? =
        bookmarks.firstOrNull { it.matchesPassage(surah, index) }

    fun isPassageBookmarked(surah: Int, index: Int): Boolean = passageBookmark(surah, index) != null

    /** Indices of the saved passages of one surah, for the passage list rows. */
    fun bookmarkedPassageIndices(surah: Int): Set<Int> =
        bookmarks.mapNotNull { if (it.surahNumber == surah) it.passageIndex else null }.toSet()

    /**
     * Saves a whole passage. [title] is the passage's display title and
     * [firstVerseArabic] the Arabic of its first verse; both are snapshotted on the
     * record. Shares the limit with verses.
     */
    fun addPassageBookmark(ref: PassageRef, surahName: String, title: String, firstVerseArabic: String): Boolean {
        if (isPassageBookmarked(ref.surah, ref.index)) {
            errorMessage = "This passage is already bookmarked"
            return false
        }
        if (!hasRoomForBookmark()) return false
        val now = System.currentTimeMillis()
        commit(
            Bookmark(
                id = UUID.randomUUID().toString(),
                surahNumber = ref.surah,
                verseNumber = ref.start,
                surahName = surahName,
                verseText = firstVerseArabic,
                verseTranslation = title,
                createdAt = now,
                updatedAt = now,
                passageIndex = ref.index
            )
        )
        return true
    }

    enum class PassageToggleResult { SAVED, REMOVED, REFUSED }

    /**
     * Saves the passage when it is not bookmarked and removes it when it is. The
     * passage list swipe, the hub heart and the reader heart all call this, so they
     * can never disagree. [PassageToggleResult.REFUSED] means the limit was reached.
     */
    fun togglePassageBookmark(ref: PassageRef, surahName: String, title: String, firstVerseArabic: String): PassageToggleResult {
        passageBookmark(ref.surah, ref.index)?.let {
            removeBookmark(it.id)
            return PassageToggleResult.REMOVED
        }
        return if (addPassageBookmark(ref, surahName, title, firstVerseArabic)) {
            PassageToggleResult.SAVED
        } else {
            PassageToggleResult.REFUSED
        }
    }

    private fun hasRoomForBookmark(): Boolean {
        if (bookmarks.size >= BOOKMARK_LIMIT) {
            errorMessage = "You've reached your bookmark limit ($BOOKMARK_LIMIT bookmarks)."
            return false
        }
        return true
    }

    private fun commit(bookmark: Bookmark) {
        bookmarks = bookmarks + bookmark
        save()
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

    /** Verse bookmarks only: a saved passage does not light the heart of its first verse. */
    fun isBookmarked(surahNumber: Int, verseNumber: Int): Boolean = getBookmark(surahNumber, verseNumber) != null

    fun getBookmark(surahNumber: Int, verseNumber: Int): Bookmark? =
        bookmarks.firstOrNull { it.matchesVerse(surahNumber, verseNumber) }

    fun sortedBookmarks(order: BookmarkSortOrder = BookmarkSortOrder.DATE_DESCENDING): List<Bookmark> =
        when (order) {
            BookmarkSortOrder.DATE_ASCENDING -> bookmarks.sortedBy { it.createdAt }
            BookmarkSortOrder.DATE_DESCENDING -> bookmarks.sortedByDescending { it.createdAt }
            BookmarkSortOrder.SURAH_ORDER -> bookmarks.sortedWith(Bookmark.quranOrder)
            BookmarkSortOrder.ALPHABETICAL -> bookmarks.sortedBy { it.surahName }
        }

    fun clearError() {
        errorMessage = null
    }
}
