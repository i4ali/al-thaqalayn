package com.thaqalayn.app.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.thaqalayn.app.data.DailyMessageProvider
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.data.IslamicCalendarManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.settings.CommentaryLanguageManager
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
    val lang = CommentaryLanguageManager.selectedLanguage
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    val surahs by produceState(initialValue = emptyList<Surah>()) {
        value = DataManager.shared.surahs()
    }

    // Day-rollover refresh whenever the tab appears.
    LaunchedEffect(Unit) {
        DailyMessageProvider.refreshIfDayChanged()
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
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = TodayStrings.greeting(UserProfileManager.greetingName, lang),
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
                        text = TodayStrings.today(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 40.sp,
                        color = colors.primaryText
                    )
                }
            }
        }

        // Daily reminder hero (gold gradient)
        val message = DailyMessageProvider.today
        if (message != null) {
            item {
                val surahName = surahs.firstOrNull { it.number == message.surah }?.englishName
                    ?: "Surah ${message.surah}"
                DailyReminderHero(
                    headline = message.english,
                    sourceLabel = "$surahName · ${message.surah}:${message.verse}",
                    lang = lang
                ) {
                    navController.navigate(Routes.surah(message.surah, message.verse))
                }
            }
        }

        // Continue reading
        item {
            ContinueReadingSection(surahs = surahs, lang = lang, navController = navController)
        }

        // Bookmark spotlight (hidden with no bookmarks)
        val latestBookmark = BookmarkManager.bookmarks.maxByOrNull { it.createdAt }
        if (latestBookmark != null) {
            item {
                BookmarkSpotlight(bookmark = latestBookmark, lang = lang, navController = navController)
            }
        }

        // Daily challenge
        if (DailyChallengeProvider.today != null) {
            item {
                DailyFeatureCard(
                    icon = { EmIconChip(icon = Icons.Filled.Psychology, size = 46.dp) },
                    title = DailyChallengeStrings.dailyChallenge(lang),
                    subLine = challengeSubLine(lang),
                    done = DailyChallengeManager.isCompletedToday,
                    lang = lang
                ) { navController.navigate(Routes.CHALLENGE) }
            }
        }

        // Daily crossword
        if (DailyCrosswordProvider.today != null) {
            item {
                DailyFeatureCard(
                    icon = { EmIconChip(icon = Icons.Filled.GridOn, size = 46.dp) },
                    title = DailyCrosswordStrings.dailyCrossword(lang),
                    subLine = crosswordSubLine(lang),
                    done = DailyCrosswordManager.isCompletedToday,
                    lang = lang
                ) { navController.navigate(Routes.CROSSWORD) }
            }
        }

        // Dua of the day
        val dua = DuasManager.duaOfTheDay()
        if (dua != null) {
            item {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
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
                                    text = TodayStrings.duaOfTheDay(lang).uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = if (lang.isRTL) 0.sp else 1.5.sp,
                                    color = colors.accentColor
                                )
                                Text(
                                    text = dua.situation(lang),
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
}

@Composable
private fun challengeSubLine(lang: CommentaryLanguage): String {
    val manager = DailyChallengeManager
    val format = DailyChallengeProvider.today?.format ?: return ""
    return if (manager.isCompletedToday) {
        "${DailyChallengeStrings.doneForToday(lang).uppercase()} · 🔥 ${manager.streak.currentStreak}"
    } else {
        val teaser = DailyChallengeStrings.teaser(format, lang).uppercase()
        if (manager.streak.currentStreak > 0) "🔥 ${manager.streak.currentStreak} · $teaser" else teaser
    }
}

@Composable
private fun crosswordSubLine(lang: CommentaryLanguage): String {
    val manager = DailyCrosswordManager
    return if (manager.isCompletedToday) {
        "${DailyCrosswordStrings.doneForToday(lang).uppercase()} · 🔥 ${manager.streak.currentStreak}"
    } else {
        val teaser = DailyCrosswordStrings.teaser(lang).uppercase()
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

/** Refined gold hero - gold-gradient block with near-black serif text (iOS EmDailyReminderHero). */
@Composable
private fun DailyReminderHero(
    headline: String,
    sourceLabel: String,
    lang: CommentaryLanguage,
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

        CompositionLocalProvider(
            LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
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
                        text = TodayStrings.reminderEyebrow(lang).uppercase(),
                        fontSize = if (lang.isRTL) 13.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang.isRTL) 0.sp else 1.3.sp,
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
}

@Composable
private fun ContinueReadingSection(
    surahs: List<Surah>,
    lang: CommentaryLanguage,
    navController: NavHostController
) {
    val colors = Theme.colors
    val info = ProgressManager.lastReadInfo
    val surah = info?.let { i -> surahs.firstOrNull { it.number == i.surahNumber } }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = TodayStrings.continueReading(lang).uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (lang.isRTL) 0.sp else 2.sp,
            color = colors.accentColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = if (lang.isRTL) TextAlign.End else TextAlign.Start
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
                                text = TodayStrings.verseOf(info.verseNumber, surah.versesCount, lang),
                                fontSize = 12.sp,
                                color = colors.tertiaryText,
                                maxLines = 1
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
                                text = TodayStrings.percentComplete((info.progress * 100).toInt(), lang),
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
                                text = TodayStrings.resume(lang),
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
                        text = TodayStrings.startJourney(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = colors.primaryText
                    )
                    Text(text = TodayStrings.openFatiha(lang), fontSize = 13.sp, color = colors.secondaryText)
                    com.thaqalayn.app.ui.components.EmGoldCTA(
                        title = TodayStrings.begin(lang),
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
    lang: CommentaryLanguage,
    navController: NavHostController
) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = BookmarkSpotlightStrings.eyebrow(lang).uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (lang.isRTL) 0.sp else 2.sp,
            color = colors.accentColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = if (lang.isRTL) TextAlign.End else TextAlign.Start
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
                                text = bookmark.verseReference,
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
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.accentGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(15.dp))
                        }
                    }
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
                        text = "“${bookmark.verseTranslation}”",
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.3f).sp,
                        color = colors.secondaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
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
                        text = BookmarkSpotlightStrings.allBookmarks(BookmarkManager.bookmarks.size, lang),
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
    lang: CommentaryLanguage,
    onOpen: () -> Unit
) {
    val colors = Theme.colors
    CompositionLocalProvider(
        LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
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
                        letterSpacing = if (lang.isRTL) 0.sp else 1.sp,
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
}
