package com.thaqalayn.app.premium

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.TafsirLayer

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

    /** Tafsir commentary: Surah 1 always free; surahs 2-114 require premium. */
    fun canAccessTafsir(surahNumber: Int): Boolean = surahNumber == 1 || isPremium

    /** Overview/summary: Surah 1 always free; surahs 2-114 require premium. */
    fun canAccessOverview(surahNumber: Int): Boolean = surahNumber == 1 || isPremium

    /** Quiz: Surah 1 always free; surahs 2-114 require premium. */
    fun canAccessQuiz(surahNumber: Int): Boolean = surahNumber == 1 || isPremium

    /**
     * Tafsir layer access:
     * - Surah 1: layers 1 & 2 free, layers 3-5 premium.
     * - Surahs 2-114: all layers premium.
     */
    fun canAccessLayer(layer: TafsirLayer, surahNumber: Int): Boolean {
        if (surahNumber == 1) {
            return when (layer) {
                TafsirLayer.FOUNDATION, TafsirLayer.CLASSICAL -> true
                else -> isPremium
            }
        }
        return isPremium
    }

    /** Journey days: day 1 always free, the rest premium. */
    fun canAccessJourneyDay(dayNumber: Int): Boolean = dayNumber == 1 || isPremium

    /** Deep Dives: Yaqin (the introductory dive) is always free. */
    fun canAccessDeepDive(id: String): Boolean = id == "yaqin" || isPremium

    /** Surah experiences: al-Fatiha is the free flagship teaser. */
    fun canAccessSurahExperience(id: String): Boolean = id == "surah-fatiha" || isPremium
}
