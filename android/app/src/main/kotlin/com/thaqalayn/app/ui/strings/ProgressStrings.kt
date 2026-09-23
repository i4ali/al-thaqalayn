package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.BadgeAward
import com.thaqalayn.app.model.BadgeType

/** Chrome copy for the Progress tab (iOS ProgressTabStrings). English only. */
object ProgressStrings {
    val yourJourneyEyebrow: String = "Your Journey"

    val progressTitle: String = "Progress"

    val progressSubtitle: String = "A record of your time with the Qur'an"

    val versesRead: String = "Verses Read"

    val surahsComplete: String = "Surahs Complete"

    val passagesRead: String = "Passages Read"

    val totalSawab: String = "Total Sawab"

    fun ofTotal(n: Int): String = "of $n"

    val blessingsEarned: String = "blessings earned"

    fun dayStreak(n: Int): String = "$n Day Streak"

    val keepItGoing: String = "Keep it going!"

    val best: String = "Best"

    fun badgesDivider(count: Int, total: Int): String = "Badges · $count of $total"

    val noBadgesYet: String = "No badges yet"

    val earnBadgesHint: String = "Complete surahs and build streaks to earn badges."

    /** Badge tile label: the surah name for a surah-completion badge, else the badge title. */
    fun badgeLabel(badge: BadgeAward): String =
        if (badge.badgeType == BadgeType.SURAH_COMPLETION) badge.surahName else badge.badgeType.title

    val quran: String = "Quran"

    val surahs: String = "Surahs"

    /** The seasonal ring label ("Ramadan" / "Hajj" / "Muharram"), shown as-is. */
    fun seasonal(raw: String): String = raw
}
