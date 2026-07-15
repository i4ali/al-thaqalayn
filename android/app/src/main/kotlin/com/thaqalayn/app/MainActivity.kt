package com.thaqalayn.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.thaqalayn.app.notifications.NotificationManager
import com.thaqalayn.app.notifications.NotificationPoster
import com.thaqalayn.app.ui.AppRoot
import com.thaqalayn.app.ui.theme.ThaqalaynTheme

/** A navigation request carried by a tapped notification (iOS NavigateToVerse/NavigateToJourney). */
sealed class NotificationDeepLink {
    data class Verse(val surah: Int, val verse: Int) : NotificationDeepLink()
    data class Journey(val journeyId: String) : NotificationDeepLink()
}

/**
 * Hands notification-tap intents to AppRoot: MainActivity writes the pending
 * deep link, AppRoot observes it and navigates (then clears it).
 */
object NotificationDeepLinks {
    var pending by mutableStateOf<NotificationDeepLink?>(null)
        private set

    fun consume(intent: Intent?) {
        if (intent == null) return
        val journeyId = intent.getStringExtra(NotificationPoster.EXTRA_JOURNEY)
        val surah = intent.getIntExtra(NotificationPoster.EXTRA_SURAH, -1)
        val verse = intent.getIntExtra(NotificationPoster.EXTRA_VERSE, -1)
        pending = when {
            journeyId != null -> NotificationDeepLink.Journey(journeyId)
            surah > 0 && verse > 0 -> NotificationDeepLink.Verse(surah, verse)
            else -> return
        }
        // A deep link is one-shot: strip the extras so a configuration-change
        // re-delivery of the same intent can't replay it.
        intent.removeExtra(NotificationPoster.EXTRA_JOURNEY)
        intent.removeExtra(NotificationPoster.EXTRA_SURAH)
        intent.removeExtra(NotificationPoster.EXTRA_VERSE)
        intent.removeExtra(NotificationPoster.EXTRA_TYPE)
    }

    fun clear() {
        pending = null
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationDeepLinks.consume(intent)
        setContent {
            ThaqalaynTheme {
                AppRoot()
            }
        }
    }

    /** Notification tap while the activity is alive (launchMode singleTask). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NotificationDeepLinks.consume(intent)
    }

    /** iOS handleAppBecameActive: re-check permission and refresh every schedule. */
    override fun onResume() {
        super.onResume()
        NotificationManager.handleAppBecameActive()
    }
}
