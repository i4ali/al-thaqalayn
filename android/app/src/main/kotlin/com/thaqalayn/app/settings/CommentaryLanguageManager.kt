package com.thaqalayn.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.CommentaryLanguage

/** Commentary language preference (EN/UR/AR tafsir content). */
object CommentaryLanguageManager {
    private const val STORAGE_KEY = "commentaryLanguage"
    private lateinit var prefs: SharedPreferences

    var selectedLanguage by mutableStateOf(CommentaryLanguage.ENGLISH)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_settings", Context.MODE_PRIVATE)
        selectedLanguage = CommentaryLanguage.fromCode(prefs.getString(STORAGE_KEY, "en") ?: "en")
    }

    fun setLanguage(language: CommentaryLanguage) {
        selectedLanguage = language
        prefs.edit().putString(STORAGE_KEY, language.code).apply()
    }

    /** Cycle to the next supported tafsir language (EN -> UR -> AR -> EN). */
    fun toggleLanguage() {
        val supported = CommentaryLanguage.supportedTafsirLanguages
        val currentIndex = supported.indexOf(selectedLanguage)
        if (currentIndex == -1) {
            setLanguage(CommentaryLanguage.ENGLISH)
            return
        }
        setLanguage(supported[(currentIndex + 1) % supported.size])
    }
}
