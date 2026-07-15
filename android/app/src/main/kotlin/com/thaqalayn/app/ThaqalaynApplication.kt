package com.thaqalayn.app

import android.app.Application
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.audio.TafsirReader
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DailyChallengeManager
import com.thaqalayn.app.data.DailyChallengeProvider
import com.thaqalayn.app.data.DailyCrosswordManager
import com.thaqalayn.app.data.DailyCrosswordProvider
import com.thaqalayn.app.data.AhlulbaytQuranManager
import com.thaqalayn.app.data.DailyMessageProvider
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.data.FastingVersesManager
import com.thaqalayn.app.data.FoodsManager
import com.thaqalayn.app.data.JourneyManagers
import com.thaqalayn.app.data.LifeMomentsManager
import com.thaqalayn.app.data.PropheticParallelsManager
import com.thaqalayn.app.data.PropheticStoriesManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.QuizManager
import com.thaqalayn.app.notifications.NotificationManager
import com.thaqalayn.app.premium.BillingManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.OnboardingManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.ThemeManager
import com.thaqalayn.app.settings.UserProfileManager

class ThaqalaynApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
        ThemeManager.init(this)
        ReadingSettingsManager.init(this)
        CommentaryLanguageManager.init(this)
        ProgressManager.init(this)
        QuizManager.init(this)
        BookmarkManager.init(this)
        PremiumManager.init(this)
        BillingManager.init(this)
        AudioManager.init(this)
        TafsirReader.init(this)
        UserProfileManager.init(this)
        OnboardingManager.init(this)
        DailyChallengeManager.init(this)
        DailyCrosswordManager.init(this)
        JourneyManagers.init(this)
        NotificationManager.init(this)
        // The daily-content JSON parses (365 challenges, crosswords, duas, messages)
        // are too heavy for Application.onCreate on the main thread; Compose state
        // writes are snapshot-safe from a background thread and the Today tab
        // renders each card as its provider comes online.
        Thread {
            DailyMessageProvider.init(this)
            DailyChallengeProvider.init(this)
            DailyCrosswordProvider.init(this)
            DuasManager.init(this)
            LifeMomentsManager.init(this)
            FoodsManager.init(this)
            PropheticStoriesManager.init(this)
            PropheticParallelsManager.init(this)
            AhlulbaytQuranManager.init(this)
            FastingVersesManager.init(this)
            JourneyManagers.loadDays(this)
            NotificationManager.loadVerseData(this)
        }.start()
    }
}
