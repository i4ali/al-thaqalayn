package com.thaqalayn.app.ui.deepdive

// The immersive "descent" (iOS DeepDiveView.swift): a full-screen vertical
// pager that renders one DeepDive's sections one screen at a time, over the
// progress-driven DeepDiveBackground. Keeps its own fixed cinematic dark
// palette regardless of the app theme - it is an immersive mode, like a film.
// Reuses the app's real audio (AudioManager verse recitation, DuaListenButton)
// and the reading-size control for body prose.

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.components.CoverVeil
import com.thaqalayn.app.ui.components.DuaListenButton
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Path

private val romans = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII")

/** Safe roman numeral - falls back to the raw number, so it can never crash. */
private fun roman(n: Int): String = if (n in romans.indices) romans[n] else "$n"

@Composable
fun DeepDiveScreen(
    dive: DeepDive,
    onClose: () -> Unit,
    /**
     * Present on surah experiences: invoked by the closing beat's
     * "Read the full surah" button. null hides the button (theme dives).
     */
    onReadSurah: (() -> Unit)? = null,
    /** The entry's premium cover: threshold doorway + locked veil. */
    coverRes: Int? = null,
    /**
     * True for a premium descent opened by a non-subscriber: only the opening
     * beats render, then the veil beat with the upgrade CTA ([onUnlock]).
     */
    locked: Boolean = false,
    onUnlock: () -> Unit = {}
) {
    val lang = CommentaryLanguageManager.selectedLanguage
    // Non-subscribers preview the opening beats; the veil is the final beat.
    val previewCount = minOf(2, dive.sections.size)
    val pageCount = if (locked) previewCount + 1 else dive.sections.size
    val pagerState = rememberPagerState(
        // Purchase mid-descent grows pageCount live; clamp against both.
        pageCount = { if (locked) previewCount + 1 else dive.sections.size }
    )
    val scope = rememberCoroutineScope()
    // First depth starts open so the "tap to open" gesture is obvious.
    var openDepths by remember { mutableStateOf(setOf(0)) }
    var saidAmin by remember { mutableStateOf(false) }

    val pagesScrolled = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val progress = (pagesScrolled / max(pageCount - 1, 1).toFloat()).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        DeepDiveBackground(progress = progress)

        // The cover is the doorway behind the opening beat: it dissolves as
        // the reader sinks past the first beat (iOS DeepDiveView threshold).
        if (coverRes != null) {
            val thresholdAlpha = (1f - pagesScrolled / 0.85f).coerceIn(0f, 1f)
            if (thresholdAlpha > 0f) {
                ThresholdCover(art = coverRes, coverAlpha = thresholdAlpha)
            }
        }

        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            if (locked && index >= previewCount) {
                DescentVeilPage(
                    art = coverRes,
                    dive = dive,
                    lang = lang,
                    show = pagerState.currentPage >= index,
                    onUnlock = onUnlock
                )
                return@VerticalPager
            }
            val section = dive.sections[index]
            val show = pagerState.currentPage >= index
            DivePage(dive, section, show, lang,
                onReadSurah = onReadSurah,
                onClose = onClose,
                openDepths = openDepths,
                onToggleDepth = { di ->
                    openDepths = if (di in openDepths) openDepths - di else openDepths + di
                },
                saidAmin = saidAmin,
                onSayAmin = { saidAmin = true },
                onBeginAgain = {
                    saidAmin = false
                    openDepths = setOf(0)
                    scope.launch { pagerState.animateScrollToPage(0) }
                }
            )
        }

        // Progress hairline pinned to the very top edge.
        Box(modifier = Modifier.fillMaxWidth().height(2.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.04f)))
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(DeepDivePalette.gold, DeepDivePalette.goldBright)
                        )
                    )
            )
        }

        // Close chevron.
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.25f), CircleShape)
                .pressable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Close",
                tint = DeepDivePalette.cream,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// MARK: - One beat (page dispatch + centering scaffold)

/**
 * One beat. Content is centered when it fits the screen and scrolls when it
 * overflows (e.g. at the largest reading size) - nothing is ever clipped. The
 * outer pager still snaps one beat per screen.
 */
