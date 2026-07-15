package com.thaqalayn.app.ui.onboarding

// Onboarding page 7: Track Your Progress (iOS ProgressTrackingScreen).
// A scripted demo: a verse card whose read-checkbox ticks itself after 2s,
// followed by a surah progress card counting up to 14%.
// Deviation from iOS: the bottom line reads "saved on this device" instead of
// "syncs across all your devices" - the Android app is local-only (no Supabase).

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.theme.AmiriFamily
import kotlinx.coroutines.delay

@Composable
fun ProgressTrackingPage() {
    var isVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var showProgressCard by remember { mutableStateOf(false) }
    var percentage by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2000)
        isChecked = true
        delay(800)
        showProgressCard = true
        delay(200)
        while (percentage < 14) {
            delay(40)
            percentage += 1
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
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 400, riseDistance = (-20).dp, durationMillis = 600) {
                    Text(
                        text = "Track Your Progress",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Master the Quran, verse by verse",
                        style = onbBody,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            // Demo cards
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 600, riseDistance = 40.dp, durationMillis = 700) {
                    DemoVerseCard(isChecked = isChecked)
                }
                DemoProgressCard(show = showProgressCard, percentage = percentage)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom message (local-only wording; iOS mentions device sync)
            FadeRise(visible = showProgressCard, delayMillis = 300, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    text = "Your progress is saved right on this device",
                    style = onbCaption,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 0.dp)
                )
            }
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}

@Composable
private fun DemoVerseCard(isChecked: Boolean) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(chipGold.fg),
                contentAlignment = Alignment.Center
            ) {
                Text("1", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnbPalette.onGold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = OnbPalette.secondaryText,
                    modifier = Modifier.size(18.dp)
                )
                Icon(
                    Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = OnbPalette.secondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            DemoCheckbox(isChecked = isChecked)
        }

        Text(
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            fontFamily = AmiriFamily,
            fontSize = 26.sp,
            color = OnbPalette.primaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "In the name of Allah, the Most Gracious, the Most Merciful",
            fontSize = 15.sp,
            color = OnbPalette.secondaryText,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun DemoCheckbox(isChecked: Boolean) {
    // Scale pulse on check (iOS 1.0 -> 1.2 -> 1.0 spring).
    var pulse by remember { mutableStateOf(false) }
    LaunchedEffect(isChecked) {
        if (isChecked) {
            pulse = true
            delay(150)
            pulse = false
        }
    }
    val scale by animateFloatAsState(
        targetValue = if (pulse) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "checkboxScale"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isChecked) chipGold.fg.copy(alpha = 0.3f)
                    else Color.White.copy(alpha = 0.045f)
                )
                .border(
                    2.dp,
                    if (isChecked) chipGold.fg else OnbPalette.gold.copy(alpha = 0.16f),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = chipGold.fg,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun DemoProgressCard(show: Boolean, percentage: Int) {
    FadeRise(visible = show, delayMillis = 0, riseDistance = 20.dp, durationMillis = 500) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onboardingCard(padding = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(chipGold.fg),
                contentAlignment = Alignment.Center
            ) {
                Text("1", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnbPalette.onGold)
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Al-Faatiha",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnbPalette.primaryText
                )
                Text(
                    text = "7 verses",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnbPalette.tertiaryText
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = chipGold.fg,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$percentage%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = chipGold.fg
                )
            }
        }
    }
}
