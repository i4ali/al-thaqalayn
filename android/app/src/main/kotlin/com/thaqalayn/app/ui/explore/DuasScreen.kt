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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
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
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.DailyDua
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

/** Material stand-in for the iOS DailyDua.categoryIcon SF Symbol. */
internal fun duaCategoryIcon(category: String): ImageVector = when (category.lowercase()) {
    "health" -> Icons.Filled.Favorite
    "provision" -> Icons.Filled.Spa
    "guidance" -> Icons.Filled.Signpost
    "faith" -> Icons.Filled.LocalFireDepartment
    "forgiveness" -> Icons.Filled.WaterDrop
    "family" -> Icons.Filled.Home
    "protection" -> Icons.Filled.Shield
    "devotion" -> Icons.Filled.NightsStay
    else -> Icons.Filled.VolunteerActivism
}

private fun eyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "الأدعية"
    CommentaryLanguage.URDU -> "دعائیں"
    else -> "Supplications"
}

private fun title(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "أدعية لكل حاجة"
    CommentaryLanguage.URDU -> "ہر حاجت کی دعا"
    else -> "Duas for Every Need"
}

private fun subtitle(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "للصحة والحفظ والرزق وغيرها"
    CommentaryLanguage.URDU -> "صحت، حفاظت، رزق اور مزید کے لیے"
    else -> "For health, protection, sustenance & more"
}

/** Daily duas list - short hadith-based supplications for everyday occasions (iOS DuasView). */
@Composable
fun DuasScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val duas = DuasManager.duas
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    item {
                        // Midnight Emerald only: night-shrine band behind the
                        // header, bleeding behind the status bar (decorative).
                        Box(modifier = Modifier.fullBleed(horizontal = 20.dp)) {
                            if (colors.isMidnightEmerald) {
                                CoverHeaderBand(art = R.drawable.explore_cover_daily_duas, height = 280.dp)
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(7.dp),
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 68.dp, bottom = 6.dp)
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

                    items(duas, key = { it.id }) { dua ->
                        DuaCard(dua = dua, lang = lang) {
                            navController.navigate(Routes.dua(dua.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DuaCard(dua: DailyDua, lang: CommentaryLanguage, onClick: () -> Unit) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmIconChip(icon = duaCategoryIcon(dua.category))
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = dua.situation(lang),
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = colors.primaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.tertiaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
