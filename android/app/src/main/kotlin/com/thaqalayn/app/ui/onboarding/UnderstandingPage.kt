package com.thaqalayn.app.ui.onboarding

// Onboarding page 3: a teaser for Understanding, the passage commentary (iOS
// UnderstandingScreen, 9.0). A reader card styled like the real Understanding screen
// plays al-Fatihah's passage through its four parts - essay, verse by verse,
// narrations, perspectives - while a rail beside it lights each part in turn. The
// content is hardcoded from passages_1.json (al-Fatihah is free, so the user meets
// exactly this after onboarding). English only, like the rest of onboarding.

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.components.rememberReduceMotion
import com.thaqalayn.app.ui.theme.CormorantFamily
import kotlinx.coroutines.delay

/** The four parts of Understanding, in reading order - the rail's stops. */
private enum class UPart(val label: String) {
    ESSAY("Essay"), VERSE("Verse by verse"), NARRATIONS("Narrations"), PERSPECTIVES("Perspectives")
}

/** Ticks each beat holds the card, in UPart order (0.35 s per tick, about 14 s a loop). */
private val BeatTicks = listOf(13, 9, 10, 9)
private val CardHeight = 304.dp
private val StopPitch = CardHeight / 4

@Composable
fun UnderstandingPage() {
    val reduceMotion = rememberReduceMotion()
    var isVisible by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { isVisible = true }
    LaunchedEffect(reduceMotion) {
        if (reduceMotion) return@LaunchedEffect
        while (true) {
            delay(350)
            tick += 1
        }
    }

    // Where the loop is: the part on the card and how many ticks it has been there.
    var t = tick % BeatTicks.sum()
    var part = UPart.ESSAY
    var offset = 0
    for ((i, length) in BeatTicks.withIndex()) {
        if (t < length) {
            part = UPart.entries[i]
            offset = t
            break
        }
        t -= length
    }
    // Under Reduce Motion the card is a static composite, so every stop reads as reached.
    val litPart = if (reduceMotion) UPart.PERSPECTIVES else part
    // Lines of a beat write in one every two ticks (0.7 s).
    val shown: (Int) -> Boolean = { line -> reduceMotion || offset >= line * 2 }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()
        OnbMotes(count = 12)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 34.dp, end = 34.dp, top = 62.dp, bottom = 68.dp)
        ) {
            FadeRise(visible = isVisible, delayMillis = 150, riseDistance = (-16).dp, durationMillis = 600) {
                Text("UNDERSTANDING", style = onbEyebrow, color = OnbPalette.gold)
            }
            FadeRise(visible = isVisible, delayMillis = 280, riseDistance = 26.dp, durationMillis = 600) {
                Text(
                    "Understand\nevery passage",
                    style = onbHeroTitle,
                    color = OnbPalette.primaryText,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
            FadeRise(visible = isVisible, delayMillis = 420, riseDistance = 20.dp, durationMillis = 600) {
                Text(
                    "Each surah is read in passages, a few verses that belong together. Read one, tap Understand, and the passage is explained in one plain essay, then verse by verse, with what the Imams said and where Shia and Sunni scholars differ. Every source is one tap away.",
                    style = onbBody,
                    lineHeight = 21.sp,
                    color = OnbPalette.secondaryText,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f).heightIn(min = 18.dp))

            // Hero: labels, rail and the living reader card.
            val heroScale by animateFloatAsState(if (isVisible) 1f else 0.86f, spring(dampingRatio = 0.72f, stiffness = 120f), label = "hero")
            val heroAlpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(500, delayMillis = 550), label = "heroAlpha")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = heroScale
                        scaleY = heroScale
                        alpha = heroAlpha
                    },
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RailLabels(litPart)
                Rail(litPart, reduceMotion)
                ReaderCard(part = part, reduceMotion = reduceMotion, shown = shown, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f).heightIn(min = 18.dp))

            FadeRise(visible = isVisible, delayMillis = 900, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    "Open any surah, read a passage, tap Understand",
                    style = onbCaption,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RailLabels(part: UPart) {
    Column(modifier = Modifier.width(96.dp)) {
        for (p in UPart.entries) {
            val color = when {
                p == part -> OnbPalette.gold
                p.ordinal < part.ordinal -> OnbPalette.cream.copy(alpha = 0.6f)
                else -> OnbPalette.cream.copy(alpha = 0.32f)
            }
            Box(modifier = Modifier.height(StopPitch).fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    p.label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = color,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun Rail(part: UPart, reduceMotion: Boolean) {
    val fill by animateDpAsState(StopPitch * part.ordinal, tween(600), label = "railFill")
    val halo = rememberInfiniteTransition(label = "halo")
    val haloScale by halo.animateFloat(
        initialValue = 0.9f,
        targetValue = if (reduceMotion) 0.9f else 1.25f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "haloScale"
    )
    Box(modifier = Modifier.width(22.dp).height(CardHeight), contentAlignment = Alignment.TopCenter) {
        // Track between the first and last stops, and the gold fill down to the lit one.
        Box(
            modifier = Modifier
                .padding(top = StopPitch / 2)
                .width(1.dp)
                .height(CardHeight - StopPitch)
                .background(OnbPalette.gold.copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .padding(top = StopPitch / 2)
                .width(1.dp)
                .height(fill)
                .background(OnbPalette.gold)
        )
        Column {
            for (p in UPart.entries) {
                val active = p == part
                val reached = p.ordinal <= part.ordinal
                Box(modifier = Modifier.height(StopPitch).width(22.dp), contentAlignment = Alignment.Center) {
                    if (active) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer {
                                    scaleX = haloScale
                                    scaleY = haloScale
                                }
                                .clip(CircleShape)
                                .background(OnbPalette.gold.copy(alpha = 0.18f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(if (active) 9.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (reached) OnbPalette.gold else Color.Transparent)
                            .border(1.dp, OnbPalette.gold.copy(alpha = if (reached) 0f else 0.35f), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaderCard(part: UPart, reduceMotion: Boolean, shown: (Int) -> Boolean, modifier: Modifier) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .height(CardHeight)
            .shadow(22.dp, shape, ambientColor = Color.Black.copy(alpha = 0.45f), spotColor = Color.Black.copy(alpha = 0.45f))
            .clip(shape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, OnbPalette.gold.copy(alpha = 0.22f), shape)
    ) {
        Column {
            MiniChrome()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    // Content that runs past the card fades out like a page that continues.
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(0f to Color.Black, 0.86f to Color.Black, 1f to Color.Transparent),
                            blendMode = BlendMode.DstIn
                        )
                    }
            ) {
                if (reduceMotion) {
                    StaticComposite()
                } else {
                    AnimatedContent(
                        targetState = part,
                        transitionSpec = {
                            (fadeIn(tween(550)) + slideInVertically(tween(550)) { it / 16 }) togetherWith
                                (fadeOut(tween(450)) + slideOutVertically(tween(450)) { -it / 16 })
                        },
                        label = "beat"
                    ) { beat ->
                        when (beat) {
                            UPart.ESSAY -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Reveal(shown(0)) { TitleBlock() }
                                Reveal(shown(1)) { Prose("Seven verses, and all of them are words God gives the worshipper to say. It opens with His Name.") }
                                Reveal(shown(2)) { Prose("Tabatabai reads that opening as the way a deed is marked with God's Name and bound to it, so that it is not left void and cut off", marker = 1) }
                            }
                            UPart.VERSE -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Reveal(shown(0)) { DividerLabel("Verse by verse") }
                                Reveal(shown(1)) { VerseRow(4, "Master of the Day") }
                                Reveal(shown(2)) { Prose("The verse is read two ways, owner of the Day and king of the Day; Tusi glosses the second as: that day the kingship is His alone, given to no one as it was given in this world", marker = 12, italic = true) }
                                Reveal(shown(3)) { Box(modifier = Modifier.padding(top = 4.dp)) { VerseRow(5, "Turning to speak to God", dimmed = true) } }
                            }
                            UPart.NARRATIONS -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Reveal(shown(0)) { VerseRow(4, "Master of the Day", dimmed = true) }
                                Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(modifier = Modifier.width(2.dp).fillMaxHeight().clip(RoundedCornerShape(1.dp)).background(OnbPalette.gold))
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Reveal(shown(1)) { Speaker("Imam Ali ibn al-Husayn") }
                                        Reveal(shown(2)) { Prose("Were all between east and west to die, I would feel no desolation so long as the Quran was with me. And when he recited 'Master of the Day of Retribution' he would repeat it until he was near to death.") }
                                        Reveal(shown(4)) { SourceLine("Sourced · Al-Kafi · vol. 2, hadith 13") }
                                    }
                                }
                            }
                            UPart.PERSPECTIVES -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Reveal(shown(0)) { DividerLabel("Perspectives") }
                                Reveal(shown(1)) { Prose("The two traditions divide over the first verse.") }
                                Reveal(shown(2)) { Prose("Tabrisi reports that our companions agree the Name-verse is a verse of this surah and of every surah, and that a prayer in which it is dropped is void", marker = 3) }
                                Reveal(shown(3)) { Prose("Tabari argues the opposite: he takes the return of the two mercy names in the third verse as proof that the opening formula is no verse of the Fatiha", marker = 27) }
                            }
                        }
                    }
                }
            }
        }

        // The tap-to-source moment: a source chip surfaces under the marker, the way
        // the real reader's source sheet answers a tap.
        AnimatedVisibility(
            visible = !reduceMotion && part == UPart.ESSAY && shown(4),
            enter = scaleIn(tween(300), initialScale = 0.8f) + fadeIn(tween(300)),
            exit = fadeOut(tween(250)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(24.dp)
                    .shadow(10.dp, CircleShape, spotColor = OnbPalette.gold.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(OnbPalette.gold)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = OnbPalette.base, modifier = Modifier.size(10.dp))
                Text("al-Mizan · Tabatabai · on 1 to 5", fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = OnbPalette.base, maxLines = 1)
            }
        }
    }
}

/** A miniature of the real reader's header: back circle and the Listen capsule. */
@Composable
private fun MiniChrome() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(1.dp, OnbPalette.gold.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = OnbPalette.gold, modifier = Modifier.size(11.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .height(22.dp)
                .clip(CircleShape)
                .background(OnbPalette.gold.copy(alpha = 0.12f))
                .border(1.dp, OnbPalette.gold.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = OnbPalette.gold, modifier = Modifier.size(9.dp))
            Text("Listen", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnbPalette.gold)
        }
    }
}

/** Reduce Motion: the four parts at once, compact, no timer. */
@Composable
private fun StaticComposite() {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        TitleBlock()
        Prose("Seven verses, and all of them are words God gives the worshipper to say", marker = 1)
        DividerLabel("Verse by verse")
        VerseRow(4, "Master of the Day")
        Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(OnbPalette.gold))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Speaker("Imam Ali ibn al-Husayn")
                Prose("Were all between east and west to die, I would feel no desolation so long as the Quran was with me.")
                SourceLine("Sourced · Al-Kafi · vol. 2, hadith 13")
            }
        }
        DividerLabel("Perspectives")
        Prose("The two traditions divide over the first verse", marker = 3)
    }
}

