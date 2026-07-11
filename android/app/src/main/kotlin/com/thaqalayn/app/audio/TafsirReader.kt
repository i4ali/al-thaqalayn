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
 * Text-to-speech for tafsir commentary and duas (the Android counterpart of the
 * iOS TafsirReader / AVSpeechSynthesizer). Tracks the spoken range so the reading
 * view can highlight the current sentence.
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

    private var currentText: String? = null
    private var currentLanguage: CommentaryLanguage = CommentaryLanguage.ENGLISH
    /** Resume offset when paused (Android TTS has no native pause). */
    private var pauseOffset = 0

    fun init(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                isPlaying = false
                isPaused = false
                highlightRange = null
                pauseOffset = 0
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isPlaying = false
                isPaused = false
                highlightRange = null
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                highlightRange = (pauseOffset + start) until (pauseOffset + end)
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
        val engine = tts ?: return
        if (!ready) return
        stop()
        currentText = text
        currentLanguage = language
        pauseOffset = 0
        engine.language = locale(language)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tafsir")
        isPlaying = true
        isPaused = false
    }

    fun togglePlayPause() {
        if (isPlaying) {
            // Android TTS cannot pause mid-utterance; remember where we were and stop.
            pauseOffset = highlightRange?.first ?: 0
            tts?.stop()
            isPlaying = false
            isPaused = true
        } else if (isPaused) {
            val text = currentText ?: return
            val engine = tts ?: return
            val resumeFrom = pauseOffset.coerceIn(0, text.length)
            engine.language = locale(currentLanguage)
            engine.speak(text.substring(resumeFrom), TextToSpeech.QUEUE_FLUSH, null, "tafsir")
            isPlaying = true
            isPaused = false
        }
    }

    fun stop() {
        tts?.stop()
        isPlaying = false
        isPaused = false
        highlightRange = null
        pauseOffset = 0
    }
}
