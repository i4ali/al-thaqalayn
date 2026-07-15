package com.thaqalayn.app.ui.onboarding

// Onboarding page 5: Five Layers of Wisdom (iOS FiveLayersScreen).
// The five tafsir layers as one colour-coded stack (icon + name + short tag),
// with a rotated "FIVE LENSES" axis label framing them as five parallel angles
// on one verse.

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.R
import com.thaqalayn.app.ui.components.PhosphorIcon

private data class Lens(val iconRes: Int, val title: String, val tag: String, val chip: OnbChip)

private val lenses = listOf(
    Lens(R.drawable.ph_bank_fill, "Foundation", "the basics", chipFoundation),
    Lens(R.drawable.ph_books_fill, "Classical Shia", "Tabatabai & Tabrisi", chipKnowledge),
    Lens(R.drawable.ph_globe_hemisphere_west_fill, "Contemporary", "modern & scientific", chipProgress),
    Lens(R.drawable.ph_star_fill, "Ahlul Bayt", "the 14 Infallibles", chipBrand),
    Lens(R.drawable.ph_scales_fill, "Comparative", "Shia & Sunni", chipComparative)
)

@Composable
fun FiveLayersPage() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val gold = OnbPalette.gold

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 50, riseDistance = 0.dp, durationMillis = 600) {
                    HeroChip(chip = chipGold, pulseDuration = 2.4) {
                        Icon(
                            Icons.Filled.Layers,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 150, riseDistance = (-20).dp, durationMillis = 600) {
                    Text(
                        text = "5 Layers of Wisdom",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 300, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "See every verse from every angle.",
                        style = onbBody,
                        color = OnbPalette.secondaryText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // "Five lenses" axis + the colour-coded stack
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(horizontal = 26.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Box(
                        modifier = Modifier.width(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FIVE LENSES",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = gold,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            // requiredWidth escapes the 20dp column so the label
                            // measures at full length before rotating vertical.
                            modifier = Modifier
                                .requiredWidth(160.dp)
                                .rotate(-90f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, gold.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                ) {
                    lenses.forEachIndexed { index, lens ->
                        FadeRise(
                            visible = isVisible,
                            delayMillis = 450 + index * 100,
                            riseDistance = 22.dp,
                            durationMillis = 550
                        ) {
                            Stratum(lens, isLast = index == lenses.size - 1)
                        }
                    }
                }
            }
        }
    }
}

/** One flush stratum: leading accent strip, icon, name, tag, hairline separator. */
@Composable
private fun Stratum(lens: Lens, isLast: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.06f))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(start = 0.dp, end = 15.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(72.dp)
                    .background(lens.chip.fg)
            )
            Box(modifier = Modifier.width(30.dp).padding(start = 8.dp), contentAlignment = Alignment.Center) {
                PhosphorIcon(resId = lens.iconRes, size = 22.dp, tint = lens.chip.fg)
            }
            Text(
                text = lens.title,
                style = onbCardTitle,
                color = OnbPalette.primaryText,
                modifier = Modifier.padding(start = 6.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = lens.tag,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = OnbPalette.secondaryText
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(OnbPalette.gold.copy(alpha = 0.08f))
            )
        }
    }
}
