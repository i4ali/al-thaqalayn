package com.thaqalayn.app.ui.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.JourneyDescriptor
import com.thaqalayn.app.data.JourneyManager
import com.thaqalayn.app.data.JourneyManagers
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.JourneyDay
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.CoverHeaderBand
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * One seasonal journey's day list (iOS RamadanJourneyView + its four clones):
 * cinematic cover header with the glow progress card, then the day rows.
 */
@Composable
fun JourneyScreen(journeyId: String, navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    val manager = JourneyManagers.byId(journeyId) ?: return
    val descriptor = JourneyDescriptor.byId(journeyId) ?: return
    val config = journeyUiConfig(journeyId)
    val currentDay = remember(journeyId) { config.currentDay() }
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                JourneyHeader(
                    manager = manager,
                    descriptor = descriptor,
                    config = config,
                    lang = lang,
                    onBack = { navController.popBackStack() }
                )
            }

            if (manager.isLoading) {
                item { JourneyLoading(lang) }
            } else if (manager.errorMessage != null) {
                item { JourneyError(manager.errorMessage ?: "", lang) }
            } else {
                items(count = manager.days.size, key = { manager.days[it].id }) { index ->
                    val day = manager.days[index]
                    CompositionLocalProvider(LocalLayoutDirection provides direction) {
                        JourneyDayRow(
                            day = day,
                            config = config,
                            lang = lang,
                            isDone = manager.isDayCompleted(day.dayNumber),
                            isCurrent = currentDay == day.dayNumber,
                            isLocked = !PremiumManager.canAccessJourneyDay(day.dayNumber),
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onTap = {
                                if (PremiumManager.canAccessJourneyDay(day.dayNumber)) {
                                    navController.navigate(Routes.journeyDay(journeyId, day.dayNumber))
                                } else {
                                    // Locked days open the veiled preview, not
                                    // the bare paywall (iOS VeiledDayPreview).
                                    navController.navigate(
                                        Routes.journeyDayPreview(journeyId, day.dayNumber)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Cover-art header + glow progress card (iOS EmJourneyHeader with coverAssetName). */
@Composable
private fun JourneyHeader(
    manager: JourneyManager,
    descriptor: JourneyDescriptor,
    config: JourneyUiConfig,
    lang: CommentaryLanguage,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val statusLine = descriptor.statusLine().ifEmpty { JourneyStrings.screenTitle(config.id, lang) }
    val countLine = when {
        config.usesStations ->
            JourneyStrings.stationsObserved(manager.completedDaysCount, manager.totalDays, lang)
        config.isObservance ->
            JourneyStrings.daysObserved(manager.completedDaysCount, manager.totalDays, lang)
        else ->
            JourneyStrings.daysCompleted(manager.completedDaysCount, manager.totalDays, lang)
    }
    val completionNote =
        if (!config.isObservance && manager.isJourneyCompleted)
            JourneyStrings.journeyCompleteNote(config.id, lang)
        else null
    val percent = manager.completionPercentage

    // Header-band art is Midnight Emerald only; the standard theme keeps the
    // plain text header with the tighter spacing and no card backing.
    val hasCover = colors.isMidnightEmerald

    Box(modifier = Modifier.fillMaxWidth()) {
        if (hasCover) {
            // Cover bleeds behind the status bar and edge-fades out before the day list.
            CoverHeaderBand(art = config.coverRes, height = 320.dp)
        }

        val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 6.dp, bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (hasCover) Color.Black.copy(alpha = 0.3f) else Color.Transparent)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.primaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = JourneyStrings.eyebrow(config.id, descriptor.eyebrow, lang).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = colors.accentColor
                        )
                        Text(
                            text = JourneyStrings.title(config.id, lang),
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 40.sp,
                            lineHeight = 44.sp,
                            color = colors.primaryText
                        )
                    }
                    EmIconChip(icon = config.icon, size = 56.dp)
                }

                // Over art: wider heading gap + deep-emerald backing keeps the
                // card text legible. Plain header: tighter 18dp, no backing.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (hasCover) 30.dp else 18.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (hasCover) Color(0xFF06120E).copy(alpha = 0.45f) else Color.Transparent
                        )
                ) {
                    EmCard(modifier = Modifier.fillMaxWidth(), glow = true) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = statusLine,
                                        fontSize = 12.sp,
                                        color = colors.secondaryText
                                    )
                                    Text(
                                        text = countLine,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.primaryText
                                    )
                                }
                                Text(
                                    text = "${(percent * 100).toInt()}%",
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 30.sp,
                                    color = colors.accentBright
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(percent.coerceIn(0f, 1f))
                                        .height(5.dp)
                                        .clip(CircleShape)
                                        .background(colors.accentGradient)
                                )
                            }
                            if (completionNote != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = colors.semanticGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = completionNote,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.semanticGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * One day row (iOS EmJourneyDayRow). Done -> gold check (festive) or quiet
 * gold-chip check (somber); current day gains the gold border + chip fill;
 * locked days wear the PREMIUM chip - never a lock icon.
 */
@Composable
internal fun JourneyDayRow(
    day: JourneyDay,
    config: JourneyUiConfig,
    lang: CommentaryLanguage,
    isDone: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(20.dp)
    val highlighted = isCurrent && !isLocked
    val dayLabel =
        if (config.usesStations) JourneyStrings.stationN(day.dayNumber, lang)
        else JourneyStrings.dayN(day.dayNumber, lang)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (highlighted) colors.accentChip else colors.glassSurface)
            .border(1.dp, if (highlighted) colors.accentColor else colors.strokeColor, shape)
            .pressable(onClick = onTap)
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isDone) {
            if (config.isObservance) {
                // Subdued: quiet gold-chip check for the somber observances.
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colors.accentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = colors.onAccentText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            EmNumeralCircle(n = day.dayNumber, size = 42.dp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = dayLabel,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.5.sp,
                    color = colors.tertiaryText
                )
                if (isLocked) {
                    Text(
                        text = JourneyStrings.premium(lang).uppercase(),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.accentColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = day.localizedTheme(lang),
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                lineHeight = 24.sp,
                maxLines = 2,
                color = if (isLocked) colors.secondaryText else colors.primaryText
            )
        }

        Text(
            text = day.themeArabic,
            fontFamily = AmiriFamily,
            fontSize = 19.sp,
            color = if (isLocked) colors.secondaryText else colors.accentColor
        )
    }
}

@Composable
private fun JourneyLoading(lang: CommentaryLanguage) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(color = colors.accentColor)
        Text(
            text = JourneyStrings.loadingJourney(lang),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.secondaryText
        )
    }
}

@Composable
private fun JourneyError(message: String, lang: CommentaryLanguage) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = JourneyStrings.errorLoadingJourney(lang),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.secondaryText
        )
    }
}