@Composable
private fun DivePage(
    dive: DeepDive,
    section: DeepDiveSection,
    show: Boolean,
    lang: CommentaryLanguage,
    onReadSurah: (() -> Unit)?,
    onClose: () -> Unit,
    openDepths: Set<Int>,
    onToggleDepth: (Int) -> Unit,
    saidAmin: Boolean,
    onSayAmin: () -> Unit,
    onBeginAgain: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val viewportHeight = maxHeight
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = viewportHeight)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 30.dp)
                        .padding(top = 58.dp)
                ) {
                    PlaceBar(dive, section, show, lang)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(max = 480.dp)
                        .statusBarsPadding()
                        .padding(horizontal = 30.dp)
                        .padding(top = 90.dp, bottom = 40.dp)
                ) {
                    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    androidx.compose.runtime.CompositionLocalProvider(
                        LocalLayoutDirection provides direction
                    ) {
                        when (section) {
                            is DeepDiveSection.Open -> OpenPage(dive, section, show, lang)
                            is DeepDiveSection.Orientation -> OrientationPage(dive, section, show, lang)
                            is DeepDiveSection.Verse -> VersePage(section, show, lang)
                            is DeepDiveSection.Depths -> DepthsPage(dive, section, show, lang, openDepths, onToggleDepth)
                            is DeepDiveSection.Act -> ActPage(dive, section, show, lang)
                            is DeepDiveSection.Narration -> NarrationPage(section, show, lang)
                            is DeepDiveSection.Response -> ResponsePage(section, show, lang)
                            is DeepDiveSection.Climax -> ClimaxPage(section, show, lang)
                            is DeepDiveSection.Refrain -> RefrainPage(section, show, lang)
                            is DeepDiveSection.ReflectionPrompt -> ReflectionPage(section, show, lang)
                            is DeepDiveSection.Release -> ReleasePage(section, show, lang)
                            is DeepDiveSection.Count -> CountPage(section, show, lang)
                            is DeepDiveSection.Sujud -> SujudPage(section, show, lang)
                            is DeepDiveSection.Extinguish -> ExtinguishPage(section, show, lang)
                            is DeepDiveSection.Door -> DoorPage(section, show, lang)
                            is DeepDiveSection.Salawat -> SalawatPage(section, show, lang)
                            is DeepDiveSection.Dua -> DuaPage(dive, section, show, lang, saidAmin, onSayAmin, onBeginAgain)
                            is DeepDiveSection.Closing -> ClosingPage(section, show, lang, onReadSurah, onClose)
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Place-label + depth meter (orientation)

/**
 * The "where am I" label + how many depth dots to fill, per section.
 * null = no bar (cover, orientation, and the movement dividers).
 */
private fun placeInfo(dive: DeepDive, section: DeepDiveSection, lang: CommentaryLanguage): Pair<String, Int>? =
    when (section) {
        is DeepDiveSection.Open, is DeepDiveSection.Orientation, is DeepDiveSection.Act -> null
        is DeepDiveSection.ReflectionPrompt -> "The Return" to dive.acts.size
        is DeepDiveSection.Dua, is DeepDiveSection.Closing -> "The Close" to dive.acts.size
        is DeepDiveSection.Release -> section.tag.text(lang) to dive.acts.size
        is DeepDiveSection.Count -> section.tag.text(lang) to dive.acts.size
        is DeepDiveSection.Sujud -> section.tag.text(lang) to dive.acts.size
        is DeepDiveSection.Extinguish -> section.tag.text(lang) to dive.acts.size
        is DeepDiveSection.Door -> section.tag.text(lang) to dive.acts.size
        is DeepDiveSection.Salawat -> section.tag.text(lang) to dive.acts.size
        else -> {
            val a = section.actNumber
            dive.actInfo(a)?.let { info -> "Movement ${roman(a)} · ${info.name.text(lang)}" to a }
        }
    }

@Composable
private fun PlaceBar(dive: DeepDive, section: DeepDiveSection, show: Boolean, lang: CommentaryLanguage) {
    val info = placeInfo(dive, section, lang) ?: return
    Reveal(show) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = info.first.uppercase(),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                color = DeepDivePalette.gold,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(dive.acts.size) { i ->
                    val filled = i < info.second
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (filled) DeepDivePalette.gold else Color.Transparent)
                            .border(
                                if (filled) 0.dp else 1.dp,
                                DeepDivePalette.gold.copy(alpha = 0.5f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

// MARK: - Shared bits

/** Fade + rise reveal, matching the iOS/JSX `reveal()`. */
@Composable
private fun Reveal(
    shown: Boolean,
    delay: Double = 0.0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(850, delayMillis = (delay * 1000).toInt(), easing = EaseOut),
        label = "revealAlpha"
    )
    val rise by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (shown) 0f else 24f,
        animationSpec = tween(850, delayMillis = (delay * 1000).toInt(), easing = EaseOut),
        label = "revealRise"
    )
    Box(modifier = modifier.graphicsLayer { this.alpha = alpha; translationY = rise * density }) {
        content()
    }
}

@Composable
private fun TagLabel(text: String, show: Boolean, delay: Double = 0.06) {
    Reveal(show, delay) {
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center,
            color = DeepDivePalette.cream
        )
    }
}

@Composable
private fun Hairline() {
    Box(modifier = Modifier.width(26.dp).height(1.dp).background(DeepDivePalette.gold.copy(alpha = 0.3f)))
}

/** The "keep going" bob: a small label + chevron at a beat's foot. */
@Composable
private fun Bob(label: String, show: Boolean, delay: Double = 1.0) {
    Reveal(show, delay) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 10.5.sp,
                letterSpacing = 3.sp,
                color = DeepDivePalette.mute
            )
            Icon(
                Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = DeepDivePalette.gold,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Serif display text (iOS EmType.serif). */
@Composable
private fun SerifText(
    text: String,
    size: Float,
    color: Color,
    modifier: Modifier = Modifier,
    italic: Boolean = false,
    lineSpacing: Float = 0f,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Medium,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        fontSize = size.sp,
        lineHeight = (size + lineSpacing + if (italic) 4f else 6f).sp,
        textAlign = textAlign,
        color = color,
        modifier = modifier
    )
}

/** Qur'anic Arabic text, always right-to-left (iOS EmType.arabic). */
@Composable
private fun ArabicText(
    text: String,
    size: Float,
    color: Color,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
    lineSpacing: Float = 8f
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Text(
            text = text,
            fontFamily = AmiriFamily,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = size.sp,
            lineHeight = (size + lineSpacing).sp,
            textAlign = TextAlign.Center,
            color = color,
            modifier = modifier
        )
    }
}

/**
 * Round verse recitation play button + "Hear it recited" caption (iOS
 * VerseRecitationButton) - plays the real recitation via AudioManager.
 */
@Composable
private fun VerseRecitation(surahNumber: Int, ayah: Int, show: Boolean) {
    val loaded by produceState<Pair<Surah, Verse>?>(initialValue = null, surahNumber, ayah) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["$surahNumber"]?.get("$ayah") ?: return@produceState
        value = surah to verse
    }
    val playback = AudioManager.currentPlayback
    val isPlayingThis = playback?.surahNumber == surahNumber &&
        playback.verseNumber == ayah &&
        AudioManager.playerState == AudioPlayerState.PLAYING

    Reveal(show, 0.95) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DeepDivePalette.gold.copy(alpha = 0.12f))
                    .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.4f), CircleShape)
                    .alpha(if (loaded == null) 0.4f else 1f)
                    .pressable {
                        loaded?.let { (surah, verse) ->
                            AudioManager.playVerse(VerseWithTafsir(ayah, verse), surah)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Recite verse",
                    tint = DeepDivePalette.goldBright,
                    modifier = Modifier.size(17.dp)
                )
            }
            Text(
                text = "Hear it recited",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                color = DeepDivePalette.gold.copy(alpha = 0.7f)
            )
        }
    }
}

// MARK: - Renderers

@Composable
private fun OpenPage(dive: DeepDive, s: DeepDiveSection.Open, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show) {
            Text(
                text = s.kicker.text(lang).uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Reveal(show, 0.25) { ArabicText(s.titleAr, 72f, DeepDivePalette.goldBright) }
        Spacer(modifier = Modifier.height(14.dp))
        Reveal(show, 0.5) { SerifText(s.titleEn, 44f, DeepDivePalette.cream) }
        Spacer(modifier = Modifier.height(8.dp))
        Reveal(show, 0.5) {
            Text(
                text = s.subtitle.text(lang).uppercase(),
                fontSize = 12.sp,
                letterSpacing = 5.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute
            )
        }
        Reveal(show, 0.78) { Box(modifier = Modifier.padding(vertical = 30.dp)) { Hairline() } }
        Reveal(show, 0.78) {
            SerifText(
                s.line.text(lang), 18f * scale, Color(0xFFB8B8B8), italic = true,
                lineSpacing = 5f * scale, modifier = Modifier.widthIn(max = 320.dp)
            )
        }
        Spacer(modifier = Modifier.height(44.dp))
        Bob(dive.descendCta, show)
    }
}

@Composable
private fun OrientationPage(dive: DeepDive, s: DeepDiveSection.Orientation, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show) {
            Text(
                text = s.eyebrow.text(lang).uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Reveal(show, 0.2) {
            SerifText(
                s.promise.text(lang), 22f * scale, DeepDivePalette.cream, italic = true,
                lineSpacing = 5f * scale, modifier = Modifier.widthIn(max = 320.dp)
            )
        }
        Reveal(show, 0.35) { Box(modifier = Modifier.padding(vertical = 24.dp)) { Hairline() } }
        Reveal(show, 0.5) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HintRow(if (dive.scrollHintAscending) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward, dive.scrollHint)
                HintRow(Icons.Filled.TouchApp, "Tap what draws you")
                HintRow(Icons.Filled.EditNote, "Reflect at the end")
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Reveal(show, 0.7) {
            Text(
                text = s.leaveWith.text(lang),
                fontSize = 13.sp * scale,
                lineHeight = 17.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 250.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Bob(dive.beginCta, show, 0.9)
    }
}

@Composable
private fun HintRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        Icon(icon, contentDescription = null, tint = DeepDivePalette.gold, modifier = Modifier.size(16.dp))
        Text(text = text, fontSize = 13.sp, color = DeepDivePalette.mute)
    }
}

