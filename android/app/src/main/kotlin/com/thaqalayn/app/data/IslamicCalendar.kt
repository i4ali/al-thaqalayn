package com.thaqalayn.app.data

import android.icu.util.Calendar as IcuCalendar
import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Hijri date helpers for the Today header pill. Uses the Umm al-Qura calendar,
 * matching iOS (Calendar(identifier: .islamicUmmAlQura)). English month names
 * like the rest of the app.
 */
object IslamicCalendarManager {

    private val monthNames = listOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhul-Qa'dah", "Dhul-Hijjah"
    )

    private fun calendar(): IslamicCalendar {
        val cal = IslamicCalendar(ULocale("@calendar=islamic-umalqura"))
        cal.calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        cal.time = java.util.Date()
        return cal
    }

    /** Hijri (1-based month, day) of any instant in the device timezone. */
    fun hijriMonthDay(date: Date): Pair<Int, Int> {
        val cal = calendar()
        cal.time = date
        return (cal.get(IcuCalendar.MONTH) + 1) to cal.get(IcuCalendar.DAY_OF_MONTH)
    }

    fun currentIslamicDay(): Int = calendar().get(IcuCalendar.DAY_OF_MONTH)

    /** 1-based month number. */
    fun currentIslamicMonth(): Int = calendar().get(IcuCalendar.MONTH) + 1

    fun monthName(monthNumber: Int): String =
        monthNames.getOrElse(monthNumber - 1) { "" }

    fun islamicDayOfWeek(): String {
        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        return days.getOrElse(index) { "" }
    }

    /** "12 MUHARRAM · FRI" - the Today header pill label. */
    fun pillLabel(): String {
        val day = currentIslamicDay()
        val month = monthName(currentIslamicMonth()).uppercase()
        val weekday = islamicDayOfWeek().take(3).uppercase()
        return "$day $month · $weekday"
    }

    // MARK: - Season detection (iOS IslamicCalendarManager parity)
    // Ramadan touches months 8/9/10, Hajj 11/12 (day <= 15), Muharram 12 (day >= 25)/1 -
    // the three windows are mutually exclusive by construction.

    /** Last 5 days of Sha'ban, all of Ramadan, first 5 days of Shawwal. */
    fun isRamadanSeason(): Boolean {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            8 -> day >= 25
            9 -> true
            10 -> day <= 5
            else -> false
        }
    }

    /** Last 5 days of Dhul-Qa'dah lead-in + Dhul-Hijjah days 1-15. */
    fun isHajjSeason(): Boolean {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            11 -> day >= 25
            12 -> day <= 15
            else -> false
        }
    }

    /** Late Dhul-Hijjah lead-in (day >= 25) + Muharram days 1-15. */
    fun isMuharramSeason(): Boolean {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            12 -> day >= 25
            1 -> day <= 15
            else -> false
        }
    }

    /**
     * Ayyam-e-Fatimiyya mourning windows (one journey, two narrated dates):
     * First Fatimiyya Jumada al-Awwal 8-18, Second Fatimiyya Jumada al-Thani 1-8.
     */
    fun isFatimiyyaSeason(): Boolean {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            5 -> day in 8..18
            6 -> day in 1..8
            else -> false
        }
    }

    /**
     * The Arbaeen "Return" window - the 40 days from Ashura to Arbaeen:
     * Muharram 11-30 + Safar 1-25 (culminating on the 20th, + a grace tail).
     */
    fun isArbaeenSeason(): Boolean {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            1 -> day >= 11
            2 -> day <= 25
            else -> false
        }
    }

    // MARK: - Current journey day (iOS current*Day parity)

    fun currentIslamicYear(): Int = calendar().get(IcuCalendar.YEAR)

    /** Islamic (Umm al-Qura) year of an arbitrary instant. */
    fun islamicYearOf(millis: Long): Int {
        val cal = IslamicCalendar(ULocale("@calendar=islamic-umalqura"))
        cal.calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        cal.timeInMillis = millis
        return cal.get(IcuCalendar.YEAR)
    }

    /** Islamic (month 1-12, day) of an arbitrary instant - daily-verse selection for future days. */
    fun islamicMonthDayOf(millis: Long): Pair<Int, Int> {
        val cal = IslamicCalendar(ULocale("@calendar=islamic-umalqura"))
        cal.calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        cal.timeInMillis = millis
        return (cal.get(IcuCalendar.MONTH) + 1) to cal.get(IcuCalendar.DAY_OF_MONTH)
    }

    /** Current Hijri (year, month 1-12, day) - iOS currentIslamicDate(). */
    fun currentIslamicDate(): Triple<Int, Int, Int> {
        val cal = calendar()
        return Triple(
            cal.get(IcuCalendar.YEAR),
            cal.get(IcuCalendar.MONTH) + 1,
            cal.get(IcuCalendar.DAY_OF_MONTH)
        )
    }

    /** Current day of Ramadan (1-30), null outside Ramadan. */
    fun currentRamadanDay(): Int? =
        if (currentIslamicMonth() == 9) currentIslamicDay() else null

    /** Current day of the Dhul-Hijjah journey (1-10), null outside it. */
    fun currentHajjDay(): Int? {
        if (currentIslamicMonth() != 12) return null
        return currentIslamicDay().takeIf { it in 1..10 }
    }

    /** Current Muharram Journey day (1-10), null during lead-in and the 11-15 grace. */
    fun currentMuharramDay(): Int? {
        if (currentIslamicMonth() != 1) return null
        return currentIslamicDay().takeIf { it in 1..10 }
    }

    /** Current Arbaeen station (1-8), mapping the ~40-day span onto the 8 stations. */
    fun currentArbaeenStation(): Int? {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when {
            month == 1 && day >= 11 -> when (day) {
                in 11..13 -> 1     // The Morning After
                in 14..17 -> 2     // The Road to Kufa
                else -> 3          // Kufa: Zaynab's sermon (18-30)
            }
            month == 2 -> when (day) {
                1 -> 4             // The Long Road to Sham
                in 2..8 -> 5       // The Court of Yazid
                in 9..15 -> 6      // The Ruin of Damascus
                in 16..19 -> 7     // The Turn Homeward
                else -> 8          // Arbaeen: Jabir at the Grave (20+)
            }
            else -> null
        }
    }

    private fun daysUntilRamadan(): Int? {
        if (currentIslamicMonth() != 8) return null
        return maxOf(0, 30 - currentIslamicDay() + 1)
    }

    private fun daysUntilHajj(): Int? {
        if (currentIslamicMonth() != 11) return null
        return maxOf(0, 30 - currentIslamicDay() + 1)
    }

    private fun daysUntilMuharram(): Int? {
        if (currentIslamicMonth() != 12) return null
        return maxOf(0, 30 - currentIslamicDay() + 1)
    }

    // MARK: - Season status lines (iOS *SeasonStatus parity)

    fun ramadanSeasonStatus(): String {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            8 -> {
                val daysUntil = daysUntilRamadan()
                if (daysUntil != null && daysUntil > 0) {
                    "$daysUntil day${if (daysUntil == 1) "" else "s"} until Ramadan"
                } else {
                    "Ramadan begins soon"
                }
            }
            9 -> "Day $day of Ramadan"
            10 -> if (day <= 5) "Eid Mubarak!" else ""
            else -> ""
        }
    }

    fun hajjSeasonStatus(): String {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            11 -> {
                val daysUntil = daysUntilHajj()
                if (daysUntil != null && daysUntil > 0) {
                    "$daysUntil day${if (daysUntil == 1) "" else "s"} until Dhul-Hijjah"
                } else {
                    "Dhul-Hijjah begins soon"
                }
            }
            12 -> when {
                day == 9 -> "Day of Arafah"
                day == 10 -> "Eid al-Adha Mubarak!"
                day <= 10 -> "Day $day of Dhul-Hijjah"
                day <= 15 -> "Eid al-Adha Mubarak!"
                else -> ""
            }
            else -> ""
        }
    }

    fun muharramSeasonStatus(): String {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when (month) {
            12 -> {
                val daysUntil = daysUntilMuharram()
                if (daysUntil != null && daysUntil > 0) {
                    "$daysUntil day${if (daysUntil == 1) "" else "s"} until Muharram"
                } else {
                    "Muharram begins soon"
                }
            }
            1 -> when {
                day == 10 -> "Ashura - Ya Husayn (AS)"
                day <= 10 -> "Day $day of Muharram"
                day <= 15 -> "The mourning continues - Ya Husayn (AS)"
                else -> ""
            }
            else -> ""
        }
    }

    fun fatimiyyaSeasonStatus(): String {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when {
            month == 5 && day in 8..18 ->
                "First Fatimiyya - Ya Zahra (AS)"
            month == 6 && day in 1..8 ->
                "Second Fatimiyya - Ya Zahra (AS)"
            else -> ""
        }
    }

    fun arbaeenSeasonStatus(): String {
        val month = currentIslamicMonth()
        val day = currentIslamicDay()
        return when {
            month == 1 && day >= 11 ->
                "The Return - the road to Arbaeen"
            month == 2 && day < 20 -> {
                val left = 20 - day
                "$left day${if (left == 1) "" else "s"} until Arbaeen"
            }
            month == 2 && day == 20 ->
                "Arbaeen - Ya Husayn (AS)"
            month == 2 && day <= 25 ->
                "Ziyarat of Arbaeen - Ya Husayn (AS)"
            else -> ""
        }
    }

    // MARK: - Hijri date math (Journey hub coming-soon/ended computation)

    /** Millis (local midnight-ish instant) of a Hijri calendar date. */
    fun hijriDateMillis(year: Int, month: Int, day: Int): Long {
        val cal = IslamicCalendar(ULocale("@calendar=islamic-umalqura"))
        cal.calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
        cal.clear()
        cal.set(IcuCalendar.YEAR, year)
        cal.set(IcuCalendar.MONTH, month - 1)
        cal.set(IcuCalendar.DAY_OF_MONTH, day)
        return cal.timeInMillis
    }

    /** Whole calendar days from now until [thenMillis], clamped to >= 0. */
    fun daysFromNow(thenMillis: Long): Int {
        val days = TimeUnit.MILLISECONDS.toDays(startOfDay(thenMillis)) -
            TimeUnit.MILLISECONDS.toDays(startOfDay(System.currentTimeMillis()))
        return maxOf(0, days.toInt())
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Medium-style Gregorian date label, in English like the rest of the app (iOS `medium(_:)`). */
    fun mediumDateLabel(millis: Long): String =
        DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(Date(millis))
}
