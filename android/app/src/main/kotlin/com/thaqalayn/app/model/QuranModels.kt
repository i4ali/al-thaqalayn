package com.thaqalayn.app.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

// MARK: - Quran Data Models

@Serializable
data class QuranData(
    val surahs: List<Surah>,
    val verses: Map<String, Map<String, Verse>>
)

@Serializable
data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val arabicName: String,
    val versesCount: Int,
    val revelationType: String
)

@Serializable
data class Verse(
    val arabicText: String,
    /**
     * English translation: Ali Quli Qarai's phrase-by-phrase translation (the standard
     * modern Shia rendering). Replaced Sahih International on 2026-09-01; refresh or
     * switch editions with scripts/fetch_quran_english.py.
     */
    val translation: String,
    /** Urdu translation (Allama Jawadi). Optional for verses that predate the field. */
    val translationUrdu: String? = null,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    @Serializable(with = SajdaInfoSerializer::class)
    val sajda: SajdaInfo
)

/**
 * Sajda is either a plain boolean or an object like {"id": 1, "recommended": true}
 * in quran_data.json, mirroring the custom Codable logic on iOS.
 */
data class SajdaInfo(
    val hasSajda: Boolean,
    val id: Int? = null,
    val recommended: Boolean? = null
)

object SajdaInfoSerializer : KSerializer<SajdaInfo> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SajdaInfo")

    override fun deserialize(decoder: Decoder): SajdaInfo {
        val input = decoder as JsonDecoder
        return when (val element = input.decodeJsonElement()) {
            is JsonPrimitive -> SajdaInfo(hasSajda = element.booleanOrNull ?: false)
            is JsonObject -> SajdaInfo(
                hasSajda = true,
                id = (element["id"] as? JsonPrimitive)?.intOrNull,
                recommended = (element["recommended"] as? JsonPrimitive)?.booleanOrNull
            )
            else -> SajdaInfo(hasSajda = false)
        }
    }

    override fun serialize(encoder: Encoder, value: SajdaInfo) {
        val output = encoder as JsonEncoder
        if (value.id != null || value.recommended != null) {
            output.encodeJsonElement(buildJsonObject {
                value.id?.let { put("id", JsonPrimitive(it)) }
                value.recommended?.let { put("recommended", JsonPrimitive(it)) }
            })
        } else {
            output.encodeJsonElement(JsonPrimitive(value.hasSajda))
        }
    }
}

// MARK: - Quick Overview Models

@Serializable
enum class ConceptPosition {
    topLeft, topRight, bottomLeft, bottomRight
}

@Serializable
data class VerseConcept(
    val id: String,
    val title: String,
    val icon: String,
    val colorHex: String,
    val coreInsight: String,
    val whyItMatters: String,
    val position: ConceptPosition,
    val arabicHighlight: String? = null,
    val title_urdu: String? = null,
    val coreInsight_urdu: String? = null,
    val whyItMatters_urdu: String? = null,
    val title_ar: String? = null,
    val coreInsight_ar: String? = null,
    val whyItMatters_ar: String? = null
)

@Serializable
data class QuickOverviewData(
    val concepts: List<VerseConcept>
)

// MARK: - Tafsir Data Models

data class TafsirData(
    val verses: Map<String, TafsirVerse>
)

/**
 * Per-verse data decoded from tafsir_N.json. Only the Quick Overview gems are
 * read now (iOS 8.6); the layered commentary keys those files still carry are
 * ignored by the decoder. Passage commentary lives in PassageStore.
 */
@Serializable
data class TafsirVerse(
    val quickOverview: QuickOverviewData? = null
)

// MARK: - Display Models

data class SurahWithTafsir(
    val surah: Surah,
    val verses: List<VerseWithTafsir>
) {
    val id: Int get() = surah.number
}

data class VerseWithTafsir(
    val number: Int,
    val arabicText: String,
    val translation: String,
    val translationUrdu: String?,
    val sajda: SajdaInfo,
    val tafsir: TafsirVerse?
) {
    val id: String get() = number.toString()
    val bookmarkKey: String get() = id

    constructor(number: Int, verse: Verse, tafsir: TafsirVerse? = null) : this(
        number = number,
        arabicText = verse.arabicText,
        translation = verse.translation,
        translationUrdu = verse.translationUrdu,
        sajda = verse.sajda,
        tafsir = tafsir
    )
}

// MARK: - Commentary Language Support

@Serializable
enum class CommentaryLanguage(val code: String) {
    ENGLISH("en"),
    URDU("ur"),
    ARABIC("ar"),
    FRENCH("fr");

    val displayName: String
        get() = when (this) {
            ENGLISH -> "English"
            URDU -> "اردو"
            ARABIC -> "العربية"
            FRENCH -> "Français"
        }

    val shortCode: String
        get() = when (this) {
            ENGLISH -> "EN"
            URDU -> "UR"
            ARABIC -> "AR"
            FRENCH -> "FR"
        }

    val isRTL: Boolean
        get() = this == URDU || this == ARABIC

    companion object {
        /** Languages that have tafsir content available (excludes French). */
        val supportedTafsirLanguages = listOf(ENGLISH, URDU, ARABIC)

        fun fromCode(code: String): CommentaryLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

// MARK: - Bookmark Models

@Serializable
data class Bookmark(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String,
    val verseText: String,
    val verseTranslation: String,
    val notes: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long,
    /**
     * 1-based passage (ruku) index when the bookmark saves a whole passage; null for
     * a single verse. A passage bookmark keeps the passage's first verse in
     * [verseNumber], that verse's Arabic in [verseText] and the passage title in
     * [verseTranslation]. Records saved before passage bookmarks decode as verses.
     */
    val passageIndex: Int? = null
) {
    val verseReference: String get() = "$surahNumber:$verseNumber"

    val isPassage: Boolean get() = passageIndex != null

    /**
     * Lookup key for the verse heart. A passage bookmark never matches, even at its
     * own first verse, so a verse and the passage holding it can both be saved.
     */
    fun matchesVerse(surah: Int, verse: Int): Boolean =
        passageIndex == null && surahNumber == surah && verseNumber == verse

    fun matchesPassage(surah: Int, index: Int): Boolean =
        surahNumber == surah && passageIndex == index

    companion object {
        /** Quran order: surah, then verse, then a passage before the verse it opens on, then save time. */
        val quranOrder: Comparator<Bookmark> = Comparator { a, b ->
            when {
                a.surahNumber != b.surahNumber -> a.surahNumber.compareTo(b.surahNumber)
                a.verseNumber != b.verseNumber -> a.verseNumber.compareTo(b.verseNumber)
                a.isPassage != b.isPassage -> if (a.isPassage) -1 else 1
                else -> a.createdAt.compareTo(b.createdAt)
            }
        }
    }
}

enum class BookmarkSortOrder(val key: String) {
    DATE_ASCENDING("date_asc"),
    DATE_DESCENDING("date_desc"),
    SURAH_ORDER("surah_order"),
    ALPHABETICAL("alphabetical");

    val title: String
        get() = when (this) {
            DATE_ASCENDING -> "Oldest First"
            DATE_DESCENDING -> "Newest First"
            SURAH_ORDER -> "Quran Order"
            ALPHABETICAL -> "Alphabetical"
        }
}
