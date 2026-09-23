package com.thaqalayn.app.ui.passages

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.model.Passage
import com.thaqalayn.app.model.PassageSource
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.launch

/**
 * One source behind a passage commentary (iOS SourceSheet), opened from a citation
 * marker, a narration's source line or the bibliography: the work, its author and
 * locus, the quoted excerpt with its gloss, the narrations it supplies in verbatim
 * Arabic with their chains, any grading, and a link to the page it was read from.
 * Tier C (copyrighted translations) shows only the work, author, locus and link.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSheet(
    source: PassageSource,
    passage: Passage,
    surahName: String,
    onDismiss: () -> Unit
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    val isTierC = source.tier.uppercase() == "C"
    val tradition = if (source.tradition.lowercase() == "sunni") "Sunni" else "Shia"
    val kind = if (source.kind.lowercase() == "hadith") "narration" else "commentary"
    val citing = passage.verses.flatMap { it.narrations }.filter { it.source == source.id }
    val grades = source.grades.orEmpty().map { it.trim() }.filter { it.isNotEmpty() }
    // The source page, only when the stored string is a real web URL.
    val link = source.url.trim().takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }?.takeIf {
        (it.scheme?.lowercase() == "http" || it.scheme?.lowercase() == "https") && !it.host.isNullOrEmpty()
    }
    val host = link?.host?.removePrefix("www.") ?: "the web"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tm.secondaryBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title block with the close button.
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Eyebrow("Source ${source.number} · $tradition · $kind", tm.accentColor)
                    Text(
                        text = source.work,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        color = tm.primaryText
                    )
                    Text(text = source.author, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = tm.secondaryText)
                    Text(text = source.locus, fontSize = 13.sp, color = tm.tertiaryText)
                    Text(
                        text = "Cited in $surahName · ${passage.title.en}",
                        fontSize = 12.sp,
                        color = tm.tertiaryText,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, tm.strokeColor, CircleShape)
                        .pressable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = tm.accentColor, modifier = Modifier.size(16.dp))
                }
            }

            val excerpt = source.excerpt
            if (!isTierC && excerpt != null) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EmDivider(label = "Excerpt")
                    if (excerpt.lang.lowercase() == "en") {
                        Text(
                            text = excerpt.text,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (17 * scale).sp,
                            lineHeight = (17 * scale * 1.4f).sp,
                            color = tm.primaryText
                        )
                    } else {
                        ArabicBlock(excerpt.text, size = 22f, scale = scale)
                    }
                    source.gloss?.takeIf { it.isNotEmpty() }?.let { gloss ->
                        Text(
                            text = gloss,
                            fontFamily = CormorantFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16 * scale).sp,
                            lineHeight = (16 * scale * 1.35f).sp,
                            color = tm.secondaryText
                        )
                    }
                }
            }

            if (!isTierC && citing.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    EmDivider(label = if (citing.size == 1) "Narration" else "Narrations")
                    for (narration in citing) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpeakerLine(narration)
                            narration.chain?.takeIf { it.isNotEmpty() }?.let { chain ->
                                Text(text = chain, fontSize = 12.sp, lineHeight = 16.sp, color = tm.secondaryText)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            ArabicBlock(narration.arabic, size = 20f, scale = scale)
                        }
                    }
                }
            }

            if (grades.isNotEmpty()) {
                Text(text = "Grading: ${grades.joinToString(", ")}", fontSize = 12.sp, color = tm.tertiaryText)
            }

            if (link != null) {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(38.dp)
                        .pressable { uriHandler.openUri(link.toString()) }
                        .clip(CircleShape)
                        .background(tm.accentChip)
                        .border(1.dp, tm.strokeColor, CircleShape)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = tm.accentColor, modifier = Modifier.size(14.dp))
                    Text(text = "Open on $host", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = tm.accentColor)
                }
            }
        }
    }
}

/** Reading Arabic: scaled, laid out right to left, so Start is the right edge. */
@Composable
private fun ArabicBlock(text: String, size: Float, scale: Float) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = text,
            fontFamily = AmiriFamily,
            fontSize = (size * scale).sp,
            lineHeight = (size * scale * 1.8f).sp,
            color = Theme.colors.primaryText,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
