package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

/**
 * The "Duas & Ziyarat" library: the major, most-recited Shia supplications
 * (Kumayl, Ziyarat Ashura, Tawassul, Nudba, al-Ahd), shown parallel to the short
 * everyday Daily Duas. Each dua is a segmented text (Arabic + transliteration +
 * translation per line, plus the occasional structural note like "repeat 100x"),
 * and streams a real recitation from a remote URL. Text and recitations are
 * courtesy of Duas.org. Mirrors the iOS SpecialDua model.
 */
@Serializable
data class SpecialDuasData(val duas: List<SpecialDua>)

/** One major supplication or ziyarat. */
@Serializable
data class SpecialDua(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    /** When it is traditionally recited (e.g. "Thursday nights"). English chrome. */
    val whenEn: String,
    /** Who it is attributed to / narrated from. */
    val attributionEn: String,
    /** A short "what it's for" line. */
    val purposeEn: String,
    /** A one-to-two sentence context blurb (reading body - scales with text size). */
    val introEn: String,
    /** Remote recitation stream (null = no recording). */
    val audioUrl: String? = null,
    /** Named reciter, when known (shown in the credit line). */
    val reciterEn: String? = null,
    /** Attribution line for the source of the text/recitation. */
    val sourceCreditEn: String,
    val segments: List<SpecialDuaSegment>
) {
    /** Content lines only (excludes structural "repeat 100x" notes). */
    val bodySegments: List<SpecialDuaSegment> get() = segments.filter { it.ar != null }
}

/**
 * One line of a supplication: either a content line (Arabic + transliteration +
 * translation) or a structural instruction (`note`, e.g. "Then prostrate and say:").
 */
@Serializable
data class SpecialDuaSegment(
    val ar: String? = null,
    val tr: String? = null,
    val en: String? = null,
    val note: String? = null
) {
    val isNote: Boolean get() = note != null
}
