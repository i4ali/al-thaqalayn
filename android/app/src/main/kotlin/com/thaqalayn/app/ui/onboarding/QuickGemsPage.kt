package com.thaqalayn.app.ui.onboarding

// Onboarding page 6: Gems (iOS QuickGemsScreen).
// A live demo of the Gems feature on Ayat al-Kursi (2:255): the Arabic opening
// with per-concept highlighting, four concept bubbles that take turns lighting
// up, and an insight card that follows the highlighted concept.

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.text.font.FontFamily
import com.thaqalayn.app.ui.theme.AmiriFamily
import kotlinx.coroutines.delay

private data class DemoConcept(
    val icon: ImageVector,
    val title: String,
    val color: Color,
    val coreInsight: String,
    val whyItMatters: String
)

private val demoConcepts = listOf(
    DemoConcept(
        Icons.Filled.WorkspacePremium, "The Throne Verse", Color(0xFF9B8FBF),
        "This is Ayat al-Kursi, the greatest verse in the Quran, describing Allah's absolute sovereignty, knowledge, and power over all creation.",
        "Understanding Allah's complete authority brings peace, removes fear of creation, and centers our worship on the only true Power."
    ),
    DemoConcept(
        Icons.Filled.AllInclusive, "The Ever-Living", Color(0xFF7BC47F),
        "Allah is Al-Hayy (The Ever-Living) and Al-Qayyum (The Self-Sustaining), needing no sleep or rest, eternally maintaining all existence.",
        "Unlike creation which needs rest and sustenance, Allah is eternally vigilant and self-sufficient, constantly upholding the universe."
    ),
    DemoConcept(
        Icons.Filled.Public, "Cosmic Ownership", Color(0xFF64B5F6),
        "Everything in the heavens and earth belongs to Allah absolutely. No one owns anything independently; all possession is temporary trust.",
        "Recognizing Allah's complete ownership liberates us from attachment and makes us grateful stewards rather than possessive owners."
    ),
    DemoConcept(
        Icons.Filled.Star, "The Kursi", Color(0xFFE8B86D),
        "Allah's Kursi extends over the heavens and earth, and preserving them does not tire Him - a display of incomprehensible majesty.",
        "The vastness of Allah's Kursi dwarfs all creation, reminding us of our smallness and His infinite greatness."
    )
)

@Composable
fun QuickGemsPage() {
    var isVisible by remember { mutableStateOf(false) }
    var highlighted by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isVisible = true
        while (true) {
            delay(1500)
            highlighted = (highlighted + 1) % demoConcepts.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 60.dp, bottom = 24.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 200, riseDistance = 0.dp, durationMillis = 600) {
                    HeroChip(chip = chipGold, pulseDuration = 2.0) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 400, riseDistance = (-20).dp, durationMillis = 600) {
                    Text(
                        text = "Gems",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Precious insights unveiled",
                        style = onbBody,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            // Demo verse card + insight card
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 600, riseDistance = 40.dp, durationMillis = 700) {
                    DemoVerseCardGems(highlighted = highlighted)
                }
                FadeRise(visible = isVisible, delayMillis = 800, riseDistance = 0.dp, durationMillis = 600) {
                    DemoInsightCard(concept = demoConcepts[highlighted])
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DemoVerseCardGems(highlighted: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard(padding = 16.dp)
    ) {
        // Verse reference
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(chipGold.fg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "255",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnbPalette.onGold
                )
            }
            Text(
                text = "Al-Baqarah 255",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnbPalette.secondaryText
            )
        }

        HighlightedArabicVerse(highlighted = highlighted)

        // Concept bubbles, 2x2
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (rowStart in listOf(0, 2)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in rowStart until rowStart + 2) {
                        Box(modifier = Modifier.weight(1f)) {
                            DemoConceptBubble(
                                concept = demoConcepts[i],
                                isHighlighted = highlighted == i
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ayat al-Kursi opening with the four segments tinted toward the highlighted
 * concept's colour (iOS HighlightedArabicVerse).
 */
@Composable
private fun HighlightedArabicVerse(highlighted: Int) {
    val highlightColor = demoConcepts[highlighted].color

    @Composable
    fun segColor(active: Boolean): Color {
        val target = if (active) highlightColor else OnbPalette.primaryText
        val animated by animateColorAsState(target, tween(300), label = "segColor")
        return animated
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                "ٱللَّهُ ",
                fontFamily = AmiriFamily, fontSize = 24.sp,
                color = segColor(highlighted == 0)
            )
            Text(
                "لَآ إِلَٰهَ إِلَّا هُوَ ",
                fontFamily = AmiriFamily, fontSize = 24.sp,
                color = segColor(highlighted == 0 || highlighted == 2)
            )
            Text(
                "ٱلْحَىُّ ",
                fontFamily = AmiriFamily, fontSize = 24.sp,
                color = segColor(highlighted == 0 || highlighted == 1)
            )
            Text(
                "ٱلْقَيُّومُ",
                fontFamily = AmiriFamily, fontSize = 24.sp,
                color = segColor(highlighted == 0 || highlighted == 3)
            )
        }
    }
}

@Composable
private fun DemoConceptBubble(concept: DemoConcept, isHighlighted: Boolean) {
    val scale by animateFloatAsState(if (isHighlighted) 1.05f else 1.0f, tween(400), label = "bubbleScale")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(
                if (isHighlighted) Modifier.shadow(
                    8.dp, RoundedCornerShape(50),
                    ambientColor = concept.color.copy(alpha = 0.4f),
                    spotColor = concept.color.copy(alpha = 0.4f)
                ) else Modifier
            )
            .clip(RoundedCornerShape(50))
            .background(concept.color.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(
            concept.icon,
            contentDescription = null,
            tint = concept.color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = concept.title,
            style = onbPill,
            color = OnbPalette.primaryText,
            maxLines = 1
        )
    }
}

@Composable
private fun DemoInsightCard(concept: DemoConcept) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard(padding = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                concept.icon,
                contentDescription = null,
                tint = concept.color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = concept.title.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = OnbPalette.primaryText
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "The Core Insight:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = concept.color
            )
            Text(
                text = concept.coreInsight,
                style = onbSerif(15, FontWeight.Medium),
                lineHeight = 21.sp,
                color = OnbPalette.primaryText
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Why it matters:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = concept.color
            )
            Text(
                text = concept.whyItMatters,
                style = onbSerif(15, FontWeight.Medium),
                lineHeight = 21.sp,
                color = OnbPalette.primaryText
            )
        }
    }
}
