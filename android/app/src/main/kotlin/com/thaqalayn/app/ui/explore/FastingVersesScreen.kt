package com.thaqalayn.app.ui.explore

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.FastingVersesManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.FastingCategory
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.R
import com.thaqalayn.app.ui.components.CoverHeaderBand
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.fullBleed
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-in for the FastingCategory.icon SF Symbol. */
private fun sfIcon(name: String): ImageVector = when (name) {
    "book.fill" -> Icons.AutoMirrored.Filled.MenuBook
    "clock.fill" -> Icons.Filled.Schedule
    "heart.circle.fill" -> Icons.Filled.Favorite
    "sparkles" -> Icons.Filled.AutoAwesome
    else -> Icons.AutoMirrored.Filled.MenuBook
}

private fun eyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "رمضان في القرآن"
    CommentaryLanguage.URDU -> "قرآن میں رمضان"
    else -> "Ramadan in the Qur'an"
}

private fun title(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "الصيام في القرآن"
    CommentaryLanguage.URDU -> "قرآن میں روزہ"
    else -> "Fasting in the Quran"
}

private fun subtitle(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "آياتٌ عن الصيام ورمضان"
    CommentaryLanguage.URDU -> "روزے اور رمضان سے متعلق آیات"
    else -> "Verses about fasting and Ramadan"
}

/** Fasting in the Quran - curated verse categories about fasting (iOS FastingVersesView). */
@Composable
fun FastingVersesScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val categories = FastingVersesManager.categories
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Box(modifier = Modifier.fillMaxSize()) {
            // Back - floats above the scrolling header band.
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp)
                    .zIndex(1f)
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

            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    item {
                        // Midnight Emerald only: night-shrine band behind the
                        // header, bleeding behind the status bar (decorative).
                        Box(modifier = Modifier.fullBleed(horizontal = 20.dp)) {
                            if (colors.isMidnightEmerald) {
                                CoverHeaderBand(art = R.drawable.explore_cover_fasting, height = 280.dp)
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(7.dp),
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 68.dp, bottom = 2.dp)
                            ) {
                            Text(
                                text = eyebrow(lang).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (lang.isRTL) 0.sp else 3.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = title(lang),
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 36.sp,
                                lineHeight = 40.sp,
                                color = colors.primaryText
                            )
                            Text(
                                text = subtitle(lang),
                                fontSize = 13.5.sp,
                                color = colors.secondaryText
                            )
                            }
                        }
                    }

                    items(categories, key = { it.id }) { category ->
                        FastingCategoryCard(category = category, lang = lang) {
                            navController.navigate(Routes.fastingCategory(category.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FastingCategoryCard(category: FastingCategory, lang: CommentaryLanguage, onClick: () -> Unit) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmIconChip(icon = sfIcon(category.icon))
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.title(lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    color = colors.primaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.description(lang),
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = colors.secondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${category.verseCount} verse" + if (category.verseCount == 1) "" else "s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.tertiaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
