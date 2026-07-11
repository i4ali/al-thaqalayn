package com.thaqalayn.app

import android.app.Application
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.audio.TafsirReader
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.premium.BillingManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.ThemeManager

class ThaqalaynApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
        ThemeManager.init(this)
        ReadingSettingsManager.init(this)
        CommentaryLanguageManager.init(this)
        ProgressManager.init(this)
        BookmarkManager.init(this)
        PremiumManager.init(this)
        BillingManager.init(this)
        AudioManager.init(this)
        TafsirReader.init(this)
    }
}
