package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.CommentaryLanguage

/**
 * Language-driven copy for the Journey tab - hub, the seasonal journeys
 * (Ramadan, Dhul-Hijjah/Hajj, Muharram, Fatimiyya, Arbaeen), their day lists
 * and day-detail screens. Ported verbatim from iOS JourneyStrings.
 * Day NARRATIVE content (theme/tafsir/reflection/dua/notes) is localized via
 * the model localized*() accessors, not here.
 */
object JourneyStrings {

    private fun pick(l: CommentaryLanguage, en: String, ur: String, ar: String): String =
        when (l) {
            CommentaryLanguage.URDU -> ur
            CommentaryLanguage.ARABIC -> ar
            else -> en
        }

    // MARK: - Hub
    fun sacredSeasons(l: CommentaryLanguage) = pick(l, "Sacred Seasons", "مقدس ایام", "المواسم المقدّسة")
    fun journeys(l: CommentaryLanguage) = pick(l, "Journeys", "روحانی سفر", "الرحلات")
    fun grow(l: CommentaryLanguage) = pick(l, "Grow", "نشوونما", "النمو")
    fun comingSoon(l: CommentaryLanguage) = pick(l, "Coming soon", "جلد آ رہا ہے", "قريباً")
    fun premium(l: CommentaryLanguage) = pick(l, "Premium", "پریمیئم", "بريميوم")

    // MARK: - Deep Dives + Inside the Surah (hub shelves, card chrome, toggle)
    fun deepDives(l: CommentaryLanguage) = pick(l, "Deep Dives", "گہرے سفر", "غوصٌ عميق")
    fun deepDiveOnItsWay(title: String, l: CommentaryLanguage) =
        pick(l, "$title is on its way.", "$title جلد دستیاب ہوگا۔", "$title قادمٌ قريباً.")
    fun deepDiveEyebrow(l: CommentaryLanguage) = pick(l, "Deep Dive", "گہرا مطالعہ", "غوص عميق")
    fun soon(l: CommentaryLanguage) = pick(l, "SOON", "جلد", "قريباً")
    fun insideTheSurah(l: CommentaryLanguage) = pick(l, "Inside the Surah", "سورہ کے اندر", "في قلب السورة")
    fun surahJourneyEyebrow(l: CommentaryLanguage) = pick(l, "Surah Journey", "سورہ کا سفر", "رحلة السورة")
    fun readTheFullSurah(l: CommentaryLanguage) = pick(l, "Read the full surah", "مکمل سورہ پڑھیں", "اقرأ السورة كاملة")
    fun readAndTafsir(l: CommentaryLanguage) = pick(l, "Read & Tafsir", "مطالعہ و تفسیر", "القراءة والتفسير")
    fun journey(l: CommentaryLanguage) = pick(l, "Journey", "سفر", "رحلة")

    // MARK: - Shelf status eyebrows (compact hub cards)
    fun live(l: CommentaryLanguage) = pick(l, "LIVE", "جاری", "جارٍ")
    fun ready(l: CommentaryLanguage) = pick(l, "READY", "تیار", "جاهز")
    fun inDaysShort(days: Int, l: CommentaryLanguage) = pick(
        l,
        "IN $days DAY${if (days == 1) "" else "S"}",
        "$days دن میں",
        "بعد $days يوماً"
    )
    fun endedShort(l: CommentaryLanguage) = pick(l, "ENDED", "ختم", "انتهت")

    /** "See all" link on a shelf header - count is that section's live total. */
    fun allCount(n: Int, l: CommentaryLanguage) = pick(l, "All $n", "تمام $n", "الكل $n")

    fun nextUp(l: CommentaryLanguage) = pick(l, "NEXT UP", "اگلا", "التالي")
    fun comingSoonInDays(days: Int, l: CommentaryLanguage) = pick(
        l,
        "Coming soon · in $days day${if (days == 1) "" else "s"}",
        "جلد آ رہا ہے · $days دن میں",
        "قريباً · بعد $days يوماً"
    )
    fun endedReturns(returnsLabel: String, l: CommentaryLanguage) =
        pick(l, "Ended · $returnsLabel", "ختم ہوا · $returnsLabel", "انتهت · $returnsLabel")
    fun gotIt(l: CommentaryLanguage) = pick(l, "Got it", "سمجھ گیا", "حسناً")

