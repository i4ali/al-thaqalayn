package com.thaqalayn.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
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
import com.thaqalayn.app.data.AhlulbaytQuranManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.model.AhlulbaytEntry
import com.thaqalayn.app.model.AhlulbaytVerse
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CommentaryLanguage
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

private fun screenEyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "أهل البيت في القرآن"
    CommentaryLanguage.URDU -> "قرآن میں اہلِ بیت"
    else -> "Ahl al-Bayt in the Quran"
}

/** Ahl al-Bayt entry detail: header, members, verses with context, revelation context, related entries (iOS AhlulbaytEntryDetailView). */
@Composable
fun AhlulbaytEntryDetailScreen(entryId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val entry = remember(entryId) { AhlulbaytQuranManager.byId(entryId) } ?: return
    val relatedEntries = remember(entry) {
        entry.relatedEntries.mapNotNull { AhlulbaytQuranManager.byId(it) }
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

            // Header card
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
                                ahlulbaytCategoryIcon(entry.category),
                                contentDescription = null,
                                tint = colors.accentColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = entry.category.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentColor
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(
                                text = screenEyebrow(lang).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (lang.isRTL) 0.sp else 3.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = entry.title(lang),
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

            // Ahl al-Bayt members
            val members = entry.ahlulbaytMembers(lang)
            if (members.isNotEmpty()) {
                MembersCard(members = members, direction = direction)
            }

            // Verses header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmSectionLabel(icon = Icons.AutoMirrored.Filled.MenuBook, text = "Quranic Reference")
                Text(
                    text = "This entry references ${entry.verseCount} verse${if (entry.verseCount == 1) "" else "s"}:",
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    color = colors.primaryText
                )
            }

            // Verses with context
            entry.verses.forEachIndexed { index, ahlulbaytVerse ->
                AhlulbaytVerseCard(
                    ahlulbaytVerse = ahlulbaytVerse,
                    index = index + 1,
                    totalVerses = entry.verseCount,
                    lang = lang,
                    scale = scale,
                    navController = navController
                )
            }

            // Revelation context
            EmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmSectionLabel(icon = Icons.Filled.Schedule, text = "Revelation Context")
                    CompositionLocalProvider(LocalLayoutDirection provides direction) {
                        Text(
                            text = entry.revelationContext(lang),
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

            // Related entries
            if (relatedEntries.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmSectionLabel(icon = Icons.Filled.Link, text = "Related Entries")
                    relatedEntries.forEach { related ->
                        RelatedAhlulbaytEntryCard(entry = related, lang = lang) {
                            navController.navigate(Routes.ahlulbaytEntry(related.id))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MembersCard(members: List<String>, direction: LayoutDirection) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmSectionLabel(icon = Icons.Filled.Groups, text = "Ahl al-Bayt Members")
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    members.forEach { member ->
                        Text(
                            text = member,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.accentColor,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.accentChip)
                                .border(1.dp, colors.strokeColor, CircleShape)
                                .padding(horizontal = 13.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

/** One referenced verse: numbered header + recitation + PRIMARY badge, Arabic, translation, context note, tafsir CTA. */
@Composable
private fun AhlulbaytVerseCard(
    ahlulbaytVerse: AhlulbaytVerse,
    index: Int,
    totalVerses: Int,
    lang: CommentaryLanguage,
    scale: Float,
    navController: NavHostController
) {
    val colors = Theme.colors
    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = ahlulbaytVerse.surahNumber,
        key2 = ahlulbaytVerse.verseNumber
    ) {
        val surah = DataManager.shared.surah(ahlulbaytVerse.surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["${ahlulbaytVerse.surahNumber}"]?.get("${ahlulbaytVerse.verseNumber}") ?: return@produceState
        value = surah to verse
    }
    val surahName = loaded?.first?.englishName ?: "Surah ${ahlulbaytVerse.surahNumber}"

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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmNumeralCircle(n = index, size = 38.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Verse $index of $totalVerses",
                        fontSize = 11.sp,
                        letterSpacing = 0.3.sp,
                        color = colors.tertiaryText
                    )
                    Text(
                        text = "$surahName · ${ahlulbaytVerse.surahNumber}:${ahlulbaytVerse.verseNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp,
                        color = colors.accentColor
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // Recitation (iOS VerseRecitationButton, size 32)
                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback != null &&
                    playback.surahNumber == ahlulbaytVerse.surahNumber &&
                    playback.verseNumber == ahlulbaytVerse.verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, verse) ->
                                AudioManager.playVerse(VerseWithTafsir(ahlulbaytVerse.verseNumber, verse), surah)
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

                if (ahlulbaytVerse.isPrimary) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PRIMARY",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.accentColor
                        )
                    }
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

            // Verse context
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
                        text = ahlulbaytVerse.context(lang),
                        fontFamily = if (lang == CommentaryLanguage.ENGLISH) null else AmiriFamily,
                        fontSize = (13 * scale).sp,
                        lineHeight = (13 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            EmGoldCTA(
                title = "Read Full Tafsir",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                small = true
            ) {
                navController.navigate(Routes.surah(ahlulbaytVerse.surahNumber, ahlulbaytVerse.verseNumber))
            }
        }
    }
}

@Composable
private fun RelatedAhlulbaytEntryCard(entry: AhlulbaytEntry, lang: CommentaryLanguage, onClick: () -> Unit) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmIconChip(icon = ahlulbaytCategoryIcon(entry.category), size = 38.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = entry.category.displayName.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = colors.accentColor
                )
                Text(
                    text = entry.title(lang),
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
