package com.thaqalayn.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.DailyChallenge
import com.thaqalayn.app.model.DailyCrossword
import com.thaqalayn.app.model.DailyDua
import com.thaqalayn.app.model.DailyDuasData
import com.thaqalayn.app.model.DailyMessage
import com.thaqalayn.app.model.DailyMessagesData
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val json = Json { ignoreUnknownKeys = true }

internal fun todayDateString(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

internal fun dayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

/**
 * Deterministic rotation-of-the-day pick with a per-day cache, mirroring the iOS
 * providers: the same item stays pinned for a calendar day even if the list grows.
 */
private fun resolveDailyIndex(prefs: SharedPreferences, cacheKey: String, count: Int): Int {
    val dateString = todayDateString()
    val cachedDate = prefs.getString("$cacheKey.date", null)
    val cachedIndex = prefs.getInt("$cacheKey.index", -1)
    if (cachedDate == dateString && cachedIndex in 0 until count) return cachedIndex

    val index = dayOfYear() % count
    prefs.edit()
        .putString("$cacheKey.date", dateString)
        .putInt("$cacheKey.index", index)
        .apply()
    return index
}

/** Loads daily_messages.json and returns today's verse deterministically. */
object DailyMessageProvider {
    private lateinit var prefs: SharedPreferences
    private var messages: List<DailyMessage> = emptyList()

    var today by mutableStateOf<DailyMessage?>(null)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        messages = try {
            val text = context.assets.open("daily_messages.json").bufferedReader().use { it.readText() }
            json.decodeFromString<DailyMessagesData>(text).messages
        } catch (e: Exception) {
            emptyList()
        }
        refreshIfDayChanged()
    }

    fun refreshIfDayChanged() {
        if (messages.isEmpty()) return
        val index = resolveDailyIndex(prefs, "ThaqalaynDailyMessageCache", messages.size)
        val resolved = messages[index]
        if (resolved.id != today?.id) today = resolved
    }
}

/** Loads daily_challenges.json and returns today's challenge deterministically. */
object DailyChallengeProvider {
    private lateinit var prefs: SharedPreferences
    private var all: List<DailyChallenge> = emptyList()

    var today by mutableStateOf<DailyChallenge?>(null)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        all = try {
            val text = context.assets.open("daily_challenges.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<DailyChallenge>>(text)
        } catch (e: Exception) {
            emptyList()
        }
        refreshIfDayChanged()
    }

    fun refreshIfDayChanged() {
        if (all.isEmpty()) return
        val index = resolveDailyIndex(prefs, "ThaqalaynDailyChallengeCache", all.size)
        val resolved = all[index]
        if (resolved.id != today?.id) today = resolved
    }
}

/** Loads daily_crosswords.json and returns today's puzzle deterministically. */
object DailyCrosswordProvider {
    private lateinit var prefs: SharedPreferences
    private var all: List<DailyCrossword> = emptyList()

    var today by mutableStateOf<DailyCrossword?>(null)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_daily", Context.MODE_PRIVATE)
        all = try {
            val text = context.assets.open("daily_crosswords.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<DailyCrossword>>(text)
        } catch (e: Exception) {
            emptyList()
        }
        refreshIfDayChanged()
    }

    fun refreshIfDayChanged() {
        if (all.isEmpty()) return
        val index = resolveDailyIndex(prefs, "ThaqalaynDailyCrosswordCache", all.size)
        val resolved = all[index]
        if (resolved.id != today?.id) today = resolved
    }
}

/** Loads the daily_duas.json bundle; dua-of-the-day rotates by day of year. */
object DuasManager {
    var duas by mutableStateOf<List<DailyDua>>(emptyList())
        private set

    fun init(context: Context) {
        duas = try {
            val text = context.assets.open("daily_duas.json").bufferedReader().use { it.readText() }
            json.decodeFromString<DailyDuasData>(text).duas
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun duaOfTheDay(): DailyDua? {
        if (duas.isEmpty()) return null
        return duas[dayOfYear() % duas.size]
    }

    fun byId(id: String): DailyDua? = duas.firstOrNull { it.id == id }
}
