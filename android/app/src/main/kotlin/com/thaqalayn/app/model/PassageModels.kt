package com.thaqalayn.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Passage commentary models (iOS Models/PassageModels.swift): one commentary per
// ruku, shipped as passages_<surah>.json keyed by the passage index within the surah.

/** The languages this text carries, English first (drives the Understanding pills). */
val LocalizedText.availableLanguages: List<CommentaryLanguage>
    get() = buildList {
        add(CommentaryLanguage.ENGLISH)
        if (!ur.isNullOrEmpty()) add(CommentaryLanguage.URDU)
        if (!ar.isNullOrEmpty()) add(CommentaryLanguage.ARABIC)
    }

@Serializable
data class Passage(
    val id: String,
    val surah: Int,
    val index: Int,
    val range: List<Int>,
    val title: LocalizedText,
    val essay: LocalizedText,
    val verses: List<PassageVerseEntry>,
    val perspectives: LocalizedText? = null,
    val sources: List<PassageSource>,
    val status: PassageStatus? = null
) {
    val start: Int get() = range[0]
    val end: Int get() = range[1]
    val verseCount: Int get() = end - start + 1
    val narrationCount: Int get() = verses.sumOf { it.narrations.size }

    fun source(id: String): PassageSource? = sources.firstOrNull { it.id == id }
    fun entry(verse: Int): PassageVerseEntry? = verses.firstOrNull { it.verse == verse }

    /** Whole minutes to read the Understanding screen at 200 words a minute, at least 1. */
    val readingMinutes: Int
        get() {
            var words = essay.en.wordCount()
            for (v in verses) {
                words += (v.note?.en ?: "").wordCount()
                words += v.narrations.sumOf { it.text.en.wordCount() }
            }
            words += (perspectives?.en ?: "").wordCount()
            return maxOf(1, words / 200 + 1)
        }
}

/** Swift `split(separator: " ").count`: runs of spaces count once, empty text counts zero. */
internal fun String.wordCount(): Int = split(' ').count { it.isNotEmpty() }

/** A note-only entry may omit `narrations`; that decodes as empty rather than failing the file. */
@Serializable
data class PassageVerseEntry(
    val verse: Int,
    val heading: LocalizedText? = null,
    val note: LocalizedText? = null,
    val narrations: List<Narration> = emptyList()
)

@Serializable
data class Narration(
    val id: String,
    val speaker: String,
    val addressee: String? = null,
    val arabic: String,
    val chain: String? = null,
    val text: LocalizedText,
    val source: String
)

@Serializable
data class SourceExcerpt(
    val lang: String,
    val text: String
)

@Serializable
data class PassageSource(
    val id: String,
    val kind: String,
    val work: String,
    val author: String,
    val tradition: String,
    val tier: String,
    val locus: String,
    val url: String,
    val excerpt: SourceExcerpt? = null,
    val gloss: String? = null,
    val grades: List<String>? = null
) {
    /** Marker number, so "s12" renders as [12]. */
    val number: Int get() = id.drop(1).toIntOrNull() ?: 0
}

@Serializable
data class PassageStatus(
    @SerialName("gathered_at") val gatheredAt: String? = null,
    @SerialName("audit_attempts") val auditAttempts: Int? = null,
    @SerialName("audited_at") val auditedAt: String? = null,
    @SerialName("assembled_at") val assembledAt: String? = null
)

// MARK: - Passage index (iOS Models/PassageIndex.swift)

/**
 * One ruku of one surah, the unit of the passage reader. Exists for every surah
 * whether or not commentary has been generated for it.
 */
data class PassageRef(
    val surah: Int,
    val index: Int,
    val start: Int,
    val end: Int
) {
    val id: String get() = "$surah:$index"
    val verses: IntRange get() = start..end
    val verseCount: Int get() = end - start + 1
    val rangeLabel: String get() = if (start == end) "$start" else "$start to $end"
}

/** Passage boundaries for all 114 surahs, derived once from quran_data.json's ruku field. */
class PassageIndex(quran: QuranData) {
    private val bySurah: Map<Int, List<PassageRef>>

