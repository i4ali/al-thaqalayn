package com.thaqalayn.app.model

// Data for one immersive "deep dive": a themed, single-sitting descent rendered
// by DeepDiveScreen (iOS Models/DeepDive.swift). Prose is localized (EN/UR/AR)
// via LocalizedText; Qur'an Arabic, references, and surah/ayah numbers stay
// single-string (identical across languages).

/** The three-part structure metadata (e.g. Ilm / Ayn / Haqq al-Yaqin). */
data class ActInfo(
    val number: Int,
    val ar: String,
    val tr: String,
    val name: LocalizedText
)

/** One row in the "three depths" interactive map. */
data class Depth(
    val ar: String,
    val tr: String,
    val label: LocalizedText,
    val desc: LocalizedText,
    val reference: String?,
    val embodies: LocalizedText
)

/** A short bridge verse carried by an Act (movement divider) section. */
data class BridgeVerse(
    val surah: Int,
    val ayah: Int,
    val arabic: String,
    val translation: LocalizedText,
    val reference: String
)

/** One full-screen beat in the descent (iOS DeepDiveSection enum). */
sealed class DeepDiveSection {
    data class Open(
        val kicker: LocalizedText,
        val titleAr: String,
        val titleEn: String,
        val subtitle: LocalizedText,
        val line: LocalizedText
    ) : DeepDiveSection()

    /** A guiding "how this works + the promise" beat, shown right after the open. */
    data class Orientation(
        val eyebrow: LocalizedText,
        val promise: LocalizedText,
        val leaveWith: LocalizedText
    ) : DeepDiveSection()

    data class Verse(
        val act: Int,
        val tag: LocalizedText,
        val surah: Int,
        val ayah: Int,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val reflection: LocalizedText
    ) : DeepDiveSection()

    data class Depths(
        val act: Int,
        val tag: LocalizedText,
        val reference: String,
        val items: List<Depth>
    ) : DeepDiveSection()

    /**
     * A movement divider. [connector] names the thread back to the prior movement
     * (e.g. "You have known it by proof.") so the KNOW -> SEE -> LIVE arc is explicit.
     */
    data class Act(
        val act: Int,
        val connector: LocalizedText?,
        val line: LocalizedText,
        val bridge: BridgeVerse?
    ) : DeepDiveSection()

    data class Narration(
        val act: Int,
        val tag: LocalizedText,
        val source: LocalizedText,
        val body: LocalizedText,
        val reflection: LocalizedText
    ) : DeepDiveSection()

    /**
     * A hadith-qudsi "reply" beat: after a verse the servant has recited, God's
     * answer in the division of the prayer (Uyun Akhbar al-Rida). Distinct from
     * [Narration] so it renders as a call-and-response reply, not a story block.
     * [replyingTo] names the line He is answering; [arabic] anchors His words.
     */
    data class Response(
        val act: Int,
        val replyingTo: LocalizedText,
        val arabic: String,
        val words: LocalizedText,
        val source: LocalizedText,
        val reflection: LocalizedText
    ) : DeepDiveSection()

    data class Climax(
        val act: Int,
        val tag: LocalizedText,
        val source: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val body: LocalizedText,
        val reflection: LocalizedText
    ) : DeepDiveSection()

    data class ReflectionPrompt(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val placeholder: LocalizedText,
        val subline: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * [close] is the theme-specific final clause shown after "The descent ends."
     * in the Amin block - per-dive so it never carries another dive's theme.
     */
    data class Dua(
        val tag: LocalizedText,
        val intro: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val source: LocalizedText,
        val note: LocalizedText,
        val close: LocalizedText
    ) : DeepDiveSection()

    /**
     * The final beat of a surah experience: restates the surah's essence and
     * hands off to reading the full surah. Replaces [Dua] for surah dives -
     * a surah experience is an understanding journey, not a devotional close.
     */
    data class Closing(
        val tag: LocalizedText,
        val titleAr: String,
        val essence: LocalizedText,
        val line: LocalizedText
    ) : DeepDiveSection()

    /** Act number for the persistent depth stepper (0 = opening, 4 = close). */
    val actNumber: Int
        get() = when (this) {
            is Open, is Orientation -> 0
            is Verse -> act
            is Depths -> act
            is Act -> act
            is Narration -> act
            is Response -> act
            is Climax -> act
            is ReflectionPrompt, is Dua, is Closing -> 4
        }
}

/**
 * One immersive deep dive. Data-driven so future dives are pure content
 * additions rendered by the same DeepDiveScreen.
 */
data class DeepDive(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val subtitle: LocalizedText,
    val estMinutes: Int,
    val acts: List<ActInfo>,
    val sections: List<DeepDiveSection>
) {
    fun actInfo(n: Int): ActInfo? = acts.firstOrNull { it.number == n }
}
