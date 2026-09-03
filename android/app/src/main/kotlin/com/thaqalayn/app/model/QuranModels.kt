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
) {
    fun getTitle(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> title_urdu ?: title
        CommentaryLanguage.ARABIC -> title_ar ?: title
        else -> title
    }

    fun getCoreInsight(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> coreInsight_urdu ?: coreInsight
        CommentaryLanguage.ARABIC -> coreInsight_ar ?: coreInsight
        else -> coreInsight
    }

    fun getWhyItMatters(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> whyItMatters_urdu ?: whyItMatters
        CommentaryLanguage.ARABIC -> whyItMatters_ar ?: whyItMatters
        else -> whyItMatters
    }
}

@Serializable
data class QuickOverviewData(
    val concepts: List<VerseConcept>
)

// MARK: - Tafsir Data Models

data class TafsirData(
    val verses: Map<String, TafsirVerse>
)

@Serializable
data class TafsirVerse(
    val layer1: String,
    val layer2: String,
    val layer3: String,
    val layer4: String,
    val layer5: String? = null,
    val layer1_urdu: String? = null,
    val layer2_urdu: String? = null,
    val layer3_urdu: String? = null,
    val layer4_urdu: String? = null,
    val layer5_urdu: String? = null,
    val layer1_ar: String? = null,
    val layer2_ar: String? = null,
    val layer3_ar: String? = null,
    val layer4_ar: String? = null,
    val layer5_ar: String? = null,
    val layer1_fr: String? = null,
    val layer2_fr: String? = null,
    val layer3_fr: String? = null,
    val layer4_fr: String? = null,
    val layer5_fr: String? = null,
    val layer2short: String? = null,
    val layer2short_urdu: String? = null,
    val layer2short_ar: String? = null,
    val layer2short_fr: String? = null,
    val quickOverview: QuickOverviewData? = null
) {
    fun content(layer: TafsirLayer, language: CommentaryLanguage): String = when (layer) {
        TafsirLayer.FOUNDATION -> when (language) {
            CommentaryLanguage.URDU -> layer1_urdu ?: layer1
            CommentaryLanguage.ARABIC -> layer1_ar ?: layer1
            CommentaryLanguage.FRENCH -> layer1_fr ?: layer1
            else -> layer1
        }
        TafsirLayer.CLASSICAL -> when (language) {
            CommentaryLanguage.URDU -> layer2_urdu ?: layer2
            CommentaryLanguage.ARABIC -> layer2_ar ?: layer2
            CommentaryLanguage.FRENCH -> layer2_fr ?: layer2
            else -> layer2
        }
        TafsirLayer.CONTEMPORARY -> when (language) {
            CommentaryLanguage.URDU -> layer3_urdu ?: layer3
            CommentaryLanguage.ARABIC -> layer3_ar ?: layer3
            CommentaryLanguage.FRENCH -> layer3_fr ?: layer3
            else -> layer3
        }
        TafsirLayer.AHLUL_BAYT -> when (language) {
            CommentaryLanguage.URDU -> layer4_urdu ?: layer4
            CommentaryLanguage.ARABIC -> layer4_ar ?: layer4
            CommentaryLanguage.FRENCH -> layer4_fr ?: layer4
            else -> layer4
        }
        TafsirLayer.COMPARATIVE -> when (language) {
            CommentaryLanguage.URDU -> layer5_urdu ?: layer5 ?: ""
            CommentaryLanguage.ARABIC -> layer5_ar ?: layer5 ?: ""
            CommentaryLanguage.FRENCH -> layer5_fr ?: layer5 ?: ""
            else -> layer5 ?: ""
        }
    }

    fun hasContent(layer: TafsirLayer, language: CommentaryLanguage): Boolean = when (layer) {
        TafsirLayer.FOUNDATION -> when (language) {
            CommentaryLanguage.ENGLISH -> true
            CommentaryLanguage.URDU -> layer1_urdu != null
            CommentaryLanguage.ARABIC -> layer1_ar != null
            CommentaryLanguage.FRENCH -> layer1_fr != null
        }
        TafsirLayer.CLASSICAL -> when (language) {
            CommentaryLanguage.ENGLISH -> true
            CommentaryLanguage.URDU -> layer2_urdu != null
            CommentaryLanguage.ARABIC -> layer2_ar != null
            CommentaryLanguage.FRENCH -> layer2_fr != null
        }
        TafsirLayer.CONTEMPORARY -> when (language) {
            CommentaryLanguage.ENGLISH -> true
            CommentaryLanguage.URDU -> layer3_urdu != null
            CommentaryLanguage.ARABIC -> layer3_ar != null
            CommentaryLanguage.FRENCH -> layer3_fr != null
        }
        TafsirLayer.AHLUL_BAYT -> when (language) {
            CommentaryLanguage.ENGLISH -> true
            CommentaryLanguage.URDU -> layer4_urdu != null
            CommentaryLanguage.ARABIC -> layer4_ar != null
            CommentaryLanguage.FRENCH -> layer4_fr != null
        }
        TafsirLayer.COMPARATIVE -> when (language) {
            CommentaryLanguage.ENGLISH -> layer5 != null
            CommentaryLanguage.URDU -> layer5_urdu != null
            CommentaryLanguage.ARABIC -> layer5_ar != null
            CommentaryLanguage.FRENCH -> layer5_fr != null
        }
    }

    fun getLayer2(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> layer2_urdu ?: layer2
        CommentaryLanguage.ARABIC -> layer2_ar ?: layer2
        CommentaryLanguage.FRENCH -> layer2_fr ?: layer2
        else -> layer2
    }

    fun getLayer2Short(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.URDU -> layer2short_urdu ?: layer2_urdu ?: layer2
        CommentaryLanguage.ARABIC -> layer2short_ar ?: layer2_ar ?: layer2
        CommentaryLanguage.FRENCH -> layer2short_fr ?: layer2_fr ?: layer2
        else -> layer2short ?: layer2
    }
}

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

    /** Verse translation for the given language; only English and Urdu exist for verse text. */
    fun displayTranslation(language: CommentaryLanguage): String {
        if (language == CommentaryLanguage.URDU && !translationUrdu.isNullOrEmpty()) {
            return translationUrdu
        }
        return translation
    }

    /** True when the displayed translation is the Urdu one (render RTL, Arabic-script font). */
    fun usesUrduTranslation(language: CommentaryLanguage): Boolean =
        language == CommentaryLanguage.URDU && !translationUrdu.isNullOrEmpty()
}

