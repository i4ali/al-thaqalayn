package com.thaqalayn.app.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.thaqalayn.app.model.SpecialDua
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Streams a remote dua/ziyarat recitation for the Duas & Ziyarat library, whose long
 * recitations are streamed rather than bundled. Mirrors the iOS DuaStreamPlayer's
 * Listen / Pause / Resume state and exposes buffering + progress so the UI can show a
 * loading state and a scrubbing bar. Playback persists across screens (the docked
 * DuaMiniPlayer keeps the controls in reach) and shows a media notification.
 *
 * It drives AudioManager's single shared ExoPlayer (verse audio and a dua stream are
 * mutually exclusive - only one plays at a time), so background playback, the media
 * session/notification, and mutual exclusion all come for free.
 */
object DuaStreamPlayer {
    var isPlaying by mutableStateOf(false)
        private set
    var isPaused by mutableStateOf(false)
        private set
    /** True while the stream is buffering enough to begin/continue playback. */
    var isLoading by mutableStateOf(false)
        private set
    /** Id of the dua currently loaded (null = idle). Buttons compare their own id. */
    var currentId by mutableStateOf<String?>(null)
        private set
    /** The dua currently loaded (null = idle) - drives the docked mini-player. */
    var currentDua by mutableStateOf<SpecialDua?>(null)
        private set
    /** Seconds. */
    var currentTime by mutableStateOf(0.0)
        private set
    var duration by mutableStateOf(0.0)
        private set
    /** Set when the stream fails to load (e.g. no connection). Cleared on the next play. */
    var failed by mutableStateOf(false)
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var tickerJob: Job? = null
    private var listenerAdded = false

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (currentId == null) return
            when (state) {
                Player.STATE_READY -> {
                    isLoading = false
                    refreshDuration()
                }
                Player.STATE_BUFFERING -> if (isPlaying) isLoading = true
                Player.STATE_ENDED -> stop()
                else -> {}
            }
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            if (currentId == null) return
            if (playing) isLoading = false
        }

        override fun onPlayerError(error: PlaybackException) {
            if (currentId == null) return
            failed = true
            AudioManager.stopStream()
            reset()
        }
    }

    private fun ensureListener() {
        if (listenerAdded) return
        AudioManager.exoPlayer?.addListener(listener)
        listenerAdded = true
    }

    /** Start streaming a dua's recitation, or resume if it's the same dua paused. */
    fun play(dua: SpecialDua) {
        val url = dua.audioUrl?.takeIf { it.isNotEmpty() } ?: return
        if (currentId == dua.id && isPaused) { resume(); return }
        if (currentId == dua.id && isPlaying) return

        TafsirReader.stop()          // mutual exclusion with TTS
        ensureListener()
        failed = false
        currentId = dua.id
        currentDua = dua
        isLoading = true
        currentTime = 0.0
        duration = 0.0
        isPlaying = true
        isPaused = false

        val artist = dua.reciterEn?.let { "Thaqalayn · $it" } ?: "Thaqalayn"
        AudioManager.playStream(dua.id, url, dua.titleEn, artist)
        startTicker()
    }

    fun pause() {
        if (!isPlaying) return
        AudioManager.exoPlayer?.pause()
        isPlaying = false
        isPaused = true
    }

    fun resume() {
        if (!isPaused) return
        AudioManager.exoPlayer?.play()
        isPlaying = true
        isPaused = false
    }

    fun togglePlayPause() {
        if (isPlaying) pause() else if (isPaused) resume()
    }

    /** Seek to an absolute position (seconds). */
    fun seek(seconds: Double) {
        val d = duration
        if (d <= 0) return
        val clamped = seconds.coerceIn(0.0, d)
        AudioManager.exoPlayer?.seekTo((clamped * 1000).toLong())
        currentTime = clamped
    }

    fun stop() {
        AudioManager.stopStream()
        reset()
    }

    /** Called by AudioManager when verse playback takes the shared player over. */
    fun onSupersededByVerse() {
        reset()
    }

    private fun reset() {
        tickerJob?.cancel(); tickerJob = null
        isPlaying = false
        isPaused = false
        isLoading = false
        currentId = null
        currentDua = null
        currentTime = 0.0
        duration = 0.0
    }

    private fun refreshDuration() {
        val d = AudioManager.exoPlayer?.duration ?: return
        if (d > 0) duration = d / 1000.0
    }

    private fun startTicker() {
        tickerJob?.cancel()
        // 0.15 s tick: fine enough for the karaoke word highlight to land on word
        // boundaries (words run ~0.3 s+), still trivial for the slider.
        tickerJob = scope.launch {
            while (isActive && currentId != null) {
                val exo = AudioManager.exoPlayer
                if (exo != null && isPlaying) {
                    val pos = exo.currentPosition
                    if (pos >= 0) currentTime = pos / 1000.0
                    if (duration <= 0) refreshDuration()
                }
                delay(150)
            }
        }
    }
}
