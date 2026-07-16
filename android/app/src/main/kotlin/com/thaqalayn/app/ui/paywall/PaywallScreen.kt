package com.thaqalayn.app.ui.paywall

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.DeepDiveDescriptor
import com.thaqalayn.app.data.SurahExperienceDescriptor
import com.thaqalayn.app.ui.components.fullBleed
import com.thaqalayn.app.ui.components.rememberReduceMotion
import com.thaqalayn.app.ui.journey.journeyUiConfig
import kotlinx.coroutines.delay
import com.thaqalayn.app.premium.BillingManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

private data class LayerInfo(
    val number: Int,
    val name: String,
    val tagline: String,
    val isGold: Boolean = false
)

private val layers = listOf(
    LayerInfo(1, "Foundation", "Historical context & basics"),
    LayerInfo(2, "Classical Shia", "Tabatabai & Tabrisi"),
    LayerInfo(3, "Contemporary", "Modern perspectives"),
    LayerInfo(4, "Ahlul Bayt", "Wisdom of the Infallibles"),
    LayerInfo(5, "Comparative", "Shia & Sunni, side by side", isGold = true)
)

private data class FeatureRow(val icon: ImageVector, val title: String, val subtitle: String)

// Fixed light hero-text colors regardless of theme (the art is dark in both).
private val PaywallIvory = Color(0xFFF1E8D6)
private val PaywallGold = Color(0xFFECD49A)

private val journeyIds = setOf("ramadan", "hajj", "muharram", "fatimiyya", "arbaeen")

/**
 * Resolve a locked entry's cover for the hero band (journey / deep-dive /
 * surah-experience id). Null - unknown key or none - falls back to the dome.
 */
private fun paywallContextCover(key: String?): Int? = when {
    key == null -> null
    key in journeyIds -> journeyUiConfig(key).coverRes
    else -> DeepDiveDescriptor.byId(key)?.coverRes
        ?: SurahExperienceDescriptor.byId(key)?.coverRes
}