// MARK: - Tafsir Layer Types

enum class TafsirLayer(val key: String) {
    FOUNDATION("layer1"),
    CLASSICAL("layer2"),
    CONTEMPORARY("layer3"),
    AHLUL_BAYT("layer4"),
    COMPARATIVE("layer5");

    val title: String
        get() = when (this) {
            FOUNDATION -> "Foundation"
            CLASSICAL -> "Classical Shia"
            CONTEMPORARY -> "Contemporary"
            AHLUL_BAYT -> "Ahlul Bayt"
            COMPARATIVE -> "Comparative"
        }

    val description: String
        get() = when (this) {
            FOUNDATION -> "Simple explanations, historical context, contemporary relevance"
            CLASSICAL -> "Tabatabai, Tabrisi, traditional scholarly consensus"
            CONTEMPORARY -> "Modern scholars, scientific insights, social justice themes"
            AHLUL_BAYT -> "Hadith from Imams, theological concepts, spiritual guidance"
            COMPARATIVE -> "Shia vs Sunni scholarly perspectives"
        }

    /**
     * Whether this layer is free for a given surah.
     * Surah 1: layers 1 & 2 are free. All other surahs: no free layers.
     */
    fun isFree(surahNumber: Int): Boolean {
        if (surahNumber == 1) {
            return this == FOUNDATION || this == CLASSICAL
        }
        return false
    }
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
    val updatedAt: Long
) {
    val verseReference: String get() = "$surahNumber:$verseNumber"
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
