package com.thaqalayn.app.audio

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CurrentPlayback
import com.thaqalayn.app.model.Reciter
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.VerseWithTafsir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Verse recitation playback (everyayah.com per-verse audio) via ExoPlayer.
 * Port of the iOS AudioManager: single-verse mode, sequential surah playback
 * with auto-advance, skip next/previous, reciter selection persisted locally.
 */
object AudioManager {
    private const val RECITER_KEY = "selectedReciterId"

    private var player: ExoPlayer? = null
    private lateinit var prefs: SharedPreferences

    /** Shared player instance for the media session service. */
    val exoPlayer: ExoPlayer? get() = player
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var playerState by mutableStateOf(AudioPlayerState.STOPPED)
        private set
    var currentPlayback by mutableStateOf<CurrentPlayback?>(null)
        private set
    var selectedReciter by mutableStateOf(Reciter.default)
        private set

    private var currentSurah: Surah? = null
    private var queuedVerseNumbers: List<Int> = emptyList()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("thaqalayn_audio", Context.MODE_PRIVATE)
        selectedReciter = Reciter.byId(prefs.getString(RECITER_KEY, null))

        val exo = ExoPlayer.Builder(context.applicationContext).build()
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                playerState = when (state) {
                    Player.STATE_BUFFERING -> AudioPlayerState.BUFFERING
                    Player.STATE_READY -> if (exo.playWhenReady) AudioPlayerState.PLAYING else AudioPlayerState.PAUSED
                    Player.STATE_ENDED -> {
                        currentPlayback = null
                        AudioPlayerState.STOPPED
                    }
                    else -> AudioPlayerState.STOPPED
                }
                refreshPlayback()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (exo.playbackState == Player.STATE_READY) {
                    playerState = if (playWhenReady) AudioPlayerState.PLAYING else AudioPlayerState.PAUSED
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                refreshPlayback()
            }

            override fun onPlayerError(error: PlaybackException) {
                playerState = AudioPlayerState.ERROR
            }
        })
        player = exo

        // Progress ticker for the bottom player UI.
        scope.launch {
            while (isActive) {
                if (playerState == AudioPlayerState.PLAYING) refreshPlayback()
                delay(500)
            }
        }
    }

    fun setReciter(reciter: Reciter) {
        selectedReciter = reciter
        prefs.edit().putString(RECITER_KEY, reciter.id).apply()
    }

    private fun refreshPlayback() {
        val exo = player ?: return
        val surah = currentSurah ?: return
        val index = exo.currentMediaItemIndex
        val verseNumber = queuedVerseNumbers.getOrNull(index) ?: return
        currentPlayback = CurrentPlayback(
            surahNumber = surah.number,
            surahName = surah.englishName,
            verseNumber = verseNumber,
            reciter = selectedReciter,
            currentTime = exo.currentPosition.coerceAtLeast(0),
            duration = exo.duration.coerceAtLeast(0)
        )
    }

    private fun mediaItem(surah: Surah, verseNumber: Int): MediaItem =
        MediaItem.Builder()
            .setUri(selectedReciter.verseAudioUrl(surah.number, verseNumber))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("${surah.englishName} · Verse $verseNumber")
                    .setArtist(selectedReciter.nameEnglish)
                    .build()
            )
            .build()

    /** Play a single verse; tapping the playing verse toggles pause/resume (iOS behavior). */
    fun playVerse(verse: VerseWithTafsir, surah: Surah) {
        val exo = player ?: return
        val playback = currentPlayback
        if (playback != null && playback.surahNumber == surah.number && playback.verseNumber == verse.number) {
            togglePlayPause()
            return
        }
        currentSurah = surah
        queuedVerseNumbers = listOf(verse.number)
        exo.setMediaItem(mediaItem(surah, verse.number))
        exo.prepare()
        exo.play()
        playerState = AudioPlayerState.LOADING
        refreshPlayback()
    }

    /** Play verses sequentially from the given index, auto-advancing to the end. */
    fun playVerseSequence(verses: List<VerseWithTafsir>, surah: Surah, startingFrom: Int = 0) {
        val exo = player ?: return
        if (verses.isEmpty()) return
        currentSurah = surah
        val remaining = verses.drop(startingFrom.coerceIn(0, verses.size - 1))
        queuedVerseNumbers = remaining.map { it.number }
        exo.setMediaItems(remaining.map { mediaItem(surah, it.number) })
        exo.prepare()
        exo.play()
        playerState = AudioPlayerState.LOADING
        refreshPlayback()
    }

    fun togglePlayPause() {
        val exo = player ?: return
        if (exo.isPlaying) exo.pause() else exo.play()
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
        player?.clearMediaItems()
        currentPlayback = null
        currentSurah = null
        queuedVerseNumbers = emptyList()
        playerState = AudioPlayerState.STOPPED
    }

    fun skipToNext() {
        val exo = player ?: return
        if (exo.hasNextMediaItem()) exo.seekToNextMediaItem()
    }

    fun skipToPrevious() {
        val exo = player ?: return
        if (exo.hasPreviousMediaItem()) exo.seekToPreviousMediaItem()
        else exo.seekTo(0)
    }
}
