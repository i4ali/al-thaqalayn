package com.thaqalayn.app.ui.reader

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.audio.TafsirReader
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.TafsirLayer
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.TextSizeButton
import com.thaqalayn.app.ui.components.TextSizePanelOverlay
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Per-layer chip tones (iOS ThemeManager.chipFoundation etc.). */
data class LayerChip(val fg: Color, val icon: Int)

fun layerChip(layer: TafsirLayer): LayerChip = when (layer) {
    TafsirLayer.FOUNDATION -> LayerChip(Color(0xFF6FA5E8), R.drawable.ph_bank_fill)
    TafsirLayer.CLASSICAL -> LayerChip(Color(0xFFB8A6D9), R.drawable.ph_books_fill)
    TafsirLayer.CONTEMPORARY -> LayerChip(Color(0xFF6FD0A6), R.drawable.ph_globe_hemisphere_west_fill)
    TafsirLayer.AHLUL_BAYT -> LayerChip(Color(0xFFECD49A), R.drawable.ph_star_fill)
    TafsirLayer.COMPARATIVE -> LayerChip(Color(0xFFD69BB0), R.drawable.ph_scales_fill)
}

fun layerShortTitle(layer: TafsirLayer): String = when (layer) {
    TafsirLayer.FOUNDATION -> "Foundation"
    TafsirLayer.CLASSICAL -> "Classical"
    TafsirLayer.CONTEMPORARY -> "Modern"
    TafsirLayer.AHLUL_BAYT -> "Ahlul Bayt"
    TafsirLayer.COMPARATIVE -> "Comparative"
}

/**
 * Dedicated full-screen reading interface for tafsir commentary
 * (iOS FullScreenCommentaryView).
 */
