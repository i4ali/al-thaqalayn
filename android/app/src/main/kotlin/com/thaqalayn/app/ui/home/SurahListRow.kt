package com.thaqalayn.app.ui.home

// One surah row in the Quran-tab list (browse + search): the navigation card,
// plus - for surahs with a built "Inside the Surah" experience - the attached
// Read & Tafsir | Journey mode toggle (iOS SurahListRow.swift +
// JourneyModeToggle.swift). The card and the Read tab open the reading view;
// the Journey tab opens the immersive experience (premium-gated, PREMIUM chip -
// never a lock). The Journey tab is alive - a breathing glow, a diagonal
// light-sweep, and embers rising behind the label. Pure chrome: fixed sizes,
// no reading-scale.

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp as lerpDp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.data.SurahExperienceDescriptor
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.Theme

/** Dark ink for the label on the warm accent fill (iOS handoff #14150D). */
private val Ink = Color(0xFF14150D)

/** Warm highlight shared by the sweep and the embers (iOS handoff #FFF6DC). */
private val WarmLight = Color(0xFFFFF6DC)

/**
 * The navigation card plus, for surahs with a built experience, the attached
 * mode toggle. Drop-in replacement for the bare SurahCard in browse + search.
 */
@Composable
fun SurahListRow(
    surah: Surah,
    lang: CommentaryLanguage,
    onOpenSurah: () -> Unit,
    onOpenExperience: (String) -> Unit,
    onShowPaywall: () -> Unit
) {
    val experience = SurahExperienceDescriptor.bySurahNumber(surah.number)?.takeIf { it.available }
    if (experience == null) {
        SurahCard(surah = surah, lang = lang, onClick = onOpenSurah)
        return
    }

    val colors = Theme.colors
    val shape = RoundedCornerShape(20.dp)
    val canAccess = PremiumManager.canAccessSurahExperience(experience.id)

    // Single continuous outline around card + toggle - no seam between them.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, colors.strokeColor, shape)
    ) {
        SurahCard(
            surah = surah,
            lang = lang,
            squaredBottom = true,
            showsBorder = false,
            onClick = onOpenSurah
        )
        JourneyModeToggle(
            locked = !canAccess,
            lang = lang,
            onRead = onOpenSurah,
            onJourney = {
                if (canAccess) onOpenExperience(experience.id) else onShowPaywall()
            }
        )
    }
}

/**
 * The segmented "Read & Tafsir | Journey" control attached under the surah
 * card. Squared top (meets the card above) so the two read as one card.
 */
@Composable
private fun JourneyModeToggle(
    locked: Boolean,
    lang: CommentaryLanguage,
    onRead: () -> Unit,
    onJourney: () -> Unit
) {
    val colors = Theme.colors
    val surfaceFill = if (colors.isMidnightEmerald) colors.glassSurface else Color.White
    val trackShape = RoundedCornerShape(16.dp)
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceFill)
                .padding(horizontal = 16.dp)
                .padding(top = 6.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (locked) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = JourneyStrings.premium(lang).uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = colors.accentColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            // Segmented track (recessed) holding the two equal-width tabs.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(trackShape)
                    .background(colors.glassSurfaceRecessed)
                    .border(1.dp, colors.strokeColor, trackShape)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Read tab (inactive - pushes the reading view).
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .pressable(onClick = onRead)
                        .padding(vertical = 13.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = JourneyStrings.readAndTafsir(lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        color = colors.secondaryText
                    )
                }
                // Journey tab (active - animated, opens the dive).
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedJourneyTab(
                        label = JourneyStrings.journey(lang),
                        onTap = onJourney
                    )
                }
            }
        }
    }
}

/**
 * The filled accent "Journey" tab, alive with three layered ambient
 * animations: a breathing glow, a diagonal light-sweep, and embers rising
 * behind the label. No icon, no arrow.
 */
@Composable
private fun AnimatedJourneyTab(label: String, onTap: () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(11.dp)
    val ambient = rememberInfiniteTransition(label = "journeyTab")

    // Breathing glow - 3.6s full cycle (1.8s each way).
    val breathe by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "breathe"
    )
    // Light-sweep drift - 3.6s, resets off-screen so the loop is seamless.
    val sweep by ambient.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Restart),
        label = "sweep"
    )

    BoxWithConstraints(
        modifier = Modifier
            .shadow(
                elevation = lerpDp(9.dp, 16.dp, breathe),
                shape = shape,
                spotColor = colors.accentColor.copy(alpha = 0.24f + 0.26f * breathe),
                ambientColor = colors.accentColor.copy(alpha = 0.24f + 0.26f * breathe)
            )
            .clip(shape)
            .background(colors.accentGradient)
            // Warm inner glow that swells with the breath.
            .border(1.5.dp, WarmLight.copy(alpha = 0.05f + 0.15f * breathe), shape)
            .pressable(onClick = onTap)
    ) {
        val w = maxWidth
        // A narrow bright band, tilted, passing left -> right across the tab.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(w * 0.38f)
                    .offset(x = w * (-0.53f + 1.44f * sweep))
                    .graphicsLayer { rotationZ = -18f }
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                WarmLight.copy(alpha = 0f),
                                WarmLight.copy(alpha = 0.55f),
                                WarmLight.copy(alpha = 0f)
                            )
                        )
                    )
            )
            // Three faint embers drifting up behind the label.
            RisingEmber(containerWidth = w, size = 3.0f, driftX = 14f, riseY = -10f, durationMs = 3200, delayMs = 150, peak = 0.9f, xFrac = 0.22f)
            RisingEmber(containerWidth = w, size = 2.0f, driftX = -12f, riseY = -12f, durationMs = 3800, delayMs = 550, peak = 0.8f, xFrac = 0.50f)
            RisingEmber(containerWidth = w, size = 2.5f, driftX = 8f, riseY = -14f, durationMs = 3400, delayMs = 1100, peak = 1.0f, xFrac = 0.72f)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = Ink
            )
        }
    }
}

/** One ember: fades in, drifts up-and-sideways, fades out - looping forever. */
@Composable
private fun BoxScope.RisingEmber(
    containerWidth: Dp,
    size: Float,
    driftX: Float,
    riseY: Float,
    durationMs: Int,
    delayMs: Int,
    peak: Float,
    xFrac: Float
) {
    val totalMs = delayMs + durationMs
    val t by rememberInfiniteTransition(label = "ember").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(totalMs, easing = LinearEasing), RepeatMode.Restart),
        label = "emberT"
    )
    val delayFrac = delayMs.toFloat() / totalMs
    val q = if (t < delayFrac) 0f else (t - delayFrac) / (1f - delayFrac)
    val alpha = when {
        t < delayFrac -> 0f
        q < 0.3f -> peak * (q / 0.3f)
        else -> peak * (1f - (q - 0.3f) / 0.7f)
    }
    val dx = driftX * q
    val dy = 6f + (riseY - 6f) * q   // starts just below the anchor, rises

    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = containerWidth * xFrac + dx.dp, y = (dy - 8f).dp)
            .size(size.dp)
            .clip(CircleShape)
            .background(WarmLight.copy(alpha = alpha.coerceIn(0f, 1f)))
    )
}
