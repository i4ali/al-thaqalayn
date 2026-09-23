package com.thaqalayn.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.model.CommentaryLanguage
import java.util.Locale

/**
 * Text-to-speech for passage commentary and duas (the Android counterpart of the
 * iOS TafsirReader / AVSpeechSynthesizer). Tracks the spoken word so the reading
 * view can highlight it.
 *
 * Android TTS refuses a single utterance longer than
 * [TextToSpeech.getMaxSpeechInputLength] (about 4000 characters), and a passage's
 * Understanding runs far past that, so long text is queued as several utterances
 * split at paragraph or sentence breaks. Each utterance id carries its start
 * offset, so word ranges map back onto the full text.
 */
object TafsirReader {
    private var tts: TextToSpeech? = null
    private var ready = false

    var isPlaying by mutableStateOf(false)
        private set
    var isPaused by mutableStateOf(false)
        private set

    /** Character range of the word currently being spoken, within the full text. */
    var highlightRange by mutableStateOf<IntRange?>(null)
        private set

    /** Text currently loaded in the reader; listen buttons key their state off this. */
    var currentText by mutableStateOf<String?>(null)
        private set
    private var currentLanguage: CommentaryLanguage = CommentaryLanguage.ENGLISH

    /** Start offset in [currentText] of each queued utterance, by utterance id. */
    private val chunkOffsets = mutableMapOf<String, Int>()
    private var lastChunkId: String? = null
    /** Bumped on every speak/resume, so callbacks from a flushed queue are ignored. */
    private var generation = 0

    fun init(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == null || utteranceId != lastChunkId) return
                isPlaying = false
                isPaused = false
                highlightRange = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (utteranceId == null || !chunkOffsets.containsKey(utteranceId)) return
                isPlaying = false
                isPaused = false
                highlightRange = null
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                val base = utteranceId?.let { chunkOffsets[it] } ?: return
                highlightRange = (base + start) until (base + end)
            }
        })
    }

    private fun locale(language: CommentaryLanguage): Locale = when (language) {
        CommentaryLanguage.ENGLISH -> Locale.US
        CommentaryLanguage.URDU -> Locale("ur")
        CommentaryLanguage.ARABIC -> Locale("ar")
        CommentaryLanguage.FRENCH -> Locale.FRENCH
    }

    fun hasVoiceAvailable(language: CommentaryLanguage): Boolean {
        val engine = tts ?: return false
        if (!ready) return false
        val availability = engine.isLanguageAvailable(locale(language))
        return availability >= TextToSpeech.LANG_AVAILABLE
    }

    fun speak(text: String, language: CommentaryLanguage) {
        if (tts == null || !ready) return
        stop()
        currentText = text
        currentLanguage = language
        enqueue(text, from = 0)
    }

    fun togglePlayPause() {
        if (isPlaying) {
            // Android TTS cannot pause mid-utterance: stop, keeping the spoken word
            // highlighted; resume re-queues the text from that word.
            generation += 1
            chunkOffsets.clear()
            lastChunkId = null
            tts?.stop()
            isPlaying = false
            isPaused = true
        } else if (isPaused) {
            val text = currentText ?: return
            val resumeFrom = (highlightRange?.first ?: 0).coerceIn(0, text.length)
            enqueue(text, from = resumeFrom)
        }
    }

    fun stop() {
        generation += 1
        chunkOffsets.clear()
        lastChunkId = null
        tts?.stop()
        isPlaying = false
        isPaused = false
        highlightRange = null
    }

    /** Queue [text] from offset [from] as utterances within the engine's length limit. */
    private fun enqueue(text: String, from: Int) {
        val engine = tts ?: return
        generation += 1
        chunkOffsets.clear()
        engine.language = locale(currentLanguage)
        val limit = (TextToSpeech.getMaxSpeechInputLength() - 100).coerceAtLeast(500)
        val chunks = chunk(text, from, limit)
        if (chunks.isEmpty()) return
        chunks.forEachIndexed { i, (start, end) ->
            val id = "tafsir-$generation-$i"
            chunkOffsets[id] = start
            if (i == chunks.lastIndex) lastChunkId = id
            val mode = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            engine.speak(text.substring(start, end), mode, null, id)
        }
        isPlaying = true
        isPaused = false
    }

    /**
     * Splits text[from, length) into [start, end) ranges no longer than [limit],
     * preferring a paragraph break, then a sentence end, then a space.
     */
    private fun chunk(text: String, from: Int, limit: Int): List<Pair<Int, Int>> {
        val out = mutableListOf<Pair<Int, Int>>()
        var start = from
        while (start < text.length) {
            if (text.length - start <= limit) {
                out += start to text.length
                break
            }
            val window = text.substring(start, start + limit)
            val cut = window.lastIndexOf("\n\n").takeIf { it > limit / 3 }?.plus(2)
                ?: sentenceBreak(window, limit / 3)
                ?: window.lastIndexOf(' ').takeIf { it > 0 }?.plus(1)
                ?: limit
            out += start to (start + cut)
            start += cut
        }
        return out
    }

    private fun sentenceBreak(window: String, minIndex: Int): Int? {
        var i = window.length - 2
        while (i >= minIndex) {
            val c = window[i]
            if ((c == '.' || c == '?' || c == '!' || c == '۔' || c == '؟') && window[i + 1] == ' ') return i + 2
            i -= 1
        }
        return null
    }
}
