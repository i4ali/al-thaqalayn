package com.thaqalayn.app.ui.deepdive

// The progress-driven "descent" background for an immersive Deep Dive (iOS
// DeepDiveBackground.swift): the BG_STOPS colour ramp, the VIG_STOPS
// vignette-opacity ramp, piecewise-linear interpolation, and 16 slow rising
// gold light-motes. The dive keeps this fixed cinematic dark palette
// regardless of the app theme - it is an immersive mode, like a film.

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random

/** Fixed accent colours + the two progress ramps that drive the descent. */
object DeepDivePalette {
    val gold = Color(0xFFC9A55C)
    val goldBright = Color(0xFFE3C37E)
    val cream = Color(0xFFECE7DB)
    val mute = Color(0xFF8F9A8C)

    /** Background colour stops (BG_STOPS); first = descent progress 0..1. */
    private val bgStops = listOf(
        0.00f to Color(0xFF0F1712),
        0.32f to Color(0xFF0B110D),
        0.55f to Color(0xFF070A08),
        0.72f to Color(0xFF040605),
        0.82f to Color(0xFF020403),
        0.90f to Color(0xFF06100B),
        1.00f to Color(0xFF0B140F)
    )

    /** Vignette-opacity stops (VIG_STOPS): black opacity at each depth. */
    private val vigStops = listOf(
        0.00f to 0.32f,
        0.55f to 0.60f,
        0.82f to 0.94f,
        1.00f to 0.50f
    )

    /** Piecewise-linear background colour at descent progress [p] (clamped 0..1). */
    fun bg(p: Float): Color {
        val cp = p.coerceIn(0f, 1f)
        for (i in 0 until bgStops.size - 1) {
            val (ap, ac) = bgStops[i]
            val (bp, bc) = bgStops[i + 1]
            if (cp in ap..bp) {
                val t = if (bp > ap) (cp - ap) / (bp - ap) else 0f
                return lerp(ac, bc, t)
            }
        }
        return bgStops.last().second
    }

    /** Piecewise-linear vignette opacity at descent progress [p] (clamped 0..1). */
    fun vignette(p: Float): Float {
        val cp = p.coerceIn(0f, 1f)
        for (i in 0 until vigStops.size - 1) {
            val (ap, ao) = vigStops[i]
            val (bp, bo) = vigStops[i + 1]
            if (cp in ap..bp) {
                val t = if (bp > ap) (cp - ap) / (bp - ap) else 0f
                return ao + (bo - ao) * t
            }
        }
        return vigStops.last().second
    }
}

/** One rising light-mote; parameters mirror the iOS/JSX `motes` state. */
private class Mote(random: Random) {
    val x = random.nextFloat()
    val size = 1f + random.nextFloat() * 2f            // diameter, 1..3 dp
    val duration = 16.0 + random.nextDouble() * 20.0   // rise period, 16..36 s
    val delay = -random.nextDouble() * 30.0            // negative stagger, -30..0 s
    val opacity = 0.06f + random.nextFloat() * 0.16f   // 0.06..0.22

    /** Loop phase 0..1 at absolute [time]; the negative delay pre-spreads the motes. */
    fun phase(time: Double): Float {
        val cycles = (time - delay) / duration
        return (cycles - floor(cycles)).toFloat()
    }
}

/**
 * Immersive background whose colour + vignette deepen as [progress] (0..1)
 * advances, with slow rising gold motes drifting up the screen.
 */
@Composable
fun DeepDiveBackground(progress: Float, modifier: Modifier = Modifier) {
    val motes = remember { List(16) { Mote(Random(it * 7919 + 13)) } }

    // Continuous slow rise: one frame clock drives all motes forever.
    val timeSec by produceState(0.0) {
        while (true) {
            withInfiniteAnimationFrameNanos { value = it / 1_000_000_000.0 }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Base fill: interpolated background colour.
            drawRect(DeepDivePalette.bg(progress))

            // Rising gold light-motes.
            for (mote in motes) {
                val yFrac = 1.08f - 1.16f * mote.phase(timeSec)
                drawCircle(
                    color = DeepDivePalette.goldBright.copy(alpha = mote.opacity),
                    radius = (mote.size / 2f).dp.toPx(),
                    center = Offset(mote.x * size.width, yFrac * size.height)
                )
            }

            // Vignette on top: transparent core, darkening toward the edges,
            // centred slightly above the middle.
            val vig = DeepDivePalette.vignette(progress)
            val maxDim = max(size.width, size.height)
            drawRect(
                brush = Brush.radialGradient(
                    0.30f to Color.Transparent,
                    1.00f to Color.Black.copy(alpha = vig),
                    center = Offset(size.width * 0.5f, size.height * 0.42f),
                    radius = maxDim * 0.72f
                )
            )
        }
    }
}
