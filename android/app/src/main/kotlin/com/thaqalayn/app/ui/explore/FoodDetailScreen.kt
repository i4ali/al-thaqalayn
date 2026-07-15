package com.thaqalayn.app.ui.explore

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.FoodsManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Detail for a single food: emoji hero, tappable Qur'an verse, narration, sunnah tip, nutrition note (iOS FoodDetailView). */
@Composable
fun FoodDetailScreen(foodId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val context = LocalContext.current
    val food = remember(foodId) { FoodsManager.byId(foodId) } ?: return
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    val verse by produceState<Verse?>(initialValue = null, key1 = food.surahNumber, key2 = food.verseNumber) {
        value = DataManager.shared.loadQuranData().verses["${food.surahNumber}"]?.get("${food.verseNumber}")
    }

    fun translation(v: Verse): String =
        if (lang == CommentaryLanguage.URDU) v.translationUrdu ?: v.translation else v.translation

    val loadedVerse = verse
    val shareText = buildList {
        add("${food.emoji} ${food.name(lang)}")
        if (loadedVerse != null) {
            add("Qur'an ${food.surahNumber}:${food.verseNumber}\n${loadedVerse.arabicText}")
            add(translation(loadedVerse))
        } else {
            add("Qur'an ${food.surahNumber}:${food.verseNumber}")
        }
        add("From the Ahl al-Bayt\n${food.narration(lang)}\n- ${food.narrationSource}")
        add("From the Sunnah\n${food.sunnahTip(lang)}")
        add("Nutrition\n${food.nutritionNote(lang)}")
        add("Sent via Thaqalayn")
    }.joinToString("\n\n")

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Back
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

            // Hero - emoji in a glowing chip (Android stand-in for the iOS illustration asset)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .shadow(
                            16.dp,
                            CircleShape,
                            ambientColor = colors.accentColor.copy(alpha = 0.18f),
                            spotColor = colors.accentColor.copy(alpha = 0.18f)
                        )
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = food.emoji, fontSize = 52.sp)
                }
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Text(
                        text = food.name(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        color = colors.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Verse - reference row opens the ayah in the reader
            EmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.pressable {
                            navController.navigate(Routes.surah(food.surahNumber, food.verseNumber))
                        },
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Qur'an ${food.surahNumber}:${food.verseNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.accentColor
                        )
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    loadedVerse?.let { v ->
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = v.arabicText,
                                fontFamily = AmiriFamily,
                                fontSize = (26 * scale).sp,
                                lineHeight = (26 * scale * 1.8f).sp,
                                color = colors.primaryText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides direction) {
                            Text(
                                text = translation(v),
                                fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = (16 * scale).sp,
                                lineHeight = (16 * scale * 1.5f).sp,
                                color = colors.secondaryText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } ?: Text(
                        text = "Tap to open this verse in the reader.",
                        fontSize = 14.sp,
                        color = colors.tertiaryText
                    )
                }
            }

            InfoCard(
                label = "From the Ahl al-Bayt",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                text = food.narration(lang),
                source = food.narrationSource,
                lang = lang
            )
            InfoCard(
                label = "From the Sunnah",
                icon = Icons.Filled.AutoAwesome,
                text = food.sunnahTip(lang),
                source = null,
                lang = lang
            )
            InfoCard(
                label = "Nutrition",
                icon = Icons.Filled.Eco,
                text = food.nutritionNote(lang),
                source = null,
                lang = lang
            )

            // Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, RoundedCornerShape(15.dp), spotColor = colors.accentColor.copy(alpha = 0.28f))
                    .clip(RoundedCornerShape(15.dp))
                    .background(colors.accentGradient)
                    .pressable {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(15.dp))
                Text(
                    text = "Share",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = colors.onAccentText
                )
            }
        }
    }
}

/** Labeled reading card: eyebrow with icon, scaled body text, optional source citation. */
@Composable
private fun InfoCard(
    label: String,
    icon: ImageVector,
    text: String,
    source: String?,
    lang: CommentaryLanguage
) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale
    EmCard(modifier = Modifier.fillMaxWidth()) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(12.dp))
                    Text(
                        text = label.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = colors.accentColor
                    )
                }
                Text(
                    text = text,
                    fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = (16 * scale).sp,
                    lineHeight = (16 * scale * 1.5f).sp,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth()
                )
                if (source != null) {
                    Text(
                        text = source,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.tertiaryText
                    )
                }
            }
        }
    }
}
