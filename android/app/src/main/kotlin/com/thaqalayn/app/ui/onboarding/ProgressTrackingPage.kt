package com.thaqalayn.app.ui.onboarding

// Onboarding page: Track Your Progress (iOS ProgressTrackingScreen, 9.0). Reading is
// counted by passage now. The mock is a slice of the real surah passage list
// (al-Baqarah, three rows) that replays a leading swipe on the passage being read:
// the gold "Read" action slides out, the row springs back with a checkmark, the
// surah's read count follows, and the home surah card appears with the same count.
// The pager builds this page before it is shown, so the demo runs only while the
// page is current (isActive), always from the unchecked state, looping every ~7s.
// Deviation from iOS: the caption says progress stays on this device (the Android
// app is local-only) and names Finish reading, which is how a passage is marked.

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.components.rememberReduceMotion
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** How far the row travels to reveal the swipe action. */
private val SwipeReveal = 84.dp
private val Night = Color(0xFF0A1512)

@Composable
fun ProgressTrackingPage(isActive: Boolean) {
    val reduceMotion = rememberReduceMotion()
    var isVisible by remember { mutableStateOf(false) }
    val swipe = remember { Animatable(0f) }            // the row's drag, 0..1 of SwipeReveal
    var touchVisible by remember { mutableStateOf(false) }
    var actionPressed by remember { mutableStateOf(false) }
    var adamRead by remember { mutableStateOf(false) }
    var showSurahCard by remember { mutableStateOf(false) }
    var readCount by remember { mutableIntStateOf(3) }
    val demoAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) { isVisible = true }

    suspend fun resetToBefore() {
        swipe.snapTo(0f)
        touchVisible = false
        actionPressed = false
        adamRead = false
        readCount = 3
        showSurahCard = false
        demoAlpha.snapTo(1f)
    }

    LaunchedEffect(isActive, reduceMotion) {
        if (reduceMotion) {
            // No replayed gesture: land on the end state.
            adamRead = true
            readCount = 4
            showSurahCard = true
            return@LaunchedEffect
        }
        resetToBefore()
        if (!isActive) return@LaunchedEffect
        while (true) {
            coroutineScope {
                // Finger lands mid-row after the unchecked row has registered, then drags right.
                delay(1400); touchVisible = true
                delay(300); launch { swipe.animateTo(1f, tween(500)) }
                // Lifts, and the action takes the tap: a brief flash.
                delay(750); touchVisible = false
                delay(100); actionPressed = true
                // The row springs home read.
                delay(150); actionPressed = false
                adamRead = true
                launch { swipe.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)) }
                // The surah's count follows, then the home card appears with the same count.
                delay(600); readCount = 4
                delay(400); showSurahCard = true
                // Dip, reset unseen, and go again.
                delay(3200); demoAlpha.animateTo(0f, tween(300))
                delay(50)
                resetToBefore()
                demoAlpha.snapTo(0f)
                demoAlpha.animateTo(1f, tween(300))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 60.dp, bottom = 24.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 200, riseDistance = 0.dp, durationMillis = 600) {
                    HeroChip(chip = chipGold, pulseDuration = 2.0) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = chipGold.fg, modifier = Modifier.size(40.dp))
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
                    Text(text = "The Quran, passage by passage", style = onbBody, color = OnbPalette.secondaryText)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .graphicsLayer { alpha = demoAlpha.value }
            ) {
                FadeRise(visible = isVisible, delayMillis = 600, riseDistance = 40.dp, durationMillis = 700) {
                    DemoPassageList(
                        swipeFraction = swipe.value,
                        touchVisible = touchVisible,
                        actionPressed = actionPressed,
                        adamRead = adamRead,
                        readCount = readCount
                    )
                }
                DemoSurahCard(show = showSurahCard, readCount = readCount)
            }

            Spacer(modifier = Modifier.weight(1f))

            val captionAlpha by animateFloatAsState(if (showSurahCard) 1f else 0f, tween(600, delayMillis = 300), label = "caption")
            Text(
                text = "Finish reading a passage to mark it read. Your progress is saved right on this device.",
                style = onbCaption,
                lineHeight = 17.sp,
                color = OnbPalette.secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .graphicsLayer { alpha = captionAlpha }
            )
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}

/**
 * A slice of the real surah passage list, built like an inset card: the rows run
 * edge to edge and the card clips them, so the swiped row slides under the card's
 * edge and the Read action sits flush with the leading edge at the row's height.
 */
