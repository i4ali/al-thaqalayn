package com.thaqalayn.app.ui.journey

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.JourneyManagers
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.JourneyDay
import com.thaqalayn.app.model.JourneyDua
import com.thaqalayn.app.model.JourneyVerse
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.DuaListenButton
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmSectionLabel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * One journey day's detail (iOS RamadanDayDetailView + its four clones,
 * emerald body): header, dua/ziyarat with Listen, verses, tafsir focus,
 * reflection, and the mark complete/observed toggle. Muharram day 10 gets the
 * dignified Ashura emphasis; Arbaeen station 8 gets the full-ziyarat reader.
 */
@Composable
fun JourneyDayDetailScreen(journeyId: String, dayNumber: Int, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val manager = JourneyManagers.byId(journeyId) ?: return
    val config = journeyUiConfig(journeyId)
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    var showFullZiyarat by remember { mutableStateOf(false) }

    // System back closes the full-ziyarat reader first, not the screen.
    BackHandler(enabled = showFullZiyarat) { showFullZiyarat = false }

    val day = manager.day(dayNumber)
    val isDone = manager.isDayCompleted(dayNumber)
    // Day 10 of Muharram is Ashura - the grief summit; dignified, somber emphasis.
    val isAshura = journeyId == "muharram" && dayNumber == 10

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()

        if (day == null) {
            // Days still parsing on the startup thread; state flips when ready.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accentColor)
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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

            DayDetailHeader(
                day = day,
                config = config,
                lang = lang,
                isDone = isDone,
                isAshura = isAshura
            )

            DuaCard(
                dua = day.dua,
                lang = lang,
                scale = scale,
                onReadFullZiyarat = if (day.dua.fullArabic != null) {
                    { showFullZiyarat = true }
                } else null
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    EmSectionLabel(icon = Icons.Filled.AutoStories, text = JourneyStrings.todaysVerses(lang))
                }
                day.verses.forEach { verse ->
                    JourneyVerseCard(
                        verse = verse,
                        lang = lang,
                        scale = scale,
                        onNavigate = {
                            navController.navigate(Routes.surah(verse.surahNumber, verse.verseNumber))
                        }
                    )
                }
            }

            DetailTextCard(
                icon = Icons.Outlined.Lightbulb,
                label = JourneyStrings.tafsirFocus(lang),
                text = day.localizedTafsir(lang),
                lang = lang,
                scale = scale
            )

            DetailTextCard(
                icon = Icons.Filled.FavoriteBorder,
                label = JourneyStrings.reflection(lang),
                text = day.localizedReflection(lang),
                lang = lang,
                scale = scale,
                italic = true,
                fontSize = 18f
            )

            ToggleButton(
                isDone = isDone,
                doneLabel = if (config.isObservance) JourneyStrings.observed(lang) else JourneyStrings.completed(lang),
                todoLabel = if (config.isObservance) JourneyStrings.markObserved(lang) else JourneyStrings.markComplete(lang),
                doneTint = if (config.isObservance) colors.secondaryText else colors.semanticGreen,
                onToggle = {
                    if (isDone) manager.unmarkDayCompleted(dayNumber)
                    else manager.markDayCompleted(dayNumber)
                }
            )
        }

        if (showFullZiyarat && day.dua.fullArabic != null) {
            FullZiyaratOverlay(
                arabic = day.dua.fullArabic!!,
                english = day.dua.fullEnglish,
                source = day.dua.localizedSource(lang),
                lang = lang,
                scale = scale,
                onDismiss = { showFullZiyarat = false }
            )
        }
    }
}

/** Header card: day/station chip, Ashura badge, status, theme + Arabic (iOS EmJourneyDetailHeader). */
@Composable
private fun DayDetailHeader(
    day: JourneyDay,
    config: JourneyUiConfig,
    lang: CommentaryLanguage,
    isDone: Boolean,
    isAshura: Boolean
) {
    val colors = Theme.colors
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    val dayLabel =
        if (config.usesStations) JourneyStrings.stationN(day.dayNumber, lang)
        else JourneyStrings.dayN(day.dayNumber, lang)
    val statusTint = if (config.isObservance) colors.secondaryText else colors.semanticGreen

    EmCard(
        modifier = Modifier.fillMaxWidth(),
        glow = isAshura,
        // Ashura: a deeper, restrained accent edge - emphasis through scale, not ornament.
        borderColor = if (isAshura) colors.accentColor.copy(alpha = 0.5f) else null
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Icon(
                            journeyDayIcon(day.icon),
                            contentDescription = null,
                            tint = colors.accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = dayLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.accentColor
                        )
                    }

                    if (isAshura) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.secondaryText.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Filled.DarkMode,
                                contentDescription = null,
                                tint = colors.secondaryText,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = JourneyStrings.ashura(lang),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondaryText
                            )
                        }
                    }

                    if (isDone) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = null,
                                tint = statusTint,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (config.isObservance) JourneyStrings.observed(lang)
                                else JourneyStrings.completed(lang),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTint
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = day.localizedTheme(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isAshura) 34.sp else 30.sp,
                        lineHeight = if (isAshura) 38.sp else 34.sp,
                        color = colors.primaryText
                    )
                    Text(
                        text = day.themeArabic,
                        fontFamily = AmiriFamily,
                        fontSize = if (isAshura) 24.sp else 22.sp,
                        color = colors.accentColor
                    )
                }
            }
        }
    }
}