/** Fades and lifts a line in once the beat has reached it. */
@Composable
private fun Reveal(visible: Boolean, content: @Composable () -> Unit) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(500), label = "reveal")
    val rise by animateFloatAsState(if (visible) 0f else 10f, tween(500), label = "revealRise")
    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            translationY = rise * density
        }
    ) { content() }
}

@Composable
private fun TitleBlock() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("AL-FATIHAH · 1 TO 7", fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp, color = OnbPalette.gold)
        Text("Praise and the straight path", fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = OnbPalette.cream)
    }
}

@Composable
private fun Prose(text: String, marker: Int? = null, italic: Boolean = false) {
    val line = buildAnnotatedString {
        append(text)
        if (marker != null) {
            withStyle(
                SpanStyle(
                    fontFamily = null,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = OnbPalette.gold,
                    baselineShift = BaselineShift(0.5f)
                )
            ) { append(" [$marker]") }
        }
    }
    Text(
        text = line,
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Medium,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        fontSize = 12.5.sp,
        lineHeight = 16.sp,
        color = OnbPalette.cream.copy(alpha = if (italic) 0.7f else 0.88f),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DividerLabel(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(OnbPalette.gold.copy(alpha = 0.25f)))
        Text(label.uppercase(), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp, color = OnbPalette.gold, maxLines = 1)
        Box(modifier = Modifier.weight(1f).height(1.dp).background(OnbPalette.gold.copy(alpha = 0.25f)))
    }
}

@Composable
private fun VerseRow(n: Int, heading: String, dimmed: Boolean = false) {
    Row(
        modifier = Modifier.graphicsLayer { alpha = if (dimmed) 0.5f else 1f },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(OnbPalette.gold.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$n", fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = OnbPalette.gold)
        }
        Text(heading, fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, color = OnbPalette.cream, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun Speaker(name: String) {
    Text(name, fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = OnbPalette.cream)
}

@Composable
private fun SourceLine(text: String) {
    Text(text.uppercase(), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = OnbPalette.gold)
}
