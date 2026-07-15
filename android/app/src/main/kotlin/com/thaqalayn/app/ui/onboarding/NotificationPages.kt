package com.thaqalayn.app.ui.onboarding

// Onboarding pages 10-11 (iOS DailyVerseScreen + ProgressNotificationsScreen).
// Both collect an intent only - the flow coordinator requests the runtime
// permission and applies the preferences on completion, mirroring iOS.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.R
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.notifications.NotificationManager
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.theme.AmiriFamily

// MARK: - Page 10: Daily Verse (iOS DailyVerseScreen)

@Composable
fun DailyVersePage(
    notificationsEnabled: Boolean,
    onToggle: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val todayVerse = remember { NotificationManager.selectTodayVerse() }
    val monthData = remember { NotificationManager.currentMonthData() }
    val verse by produceState<Verse?>(null, todayVerse) {
        value = todayVerse?.let {
            DataManager.shared.loadQuranData()
                .verses["${it.surah}"]?.get("${it.verse}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 200, riseDistance = 0.dp, durationMillis = 600) {
                    HeroChip(chip = chipGold) {
                        Icon(
                            Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 400, riseDistance = 20.dp, durationMillis = 600) {
                    Text(
                        text = "Your Daily Companion",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Start each day with a meaningful verse",
                        style = onbBody,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            // Notification preview card (today's real verse)
            if (todayVerse != null && monthData != null && verse != null) {
                FadeRise(visible = isVisible, delayMillis = 700, riseDistance = 30.dp, durationMillis = 800) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .onboardingCard()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Verse of the Day",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnbPalette.secondaryText
                                )
                                Text(
                                    text = monthData.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnbPalette.primaryText
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = chipGold.fg,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = verse!!.arabicText,
                                fontFamily = AmiriFamily,
                                fontSize = 20.sp,
                                lineHeight = 36.sp,
                                color = OnbPalette.primaryText,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = verse!!.translation,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                                color = OnbPalette.secondaryText
                            )
                            Text(
                                text = "Surah ${todayVerse.surah}, Verse ${todayVerse.verse}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnbPalette.tertiaryText
                            )
                            Text(
                                text = todayVerse.theme,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = chipGold.fg,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(chipGold.bg)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Islamic calendar explanation
            FadeRise(visible = isVisible, delayMillis = 900, riseDistance = 0.dp, durationMillis = 600) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .onboardingRow()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Based on Islamic Calendar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnbPalette.primaryText
                        )
                    }
                    Text(
                        text = "Verses are carefully selected for each Islamic month, ensuring spiritual relevance throughout the year.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 19.sp,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            // Enable button
            FadeRise(visible = isVisible, delayMillis = 1100, riseDistance = 0.dp, durationMillis = 600) {
                EnableColumn(
                    enabled = notificationsEnabled,
                    enabledText = "Notifications Enabled",
                    disabledText = "Enable Daily Verses",
                    onToggle = onToggle
                )
            }
        }
    }
}

// MARK: - Page 11: Progress notifications (iOS ProgressNotificationsScreen)

@Composable
fun ProgressNotificationsPage(
    progressNotificationsEnabled: Boolean,
    onToggle: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 200, riseDistance = 0.dp, durationMillis = 600) {
                    HeroChip(chip = chipGold) {
                        PhosphorIcon(resId = R.drawable.ph_flame_fill, size = 44.dp, tint = chipGold.fg)
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 400, riseDistance = 20.dp, durationMillis = 600) {
                    Text(
                        text = "Stay Motivated",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Build your reading streak and earn badges",
                        style = onbBody,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            // Feature cards
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 700, riseDistance = 30.dp, durationMillis = 600) {
                    ProgressFeatureCard(
                        icon = Icons.Filled.BarChart,
                        title = "Track Your Progress",
                        description = "See your daily verse count and reading streaks"
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 800, riseDistance = 30.dp, durationMillis = 600) {
                    ProgressFeatureCard(
                        icon = Icons.Filled.LocalFireDepartment,
                        title = "Build Streaks",
                        description = "Read daily to maintain your streak and reach new milestones"
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 900, riseDistance = 30.dp, durationMillis = 600) {
                    ProgressFeatureCard(
                        icon = Icons.Filled.EmojiEvents,
                        title = "Earn Badges",
                        description = "Complete surahs and hit milestones to unlock achievements"
                    )
                }
            }

            // Enable button
            FadeRise(visible = isVisible, delayMillis = 1100, riseDistance = 0.dp, durationMillis = 600) {
                EnableColumn(
                    enabled = progressNotificationsEnabled,
                    enabledText = "Reminders Enabled",
                    disabledText = "Enable Progress Reminders",
                    onToggle = onToggle
                )
            }
        }
    }
}

@Composable
private fun ProgressFeatureCard(icon: ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .onboardingRow()
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(chipGold.bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = chipGold.fg, modifier = Modifier.size(22.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = onbRowTitle, color = OnbPalette.primaryText)
            Text(text = description, style = onbBody, color = OnbPalette.secondaryText)
        }
    }
}

// MARK: - Shared enable column (gold CTA + "later in Settings" hint)

@Composable
private fun EnableColumn(
    enabled: Boolean,
    enabledText: String,
    disabledText: String,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        OnbGoldButton(
            text = if (enabled) enabledText else disabledText,
            icon = {
                Icon(
                    if (enabled) Icons.Filled.CheckCircle else Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    tint = OnbPalette.onGold,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = onToggle
        )
        if (!enabled) {
            Text(
                text = "You can always enable this later in Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = OnbPalette.tertiaryText
            )
        }
    }
}
