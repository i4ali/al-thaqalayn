package com.thaqalayn.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.R
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.QuranSearchEngine
import com.thaqalayn.app.data.QuranSearchResults
import com.thaqalayn.app.data.ThemeHit
import com.thaqalayn.app.data.VerseHit
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.home.SurahCard
import com.thaqalayn.app.ui.strings.QuranTabStrings
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay

/** Hex like "#E8B86D" -> Color. */
fun colorFromHex(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    val value = cleaned.toLongOrNull(16) ?: return Color.Gray
    return when (cleaned.length) {
        6 -> Color(0xFF000000 or value)
        8 -> Color(value)
        else -> Color.Gray
    }
}

/**
 * Shared, theme-aware results for the Quran tab search. Surahs / Verses / Themes
 * sections (iOS SearchResultsView).
 */
@Composable
fun SearchResults(
    query: String,
    lang: CommentaryLanguage,
    onOpenSurah: (Int) -> Unit,
    onOpenVerse: (Int, Int) -> Unit
) {
    val colors = Theme.colors

    val searched by produceState(initialValue = false to QuranSearchResults(), key1 = query) {
        delay(200) // debounce, matching iOS
        val index = DataManager.shared.searchIndex()
        value = true to QuranSearchEngine.search(query, index)
    }
    val (didSearch, results) = searched

    val surahs by produceState(initialValue = emptyList<com.thaqalayn.app.model.Surah>(), key1 = Unit) {
        value = DataManager.shared.surahs()
    }
    val surahsByNumber = remember(surahs) { surahs.associateBy { it.number } }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (didSearch && results.isEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhosphorIcon(resId = R.drawable.ph_magnifying_glass, size = 28.dp, tint = colors.tertiaryText)
                Text(
                    text = QuranTabStrings.noResults(query, lang),
                    fontSize = 15.sp,
                    color = colors.secondaryText
                )
            }
            return@Column
        }

        if (results.surahs.isNotEmpty()) {
            SectionLabel(QuranTabStrings.surahsLabel(lang), results.surahs.size)
            results.surahs.forEach { number ->
                surahsByNumber[number]?.let { surah ->
                    SurahCard(surah = surah, lang = lang) { onOpenSurah(number) }
                }
            }
        }

        if (results.verses.isNotEmpty()) {
            SectionLabel(QuranTabStrings.versesLabel(lang), results.verseTotal)
            results.verses.forEach { hit ->
                VerseResultRow(hit) { onOpenVerse(hit.surahNumber, hit.verseNumber) }
            }
            if (results.verseTotal > results.verses.size) {
                MoreLabel(results.verses.size, results.verseTotal, lang)
            }
        }

        if (results.themes.isNotEmpty()) {
            SectionLabel(QuranTabStrings.themesLabel(lang), results.themeTotal)
            results.themes.forEach { hit ->
                ThemeResultRow(hit) { onOpenVerse(hit.surahNumber, hit.verseNumber) }
            }
            if (results.themeTotal > results.themes.size) {
                MoreLabel(results.themes.size, results.themeTotal, lang)
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, count: Int) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = colors.accentColor
        )
        Text(
            text = "· $count",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.tertiaryText
        )
    }
}

@Composable
private fun MoreLabel(showing: Int, total: Int, lang: CommentaryLanguage) {
    Text(
        text = QuranTabStrings.showingFirst(showing, total, lang),
        fontSize = 12.sp,
        color = Theme.colors.tertiaryText,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun VerseResultRow(hit: VerseHit, onClick: () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, shape)
            .pressable(onClick = onClick)
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "${hit.surahEnglishName.uppercase()} · ${hit.surahNumber}:${hit.verseNumber}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = colors.accentColor
        )
        Text(
            text = highlighted(hit.snippet, hit.matchRange, colors.accentBright),
            fontSize = 14.sp,
            color = colors.primaryText
        )
    }
}

private fun highlighted(text: String, range: IntRange?, highlightColor: Color): AnnotatedString =
    buildAnnotatedString {
        append(text)
        if (range != null && range.first >= 0 && range.last < text.length) {
            addStyle(
                SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold),
                range.first,
                range.last + 1
            )
        }
    }

@Composable
private fun ThemeResultRow(hit: ThemeHit, onClick: () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(13.dp)
    val dotColor = remember(hit.colorHex) { colorFromHex(hit.colorHex) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, shape)
            .pressable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .shadow(4.dp, CircleShape, spotColor = dotColor.copy(alpha = 0.6f))
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = hit.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.primaryText,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${hit.surahEnglishName} · ${hit.surahNumber}:${hit.verseNumber}",
            fontSize = 11.sp,
            color = colors.tertiaryText,
            maxLines = 1
        )
    }
}
