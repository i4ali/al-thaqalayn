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
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.thaqalayn.app.ui.components.DuaListenButton
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import kotlinx.coroutines.launch
import kotlin.math.max

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
    onReadSurah: (() -> Unit)? = null
) {
    val lang = CommentaryLanguageManager.selectedLanguage
    val pagerState = rememberPagerState { dive.sections.size }
    val scope = rememberCoroutineScope()
    // First depth starts open so the "tap to open" gesture is obvious.
    var openDepths by remember { mutableStateOf(setOf(0)) }
    var saidAmin by remember { mutableStateOf(false) }

    val progress = ((pagerState.currentPage + pagerState.currentPageOffsetFraction) /
        max(dive.sections.size - 1, 1).toFloat()).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        DeepDiveBackground(progress = progress)

        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
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
                            is DeepDiveSection.Open -> OpenPage(section, show, lang)
                            is DeepDiveSection.Orientation -> OrientationPage(section, show, lang)
                            is DeepDiveSection.Verse -> VersePage(section, show, lang)
                            is DeepDiveSection.Depths -> DepthsPage(section, show, lang, openDepths, onToggleDepth)
                            is DeepDiveSection.Act -> ActPage(dive, section, show, lang)
                            is DeepDiveSection.Narration -> NarrationPage(section, show, lang)
                            is DeepDiveSection.Response -> ResponsePage(section, show, lang)
                            is DeepDiveSection.Climax -> ClimaxPage(section, show, lang)
                            is DeepDiveSection.ReflectionPrompt -> ReflectionPage(section, show, lang)
                            is DeepDiveSection.Dua -> DuaPage(section, show, lang, saidAmin, onSayAmin, onBeginAgain)
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
private fun OpenPage(s: DeepDiveSection.Open, show: Boolean, lang: CommentaryLanguage) {
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
        Bob("Descend", show)
    }
}

@Composable
private fun OrientationPage(s: DeepDiveSection.Orientation, show: Boolean, lang: CommentaryLanguage) {
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
                HintRow(Icons.Filled.ArrowDownward, "Scroll to sink deeper")
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
        Bob("Begin the descent", show, 0.9)
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
            SerifText("The map for everything below.", 15f, DeepDivePalette.mute, italic = true)
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
                text = "Movement".uppercase(),
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
                text = "${info?.name?.text(lang) ?: ""} · Depth ${s.act} of ${dive.acts.size}".uppercase(),
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
        AminBlock(s.close.text(lang), show, saidAmin, onSayAmin, onBeginAgain, scale)
    }
}

@Composable
private fun AminBlock(
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
                text = "The descent ends. $close",
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
