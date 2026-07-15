package com.thaqalayn.app.ui.onboarding

// Onboarding page 1: Hadith of Thaqalayn (iOS HadithScreen).
// Cinematic hero: the bundled 7.25s seamless loop of doves over the floodlit
// shrine (res/raw/shrine_hero_loop.mp4, muted, aspect-fill) under a soft scrim,
// with the hadith on deep emerald glass. Falls back to the procedural
// FloatingEmbers layer if the player cannot start. Auto-advances after 10s
// (only while still on this page); any tap advances immediately.

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.thaqalayn.app.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun HadithPage(isCurrent: Boolean, onAdvance: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    var videoFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    // Auto-advance after 10s - only if the user is still on this first page
    // (iOS guard: the delayed closure must not yank the user back).
    LaunchedEffect(isCurrent) {
        if (isCurrent) {
            delay(10_000)
            if (isCurrent) onAdvance()
        }
    }

    val entrance = remember { Animatable(0f) }
    LaunchedEffect(isVisible) { if (isVisible) entrance.animateTo(1f, tween(800)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onAdvance() }
    ) {
        OnboardingBackground()

        if (!videoFailed) {
            ShrineHeroVideoLayer(
                isActive = isCurrent,
                onError = { videoFailed = true },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(entrance.value)
            )
        } else {
            FloatingEmbers(modifier = Modifier.fillMaxSize().alpha(entrance.value))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                TitleWithGlow(isVisible = isVisible)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .padding(horizontal = 22.dp)
                        .clip(RoundedCornerShape(24.dp))
                        // Deep emerald glass so the hadith reads clearly over the
                        // bright gold dome of the hero video (iOS 06120E @ 52%).
                        .background(Color(0xFF06120E).copy(alpha = 0.52f))
                        .border(1.dp, OnbPalette.gold.copy(alpha = 0.13f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    FadeRise(visible = isVisible, delayMillis = 600) {
                        Text(
                            text = "إني تارك فيكم الثقلين:\nكتاب الله وعترتي أهل بيتي،\nما إن تمسكتم بهما\nلن تضلوا بعدي أبداً",
                            style = onbArabic(26),
                            color = OnbPalette.primaryText,
                            textAlign = TextAlign.Center,
                            lineHeight = 46.sp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }

                    FadeRise(visible = isVisible, delayMillis = 900) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(OnbPalette.gold)
                        )
                    }

                    FadeRise(visible = isVisible, delayMillis = 1100) {
                        Text(
                            text = "\"I am leaving among you two weighty things:\nthe Book of Allah and my progeny,\nthe people of my household.\nAs long as you hold fast to them,\nyou shall never go astray.\"",
                            style = onbSerifItalic(22),
                            color = OnbPalette.creamBright,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                    }

                    FadeRise(visible = isVisible, delayMillis = 1400) {
                        Text(
                            text = "- Prophet Muhammad ﷺ",
                            fontSize = 14.sp,
                            color = OnbPalette.secondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FadeRise(visible = isVisible, delayMillis = 1700, riseDistance = 0.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(bottom = 40.dp)
                ) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = OnbPalette.tertiaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Swipe or tap to continue",
                        fontSize = 13.sp,
                        color = OnbPalette.tertiaryText
                    )
                }
            }
        }
    }
}

// MARK: - Title with breathing glow (shimmer omitted; the glow carries the effect)

@Composable
private fun TitleWithGlow(isVisible: Boolean) {
    val pulse = rememberInfiniteTransition(label = "titleGlow")
    val glowScale by pulse.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "titleGlowScale"
    )

    FadeRise(visible = isVisible, delayMillis = 300) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 80.dp)
                    .graphicsLayer { scaleX = glowScale; scaleY = glowScale }
                    .background(
                        Brush.radialGradient(
                            listOf(OnbPalette.gold.copy(alpha = 0.16f), Color.Transparent)
                        )
                    )
            )
            Text(
                text = "Hadith of Thaqalayn",
                style = onbHeroTitle,
                color = OnbPalette.primaryText
            )
        }
    }
}

// MARK: - Shared fade+rise entrance (iOS .opacity/.offset(y:)/.animation idiom)

@Composable
internal fun FadeRise(
    visible: Boolean,
    delayMillis: Int,
    riseDistance: androidx.compose.ui.unit.Dp = 30.dp,
    durationMillis: Int = 800,
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMillis.toLong())
            progress.animateTo(1f, tween(durationMillis))
        }
    }
    val risePx = with(androidx.compose.ui.platform.LocalDensity.current) { riseDistance.toPx() }
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * risePx
        }
    ) {
        content()
    }
}

// MARK: - Video hero (iOS ShrineHeroVideoLayer)

@Composable
private fun ShrineHeroVideoLayer(
    isActive: Boolean,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.shrine_hero_loop}"))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            prepare()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                onError()
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) player.play() else player.pause()
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        // Soft scrim: darken the title zone and the bottom hint zone (iOS stops).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Black.copy(alpha = 0.28f),
                        0.22f to Color.Black.copy(alpha = 0.04f),
                        0.58f to Color.Black.copy(alpha = 0.04f),
                        1.00f to Color.Black.copy(alpha = 0.40f)
                    )
                )
        )
    }
}

// MARK: - Floating embers fallback (iOS FloatingEmbers)

private class Ember(random: Random) {
    val baseX = 0.04 + random.nextDouble() * 0.92
    val startY = random.nextDouble()
    val radius = 3.0 + random.nextDouble() * 10.0
    val speed = 0.05 + random.nextDouble() * 0.035
    val swayAmp = 6.0 + random.nextDouble() * 16.0
    val swayFreq = 0.15 + random.nextDouble() * 0.30
    val phase = random.nextDouble() * 2 * PI
    val peakOpacity = 0.12 + (radius / 13.0) * 0.38
    val twinkleFreq = 0.3 + random.nextDouble() * 0.6
}

@Composable
private fun FloatingEmbers(modifier: Modifier = Modifier) {
    val embers = remember { List(16) { Ember(Random(it * 733 + 41)) } }
    val timeSec by produceState(0.0) {
        while (true) {
            withInfiniteAnimationFrameNanos { value = it / 1_000_000_000.0 }
        }
    }
    val gold = Color(0xFFECD49A)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        for (e in embers) {
            var p = (e.startY - e.speed * timeSec).mod(1.0)
            if (p < 0) p += 1.0
            val y = (p * h).toFloat()
            val x = (e.baseX * w + sin(timeSec * e.swayFreq + e.phase) * e.swayAmp.dp.toPx()).toFloat()
            val envelope = sin(p * PI)
            val twinkle = 0.78 + 0.22 * sin(timeSec * e.twinkleFreq + e.phase)
            val opacity = (e.peakOpacity * envelope * twinkle).coerceAtLeast(0.0).toFloat()
            val r = e.radius.dp.toPx().toFloat()

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(gold.copy(alpha = opacity * 0.6f), Color.Transparent),
                    center = Offset(x, y),
                    radius = r
                ),
                radius = r,
                center = Offset(x, y)
            )
            drawCircle(
                color = gold.copy(alpha = min(opacity * 1.5f, 0.9f)),
                radius = r * 0.32f,
                center = Offset(x, y)
            )
        }
    }
}
