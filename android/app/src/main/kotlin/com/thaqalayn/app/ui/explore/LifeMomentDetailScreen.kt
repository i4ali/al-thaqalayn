package com.thaqalayn.app.ui.explore

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.data.LifeMomentsManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.DailyDua
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.Verse
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * Detail for a single life moment: the situation, its Qur'an verse (tappable ->
 * reader), and a linked supplication from the Daily Duas (iOS LifeMomentDetailView).
 */
@Composable
fun LifeMomentDetailScreen(momentId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val moment = remember(momentId) { LifeMomentsManager.byId(momentId) } ?: return
    val linkedDua = remember(moment.duaId) { moment.duaId?.let { DuasManager.byId(it) } }

    val loaded by produceState<Pair<Surah, Verse>?>(
        initialValue = null,
        key1 = moment.surahNumber,
        key2 = moment.verseNumber
    ) {
        val surah = DataManager.shared.surah(moment.surahNumber) ?: return@produceState
        val verse = DataManager.shared.loadQuranData()
            .verses["${moment.surahNumber}"]?.get("${moment.verseNumber}") ?: return@produceState
        value = surah to verse
    }

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

            Hero(moment.category, moment.situation(lang), lang)
            VerseSection(moment.surahNumber, moment.verseNumber, loaded, lang, scale, navController)
            if (linkedDua != null) {
                DuaSection(linkedDua, lang, scale) {
                    navController.navigate(Routes.dua(linkedDua.id))
                }
            }
        }
    }
}

// Hero - category icon chip + the situation + a category pill.
@Composable
private fun Hero(category: String, situation: String, lang: CommentaryLanguage) {
    val colors = Theme.colors
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .shadow(16.dp, CircleShape, spotColor = colors.accentColor.copy(alpha = 0.18f))
                .clip(CircleShape)
                .background(colors.accentChip)
                .border(1.dp, colors.strokeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                momentCategoryIcon(category),
                contentDescription = null,
                tint = colors.accentColor,
                modifier = Modifier.size(42.dp)
            )
        }

        CompositionLocalProvider(
            LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Text(
                text = situation,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                color = colors.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.accentChip)
                .border(1.dp, colors.strokeColor, CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = category.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = colors.accentColor
            )
        }
    }
}

// The moment's verse: reference (opens the reader), recitation button, Arabic + translation.
@Composable
private fun VerseSection(
    surahNumber: Int,
    verseNumber: Int,
    loaded: Pair<Surah, Verse>?,
    lang: CommentaryLanguage,
    scale: Float,
    navController: NavHostController
) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.pressable {
                        navController.navigate(Routes.surah(surahNumber, verseNumber))
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Qur'an $surahNumber:$verseNumber",
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

                Spacer(modifier = Modifier.weight(1f))

                // Recitation (iOS VerseRecitationButton, size 34)
                val playback = AudioManager.currentPlayback
                val isPlayingThis = playback?.surahNumber == surahNumber &&
                    playback.verseNumber == verseNumber &&
                    AudioManager.playerState == AudioPlayerState.PLAYING
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.accentChip)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .pressable {
                            loaded?.let { (surah, verse) ->
                                AudioManager.playVerse(VerseWithTafsir(verseNumber, verse), surah)
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
            }

            val verse = loaded?.second
            if (verse != null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = verse.arabicText,
                        fontFamily = AmiriFamily,
                        fontSize = (26 * scale).sp,
                        lineHeight = (26 * scale * 1.8f).sp,
                        color = colors.primaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    val translation =
                        if (lang == CommentaryLanguage.URDU) verse.translationUrdu ?: verse.translation
                        else verse.translation
                    Text(
                        text = translation,
                        fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (16 * scale).sp,
                        lineHeight = (16 * scale * 1.5f).sp,
                        color = colors.secondaryText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Text(
                    text = "Tap to open this verse in the reader.",
                    fontSize = 14.sp,
                    color = colors.tertiaryText
                )
            }
        }
    }
}

// The linked supplication - the whole card taps through to the full dua detail.
@Composable
private fun DuaSection(dua: DailyDua, lang: CommentaryLanguage, scale: Float, onClick: () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, shape, spotColor = colors.accentColor.copy(alpha = 0.12f))
            .clip(shape)
            .background(colors.accentColor.copy(alpha = 0.07f))
            .border(1.dp, colors.accentColor.copy(alpha = 0.30f), shape)
            .pressable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                Icons.Filled.VolunteerActivism,
                contentDescription = null,
                tint = colors.accentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "A DU'A FOR THIS MOMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = colors.accentColor
            )
        }

        Text(
            text = dua.situation(lang),
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            color = colors.primaryText,
            modifier = Modifier.fillMaxWidth()
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = dua.arabic,
                fontFamily = AmiriFamily,
                fontSize = (21 * scale).sp,
                lineHeight = (21 * scale * 1.8f).sp,
                color = colors.primaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = dua.transliteration,
            fontFamily = CormorantFamily,
            fontStyle = FontStyle.Italic,
            fontSize = (15 * scale).sp,
            color = colors.secondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .height(1.dp)
                .background(colors.strokeColor)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Open du'a",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.accentColor,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = dua.source,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.tertiaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
