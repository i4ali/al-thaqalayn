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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.FastingVersesManager
import com.thaqalayn.app.model.AhlulBaytNarration
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.FastingVerse
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.EmSectionLabel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-in for the FastingCategory.icon SF Symbol. */
private fun sfIcon(name: String): ImageVector = when (name) {
    "book.fill" -> Icons.AutoMirrored.Filled.MenuBook
    "clock.fill" -> Icons.Filled.Schedule
    "heart.circle.fill" -> Icons.Filled.Favorite
    "sparkles" -> Icons.Filled.AutoAwesome
    else -> Icons.AutoMirrored.Filled.MenuBook
}

private fun fastingEyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "الصيام في القرآن"
    CommentaryLanguage.URDU -> "قرآن میں روزہ"
    else -> "Fasting in the Quran"
}

private fun narrationLabel(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "من أهل البيت (ع)"
    CommentaryLanguage.URDU -> "اہلِ بیتؑ سے"
    else -> "From the Ahlul Bayt (a)"
}

/** Fasting category detail: header, optional narration, verses (iOS FastingCategoryDetailView). */
@Composable
fun FastingCategoryDetailScreen(categoryId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val category = remember(categoryId) { FastingVersesManager.byId(categoryId) } ?: return
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

            // Category header card
            EmCard(modifier = Modifier.fillMaxWidth()) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EmIconChip(icon = sfIcon(category.icon), size = 56.dp)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = fastingEyebrow(lang).uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = if (lang.isRTL) 0.sp else 3.sp,
                                    color = colors.accentColor
                                )
                                Text(
                                    text = category.title(lang),
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 28.sp,
                                    lineHeight = 32.sp,
                                    color = colors.primaryText
                                )
                                Text(
                                    text = "${category.verseCount} verse" + if (category.verseCount == 1) "" else "s",
                                    fontSize = 13.sp,
                                    color = colors.secondaryText
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colors.dividerColor)
                        )
                        Text(
                            text = category.description(lang),
                            fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16 * scale).sp,
                            lineHeight = (16 * scale * 1.5f).sp,
                            color = colors.primaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            category.narration?.let { AhlulBaytNarrationCard(narration = it, lang = lang, scale = scale) }

            // Verses section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    EmSectionLabel(icon = Icons.Filled.AutoStories, text = "Verses")
                }
                category.verses.forEachIndexed { index, fastingVerse ->
                    FastingVerseCard(
                        fastingVerse = fastingVerse,
                        index = index + 1,
                        totalVerses = category.verseCount,
                        lang = lang,
                        scale = scale,
                        onNavigate = {
                            navController.navigate(Routes.surah(fastingVerse.surahNumber, fastingVerse.verseNumber))
                        }
                    )
                }
            }
        }
    }
}

// One verse: header (numeral, reference, recitation, key-verse chip), Arabic +
// translation, relevance note, tafsir CTA (iOS FastingVerseCard).
@Composable
private fun FastingVerseCard(
    fastingVerse: FastingVerse,
    index: Int,
    totalVerses: Int,
    lang: CommentaryLanguage,
    scale: Float,
    onNavigate: () -> Unit
) {
    val colors = Theme.colors

    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = fastingVerse.surahNumber,
        key2 = fastingVerse.verseNumber
    ) {
        val surah = DataManager.shared.surah(fastingVerse.surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["${fastingVerse.surahNumber}"]?.get("${fastingVerse.verseNumber}") ?: return@produceState
        value = surah to verse
    }
    val surahName = loaded?.first?.englishName ?: "Surah ${fastingVerse.surahNumber}"
    // Verse translations exist only in English + Urdu; Arabic UI falls back to English.
    val translationIsRTL = lang == CommentaryLanguage.URDU

    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Verse header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmNumeralCircle(n = index, size = 40.dp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Verse $index of $totalVerses",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.secondaryText
                    )
                    Text(
                        text = "$surahName · ${fastingVerse.surahNumber}:${fastingVerse.verseNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp,
                        color = colors.accentColor
                    )
                }

                // Recitation (iOS VerseRecitationButton, size 32)
                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback != null &&
                    playback.surahNumber == fastingVerse.surahNumber &&
                    playback.verseNumber == fastingVerse.verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, verse) ->
                                AudioManager.playVerse(VerseWithTafsir(fastingVerse.verseNumber, verse), surah)
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

                if (fastingVerse.isKeyVerse) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (translationIsRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    val translation =
                        if (translationIsRTL) verse.translationUrdu ?: verse.translation
                        else verse.translation
                    Text(
                        text = translation,
                        fontFamily = if (translationIsRTL) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Relevance note
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
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = fastingVerse.relevanceNote(lang),
                        fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else null,
                        fontSize = (13 * scale).sp,
                        lineHeight = (13 * scale * 1.4f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            EmGoldCTA(
                title = "Read Full Tafsir",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                small = true,
                onClick = onNavigate
            )
        }
    }
}

// "From the Ahlul Bayt (a)": attributed narration - Arabic + translation + source
// (iOS AhlulBaytNarrationCard).
@Composable
private fun AhlulBaytNarrationCard(narration: AhlulBaytNarration, lang: CommentaryLanguage, scale: Float) {
    val colors = Theme.colors
    val isUrdu = lang == CommentaryLanguage.URDU
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
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
                        text = narrationLabel(lang).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang.isRTL) 0.sp else 2.sp,
                        color = colors.accentColor
                    )
                }
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

            // Arabic readers read the narration itself; show a translation only otherwise.
            if (lang != CommentaryLanguage.ARABIC) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (isUrdu) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    Text(
                        text = narration.translation(lang),
                        fontFamily = if (isUrdu) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Text(
                    text = narration.source(lang),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