@Composable
private fun VersePage(s: DeepDiveSection.Verse, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TagLabel(s.tag.text(lang), show)
        Spacer(modifier = Modifier.height(30.dp))
        Reveal(show, 0.24) {
            ArabicText(s.arabic, 26f * scale, DeepDivePalette.cream, lineSpacing = 14f * scale)
        }
        val translation = s.translation.text(lang)
        if (translation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(26.dp))
            Reveal(show, 0.55) {
                SerifText(
                    translation, 20f * scale, Color(0xFFCCCCCC), italic = true,
                    lineSpacing = 4f * scale, modifier = Modifier.widthIn(max = 400.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Reveal(show, 0.55) {
            Text(
                text = s.reference,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = DeepDivePalette.gold.copy(alpha = 0.85f)
            )
        }
        Reveal(show, 0.9) {
            Box(modifier = Modifier.padding(top = 28.dp, bottom = 22.dp)) { Hairline() }
        }
        Reveal(show, 0.9) {
            Text(
                text = s.reflection.text(lang),
                fontSize = 15.sp * scale,
                lineHeight = 21.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        VerseRecitation(s.surah, s.ayah, show)
    }
}

@Composable
private fun DepthsPage(
    dive: DeepDive,
    s: DeepDiveSection.Depths,
    show: Boolean,
    lang: CommentaryLanguage,
    openDepths: Set<Int>,
    onToggleDepth: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show, 0.06) { SerifText(s.tag.text(lang), 28f, DeepDivePalette.cream) }
        Spacer(modifier = Modifier.height(4.dp))
        Reveal(show, 0.12) {
            SerifText(dive.mapLine, 15f, DeepDivePalette.mute, italic = true)
        }
        Reveal(show, 0.2) {
            Row(
                modifier = Modifier.padding(top = 14.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.width(16.dp).height(1.dp).background(DeepDivePalette.goldBright.copy(alpha = 0.4f)))
                Text(
                    text = "Tap each to open",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.6.sp,
                    color = DeepDivePalette.goldBright
                )
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = DeepDivePalette.goldBright,
                    modifier = Modifier.size(14.dp)
                )
                Box(modifier = Modifier.width(16.dp).height(1.dp).background(DeepDivePalette.goldBright.copy(alpha = 0.4f)))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            s.items.forEachIndexed { di, d ->
                Reveal(show, 0.3 + di * 0.16) {
                    DepthCard(di, d, di in openDepths, lang) { onToggleDepth(di) }
                }
            }
        }
    }
}

@Composable
private fun DepthCard(di: Int, d: Depth, open: Boolean, lang: CommentaryLanguage, onTap: () -> Unit) {
    val scale = ReadingSettingsManager.scale
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (open) DeepDivePalette.goldBright.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.022f))
            .border(
                1.dp,
                if (open) DeepDivePalette.goldBright.copy(alpha = 0.34f) else DeepDivePalette.gold.copy(alpha = 0.16f),
                shape
            )
            .pressable(onClick = onTap)
            .animateContentSize(animationSpec = tween(450))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SerifText("${roman(di + 1)} · ${d.tr}", 17f, DeepDivePalette.cream, textAlign = TextAlign.Start)
                Text(text = d.label.text(lang), fontSize = 11.sp, color = DeepDivePalette.mute)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = d.ar,
                fontFamily = AmiriFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (open) DeepDivePalette.goldBright else DeepDivePalette.gold
            )
        }
        if (open) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(1.dp)
                    .background(DeepDivePalette.gold.copy(alpha = 0.22f))
            )
            SerifText(
                d.desc.text(lang), 16f * scale, Color(0xFFCCCCCC), italic = true,
                lineSpacing = 3f * scale, textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "→ ${d.embodies.text(lang)}".uppercase(),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                color = DeepDivePalette.gold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ActPage(dive: DeepDive, s: DeepDiveSection.Act, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val info = dive.actInfo(s.act)
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        s.connector?.let { connector ->
            Reveal(show) {
                Text(
                    text = connector.text(lang),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.mute
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        Reveal(show, 0.1) {
            Text(
                text = dive.stageWord.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 6.sp,
                color = DeepDivePalette.gold
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Reveal(show, 0.2) {
            SerifText(roman(s.act), 80f, DeepDivePalette.goldBright.copy(alpha = 0.28f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Reveal(show, 0.36) { ArabicText(info?.ar ?: "", 40f, DeepDivePalette.goldBright, bold = true) }
        Spacer(modifier = Modifier.height(6.dp))
        Reveal(show, 0.36) { SerifText(info?.tr ?: "", 26f, DeepDivePalette.cream) }
        Spacer(modifier = Modifier.height(8.dp))
        Reveal(show, 0.36) {
            Text(
                text = "${info?.name?.text(lang) ?: ""} · ${dive.stageNoun} ${s.act} of ${dive.acts.size}".uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.4.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute
            )
        }
        s.bridge?.let { b ->
            Spacer(modifier = Modifier.height(26.dp))
            Reveal(show, 0.6) { BridgeVerseCard(b, lang) }
        }
        val lineDelay = if (s.bridge == null) 0.6 else 0.85
        Reveal(show, lineDelay) {
            Box(modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)) { Hairline() }
        }
        Reveal(show, lineDelay) {
            SerifText(
                s.line.text(lang), 18f * scale, Color(0xFFB8B8B8), italic = true,
                lineSpacing = 5f * scale, modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Bob("Continue", show, 1.1)
    }
}

@Composable
private fun BridgeVerseCard(b: BridgeVerse, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .widthIn(max = 380.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.02f))
            .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.16f), shape)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ArabicText(b.arabic, 22f * scale, DeepDivePalette.cream, lineSpacing = 9f * scale)
        val translation = b.translation.text(lang)
        if (translation.isNotEmpty()) {
            SerifText(translation, 16f * scale, Color(0xFFCCCCCC), italic = true)
        }
        Text(
            text = b.reference,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            color = DeepDivePalette.gold.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun NarrationPage(s: DeepDiveSection.Narration, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TagLabel(s.tag.text(lang), show)
        Spacer(modifier = Modifier.height(28.dp))
        Reveal(show, 0.25) {
            SerifText(s.body.text(lang), 21f * scale, DeepDivePalette.cream, lineSpacing = 8f * scale)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Reveal(show, 0.8) {
            Text(
                text = s.source.text(lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold.copy(alpha = 0.75f)
            )
        }
        Reveal(show, 1.05) {
            Box(modifier = Modifier.padding(top = 26.dp, bottom = 20.dp)) { Hairline() }
        }
        Reveal(show, 1.05) {
            SerifText(
                s.reflection.text(lang), 16f * scale, DeepDivePalette.mute, italic = true,
                lineSpacing = 4f * scale, modifier = Modifier.widthIn(max = 330.dp)
            )
        }
    }
}

/**
 * The hadith-qudsi reply. God's answer to the line just recited, staged as a
 * call-and-response: a thread of light descends, a fixed "He answers" eyebrow,
 * then His words glow.
 */
@Composable
private fun ResponsePage(s: DeepDiveSection.Response, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show, 0.06) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(22.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DeepDivePalette.goldBright.copy(alpha = 0.7f),
                                DeepDivePalette.goldBright.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Reveal(show, 0.12) {
            Text(
                text = "He Answers".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                color = DeepDivePalette.goldBright
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Reveal(show, 0.18) {
            Text(
                text = s.replyingTo.text(lang).uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute
            )
        }
        if (s.arabic.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Reveal(show, 0.32) {
                ArabicText(s.arabic, 23f * scale, DeepDivePalette.goldBright, bold = true)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Reveal(show, 0.52) {
            SerifText(
                s.words.text(lang), 25f * scale, DeepDivePalette.cream, italic = true,
                lineSpacing = 6f * scale, modifier = Modifier.widthIn(max = 320.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Reveal(show, 0.82) {
            Text(
                text = s.source.text(lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold.copy(alpha = 0.8f)
            )
        }
        Reveal(show, 1.0) {
            Box(modifier = Modifier.padding(top = 26.dp, bottom = 20.dp)) { Hairline() }
        }
        Reveal(show, 1.0) {
            Text(
                text = s.reflection.text(lang),
                fontSize = 15.sp * scale,
                lineHeight = 21.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 330.dp)
            )
        }
    }
}

@Composable
private fun ClimaxPage(s: DeepDiveSection.Climax, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TagLabel(s.tag.text(lang), show)
        Spacer(modifier = Modifier.height(26.dp))
        Reveal(show, 0.2) {
            Text(
                text = s.body.text(lang),
                fontSize = 15.sp * scale,
                lineHeight = 21.sp * scale,
                textAlign = TextAlign.Center,
                color = Color(0xFFA8A8A8),
                modifier = Modifier.widthIn(max = 360.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Reveal(show, 0.65) {
            ArabicText(s.arabic, 30f * scale, DeepDivePalette.goldBright, bold = true)
        }
        val translation = s.translation.text(lang)
        if (translation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(22.dp))
            Reveal(show, 1.0) {
                SerifText(translation, 22f * scale, DeepDivePalette.cream, italic = true)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Reveal(show, 1.0) {
            Text(
                text = s.source.text(lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold.copy(alpha = 0.8f)
            )
        }
        Reveal(show, 1.35) {
            Box(modifier = Modifier.padding(top = 28.dp, bottom = 22.dp)) { Hairline() }
        }
        Reveal(show, 1.35) {
            Text(
                text = s.reflection.text(lang),
                fontSize = 15.sp * scale,
                lineHeight = 21.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 340.dp)
            )
        }
    }
}

@Composable
private fun ReflectionPage(s: DeepDiveSection.ReflectionPrompt, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show) {
            Text(text = "✦", fontSize = 20.sp, color = DeepDivePalette.gold)
        }
        Spacer(modifier = Modifier.height(22.dp))
        Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream) }
        Spacer(modifier = Modifier.height(16.dp))
        Reveal(show, 0.35) {
            SerifText(
                s.subline.text(lang), 16f * scale, Color(0xFFA8A8A8), italic = true,
                lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Spacer(modifier = Modifier.height(34.dp))
        Bob(s.nextLabel.text(lang), show)
    }
}

@Composable
private fun DuaPage(
    dive: DeepDive,
    s: DeepDiveSection.Dua,
    show: Boolean,
    lang: CommentaryLanguage,
    saidAmin: Boolean,
    onSayAmin: () -> Unit,
    onBeginAgain: () -> Unit
) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Reveal(show) {
            Text(
                text = s.tag.text(lang).uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.4.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Reveal(show, 0.15) {
            SerifText(
                s.intro.text(lang), 16f * scale, Color(0xFFA8A8A8), italic = true,
                lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Spacer(modifier = Modifier.height(26.dp))
        Reveal(show, 0.38) {
            ArabicText(s.arabic, 24f * scale, DeepDivePalette.cream, lineSpacing = 14f * scale)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Reveal(show, 0.5) { DuaListenButton(arabic = s.arabic) }
        val translation = s.translation.text(lang)
        if (translation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(22.dp))
            Reveal(show, 0.72) {
                SerifText(
                    translation, 19f * scale, Color(0xFFCCCCCC), italic = true,
                    lineSpacing = 4f * scale, modifier = Modifier.widthIn(max = 400.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Reveal(show, 0.72) {
            Text(
                text = s.source.text(lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.gold.copy(alpha = 0.85f)
            )
        }
        Reveal(show, 0.98) {
            Box(modifier = Modifier.padding(top = 24.dp, bottom = 18.dp)) { Hairline() }
        }
        Reveal(show, 0.98) {
            Text(
                text = s.note.text(lang),
                fontSize = 14.sp * scale,
                lineHeight = 20.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 350.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        AminBlock(dive.endLine, s.close.text(lang), show, saidAmin, onSayAmin, onBeginAgain, scale)
    }
}

@Composable
private fun AminBlock(
    endLine: String,
    close: String,
    show: Boolean,
    saidAmin: Boolean,
    onSayAmin: () -> Unit,
    onBeginAgain: () -> Unit,
    scale: Float
) {
    if (!saidAmin) {
        Reveal(show, 1.25) {
            Column(
                modifier = Modifier.pressable(onClick = onSayAmin),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "آمِين",
                    fontFamily = AmiriFamily,
                    fontSize = 34.sp,
                    color = DeepDivePalette.goldBright
                )
                Text(
                    text = "Tap to say Amin",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp,
                    color = DeepDivePalette.gold.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SerifText("Amin.", 26f, DeepDivePalette.goldBright, italic = true)
            Text(
                text = "$endLine $close",
                fontSize = 14.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute
            )
            Box(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clip(CircleShape)
                    .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.24f), CircleShape)
                    .pressable(onClick = onBeginAgain)
                    .padding(horizontal = 22.dp, vertical = 11.dp)
            ) {
                Text(
                    text = "Begin again",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = DeepDivePalette.gold
                )
            }
        }
    }
}

@Composable
private fun ClosingPage(
    s: DeepDiveSection.Closing,
    show: Boolean,
    lang: CommentaryLanguage,
    onReadSurah: (() -> Unit)?,
    onClose: () -> Unit
) {
    val scale = ReadingSettingsManager.scale
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TagLabel(s.tag.text(lang), show)
        Spacer(modifier = Modifier.height(26.dp))
        Reveal(show, 0.2) { ArabicText(s.titleAr, 56f, DeepDivePalette.goldBright) }
        Spacer(modifier = Modifier.height(20.dp))
        Reveal(show, 0.45) {
            SerifText(
                s.essence.text(lang), 20f * scale, DeepDivePalette.cream, italic = true,
                lineSpacing = 5f * scale, modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Reveal(show, 0.7) { Box(modifier = Modifier.padding(vertical = 26.dp)) { Hairline() } }
        Reveal(show, 0.7) {
            Text(
                text = s.line.text(lang),
                fontSize = 14.sp * scale,
                lineHeight = 20.sp * scale,
                textAlign = TextAlign.Center,
                color = DeepDivePalette.mute,
                modifier = Modifier.widthIn(max = 340.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Reveal(show, 1.0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (onReadSurah != null) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(DeepDivePalette.gold, DeepDivePalette.goldBright)
                                )
                            )
                            .pressable(onClick = onReadSurah)
                            .padding(horizontal = 26.dp, vertical = 13.dp)
                    ) {
                        Text(
                            text = JourneyStrings.readTheFullSurah(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF1F1708)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.24f), CircleShape)
                        .pressable(onClick = onClose)
                        .padding(horizontal = 22.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = JourneyStrings.done(lang),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = DeepDivePalette.gold
                    )
                }
            }
        }
    }
}

// MARK: - Refrain + interactive close beats (iOS DeepDiveView)
//
// al-Rahman's refrain and the six theme-dive gesture closes. Ported faithfully from
// DeepDiveView.swift: same state machines, timings, and copy. The six closes all
// resolve through [CloseResolved]; their idle heads share [CloseStatus].

/** A blessing-light in the Shukr count field (unit space). */
private data class CountDot(val x: Float, val y: Float, val size: Float, val opacity: Float)

/** A fixed audience-light in the Ikhlas extinguish field (unit space). */
private data class ExtinguishDot(val id: Int, val x: Float, val y: Float, val size: Float)

private val extinguishDots = listOf(
    ExtinguishDot(0, 0.16f, 0.30f, 6f), ExtinguishDot(1, 0.50f, 0.15f, 5f),
    ExtinguishDot(2, 0.84f, 0.26f, 6.5f), ExtinguishDot(3, 0.29f, 0.63f, 5.5f),
    ExtinguishDot(4, 0.68f, 0.54f, 6f), ExtinguishDot(5, 0.13f, 0.82f, 5f),
    ExtinguishDot(6, 0.52f, 0.85f, 6.5f), ExtinguishDot(7, 0.88f, 0.74f, 5.5f)
)

/** One soul beneath the cloak in the al-Kisa salawat arc (unit space), gathered in order. */
private data class SalawatName(val id: Int, val x: Float, val y: Float, val ar: String, val en: String)

private val salawatNames = listOf(
    SalawatName(0, 0.08f, 0.24f, "مُحَمَّد ﷺ", "Muhammad ﷺ"),
    SalawatName(1, 0.29f, 0.56f, "الحَسَن", "Hasan"),
    SalawatName(2, 0.50f, 0.68f, "الحُسَيْن", "Husayn"),
    SalawatName(3, 0.71f, 0.56f, "عَلِيّ", "Ali"),
    SalawatName(4, 0.92f, 0.24f, "فَاطِمَة", "Fatima")
)

/** Small-caps status line under an interactive close; brightens + grows when active. */
@Composable
private fun CloseStatus(text: String, active: Boolean, topPad: Dp) {
    Text(
        text = text.uppercase(),
        fontSize = if (active) 12.sp else 10.5.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = if (active) 4.sp else 3.sp,
        textAlign = TextAlign.Center,
        color = if (active) DeepDivePalette.goldBright else DeepDivePalette.gold,
        modifier = Modifier.padding(top = topPad)
    )
}

/** The shared resolved state of every interactive close: the verse arrives on a faint glow. */
@Composable
private fun CloseResolved(
    arabic: String,
    translation: String,
    reference: String,
    note: String,
    nextLabel: String,
    arabicSize: Float = 30f
) {
    val scale = ReadingSettingsManager.scale
    Column(
        modifier = Modifier.fillMaxWidth().drawBehind {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(DeepDivePalette.goldBright.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.maxDimension * 0.6f
                )
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArabicText(arabic, arabicSize * scale, DeepDivePalette.goldBright, lineSpacing = 10f * scale)
        Spacer(Modifier.height(16.dp))
        SerifText(
            translation, 21f * scale, DeepDivePalette.cream, italic = true,
            lineSpacing = 4f * scale, modifier = Modifier.widthIn(max = 340.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = reference, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp, color = DeepDivePalette.gold.copy(alpha = 0.85f)
        )
        Box(Modifier.padding(vertical = 22.dp)) { Hairline() }
        Text(
            text = note, fontSize = 14.sp * scale, lineHeight = 20.sp * scale,
            textAlign = TextAlign.Center, color = DeepDivePalette.mute,
            modifier = Modifier.widthIn(max = 320.dp)
        )
        Spacer(Modifier.height(30.dp))
        Bob(nextLabel, true, 0.4)
    }
}

/** al-Rahman's recurring question: the refrain glows, the reader answers in the taught reply. */
@Composable
private fun RefrainPage(s: DeepDiveSection.Refrain, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    var answered by rememberSaveable(s.surah, s.ayah) { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TagLabel(s.tag.text(lang), show)
        Spacer(Modifier.height(26.dp))
        Reveal(show, 0.2) { ArabicText(s.arabic, 26f * scale, DeepDivePalette.cream, bold = true, lineSpacing = 12f * scale) }
        Spacer(Modifier.height(20.dp))
        Reveal(show, 0.45) {
            SerifText(s.translation.text(lang), 19f * scale, Color(0xFFCCCCCC), italic = true, lineSpacing = 4f * scale, modifier = Modifier.widthIn(max = 380.dp))
        }
        Spacer(Modifier.height(14.dp))
        Reveal(show, 0.45) { Text(s.reference, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, color = DeepDivePalette.gold.copy(alpha = 0.85f)) }
        Reveal(show, 0.7) { Box(Modifier.padding(top = 24.dp, bottom = 18.dp)) { Hairline() } }
        Reveal(show, 0.7) { SerifText(s.intro.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 340.dp)) }
        if (answered) {
            Spacer(Modifier.height(24.dp))
            Box(Modifier.width(1.dp).height(22.dp).background(Brush.verticalGradient(listOf(DeepDivePalette.goldBright.copy(alpha = 0f), DeepDivePalette.goldBright.copy(alpha = 0.7f)))))
            Spacer(Modifier.height(10.dp))
            Text("You Answer".uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 4.sp, color = DeepDivePalette.goldBright)
            Spacer(Modifier.height(18.dp))
            ArabicText(s.replyArabic, 26f * scale, DeepDivePalette.goldBright, bold = true, lineSpacing = 10f * scale)
            Spacer(Modifier.height(10.dp))
            Text(s.replyTransliteration, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp, textAlign = TextAlign.Center, color = DeepDivePalette.mute)
            Spacer(Modifier.height(14.dp))
            SerifText(s.replyTranslation.text(lang), 21f * scale, DeepDivePalette.cream, italic = true, lineSpacing = 5f * scale, modifier = Modifier.widthIn(max = 330.dp))
            Spacer(Modifier.height(16.dp))
            DuaListenButton(arabic = s.replyArabic)
            s.teachSource?.let {
                Spacer(Modifier.height(18.dp))
                Text(it.text(lang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, textAlign = TextAlign.Center, color = DeepDivePalette.gold.copy(alpha = 0.8f))
            }
            Box(Modifier.padding(top = 24.dp, bottom = 18.dp)) { Hairline() }
            Text(s.reflection.text(lang), fontSize = 15.sp * scale, lineHeight = 21.sp * scale, textAlign = TextAlign.Center, color = DeepDivePalette.mute, modifier = Modifier.widthIn(max = 340.dp))
        } else {
            Reveal(show, 0.95) {
                Column(
                    modifier = Modifier.padding(top = 30.dp).pressable { haptic.performHapticFeedback(HapticFeedbackType.LongPress); answered = true },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.ExpandLess, contentDescription = null, tint = DeepDivePalette.goldBright, modifier = Modifier.size(16.dp))
                    Text(
                        "Answer Him".uppercase(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 3.5.sp, color = DeepDivePalette.goldBright,
                        modifier = Modifier.clip(CircleShape).border(1.dp, DeepDivePalette.goldBright.copy(alpha = 0.35f), CircleShape).padding(horizontal = 26.dp, vertical = 13.dp)
                    )
                }
            }
        }
    }
}

/** Tawakkul: press and hold the ring (the grip); lifting after it fills IS the release. */
@Composable
private fun ReleasePage(s: DeepDiveSection.Release, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var holding by remember { mutableStateOf(false) }
    var primed by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val fill by animateFloatAsState(
        targetValue = if (holding || primed) 1f else 0f,
        animationSpec = tween(if (holding && !primed) 2200 else 300, easing = if (holding && !primed) LinearEasing else EaseOut),
        label = "releaseFill"
    )
    val core by animateFloatAsState(if (primed) 36f else 14f, tween(500, easing = EaseInOut), label = "releaseCore")
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (done) {
            CloseResolved(s.arabic, s.translation.text(lang), s.reference, s.note.text(lang), s.nextLabel.text(lang))
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (holding) 0.35f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream, modifier = Modifier.alpha(if (holding) 0.45f else 1f)) }
            Spacer(Modifier.height(14.dp))
            Reveal(show, 0.3) {
                SerifText(if (holding) "Hold it. All of it." else s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp).alpha(if (holding) 0.5f else 1f))
            }
            Spacer(Modifier.height(34.dp))
            Reveal(show, 0.5) {
                Box(
                    modifier = Modifier.size(120.dp).pointerInput(done) {
                        detectTapGestures(onPress = {
                            if (!done) {
                                holding = true; primed = false
                                val job = scope.launch { delay(2200); if (holding && !done) { primed = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) } }
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                tryAwaitRelease()
                                job.cancel()
                                if (primed && !done) { done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) } else if (!done) holding = false
                            }
                        })
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(120.dp)) {
                        val s1 = 1.5.dp.toPx()
                        drawCircle(DeepDivePalette.gold.copy(alpha = 0.5f), radius = (size.minDimension - s1) / 2f, style = Stroke(width = s1))
                        val inset = 1.dp.toPx()
                        drawArc(DeepDivePalette.goldBright, -90f, 360f * fill, false, topLeft = Offset(inset, inset), size = Size(size.width - inset * 2, size.height - inset * 2), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                    }
                    Box(Modifier.size(core.dp).clip(CircleShape).background(DeepDivePalette.goldBright))
                }
            }
            Reveal(show, 0.6) { CloseStatus(if (primed) "Now - let go" else "Press and hold - that is the grip", primed, 18.dp) }
        }
    }
}

/** Shukr: tap to count blessings; at seven they overrun the finger and cannot be finished. */
@Composable
private fun CountPage(s: DeepDiveSection.Count, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    var taps by remember { mutableStateOf(0) }
    var tally by remember { mutableStateOf(0) }
    var overflow by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val lights = remember { mutableStateListOf<CountDot>() }
    fun addLight() = lights.add(CountDot(Random.nextFloat() * 0.94f + 0.03f, Random.nextFloat() * 0.90f + 0.05f, Random.nextFloat() * 2f + 2.5f, Random.nextFloat() * 0.45f + 0.5f))
    LaunchedEffect(overflow) {
        if (overflow) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            var tick = 0
            while (tick < 18 && !done) {
                delay(120); tick++
                tally += tick * Random.nextInt(2, 6)
                if (lights.size < 110) repeat(4) { addLight() }
            }
            if (!done) { done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth().pointerInput(done, overflow) {
            detectTapGestures {
                if (!done && !overflow) {
                    taps++; tally = taps; addLight(); haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (taps >= 7) overflow = true
                }
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (done) {
            CloseResolved(s.arabic, s.translation.text(lang), s.reference, s.note.text(lang), s.nextLabel.text(lang))
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (overflow) 0.35f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream, modifier = Modifier.alpha(if (overflow) 0.4f else 1f)) }
            if (taps == 0) {
                Spacer(Modifier.height(14.dp))
                Reveal(show, 0.3) { SerifText(s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp)) }
            } else {
                Spacer(Modifier.height(22.dp))
                SerifText("$tally", 54f, DeepDivePalette.goldBright)
                if (overflow) {
                    Spacer(Modifier.height(10.dp))
                    Text("And counting itself".uppercase(), fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.6.sp, color = DeepDivePalette.mute)
                }
            }
            Spacer(Modifier.height(if (taps == 0) 34.dp else 14.dp))
            Box(Modifier.widthIn(max = 300.dp).fillMaxWidth().height(190.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    if (lights.isEmpty()) {
                        drawCircle(DeepDivePalette.goldBright, radius = 7.dp.toPx(), center = Offset(size.width / 2, size.height / 2))
                    } else {
                        lights.forEach { l ->
                            val c = Offset(l.x * size.width, l.y * size.height)
                            drawCircle(DeepDivePalette.goldBright.copy(alpha = l.opacity * 0.35f), radius = l.size.dp.toPx() * 1.6f, center = c)
                            drawCircle(DeepDivePalette.goldBright.copy(alpha = l.opacity), radius = l.size.dp.toPx() / 2f, center = c)
                        }
                    }
                }
            }
            Reveal(show, 0.6) { CloseStatus(if (overflow) "They outrun the count" else "Tap - each tap, one blessing", overflow, 18.dp) }
        }
    }
}

/** Salah: press and hold; the core sinks to the earth-line (sujud) and, held, the verse resolves. */
@Composable
private fun SujudPage(s: DeepDiveSection.Sujud, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var holding by remember { mutableStateOf(false) }
    var atBottom by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val coreY by animateFloatAsState(
        targetValue = if (holding || atBottom) 54f else -54f,
        animationSpec = tween(if (holding && !atBottom) 2200 else 300, easing = if (holding && !atBottom) LinearEasing else EaseOut),
        label = "sujudCore"
    )
    val warmth by animateFloatAsState(if (holding || atBottom) 0.85f else 0f, tween(if (holding && !atBottom) 2200 else 300), label = "sujudWarm")
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (done) {
            CloseResolved(s.arabic, s.translation.text(lang), s.reference, s.note.text(lang), s.nextLabel.text(lang))
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (holding) 0.35f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream, modifier = Modifier.alpha(if (holding) 0.45f else 1f)) }
            Spacer(Modifier.height(14.dp))
            Reveal(show, 0.3) { SerifText(s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp).alpha(if (holding) 0.5f else 1f)) }
            Spacer(Modifier.height(34.dp))
            Reveal(show, 0.5) {
                Box(
                    modifier = Modifier.size(120.dp).pointerInput(done) {
                        detectTapGestures(onPress = {
                            if (!done) {
                                holding = true
                                val job = scope.launch {
                                    delay(2200)
                                    if (holding && !done) {
                                        atBottom = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        delay(2000)
                                        if (holding && !done) { done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                                    }
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                tryAwaitRelease()
                                job.cancel()
                                if (atBottom && !done) { done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) } else if (!done) holding = false
                            }
                        })
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(120.dp)) {
                        val r = size.minDimension / 2f
                        drawCircle(DeepDivePalette.gold.copy(alpha = 0.5f), radius = r - 0.75.dp.toPx(), style = Stroke(width = 1.5.dp.toPx()))
                        if (warmth > 0f) drawCircle(DeepDivePalette.goldBright.copy(alpha = warmth), radius = r - 1.dp.toPx(), style = Stroke(width = 2.dp.toPx()))
                        val ly = size.height / 2 + 56.dp.toPx()
                        drawLine(
                            Brush.horizontalGradient(listOf(DeepDivePalette.gold.copy(alpha = 0f), DeepDivePalette.gold.copy(alpha = if (atBottom) 0.6f else 0.35f), DeepDivePalette.gold.copy(alpha = 0f))),
                            Offset(size.width / 2 - 85.dp.toPx(), ly), Offset(size.width / 2 + 85.dp.toPx(), ly), strokeWidth = 1.dp.toPx()
                        )
                        val coreR = (if (atBottom) 20f else 12f).dp.toPx() / 2f
                        drawCircle(DeepDivePalette.goldBright, radius = coreR, center = Offset(size.width / 2, size.height / 2 + coreY.dp.toPx()))
                    }
                }
            }
            Reveal(show, 0.5) { CloseStatus(if (atBottom) "Stay - this is the nearest point" else "Press and hold - go down", atBottom, 22.dp) }
        }
    }
}

/** Ikhlas: tap each audience-light out; the last will not go out - everything perishes but His Face. */
@Composable
private fun ExtinguishPage(s: DeepDiveSection.Extinguish, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val out = remember { mutableStateListOf<Int>() }
    var flared by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val remaining = extinguishDots.size - out.size
    val promptDim = if (flared) 0.35f else (1f - 0.5f * (1f - remaining.toFloat() / extinguishDots.size))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (done) {
            CloseResolved(s.arabic, s.translation.text(lang), s.reference, s.note.text(lang), s.nextLabel.text(lang))
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (flared) 0.35f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream, modifier = Modifier.alpha(promptDim)) }
            if (out.isEmpty()) {
                Spacer(Modifier.height(14.dp))
                Reveal(show, 0.3) { SerifText(s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp)) }
            }
            Spacer(Modifier.height(if (out.isEmpty()) 34.dp else 14.dp))
            Reveal(show, 0.5) {
                BoxWithConstraints(Modifier.widthIn(max = 300.dp).fillMaxWidth().height(190.dp)) {
                    val w = maxWidth; val h = maxHeight
                    extinguishDots.forEach { d ->
                        val isOut = out.contains(d.id)
                        val isLast = !isOut && (extinguishDots.size - out.size) == 1
                        val isFlared = isLast && flared
                        Box(
                            modifier = Modifier.offset(x = w * d.x - 20.dp, y = h * d.y - 20.dp).size(40.dp)
                                .then(if (!isOut) Modifier.pressable {
                                    if (!done) {
                                        if (extinguishDots.size - out.size > 1) { out.add(d.id); haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                                        else if (!flared) { flared = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress); scope.launch { delay(1500); if (!done) done = true } }
                                        else done = true
                                    }
                                } else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(Modifier.size(40.dp)) {
                                val c = Offset(size.width / 2, size.height / 2)
                                if (isOut) {
                                    drawCircle(DeepDivePalette.gold.copy(alpha = 0.16f), radius = (d.size + 3).dp.toPx() / 2f, center = c, style = Stroke(width = 1.dp.toPx()))
                                } else {
                                    val dia = if (isFlared) d.size * 2.6f else if (isLast) d.size * 1.5f else d.size
                                    drawCircle(DeepDivePalette.goldBright.copy(alpha = if (isFlared) 0.9f else if (isLast) 0.85f else 0.6f), radius = dia.dp.toPx() * 1.4f / 2f, center = c)
                                    drawCircle(DeepDivePalette.goldBright, radius = dia.dp.toPx() / 2f, center = c)
                                }
                            }
                        }
                    }
                }
            }
            Reveal(show, 0.6) { CloseStatus(if (flared) "This one does not go out" else "Tap each light - put it out", flared, 18.dp) }
        }
    }
}

/** Taqwa: a warm forbidden doorway drifts past; withhold - do not touch it - and let it pass. */
@Composable
private fun DoorPage(s: DeepDiveSection.Door, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var started by remember { mutableStateOf(false) }
    var reached by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    val offsetAnim = remember { Animatable(0f) }
    var driftJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    fun startDrift() {
        driftJob?.cancel()
        driftJob = scope.launch {
            started = true; reached = false
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            offsetAnim.snapTo(0f)
            offsetAnim.animateTo(1.15f, tween(4000, easing = EaseInOut))
            if (!done) { done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        }
    }
    LaunchedEffect(show) { if (show && !done && !started) { delay(1600); if (!done) startDrift() } }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (done) {
            CloseResolved(s.arabic, s.translation.text(lang), s.reference, s.note.text(lang), s.nextLabel.text(lang), arabicSize = 27f)
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (started) 0.5f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 33f, DeepDivePalette.cream, modifier = Modifier.alpha(if (started || reached) 0.4f else 1f)) }
            if (!started && !reached) {
                Spacer(Modifier.height(14.dp))
                Reveal(show, 0.3) { SerifText(s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp)) }
            }
            Spacer(Modifier.height(if (started || reached) 18.dp else 30.dp))
            Reveal(show, 0.5) {
                BoxWithConstraints(
                    Modifier.widthIn(max = 300.dp).fillMaxWidth().height(190.dp).pointerInput(done) {
                        detectTapGestures {
                            if (!done && started) {
                                driftJob?.cancel(); reached = true; started = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    offsetAnim.animateTo(0f, tween(400, easing = EaseInOut))
                                    delay(1000); reached = false
                                    if (!done) startDrift()
                                }
                            }
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.offset(x = maxWidth * 0.62f * offsetAnim.value).alpha(if (reached) 0.4f else 1f)) { DoorGlow() }
                }
            }
            Reveal(show, 0.6) {
                CloseStatus(
                    if (reached) "It opens again" else if (started) "Hold still - it is passing" else "Do not touch it - let it pass",
                    started && !reached, 18.dp
                )
            }
        }
    }
}

/** The warm forbidden doorway - the one amber element in an emerald/gold dive. */
@Composable
private fun DoorGlow() {
    Box(
        modifier = Modifier.size(width = 78.dp, height = 120.dp).clip(RoundedCornerShape(30.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFE8C48C).copy(alpha = 0.55f), Color(0xFFC7783B).copy(alpha = 0.24f)))),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier.padding(top = 8.dp).size(width = 40.dp, height = 86.dp).clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFFFE8BA).copy(alpha = 0.5f), Color.Transparent)))
        )
    }
}

/** al-Kisa: light the five names in the order the cloak gathered them; the fifth completes the salawat. */
@Composable
private fun SalawatPage(s: DeepDiveSection.Salawat, show: Boolean, lang: CommentaryLanguage) {
    val scale = ReadingSettingsManager.scale
    val haptic = LocalHapticFeedback.current
    var lit by remember { mutableStateOf(0) }
    var done by remember { mutableStateOf(false) }
    fun tap() {
        if (!done) {
            if (lit < salawatNames.size - 1) { lit++; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
            else { lit = salawatNames.size; done = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        }
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (done) {
            SalawatField(lit = lit, done = true, onTap = {})
            Spacer(Modifier.height(22.dp))
            CloseResolved(s.arabic, s.translation.text(lang), s.reference.uppercase(), s.note.text(lang), s.nextLabel.text(lang), arabicSize = 26f)
        } else {
            Reveal(show) { Text("✦", fontSize = 20.sp, color = DeepDivePalette.gold, modifier = Modifier.alpha(if (lit > 0) 0.35f else 1f)) }
            Spacer(Modifier.height(20.dp))
            Reveal(show, 0.15) { SerifText(s.prompt.text(lang), 34f, DeepDivePalette.cream, modifier = Modifier.alpha(if (lit > 0) 0.4f else 1f)) }
            if (lit == 0) {
                Spacer(Modifier.height(14.dp))
                Reveal(show, 0.3) { SerifText(s.subline.text(lang), 16f * scale, Color(0xFFB8B8B8), italic = true, lineSpacing = 3f * scale, modifier = Modifier.widthIn(max = 320.dp)) }
            }
            Spacer(Modifier.height(if (lit == 0) 30.dp else 14.dp))
            Reveal(show, 0.5) { SalawatField(lit = lit, done = false, onTap = { tap() }) }
            Reveal(show, 0.6) { CloseStatus(if (lit == salawatNames.size - 1) "One name remains" else "Tap each light - greet them by name", lit == salawatNames.size - 1, 16.dp) }
        }
    }
}

@Composable
private fun SalawatField(lit: Int, done: Boolean, onTap: () -> Unit) {
    BoxWithConstraints(
        Modifier.widthIn(max = 310.dp).fillMaxWidth().height(if (done) 120.dp else 165.dp)
            .then(if (!done) Modifier.pointerInput(Unit) { detectTapGestures { onTap() } } else Modifier)
    ) {
        val w = maxWidth; val h = maxHeight
        if (done) {
            Canvas(Modifier.fillMaxSize()) {
                val p = Path().apply {
                    moveTo(size.width * 0.08f, size.height * 0.24f + 24.dp.toPx())
                    quadraticTo(size.width * 0.5f, size.height * 0.95f + 24.dp.toPx(), size.width * 0.92f, size.height * 0.24f + 24.dp.toPx())
                }
                drawPath(p, DeepDivePalette.goldBright.copy(alpha = 0.55f), style = Stroke(width = 1.5.dp.toPx()))
            }
        }
        salawatNames.forEach { n ->
            val isLit = n.id < lit || done
            Column(
                modifier = Modifier.offset(x = w * n.x - 42.dp, y = h * n.y + 12.dp).width(84.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Canvas(Modifier.size(18.dp)) {
                    val c = Offset(size.width / 2, size.height / 2)
                    if (isLit) {
                        drawCircle(DeepDivePalette.goldBright.copy(alpha = 0.4f), radius = 10.dp.toPx(), center = c)
                        drawCircle(DeepDivePalette.goldBright, radius = 6.dp.toPx(), center = c)
                    } else {
                        drawCircle(DeepDivePalette.goldBright.copy(alpha = 0.08f), radius = 6.dp.toPx(), center = c)
                        drawCircle(DeepDivePalette.goldBright.copy(alpha = 0.25f), radius = 6.dp.toPx(), center = c, style = Stroke(width = 1.dp.toPx()))
                    }
                }
                if (isLit && !done) {
                    ArabicText(n.ar, 15f, DeepDivePalette.goldBright)
                    Text(n.en.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp, color = DeepDivePalette.mute)
                }
            }
        }
    }
}

// MARK: - Threshold cover + locked veil (premium art)

/**
 * The cover as the "doorway" behind the opening beat (iOS DeepDiveView
 * threshold): full-bleed art over the #040A07 base, washed by a vertical
 * black gradient that lets the art glow through the middle, plus a radial
 * vignette. The caller drives [coverAlpha] from the pager scroll.
 */
@Composable
private fun ThresholdCover(art: Int, coverAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = coverAlpha }
            .background(Color(0xFF040A07))
    ) {
        Image(
            painter = painterResource(art),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Black.copy(alpha = 0.50f),
                        0.34f to Color.Black.copy(alpha = 0.66f),
                        0.62f to Color.Black.copy(alpha = 0.74f),
                        1.00f to Color.Black.copy(alpha = 0.60f)
                    )
                )
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    0.32f to Color.Transparent,
                    1.00f to Color.Black.copy(alpha = 0.55f),
                    center = center,
                    radius = max(size.width, size.height) * 0.62f
                )
            )
        }
    }
}

/**
 * The final beat of a locked premium descent: the same veil recipe as the
 * journey locked-day preview but with the lighter 0.46 overlay (this veil
 * carries less text). The CTA opens the paywall with this dive's cover.
 */
@Composable
private fun DescentVeilPage(
    art: Int?,
    dive: DeepDive,
    lang: CommentaryLanguage,
    show: Boolean,
    onUnlock: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (art != null) {
            CoverVeil(art = art, overlayAlpha = 0.46f, modifier = Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 480.dp)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Reveal(show) {
                Text(
                    text = JourneyStrings.premium(lang).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.gold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Reveal(show, 0.2) { ArabicText(dive.titleAr, 52f, DeepDivePalette.goldBright) }
            Spacer(modifier = Modifier.height(12.dp))
            Reveal(show, 0.4) { SerifText(dive.titleEn, 32f, DeepDivePalette.cream) }
            Reveal(show, 0.6) { Box(modifier = Modifier.padding(vertical = 26.dp)) { Hairline() } }
            Reveal(show, 0.6) {
                Text(
                    text = JourneyStrings.premiumDescentNote(lang),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.mute,
                    modifier = Modifier.widthIn(max = 320.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Reveal(show, 0.85) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepDivePalette.gold, DeepDivePalette.goldBright)
                            )
                        )
                        .pressable(onClick = onUnlock)
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = JourneyStrings.unlockPremium(lang),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF1F1708)
                    )
                }
            }
        }
    }
}
