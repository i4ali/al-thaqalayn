package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

@Serializable
data class VerseProgress(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val readDate: Long,
    val isRead: Boolean = true
) {
    val verseKey: String get() = "$surahNumber:$verseNumber"
}

@Serializable
data class ReadingStreak(
    var currentStreak: Int = 0,
    var longestStreak: Int = 0,
    var lastReadDate: Long? = null,
    var streakStartDate: Long? = null
)

@Serializable
data class BadgeAward(
    val id: String,
    val surahNumber: Int,
    val surahName: String,
    val arabicName: String,
    val awardedDate: Long,
    val badgeType: BadgeType = BadgeType.SURAH_COMPLETION
)

@Serializable
enum class BadgeType(val key: String) {
    SURAH_COMPLETION("surah_completion"),
    MILESTONE_10("milestone_10"),
    MILESTONE_25("milestone_25"),
    MILESTONE_50("milestone_50"),
    ALL_SURAHS("all_surahs"),
    STREAK_7("streak_7"),
    STREAK_30("streak_30"),
    STREAK_100("streak_100"),
    RAMADAN_COMPLETION("ramadan_completion"),
    HAJJ_COMPLETION("hajj_completion"),
    DAILY_CHALLENGE_FIRST("daily_challenge_first"),
    DAILY_CHALLENGE_STREAK_7("daily_challenge_streak_7"),
    DAILY_CHALLENGE_STREAK_30("daily_challenge_streak_30"),
    DAILY_CHALLENGE_STREAK_100("daily_challenge_streak_100"),
    CROSSWORD_FIRST("crossword_first"),
    CROSSWORD_7("crossword_7"),
    CROSSWORD_30("crossword_30"),
    CROSSWORD_100("crossword_100");

    val title: String
        get() = when (this) {
            SURAH_COMPLETION -> "Khatm Surah"
            MILESTONE_10 -> "Mubtadi"
            MILESTONE_25 -> "Salik"
            MILESTONE_50 -> "Murid"
            ALL_SURAHS -> "Waliy Allah"
            STREAK_7 -> "Mu'min Mutaqin"
            STREAK_30 -> "Sahib al-Wird"
            STREAK_100 -> "Mukhlis"
            RAMADAN_COMPLETION -> "Ramadan Champion"
            HAJJ_COMPLETION -> "Hajj Champion"
            DAILY_CHALLENGE_FIRST -> "First Challenge"
            DAILY_CHALLENGE_STREAK_7 -> "7-Day Challenge Streak"
            DAILY_CHALLENGE_STREAK_30 -> "30-Day Challenge Streak"
            DAILY_CHALLENGE_STREAK_100 -> "100-Day Challenge Streak"
            CROSSWORD_FIRST -> "First Crossword"
            CROSSWORD_7 -> "Crossword Adept"
            CROSSWORD_30 -> "Crossword Devotee"
            CROSSWORD_100 -> "Crossword Master"
        }

    val subtitle: String
        get() = when (this) {
            SURAH_COMPLETION -> "ختم السورة"
            MILESTONE_10 -> "المبتدئ"
            MILESTONE_25 -> "السالك"
            MILESTONE_50 -> "المريد"
            ALL_SURAHS -> "ولي الله"
            STREAK_7 -> "مؤمن متقين"
            STREAK_30 -> "صاحب الورد"
            STREAK_100 -> "المخلص"
            RAMADAN_COMPLETION -> "بطل رمضان"
            HAJJ_COMPLETION -> "بطل الحج"
            DAILY_CHALLENGE_FIRST -> "أول تحدٍّ يومي"
            DAILY_CHALLENGE_STREAK_7 -> "سلسلة التحدي ٧ أيام"
            DAILY_CHALLENGE_STREAK_30 -> "سلسلة التحدي ٣٠ يوماً"
            DAILY_CHALLENGE_STREAK_100 -> "سلسلة التحدي ١٠٠ يوم"
            CROSSWORD_FIRST -> "أول كلمات متقاطعة"
            CROSSWORD_7 -> "سلسلة الكلمات ٧ أيام"
            CROSSWORD_30 -> "سلسلة الكلمات ٣٠ يوماً"
            CROSSWORD_100 -> "سلسلة الكلمات ١٠٠ يوم"
        }

