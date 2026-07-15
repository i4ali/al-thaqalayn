package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.BadgeAward
import com.thaqalayn.app.model.BadgeType
import com.thaqalayn.app.model.CommentaryLanguage

/**
 * Language-driven copy for the Progress tab (iOS ProgressTabStrings). Numbers stay
 * Western digits per app convention; rank badges reuse the shipped Arabic honorific
 * (BadgeType.subtitle) for both Urdu and Arabic.
 */
object ProgressStrings {
    fun yourJourneyEyebrow(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "رحلتك"
        CommentaryLanguage.URDU -> "آپ کا سفر"
        else -> "Your Journey"
    }

    fun progressTitle(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "التقدّم"
        CommentaryLanguage.URDU -> "پیش رفت"
        else -> "Progress"
    }

    fun progressSubtitle(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "سجلّ وقتك مع القرآن"
        CommentaryLanguage.URDU -> "قرآن کے ساتھ گزرے آپ کے وقت کا ریکارڈ"
        else -> "A record of your time with the Qur'an"
    }

    fun versesRead(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "الآيات المقروءة"
        CommentaryLanguage.URDU -> "پڑھی گئی آیات"
        else -> "Verses Read"
    }

    fun surahsComplete(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "السور المكتملة"
        CommentaryLanguage.URDU -> "مکمل سورتیں"
        else -> "Surahs Complete"
    }

    fun quizzesDone(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "الاختبارات المنجزة"
        CommentaryLanguage.URDU -> "مکمل کوئز"
        else -> "Quizzes Done"
    }

    fun totalSawab(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "مجموع الثواب"
        CommentaryLanguage.URDU -> "کل ثواب"
        else -> "Total Sawab"
    }

    fun ofTotal(n: Int, l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "من $n"
        CommentaryLanguage.URDU -> "$n میں سے"
        else -> "of $n"
    }

    fun surahsTested(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "سور مُختبَرة"
        CommentaryLanguage.URDU -> "آزمودہ سورتیں"
        else -> "surahs tested"
    }

    fun blessingsEarned(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "بركات مكتسبة"
        CommentaryLanguage.URDU -> "حاصل شدہ برکات"
        else -> "blessings earned"
    }

    fun dayStreak(n: Int, l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "سلسلة $n يوم"
        CommentaryLanguage.URDU -> "$n دن کا سلسلہ"
        else -> "$n Day Streak"
    }

    fun keepItGoing(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "واصل التقدّم!"
        CommentaryLanguage.URDU -> "اسے جاری رکھیں!"
        else -> "Keep it going!"
    }

    fun best(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "الأفضل"
        CommentaryLanguage.URDU -> "بہترین"
        else -> "Best"
    }

    fun badgesDivider(count: Int, total: Int, l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "الأوسمة · $count من $total"
        CommentaryLanguage.URDU -> "تمغے · $count / $total"
        else -> "Badges · $count of $total"
    }

    fun noBadgesYet(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "لا أوسمة بعد"
        CommentaryLanguage.URDU -> "ابھی کوئی تمغہ نہیں"
        else -> "No badges yet"
    }

    fun earnBadgesHint(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "أكمل السور وواصل سلسلتك لتكسب الأوسمة."
        CommentaryLanguage.URDU -> "تمغے حاصل کرنے کے لیے سورتیں مکمل کریں اور تسلسل برقرار رکھیں۔"
        else -> "Complete surahs and build streaks to earn badges."
    }

    /**
     * Badge tile label: surah-completion badges show the (English) surah name; rank
     * badges use the English transliteration for EN and the Arabic honorific otherwise.
     */
    fun badgeLabel(badge: BadgeAward, l: CommentaryLanguage): String {
        if (badge.badgeType == BadgeType.SURAH_COMPLETION) return badge.surahName
        return when (l) {
            CommentaryLanguage.ENGLISH -> badge.badgeType.title
            else -> badge.badgeType.subtitle
        }
    }

    fun quran(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "القرآن"
        CommentaryLanguage.URDU -> "قرآن"
        else -> "Quran"
    }

    fun surahs(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "السور"
        CommentaryLanguage.URDU -> "سورتیں"
        else -> "Surahs"
    }

    fun quizzes(l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> "اختبارات"
        CommentaryLanguage.URDU -> "کوئز"
        else -> "Quizzes"
    }

    /** Localizes the seasonal ring label ("Ramadan" / "Hajj" / "Muharram"). */
    fun seasonal(raw: String, l: CommentaryLanguage): String = when (l) {
        CommentaryLanguage.ARABIC -> when (raw) {
            "Hajj" -> "الحج"
            "Muharram" -> "محرم"
            else -> "رمضان"
        }
        CommentaryLanguage.URDU -> when (raw) {
            "Hajj" -> "حج"
            "Muharram" -> "محرم"
            else -> "رمضان"
        }
        else -> raw
    }
}
