package com.thaqalayn.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Two themes: WARM_INVITING ("Light") and NIGHT_SANCTUARY ("Midnight Emerald" -
 * emerald-black & gold). Midnight Emerald is the default for fresh installs.
 */
enum class ThemeVariant(val key: String) {
    WARM_INVITING("warmInviting"),
    NIGHT_SANCTUARY("nightSanctuary");

    val displayName: String
        get() = when (this) {
            WARM_INVITING -> "Light"
            NIGHT_SANCTUARY -> "Dark"
        }

    val description: String
        get() = when (this) {
            WARM_INVITING -> "Sanctuary-like warm design"
            NIGHT_SANCTUARY -> "Midnight Emerald - emerald-black & gold"
        }

    companion object {
        fun fromKey(key: String?): ThemeVariant? = entries.firstOrNull { it.key == key }
    }
}

object ThemeManager {
    private const val STORAGE_KEY = "selectedTheme"
    private lateinit var prefs: SharedPreferences

    var selectedTheme by mutableStateOf(ThemeVariant.NIGHT_SANCTUARY)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_settings", Context.MODE_PRIVATE)
        selectedTheme = ThemeVariant.fromKey(prefs.getString(STORAGE_KEY, null))
            ?: ThemeVariant.NIGHT_SANCTUARY
    }

    fun setTheme(theme: ThemeVariant) {
        selectedTheme = theme
        prefs.edit().putString(STORAGE_KEY, theme.key).apply()
    }

    val isDarkMode: Boolean get() = selectedTheme == ThemeVariant.NIGHT_SANCTUARY
    val isMidnightEmerald: Boolean get() = selectedTheme == ThemeVariant.NIGHT_SANCTUARY
}
