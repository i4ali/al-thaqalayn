package com.thaqalayn.app.ui.explore

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PropheticParallelsManager
import com.thaqalayn.app.data.PropheticStoriesManager
import com.thaqalayn.app.model.AhlulBaytNarration
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.ParallelCategory
import com.thaqalayn.app.model.ParallelVerse
import com.thaqalayn.app.model.PropheticStory
import com.thaqalayn.app.model.StoryCategory
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.EmSectionLabel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-in for the PropheticParallel.icon SF Symbol. */
private fun sfIcon(name: String): ImageVector = when (name) {
    "arrow.triangle.2.circlepath" -> Icons.Filled.Sync
    "arrow.uturn.backward.circle.fill" -> Icons.AutoMirrored.Filled.Undo
    "banknote" -> Icons.Filled.Payments
    "bubble.left.and.exclamationmark.bubble.right.fill" -> Icons.Filled.Forum
    "clock.badge.questionmark" -> Icons.AutoMirrored.Filled.HelpOutline
    "crown.fill" -> Icons.Filled.WorkspacePremium
    "ear.trianglebadge.exclamationmark" -> Icons.Filled.Hearing
    "figure.2.and.child.holdinghands" -> Icons.Filled.FamilyRestroom
    "figure.stand" -> Icons.Filled.Accessibility
    "figure.stand.line.dotted.figure.stand" -> Icons.Filled.SocialDistance
    "figure.walk.departure" -> Icons.AutoMirrored.Filled.DirectionsWalk
    "figure.walk.motion" -> Icons.AutoMirrored.Filled.DirectionsRun
    "heart.fill" -> Icons.Filled.Favorite
    "heart.slash" -> Icons.Filled.HeartBroken
    "hourglass.bottomhalf.filled" -> Icons.Filled.HourglassBottom
    "person.3.fill" -> Icons.Filled.Groups
    "person.crop.circle.badge.questionmark" -> Icons.Filled.PersonSearch
    "person.fill.xmark" -> Icons.Filled.PersonOff
    "scale.3d" -> Icons.Filled.Balance
    "water.waves" -> Icons.Filled.Waves
    else -> Icons.Filled.Favorite
}

/** Material stand-in for the iOS ParallelCategory.icon SF Symbol. */
private fun parallelCategoryIcon(category: ParallelCategory): ImageVector = when (category) {
    ParallelCategory.EMOTIONAL_STRUGGLES -> Icons.Filled.HeartBroken
    ParallelCategory.FAMILY_CHALLENGES -> Icons.Filled.Home
    ParallelCategory.FAITH_TESTS -> Icons.Filled.LocalFireDepartment
    ParallelCategory.WORLDLY_PRESSURES -> Icons.Filled.Public
    ParallelCategory.ISOLATION -> Icons.Filled.PersonSearch
    ParallelCategory.PERSECUTION -> Icons.Filled.GppBad
}

/** Material stand-in for the iOS StoryCategory SF Symbol (related story row). */
private fun relatedStoryIcon(category: StoryCategory): ImageVector = when (category) {
    StoryCategory.PATIENCE -> Icons.Filled.Schedule
    StoryCategory.COURAGE -> Icons.Filled.Shield
    StoryCategory.FAITH -> Icons.Filled.Star
    StoryCategory.SACRIFICE -> Icons.Filled.Favorite
    StoryCategory.LEADERSHIP -> Icons.Filled.WorkspacePremium
    StoryCategory.WISDOM -> Icons.Filled.Psychology
}

private val narrationLabel = "From the Ahlul Bayt (a)"

