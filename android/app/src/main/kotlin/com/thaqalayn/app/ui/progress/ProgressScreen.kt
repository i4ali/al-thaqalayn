package com.thaqalayn.app.ui.progress

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.IslamicCalendarManager
import com.thaqalayn.app.data.JourneyManagers
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageProgress
import com.thaqalayn.app.data.QuizResultsStore
import com.thaqalayn.app.data.QuizStore
import com.thaqalayn.app.model.BadgeAward
import com.thaqalayn.app.model.BadgeType
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.strings.ProgressStrings
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

private const val TOTAL_QURAN_VERSES = 6236
private const val TOTAL_SURAHS = 114
private const val TOTAL_BADGES = 24

/** Material stand-in for the iOS BadgeType.icon SF Symbol. */
private fun badgeIcon(type: BadgeType): ImageVector = when (type) {
    BadgeType.SURAH_COMPLETION -> Icons.Filled.Verified
    BadgeType.MILESTONE_10 -> Icons.Filled.Book
    BadgeType.MILESTONE_25 -> Icons.Filled.Star
    BadgeType.MILESTONE_50 -> Icons.Filled.AutoAwesome
    BadgeType.ALL_SURAHS -> Icons.Filled.Stars
    BadgeType.STREAK_7 -> Icons.Filled.LocalFireDepartment
    BadgeType.STREAK_30 -> Icons.Filled.AutoAwesome
    BadgeType.STREAK_100 -> Icons.Filled.WorkspacePremium
    BadgeType.RAMADAN_COMPLETION -> Icons.Filled.NightsStay
    BadgeType.HAJJ_COMPLETION -> Icons.Filled.AccountBalance
    BadgeType.DAILY_CHALLENGE_FIRST -> Icons.Filled.Psychology
    BadgeType.DAILY_CHALLENGE_STREAK_7 -> Icons.Filled.LocalFireDepartment
    BadgeType.DAILY_CHALLENGE_STREAK_30 -> Icons.Filled.Bolt
    BadgeType.DAILY_CHALLENGE_STREAK_100 -> Icons.Filled.WorkspacePremium
    BadgeType.CROSSWORD_FIRST -> Icons.Filled.GridOn
    BadgeType.CROSSWORD_7 -> Icons.Filled.LocalFireDepartment
    BadgeType.CROSSWORD_30 -> Icons.Filled.Bolt
    BadgeType.CROSSWORD_100 -> Icons.Filled.WorkspacePremium
}

private fun formatSawab(value: Int): String = when {
    value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
    value >= 1_000 -> String.format("%.1fK", value / 1_000.0)
    else -> "$value"
}