    // Locked-journey alert
    fun hasEnded(title: String, l: CommentaryLanguage) =
        pick(l, "$title has ended", "$title ختم ہو چکا ہے", "$title قد انتهت")
    fun notOpenYet(title: String, l: CommentaryLanguage) =
        pick(l, "$title isn't open yet", "$title ابھی نہیں کھلا", "$title لم تبدأ بعد")
    fun upNextInDays(title: String, days: Int, l: CommentaryLanguage) = pick(
        l,
        "Up next: $title · in $days day${if (days == 1) "" else "s"}",
        "اگلا: $title · $days دن میں",
        "التالي: $title · بعد $days يوماً"
    )
    fun upNextToday(title: String, l: CommentaryLanguage) =
        pick(l, "Up next: $title · today", "اگلا: $title · آج", "التالي: $title · اليوم")
    fun isOpenNow(title: String, l: CommentaryLanguage) =
        pick(l, "$title is open now", "$title اب کھلا ہے", "$title مفتوحة الآن")
    fun begins(date: String, l: CommentaryLanguage) =
        pick(l, "Begins $date", "$date کو شروع", "تبدأ في $date")
    fun returns(date: String, l: CommentaryLanguage) =
        pick(l, "Returns $date", "$date کو واپسی", "تعود في $date")
    fun firstFatimiyya(date: String, l: CommentaryLanguage) =
        pick(l, "First Fatimiyya · $date", "پہلی فاطمیہ · $date", "الفاطمية الأولى · $date")
    fun secondFatimiyya(date: String, l: CommentaryLanguage) =
        pick(l, "Second Fatimiyya · $date", "دوسری فاطمیہ · $date", "الفاطمية الثانية · $date")

    // MARK: - Journey identity (by descriptor id) - used in hub + journey headers
    fun title(id: String, l: CommentaryLanguage): String = when (id) {
        "ramadan" -> pick(l, "Ramadan", "رمضان", "رمضان")
        "hajj" -> pick(l, "Dhul-Hijjah", "ذی الحجہ", "ذو الحجة")
        "muharram" -> pick(l, "Muharram", "محرم", "المحرّم")
        "fatimiyya" -> pick(l, "Fatimiyya", "ایامِ فاطمیہ", "الفاطمية")
        "arbaeen" -> pick(l, "Arbaeen", "اربعین", "الأربعين")
        else -> id.replaceFirstChar { it.uppercase() }
    }

    fun eyebrow(id: String, english: String, l: CommentaryLanguage): String = when (id) {
        "ramadan" -> pick(l, english, "30 روزہ سفر", "رحلة 30 يوماً")
        "hajj" -> pick(l, english, "10 روزہ سفر", "رحلة 10 أيام")
        "muharram" -> pick(l, english, "10 روزہ سفر", "رحلة 10 أيام")
        "fatimiyya" -> pick(l, english, "عزائے زہراؑ", "عزاء الزهراء (ع)")
        "arbaeen" -> pick(l, english, "40 روزہ سفر", "رحلة 40 يوماً")
        else -> english
    }

    /** Short evocative tagline for a seasonal journey - hub shelf card description. */
    fun seasonTagline(id: String, l: CommentaryLanguage): String = when (id) {
        "ramadan" -> pick(l, "Thirty nights of nearness", "قربِ الٰہی کی تیس راتیں", "ثلاثون ليلةً من القُرب")
        "hajj" -> pick(l, "The best ten days", "سال کے بہترین دس دن", "أفضلُ عشرةِ أيّام")
        "muharram" -> pick(l, "The stand at Karbala", "کربلا کا قیام", "وقفةُ كربلاء")
        "arbaeen" -> pick(l, "The road to Arbaeen", "اربعین کی راہ", "الطريق إلى الأربعين")
        "fatimiyya" -> pick(l, "Mourning of az-Zahra (AS)", "عزائے زہراؑ", "عزاء الزهراء (ع)")
        else -> ""
    }

    /** Legacy in-screen header title, e.g. "Muharram Journey". */
    fun screenTitle(id: String, l: CommentaryLanguage): String {
        val name = title(id, l)
        return when (l) {
            CommentaryLanguage.URDU -> "$name کا سفر"
            CommentaryLanguage.ARABIC -> "رحلة $name"
            else -> "$name Journey"
        }
    }

    // MARK: - Day list / progress
    fun daysObserved(done: Int, total: Int, l: CommentaryLanguage) = pick(
        l,
        "$done of $total days observed",
        "$total میں سے $done دن منائے گئے",
        "أُحيِيَ $done من $total يوماً"
    )
    fun stationsObserved(done: Int, total: Int, l: CommentaryLanguage) = pick(
        l,
        "$done of $total stations observed",
        "$total میں سے $done منزلیں منائی گئیں",
        "أُحيِيَت $done من $total محطة"
    )
    fun daysCompleted(done: Int, total: Int, l: CommentaryLanguage) = pick(
        l,
        "$done of $total days completed",
        "$total میں سے $done دن مکمل",
        "اكتمل $done من $total يوماً"
    )
    fun dayN(n: Int, l: CommentaryLanguage) = pick(l, "Day $n", "دن $n", "اليوم $n")
    fun stationN(n: Int, l: CommentaryLanguage) = pick(l, "Station $n", "منزل $n", "المحطة $n")
    fun today(l: CommentaryLanguage) = pick(l, "TODAY", "آج", "اليوم")
    fun loadingJourney(l: CommentaryLanguage) =
        pick(l, "Loading journey...", "سفر لوڈ ہو رہا ہے…", "جارٍ تحميل الرحلة…")
    fun errorLoadingJourney(l: CommentaryLanguage) =
        pick(l, "Error Loading Journey", "سفر لوڈ کرنے میں خرابی", "خطأ في تحميل الرحلة")

