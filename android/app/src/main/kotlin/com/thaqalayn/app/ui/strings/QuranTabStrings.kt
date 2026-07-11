package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.CommentaryLanguage.ARABIC
import com.thaqalayn.app.model.CommentaryLanguage.URDU

/**
 * Language-driven copy for the Quran tab, keyed off the global language picker.
 * Surah names and meanings stay English by product decision (the Arabic surah
 * name is already shown beside the English one).
 */
object QuranTabStrings {
    fun greeting(l: CommentaryLanguage) = when (l) {
        ARABIC -> "السلام عليكم"; URDU -> "السلام علیکم"; else -> "Assalamu alaykum"
    }

    fun nobleQuranEyebrow(l: CommentaryLanguage) = when (l) {
        ARABIC -> "القرآن الكريم"; URDU -> "قرآنِ کریم"; else -> "The Noble Qur'an"
    }

    fun readAndReflect(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اقرأ وتدبّر"; URDU -> "پڑھیں اور غور کریں"; else -> "Read & Reflect"
    }

    fun holyQuran(l: CommentaryLanguage) = when (l) {
        ARABIC -> "القرآن الكريم"; URDU -> "قرآنِ مجید"; else -> "The Holy Quran"
    }

    fun continueReading(l: CommentaryLanguage) = when (l) {
        ARABIC -> "متابعة القراءة"; URDU -> "مطالعہ جاری رکھیں"; else -> "Continue Reading"
    }

    fun resume(l: CommentaryLanguage) = when (l) {
        ARABIC -> "استئناف"; URDU -> "جاری رکھیں"; else -> "Resume"
    }

    fun searchPlaceholder(l: CommentaryLanguage) = when (l) {
        ARABIC -> "ابحث في السور والآيات والمواضيع…"
        URDU -> "سورتیں، آیات، موضوعات تلاش کریں…"
        else -> "Search surahs, verses, themes…"
    }

    fun surahsCount(n: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "$n سورة"; URDU -> "$n سورتیں"; else -> "$n Surahs"
    }

    fun versesCount(n: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "$n آية"; URDU -> "$n آیات"; else -> "$n verses"
    }

    /** Maps the data's "Meccan"/"Medinan" revelationType to the active language. */
    fun revelation(raw: String, l: CommentaryLanguage): String {
        val isMeccan = raw.equals("Meccan", ignoreCase = true)
        return when (l) {
            ARABIC -> if (isMeccan) "مكية" else "مدنية"
            URDU -> if (isMeccan) "مکی" else "مدنی"
            else -> raw
        }
    }

    fun verseOf(n: Int, total: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "الآية $n من $total"; URDU -> "آیت $n از $total"; else -> "Verse $n of $total"
    }

    fun percentComplete(p: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "$p% مكتمل"; URDU -> "$p% مکمل"; else -> "$p% complete"
    }

    // Search results
    fun surahsLabel(l: CommentaryLanguage) = when (l) {
        ARABIC -> "السور"; URDU -> "سورتیں"; else -> "Surahs"
    }

    fun versesLabel(l: CommentaryLanguage) = when (l) {
        ARABIC -> "الآيات"; URDU -> "آیات"; else -> "Verses"
    }

    fun themesLabel(l: CommentaryLanguage) = when (l) {
        ARABIC -> "المواضيع"; URDU -> "موضوعات"; else -> "Themes"
    }

    fun showingFirst(showing: Int, total: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "عرض أول $showing من $total"
        URDU -> "پہلے $showing از $total دکھائے جا رہے ہیں"
        else -> "Showing first $showing of $total"
    }

    fun noResults(query: String, l: CommentaryLanguage) = when (l) {
        ARABIC -> "لا نتائج لـ «$query»"
        URDU -> "«$query» کے لیے کوئی نتیجہ نہیں"
        else -> "No results for “$query”"
    }
}

/** Tab bar labels (iOS MainTabView.tabLabel). */
object TabStrings {
    fun today(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اليوم"; URDU -> "آج"; else -> "Today"
    }

    fun quran(l: CommentaryLanguage) = when (l) {
        ARABIC -> "القرآن"; URDU -> "قرآن"; else -> "Quran"
    }

    fun explore(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اكتشف"; URDU -> "دریافت"; else -> "Explore"
    }

    fun progress(l: CommentaryLanguage) = when (l) {
        ARABIC -> "التقدّم"; URDU -> "پیش رفت"; else -> "Progress"
    }

    fun journey(l: CommentaryLanguage) = when (l) {
        ARABIC -> "رحلة"; URDU -> "سفر"; else -> "Journey"
    }
}
