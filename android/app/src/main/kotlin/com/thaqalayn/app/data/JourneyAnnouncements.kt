package com.thaqalayn.app.data

import java.util.Calendar

/**
 * Data-driven table + a pure, side-effect-free scheduling decision for
 * "Journey is open" notifications - exact port of iOS JourneyAnnouncements.swift.
 * The decision function takes an injected "now" and Hijri date so it is
 * verifiable without a test harness.
 */
class JourneyAnnouncement(
    /** Stable id - also the deep-link id and the notification identifier suffix. */
    val id: String,
    /** Notification title. */
    val title: String,
    /** Notification body. */
    val body: String,
    /** Hijri month the tab appears (lead-in start). 8=Sha'ban, 11=Dhul-Qa'dah, 12=Dhul-Hijjah. */
    val leadInHijriMonth: Int,
    /** Hijri day the tab appears. */
    val leadInHijriDay: Int,
    /**
     * True when the lead-in falls in the Hijri year *before* the content month
     * (Muharram: lead-in 25 Dhul-Hijjah of year C-1, content Muharram of year C).
     */
    val leadInIsPreviousHijriYear: Boolean,
    /**
     * Whether the given Islamic (month, day) is inside this journey's announce
     * window - narrower than the season window: it excludes the post-content
     * grace tail so a late catch-up never fires on Eid etc.
     */
    val isWithinAnnounceWindow: (islamicMonth: Int, islamicDay: Int) -> Boolean
) {
    /**
     * The Hijri year of this journey's *content* month for the cycle "now"
     * belongs to (the dedup key).
     */
    fun cycleYear(currentIslamicYear: Int, currentIslamicMonth: Int): Int {
        if (!leadInIsPreviousHijriYear) {
            return currentIslamicYear // Ramadan / Hajj: lead-in & content share the year.
        }
        // Muharram: content year == current year only when we are already in
        // Muharram (month 1); otherwise the next Muharram is next Hijri year.
        return currentIslamicYear + if (currentIslamicMonth == 1) 0 else 1
    }

    /** The Hijri year the lead-in date itself falls in, for a given cycle year. */
    fun leadInHijriYear(forCycleYear: Int): Int =
        if (leadInIsPreviousHijriYear) forCycleYear - 1 else forCycleYear

    companion object {
        /**
         * The canonical journeys. Adding a future journey = append one row here;
         * the scheduler is unchanged.
         */
        val all: List<JourneyAnnouncement> = listOf(
            JourneyAnnouncement(
                id = "ramadan",
                title = "🌙 The Ramadan Journey is open",
                body = "The blessed month draws near. Step into your Ramadan Journey through the Quran. Tap to begin.",
                leadInHijriMonth = 8, leadInHijriDay = 25,
                leadInIsPreviousHijriYear = false,
                isWithinAnnounceWindow = { month, day ->
                    (month == 8 && day >= 25) || month == 9 // Sha'ban 25-30 or all Ramadan; NOT Shawwal.
                }
            ),
            JourneyAnnouncement(
                id = "hajj",
                title = "🕋 The Dhul-Hijjah Journey is open",
                body = "The sacred days of Hajj approach. Begin your 10-day Dhul-Hijjah Journey. Tap to enter.",
                leadInHijriMonth = 11, leadInHijriDay = 25,
                leadInIsPreviousHijriYear = false,
                isWithinAnnounceWindow = { month, day ->
                    (month == 11 && day >= 25) || (month == 12 && day <= 10) // NOT the 11-15 tail.
                }
            ),
            JourneyAnnouncement(
                id = "muharram",
                title = "The Muharram Journey is open",
                body = "The month of Imam al-Husayn (AS) approaches. Walk the first ten days of Muharram in remembrance. Tap to begin.",
                leadInHijriMonth = 12, leadInHijriDay = 25,
                leadInIsPreviousHijriYear = true,
                isWithinAnnounceWindow = { month, day ->
                    (month == 12 && day >= 25) || (month == 1 && day <= 10) // NOT the 11-15 grace.
                }
            ),
            JourneyAnnouncement(
                id = "fatimiyya",
                title = "The Fatimiyya mourning has begun",
                body = "The days of az-Zahrā (AS). Walk the Ayyam-e-Fatimiyya through the Quran. Tap to begin.",
                leadInHijriMonth = 5, leadInHijriDay = 8,
                leadInIsPreviousHijriYear = false,
                isWithinAnnounceWindow = { month, day ->
                    month == 5 && day in 8..15
                }
            ),
            JourneyAnnouncement(
                id = "arbaeen",
                title = "The Arbaeen Journey is open",
                body = "Ashura has passed; the caravan sets out. Walk the forty-day road of the return with the family of al-Husayn (AS). Tap to begin.",
                leadInHijriMonth = 1, leadInHijriDay = 11,
                leadInIsPreviousHijriYear = false,
                isWithinAnnounceWindow = { month, day ->
                    // From 11 Muharram (the day after Ashura) through Arbaeen (20 Safar);
                    // matches the openable window's start, minus the 21-25 grace tail.
                    (month == 1 && day >= 11) || (month == 2 && day <= 20)
                }
            )
        )
    }
}

