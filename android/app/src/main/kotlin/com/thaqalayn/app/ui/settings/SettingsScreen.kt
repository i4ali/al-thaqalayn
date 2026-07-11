package com.thaqalayn.app.ui.settings

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Reciter
import com.thaqalayn.app.premium.BillingManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.ThemeManager
import com.thaqalayn.app.settings.ThemeVariant
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.TextSizePanel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.Theme

/** Settings: appearance, language, reading, audio, premium, about (iOS SettingsView core). */
@Composable
fun SettingsScreen(navController: NavHostController) {
    val colors = Theme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 60.dp
                )
            ) {
                item {
                    EmHeading(eyebrow = "Preferences", title = "Settings")
                }

                // Appearance
                item {
                    SettingsSection(title = "Appearance") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Theme", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                                Text("Light or Dark", fontSize = 12.sp, color = colors.tertiaryText)
                            }
                            SegmentedPicker(
                                options = ThemeVariant.entries.map { it.displayName },
                                selectedIndex = ThemeVariant.entries.indexOf(ThemeManager.selectedTheme),
                                onSelect = { ThemeManager.setTheme(ThemeVariant.entries[it]) }
                            )
                        }
                    }
                }

                // Language
                item {
                    SettingsSection(title = "Language") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Translations, duas & commentary",
                                fontSize = 12.sp,
                                color = colors.tertiaryText
                            )
                            SegmentedPicker(
                                options = CommentaryLanguage.supportedTafsirLanguages.map { it.displayName },
                                selectedIndex = CommentaryLanguage.supportedTafsirLanguages
                                    .indexOf(com.thaqalayn.app.settings.CommentaryLanguageManager.selectedLanguage)
                                    .coerceAtLeast(0),
                                onSelect = {
                                    com.thaqalayn.app.settings.CommentaryLanguageManager.setLanguage(
                                        CommentaryLanguage.supportedTafsirLanguages[it]
                                    )
                                }
                            )
                        }
                    }
                }

                // Reading text size
                item {
                    SettingsSection(title = "Reading") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Verses, translation & commentary text size",
                                fontSize = 12.sp,
                                color = colors.tertiaryText
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextSizePanel()
                            }
                            Text(
                                "Current: ${(ReadingSettingsManager.scale * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = colors.secondaryText,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Audio
                item {
                    SettingsSection(title = "Audio") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Reciter", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                            Reciter.popularReciters.forEach { reciter ->
                                val selected = AudioManager.selectedReciter.id == reciter.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) colors.accentChip else androidx.compose.ui.graphics.Color.Transparent)
                                        .pressable(depth = 0.98f) { AudioManager.setReciter(reciter) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            reciter.nameEnglish,
                                            fontSize = 14.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) colors.accentColor else colors.primaryText
                                        )
                                        Text(reciter.description, fontSize = 11.sp, color = colors.tertiaryText)
                                    }
                                    if (selected) {
                                        Text(
                                            "✓",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.accentColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Premium
                item {
                    SettingsSection(title = "Premium") {
                        if (PremiumManager.isPremium) {
                            Text(
                                "Premium unlocked. Thank you for supporting Thaqalayn.",
                                fontSize = 14.sp,
                                color = colors.semanticGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.accentGradient)
                                        .pressable { navController.navigate(Routes.PAYWALL) }
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Unlock Premium",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onAccentText
                                    )
                                }
                                Text(
                                    "Restore purchases",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.accentColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pressable { BillingManager.restorePurchases() }
                                        .padding(vertical = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // About
                item {
                    SettingsSection(title = "About") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Thaqalayn for Android", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                            Text(
                                "The Holy Quran with 5-layer Shia tafsir in English, Urdu & Arabic.",
                                fontSize = 12.sp,
                                color = colors.tertiaryText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor,
            modifier = Modifier.padding(start = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.glassSurface)
                .border(1.dp, colors.strokeColor, shape)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SegmentedPicker(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(colors.glassSurfaceRecessed)
            .border(1.dp, colors.strokeColor, shape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Text(
                text = option,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) colors.onAccentText else colors.secondaryText,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .let {
                        if (selected) it.background(colors.accentGradient)
                        else it
                    }
                    .pressable(depth = 0.97f) { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