    init {
        val out = HashMap<Int, List<PassageRef>>()
        for (surah in quran.surahs) {
            val verses = quran.verses[surah.number.toString()] ?: continue
            val groups = sortedMapOf<Int, MutableList<Int>>()
            for ((key, record) in verses) {
                val n = key.toIntOrNull() ?: continue
                groups.getOrPut(record.ruku) { mutableListOf() }.add(n)
            }
            out[surah.number] = groups.values.mapIndexed { i, vs ->
                PassageRef(surah = surah.number, index = i + 1, start = vs.min(), end = vs.max())
            }
        }
        bySurah = out
    }

    fun passages(surah: Int): List<PassageRef> = bySurah[surah].orEmpty()

    fun passage(surah: Int, index: Int): PassageRef? = passages(surah).getOrNull(index - 1)

    fun passageContaining(surah: Int, verse: Int): PassageRef? =
        passages(surah).firstOrNull { verse in it.start..it.end }

    fun next(after: PassageRef): PassageRef? = passage(after.surah, after.index + 1)
    fun previous(before: PassageRef): PassageRef? = passage(before.surah, before.index - 1)
}

// MARK: - Passage stages (iOS Models/PassageStages.swift)

/**
 * The three stages of a passage - Read, Understand, Test yourself - and where a
 * reader stands in them. Understand exists only where the commentary has shipped
 * and Test only where the quiz has, so a passage with verses alone is complete
 * once it is read. The hub, the list ring and the hub's bottom bar all read this.
 */
data class PassageStages(
    val isRead: Boolean,
    val isUnderstood: Boolean,
    /** Best quiz score out of [quizTotal], once the quiz has been taken. */
    val bestScore: Int? = null,
    val quizTotal: Int = 5,
    val hasCommentary: Boolean,
    val hasQuiz: Boolean
) {
    enum class Stage { READ, UNDERSTAND, TEST }

    /** The stages this passage offers, in order. */
    val available: List<Stage>
        get() = buildList {
            add(Stage.READ)
            if (hasCommentary) add(Stage.UNDERSTAND)
            if (hasQuiz) add(Stage.TEST)
        }

    fun isDone(stage: Stage): Boolean = when (stage) {
        Stage.READ -> isRead
        Stage.UNDERSTAND -> isUnderstood
        Stage.TEST -> bestScore != null
    }

    val total: Int get() = available.size
    val doneCount: Int get() = available.count(::isDone)
    val isComplete: Boolean get() = doneCount == total

    /** The first stage still to do, in order; null once the passage is complete. */
    val next: Stage? get() = available.firstOrNull { !isDone(it) }
}

// MARK: - Passage quizzes (iOS Models/QuizModels.swift)

/**
 * One shipped passage quiz, decoded from quiz_<surah>.json, an object keyed by
 * passage index. Generated and reviewed by the iOS quiz pipeline; never written.
 */
@Serializable
data class PassageQuiz(
    val id: String,
    val surah: Int,
    val index: Int,
    val range: List<Int>,
    val questions: List<QuizQuestion>
) {
    val start: Int get() = range.firstOrNull() ?: 0
    val end: Int get() = range.lastOrNull() ?: 0
    val ref: PassageRef get() = PassageRef(surah, index, start, end)
}

/** The four framings. A file carrying any other type fails to decode and hides that surah's quizzes. */
@Serializable
enum class QuizQuestionType {
    multipleChoice, trueFalse, fillGap, whoSaid;

    val label: String
        get() = when (this) {
            multipleChoice -> "Multiple choice"
            trueFalse -> "True or false"
            fillGap -> "Fill the gap"
            whoSaid -> "Who said it"
        }
}

@Serializable
data class QuizQuestion(
    val id: String,
    val type: QuizQuestionType,
    val verse: Int,
    val prompt: LocalizedText,
    val options: List<LocalizedText>,
    val answer: Int,
    val explanation: LocalizedText,
    val anchor: QuizAnchor
)

/** Where in the passage the answer is settled. */
@Serializable
data class QuizAnchor(
    @SerialName("where") val location: String,
    val quote: String
)

/** Best score on one passage quiz (iOS PassageQuizResult). */
@Serializable
data class PassageQuizResult(
    val surah: Int,
    val index: Int,
    val score: Int,
    val total: Int,
    val completedAt: Long
) {
    val key: String get() = "$surah:$index"
}