/** Outcome of the pure scheduling decision. */
data class JourneyScheduleDecision(
    /** If non-null, ensure an idempotent calendar notification exists at this instant. */
    val calendarFireMillis: Long?,
    /** If true, fire the ~5s catch-up now. */
    val fireCatchUpNow: Boolean,
    /** If non-null, persist handledYears[id] = this value. */
    val markHandledCycleYear: Int?
) {
    companion object {
        val noop = JourneyScheduleDecision(
            calendarFireMillis = null, fireCatchUpNow = false, markHandledCycleYear = null
        )
    }
}

/**
 * Pure, side-effect-free scheduling decision. The only calendar use is the
 * deterministic Hijri→Gregorian conversion via IslamicCalendarManager.
 */
fun journeyScheduleDecision(
    journey: JourneyAnnouncement,
    nowMillis: Long,
    islamicYear: Int,
    islamicMonth: Int,
    islamicDay: Int,
    preferredHour: Int,
    preferredMinute: Int,
    handledCycleYear: Int?
): JourneyScheduleDecision {
    val cycleYear = journey.cycleYear(
        currentIslamicYear = islamicYear,
        currentIslamicMonth = islamicMonth
    )
    val leadInHYear = journey.leadInHijriYear(cycleYear)

    val leadInDayMillis = IslamicCalendarManager.hijriDateMillis(
        leadInHYear, journey.leadInHijriMonth, journey.leadInHijriDay
    )

    // Fire on the lead-in day at the user's preferred notification time.
    val cal = Calendar.getInstance()
    cal.timeInMillis = leadInDayMillis
    cal.set(Calendar.HOUR_OF_DAY, preferredHour)
    cal.set(Calendar.MINUTE, preferredMinute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val fireMillis = cal.timeInMillis

    if (fireMillis > nowMillis) {
        // (Re)materialize the calendar notification - idempotent, wipe-recoverable.
        // Mark handled only the first time we commit this cycle.
        val mark = if (handledCycleYear == cycleYear) null else cycleYear
        return JourneyScheduleDecision(
            calendarFireMillis = fireMillis,
            fireCatchUpNow = false,
            markHandledCycleYear = mark
        )
    }

    // Lead-in instant has passed.
    if (journey.isWithinAnnounceWindow(islamicMonth, islamicDay) && handledCycleYear != cycleYear) {
        return JourneyScheduleDecision(
            calendarFireMillis = null,
            fireCatchUpNow = true,
            markHandledCycleYear = cycleYear
        )
    }
    return JourneyScheduleDecision.noop
}
