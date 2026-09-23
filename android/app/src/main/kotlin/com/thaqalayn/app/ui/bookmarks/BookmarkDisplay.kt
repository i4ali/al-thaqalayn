package com.thaqalayn.app.ui.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.model.Bookmark
import com.thaqalayn.app.model.PassageRef

// Display helpers for bookmark rows and cards (iOS Bookmark+Display.swift). A
// bookmark stores a snapshot of its text; these prefer the live bundled data (the
// verse's translation, the passage's title) and fall back to the snapshot.

/** The saved passage, once the passage index has loaded. Null for a verse bookmark. */
val Bookmark.passageRef: PassageRef?
    get() {
        val index = passageIndex ?: return null
        return DataManager.shared.passageIndex?.passage(surahNumber, index)
    }

/**
 * The passage's current title from the shipped commentary, else the title
 * snapshotted when it was saved. For a verse bookmark, the stored translation.
 */
val Bookmark.passageTitle: String
    get() {
        val index = passageIndex ?: return verseTranslation
        return PassageStore.passage(surahNumber, index)?.title?.en ?: verseTranslation
    }

/** "Verse 30" for a verse; "Passage 4 · Verses 30 to 39" for a passage. */
val Bookmark.positionLabel: String
    get() {
        val index = passageIndex ?: return "Verse $verseNumber"
        val ref = passageRef ?: return "Passage $index"
        return "Passage $index · Verses ${ref.rangeLabel}"
    }

/** "Passage 4 · 10 verses" for a passage; null for a verse. */
val Bookmark.passageMetaLabel: String?
    get() {
        val index = passageIndex ?: return null
        val ref = passageRef ?: return "Passage $index"
        val verses = if (ref.verseCount == 1) "1 verse" else "${ref.verseCount} verses"
        return "Passage $index · $verses"
    }

/** "2:30 to 39" for a passage, "2:30" for a verse. */
val Bookmark.referenceLabel: String
    get() = passageRef?.let { "$surahNumber:${it.rangeLabel}" } ?: verseReference

/**
 * The text a card shows under the reference. Verse: its current English
 * translation, read live from the bundled Quran data, falling back to the snapshot
 * (which only goes stale for display when the shipped translation edition changes,
 * as it did with the 2026-09 move from Sahih International to Ali Quli Qarai).
 * Passage: its title. Mirrors iOS `Bookmark.displayTranslation`.
 */
@Composable
fun rememberBookmarkTranslation(bookmark: Bookmark): String {
    if (bookmark.isPassage) {
        LaunchedEffect(bookmark.surahNumber) { PassageStore.load(bookmark.surahNumber) }
        return bookmark.passageTitle
    }
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
