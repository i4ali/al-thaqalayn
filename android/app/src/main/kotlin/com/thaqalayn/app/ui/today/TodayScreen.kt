package com.thaqalayn.app.ui.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DailyChallengeManager
import com.thaqalayn.app.data.DailyChallengeProvider
import com.thaqalayn.app.data.DailyCrosswordManager
import com.thaqalayn.app.data.DailyCrosswordProvider
import com.thaqalayn.app.data.DailyVerseProvider
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.data.IslamicCalendarManager
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.UserProfileManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.bookmarks.rememberBookmarkTranslation
import com.thaqalayn.app.ui.bookmarks.referenceLabel
import com.thaqalayn.app.ui.bookmarks.passageMetaLabel
import com.thaqalayn.app.ui.bookmarks.BookmarkKindBadge
import com.thaqalayn.app.ui.strings.BookmarkSpotlightStrings
import com.thaqalayn.app.ui.strings.DailyChallengeStrings
import com.thaqalayn.app.ui.strings.DailyCrosswordStrings
import com.thaqalayn.app.ui.strings.TodayStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** The Today tab: daily reminder, continue reading, daily cards (iOS TodayView). */
@Composable
fun TodayScreen(navController: NavHostController) {
    val colors = Theme.colors
    val surahs by produceState(initialValue = emptyList<Surah>()) {
        value = DataManager.shared.surahs()
    }

    val dailySelection = DailyVerseProvider.today
    val dailyVerseText by produceState<String?>(initialValue = null, dailySelection) {
        value = dailySelection?.let { sel ->
            DataManager.shared.loadQuranData().verses[sel.surah.toString()]?.get(sel.verse.toString())?.translation
        }
    }

    // Day-rollover refresh whenever the tab appears.
    LaunchedEffect(Unit) {
        DailyVerseProvider.refreshIfDayChanged()
        DailyChallengeProvider.refreshIfDayChanged()
        DailyCrosswordProvider.refreshIfDayChanged()
        DailyCrosswordManager.refreshForToday()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp
        )
    ) {
        // Header row: settings avatar chip + hijri pill
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.navigate(Routes.SETTINGS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = colors.accentColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.navigate(Routes.NOTIFICATIONS) },
                        contentAlignment = Alignment.Center
                    ) {
                        PhosphorIcon(resId = R.drawable.ph_bell, size = 15.dp, tint = colors.accentColor, contentDescription = "Notifications")
                    }
                }
                Row {
                    HijriDatePill()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Greeting
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = TodayStrings.greeting(UserProfileManager.greetingName),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        color = colors.tertiaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    PhosphorIcon(resId = R.drawable.ph_moon_stars_fill, size = 13.dp, tint = colors.accentColor)
                }
                Text(
                    text = TodayStrings.today,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 40.sp,
                    color = colors.primaryText
                )
            }

        }

        // Daily reminder hero: today's verse from the 365-verse pool, its
        // translation hydrated from quran_data.json (iOS DailyVerseProvider).
        val selection = DailyVerseProvider.today
        val headline = dailyVerseText
        if (selection != null && headline != null) {
            item {
                val surahName = surahs.firstOrNull { it.number == selection.surah }?.englishName
                    ?: "Surah ${selection.surah}"
                DailyReminderHero(
                    headline = headline,
                    sourceLabel = "$surahName · ${selection.surah}:${selection.verse}",
                ) {
                    navController.navigate(Routes.surah(selection.surah, selection.verse))
                }
            }
        }

        // Continue reading
        item {
            ContinueReadingSection(surahs = surahs, navController = navController)
        }

        // Bookmark spotlight (hidden with no bookmarks)
        val latestBookmark = BookmarkManager.bookmarks.maxByOrNull { it.createdAt }
        if (latestBookmark != null) {
            item {
                BookmarkSpotlight(bookmark = latestBookmark, navController = navController)
            }
        }

        // Daily challenge
        if (DailyChallengeProvider.today != null) {
            item {
                DailyFeatureCard(
                    icon = { EmIconChip(icon = Icons.Filled.Psychology, size = 46.dp) },
                    title = DailyChallengeStrings.dailyChallenge,
                    subLine = challengeSubLine(),
                    done = DailyChallengeManager.isCompletedToday,
                ) { navController.navigate(Routes.CHALLENGE) }
            }
        }

        // Daily crossword
        if (DailyCrosswordProvider.today != null) {
            item {
                DailyFeatureCard(
                    icon = { EmIconChip(icon = Icons.Filled.GridOn, size = 46.dp) },
                    title = DailyCrosswordStrings.dailyCrossword,
                    subLine = crosswordSubLine(),
                    done = DailyCrosswordManager.isCompletedToday,
                ) { navController.navigate(Routes.CROSSWORD) }
            }
        }

        // Dua of the day
        val dua = DuasManager.duaOfTheDay()
        if (dua != null) {
            item {
                EmCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressableGentle { navController.navigate("dua/${dua.id}") }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmIconChip(icon = Icons.Filled.FormatQuote, size = 40.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = TodayStrings.duaOfTheDay.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = dua.situation,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = colors.primaryText,
                                maxLines = 2
                            )
                            Text(
                                text = dua.category.replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                color = colors.tertiaryText
                            )
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.tertiaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun challengeSubLine(): String {
    val manager = DailyChallengeManager
    val format = DailyChallengeProvider.today?.format ?: return ""
    return if (manager.isCompletedToday) {
        "${DailyChallengeStrings.doneForToday.uppercase()} · 🔥 ${manager.streak.currentStreak}"
    } else {
        val teaser = DailyChallengeStrings.teaser(format).uppercase()
        if (manager.streak.currentStreak > 0) "🔥 ${manager.streak.currentStreak} · $teaser" else teaser
    }
}

@Composable
private fun crosswordSubLine(): String {
    val manager = DailyCrosswordManager
    return if (manager.isCompletedToday) {
        "${DailyCrosswordStrings.doneForToday.uppercase()} · 🔥 ${manager.streak.currentStreak}"
    } else {
        val teaser = DailyCrosswordStrings.teaser.uppercase()
        if (manager.streak.currentStreak > 0) "🔥 ${manager.streak.currentStreak} · $teaser" else teaser
    }
}

@Composable
private fun HijriDatePill() {
    val colors = Theme.colors
    Text(
        text = IslamicCalendarManager.pillLabel(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp,
        color = colors.secondaryText,
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/**
 * Hijri season -> Today hero art (iOS ReminderSeason.current(month:day:)).
 * Everyday art fills the gaps so the Midnight Emerald card always has a cover.
 */
private fun seasonalHeroRes(): Int {
    val (_, month, day) = IslamicCalendarManager.currentIslamicDate()
    return when {
        month == 9 -> R.drawable.today_hero_ramadan
        month == 12 -> R.drawable.today_hero_hajj
        month == 1 && day <= 10 -> R.drawable.today_hero_muharram
        month == 1 -> R.drawable.today_hero_arbaeen
        month == 2 && day <= 20 -> R.drawable.today_hero_arbaeen
        month == 5 && day in 8..15 -> R.drawable.today_hero_fatimiyya
        else -> R.drawable.today_hero_everyday
    }
}

/**
 * The daily-verse hero. Midnight Emerald: seasonal night-shrine art with the
 * legibility scrims (iOS EmDailyReminderHero). Standard theme: the flat
 * gold-gradient banner, unchanged.
 */
@Composable
private fun DailyReminderHero(
    headline: String,
    sourceLabel: String,
    onTap: () -> Unit
) {
    if (Theme.colors.isMidnightEmerald) {
        SeasonalReminderHero(headline, sourceLabel, onTap)
    } else {
        GoldReminderHero(headline, sourceLabel, onTap)
    }
}

private val HeroBase = Color(0xFF06110D)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun SeasonalReminderHero(
    headline: String,
    sourceLabel: String,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    val shape = RoundedCornerShape(22.dp)
    // Horizontal legibility scrim: darkens the text side, leaves the warm
    // focal glow on the far side. Reversed for RTL, where the text sits at
    // the right edge (the art is mirrored instead - see below).
    val sideScrim =
        if (false) Brush.horizontalGradient(
            0.16f to Color.Transparent,
            0.36f to Color.Black.copy(alpha = 0.16f),
            0.62f to Color.Black.copy(alpha = 0.60f),
            1.00f to Color.Black.copy(alpha = 0.88f)
        )
        else Brush.horizontalGradient(
            0.00f to Color.Black.copy(alpha = 0.88f),
            0.38f to Color.Black.copy(alpha = 0.60f),
            0.64f to Color.Black.copy(alpha = 0.16f),
            0.84f to Color.Transparent
        )
    val bottomScrim = Brush.verticalGradient(
        0.5f to Color.Transparent,
        1.0f to HeroBase.copy(alpha = 0.5f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 136.dp)
            .shadow(20.dp, shape, ambientColor = Color.Black.copy(alpha = 0.42f), spotColor = Color.Black.copy(alpha = 0.42f))
            .clip(shape)
            .background(HeroBase)
            .border(1.dp, colors.accentColor.copy(alpha = 0.14f), shape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
                // Long-press exposes share (iOS parity).
                onLongClick = {
                    val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, "“$headline” - $sourceLabel")
                    }
                    context.startActivity(android.content.Intent.createChooser(send, null))
                }
            )
    ) {
        Image(
            painter = painterResource(seasonalHeroRes()),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                // RTL: mirror the ART so its dark side stays under the text;
                // scrims and text follow layout direction as normal.
                .graphicsLayer { if (false) scaleX = -1f }
        )
        Box(modifier = Modifier.matchParentSize().background(sideScrim))
        Box(modifier = Modifier.matchParentSize().background(bottomScrim))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = colors.accentBright,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = TodayStrings.reminderEyebrow.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    color = colors.accentBright
                )
            }
            Text(
                text = "“$headline”",
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = colors.primaryText,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.55f),
                        offset = androidx.compose.ui.geometry.Offset(0f, 1f),
                        blurRadius = 10f
                    )
                )
            )
            Text(
                text = sourceLabel,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.primaryText.copy(alpha = 0.72f)
            )
        }

    }
}

