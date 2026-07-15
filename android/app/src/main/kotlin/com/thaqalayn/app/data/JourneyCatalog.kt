package com.thaqalayn.app.data

import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.strings.JourneyStrings

/**
 * Static registry of the seasonal Journeys shown in the Journey hub, plus the
 * per-journey status (active / coming-soon / ended) computed by bucketing the
 * current Hijri date against each journey's content-start month. Exact port of
 * iOS JourneyCatalog.swift.
 */
sealed class JourneyStatus {
    /** In season - openable. [line] is the existing season-status string. */
    data class Active(val line: String) : JourneyStatus()

    /** Season is still ahead this Hijri year - locked, with a countdown. */
    data class ComingSoon(val daysUntil: Int, val startsLabel: String) : JourneyStatus()

    /** Season already passed this Hijri year - locked, counts to next year's return. */
    data class Ended(val daysUntil: Int, val returnsLabel: String) : JourneyStatus()

    val isActive: Boolean get() = this is Active

    /** Days until this journey (next) opens; 0 when active. */
    val opensIn: Int
        get() = when (this) {
            is Active -> 0
            is ComingSoon -> daysUntil
            is Ended -> daysUntil
        }
}

/** One journey in the hub. Static registry - see [JourneyDescriptor.all]. */
data class JourneyDescriptor(
    /** Stable id - matches JourneyManagers and the route argument. */
    val id: String,
    /** English eyebrow, e.g. "30-Day Journey" (JourneyStrings.eyebrow localizes). */
    val eyebrow: String,
    /** Hijri month the journey's content begins (Ramadan=9, Dhul-Hijjah=12, Muharram=1). */
    val contentStartMonth: Int,
    val isActive: () -> Boolean,
    /** The existing season-status string for the active card line. */
    val statusLine: () -> String,
    /**
     * Optional custom status for journeys whose schedule isn't a single content
     * month (Fatimiyya's two windows, Arbaeen's mid-Muharram start).
     */
    val statusOverride: (() -> JourneyStatus)? = null
) {
    /**
     * Status for the current Hijri date. Bucketing: active -> in season;
     * else if this Hijri year's content-start is still ahead -> coming soon;
     * else -> ended (counts to next year's start).
     */
    fun status(): JourneyStatus {
        statusOverride?.let { return it() }
        if (isActive()) return JourneyStatus.Active(statusLine())

        val cal = IslamicCalendarManager
        val lang = CommentaryLanguageManager.selectedLanguage
        val year = cal.currentIslamicYear()
        val thisYearStart = cal.hijriDateMillis(year, contentStartMonth, 1)
        val now = System.currentTimeMillis()
        if (now < thisYearStart) {
            return JourneyStatus.ComingSoon(
                daysUntil = cal.daysFromNow(thisYearStart),
                startsLabel = JourneyStrings.begins(cal.mediumDateLabel(thisYearStart), lang)
            )
        }
        val nextYearStart = cal.hijriDateMillis(year + 1, contentStartMonth, 1)
        return JourneyStatus.Ended(
            daysUntil = cal.daysFromNow(nextYearStart),
            returnsLabel = JourneyStrings.returns(cal.mediumDateLabel(nextYearStart), lang)
        )
    }

    companion object {
        val all: List<JourneyDescriptor> = listOf(
            JourneyDescriptor(
                id = "ramadan", eyebrow = "30-Day Journey", contentStartMonth = 9,
                isActive = { IslamicCalendarManager.isRamadanSeason() },
                statusLine = { IslamicCalendarManager.ramadanSeasonStatus() }
            ),
            JourneyDescriptor(
                id = "hajj", eyebrow = "10-Day Journey", contentStartMonth = 12,
                isActive = { IslamicCalendarManager.isHajjSeason() },
                statusLine = { IslamicCalendarManager.hajjSeasonStatus() }
            ),
            JourneyDescriptor(
                id = "muharram", eyebrow = "10-Day Journey", contentStartMonth = 1,
                isActive = { IslamicCalendarManager.isMuharramSeason() },
                statusLine = { IslamicCalendarManager.muharramSeasonStatus() }
            ),
            JourneyDescriptor(
                id = "fatimiyya", eyebrow = "Mourning of az-Zahra (AS)", contentStartMonth = 5,
                isActive = { IslamicCalendarManager.isFatimiyyaSeason() },
                statusLine = { IslamicCalendarManager.fatimiyyaSeasonStatus() },
                statusOverride = {
                    val cal = IslamicCalendarManager
                    val lang = CommentaryLanguageManager.selectedLanguage
                    if (cal.isFatimiyyaSeason()) {
                        JourneyStatus.Active(cal.fatimiyyaSeasonStatus())
                    } else {
                        val year = cal.currentIslamicYear()
                        val firstStart = cal.hijriDateMillis(year, 5, 8)
                        val secondStart = cal.hijriDateMillis(year, 6, 1)
                        val now = System.currentTimeMillis()
                        when {
                            now < firstStart -> JourneyStatus.ComingSoon(
                                cal.daysFromNow(firstStart),
                                JourneyStrings.firstFatimiyya(cal.mediumDateLabel(firstStart), lang)
                            )
                            now < secondStart -> JourneyStatus.ComingSoon(
                                cal.daysFromNow(secondStart),
                                JourneyStrings.secondFatimiyya(cal.mediumDateLabel(secondStart), lang)
                            )
                            else -> {
                                val nextReturn = cal.hijriDateMillis(year + 1, 5, 8)
                                JourneyStatus.Ended(
                                    cal.daysFromNow(nextReturn),
                                    JourneyStrings.returns(cal.mediumDateLabel(nextReturn), lang)
                                )
                            }
                        }
                    }
                }
            ),
            JourneyDescriptor(
                id = "arbaeen", eyebrow = "40-Day Journey", contentStartMonth = 2,
                isActive = { IslamicCalendarManager.isArbaeenSeason() },
                statusLine = { IslamicCalendarManager.arbaeenSeasonStatus() },
                statusOverride = {
                    val cal = IslamicCalendarManager
                    val lang = CommentaryLanguageManager.selectedLanguage
                    if (cal.isArbaeenSeason()) {
                        JourneyStatus.Active(cal.arbaeenSeasonStatus())
                    } else {
                        // Window opens 11 Muharram (the day after Ashura) of the current Hijri year.
                        val year = cal.currentIslamicYear()
                        val windowStart = cal.hijriDateMillis(year, 1, 11)
                        val now = System.currentTimeMillis()
                        if (now < windowStart) {
                            JourneyStatus.ComingSoon(
                                cal.daysFromNow(windowStart),
                                JourneyStrings.begins(cal.mediumDateLabel(windowStart), lang)
                            )
                        } else {
                            val nextStart = cal.hijriDateMillis(year + 1, 1, 11)
                            JourneyStatus.Ended(
                                cal.daysFromNow(nextStart),
                                JourneyStrings.returns(cal.mediumDateLabel(nextStart), lang)
                            )
                        }
                    }
                }
            )
        )

        fun byId(id: String): JourneyDescriptor? = all.firstOrNull { it.id == id }

        /** Sort bucket: active -> coming soon (soonest) -> ended (soonest to return). */
        private fun sortKey(s: JourneyStatus): Pair<Int, Int> = when (s) {
            is JourneyStatus.Active -> 0 to 0
            is JourneyStatus.ComingSoon -> 1 to s.daysUntil
            is JourneyStatus.Ended -> 2 to s.daysUntil
        }

        /** Every journey paired with its current status, in hub display order. */
        fun orderedByStatus(): List<Pair<JourneyDescriptor, JourneyStatus>> =
            all.map { it to it.status() }
                .sortedWith(compareBy({ sortKey(it.second).first }, { sortKey(it.second).second }))
    }
}
