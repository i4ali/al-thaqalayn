package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

/**
 * Seasonal journey models. iOS defines five structurally identical model
 * families (RamadanDay/HajjDay/MuharramDay/FatimiyyaDay/ArbaeenDay + their
 * dua/verse/progress types); the JSON shapes are byte-for-byte identical, so
 * the Android port collapses them into one generic family. Per-journey
 * behavior (day counts, wording, badges) lives in JourneyManager/JourneyCatalog.
 */
@Serializable
data class JourneyData(
    val days: List<JourneyDay>
)

@Serializable
data class JourneyDay(
    val id: String,
    val dayNumber: Int,
    val theme: String,
    val themeArabic: String,
    val icon: String,
    val dua: JourneyDua,
    val verses: List<JourneyVerse>,
    val tafsirFocus: String,
    val reflection: String,
    val themeUr: String,
    val tafsirFocusUr: String,
    val reflectionUr: String
) {
    fun localizedTheme(lang: CommentaryLanguage): String =
        if (lang == CommentaryLanguage.URDU) themeUr else theme

    fun localizedTafsir(lang: CommentaryLanguage): String =
        if (lang == CommentaryLanguage.URDU) tafsirFocusUr else tafsirFocus

    fun localizedReflection(lang: CommentaryLanguage): String =
        if (lang == CommentaryLanguage.URDU) reflectionUr else reflection
}

@Serializable
data class JourneyDua(
    val arabic: String,
    val transliteration: String,
    val english: String,
    val source: String? = null,
    val englishUr: String,
    val sourceUr: String? = null,
    /** Full text for the "Read the full ziyarat" disclosure (Arbaeen station 8 only). */
    val fullArabic: String? = null,
    val fullEnglish: String? = null
) {
    fun localizedEnglish(lang: CommentaryLanguage): String =
        if (lang == CommentaryLanguage.URDU) englishUr else english

    fun localizedSource(lang: CommentaryLanguage): String? =
        if (lang == CommentaryLanguage.URDU) sourceUr ?: source else source
}

@Serializable
data class JourneyVerse(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val relevanceNote: String,
    val relevanceNoteUr: String
) {
    fun localizedNote(lang: CommentaryLanguage): String =
        if (lang == CommentaryLanguage.URDU) relevanceNoteUr else relevanceNote
}

/**
 * Per-journey completion state, persisted per Islamic year (iOS
 * Ramadan/Hajj/Muharram/Fatimiyya/ArbaeenJourneyProgress; the iOS
 * completedDays/observedDays naming split is display-only and handled
 * by JourneyStrings, not the model).
 */
@Serializable
data class JourneyProgress(
    val completedDays: Set<Int> = emptySet(),
    val lastCompletedDate: Long? = null,
    val year: Int = 0
)
