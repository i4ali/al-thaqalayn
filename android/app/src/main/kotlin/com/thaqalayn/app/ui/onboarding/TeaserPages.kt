package com.thaqalayn.app.ui.onboarding

// Onboarding pages 3-4: immersive feature teasers (iOS DeepDiveScreen +
// SurahExperienceScreen). Both share one layout: gold eyebrow, serif headline,
// body line, a cycling Arabic hero inside a breathing halo, and a quiet footer.
// Rising gold motes (the Deep Dive feature's signature) float behind both.

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.deepdive.DeepDivePalette
import kotlinx.coroutines.delay

// MARK: - Deep Dive teaser (page 3)

private val deepDiveThemes = listOf(
    "يَقِين" to "Certainty",
    "صَبْر" to "Patience",
    "شُكْر" to "Gratitude",
    "تَوَكُّل" to "Reliance",
    "إِخْلَاص" to "Sincerity"
)

@Composable
fun DeepDiveTeaserPage() {
    TeaserScaffold(
        eyebrow = "Deep Dive",
        title = "Dive deep into\na single theme",
        body = "A virtue, a word from the Qur'an, a question of the heart - explored layer by layer in an immersive descent.",
        footer = "Find Deep Dives in the Journey tab",
        cycleMillis = 2200L,
        itemCount = deepDiveThemes.size
    ) { index ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = deepDiveThemes[index].first,
                style = onbArabic(78),
                color = OnbPalette.cream,
                maxLines = 1
            )
            Text(
                text = deepDiveThemes[index].second.uppercase(),
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                letterSpacing = 3.5.sp,
                color = OnbPalette.gold
            )
        }
    }
}

// MARK: - Inside the Surah teaser (page 4)

private data class TeaserSurah(val ar: String, val en: String, val story: String)

private val teaserSurahs = listOf(
    TeaserSurah("يُوسُف", "Surah Yusuf", "The most beautiful of stories - loss, patience, reunion."),
    TeaserSurah("يس", "Surah Yasin", "The heart of the Qur'an - and what it keeps asking you."),
    TeaserSurah("الرَّحْمَٰن", "Surah al-Rahman", "One question, asked thirty-one times."),
    TeaserSurah("الْمُلْك", "Surah al-Mulk", "The protector - whose hand holds the kingdom.")
)

@Composable
fun SurahExperienceTeaserPage() {
    TeaserScaffold(
        eyebrow = "Inside the Surah",
        title = "Step inside\na whole surah",
        body = "Not scattered verses, but one chapter lived from the first word to the last - its story, its turns, the questions it keeps asking you.",
        footer = "Find surah experiences in the Journey tab",
        cycleMillis = 2600L,
        itemCount = teaserSurahs.size,
        heroHeight = 250.dp
    ) { index ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = teaserSurahs[index].ar,
                style = onbArabic(68),
                color = OnbPalette.cream,
                maxLines = 1
            )
            Text(
                text = teaserSurahs[index].en.uppercase(),
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                letterSpacing = 3.sp,
                color = OnbPalette.gold
            )
            Text(
                text = teaserSurahs[index].story,
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = DeepDivePalette.mute,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
            )
        }
    }
}

// MARK: - Shared scaffold

@Composable
private fun TeaserScaffold(
    eyebrow: String,
    title: String,
    body: String,
    footer: String,
    cycleMillis: Long,
    itemCount: Int,
    heroHeight: androidx.compose.ui.unit.Dp = 190.dp,
    heroContent: @Composable (Int) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isVisible = true
        while (true) {
            delay(cycleMillis)
            index = (index + 1) % itemCount
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()
        OnbMotes(count = 14)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp)
                .padding(top = 62.dp, bottom = 68.dp)
        ) {
            // Header
            FadeRise(visible = isVisible, delayMillis = 150, riseDistance = (-16).dp, durationMillis = 600) {
                Text(
                    text = eyebrow.uppercase(),
                    style = onbEyebrow,
                    color = OnbPalette.gold
                )
            }
            FadeRise(visible = isVisible, delayMillis = 280, riseDistance = 26.dp, durationMillis = 600) {
                Text(
                    text = title,
                    style = onbHeroTitle,
                    lineHeight = 34.sp,
                    color = OnbPalette.primaryText,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
            FadeRise(visible = isVisible, delayMillis = 420, riseDistance = 20.dp, durationMillis = 600) {
                Text(
                    text = body,
                    style = onbBody,
                    color = OnbPalette.secondaryText,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cycling hero inside a breathing halo
            FadeRise(visible = isVisible, delayMillis = 550, riseDistance = 0.dp, durationMillis = 700) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeight),
                    contentAlignment = Alignment.Center
                ) {
                    val pulse = rememberInfiniteTransition(label = "teaserHalo")
                    val haloScale by pulse.animateFloat(
                        initialValue = 0.96f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
                        label = "teaserHaloScale"
                    )
                    Box(
                        modifier = Modifier
                            .size(270.dp)
                            .graphicsLayer { scaleX = haloScale; scaleY = haloScale }
                            .background(
                                Brush.radialGradient(
                                    listOf(OnbPalette.gold.copy(alpha = 0.15f), Color.Transparent)
                                )
                            )
                    )
                    AnimatedContent(
                        targetState = index,
                        transitionSpec = {
                            (fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 6 })
                                .togetherWith(fadeOut(tween(700)) + slideOutVertically(tween(700)) { -it / 6 })
                        },
                        label = "teaserHero"
                    ) { i ->
                        heroContent(i)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            FadeRise(visible = isVisible, delayMillis = 900, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    text = footer,
                    style = onbCaption,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
