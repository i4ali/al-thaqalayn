package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.CommentaryLanguage.ARABIC
import com.thaqalayn.app.model.CommentaryLanguage.URDU
import com.thaqalayn.app.model.DailyChallengeFormat

/** Language-driven copy for the Today tab (iOS TodayStrings). */
object TodayStrings {
    fun greeting(l: CommentaryLanguage) = when (l) {
        ARABIC -> "السلام عليكم"; URDU -> "السلام علیکم"; else -> "Assalamu alaykum"
    }

    fun greeting(name: String, l: CommentaryLanguage): String {
        val base = greeting(l)
        if (name.isEmpty()) return base
        return base + (if (l.isRTL) "، " else ", ") + name
    }

    fun today(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اليوم"; URDU -> "آج"; else -> "Today"
    }

    fun reminderEyebrow(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تذكير اليوم"; URDU -> "آج کی نصیحت"; else -> "A reminder for today"
    }

    fun continueReading(l: CommentaryLanguage) = when (l) {
        ARABIC -> "متابعة القراءة"; URDU -> "مطالعہ جاری رکھیں"; else -> "Continue reading"
    }

    fun duaOfTheDay(l: CommentaryLanguage) = when (l) {
        ARABIC -> "دعاء اليوم"; URDU -> "آج کی دعا"; else -> "Du'a of the day"
    }

    fun startJourney(l: CommentaryLanguage) = when (l) {
        ARABIC -> "ابدأ رحلتك"; URDU -> "اپنا سفر شروع کریں"; else -> "Start your journey"
    }

    fun openFatiha(l: CommentaryLanguage) = when (l) {
        ARABIC -> "افتح سورة الفاتحة"; URDU -> "سورۃ الفاتحہ کھولیں"; else -> "Open Surah Al-Fatiha"
    }

    fun begin(l: CommentaryLanguage) = when (l) {
        ARABIC -> "ابدأ"; URDU -> "شروع کریں"; else -> "Begin"
    }

    fun resume(l: CommentaryLanguage) = when (l) {
        ARABIC -> "استئناف"; URDU -> "جاری رکھیں"; else -> "Resume"
    }

    fun verseOf(n: Int, total: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "الآية $n من $total"; URDU -> "آیت $n از $total"; else -> "Verse $n of $total"
    }

    fun percentComplete(p: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "$p% مكتمل"; URDU -> "$p% مکمل"; else -> "$p% complete"
    }
}

/** Bookmark spotlight card copy (iOS BookmarkSpotlightStrings). */
object BookmarkSpotlightStrings {
    fun eyebrow(l: CommentaryLanguage) = when (l) {
        ARABIC -> "آياتك المحفوظة"; URDU -> "محفوظ کردہ آیات"; else -> "Your bookmarks"
    }

    fun allBookmarks(n: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "كل الآيات المحفوظة ($n)"; URDU -> "تمام محفوظ آیات ($n)"; else -> "All bookmarks ($n)"
    }
}

/** Daily challenge copy (iOS DailyChallengeStrings). */
object DailyChallengeStrings {
    fun dailyChallenge(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تحدي اليوم"; URDU -> "آج کا چیلنج"; else -> "Daily Challenge"
    }

    fun doneForToday(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تم لليوم"; URDU -> "آج کے لیے مکمل"; else -> "Done for today"
    }

    fun correct(l: CommentaryLanguage) = when (l) {
        ARABIC -> "صحيح"; URDU -> "درست"; else -> "Correct"
    }

    fun notQuite(l: CommentaryLanguage) = when (l) {
        ARABIC -> "ليس تمامًا"; URDU -> "قریب تھے"; else -> "Not quite"
    }

    fun flipCard(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اقلب البطاقة"; URDU -> "کارڈ پلٹیں"; else -> "Tap to flip"
    }

    fun gotIt(l: CommentaryLanguage) = when (l) {
        ARABIC -> "فهمت"; URDU -> "سمجھ گیا"; else -> "Got it"
    }

    fun reviewAgain(l: CommentaryLanguage) = when (l) {
        ARABIC -> "راجع مجددًا"; URDU -> "دوبارہ دیکھیں"; else -> "Review again"
    }

