package com.thaqalayn.app.audio

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * Media3 session service: exposes the shared ExoPlayer as a media session so
 * recitation keeps playing in the background with a media notification
 * (the Android counterpart of iOS background audio + Now Playing info).
 */
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        AudioManager.exoPlayer?.let { player ->
            mediaSession = MediaSession.Builder(this, player).build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}
