package com.thaqalayn.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * First-launch gate for the onboarding flow. Same key as iOS
 * (`UserDefaults "hasShownWelcome"`), stored in the shared settings prefs.
 */
object OnboardingManager {
    private const val STORAGE_KEY = "hasShownWelcome"
    private lateinit var prefs: SharedPreferences

    var hasShownWelcome by mutableStateOf(true)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_settings", Context.MODE_PRIVATE)
        hasShownWelcome = prefs.getBoolean(STORAGE_KEY, false)
    }

    fun markShown() {
        hasShownWelcome = true
        prefs.edit().putBoolean(STORAGE_KEY, true).apply()
    }
}
