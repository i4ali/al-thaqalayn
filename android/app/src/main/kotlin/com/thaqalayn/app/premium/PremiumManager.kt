package com.thaqalayn.app.premium

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Premium status + access control. Status is cached locally and updated by
 * BillingManager after Google Play purchase/restore (offline-first, like iOS).
 */
object PremiumManager {
    private const val PREMIUM_STATUS_KEY = "com.thaqalayn.premiumStatus"
    private lateinit var prefs: SharedPreferences

    var isPremium by mutableStateOf(false)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_premium", Context.MODE_PRIVATE)
        isPremium = prefs.getBoolean(PREMIUM_STATUS_KEY, false)
    }

    fun updatePremium(status: Boolean) {
        isPremium = status
        prefs.edit().putBoolean(PREMIUM_STATUS_KEY, status).apply()
    }

    // MARK: - Access control (mirrors iOS PremiumManager exactly)

    /** The single gate of the passage reader: the Understand stage. Surah 1 is free. */
    fun canAccessUnderstanding(surahNumber: Int): Boolean = surahNumber == 1 || isPremium

    /** The passage quiz follows the Understanding gate: al-Fatihah is free, the rest is premium. */
    fun canAccessQuiz(surahNumber: Int): Boolean = canAccessUnderstanding(surahNumber)

    /** Gems (quick overview): Surah 1 always free; surahs 2-114 require premium. */
    fun canAccessOverview(surahNumber: Int): Boolean = surahNumber == 1 || isPremium

    /** Journey days: day 1 always free, the rest premium. */
    fun canAccessJourneyDay(dayNumber: Int): Boolean = dayNumber == 1 || isPremium

    /** Deep Dives: Yaqin (the introductory dive) is always free. */
    fun canAccessDeepDive(id: String): Boolean = id == "yaqin" || isPremium

    /** Surah experiences: al-Fatiha is the free flagship teaser. */
    fun canAccessSurahExperience(id: String): Boolean = id == "surah-fatiha" || isPremium
}
