package com.thaqalayn.app.ui.paywall

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

/** Premium paywall: price-forward hero, 5-layer ladder, features, CTA (iOS PaywallView). */
@Composable
fun PaywallScreen(navController: NavHostController) {
    val colors = Theme.colors
    val activity = LocalContext.current as? Activity

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
                // Hero
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "THAQALAYN PREMIUM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = colors.accentColor
                        )
                        Text(
                            "Everything.\nForever.",
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 44.sp,
                            lineHeight = 48.sp,
                            color = colors.primaryText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "One payment. No renewals. Yours for life.",
                            fontSize = 14.sp,
                            color = colors.secondaryText
                        )
                        BillingManager.priceText?.let { price ->
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    price,
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 40.sp,
                                    color = colors.accentBright
                                )
                                Text(
                                    "ONE-TIME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = colors.tertiaryText,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 5-layer ladder
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmDivider(label = "5 Layers of Tafsir")
                        layers.forEach { layer ->
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

                // Feature rows
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        EmDivider(label = "Everything Included")
                        features.forEach { feature ->
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