/** Dua/ziyarat card: Arabic + Listen + transliteration + translation + source. */
@Composable
private fun DuaCard(
    dua: JourneyDua,
    lang: CommentaryLanguage,
    scale: Float,
    onReadFullZiyarat: (() -> Unit)?
) {
    val colors = Theme.colors
    val isRTL = lang.isRTL

    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                EmSectionLabel(icon = Icons.Filled.VolunteerActivism, text = JourneyStrings.duaZiyarat(lang))
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = dua.arabic,
                    fontFamily = AmiriFamily,
                    fontSize = (24 * scale).sp,
                    lineHeight = (24 * scale * 1.9f).sp,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DuaListenButton(arabic = dua.arabic)

            if (onReadFullZiyarat != null) {
                Row(
                    modifier = Modifier.pressable(onClick = onReadFullZiyarat),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = JourneyStrings.readFullZiyarat(lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentColor
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Text(
                text = dua.transliteration,
                fontFamily = CormorantFamily,
                fontStyle = FontStyle.Italic,
                fontSize = (16 * scale).sp,
                lineHeight = (16 * scale * 1.4f).sp,
                color = colors.secondaryText
            )

            CompositionLocalProvider(
                LocalLayoutDirection provides if (isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = dua.localizedEnglish(lang),
                        fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (17 * scale).sp,
                        lineHeight = (17 * scale * 1.45f).sp,
                        color = colors.primaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                    dua.localizedSource(lang)?.let { source ->
                        Text(
                            text = "- $source",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.tertiaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/** One verse: reference + recitation + tafsir link, Arabic + translation, relevance note. */
@Composable
private fun JourneyVerseCard(
    verse: JourneyVerse,
    lang: CommentaryLanguage,
    scale: Float,
    onNavigate: () -> Unit
) {
    val colors = Theme.colors

    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = verse.surahNumber,
        key2 = verse.verseNumber
    ) {
        val surah = DataManager.shared.surah(verse.surahNumber) ?: return@produceState
        val v = DataManager.shared.loadQuranData()
            .verses["${verse.surahNumber}"]?.get("${verse.verseNumber}") ?: return@produceState
        value = surah to v
    }
    val surahName = loaded?.first?.englishName ?: "Surah ${verse.surahNumber}"
    val translationIsRTL = lang == CommentaryLanguage.URDU

    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "$surahName · ${verse.surahNumber}:${verse.verseNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = colors.accentColor,
                    modifier = Modifier.weight(1f)
                )

                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback != null &&
                    playback.surahNumber == verse.surahNumber &&
                    playback.verseNumber == verse.verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, v) ->
                                AudioManager.playVerse(VerseWithTafsir(verse.verseNumber, v), surah)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Recite verse",
                        tint = colors.accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Row(
                    modifier = Modifier.pressable(onClick = onNavigate),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = JourneyStrings.fullTafsir(lang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentColor
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            val v = loaded?.second
            if (v != null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = v.arabicText,
                        fontFamily = AmiriFamily,
                        fontSize = (25 * scale).sp,
                        lineHeight = (25 * scale * 1.8f).sp,
                        color = colors.primaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (translationIsRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    val translation =
                        if (translationIsRTL) v.translationUrdu ?: v.translation
                        else v.translation
                    Text(
                        text = translation,
                        fontFamily = if (translationIsRTL) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accentChip.copy(alpha = colors.accentChip.alpha * 0.6f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = colors.accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = verse.localizedNote(lang),
                        fontSize = (13 * scale).sp,
                        lineHeight = (13 * scale * 1.4f).sp,
                        color = colors.secondaryText
                    )
                }
            }
        }
    }
}

/** Labelled reading card (iOS EmDetailCard with a single text body). */
@Composable
private fun DetailTextCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    text: String,
    lang: CommentaryLanguage,
    scale: Float,
    italic: Boolean = false,
    fontSize: Float = 17f
) {
    val colors = Theme.colors
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    EmCard(modifier = Modifier.fillMaxWidth()) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmSectionLabel(icon = icon, text = label)
                Text(
                    text = text,
                    fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else CormorantFamily,
                    fontWeight = FontWeight.Medium,
                    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                    fontSize = (fontSize * scale).sp,
                    lineHeight = (fontSize * scale * 1.45f).sp,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** Mark complete/observed toggle (iOS EmJourneyToggleButton). */
@Composable
private fun ToggleButton(
    isDone: Boolean,
    doneLabel: String,
    todoLabel: String,
    doneTint: androidx.compose.ui.graphics.Color,
    onToggle: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(15.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .let {
                if (isDone) it.background(doneTint.copy(alpha = 0.14f)).border(1.dp, doneTint.copy(alpha = 0.5f), shape)
                else it.background(colors.accentGradient)
            }
            .pressable(onClick = onToggle)
            .padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            if (isDone) Icons.Filled.Verified else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) doneTint else colors.onAccentText,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = if (isDone) doneLabel else todoLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = if (isDone) doneTint else colors.onAccentText
        )
    }
}

/** Full Ziyarat of Arbaeen reader (iOS FullZiyaratSheet), as a full-screen overlay. */
@Composable
private fun FullZiyaratOverlay(
    arabic: String,
    english: String?,
    source: String?,
    lang: CommentaryLanguage,
    scale: Float,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val isRTL = lang.isRTL

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = JourneyStrings.fullZiyaratTitle(lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = colors.primaryText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = JourneyStrings.done(lang),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentColor,
                    modifier = Modifier.pressable(onClick = onDismiss)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = arabic,
                        fontFamily = AmiriFamily,
                        fontSize = (23 * scale).sp,
                        lineHeight = (23 * scale * 2.1f).sp,
                        color = colors.primaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                DuaListenButton(arabic = arabic)

                if (english != null) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    ) {
                        Text(
                            text = english,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16 * scale).sp,
                            lineHeight = (16 * scale * 1.5f).sp,
                            color = colors.secondaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (source != null) {
                    Text(
                        text = "- $source",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.tertiaryText
                    )
                }
            }
        }
    }
}
