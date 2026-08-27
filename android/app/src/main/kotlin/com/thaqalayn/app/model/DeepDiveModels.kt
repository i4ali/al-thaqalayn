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

    /**
     * The recurring question of al-Rahman: the refrain verse glows, and the
     * reader answers it in the words the Ahl al-Bayt taught - the reply rising
     * as an ascending thread of light, the deliberate inverse of [Response]'s
     * descending one (there He answers you; here He asks and you answer).
     * [teachSource] is non-null on the first occurrence only, where the reply is
     * being taught; [replyArabic] is a taught devotional phrase, not Qur'an.
     */
    data class Refrain(
        val act: Int,
        val tag: LocalizedText,
        val surah: Int,
        val ayah: Int,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val intro: LocalizedText,
        val teachSource: LocalizedText?,
        val replyArabic: String,
        val replyTransliteration: String,
        val replyTranslation: LocalizedText,
        val reflection: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on entrustment (Tawakkul): the reader
     * names what they are gripping - in their heart - presses and holds the ring
     * (that is the grip), and the lifting of the finger IS the release, resolving
     * into the entrusting verse. Replaces [ReflectionPrompt] for such dives.
     */
    data class Release(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on gratitude (Shukr): the reader taps
     * to count blessings - each tap births a point of light - until the lights
     * begin multiplying on their own, outrunning the finger, and the screen
     * resolves into the verse: the count cannot be finished.
     */
    data class Count(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on prayer and nearness (Salah): the
     * reader presses and holds - the held stillness IS the prostration: a point of
     * light sinks to the earth-line while the screen draws close, and the verse
     * resolves while still held. Lifting afterward is the rising from sujud.
     */
    data class Sujud(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on sincerity (Ikhlas): a fixed scatter
     * of small lights - the audiences the reader has performed for. Tapping a light
     * puts it out; the last light cannot be put out - tapping it only makes it
     * flare - and the screen resolves into the verse: everything perishes except
     * His Face. Subtraction to the one unremovable Watcher, the inverse of [Count].
     */
    data class Extinguish(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on the guarding fear (Taqwa): a warm
     * "forbidden" opening rests, then drifts across the screen and away. The reader
     * must WITHHOLD - not touch it - and let it pass; holding still until it has
     * passed resolves into the verse, while reaching for it (a tap) gently resets
     * the drift. The one close where acting is the failure - restraint is the gesture.
     */
    data class Door(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
    ) : DeepDiveSection()

    /**
     * The interactive close of a dive built on the gathering's answer (al-Kisa):
     * five dim lights in a low arc - one for each soul beneath the cloak. Each tap
     * lights the next name in the order the cloak gathered them (Muhammad, Hasan,
     * Husayn, Ali, Fatima); at five the arc joins into a single glow and resolves
     * into the salawat formula. A count that COMPLETES at exactly five.
     */
    data class Salawat(
        val tag: LocalizedText,
        val prompt: LocalizedText,
        val subline: LocalizedText,
        val arabic: String,
        val translation: LocalizedText,
        val reference: String,
        val note: LocalizedText,
        val nextLabel: LocalizedText
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
            is Refrain -> act
            is ReflectionPrompt, is Dua, is Closing,
            is Release, is Count, is Sujud, is Extinguish, is Door, is Salawat -> 4
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
    val sections: List<DeepDiveSection>,
    /**
     * Noun in the movement-card chrome ("THE ENDURING - STATION 1 OF 3") - each
     * dive's own spine vocabulary: Depth (Yaqin), Station (Sabr), Motion
     * (Tawakkul), Tongue (Shukr).
     */
    val stageNoun: String = "Depth",
    /** CTA under the open beat. "Descend" for the classic dives; "Ascend" for Salah. */
    val descendCta: String = "Descend",
    /** CTA under the orientation beat. */
    val beginCta: String = "Begin the descent",
    /** Subline under the threshold-map title. */
    val mapLine: String = "The map for everything below.",
    /** The big label on movement cards and the place-bar noun ("Movement I - ..."). */
    val stageWord: String = "Movement",
    /** The line in the Amin block before the dive's `close` clause. */
    val endLine: String = "The descent ends.",
    /** The orientation beat's scroll-hint row - the journey metaphor, not the gesture. */
    val scrollHint: String = "Scroll to sink deeper",
    /** True for ascents (Salah): flips the orientation scroll-hint arrow upward. */
    val scrollHintAscending: Boolean = false
) {
    fun actInfo(n: Int): ActInfo? = acts.firstOrNull { it.number == n }
}
