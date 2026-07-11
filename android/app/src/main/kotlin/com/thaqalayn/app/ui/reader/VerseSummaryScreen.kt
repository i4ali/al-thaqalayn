package com.thaqalayn.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.VerseConcept
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.search.colorFromHex
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * "Gems" screen: quick overview of a verse - interactive concept gems plus the
 * short classical insight (iOS VerseSummaryView / QuickOverviewView).
 */
@Composable
fun VerseSummaryScreen(
    surahNumber: Int,
    verseNumber: Int,
    navController: NavHostController
) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale

    val loaded by produceState<Pair<Surah, VerseWithTafsir>?>(initialValue = null, key1 = surahNumber, key2 = verseNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        val data = DataManager.shared.loadSurahWithTafsir(surah)
        val verse = data.verses.firstOrNull { it.number == verseNumber } ?: return@produceState
        value = surah to verse
    }

    var expandedConceptId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        val data = loaded ?: return@Box
        val (surah, verse) = data
        val concepts = verse.tafsir?.quickOverview?.concepts.orEmpty()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmIconChip(icon = Icons.Filled.AutoAwesome, active = true)
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            "Gems",
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 26.sp,
                            color = colors.primaryText
                        )
                        Text("Precious insights unveiled", fontSize = 13.sp, color = colors.secondaryText)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.accentColor, modifier = Modifier.size(15.dp))
                    }
                }
            }

            // Verse reference + recitation
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmNumeralCircle(n = verse.number, size = 44.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            surah.englishName,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )
                        Text("Verse ${verse.number}", fontSize = 13.sp, color = colors.secondaryText)
                    }
                    val playback = AudioManager.currentPlayback
                    val isPlayingThis = playback?.surahNumber == surah.number &&
                        playback.verseNumber == verse.number &&
                        AudioManager.playerState == AudioPlayerState.PLAYING
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { AudioManager.playVerse(verse, surah) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Recite verse",
                            tint = colors.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Arabic verse
            item {
                val shape = RoundedCornerShape(18.dp)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(colors.glassSurface)
                            .border(1.dp, colors.strokeColor, shape)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = verse.arabicText,
                            fontFamily = AmiriFamily,
                            fontSize = (24 * scale).sp,
                            lineHeight = (24 * scale * 1.9f).sp,
                            color = colors.primaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Concept gems
            if (concepts.isNotEmpty()) {
                item { EmDivider(label = "Key Concepts") }
                items(concepts.size) { i ->
                    ConceptGemCard(
                        concept = concepts[i],
                        expanded = expandedConceptId == concepts[i].id,
                        onToggle = {
                            expandedConceptId = if (expandedConceptId == concepts[i].id) null else concepts[i].id
                        }
                    )
                }
            }

            // Core insight (short classical commentary)
            item { EmDivider(label = "The Core Insight") }
            item {
                val layer2Short = verse.tafsir?.getLayer2Short(lang)
                if (layer2Short != null) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    ) {
                        Text(
                            text = layer2Short,
                            fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (18 * scale).sp,
                            lineHeight = (18 * scale * 1.45f).sp,
                            color = colors.primaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Text(
                        "Overview not available for this verse.",
                        fontFamily = CormorantFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        color = colors.secondaryText
                    )
                }
            }

            item {
                EmGoldCTA(title = "Read In-Depth Commentary", icon = Icons.Filled.MenuBook) {
                    navController.popBackStack()
                    navController.navigate("commentary/$surahNumber/$verseNumber")
                }
            }
        }
    }
}

@Composable
private fun ConceptGemCard(
    concept: VerseConcept,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val gemColor = remember(concept.colorHex) { colorFromHex(concept.colorHex) }
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (expanded) 12.dp else 6.dp, shape, spotColor = gemColor.copy(alpha = 0.35f))
            .clip(shape)
            .background(colors.glassSurfaceElevated)
            .border(1.dp, if (expanded) gemColor.copy(alpha = 0.6f) else colors.strokeColor, shape)
            .pressable(depth = 0.97f, onClick = onToggle)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .shadow(4.dp, CircleShape, spotColor = gemColor.copy(alpha = 0.6f))
                    .clip(CircleShape)
                    .background(gemColor)
            )
            Text(
                text = concept.getTitle(lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = gemColor,
                modifier = Modifier.size(14.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = concept.getCoreInsight(lang),
                        fontSize = (15 * scale).sp,
                        lineHeight = (15 * scale * 1.5f).sp,
                        color = colors.primaryText
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "WHY IT MATTERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = gemColor
                        )
                        Text(
                            text = concept.getWhyItMatters(lang),
                            fontSize = (14 * scale).sp,
                            lineHeight = (14 * scale * 1.5f).sp,
                            color = colors.secondaryText
                        )
                    }
                }
            }
        }
    }
}
