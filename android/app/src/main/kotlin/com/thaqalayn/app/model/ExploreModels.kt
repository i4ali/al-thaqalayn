package com.thaqalayn.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// MARK: - Shared

/**
 * A single attributed narration from the Ahlul Bayt (a) - shared by the Fasting and
 * Prophetic Parallels features. The Arabic narration is always shown; the translation
 * is read by language (Arabic readers read the narration itself), and the source
 * citation is localized like every other field in this feature.
 */
@Serializable
data class AhlulBaytNarration(
    val arabic: String,
    val translationEn: String,
    val translationUr: String,
    val sourceEn: String,
    val sourceAr: String,
    val sourceUr: String
) {
    fun translation(language: CommentaryLanguage): String =
        if (language == CommentaryLanguage.URDU) translationUr else translationEn

    fun source(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> sourceAr
        CommentaryLanguage.URDU -> sourceUr
        else -> sourceEn
    }
}

// MARK: - Life Moments

@Serializable
data class LifeMomentsData(val moments: List<LifeMoment>)

@Serializable
data class LifeMoment(
    val id: String,
    val situationEn: String,
    val situationAr: String,
    val situationUr: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val category: String,
    /** Optional id of a Daily Dua (see daily_duas.json) linked to this moment. */
    val duaId: String? = null
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun situation(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> situationAr
        CommentaryLanguage.URDU -> situationUr
        else -> situationEn
    }
}

// MARK: - Foods of the Quran

@Serializable
data class FoodsData(val foods: List<Food>)

@Serializable
data class Food(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val nameUr: String,
    val emoji: String,
    val illustrationAsset: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val narrationEn: String,
    val narrationAr: String,
    val narrationUr: String,
    val narrationSource: String,
    val sunnahTipEn: String,
    val sunnahTipAr: String,
    val sunnahTipUr: String,
    val nutritionNoteEn: String,
    val nutritionNoteAr: String,
    val nutritionNoteUr: String
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun name(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> nameAr
        CommentaryLanguage.URDU -> nameUr
        else -> nameEn
    }

    fun narration(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> narrationAr
        CommentaryLanguage.URDU -> narrationUr
        else -> narrationEn
    }

    fun sunnahTip(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> sunnahTipAr
        CommentaryLanguage.URDU -> sunnahTipUr
        else -> sunnahTipEn
    }

    fun nutritionNote(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> nutritionNoteAr
        CommentaryLanguage.URDU -> nutritionNoteUr
        else -> nutritionNoteEn
    }
}

// MARK: - Prophetic Stories

@Serializable
data class PropheticStoriesData(val stories: List<PropheticStory>)

@Serializable
enum class StoryCategory {
    @SerialName("patience") PATIENCE,
    @SerialName("courage") COURAGE,
    @SerialName("faith") FAITH,
    @SerialName("sacrifice") SACRIFICE,
    @SerialName("leadership") LEADERSHIP,
    @SerialName("wisdom") WISDOM;

    val displayName: String
        get() = when (this) {
            PATIENCE -> "Patience & Perseverance"
            COURAGE -> "Courage & Bravery"
            FAITH -> "Faith & Trust"
            SACRIFICE -> "Sacrifice & Devotion"
            LEADERSHIP -> "Leadership"
            WISDOM -> "Wisdom & Knowledge"
        }
}

@Serializable
data class StoryVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val storyNoteEn: String,
    val storyNoteAr: String,
    val storyNoteUr: String,
    val isKeyVerse: Boolean
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun storyNote(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> storyNoteAr
        CommentaryLanguage.URDU -> storyNoteUr
        else -> storyNoteEn
    }
}

@Serializable
data class PropheticStory(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    val shortTitleEn: String? = null,
    val shortTitleAr: String? = null,
    val shortTitleUr: String? = null,
    val prophetEn: String,
    val prophetAr: String,
    val prophetUr: String,
    val category: StoryCategory,
    val verses: List<StoryVerse>,
    val relatedStories: List<String>,
    val lessonsSummaryEn: String? = null,
    val lessonsSummaryAr: String? = null,
    val lessonsSummaryUr: String? = null
) {
    val verseCount: Int get() = verses.size
    val keyVerses: List<StoryVerse> get() = verses.filter { it.isKeyVerse }

    fun title(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> titleAr
        CommentaryLanguage.URDU -> titleUr
        else -> titleEn
    }

    fun shortTitle(language: CommentaryLanguage): String? = when (language) {
        CommentaryLanguage.ARABIC -> shortTitleAr
        CommentaryLanguage.URDU -> shortTitleUr
        else -> shortTitleEn
    }

    fun prophet(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> prophetAr
        CommentaryLanguage.URDU -> prophetUr
        else -> prophetEn
    }

    fun lessonsSummary(language: CommentaryLanguage): String? = when (language) {
        CommentaryLanguage.ARABIC -> lessonsSummaryAr
        CommentaryLanguage.URDU -> lessonsSummaryUr
        else -> lessonsSummaryEn
    }
}

// MARK: - Prophetic Parallels

@Serializable
data class PropheticParallelsData(val parallels: List<PropheticParallel>)

@Serializable
enum class ParallelCategory {
    @SerialName("emotional_struggles") EMOTIONAL_STRUGGLES,
    @SerialName("family_challenges") FAMILY_CHALLENGES,
    @SerialName("faith_tests") FAITH_TESTS,
    @SerialName("worldly_pressures") WORLDLY_PRESSURES,
    @SerialName("isolation") ISOLATION,
    @SerialName("persecution") PERSECUTION;

