package com.thaqalayn.app.ui.onboarding

// Onboarding page 9: Special Seasons (iOS SeasonalFeaturesScreen).
// Spotlights the current season (the active one, or the soonest to open) and
// lists the rest as a quiet "year ahead", driven by the shared JourneyCatalog
// ordering so it always agrees with the Journey hub.

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.R
import com.thaqalayn.app.data.JourneyDescriptor
import com.thaqalayn.app.data.JourneyStatus
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.journey.journeyUiConfig
import com.thaqalayn.app.ui.strings.JourneyStrings
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SeasonalFeaturesPage() {
    var isVisible by remember { mutableStateOf(false) }
    var showCards by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(800)
        showCards = true
    }

    val seasons = remember { JourneyDescriptor.orderedByStatus() }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SeasonalHeader(isVisible = isVisible)

            seasons.firstOrNull()?.let { (descriptor, status) ->
                FadeRise(visible = showCards, delayMillis = 100, riseDistance = 24.dp, durationMillis = 500) {
                    SeasonSpotlightHero(
                        descriptor = descriptor,
                        status = status,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                FadeRise(visible = showCards, delayMillis = 250, riseDistance = 24.dp, durationMillis = 500) {
                    YearAheadSection(
                        items = seasons.drop(1),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FadeRise(visible = showCards, delayMillis = 600, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    text = "Every season lives in the Journey tab -\neach opens in its blessed time",
                    style = onbCaption,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
            Spacer(modifier = Modifier.size(84.dp))
        }
    }
}

// MARK: - Header (moon + pulsing stars)

@Composable
private fun SeasonalHeader(isVisible: Boolean) {
    val gold = OnbPalette.gold

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(top = 34.dp, bottom = 18.dp)
    ) {
        FadeRise(visible = isVisible, delayMillis = 200, riseDistance = 0.dp, durationMillis = 600) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                // Pulsing stars scattered around the moon
                val starSizes = listOf(10, 8, 12, 9, 11)
                val starX = listOf(-40, 35, -25, 45, -50)
                val starY = listOf(-35, -40, 30, 25, -10)
                val starDurations = listOf(1800, 2200, 1500, 2000, 2400)
                for (i in 0 until 5) {
                    val pulse = rememberInfiniteTransition(label = "star$i")
                    val starAlpha by pulse.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            tween(starDurations[i], delayMillis = i * 200),
                            RepeatMode.Reverse
                        ),
                        label = "starAlpha$i"
                    )
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFF2C969).copy(alpha = 0.6f),
                        modifier = Modifier
                            .offset(x = starX[i].dp, y = starY[i].dp)
                            .size(starSizes[i].dp)
                            .alpha(starAlpha)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(gold.copy(alpha = 0.22f), gold.copy(alpha = 0.05f))
                            )
                        )
                        .border(1.dp, gold.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    PhosphorIcon(resId = R.drawable.ph_moon_stars_fill, size = 26.dp, tint = gold)
                }
            }
        }

        FadeRise(visible = isVisible, delayMillis = 400, riseDistance = (-20).dp, durationMillis = 600) {
            Text(
                text = "Special Seasons",
                style = onbHeroTitle,
                color = OnbPalette.primaryText,
                textAlign = TextAlign.Center
            )
        }
        FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
            Text(
                text = "A guided journey for every sacred time",
                style = onbBody,
                color = OnbPalette.secondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}

// MARK: - Spotlight hero (the current season)

