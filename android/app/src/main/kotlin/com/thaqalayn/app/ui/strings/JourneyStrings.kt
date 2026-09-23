package com.thaqalayn.app.ui.strings

/**
 * Chrome copy for the Journey tab - hub, the seasonal journeys (Ramadan,
 * Dhul-Hijjah/Hajj, Muharram, Fatimiyya, Arbaeen), their day lists and
 * day-detail screens (iOS JourneyStrings). English only, plain spelling.
 */
object JourneyStrings {

    // MARK: - Hub
    val sacredSeasons = "Sacred Seasons"
    val journeys = "Journeys"
    val grow = "Grow"
    val comingSoon = "Coming soon"
    val premium = "Premium"

    // MARK: - Deep Dives + Inside the Surah (hub shelves, card chrome, toggle)
    val deepDives = "Deep Dives"
    fun deepDiveOnItsWay(title: String) =
        "$title is on its way."
    val deepDiveEyebrow = "Deep Dive"
    val soon = "SOON"
    val insideTheSurah = "Inside the Surah"
    val surahJourneyEyebrow = "Surah Journey"
    val readTheFullSurah = "Read the full surah"
    val readAndTafsir = "Read & Tafsir"
    val journey = "Journey"

    // MARK: - Shelf status eyebrows (compact hub cards)
    val live = "LIVE"
    val ready = "READY"
    fun inDaysShort(days: Int) = "IN $days DAY${if (days == 1) "" else "S"}"
    val endedShort = "ENDED"

    /** "See all" link on a shelf header - count is that section's live total. */
    fun allCount(n: Int) = "All $n"

    val nextUp = "NEXT UP"
    fun comingSoonInDays(days: Int) = "Coming soon · in $days day${if (days == 1) "" else "s"}"
    fun endedReturns(returnsLabel: String) =
        "Ended · $returnsLabel"
    val gotIt = "Got it"

    // Locked-journey alert
    fun hasEnded(title: String) =
        "$title has ended"
    fun notOpenYet(title: String) =
        "$title isn't open yet"
    fun upNextInDays(title: String, days: Int) = "Up next: $title · in $days day${if (days == 1) "" else "s"}"
    fun upNextToday(title: String) =
        "Up next: $title · today"
    fun isOpenNow(title: String) =
        "$title is open now"
    fun begins(date: String) =
        "Begins $date"
    fun returns(date: String) =
        "Returns $date"
    fun firstFatimiyya(date: String) =
        "First Fatimiyya · $date"
    fun secondFatimiyya(date: String) =
        "Second Fatimiyya · $date"

    // MARK: - Journey identity (by descriptor id) - used in hub + journey headers
    fun title(id: String): String = when (id) {
        "ramadan" -> "Ramadan"
        "hajj" -> "Dhul-Hijjah"
        "muharram" -> "Muharram"
        "fatimiyya" -> "Fatimiyya"
        "arbaeen" -> "Arbaeen"
        else -> id.replaceFirstChar { it.uppercase() }
    }

    /** Short evocative tagline for a seasonal journey - hub shelf card description. */
    fun seasonTagline(id: String): String = when (id) {
        "ramadan" -> "Thirty nights of nearness"
        "hajj" -> "The best ten days"
        "muharram" -> "The stand at Karbala"
        "arbaeen" -> "The road to Arbaeen"
        "fatimiyya" -> "Mourning of az-Zahra (AS)"
        else -> ""
    }

    /** Legacy in-screen header title, e.g. "Muharram Journey". */
    fun screenTitle(id: String) = "${title(id)} Journey"

    // MARK: - Day list / progress
    fun daysObserved(done: Int, total: Int) = "$done of $total days observed"
    fun stationsObserved(done: Int, total: Int) = "$done of $total stations observed"
    fun daysCompleted(done: Int, total: Int) = "$done of $total days completed"
    fun dayN(n: Int) = "Day $n"
    fun stationN(n: Int) = "Station $n"
    val today = "TODAY"
    val loadingJourney =
        "Loading journey..."
    val errorLoadingJourney =
        "Error Loading Journey"

    // MARK: - Day detail section labels & buttons
    val todaysVerses = "Today's Verses"
    val tafsirFocus = "Tafsir Focus"
    val reflection = "Reflection"
    val duaZiyarat = "Dua / Ziyarat"
    val fullTafsir = "Full Tafsir"
    val readFullZiyarat =
        "Read the full ziyarat"
    val fullZiyaratTitle =
        "Ziyarat of Arbaeen"
    val done = "Done"
    val ashura = "Ashura"

    // Toggle button - mourning journeys ("observed") vs others ("completed")
    val observed = "Observed"
    val markObserved = "Mark as observed"
    val completed = "Completed"
    val markComplete = "Mark as complete"

    // MARK: - Veiled locked-day preview (iOS VeiledDayPreview)
    val waitsInside =
        "What waits inside"
    val aDuaForThisDay =
        "A dua for this day"
    fun versesWithReflections(n: Int) = "$n verse${if (n == 1) "" else "s"} with reflections"
    val aGuidedReflection =
        "A guided reflection"
    val premiumDayNote = "This day is part of the premium journey. One payment unlocks every day, forever."
    val premiumDescentNote = "The descent continues with Premium. One payment unlocks everything, forever."
    val unlockPremium =
        "Unlock Premium"

    // Journey-complete notes (header completion badge, Ramadan/Hajj only)
    fun journeyCompleteNote(id: String): String? = when (id) {
        "ramadan" -> "Journey complete · Ramadan Champion earned"
        "hajj" -> "Journey complete · Hajj Champion earned"
        else -> null
    }
}