    // MARK: - Day detail section labels & buttons
    fun todaysVerses(l: CommentaryLanguage) = pick(l, "Today's Verses", "آج کی آیات", "آيات اليوم")
    fun tafsirFocus(l: CommentaryLanguage) = pick(l, "Tafsir Focus", "تفسیری نکتہ", "محور التفسير")
    fun reflection(l: CommentaryLanguage) = pick(l, "Reflection", "غور و فکر", "تأمّل")
    fun duaZiyarat(l: CommentaryLanguage) = pick(l, "Dua / Ziyarat", "دعا / زیارت", "دعاء / زيارة")
    fun fullTafsir(l: CommentaryLanguage) = pick(l, "Full Tafsir", "مکمل تفسیر", "التفسير الكامل")
    fun readFullZiyarat(l: CommentaryLanguage) =
        pick(l, "Read the full ziyarat", "مکمل زیارت پڑھیں", "اقرأ الزيارة كاملة")
    fun fullZiyaratTitle(l: CommentaryLanguage) =
        pick(l, "Ziyarat of Arbaeen", "زیارتِ اربعین", "زيارة الأربعين")
    fun done(l: CommentaryLanguage) = pick(l, "Done", "مکمل", "تمّ")
    fun ashura(l: CommentaryLanguage) = pick(l, "Ashura", "عاشورا", "عاشوراء")

    // Toggle button - mourning journeys ("observed") vs others ("completed")
    fun observed(l: CommentaryLanguage) = pick(l, "Observed", "منایا گیا", "أُحيِيَ")
    fun markObserved(l: CommentaryLanguage) = pick(l, "Mark as observed", "اس دن کو منائیں", "أحيِ هذا اليوم")
    fun completed(l: CommentaryLanguage) = pick(l, "Completed", "مکمل", "مكتمل")
    fun markComplete(l: CommentaryLanguage) = pick(l, "Mark as complete", "مکمل کریں", "سجّله مكتملاً")

    // MARK: - Veiled locked-day preview (iOS VeiledDayPreview)
    fun waitsInside(l: CommentaryLanguage) =
        pick(l, "What waits inside", "اندر کیا منتظر ہے", "ما الذي ينتظرك في الداخل")
    fun aDuaForThisDay(l: CommentaryLanguage) =
        pick(l, "A dua for this day", "اس دن کی دعا", "دعاء هذا اليوم")
    fun versesWithReflections(n: Int, l: CommentaryLanguage) = pick(
        l,
        "$n verse${if (n == 1) "" else "s"} with reflections",
        "$n آیات مع تدبر",
        "$n آيات مع تأملات"
    )
    fun aGuidedReflection(l: CommentaryLanguage) =
        pick(l, "A guided reflection", "رہنما غور و فکر", "تأمل موجه")
    fun premiumDayNote(l: CommentaryLanguage) = pick(
        l,
        "This day is part of the premium journey. One payment unlocks every day, forever.",
        "یہ دن پریمیئم سفر کا حصہ ہے۔ ایک ادائیگی سے ہر دن ہمیشہ کے لیے کھل جاتا ہے۔",
        "هذا اليوم جزء من الرحلة المميزة. دفعة واحدة تفتح كل الأيام، للأبد."
    )
    fun premiumDescentNote(l: CommentaryLanguage) = pick(
        l,
        "The descent continues with Premium. One payment unlocks everything, forever.",
        "یہ سفر پریمیئم کے ساتھ جاری رہتا ہے۔ ایک ادائیگی سے سب کچھ ہمیشہ کے لیے کھل جاتا ہے۔",
        "يستمر النزول مع بريميوم. دفعة واحدة تفتح كل شيء، للأبد."
    )
    fun unlockPremium(l: CommentaryLanguage) =
        pick(l, "Unlock Premium", "پریمیئم کھولیں", "افتح بريميوم")

    // Journey-complete notes (header completion badge, Ramadan/Hajj only)
    fun journeyCompleteNote(id: String, l: CommentaryLanguage): String? = when (id) {
        "ramadan" -> pick(
            l,
            "Journey complete · Ramadan Champion earned",
            "سفر مکمل · Ramadan Champion حاصل ہوا",
            "Journey complete · Ramadan Champion earned"
        )
        "hajj" -> pick(
            l,
            "Journey complete · Hajj Champion earned",
            "سفر مکمل · Hajj Champion حاصل کر لیا",
            "Journey complete · Hajj Champion earned"
        )
        else -> null
    }
}
