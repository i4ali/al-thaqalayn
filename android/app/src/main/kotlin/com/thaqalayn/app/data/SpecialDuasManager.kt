package com.thaqalayn.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.SpecialDua
import com.thaqalayn.app.model.SpecialDuasData
import kotlinx.serialization.json.Json

/**
 * Loads special_duas.json (the Duas & Ziyarat library) from assets. Mirrors
 * DuasManager and the iOS SpecialDuasManager. Pure content; no backend.
 */
object SpecialDuasManager {
    private val json = Json { ignoreUnknownKeys = true }

    var duas by mutableStateOf<List<SpecialDua>>(emptyList())
        private set

    fun init(context: Context) {
        duas = try {
            val text = context.assets.open("special_duas.json").bufferedReader().use { it.readText() }
            json.decodeFromString<SpecialDuasData>(text).duas
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun byId(id: String): SpecialDua? = duas.firstOrNull { it.id == id }
}
