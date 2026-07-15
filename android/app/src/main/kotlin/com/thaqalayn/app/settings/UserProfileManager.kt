package com.thaqalayn.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Personal display name shown beside the Today-tab greeting. */
object UserProfileManager {
    private const val STORAGE_KEY = "userDisplayName"
    private lateinit var prefs: SharedPreferences

    var displayName by mutableStateOf("")
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_settings", Context.MODE_PRIVATE)
        displayName = prefs.getString(STORAGE_KEY, "") ?: ""
    }

    fun setName(name: String) {
        displayName = name
        prefs.edit().putString(STORAGE_KEY, name).apply()
    }

    /** Trimmed name for the greeting (empty when only whitespace). */
    val greetingName: String get() = displayName.trim()
}
