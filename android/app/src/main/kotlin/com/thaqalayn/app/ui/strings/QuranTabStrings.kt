package com.thaqalayn.app.ui.strings

/**
 * Chrome copy for the Quran tab (iOS QuranTabStrings). English only; the Arabic
 * surah name is shown beside the English one.
 */
object QuranTabStrings {
    val greeting = "Assalamu alaykum"

    val nobleQuranEyebrow = "The Noble Qur'an"

    val readAndReflect = "Read & Reflect"

    val holyQuran = "The Holy Quran"

    val continueReading = "Continue Reading"

    val resume = "Resume"

    val searchPlaceholder = "Search surahs, verses, themes…"

    fun surahsCount(n: Int) = "$n Surahs"

    fun versesCount(n: Int) = "$n verses"

    /** The data's "Meccan"/"Medinan" revelationType, shown as-is. */
    fun revelation(raw: String) = raw

    /** Surah-row passage count: "40 passages" / "1 passage". */
    fun passagesCount(n: Int) = if (n == 1) "1 passage" else "$n passages"

    /** Surah-row passage progress: "3 of 40 passages" / "1 of 1 passage". */
    fun passagesRead(read: Int, total: Int) = if (total == 1) "$read of 1 passage" else "$read of $total passages"

    /** Continue Reading progress: "3 of 24 passages read". */
    fun passagesReadOf(read: Int, total: Int) = "$read of $total passages read"

    fun verseOf(n: Int, total: Int) = "Verse $n of $total"

    fun percentComplete(p: Int) = "$p% complete"

    // Search results
    val surahsLabel = "Surahs"

    val versesLabel = "Verses"

    val themesLabel = "Themes"

    fun showingFirst(showing: Int, total: Int) = "Showing first $showing of $total"

    fun noResults(query: String) = "No results for “$query”"
}

/** Tab bar labels (iOS MainTabView.tabLabel). */
object TabStrings {
    val today = "Today"

    val quran = "Quran"

    val explore = "Explore"

    val progress = "Progress"

    val journey = "Journey"
}