/** Premium paywall: art hero band, 5-layer ladder, features, CTA (iOS PaywallView). */
@Composable
fun PaywallScreen(navController: NavHostController, contextCoverKey: String? = null) {
    val colors = Theme.colors
    val activity = LocalContext.current as? Activity
    val reduceMotion = rememberReduceMotion()
    val contextCover = remember(contextCoverKey) { paywallContextCover(contextCoverKey) }

    // Close automatically once the purchase lands.
    LaunchedEffect(PremiumManager.isPremium) {
        if (PremiumManager.isPremium && BillingManager.purchaseSuccess) {
            BillingManager.purchaseSuccess = false
            navController.popBackStack()
        }
    }

    val features = listOf(
        FeatureRow(Icons.Filled.MenuBook, "5 Layers of Tafsir", "All 114 surahs · English, Urdu & Arabic"),
        FeatureRow(Icons.Filled.AutoAwesome, "Gems on every verse", "Key concepts and insights at a glance"),
        FeatureRow(Icons.Filled.Psychology, "Surah quizzes", "Test and deepen your understanding"),
        FeatureRow(Icons.Filled.RecordVoiceOver, "Journeys & Deep Dives", "Seasonal journeys and immersive experiences")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Close row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.accentColor, modifier = Modifier.size(15.dp))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 2.dp, bottom = 140.dp)
            ) {
                // Hero: night-shrine art band with the pitch over it. The band
                // escapes the column's 20dp padding and bleeds edge to edge.
                item {
                    Box(modifier = Modifier.fullBleed(horizontal = 20.dp)) {
                        PaywallHeroBand(contextCover = contextCover, reduceMotion = reduceMotion)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "THAQALAYN PREMIUM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp,
                                color = PaywallGold,
                                style = heroTextShadow(blur = 8f)
                            )
                            Text(
                                "Everything.\nForever.",
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 44.sp,
                                lineHeight = 48.sp,
                                color = PaywallIvory,
                                textAlign = TextAlign.Center,
                                style = heroTextShadow(blur = 14f)
                            )
                            Text(
                                "One payment. No renewals. Yours for life.",
                                fontSize = 14.sp,
                                color = PaywallIvory.copy(alpha = 0.85f),
                                style = heroTextShadow(blur = 8f)
                            )
                            rememberCountUpPrice(BillingManager.priceText, reduceMotion)?.let { price ->
                                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        price,
                                        fontFamily = CormorantFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 40.sp,
                                        color = PaywallGold,
                                        style = heroTextShadow(blur = 12f)
                                    )
                                    Text(
                                        "ONE-TIME",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp,
                                        color = PaywallIvory.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        style = heroTextShadow(blur = 8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 5-layer ladder
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmDivider(label = "5 Layers of Tafsir")
                        layers.forEachIndexed { cascadeIndex, layer ->
                            CascadeIn(index = cascadeIndex, reduceMotion = reduceMotion) {
                            val shape = RoundedCornerShape(14.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape)
                                    .background(if (layer.isGold) colors.accentChip else colors.glassSurface)
                                    .border(
                                        1.dp,
                                        if (layer.isGold) colors.accentColor.copy(alpha = 0.5f) else colors.strokeColor,
                                        shape
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(colors.accentChip),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${layer.number}",
                                        fontFamily = CormorantFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = colors.accentBright
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                    Text(
                                        layer.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText
                                    )
                                    Text(layer.tagline, fontSize = 12.sp, color = colors.tertiaryText)
                                }
                            }
                            }
                        }
                    }
                }

                // Feature rows
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        EmDivider(label = "Everything Included")
                        features.forEachIndexed { cascadeIndex, feature ->
                            CascadeIn(index = layers.size + cascadeIndex, reduceMotion = reduceMotion) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.accentChip),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(feature.icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(18.dp))
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                    Text(feature.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                                    Text(feature.subtitle, fontSize = 12.sp, color = colors.tertiaryText)
                                }
                            }
                            }
                        }
                    }
                }

                // Curated review
                item {
                    val shape = RoundedCornerShape(18.dp)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(colors.glassSurfaceElevated)
                            .border(1.dp, colors.strokeColor, shape)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("★★★★★", fontSize = 14.sp, color = colors.accentBright, letterSpacing = 2.sp)
                        Text(
                            "“What I needed”",
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )
                        Text(
                            "“That's the App I was searching for. Quran (reading, listening, traduction), quiz, daily reminder, Tafsir.”",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 20.sp,
                            color = colors.secondaryText
                        )
                        Text("BiBiGeRm · App Store review", fontSize = 11.sp, color = colors.tertiaryText)
                    }
                }
            }
        }

        // Pinned CTA bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colors.primaryBackground.copy(alpha = 0.96f))
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val shape = RoundedCornerShape(15.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, shape, spotColor = colors.accentColor.copy(alpha = 0.28f))
                    .clip(shape)
                    .background(colors.accentGradient)
                    .pressable { activity?.let { BillingManager.purchase(it) } }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = BillingManager.priceText?.let { "Unlock Premium · $it" } ?: "Unlock Premium",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onAccentText
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("One-time · yours for life", fontSize = 12.sp, color = colors.tertiaryText)
                Text("·", fontSize = 12.sp, color = colors.tertiaryText)
                Text(
                    "Restore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentColor,
                    modifier = Modifier.pressable { BillingManager.restorePurchases() }
                )
            }
            BillingManager.purchaseError?.let { error ->
                Text(
                    error,
                    fontSize = 12.sp,
                    color = colors.semanticRed,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// MARK: - Hero band + paywall motion (iOS PaywallView hero)

/**
 * 348dp full-width hero band. The art fades to transparent into the screen
 * background via an alpha mask (same technique as the Explore covers - NOT a
 * dark scrim). Dome crops center; portrait context covers crop from the top so
 * the composed sky stays in frame. Ken Burns drift: 1.0 -> 1.09, 22s per leg,
 * autoreversing, anchored away from the headline; skipped with reduce motion.
 */
@Composable
private fun PaywallHeroBand(contextCover: Int?, reduceMotion: Boolean) {
    val art = contextCover ?: R.drawable.paywall_hero_dome
    val isContext = contextCover != null
    val mask = remember {
        Brush.verticalGradient(
            0.00f to Color.Black.copy(alpha = 0.72f),
            0.12f to Color.Black,
            0.55f to Color.Black,
            1.00f to Color.Transparent
        )
    }

    val drift: Float = if (reduceMotion) 1f else {
        val transition = rememberInfiniteTransition(label = "heroDrift")
        transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.09f,
            animationSpec = infiniteRepeatable(
                tween(22_000, easing = EaseInOut),
                RepeatMode.Reverse
            ),
            label = "heroDrift"
        ).value
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(348.dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(brush = mask, blendMode = BlendMode.DstIn)
            }
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(art),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = if (isContext) Alignment.TopCenter else Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = drift
                    scaleY = drift
                    transformOrigin = TransformOrigin(0.5f, if (isContext) 0f else 0.5f)
                }
        )
    }
}

/** Black drop shadow that keeps the fixed light hero text legible over art. */
private fun heroTextShadow(blur: Float) = TextStyle(
    shadow = Shadow(
        color = Color.Black.copy(alpha = 0.5f),
        offset = Offset(0f, 1f),
        blurRadius = blur
    )
)

/** Entrance cascade: fade in + rise 14dp, ease-out 0.5s, staggered 50ms/row. */
@Composable
private fun CascadeIn(index: Int, reduceMotion: Boolean, content: @Composable () -> Unit) {
    val progress = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!reduceMotion) {
            delay(index * 50L)
            progress.animateTo(1f, tween(500, easing = EaseOut))
        }
    }
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 14.dp.toPx()
        }
    ) {
        content()
    }
}

/**
 * Price count-up: the numeric part animates 0 -> price, ease-out 0.7s,
 * re-running if the store price loads after the screen appears. Falls back to
 * the raw string when the price cannot be parsed.
 */
@Composable
private fun rememberCountUpPrice(price: String?, reduceMotion: Boolean): String? {
    if (price == null) return null
    if (reduceMotion) return price
    val match = remember(price) { Regex("""\d[\d.,]*""").find(price) } ?: return price
    val raw = match.value
    val decimals = if ('.' in raw) raw.substringAfterLast('.').length.coerceAtMost(2) else 0
    val target = raw.replace(",", "").toFloatOrNull() ?: return price
    val anim = remember(price) { Animatable(0f) }
    LaunchedEffect(price) { anim.animateTo(target, tween(700, easing = EaseOut)) }
    // Once settled, show the store's exact string (locale separators intact).
    if (anim.value >= target) return price
    return price.replaceRange(match.range, String.format("%,.${decimals}f", anim.value))
}