    fun trueLabel(l: CommentaryLanguage) = when (l) {
        ARABIC -> "صحيح"; URDU -> "سچ"; else -> "True"
    }

    fun falseLabel(l: CommentaryLanguage) = when (l) {
        ARABIC -> "خطأ"; URDU -> "جھوٹ"; else -> "False"
    }

    fun dayUnit(count: Int, l: CommentaryLanguage): String = when (l) {
        ARABIC -> when (count) {
            1 -> "يوم واحد"
            2 -> "يومان"
            else -> "$count أيام"
        }
        URDU -> if (count == 1) "۱ دن" else "$count دن"
        else -> if (count == 1) "1 day" else "$count days"
    }

    fun streakLabel(count: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "سلسلة ${dayUnit(count, l)}"
        URDU -> "${dayUnit(count, l)} کا سلسلہ"
        else -> "${dayUnit(count, l)} streak"
    }

    fun teaser(format: DailyChallengeFormat, l: CommentaryLanguage): String = when (format) {
        DailyChallengeFormat.multipleChoice -> when (l) {
            ARABIC -> "اختر الإجابة الصحيحة"; URDU -> "صحیح جواب چنیں"; else -> "Pick the right answer"
        }
        DailyChallengeFormat.trueFalse -> when (l) {
            ARABIC -> "صحيح أم خطأ؟"; URDU -> "سچ ہے یا جھوٹ؟"; else -> "True or false?"
        }
        DailyChallengeFormat.flashcard -> when (l) {
            ARABIC -> "بطاقة تعليمية — اقلبها واختبر نفسك"; URDU -> "فلیش کارڈ — پلٹیں اور جانچیں"; else -> "Flashcard — flip to test yourself"
        }
        DailyChallengeFormat.fillInBlank -> when (l) {
            ARABIC -> "املأ الفراغ"; URDU -> "خالی جگہ بھریں"; else -> "Fill in the blank"
        }
    }

    fun completionTitle(l: CommentaryLanguage) = when (l) {
        ARABIC -> "أحسنت"; URDU -> "شاباش"; else -> "Well done"
    }

    fun doneButton(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تم"; URDU -> "مکمل"; else -> "Done"
    }
}

/** Daily crossword copy (iOS DailyCrosswordStrings). */
object DailyCrosswordStrings {
    fun dailyCrossword(l: CommentaryLanguage) = when (l) {
        ARABIC -> "الكلمات المتقاطعة"; URDU -> "روزانہ کراس ورڈ"; else -> "Daily Crossword"
    }

    fun teaser(l: CommentaryLanguage) = when (l) {
        ARABIC -> "٦ كلمات للحل"; URDU -> "حل کرنے کے لیے ٦ الفاظ"; else -> "6 words to solve"
    }

    fun doneForToday(l: CommentaryLanguage) = when (l) {
        ARABIC -> "اكتمل اليوم"; URDU -> "آج مکمل"; else -> "Done for today"
    }

    fun solved(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تم الحل!"; URDU -> "حل ہو گیا!"; else -> "Solved!"
    }

    fun comeBackTomorrow(l: CommentaryLanguage) = when (l) {
        ARABIC -> "عُد غدًا لِلُغزٍ جديد."; URDU -> "نیا معمہ کل دوبارہ آئیں۔"; else -> "Come back tomorrow for a new puzzle."
    }

    fun across(l: CommentaryLanguage) = when (l) {
        ARABIC -> "أفقي"; URDU -> "افقی"; else -> "Across"
    }

    fun down(l: CommentaryLanguage) = when (l) {
        ARABIC -> "عمودي"; URDU -> "عمودی"; else -> "Down"
    }

    fun hint(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تلميح"; URDU -> "اشارہ"; else -> "Hint"
    }

    fun done(l: CommentaryLanguage) = when (l) {
        ARABIC -> "تم"; URDU -> "مکمل"; else -> "Done"
    }

    fun words(l: CommentaryLanguage) = when (l) {
        ARABIC -> "كلمات"; URDU -> "الفاظ"; else -> "words"
    }

    fun streakLabel(n: Int, l: CommentaryLanguage) = when (l) {
        ARABIC -> "$n أيام متتالية"; URDU -> "$n دن کا سلسلہ"; else -> "$n-day streak"
    }
}