/** Refined gold hero - gold-gradient block with near-black serif text (iOS EmDailyReminderHero). */
@Composable
private fun GoldReminderHero(
    headline: String,
    sourceLabel: String,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, shape, spotColor = colors.accentColor.copy(alpha = 0.30f))
            .clip(shape)
            .background(colors.accentGradient)
            .pressable(onClick = onTap)
    ) {
        // Decorative crescent circles
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .size(110.dp)
                .clip(CircleShape)
                .background(colors.onAccentText.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-10).dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(colors.onAccentText.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = colors.onAccentText.copy(alpha = 0.75f),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = TodayStrings.reminderEyebrow.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    color = colors.onAccentText.copy(alpha = 0.75f)
                )
            }
            Text(
                text = "“$headline”",
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = colors.onAccentText
            )
            Text(
                text = sourceLabel,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onAccentText.copy(alpha = 0.7f)
            )
        }

    }
}

@Composable
private fun ContinueReadingSection(
    surahs: List<Surah>,
    navController: NavHostController
) {
    val colors = Theme.colors
    val info = ProgressManager.lastReadInfo
    val surah = info?.let { i -> surahs.firstOrNull { it.number == i.surahNumber } }
    // Load that surah's passages so the card can name the passage the reader is in.
    LaunchedEffect(info?.surahNumber) { info?.surahNumber?.let { PassageStore.load(it) } }
    // "<title> · passage i of n"; the verse line until the passage index has loaded.
    // The no-break space keeps the dot with the title when the line wraps.
    val positionLine = info?.let { i ->
        val index = i.passageIndex
        val title = i.passageTitle
        if (index != null && title != null && i.passagesTotal > 0) "$title\u00A0· passage $index of ${i.passagesTotal}" else null
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = TodayStrings.continueReading.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
            if (info != null && surah != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        EmNumeralCircle(n = surah.number, size = 48.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = surah.englishName,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = colors.primaryText
                            )
                            Text(
                                text = positionLine ?: TodayStrings.verseOf(info.verseNumber, surah.versesCount),
                                fontSize = 12.sp,
                                color = colors.tertiaryText,
                                maxLines = 2
                            )
                        }
                        Text(
                            text = surah.arabicName,
                            fontFamily = AmiriFamily,
                            fontSize = 22.sp,
                            color = colors.accentBright,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.accentChip)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(info.progress.toFloat().coerceIn(0f, 1f))
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(colors.accentGradient)
                                )
                            }
                            Text(
                                // "3 of 24 passages read", or the verse percentage until the index loads.
                                text = if (info.passagesTotal > 0) "${info.passagesRead} of ${info.passagesTotal} passages read"
                                else TodayStrings.percentComplete((info.progress * 100).toInt()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.tertiaryText
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.accentGradient)
                                .pressable { navController.navigate(Routes.surah(surah.number, info.verseNumber)) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(14.dp))
                            Text(
                                text = TodayStrings.resume,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onAccentText
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = TodayStrings.startJourney,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = colors.primaryText
                    )
                    Text(text = TodayStrings.openFatiha, fontSize = 13.sp, color = colors.secondaryText)
                    com.thaqalayn.app.ui.components.EmGoldCTA(
                        title = TodayStrings.begin,
                        icon = Icons.Filled.PlayArrow
                    ) { navController.navigate(Routes.surah(1, 1)) }
                }
            }
        }
    }
}

