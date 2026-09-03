package com.thaqalayn.app.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Per-word recitation timings for the Duas & Ziyarat library, powering the golden
 * karaoke word highlight while a recitation streams. Produced offline (MMS forced
 * alignment of each dua's Arabic against its duas.org recording) and bundled as
 * special_dua_timings.json. A word is addressed as (segment index in
 * SpecialDua.segments, token index in that segment's whitespace-split Arabic).
 * Mirrors the iOS SpecialDuaTimings.
 */

/** Address of one Arabic word: [segment] indexes SpecialDua.segments (notes included),
 *  [token] the space-split tokens of that segment's `ar`. */
data class DuaWordPosition(val segment: Int, val token: Int)

/**
 * Tokenizes a segment's Arabic exactly like the alignment pipeline (Python's
 * str.split(); the texts only ever use plain single spaces). Splits on the space
 * character, not on grapheme clusters: a standalone Quranic pause mark clusters with
 * the preceding space, and a grapheme-level split would swallow it, shifting every
 * later index off the aligned timings.
 */
object DuaArabicTokenizer {
    fun tokens(ar: String): List<String> = ar.split(" ").filter { it.isNotEmpty() }
}

data class DuaWordTiming(
    val position: DuaWordPosition,
    val start: Double,
    val end: Double
)

/** One dua's aligned word timeline, with the lookups the karaoke UI needs. */
class DuaTimings(
    /** Duration of the recording the alignment was made against. If the streamed
     *  file's duration disagrees (duas.org swapped the recording), the timings are
     *  stale and highlighting is disabled. */
    val audioDuration: Double,
    words: List<DuaWordTiming>
) {
    /** Words in recitation order (starts are monotonic). */
    val words: List<DuaWordTiming> = words.sortedBy { it.start }
    private val segmentStarts: Map<Int, Double>

    init {
        val starts = HashMap<Int, Double>()
        for (w in this.words) starts.putIfAbsent(w.position.segment, w.start)
        segmentStarts = starts
    }

    /** The word being recited at [time]. A word stays lit until the next one starts
     *  (recitation pauses shouldn't flicker the highlight off). */
    fun position(at: Double): DuaWordPosition? {
        val first = words.firstOrNull() ?: return null
        val last = words.last()
        if (at < first.start || at > last.end + TAIL_LINGER) return null
        // Binary search: last word whose start is <= time.
        var lo = 0
        var hi = words.size - 1
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (words[mid].start <= at) lo = mid else hi = mid - 1
        }
        return words[lo].position
    }

    /** Where segment [segment]'s first word is recited (for tap-to-seek). */
    fun segmentStart(segment: Int): Double? = segmentStarts[segment]

    companion object {
        /** Once the last word has ended, keep it lit briefly (recitations often trail
         *  off with un-transcribed audio), then clear the highlight. */
        private const val TAIL_LINGER = 1.5
    }
}

/** Loads the bundled timings file once and hands out per-dua timelines. */
object SpecialDuaTimingsStore {
    private var appContext: Context? = null
    private var cache: Map<String, DuaTimings>? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun timings(duaID: String): DuaTimings? {
        if (cache == null) cache = load()
        return cache?.get(duaID)
    }

    // Compact wire format: words are [segment, token, startCentisec, endCentisec].
    @Serializable
    private data class TimingsFile(val version: Int, val duas: Map<String, RawDua>)

    @Serializable
    private data class RawDua(val duration: Double, val words: List<List<Int>>)

    private fun load(): Map<String, DuaTimings> {
        val ctx = appContext ?: return emptyMap()
        val file = try {
            val text = ctx.assets.open("special_dua_timings.json").bufferedReader().use { it.readText() }
            Json { ignoreUnknownKeys = true }.decodeFromString<TimingsFile>(text)
        } catch (e: Exception) {
            return emptyMap()
        }
        val out = HashMap<String, DuaTimings>()
        for ((id, raw) in file.duas) {
            val words = raw.words.mapNotNull { entry ->
                if (entry.size != 4) return@mapNotNull null
                DuaWordTiming(
                    position = DuaWordPosition(segment = entry[0], token = entry[1]),
                    start = entry[2] / 100.0,
                    end = entry[3] / 100.0
                )
            }
            if (words.isEmpty()) continue
            out[id] = DuaTimings(audioDuration = raw.duration, words = words)
        }
        return out
    }
}
