package com.thaqalayn.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.AhlulbaytEntry
import com.thaqalayn.app.model.AhlulbaytQuranData
import com.thaqalayn.app.model.FastingCategory
import com.thaqalayn.app.model.FastingVersesData
import com.thaqalayn.app.model.Food
import com.thaqalayn.app.model.FoodsData
import com.thaqalayn.app.model.LifeMoment
import com.thaqalayn.app.model.LifeMomentsData
import com.thaqalayn.app.model.PropheticParallel
import com.thaqalayn.app.model.PropheticParallelsData
import com.thaqalayn.app.model.PropheticStoriesData
import com.thaqalayn.app.model.PropheticStory
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

private inline fun <reified T> loadAsset(context: Context, name: String): T? = try {
    val text = context.assets.open(name).bufferedReader().use { it.readText() }
    json.decodeFromString<T>(text)
} catch (e: Exception) {
    null
}

/** Loads life_moments.json (Explore -> Life Moments). */
object LifeMomentsManager {
    var moments by mutableStateOf<List<LifeMoment>>(emptyList())
        private set

    fun init(context: Context) {
        moments = loadAsset<LifeMomentsData>(context, "life_moments.json")?.moments ?: emptyList()
    }

    fun byId(id: String): LifeMoment? = moments.firstOrNull { it.id == id }
}

/** Loads foods.json (Explore -> Foods of the Quran). */
object FoodsManager {
    var foods by mutableStateOf<List<Food>>(emptyList())
        private set

    fun init(context: Context) {
        foods = loadAsset<FoodsData>(context, "foods.json")?.foods ?: emptyList()
    }

    fun byId(id: String): Food? = foods.firstOrNull { it.id == id }
}

/** Loads prophetic_stories.json (Explore -> Prophetic Stories). */
object PropheticStoriesManager {
    var stories by mutableStateOf<List<PropheticStory>>(emptyList())
        private set

    fun init(context: Context) {
        stories = loadAsset<PropheticStoriesData>(context, "prophetic_stories.json")?.stories ?: emptyList()
    }

    fun byId(id: String): PropheticStory? = stories.firstOrNull { it.id == id }
}

/** Loads prophetic_parallels.json (Explore -> Prophetic Parallels). */
object PropheticParallelsManager {
    var parallels by mutableStateOf<List<PropheticParallel>>(emptyList())
        private set

    fun init(context: Context) {
        parallels = loadAsset<PropheticParallelsData>(context, "prophetic_parallels.json")?.parallels ?: emptyList()
    }

    fun byId(id: String): PropheticParallel? = parallels.firstOrNull { it.id == id }
}

/** Loads ahlulbayt_quran.json (Explore -> Ahl al-Bayt in Quran). */
object AhlulbaytQuranManager {
    var entries by mutableStateOf<List<AhlulbaytEntry>>(emptyList())
        private set

    fun init(context: Context) {
        entries = loadAsset<AhlulbaytQuranData>(context, "ahlulbayt_quran.json")?.entries ?: emptyList()
    }

    fun byId(id: String): AhlulbaytEntry? = entries.firstOrNull { it.id == id }
}

/** Loads fasting_verses.json (Explore -> Fasting in the Quran). */
object FastingVersesManager {
    var categories by mutableStateOf<List<FastingCategory>>(emptyList())
        private set

    fun init(context: Context) {
        categories = loadAsset<FastingVersesData>(context, "fasting_verses.json")?.categories ?: emptyList()
    }

    fun byId(id: String): FastingCategory? = categories.firstOrNull { it.id == id }
}