/** The Progress tab: rings, stats grid, streak, badge collection (iOS ProgressRingsView). */
@Composable
fun ProgressScreen(navController: NavHostController) {
    val colors = Theme.colors
    val stats = ProgressManager.stats
    val badges = ProgressManager.badges

    // Ramadan, Hajj, and Muharram seasons are mutually exclusive; they share one ring slot.
    val isRamadan = remember { IslamicCalendarManager.isRamadanSeason() }
    val isHajj = remember { IslamicCalendarManager.isHajjSeason() }
    val isMuharram = remember { IslamicCalendarManager.isMuharramSeason() }
    val showSeasonalRing = isRamadan || isHajj || isMuharram
    val seasonalLabel = when {
        isHajj -> "Hajj"
        isMuharram -> "Muharram"
        else -> "Ramadan"
    }
    // The in-season journey's completion drives the seasonal ring (iOS seasonalProgress).
    val seasonalProgress = when {
        isRamadan -> JourneyManagers.ramadan.completionPercentage
        isHajj -> JourneyManagers.hajj.completionPercentage
        isMuharram -> JourneyManagers.muharram.completionPercentage
        else -> 0f
    }
    // Passages fully read across the Quran, and the total, from the ruku index.
    val passageIndex = DataManager.shared.passageIndex
    val readVerseKeys = ProgressManager.readVerseKeys
    val passageCounts = remember(passageIndex, readVerseKeys) {
        if (passageIndex == null) 0 to 0 else {
            var read = 0
            var total = 0
            for (surah in 1..TOTAL_SURAHS) {
                val refs = passageIndex.passages(surah)
                total += refs.size
                read += PassageProgress.readCount(refs, readVerseKeys)
            }
            read to total
        }
    }
    // Quizzes shipped across the Quran; the quiz card stays hidden until there is one.
    LaunchedEffect(Unit) { QuizStore.loadShippedQuizCount() }
    val shippedQuizzes = QuizStore.shippedQuizCount ?: 0

    val quranProgress = stats.totalVersesRead / TOTAL_QURAN_VERSES.toFloat()
    val surahProgress = stats.totalSurahsCompleted / TOTAL_SURAHS.toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp)
    ) {
        item {
            EmHeading(
                eyebrow = ProgressStrings.yourJourneyEyebrow,
                title = ProgressStrings.progressTitle,
                sub = ProgressStrings.progressSubtitle
            )
        }

        // Rings card
        item {
            EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProgressRingsStack(
                        quranProgress = quranProgress,
                        surahProgress = surahProgress,
                        seasonalProgress = seasonalProgress,
                        showSeasonalRing = showSeasonalRing,
                        centerLabel = ProgressStrings.quran
                    )
                    RingLegend(
                        showSeasonalRing = showSeasonalRing,
                        seasonalLabel = ProgressStrings.seasonal(seasonalLabel),
                    )
                }
            }
        }

        // Stats grid (2 x 2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        icon = Icons.Filled.Book,
                        value = "${stats.totalVersesRead}",
                        label = ProgressStrings.versesRead,
                        sub = ProgressStrings.ofTotal(TOTAL_QURAN_VERSES),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.Verified,
                        value = "${stats.totalSurahsCompleted}",
                        label = ProgressStrings.surahsComplete,
                        sub = ProgressStrings.ofTotal(TOTAL_SURAHS),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        value = "${passageCounts.first}",
                        label = ProgressStrings.passagesRead,
                        sub = ProgressStrings.ofTotal(passageCounts.second),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.AutoAwesome,
                        value = formatSawab(stats.totalSawab),
                        label = ProgressStrings.totalSawab,
                        sub = ProgressStrings.blessingsEarned,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Passage quizzes
        if (shippedQuizzes > 0) {
            item { PassageQuizCard(shippedQuizzes) }
        }

        // Streak card
        if (stats.currentStreak > 0) {
            item {
                EmCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PhosphorIcon(resId = R.drawable.ph_flame_fill, size = 28.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = ProgressStrings.dayStreak(stats.currentStreak),
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = colors.primaryText
                            )
                            Text(
                                text = ProgressStrings.keepItGoing,
                                fontSize = 13.sp,
                                color = colors.secondaryText
                            )
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = ProgressStrings.best.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.tertiaryText
                            )
                            Text(
                                text = "${stats.longestStreak}",
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                color = colors.accentBright
                            )
                        }
                    }
                }
            }
        }

        // Badge collection
        item {
            EmDivider(label = ProgressStrings.badgesDivider(badges.size, TOTAL_BADGES))
        }
        if (badges.isEmpty()) {
            item {
                EmCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.StarOutline,
                            contentDescription = null,
                            tint = colors.tertiaryText,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = ProgressStrings.noBadgesYet,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )
                        Text(
                            text = ProgressStrings.earnBadgesHint,
                            fontSize = 12.5.sp,
                            color = colors.tertiaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val badgeRows = badges.sortedByDescending { it.awardedDate }.chunked(3)
            items(badgeRows.size) { rowIndex ->
                val row = badgeRows[rowIndex]
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { badge ->
                        BadgeTile(badge = badge, modifier = Modifier.weight(1f))
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// MARK: - Rings (iOS ProgressRingView / ProgressRingsStack)

@Composable
private fun ProgressRing(
    progress: Float,
    ringColors: List<Color>,
    lineWidth: Dp,
    size: Dp,
    glowColor: Color
) {
    val colors = Theme.colors
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "ringProgress"
    )
    Canvas(modifier = Modifier.size(size)) {
        val strokePx = lineWidth.toPx()
        val inset = strokePx / 2
        val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
        val topLeft = Offset(inset, inset)
        val brush = Brush.linearGradient(ringColors)

        // Background track (dimmed)
        drawArc(
            brush = Brush.linearGradient(ringColors.map { it.copy(alpha = 0.2f) }),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        if (animated > 0f) {
            // Soft glow behind the arc (dark themes read better with it, like iOS)
            if (colors.isMidnightEmerald) {
                drawArc(
                    color = glowColor.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx + 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun ProgressRingsStack(
    quranProgress: Float,
    surahProgress: Float,
    seasonalProgress: Float,
    showSeasonalRing: Boolean,
    centerLabel: String
) {
    val colors = Theme.colors
    val quranColors =
        if (colors.isMidnightEmerald) listOf(colors.accentColor, colors.accentColorDeep)
        else listOf(colors.semanticRed, colors.semanticRed.copy(alpha = 0.85f))
    val surahColors = listOf(colors.semanticGreen, colors.semanticGreen.copy(alpha = 0.85f))
    val seasonalColors =
        if (colors.isMidnightEmerald) listOf(colors.accentBright, colors.accentColor)
        else listOf(colors.semanticYellow, colors.semanticYellow.copy(alpha = 0.85f))

    Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
        ProgressRing(
            progress = quranProgress,
            ringColors = quranColors,
            lineWidth = 20.dp,
            size = 240.dp,
            glowColor = if (colors.isMidnightEmerald) colors.accentColor else colors.semanticRed
        )
        ProgressRing(
            progress = surahProgress,
            ringColors = surahColors,
            lineWidth = 18.dp,
            size = 180.dp,
            glowColor = colors.semanticGreen
        )
        // Inner ring: the seasonal journey, in season only (the quiz ring left with iOS 9.0).
        if (showSeasonalRing) {
            ProgressRing(
                progress = seasonalProgress,
                ringColors = seasonalColors,
                lineWidth = 16.dp,
                size = 120.dp,
                glowColor = if (colors.isMidnightEmerald) colors.accentBright else colors.semanticYellow
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${(quranProgress.coerceIn(0f, 1f) * 100).toInt()}%",
                fontSize = if (showSeasonalRing) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )
            Text(
                text = centerLabel,
                fontSize = if (showSeasonalRing) 10.sp else 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryText
            )
        }
    }
}

@Composable
private fun RingLegend(
    showSeasonalRing: Boolean,
    seasonalLabel: String,
) {
    val colors = Theme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendItem(
            color = if (colors.isMidnightEmerald) colors.accentColor else colors.semanticRed,
            label = ProgressStrings.quran
        )
        LegendItem(color = colors.semanticGreen, label = ProgressStrings.surahs)
        if (showSeasonalRing) {
            LegendItem(
                color = if (colors.isMidnightEmerald) colors.accentBright else colors.semanticYellow,
                label = seasonalLabel
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    val colors = Theme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.secondaryText)
    }
}

// MARK: - Stat + badge cards

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    EmCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(16.dp))
            Text(
                text = value,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                color = colors.accentBright
            )
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
            Text(text = sub, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = colors.tertiaryText)
        }
    }
}

@Composable
private fun BadgeTile(badge: BadgeAward, modifier: Modifier = Modifier) {
    val colors = Theme.colors
    EmCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(colors.accentChip)
                    .border(1.dp, colors.accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    badgeIcon(badge.badgeType),
                    contentDescription = null,
                    tint = colors.accentBright,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = ProgressStrings.badgeLabel(badge),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Passage quizzes on the Progress tab (iOS ProgressRingsView quiz card): taken of
 * shipped, or a hint before the first; full marks and the average best score once
 * any quiz has been taken.
 */
@Composable
private fun PassageQuizCard(shippedQuizzes: Int) {
    val colors = Theme.colors
    val taken = QuizResultsStore.takenCount
    val average = QuizResultsStore.averageBestScore
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = colors.accentColor,
                modifier = Modifier.size(28.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = if (taken > 0) {
                        if (taken == 1) "1 quiz taken" else "$taken quizzes taken"
                    } else {
                        "No quizzes taken yet"
                    },
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.primaryText
                )
                Text(
                    text = if (taken > 0) "of $shippedQuizzes available" else "Test yourself at the end of any passage.",
                    fontSize = 13.sp,
                    color = colors.secondaryText
                )
            }
            if (average != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuizFigure(label = "Full marks", value = "${QuizResultsStore.fullMarksCount}")
                    QuizFigure(label = "Average", value = String.format(java.util.Locale.US, "%.1f", average) + "/5")
                }
            }
        }
    }
}

@Composable
private fun QuizFigure(label: String, value: String) {
    val colors = Theme.colors
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = colors.tertiaryText
        )
        Text(
            text = value,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = colors.accentBright
        )
    }
}