@Composable
fun FullScreenCommentaryScreen(
    surahNumber: Int,
    verseNumber: Int,
    navController: NavHostController
) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    var selectedLayer by rememberSaveable { mutableStateOf(TafsirLayer.FOUNDATION) }
    var showTextSizePanel by remember { mutableStateOf(false) }

    val loaded by produceState<Pair<Surah, VerseWithTafsir>?>(initialValue = null, key1 = surahNumber, key2 = verseNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        val data = DataManager.shared.loadSurahWithTafsir(surah)
        val verse = data.verses.firstOrNull { it.number == verseNumber } ?: return@produceState
        value = surah to verse
    }

    // Stop TTS when the layer/language changes or the screen closes.
    LaunchedEffect(selectedLayer, lang) { TafsirReader.stop() }
    DisposableEffect(Unit) {
        onDispose { TafsirReader.stop() }
    }

    TextSizePanelOverlay(isOpen = showTextSizePanel, onDismiss = { showTextSizePanel = false }) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThemedBackground()
            val data = loaded
            if (data != null) {
                val (surah, verse) = data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, colors.strokeColor, CircleShape)
                                    .pressable { navController.popBackStack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.accentColor, modifier = Modifier.size(16.dp))
                            }
                            TextSizeButton(isOpen = showTextSizePanel) { showTextSizePanel = !showTextSizePanel }
                        }
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                "Commentary",
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                color = colors.primaryText
                            )
                            Text(
                                "${surah.englishName} · Verse ${verse.number}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.tertiaryText
                            )
                        }
                    }

                    // Layer selector
                    val layerListState = rememberLazyListState()
                    LaunchedEffect(Unit) {
                        layerListState.animateScrollToItem(TafsirLayer.entries.indexOf(selectedLayer))
                    }
                    LazyRow(
                        state = layerListState,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        items(TafsirLayer.entries.size) { i ->
                            val layer = TafsirLayer.entries[i]
                            LayerButton(
                                layer = layer,
                                selected = selectedLayer == layer,
                                locked = !PremiumManager.canAccessLayer(layer, surah.number),
                                onClick = {
                                    if (!PremiumManager.canAccessLayer(layer, surah.number)) {
                                        navController.navigate(Routes.PAYWALL)
                                    } else {
                                        selectedLayer = layer
                                    }
                                }
                            )
                        }
                    }

                    // Reading content
                    val tafsir = verse.tafsir
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 24.dp, end = 24.dp, bottom = 60.dp
                        )
                    ) {
                        if (tafsir != null) {
                            val text = tafsir.content(selectedLayer, lang)
                            item {
                                LayerHeader(
                                    layer = selectedLayer,
                                    onTts = {
                                        if (TafsirReader.isPlaying || TafsirReader.isPaused) {
                                            TafsirReader.togglePlayPause()
                                        } else {
                                            TafsirReader.speak(text, lang)
                                        }
                                    }
                                )
                            }
                            val paragraphs = formattedParagraphs(text)
                            items(paragraphs.size) { index ->
                                ParagraphCard(
                                    paragraphs = paragraphs,
                                    index = index,
                                    isRTL = lang.isRTL
                                )
                            }
                        } else {
                            item { NoCommentary(selectedLayer) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerButton(
    layer: TafsirLayer,
    selected: Boolean,
    locked: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val chip = layerChip(layer)
    val active = selected && !locked
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .width(84.dp)
            .let {
                if (active) {
                    it.shadow(12.dp, shape, spotColor = colors.accentColor.copy(alpha = 0.3f))
                        .clip(shape)
                        .background(colors.accentGradient)
                } else {
                    it.clip(shape)
                        .background(colors.glassSurface)
                        .border(1.dp, colors.strokeColor, shape)
                }
            }
            .pressable(onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (active) Color.Black.copy(alpha = 0.20f) else chip.fg.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            PhosphorIcon(
                resId = chip.icon,
                size = 17.dp,
                tint = if (active) Color.White.copy(alpha = 0.95f) else chip.fg.copy(alpha = if (locked) 0.45f else 1f)
            )
        }
        Text(
            text = layerShortTitle(layer),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = when {
                active -> colors.onAccentText
                locked -> colors.tertiaryText
                else -> colors.primaryText
            }
        )
    }
}

@Composable
private fun LayerHeader(layer: TafsirLayer, onTts: () -> Unit) {
    val colors = Theme.colors
    val chip = layerChip(layer)
    Column(modifier = Modifier.padding(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(chip.fg.copy(alpha = 0.15f))
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(resId = chip.icon, size = 22.dp, tint = chip.fg)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = layer.title,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = colors.primaryText
                )
                Text(
                    text = layer.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                    color = colors.secondaryText
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            if (TafsirReader.hasVoiceAvailable(CommentaryLanguageManager.selectedLanguage)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.accentColor.copy(alpha = 0.1f))
                        .pressable(onClick = onTts),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (TafsirReader.isPlaying) Icons.Filled.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = colors.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        EmDivider()
    }
}

@Composable
private fun ParagraphCard(paragraphs: List<String>, index: Int, isRTL: Boolean) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale
    val shape = RoundedCornerShape(18.dp)
    val paragraph = paragraphs[index]

    val highlight = if (TafsirReader.isPlaying || TafsirReader.isPaused) {
        highlightRangeForParagraph(paragraph, index, paragraphs, TafsirReader.highlightRange)
    } else null

    val annotated = buildAnnotatedString {
        append(paragraph)
        if (highlight != null && highlight.first >= 0 && highlight.last < paragraph.length) {
            addStyle(
                SpanStyle(background = colors.accentColor.copy(alpha = 0.28f)),
                highlight.first,
                highlight.last + 1
            )
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(shape)
                .background(colors.glassSurface)
                .border(1.dp, colors.strokeColor, shape)
                .padding(18.dp)
        ) {
            Text(
                text = annotated,
                fontFamily = if (isRTL) com.thaqalayn.app.ui.theme.AmiriFamily else CormorantFamily,
                fontWeight = if (isRTL) FontWeight.Normal else FontWeight.Medium,
                fontSize = ((if (isRTL) 20 else 19) * scale).sp,
                lineHeight = ((if (isRTL) 20 * 1.7f else 19 * 1.45f) * scale).sp,
                color = colors.primaryText,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NoCommentary(layer: TafsirLayer) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PhosphorIcon(resId = R.drawable.ph_book_open, size = 64.dp, tint = colors.tertiaryText.copy(alpha = 0.6f))
        Text("No commentary available", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = colors.secondaryText)
        Text("for ${layer.title}", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = colors.tertiaryText)
        Text(
            "Try selecting a different commentary layer above.",
            fontSize = 16.sp,
            color = colors.tertiaryText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Split tafsir text into readable paragraphs: 2-3 sentences each, breaking early
 * at transition words (iOS formattedParagraphs).
 */
fun formattedParagraphs(text: String): List<String> {
    val sentences = text.split(". ")
    val paragraphs = mutableListOf<String>()
    var current = ""

    for ((index, sentence) in sentences.withIndex()) {
        val trimmed = sentence.trim()
        if (trimmed.isEmpty()) continue
        current = if (current.isEmpty()) trimmed else "$current. $trimmed"

        val sentenceCount = current.split(". ").size
        val isLast = index == sentences.size - 1
        val hasNaturalBreak = listOf(
            "However", "Furthermore", "Additionally", "In contrast", "Therefore", "Moreover", "Nevertheless"
        ).any { trimmed.contains(it) }

        if (sentenceCount >= 3 || hasNaturalBreak || isLast) {
            paragraphs.add(current + if (isLast) "" else ".")
            current = ""
        }
    }
    return paragraphs.ifEmpty { listOf(text) }
}

/** Map a highlight range within the full text onto one paragraph (iOS logic). */
fun highlightRangeForParagraph(
    paragraphText: String,
    paragraphIndex: Int,
    allParagraphs: List<String>,
    fullHighlightRange: IntRange?
): IntRange? {
    val highlight = fullHighlightRange ?: return null

    var paragraphStart = 0
    for (i in 0 until paragraphIndex) {
        paragraphStart += allParagraphs[i].length + 2 // ". " separator
    }
    val paragraphEnd = paragraphStart + paragraphText.length

    if (highlight.first >= paragraphEnd || highlight.last + 1 <= paragraphStart) return null

    val adjustedStart = maxOf(0, highlight.first - paragraphStart)
    val adjustedEnd = minOf(paragraphText.length, highlight.last + 1 - paragraphStart)
    return if (adjustedEnd > adjustedStart) adjustedStart until adjustedEnd else null
}