@Composable
private fun SeasonSpotlightHero(
    descriptor: JourneyDescriptor,
    status: JourneyStatus,
    modifier: Modifier = Modifier
) {
    val gold = OnbPalette.gold
    val isActive = status.isActive
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                if (isActive) 22.dp else 16.dp, shape,
                ambientColor = if (isActive) gold.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.35f),
                spotColor = if (isActive) gold.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, gold.copy(alpha = if (isActive) 0.4f else 0.12f), shape)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(gold.copy(alpha = 0.26f), gold.copy(alpha = 0.05f))
                        )
                    )
                    .border(1.dp, gold.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    journeyUiConfig(descriptor.id).icon,
                    contentDescription = null,
                    tint = gold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = descriptor.eyebrow.uppercase(),
                style = onbEyebrow,
                color = gold.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = JourneyStrings.title(descriptor.id, CommentaryLanguage.ENGLISH),
                style = onbHeroTitle,
                color = OnbPalette.primaryText,
                modifier = Modifier.padding(top = 4.dp, bottom = 13.dp)
            )

            StatusPill(status)

            Text(
                text = seasonBlurb(descriptor.id),
                style = onbBody,
                color = OnbPalette.secondaryText,
                modifier = Modifier.padding(top = 14.dp)
            )
        }

        if (!isActive) {
            Text(
                text = "NEXT UP",
                style = onbPill,
                color = gold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .border(1.dp, gold.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun StatusPill(status: JourneyStatus) {
    val gold = OnbPalette.gold
    when (status) {
        is JourneyStatus.Active -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFF3DFA6), Color(0xFFE3C078)))
                )
                .padding(horizontal = 13.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A1512))
            )
            Text(text = "Open now", style = onbPill, color = Color(0xFF0A1512))
        }

        is JourneyStatus.ComingSoon -> SoftPill("Coming soon · " + compactTiming(status))
        is JourneyStatus.Ended -> SoftPill("Returns " + compactTiming(status))
    }
}

@Composable
private fun SoftPill(text: String) {
    val gold = OnbPalette.gold
    Text(
        text = text,
        style = onbPill,
        color = gold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(gold.copy(alpha = 0.12f))
            .border(1.dp, gold.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 13.dp, vertical = 6.dp)
    )
}

// MARK: - The Year Ahead (the other seasons)

@Composable
private fun YearAheadSection(
    items: List<Pair<JourneyDescriptor, JourneyStatus>>,
    modifier: Modifier = Modifier
) {
    val gold = OnbPalette.gold

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = "THE YEAR AHEAD",
                style = onbEyebrow,
                color = gold.copy(alpha = 0.55f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(gold.copy(alpha = 0.14f))
            )
        }

        items.forEachIndexed { index, (descriptor, status) ->
            YearAheadRow(descriptor, status)
            if (index < items.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Composable
private fun YearAheadRow(descriptor: JourneyDescriptor, status: JourneyStatus) {
    val gold = OnbPalette.gold

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(gold.copy(alpha = 0.08f))
                .border(1.dp, gold.copy(alpha = 0.14f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                journeyUiConfig(descriptor.id).icon,
                contentDescription = null,
                tint = gold,
                modifier = Modifier.size(17.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = JourneyStrings.title(descriptor.id, CommentaryLanguage.ENGLISH),
                style = onbRowTitle,
                color = OnbPalette.primaryText
            )
            Text(
                text = descriptor.eyebrow,
                style = onbCaption,
                color = OnbPalette.tertiaryText
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = compactTiming(status),
            style = onbCaption,
            fontWeight = FontWeight.SemiBold,
            color = gold.copy(alpha = 0.6f)
        )
    }
}

// MARK: - Copy (iOS SeasonCopy)

/** One evocative line per season, keyed by JourneyDescriptor.id. */
private fun seasonBlurb(id: String): String = when (id) {
    "ramadan" -> "Thirty days of duas, curated verses and nightly reflection."
    "hajj" -> "The ten blessed days - amaal, Arafah and the Du'a of Imam Husayn (AS)."
    "muharram" -> "Imam Husayn (AS) on the road to Ashura - duas, ziyarat and reflection."
    "fatimiyya" -> "Mourning az-Zahra (AS) - duas, ziyarat and remembrance."
    "arbaeen" -> "The forty-day road to Karbala - ziyarat, duas and reflection."
    else -> ""
}

/** A short, friendly countdown: "Open now", "soon", "tomorrow", "in 12 days", ... */
private fun compactTiming(status: JourneyStatus): String {
    val days = when (status) {
        is JourneyStatus.Active -> return "Open now"
        is JourneyStatus.ComingSoon -> status.daysUntil
        is JourneyStatus.Ended -> status.daysUntil
    }
    if (days <= 0) return "soon"
    if (days == 1) return "tomorrow"
    if (days <= 45) return "in $days days"
    val months = (days / 30.4).roundToInt()
    return if (months >= 12) "next year" else "in $months months"
}
