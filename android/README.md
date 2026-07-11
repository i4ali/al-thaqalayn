# Thaqalayn for Android

Native Android port of the Thaqalayn iOS app (Kotlin + Jetpack Compose).

## Layout

```
android/
  app/                      The Android app module
    src/main/kotlin/com/thaqalayn/app/
      model/                Data models (port of iOS Models/)
      data/                 DataManager, BookmarkManager, ProgressManager, search (port of iOS Services/)
      settings/             ThemeManager, ReadingSettingsManager, CommentaryLanguageManager
      audio/                AudioManager (ExoPlayer recitation), TafsirReader (TTS), PlaybackService
      premium/              PremiumManager (gating rules) + BillingManager (Google Play Billing)
      ui/                   Compose screens & components
    src/main/assets/        themes_index.json (generated - see below)
  scripts/                  Build-time data generators
```

## Shared data with iOS

The Quran + tafsir JSON (~195MB) is **shared with the iOS app and referenced in
place**, not copied. `app/build.gradle.kts` adds
`../../Thaqalayn/Thaqalayn/Data` as an extra assets source dir, so
`quran_data.json` and `tafsir_1..114.json` are bundled straight from the iOS
tree. Text compresses heavily in the AAB, and everything ships on-device -
the app is fully offline (no Supabase on Android; bookmarks, progress, and
premium status are stored locally).

### themes_index.json

iOS builds its theme-search entries at runtime by preloading all 114 tafsir
files. Android keeps tafsir lazy-loaded per surah, so the tiny theme index is
precomputed instead:

```
python3 android/scripts/generate_theme_index.py
```

Re-run whenever any `tafsir_N.json` quickOverview data changes.

## Differences from iOS (by design)

- **No Supabase**: no auth, account deletion, or cloud sync. All state is local.
- **Premium** uses Google Play Billing with the one-time product
  `com.thaqalayn.premium.tafsir` (create it in Play Console before release).
- Gems concept bubbles render as an expandable gem list instead of the
  floating-bubble layout around the Arabic text.

## Build

```
cd android
./gradlew :app:assembleDebug
```

Requires JDK 17+ and the Android SDK (compileSdk 36). minSdk 26,
applicationId `com.thaqalayn.app`.
