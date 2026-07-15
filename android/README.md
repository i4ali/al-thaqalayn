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

## Release build & signing

Release builds are minified (R8 + resource shrinking) and signed with an
**upload key** that lives outside git:

- `android/keystore/upload-keystore.jks` - the upload keystore (gitignored)
- `android/keystore.properties` - storeFile/storePassword/keyAlias/keyPassword
  (gitignored)

`app/build.gradle.kts` reads `keystore.properties` if present and attaches the
signing config to the `release` build type; without it, release builds are
simply unsigned (debug builds are unaffected). **Back up both files** (e.g. a
password manager). With Play App Signing the upload key can be reset if lost,
but a backup avoids the support round-trip.

```
./gradlew :app:bundleRelease     # -> app/build/outputs/bundle/release/app-release.aab
./gradlew :app:assembleRelease   # signed APK for local install/testing
```

The AAB is ~66MB (the 195MB of JSON compresses well) - far under Play's 200MB
limit, so no Play Asset Delivery needed.

### Launcher icon

The adaptive icon (`mipmap-anydpi-v26/ic_launcher.xml`) is generated from the
iOS `icon-1024.png`: foreground PNGs per density (emblem scaled into the 66dp
safe zone on a matching emerald fill), `ic_launcher_background` color, and a
monochrome layer for Android 13+ themed icons. Legacy `ic_launcher.png` files
remain as fallback. The Play listing icon is `android/store-assets/play_icon_512.png`.

## Releasing to Google Play (one-time setup)

Everything below happens in [Play Console](https://play.google.com/console)
and can only be done by the account owner:

1. **Create the app**: All apps -> Create app -> name "Thaqalayn", default
   language English (US), App, Free. The applicationId `com.thaqalayn.app` is
   fixed by the first uploaded AAB.
2. **Play App Signing** is enabled by default for new apps: Google holds the
   app signing key; the local keystore above is only the upload key. Accept
   the default (Google-generated signing key) on first upload.
3. **Upload the AAB**: Testing -> Internal testing -> Create release ->
   upload `app-release.aab`. Internal testing first; promote later.
4. **Create the premium product**: Monetize -> Products -> In-app products ->
   Create product, Product ID exactly `com.thaqalayn.premium.tafsir`
   (must match `BillingManager`), one-time (managed) product, set name,
   description, and price, then **Activate** it. In-app products only work
   after the app has been uploaded at least once, and the Google account used
   for testing must be a license tester (Settings -> License testing) to test
   purchases without being charged.
5. **Store listing**: app icon = `store-assets/play_icon_512.png`, feature
   graphic 1024x500, 2+ phone screenshots, short + full description.
6. **Declarations**: content rating questionnaire, data safety form (no data
   collected/shared - everything is on-device), target audience, ads
   declaration (none).

Subsequent releases: bump `versionCode`/`versionName` in
`app/build.gradle.kts`, `./gradlew :app:bundleRelease`, upload.