/** Most recently saved bookmark spotlight (iOS BookmarkSpotlightCard). */
@Composable
private fun BookmarkSpotlight(
    bookmark: com.thaqalayn.app.model.Bookmark,
    navController: NavHostController
) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale
    val translation = rememberBookmarkTranslation(bookmark)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = BookmarkSpotlightStrings.eyebrow.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        EmCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressableGentle {
                            navController.navigate(Routes.surah(bookmark.surahNumber, bookmark.verseNumber))
                        }
                        .padding(17.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = bookmark.referenceLabel,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                color = colors.accentBright
                            )
                            Text(
                                text = bookmark.surahName,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = colors.secondaryText
                            )
                        }
                        BookmarkKindBadge(isPassage = bookmark.isPassage)
                    }
                    if (bookmark.isPassage) {
                        // A passage is identified by its title, not its first verse's Arabic.
                        Text(
                            text = translation,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colors.primaryText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        bookmark.passageMetaLabel?.let {
                            Text(text = it, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = colors.tertiaryText)
                        }
                    } else {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = bookmark.verseText,
                                fontFamily = AmiriFamily,
                                fontSize = (21 * scale).sp,
                                lineHeight = (21 * scale * 1.5f).sp,
                                color = colors.primaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text(
                            text = "“$translation”",
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16 * scale).sp,
                            lineHeight = (16 * scale * 1.3f).sp,
                            color = colors.secondaryText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                EmDivider(modifier = Modifier.padding(horizontal = 17.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressableGentle { navController.navigate(Routes.BOOKMARKS) }
                        .padding(horizontal = 17.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = BookmarkSpotlightStrings.allBookmarks(BookmarkManager.bookmarks.size),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

/** Shared entry card for the daily challenge/crossword (iOS DailyChallengeCard shape). */
@Composable
private fun DailyFeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    subLine: String,
    done: Boolean,
    onOpen: () -> Unit
) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (done) it else it.pressableGentle(onClick = onOpen) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            icon()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.primaryText,
                    maxLines = 2
                )
                Text(
                    text = subLine,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (done) colors.semanticGreen else colors.accentColor
                )
            }
            if (done) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Done", tint = colors.semanticGreen, modifier = Modifier.size(16.dp))
            } else {
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = colors.tertiaryText, modifier = Modifier.size(15.dp))
            }
        }
    }
}
