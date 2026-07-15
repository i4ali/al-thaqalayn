package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.JourneyData
import com.thaqalayn.app.model.JourneyDay
import com.thaqalayn.app.model.JourneyProgress
import kotlinx.serialization.json.Json

/**
 * One seasonal journey's days + per-year completion progress. iOS has five
 * near-identical manager classes (Ramadan/Hajj/Muharram/Fatimiyya/Arbaeen
 * JourneyManager); this single class is instantiated five times in
 * [JourneyManagers] with per-journey config. Progress persistence keys and
 * semantics (year reset, mark/unmark, completion badge) match iOS exactly.
 */
class JourneyManager(
    val journeyId: String,
    private val assetFile: String,
    val totalDays: Int,
    private val progressKey: String,
    /** Called once when every day is completed (Ramadan/Hajj badge award). */
    private val onJourneyCompleted: ((islamicYear: Int) -> Unit)? = null
) {
    var days by mutableStateOf<List<JourneyDay>>(emptyList())
        private set
    var progress by mutableStateOf(JourneyProgress())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    /** Cheap part of startup: progress from prefs + Islamic-year reset. */
    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_journeys", Context.MODE_PRIVATE)
        prefs.getString(progressKey, null)?.let {
            try {
                progress = json.decodeFromString<JourneyProgress>(it)
            } catch (e: Exception) {
                Log.w("JourneyManager", "$journeyId: could not decode progress", e)
            }
        }
        checkYearReset()
    }

    /** Heavy part of startup: the journey JSON parse. Background thread. */
    fun loadDays(context: Context) {
        try {
            val text = context.assets.open(assetFile).bufferedReader().use { it.readText() }
            days = json.decodeFromString<JourneyData>(text).days
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load journey: ${e.message}"
            isLoading = false
            Log.e("JourneyManager", "$journeyId: failed to load $assetFile", e)
        }
    }

    private fun saveProgress() {
        prefs.edit().putString(progressKey, json.encodeToString(progress)).apply()
    }

    /** New Islamic year -> fresh progress (iOS checkYearReset). */
    private fun checkYearReset() {
        val currentYear = IslamicCalendarManager.currentIslamicYear()
        if (progress.year != currentYear) {
            progress = JourneyProgress(year = currentYear)
            saveProgress()
        }
    }

    fun isDayCompleted(dayNumber: Int): Boolean = dayNumber in progress.completedDays

    fun markDayCompleted(dayNumber: Int) {
        if (dayNumber !in 1..totalDays || isDayCompleted(dayNumber)) return
        var updated = progress.copy(
            completedDays = progress.completedDays + dayNumber,
            lastCompletedDate = System.currentTimeMillis()
        )
        if (updated.year == 0) {
            updated = updated.copy(year = IslamicCalendarManager.currentIslamicYear())
        }
        progress = updated
        saveProgress()
        if (progress.completedDays.size >= totalDays) {
            onJourneyCompleted?.invoke(IslamicCalendarManager.currentIslamicYear())
        }
    }

    fun unmarkDayCompleted(dayNumber: Int) {
        if (dayNumber !in 1..totalDays || !isDayCompleted(dayNumber)) return
        progress = progress.copy(completedDays = progress.completedDays - dayNumber)
        saveProgress()
    }

    fun day(byNumber: Int): JourneyDay? = days.firstOrNull { it.dayNumber == byNumber }

    val completedDaysCount: Int get() = progress.completedDays.size

    val completionPercentage: Float get() = completedDaysCount / totalDays.toFloat()

    val isJourneyCompleted: Boolean get() = completedDaysCount >= totalDays
}

/** The five seasonal journeys (iOS *JourneyManager.shared singletons). */
object JourneyManagers {
    val ramadan = JourneyManager("ramadan", "ramadan_journey.json", 30, "ramadanJourneyProgress") {
        ProgressManager.awardRamadanBadge(it)
    }
    val hajj = JourneyManager("hajj", "hajj_journey.json", 10, "hajjJourneyProgress") {
        ProgressManager.awardHajjBadge(it)
    }

    // Somber observances - no badges (mourning, not achievement).
    val muharram = JourneyManager("muharram", "muharram_journey.json", 10, "muharramJourneyProgress")
    val fatimiyya = JourneyManager("fatimiyya", "fatimiyya_journey.json", 5, "fatimiyyaJourneyProgress")
    val arbaeen = JourneyManager("arbaeen", "arbaeen_journey.json", 8, "arbaeenJourneyProgress")

    val all = listOf(ramadan, hajj, muharram, fatimiyya, arbaeen)

    fun byId(id: String): JourneyManager? = all.firstOrNull { it.journeyId == id }

    fun init(context: Context) = all.forEach { it.init(context) }

    fun loadDays(context: Context) = all.forEach { it.loadDays(context) }
}