/** Parallel detail: situation + prophet header, narration, key verses, related story (iOS ParallelDetailView). */
@Composable
fun ParallelDetailScreen(parallelId: String, navController: NavHostController) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale
    val parallel = remember(parallelId) { PropheticParallelsManager.byId(parallelId) } ?: return
    val relatedStory = remember(parallelId) { parallel.relatedStoryId?.let { PropheticStoriesManager.byId(it) } }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Back
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, colors.strokeColor, CircleShape)
                    .pressable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Header card: category eyebrow, your situation, prophet + connection
            EmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EmSectionLabel(icon = parallelCategoryIcon(parallel.category), text = parallel.category.displayName)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        EmSectionLabel(icon = sfIcon(parallel.icon), text = "Your Situation")
                        Text(
                            text = parallel.situation,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            color = colors.primaryText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.dividerColor)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        EmSectionLabel(icon = Icons.Filled.Person, text = "Prophet")
                        Text(
                            text = parallel.prophet,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                            color = colors.accentBright
                        )
                        Text(
                            text = parallel.connection,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (17 * scale).sp,
                            lineHeight = (17 * scale * 1.5f).sp,
                            color = colors.primaryText
                        )
                    }
                }

            }

            parallel.narration?.let { AhlulBaytNarrationCard(narration = it, scale = scale) }

            // Key verses
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EmSectionLabel(icon = Icons.Filled.AutoStories, text = "Key Verses")

                parallel.verses.forEach { parallelVerse ->
                    ParallelVerseCard(
                        parallelVerse = parallelVerse,
                        scale = scale,
                        onNavigate = {
                            navController.navigate(Routes.surah(parallelVerse.surahNumber, parallelVerse.verseNumber))
                        }
                    )
                }
            }

            if (relatedStory != null) {
                RelatedStoryCard(story = relatedStory) {
                    navController.navigate(Routes.story(relatedStory.id))
                }
            }
        }
    }
}

// One verse: reference + recitation + tafsir link, Arabic + translation, relevance
// note (iOS ParallelVerseCard).
@Composable
private fun ParallelVerseCard(
    parallelVerse: ParallelVerse,
    scale: Float,
    onNavigate: () -> Unit
) {
    val colors = Theme.colors

    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = parallelVerse.surahNumber,
        key2 = parallelVerse.verseNumber
    ) {
        val surah = DataManager.shared.surah(parallelVerse.surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["${parallelVerse.surahNumber}"]?.get("${parallelVerse.verseNumber}") ?: return@produceState
        value = surah to verse
    }
    val surahName = loaded?.first?.englishName ?: "Surah ${parallelVerse.surahNumber}"
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: reference, recitation, tafsir link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "$surahName · ${parallelVerse.surahNumber}:${parallelVerse.verseNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = colors.accentColor,
                    modifier = Modifier.weight(1f)
                )

                // Recitation (iOS VerseRecitationButton, size 32)
                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback != null &&
                    playback.surahNumber == parallelVerse.surahNumber &&
                    playback.verseNumber == parallelVerse.verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, verse) ->
                                AudioManager.playVerse(VerseWithTafsir(parallelVerse.verseNumber, verse), surah)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Recite verse",
                        tint = colors.accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Row(
                    modifier = Modifier.pressable(onClick = onNavigate),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Full Tafsir",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentColor
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            // Verse text
            val verse = loaded?.second
            if (verse != null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = verse.arabicText,
                        fontFamily = AmiriFamily,
                        fontSize = (25 * scale).sp,
                        lineHeight = (25 * scale * 1.8f).sp,
                        color = colors.primaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                                Text(
                    text = verse.translation,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = (16 * scale).sp,
                    lineHeight = (16 * scale * 1.5f).sp,
                    color = colors.secondaryText,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            // Relevance note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentChip.copy(alpha = colors.accentChip.alpha * 0.6f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = parallelVerse.relevanceNote,
                    fontFamily = null,
                    fontSize = (13 * scale).sp,
                    lineHeight = (13 * scale * 1.4f).sp,
                    color = colors.secondaryText,
                    modifier = Modifier.weight(1f)
                )
            }

        }
    }
}

// "Full Story" link row to the related PropheticStory (iOS emeraldRelatedStorySection).
@Composable
private fun RelatedStoryCard(
    story: PropheticStory,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmSectionLabel(icon = Icons.Filled.Link, text = "Full Story")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmIconChip(icon = relatedStoryIcon(story.category), size = 40.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = story.prophet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = colors.accentColor
                    )
                    Text(
                        text = story.title,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        color = colors.primaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${story.verseCount} verses · Full Quranic account",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
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

// "From the Ahlul Bayt (a)": attributed narration - Arabic + translation + source
// (iOS AhlulBaytNarrationCard).
@Composable
private fun AhlulBaytNarrationCard(narration: AhlulBaytNarration, scale: Float) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.FormatQuote,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = narrationLabel.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = colors.accentColor
                )
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = narration.arabic,
                    fontFamily = AmiriFamily,
                    fontSize = (22 * scale).sp,
                    lineHeight = (22 * scale * 1.8f).sp,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = narration.translation,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (16 * scale).sp,
                lineHeight = (16 * scale * 1.5f).sp,
                color = colors.secondaryText,
                modifier = Modifier.fillMaxWidth()
            )

        

            Text(
                text = narration.source,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentColor,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}
