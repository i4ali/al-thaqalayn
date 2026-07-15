package com.thaqalayn.app.ui.onboarding

// Shared chrome for the story-driven onboarding flow - exact port of the iOS
// Components/ folder (OnboardingBackground, OnboardingCard, OnboardingTypography,
// HeroChip). Onboarding keeps a fixed Midnight Emerald + gold look regardless of
// the app theme, matching iOS ("onboarding is always warm/dark, no branching").

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random

/** Fixed onboarding palette (iOS hex values). */
object OnbPalette {
    val gold = Color(0xFFECD49A)
    val goldDeep = Color(0xFFD6B25E)
    val cream = Color(0xFFF1E8D6)
    val creamBright = Color(0xFFF7F1E3)
    val ivory = Color(0xFFF3EAD6)
    val base = Color(0xFF0A1512)
    val onGold = Color(0xFF1A1408)

    // themeManager text tones as they resolve in the (always-dark) onboarding
    val primaryText = cream
    val secondaryText = cream.copy(alpha = 0.74f)
    val tertiaryText = cream.copy(alpha = 0.52f)
}

/** Accent chip tones (iOS ThemeManager.ChipColor statics). */
data class OnbChip(val bg: Color, val fg: Color)

val chipGold = OnbChip(Color(0xFFECD49A).copy(alpha = 0.15f), Color(0xFFECD49A))
val chipFoundation = OnbChip(Color(0xFF6FA5E8).copy(alpha = 0.15f), Color(0xFF6FA5E8))
val chipKnowledge = OnbChip(Color(0xFFB8A6D9).copy(alpha = 0.15f), Color(0xFFB8A6D9))
val chipProgress = OnbChip(Color(0xFF6FD0A6).copy(alpha = 0.15f), Color(0xFF6FD0A6))
val chipBrand = OnbChip(Color(0xFFECD49A).copy(alpha = 0.16f), Color(0xFFECD49A))
val chipComparative = OnbChip(Color(0xFFD69BB0).copy(alpha = 0.15f), Color(0xFFD69BB0))

// MARK: - Typography (iOS OnboardingTypography; EmType.serif defaults to semiBold)

val onbHeroTitle = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 27.sp)
val onbFinalTitle = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 36.sp)
val onbEyebrow = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.4.sp)
val onbCardTitle = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
val onbRowTitle = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
val onbBody = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
val onbCaption = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
val onbPill = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)

fun onbSerif(size: Int, weight: FontWeight = FontWeight.SemiBold) =
    TextStyle(fontFamily = CormorantFamily, fontWeight = weight, fontSize = size.sp)

fun onbSerifItalic(size: Int) = TextStyle(
    fontFamily = CormorantFamily, fontWeight = FontWeight.Medium,
    fontStyle = FontStyle.Italic, fontSize = size.sp
)

fun onbArabic(size: Int) = TextStyle(fontFamily = AmiriFamily, fontSize = size.sp)

// MARK: - Background (iOS OnboardingBackground)

/**
 * The shared full-screen onboarding background: deep emerald base, a radial
 * emerald wash centred above the top edge, and a soft amber crown glow.
 */
@Composable
fun OnboardingBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(OnbPalette.base)
        drawRect(
            brush = Brush.radialGradient(
                0.0f to Color(0xFF0C1D16),
                0.55f to Color(0xFF0A1512),
                1.0f to Color(0xFF081310),
                center = Offset(size.width * 0.5f, -size.height * 0.10f),
                radius = max(size.width, size.height) * 1.1f
            )
        )
        // Amber crown glow (ECD49A 13%, radius 230pt, centre lifted 90pt).
        drawCircle(
            brush = Brush.radialGradient(
                listOf(OnbPalette.gold.copy(alpha = 0.13f), Color.Transparent),
                center = Offset(size.width * 0.5f, -90.dp.toPx()),
                radius = 230.dp.toPx()
            ),
            radius = 230.dp.toPx(),
            center = Offset(size.width * 0.5f, -90.dp.toPx())
        )
    }
}

