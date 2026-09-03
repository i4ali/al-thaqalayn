package com.thaqalayn.app.ui.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.model.Bookmark

/**
 * The bookmark's current English translation, read live from the bundled Quran data,
 * falling back to the text snapshotted when the bookmark was saved
 * (`Bookmark.verseTranslation`).
 *
 * The snapshot on the stored record is left untouched; it only goes stale for display
 * when the shipped translation edition changes, as it did with the 2026-09 move from
 * Sahih International to Ali Quli Qarai. Reading live keeps old bookmarks in step with
 * the reader instead of showing the wording that was current when they were saved.
 * Mirrors iOS `Bookmark.displayTranslation`.
 */
@Composable
fun rememberBookmarkTranslation(bookmark: Bookmark): String {
    val live by produceState<String?>(
        initialValue = null,
        bookmark.surahNumber,
        bookmark.verseNumber
    ) {
        value = DataManager.shared.loadQuranData()
            .verses[bookmark.surahNumber.toString()]
            ?.get(bookmark.verseNumber.toString())
            ?.translation
    }
    return live ?: bookmark.verseTranslation
}