@Composable
private fun DemoPassageList(
    swipeFraction: Float,
    touchVisible: Boolean,
    actionPressed: Boolean,
    adamRead: Boolean,
    readCount: Int
) {
    val inset = 20.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onboardingCard(padding = 0.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = inset, end = inset, top = 18.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("Al-Baqarah", fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = OnbPalette.primaryText)
            Row {
                Text("The Cow · Medinan · 40 passages · ", fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = OnbPalette.secondaryText)
                CountText("$readCount read", fontSize = 12.5f, color = OnbPalette.secondaryText, weight = FontWeight.Medium)
            }
        }
        DemoRow("The call to worship", "21 to 29", read = true)
        DemoDivider()
        // The swiped row: the Read action grows out of the leading edge behind it.
        Box(modifier = Modifier.fillMaxWidth()) {
            val revealed = SwipeReveal * swipeFraction
            Box(
                modifier = Modifier
                    .matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .width(revealed)
                        .fillMaxHeight()
                        .background(if (actionPressed) Color(0xFFFFF1CB) else OnbPalette.gold),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(
                        modifier = Modifier.width(SwipeReveal),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Night, modifier = Modifier.size(18.dp))
                        Text("Read", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Night)
                    }
                }
            }
            Box(modifier = Modifier.offset(x = revealed)) {
                DemoRow("Adam and the angels", "30 to 39", read = adamRead)
            }
            // The finger: lands mid-row, drags with it, lifts.
            val touchScale by animateFloatAsState(if (touchVisible) 1f else 0.6f, spring(dampingRatio = 0.7f), label = "touch")
            val touchAlpha by animateFloatAsState(if (touchVisible) 1f else 0f, tween(200), label = "touchAlpha")
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-30).dp + revealed)
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = touchScale
                        scaleY = touchScale
                        alpha = touchAlpha
                    }
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.2.dp, Color.White.copy(alpha = 0.55f), CircleShape)
            )
        }
        DemoDivider()
        DemoRow("Children of Israel and the covenant", "40 to 46", read = false)
    }
}

@Composable
private fun DemoDivider() {
    Box(
        modifier = Modifier
            .padding(start = 58.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(OnbPalette.gold.copy(alpha = 0.10f))
    )
}

@Composable
private fun DemoRow(title: String, range: String, read: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("ع", fontFamily = AmiriFamily, fontSize = 19.sp, color = OnbPalette.gold, textAlign = TextAlign.Center, modifier = Modifier.width(26.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = OnbPalette.primaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(range, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = OnbPalette.secondaryText)
        }
        Box(modifier = Modifier.widthIn(min = 52.dp), contentAlignment = Alignment.CenterEnd) {
            val scale by animateFloatAsState(if (read) 1f else 0.4f, spring(dampingRatio = 0.6f), label = "check")
            val alpha by animateFloatAsState(if (read) 1f else 0f, tween(250), label = "checkAlpha")
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = OnbPalette.gold,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }
    }
}

/** The home tab's surah row, appearing once the passage is read. */
@Composable
private fun DemoSurahCard(show: Boolean, readCount: Int) {
    val alpha by animateFloatAsState(if (show) 1f else 0f, tween(500), label = "cardAlpha")
    val rise by animateFloatAsState(if (show) 0f else 20f, tween(500), label = "cardRise")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = rise * density
            }
            .onboardingCard(padding = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(OnbPalette.gold),
            contentAlignment = Alignment.Center
        ) {
            Text("2", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Night)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Al-Baqarah", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = OnbPalette.primaryText)
            CountText("$readCount of 40 passages", fontSize = 13f, color = OnbPalette.tertiaryText, weight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = OnbPalette.gold, modifier = Modifier.size(15.dp))
            CountText("$readCount of 40", fontSize = 16f, color = OnbPalette.gold, weight = FontWeight.Bold)
        }
    }
}

/** A line whose number rolls when it changes (iOS contentTransition(.numericText())). */
@Composable
private fun CountText(text: String, fontSize: Float, color: Color, weight: FontWeight) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (slideInVertically(tween(350)) { it / 2 } + fadeIn(tween(350))) togetherWith
                (slideOutVertically(tween(350)) { -it / 2 } + fadeOut(tween(350)))
        },
        label = "count"
    ) { value ->
        Text(value, fontSize = fontSize.sp, fontWeight = weight, color = color)
    }
}
