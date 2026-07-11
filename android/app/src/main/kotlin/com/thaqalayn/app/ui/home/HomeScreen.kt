package com.thaqalayn.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.Icon
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.LastReadInfo
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.search.SearchResults
import com.thaqalayn.app.ui.strings.QuranTabStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Quran tab: surah list + search (iOS HomeView/EmeraldHomeView). */
@Composable
fun HomeScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    var searchText by rememberSaveable { mutableStateOf("") }

    val surahs by produceState(initialValue = emptyList<Surah>()) {
        value = DataManager.shared.surahs()
    }

    val filtered = remember(surahs, searchText) {
        if (searchText.isBlank()) surahs
        else surahs.filter {
            it.englishName.contains(searchText, ignoreCase = true) ||
                it.englishNameTranslation.contains(searchText, ignoreCase = true) ||
                it.arabicName.contains(searchText)
        }
    }

    val isEmerald = colors.isMidnightEmerald
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        // Greeting row: bookmarks + notifications entry points
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, colors.strokeColor, CircleShape)
                    .pressable { navController.navigate(Routes.BOOKMARKS) },
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(resId = R.drawable.ph_heart_fill, size = 15.dp, tint = colors.accentColor)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, colors.strokeColor, CircleShape)
                    .pressable { navController.navigate(Routes.SETTINGS) },
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(resId = R.drawable.ph_sparkle_fill, size = 15.dp, tint = colors.accentColor)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp
            )
        ) {
            item {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    if (isEmerald) {
                        EmHeading(
                            eyebrow = QuranTabStrings.nobleQuranEyebrow(lang),
                            title = QuranTabStrings.readAndReflect(lang)
                        )
                    } else {
                        Text(
                            text = QuranTabStrings.holyQuran(lang),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }
                }
            }

            val lastRead = ProgressManager.lastReadInfo
            if (lastRead != null && searchText.isBlank()) {
                val surah = surahs.firstOrNull { it.number == lastRead.surahNumber }
                if (surah != null) {
                    item {
                        CompositionLocalProvider(LocalLayoutDirection provides direction) {
                            ContinueReadingCard(info = lastRead, surah = surah, lang = lang) {
                                navController.navigate(Routes.surah(surah.number, lastRead.verseNumber))
                            }
                        }
                    }
                }
            }

            item {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    SearchField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = QuranTabStrings.searchPlaceholder(lang)
                    )
                }
            }

            if (searchText.isBlank()) {
                item {
                    EmDivider(label = QuranTabStrings.surahsCount(surahs.size, lang))
                }
                items(filtered, key = { it.number }) { surah ->
                    SurahCard(surah = surah, lang = lang) {
                        navController.navigate(Routes.surah(surah.number))
                    }
                }
            } else {
                item {
                    SearchResults(
                        query = searchText,
                        lang = lang,
                        onOpenSurah = { navController.navigate(Routes.surah(it)) },
                        onOpenVerse = { s, v -> navController.navigate(Routes.surah(s, v)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, shape)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PhosphorIcon(resId = R.drawable.ph_magnifying_glass, size = 16.dp, tint = colors.accentColor)
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(text = placeholder, fontSize = 15.sp, color = colors.tertiaryText)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 15.sp, color = colors.primaryText),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.accentColor),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ContinueReadingCard(
    info: LastReadInfo,
    surah: Surah,
    lang: CommentaryLanguage,
    onResume: () -> Unit
) {
    val colors = Theme.colors
    EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
        Box {
            Text(
                text = "${info.surahNumber}",
                fontFamily = CormorantFamily,
                fontSize = 120.sp,
                color = colors.accentColor.copy(alpha = 0.07f),
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp)
            )
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = QuranTabStrings.continueReading(lang).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (lang.isRTL) 0.sp else 2.sp,
                    color = colors.accentColor
                )
                Text(
                    text = surah.englishName,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 27.sp,
                    color = colors.primaryText
                )
                Text(
                    text = "${QuranTabStrings.verseOf(info.verseNumber, surah.versesCount, lang)} · " +
                        QuranTabStrings.percentComplete((info.progress * 100).toInt(), lang),
                    fontSize = 13.sp,
                    color = colors.secondaryText
                )
                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(info.progress.toFloat().coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(colors.accentGradient)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .shadow(10.dp, CircleShape, spotColor = colors.accentColor.copy(alpha = 0.28f))
                        .clip(CircleShape)
                        .background(colors.accentGradient)
                        .pressable(onClick = onResume)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = colors.onAccentText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = QuranTabStrings.resume(lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp,
                        color = colors.onAccentText
                    )
                }
            }
        }
    }
}

/** One surah row (iOS ModernSurahCard, emerald + legacy variants). */
@Composable
fun SurahCard(
    surah: Surah,
    lang: CommentaryLanguage,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val isEmerald = colors.isMidnightEmerald
    val shape = RoundedCornerShape(20.dp)
    val (read, total) = ProgressManager.getSurahCompletion(surah.number)
    val percentage = if (total > 0) (read * 100) / total else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                if (isEmerald) 12.dp else 6.dp,
                shape,
                spotColor = Color.Black.copy(alpha = if (isEmerald) 0.28f else 0.04f)
            )
            .clip(shape)
            .background(if (isEmerald) colors.glassSurface else Color.White)
            .border(1.dp, colors.strokeColor, shape)
            .pressable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEmerald) {
            EmNumeralCircle(n = surah.number, size = 46.dp)
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0xFFE89A6F).copy(alpha = 0.3f))
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFE89A6F), Color(0xFFD88A5F)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = surah.englishName,
                        fontFamily = if (isEmerald) CormorantFamily else null,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isEmerald) 20.sp else 18.sp,
                        color = colors.primaryText
                    )
                    Text(
                        text = surah.englishNameTranslation,
                        fontSize = if (isEmerald) 12.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isEmerald) colors.tertiaryText else colors.secondaryText
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = surah.arabicName,
                    fontFamily = if (isEmerald) AmiriFamily else null,
                    fontSize = if (isEmerald) 22.sp else 20.sp,
                    fontWeight = if (isEmerald) FontWeight.Normal else FontWeight.Medium,
                    color = if (isEmerald) colors.accentBright else colors.primaryText,
                    maxLines = 1
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = QuranTabStrings.versesCount(surah.versesCount, lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText
                )
                Text(text = "·", color = colors.tertiaryText, fontSize = 12.sp)
                Text(
                    text = QuranTabStrings.revelation(surah.revelationType, lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText
                )
                if (read > 0) {
                    Text(text = "·", color = colors.tertiaryText, fontSize = 12.sp)
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isEmerald) colors.accentColor else colors.semanticGreen
                    )
                }
            }
        }
    }
}
