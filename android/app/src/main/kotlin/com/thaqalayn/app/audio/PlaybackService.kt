package com.thaqalayn.app.audio

import android.app.PendingIntent
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
            val builder = MediaSession.Builder(this, player)
            packageManager.getLaunchIntentForPackage(packageName)?.let { launch ->
                builder.setSessionActivity(
                    PendingIntent.getActivity(this, 0, launch, PendingIntent.FLAG_IMMUTABLE)
                )
            }
            mediaSession = builder.build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}