    val displayName: String
        get() = when (this) {
            EMOTIONAL_STRUGGLES -> "Emotional Struggles"
            FAMILY_CHALLENGES -> "Family Challenges"
            FAITH_TESTS -> "Tests of Faith"
            WORLDLY_PRESSURES -> "Worldly Pressures"
            ISOLATION -> "Isolation & Loneliness"
            PERSECUTION -> "Persecution & Opposition"
        }
}

@Serializable
data class ParallelVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val relevanceNoteEn: String,
    val relevanceNoteAr: String,
    val relevanceNoteUr: String
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun relevanceNote(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> relevanceNoteAr
        CommentaryLanguage.URDU -> relevanceNoteUr
        else -> relevanceNoteEn
    }
}

@Serializable
data class PropheticParallel(
    val id: String,
    val situationEn: String,
    val situationAr: String,
    val situationUr: String,
    val category: ParallelCategory,
    val prophetEn: String,
    val prophetAr: String,
    val prophetUr: String,
    val connectionEn: String,
    val connectionAr: String,
    val connectionUr: String,
    val comfortMessageEn: String,
    val comfortMessageAr: String,
    val comfortMessageUr: String,
    val storySummaryEn: String,
    val storySummaryAr: String,
    val storySummaryUr: String,
    val verses: List<ParallelVerse>,
    /** Links to PropheticStory.id. */
    val relatedStoryId: String? = null,
    val icon: String,
    val narration: AhlulBaytNarration? = null
) {
    fun situation(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> situationAr
        CommentaryLanguage.URDU -> situationUr
        else -> situationEn
    }

    fun prophet(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> prophetAr
        CommentaryLanguage.URDU -> prophetUr
        else -> prophetEn
    }

    fun connection(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> connectionAr
        CommentaryLanguage.URDU -> connectionUr
        else -> connectionEn
    }

    fun comfortMessage(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> comfortMessageAr
        CommentaryLanguage.URDU -> comfortMessageUr
        else -> comfortMessageEn
    }

    fun storySummary(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> storySummaryAr
        CommentaryLanguage.URDU -> storySummaryUr
        else -> storySummaryEn
    }
}

// MARK: - Ahl al-Bayt in the Quran

@Serializable
data class AhlulbaytQuranData(val entries: List<AhlulbaytEntry>)

@Serializable
enum class AhlulbaytCategory {
    @SerialName("purity") PURITY,
    @SerialName("love") LOVE,
    @SerialName("authority") AUTHORITY,
    @SerialName("sacrifice") SACRIFICE,
    @SerialName("knowledge") KNOWLEDGE,
    @SerialName("rights") RIGHTS;

    val displayName: String
        get() = when (this) {
            PURITY -> "Purity & Sanctity"
            LOVE -> "Love & Reverence"
            AUTHORITY -> "Authority & Leadership"
            SACRIFICE -> "Sacrifice & Devotion"
            KNOWLEDGE -> "Knowledge & Wisdom"
            RIGHTS -> "Rights & Status"
        }
}

@Serializable
data class AhlulbaytVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val contextEn: String,
    val contextAr: String,
    val contextUr: String,
    val isPrimary: Boolean
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun context(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> contextAr
        CommentaryLanguage.URDU -> contextUr
        else -> contextEn
    }
}

@Serializable
data class AhlulbaytEntry(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    val shortTitleEn: String? = null,
    val shortTitleAr: String? = null,
    val shortTitleUr: String? = null,
    val category: AhlulbaytCategory,
    val verses: List<AhlulbaytVerse>,
    val ahlulbaytMembersEn: List<String>,
    val ahlulbaytMembersAr: List<String>,
    val ahlulbaytMembersUr: List<String>,
    val revelationContextEn: String,
    val revelationContextAr: String,
    val revelationContextUr: String,
    val relatedEntries: List<String>
) {
    val verseCount: Int get() = verses.size
    val primaryVerses: List<AhlulbaytVerse> get() = verses.filter { it.isPrimary }

    fun title(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> titleAr
        CommentaryLanguage.URDU -> titleUr
        else -> titleEn
    }

    fun shortTitle(language: CommentaryLanguage): String? = when (language) {
        CommentaryLanguage.ARABIC -> shortTitleAr
        CommentaryLanguage.URDU -> shortTitleUr
        else -> shortTitleEn
    }

    fun ahlulbaytMembers(language: CommentaryLanguage): List<String> = when (language) {
        CommentaryLanguage.ARABIC -> ahlulbaytMembersAr
        CommentaryLanguage.URDU -> ahlulbaytMembersUr
        else -> ahlulbaytMembersEn
    }

    fun revelationContext(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> revelationContextAr
        CommentaryLanguage.URDU -> revelationContextUr
        else -> revelationContextEn
    }
}

// MARK: - Fasting in the Quran

@Serializable
data class FastingVersesData(val categories: List<FastingCategory>)

@Serializable
data class FastingCategory(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    val icon: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val descriptionUr: String,
    val verses: List<FastingVerse>,
    val narration: AhlulBaytNarration? = null
) {
    val verseCount: Int get() = verses.size

    fun title(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> titleAr
        CommentaryLanguage.URDU -> titleUr
        else -> titleEn
    }

    fun description(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> descriptionAr
        CommentaryLanguage.URDU -> descriptionUr
        else -> descriptionEn
    }
}

@Serializable
data class FastingVerse(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val relevanceNoteEn: String,
    val relevanceNoteAr: String,
    val relevanceNoteUr: String,
    val isKeyVerse: Boolean
) {
    val verseReference: String get() = "Quran $surahNumber:$verseNumber"

    fun relevanceNote(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> relevanceNoteAr
        CommentaryLanguage.URDU -> relevanceNoteUr
        else -> relevanceNoteEn
    }
}
