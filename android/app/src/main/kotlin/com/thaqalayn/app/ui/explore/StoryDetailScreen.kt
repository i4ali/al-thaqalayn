package com.thaqalayn.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PropheticStoriesManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.PropheticStory
import com.thaqalayn.app.model.StoryVerse
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
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

/** Prophetic story detail: header card, Quranic narrative verses, lessons, related stories (iOS StoryDetailView). */
@Composable
fun StoryDetailScreen(storyId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val story = remember(storyId) { PropheticStoriesManager.byId(storyId) } ?: return
    val relatedStories = remember(story) {
        story.relatedStories.mapNotNull { PropheticStoriesManager.byId(it) }
    }
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

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

            // Story header card
            EmCard(modifier = Modifier.fillMaxWidth()) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.accentChip)
                                .border(1.dp, colors.strokeColor, CircleShape)
                                .padding(horizontal = 13.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Icon(
                                storyCategoryIcon(story.category),
                                contentDescription = null,
                                tint = colors.accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = story.category.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentColor
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = story.prophet(lang).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (lang.isRTL) 0.sp else 2.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = story.title(lang),
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 30.sp,
                                lineHeight = 36.sp,
                                color = colors.primaryText
                            )
                        }
                    }
                }
            }

            // Quranic narrative intro
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmSectionLabel(icon = Icons.AutoMirrored.Filled.MenuBook, text = "Quranic Narrative")
                Text(
                    text = "This story is told through ${story.verseCount} verse${if (story.verseCount == 1) "" else "s"}:",
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    color = colors.secondaryText
                )
            }

            // Verses with story notes
            story.verses.forEach { storyVerse ->
                StoryVerseCard(storyVerse = storyVerse, lang = lang, scale = scale, navController = navController)
            }

            // Lessons summary
            val lessons = story.lessonsSummary(lang)
            if (!lessons.isNullOrBlank()) {
                LessonsCard(lessons = lessons, lang = lang, scale = scale)
            }

            // Related stories
            if (relatedStories.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EmSectionLabel(icon = Icons.Filled.Link, text = "Related Stories")
                    relatedStories.forEach { related ->
                        RelatedStoryCard(story = related, lang = lang) {
                            navController.navigate(Routes.story(related.id))
                        }
                    }
                }
            }
        }
    }
}

/** One story verse: reference row + recitation + Full Tafsir link, Arabic, translation, story note. */
@Composable
private fun StoryVerseCard(
    storyVerse: StoryVerse,
    lang: CommentaryLanguage,
    scale: Float,
    navController: NavHostController
) {
    val colors = Theme.colors
    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = storyVerse.surahNumber,
        key2 = storyVerse.verseNumber
    ) {
        val surah = DataManager.shared.surah(storyVerse.surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["${storyVerse.surahNumber}"]?.get("${storyVerse.verseNumber}") ?: return@produceState
        value = surah to verse
    }
    val surahName = loaded?.first?.englishName ?: "Surah ${storyVerse.surahNumber}"

    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$surahName · ${storyVerse.surahNumber}:${storyVerse.verseNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = colors.accentColor
                )
                if (storyVerse.isKeyVerse) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "KEY VERSE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.accentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                // Recitation (iOS VerseRecitationButton, size 32)
                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback != null &&
                    playback.surahNumber == storyVerse.surahNumber &&
                    playback.verseNumber == storyVerse.verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, verse) ->
                                AudioManager.playVerse(VerseWithTafsir(storyVerse.verseNumber, verse), surah)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Recite verse",
                        tint = colors.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Row(
                    modifier = Modifier.pressable {
                        navController.navigate(Routes.surah(storyVerse.surahNumber, storyVerse.verseNumber))
                    },
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
                // Verse translations exist only in English + Urdu; Arabic UI falls back to English.
                val translationIsRTL = lang == CommentaryLanguage.URDU
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (translationIsRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    Text(
                        text = if (translationIsRTL) verse.translationUrdu ?: verse.translation else verse.translation,
                        fontFamily = if (translationIsRTL) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Story note
            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accentChip.copy(alpha = colors.accentChip.alpha * 0.6f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = storyVerse.storyNote(lang),
                        fontFamily = if (lang == CommentaryLanguage.ENGLISH) null else AmiriFamily,
                        fontSize = (13 * scale).sp,
                        lineHeight = (13 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonsCard(lessons: String, lang: CommentaryLanguage, scale: Float) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmSectionLabel(icon = Icons.Filled.Lightbulb, text = "Lessons to Learn")
            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Text(
                    text = lessons,
                    fontFamily = if (lang == CommentaryLanguage.ENGLISH) CormorantFamily else AmiriFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = (17 * scale).sp,
                    lineHeight = (17 * scale * 1.5f).sp,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RelatedStoryCard(story: PropheticStory, lang: CommentaryLanguage, onClick: () -> Unit) {
    val colors = Theme.colors
    EmCard(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmIconChip(icon = storyCategoryIcon(story.category), size = 38.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = story.prophet(lang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = colors.accentColor
                )
                Text(
                    text = story.title(lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    color = colors.primaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.tertiaryText,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
