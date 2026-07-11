package com.thaqalayn.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * User reading preferences: the commentary/reading body font scale, adjusted from
 * the in-context "Aa" control and persisted across launches. All reading content
 * (Arabic, translations, tafsir, narrations) must multiply its font size by [scale].
 */
object ReadingSettingsManager {
    private const val STORAGE_KEY = "commentaryFontScaleIndex"

    /** Discrete multiplier steps; default is index 1 (1.0x). */
    val steps = listOf(0.9f, 1.0f, 1.15f, 1.3f, 1.5f)
    const val DEFAULT_INDEX = 1

    private lateinit var prefs: SharedPreferences

    var stepIndex by mutableIntStateOf(DEFAULT_INDEX)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_settings", Context.MODE_PRIVATE)
        stepIndex = prefs.getInt(STORAGE_KEY, DEFAULT_INDEX).coerceIn(0, steps.size - 1)
    }

    val scale: Float get() = steps[stepIndex]
    val stepCount: Int get() = steps.size
    val canIncrease: Boolean get() = stepIndex < steps.size - 1
    val canDecrease: Boolean get() = stepIndex > 0

    fun increase() {
        if (canIncrease) setIndex(stepIndex + 1)
    }

    fun decrease() {
        if (canDecrease) setIndex(stepIndex - 1)
    }

    fun setIndex(index: Int) {
        stepIndex = index.coerceIn(0, steps.size - 1)
        prefs.edit().putInt(STORAGE_KEY, stepIndex).apply()
    }
}