    /** Long-form badge blurb (iOS BadgeType.description) - used in milestone notifications. */
    val description: String
        get() = when (this) {
            SURAH_COMPLETION -> "Completed a surah of the Noble Quran"
            MILESTONE_10 -> "The Beginner - Completed 10 surahs on your journey"
            MILESTONE_25 -> "The Traveler - Completed 25 surahs on the spiritual path"
            MILESTONE_50 -> "The Dedicated Student - Reached the halfway mark with 50 surahs"
            ALL_SURAHS -> "Friend of Allah - Completed all 114 surahs of the Quran"
            STREAK_7 -> "Consistent Believer - Maintained 7 days of steadfast reading"
            STREAK_30 -> "Keeper of Daily Portion - 30 days of unwavering commitment"
            STREAK_100 -> "The Devoted One - 100 days of dedicated spiritual practice"
            RAMADAN_COMPLETION -> "Completed the entire 30-day Ramadan Journey"
            HAJJ_COMPLETION -> "Completed the entire 10-day Dhul-Hijjah Journey"
            DAILY_CHALLENGE_FIRST -> "Completed your first Daily Challenge"
            DAILY_CHALLENGE_STREAK_7 -> "Completed Daily Challenges for 7 days in a row"
            DAILY_CHALLENGE_STREAK_30 -> "Completed Daily Challenges for 30 days in a row"
            DAILY_CHALLENGE_STREAK_100 -> "Completed Daily Challenges for 100 days in a row"
            CROSSWORD_FIRST -> "Completed your first Daily Crossword"
            CROSSWORD_7 -> "Completed Daily Crosswords for 7 days in a row"
            CROSSWORD_30 -> "Completed Daily Crosswords for 30 days in a row"
            CROSSWORD_100 -> "Completed Daily Crosswords for 100 days in a row"
        }

    val sawabValue: Int
        get() = when (this) {
            SURAH_COMPLETION -> 100
            MILESTONE_10 -> 1000
            MILESTONE_25 -> 2500
            MILESTONE_50 -> 5000
            ALL_SURAHS -> 11400
            STREAK_7 -> 700
            STREAK_30 -> 3000
            STREAK_100 -> 10000
            RAMADAN_COMPLETION -> 500
            HAJJ_COMPLETION -> 500
            DAILY_CHALLENGE_FIRST -> 50
            DAILY_CHALLENGE_STREAK_7 -> 150
            DAILY_CHALLENGE_STREAK_30 -> 600
            DAILY_CHALLENGE_STREAK_100 -> 2500
            CROSSWORD_FIRST -> 50
            CROSSWORD_7 -> 150
            CROSSWORD_30 -> 600
            CROSSWORD_100 -> 2500
        }
}

@Serializable
data class ProgressStats(
    var totalVersesRead: Int = 0,
    var totalSurahsCompleted: Int = 0,
    var currentStreak: Int = 0,
    var longestStreak: Int = 0,
    var versesReadToday: Int = 0,
    var lastReadDate: Long? = null,
    var startDate: Long = 0,
    var totalSawab: Int = 0
)

data class LastReadInfo(
    val surahNumber: Int,
    val verseNumber: Int,
    val progress: Double,
    val updatedAt: Long
)

/** iOS ProgressPreferences - gates progress notifications and badge celebrations. */
@Serializable
data class ProgressPreferences(
    val notificationsEnabled: Boolean = true,
    val celebrationsEnabled: Boolean = true,
    val showStreakInHeader: Boolean = true
)
