package com.thaqalayn.app.ui.onboarding

// Onboarding page 2: App Mission (iOS MissionScreen).
// A manifesto screen - the ثقلين wordmark crowned by a breathing gold glow with
// two ivory doves wheeling through it, a punchy claim, and four gold-verb beats
// (Reflect / Journey / Descend / Walk). English-only, matching iOS.

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private data class Beat(val verb: String, val line: String)

private val beats = listOf(
    Beat("Reflect", "on a verse until it stays with you."),
    Beat("Journey", "through the seasons of faith, day by day."),
    Beat("Descend", "layer by layer, into the words you thought you knew."),
    Beat("Walk", "beside the Ahlul Bayt, who never leave the Quran's side.")
)

@Composable
fun MissionPage() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val gold = OnbPalette.gold

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Wordmark crowned by breathing glow with two wheeling doves.
            Wordmark(isVisible = isVisible)

            FadeRise(visible = isVisible, delayMillis = 300, riseDistance = 22.dp, durationMillis = 600) {
                Text(
                    text = "This was never meant to be just another Quran app.",
                    style = onbSerif(30),
                    lineHeight = 38.sp,
                    color = OnbPalette.primaryText,
                    modifier = Modifier.padding(top = 22.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(22.dp),
                modifier = Modifier.padding(top = 44.dp)
            ) {
                beats.forEachIndexed { index, beat ->
                    if (index > 0) {
                        FadeRise(
                            visible = isVisible,
                            delayMillis = 550 + index * 140,
                            riseDistance = 0.dp,
                            durationMillis = 500
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(gold.copy(alpha = 0.30f), Color.Transparent)
                                        )
                                    )
                            )
                        }
                    }
                    SlideInBeat(
                        visible = isVisible,
                        delayMillis = 500 + index * 140
                    ) {
                        BeatRow(beat)
                    }
                }
            }
        }
    }
}

// MARK: - Wordmark + glow + doves

@Composable
private fun Wordmark(isVisible: Boolean) {
    val gold = OnbPalette.gold
    val pulse = rememberInfiniteTransition(label = "wordmarkGlow")
    val glowScale by pulse.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse),
        label = "wordmarkGlowScale"
    )
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse),
        label = "wordmarkGlowAlpha"
    )

    val entrance = remember { Animatable(0f) }
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(120)
            entrance.animateTo(1f, tween(800))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 230.dp, height = 170.dp)
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                    alpha = glowAlpha
                }
                .background(
                    Brush.radialGradient(
                        0.0f to gold.copy(alpha = 0.20f),
                        0.6f to gold.copy(alpha = 0.07f),
                        1.0f to Color.Transparent
                    )
                )
        )

        ThaqalaynDovesLayer(modifier = Modifier.fillMaxSize())

        Text(
            text = "ثقلين",
            style = onbArabic(54),
            color = gold,
            modifier = Modifier.graphicsLayer {
                val e = entrance.value
                scaleX = 0.6f + 0.4f * e
                scaleY = 0.6f + 0.4f * e
                alpha = 0.95f * e
            }
        )
    }
}

// MARK: - Beat row (gold verb + serif-italic line)

@Composable
private fun BeatRow(beat: Beat) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = beat.verb,
            style = onbSerif(29),
            color = OnbPalette.gold,
            maxLines = 1,
            modifier = Modifier.width(132.dp)
        )
        Text(
            text = beat.line,
            style = onbSerifItalic(20),
            lineHeight = 26.sp,
            color = OnbPalette.cream.copy(alpha = 0.88f),
            modifier = Modifier.weight(1f)
        )
    }
}

/** Fade + slide-in-from-left entrance for the beats (iOS offset(x: -24)). */
@Composable
private fun SlideInBeat(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMillis.toLong())
            progress.animateTo(1f, tween(550))
        }
    }
    val slidePx = with(androidx.compose.ui.platform.LocalDensity.current) { 24.dp.toPx() }
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress.value
            translationX = -(1f - progress.value) * slidePx
        }
    ) {
        content()
    }
}

// MARK: - The two doves (iOS ThaqalaynDovesLayer)

/**
 * Two ivory doves - the two weighty things of the wordmark - wheeling slowly on
 * a shared ellipse through the ثقلين glow, drawn beneath the wordmark text.
 */
@Composable
private fun ThaqalaynDovesLayer(modifier: Modifier = Modifier) {
    val timeSec by produceState(0.0) {
        while (true) {
            withInfiniteAnimationFrameNanos { value = it / 1_000_000_000.0 }
        }
    }
    val ivory = Color(0xFFF3EAD6)

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val rx = min(size.width * 0.40f, 150.dp.toPx())
        val ry = size.height * 0.34f

        for (i in 0 until 2) {
            val theta = timeSec * 0.32 + i * PI
            val x = cx + (cos(theta) * rx).toFloat()
            val y = cy + (sin(theta) * ry).toFloat()
            val bank = (cos(theta) * 0.35).coerceIn(-0.4, 0.4)
            val depth = 0.72 + 0.28 * (sin(theta) + 1) / 2
            val doveSize = (8.5 * depth).toFloat().dp.toPx()
            val alpha = (0.30 + 0.35 * depth).toFloat()
            val lift = (sin(timeSec * 9 + i * 2.1) * doveSize * 0.45).toFloat()

            drawDove(
                x = x, y = y,
                bankRadians = bank.toFloat(),
                doveSize = doveSize,
                lift = lift,
                color = ivory.copy(alpha = alpha)
            )
        }
    }
}

/** Shared dove glyph: two quad-curve wings + a small elliptical body. */
private fun DrawScope.drawDove(
    x: Float,
    y: Float,
    bankRadians: Float,
    doveSize: Float,
    lift: Float,
    color: Color
) {
    translate(left = x, top = y) {
        rotate(degrees = bankRadians * (180f / PI.toFloat()), pivot = Offset.Zero) {
            val wings = Path().apply {
                moveTo(-doveSize, -lift)
                quadraticTo(-doveSize * 0.45f, doveSize * 0.22f, 0f, 0f)
                quadraticTo(doveSize * 0.45f, doveSize * 0.22f, doveSize, -lift)
            }
            drawPath(
                path = wings,
                color = color,
                style = Stroke(width = max(1.0f, doveSize * 0.22f), cap = StrokeCap.Round)
            )
            drawOval(
                color = color,
                topLeft = Offset(-doveSize * 0.18f, -doveSize * 0.06f),
                size = androidx.compose.ui.geometry.Size(doveSize * 0.36f, doveSize * 0.24f)
            )
        }
    }
}
