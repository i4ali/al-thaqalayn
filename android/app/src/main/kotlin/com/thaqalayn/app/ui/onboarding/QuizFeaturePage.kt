package com.thaqalayn.app.ui.onboarding

// Onboarding page 8: Test Your Knowledge (iOS QuizFeatureScreen).
// A scripted quiz demo: a Foundation-layer question appears, answer B selects
// itself, the correct feedback flashes, then the card gives way to a Scholar
// Level result whose score counts up to 9/10.

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Psychology
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

private val quizOptions = listOf(
    "A" to "The physical throne of Allah",
    "B" to "Allah's knowledge and authority",
    "C" to "A type of angel",
    "D" to "The heavens"
)

@Composable
fun QuizFeaturePage() {
    var isVisible by remember { mutableStateOf(false) }
    var showQuestion by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(800)
        showQuestion = true
        delay(1700)
        selectedAnswer = "B"
        delay(500)
        showFeedback = true
        delay(1500)
        showResult = true
        delay(300)
        while (score < 9) {
            delay(80)
            score += 1
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
                            Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = chipGold.fg,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                FadeRise(visible = isVisible, delayMillis = 400, riseDistance = (-20).dp, durationMillis = 600) {
                    Text(
                        text = "Test Your Knowledge",
                        style = onbHeroTitle,
                        color = OnbPalette.primaryText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Quizzes for every surah",
                        style = onbBody,
                        color = OnbPalette.secondaryText
                    )
                }
            }

            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Crossfade(targetState = showResult, label = "quizDemo") { result ->
                    if (!result) {
                        FadeRise(visible = showQuestion, delayMillis = 0, riseDistance = 40.dp, durationMillis = 600) {
                            DemoQuestionCard(
                                selectedAnswer = selectedAnswer,
                                showFeedback = showFeedback
                            )
                        }
                    } else {
                        DemoResultCard(score = score)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FadeRise(visible = isVisible, delayMillis = 800, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    text = "Deepen your understanding through reflection",
                    style = onbCaption,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}

@Composable
private fun DemoQuestionCard(selectedAnswer: String?, showFeedback: Boolean) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard()
    ) {
        // Layer badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(chipGold.bg)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.Filled.Layers,
                contentDescription = null,
                tint = chipGold.fg,
                modifier = Modifier.size(12.dp)
            )
            Text(text = "Foundation", style = onbPill, color = chipGold.fg)
        }

        Text(
            text = "What does 'Kursi' represent in Ayat al-Kursi?",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnbPalette.primaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            quizOptions.forEach { (letter, text) ->
                DemoAnswerOption(
                    letter = letter,
                    text = text,
                    isSelected = selectedAnswer == letter,
                    isCorrect = letter == "B",
                    showFeedback = showFeedback && selectedAnswer != null
                )
            }
        }
    }
}

@Composable
private fun DemoAnswerOption(
    letter: String,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean
) {
    val green = Color(0xFF3E9B79)
    val red = Color(0xFFF47875)

    val backgroundColor = when {
        showFeedback && isCorrect -> green.copy(alpha = 0.2f)
        showFeedback && isSelected -> red.copy(alpha = 0.2f)
        isSelected -> chipGold.fg.copy(alpha = 0.2f)
        else -> Color.White.copy(alpha = 0.045f)
    }
    val borderColor = when {
        showFeedback && isCorrect -> green
        showFeedback && isSelected -> red
        isSelected -> chipGold.fg
        else -> OnbPalette.gold.copy(alpha = 0.16f)
    }
    val emphasized = isSelected || (showFeedback && isCorrect)

    val scale by animateFloatAsState(
        targetValue = if (isSelected && !showFeedback) 1.02f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "optionScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(if (emphasized) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(borderColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (emphasized) borderColor else OnbPalette.secondaryText
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnbPalette.primaryText,
            maxLines = 1
        )
        Spacer(modifier = Modifier.weight(1f))
        if (showFeedback && isCorrect) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = green,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DemoResultCard(score: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard(padding = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(chipGold.bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Book,
                contentDescription = null,
                tint = chipGold.fg,
                modifier = Modifier.size(44.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Scholar Level",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnbPalette.primaryText
            )
            Text(
                text = "عالم",
                fontFamily = AmiriFamily,
                fontSize = 18.sp,
                color = chipGold.fg
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$score",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = chipGold.fg
            )
            Text(
                text = "/10",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = OnbPalette.secondaryText,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Text(
            text = "Excellent understanding!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = OnbPalette.secondaryText,
            textAlign = TextAlign.Center
        )
    }
}