// MARK: - Cards (iOS onboardingCard / onboardingRow modifiers)

fun Modifier.onboardingCard(padding: Dp = 20.dp): Modifier {
    val shape = RoundedCornerShape(22.dp)
    return this
        .shadow(16.dp, shape, ambientColor = Color.Black.copy(alpha = 0.35f), spotColor = Color.Black.copy(alpha = 0.35f))
        .clip(shape)
        .background(Color.White.copy(alpha = 0.05f))
        .border(1.dp, OnbPalette.gold.copy(alpha = 0.10f), shape)
        .padding(padding)
}

fun Modifier.onboardingRow(padding: Dp = 14.dp): Modifier {
    val shape = RoundedCornerShape(18.dp)
    return this
        .shadow(8.dp, shape, ambientColor = Color.Black.copy(alpha = 0.30f), spotColor = Color.Black.copy(alpha = 0.30f))
        .clip(shape)
        .background(Color.White.copy(alpha = 0.045f))
        .border(1.dp, OnbPalette.gold.copy(alpha = 0.10f), shape)
        .padding(padding)
}

// MARK: - HeroChip (88dp chip badge with a breathing halo)

/**
 * Pastel chip badge crowning most onboarding screens, wrapped in a soft
 * breathing halo. The halo is a radial gradient (not a blur) so it renders
 * identically on every API level.
 */
@Composable
fun HeroChip(
    chip: OnbChip,
    iconColor: Color? = null,
    pulseDuration: Double = 2.5,
    icon: @Composable () -> Unit
) {
    val halo = iconColor ?: chip.fg
    val pulse = rememberInfiniteTransition(label = "heroChipPulse")
    val scale by pulse.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween((pulseDuration * 1000).toInt()),
            RepeatMode.Reverse
        ),
        label = "heroChipScale"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(
                    Brush.radialGradient(
                        listOf(halo.copy(alpha = 0.34f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(chip.bg)
                .border(1.dp, halo.copy(alpha = 0.22f), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

// MARK: - Gold CTA (the shared onboarding enable/continue button)

@Composable
fun OnbGoldButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pressable(onClick = onClick)
            .shadow(12.dp, shape, spotColor = OnbPalette.gold.copy(alpha = 0.4f))
            .clip(shape)
            .background(
                Brush.linearGradient(listOf(Color(0xFFECD49A), Color(0xFFD6B25E)))
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            icon?.invoke()
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnbPalette.onGold
            )
        }
    }
}

// MARK: - Rising gold motes (shared with the Deep Dive teasers)

private class OnbMote(random: Random) {
    val x = random.nextFloat()
    val size = 1f + random.nextFloat() * 2f
    val duration = 16.0 + random.nextDouble() * 20.0
    val delay = -random.nextDouble() * 30.0
    val opacity = 0.06f + random.nextFloat() * 0.16f

    fun phase(time: Double): Float {
        val cycles = (time - delay) / duration
        return (cycles - floor(cycles)).toFloat()
    }
}

/** Slow rising gold light-motes (iOS DeepDiveMotes), used by the teaser pages. */
@Composable
fun OnbMotes(count: Int = 14, modifier: Modifier = Modifier) {
    val motes = remember { List(count) { OnbMote(Random(it * 7919 + 13)) } }
    val timeSec by produceState(0.0) {
        while (true) {
            withInfiniteAnimationFrameNanos { value = it / 1_000_000_000.0 }
        }
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        for (mote in motes) {
            val yFrac = 1.08f - 1.16f * mote.phase(timeSec)
            drawCircle(
                color = Color(0xFFE3C37E).copy(alpha = mote.opacity),
                radius = (mote.size / 2f).dp.toPx(),
                center = Offset(mote.x * size.width, yFrac * size.height)
            )
        }
    }
}
