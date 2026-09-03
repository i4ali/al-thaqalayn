package com.thaqalayn.app.ui.dua

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.DuaKaraokeEngine
import com.thaqalayn.app.audio.DuaStreamPlayer
import com.thaqalayn.app.data.DuaArabicTokenizer
import com.thaqalayn.app.data.DuaWordPosition
import com.thaqalayn.app.data.SpecialDuasManager
import com.thaqalayn.app.model.SpecialDua
import com.thaqalayn.app.model.SpecialDuaSegment
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.ThemeVariant
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

private fun timeString(t: Double): String {
    if (t.isNaN() || t < 0) return "0:00"
    val total = t.toInt()
    return "%d:%02d".format(total / 60, total % 60)
}

/**
 * Reader for one major supplication / ziyarat: a header with context, a streamed
 * recitation bar (buffering + scrubbing), then the full text line by line - Arabic +
 * transliteration + translation, with the occasional structural note. While the
 * recitation plays, the word being recited glows gold and the reader gently follows
 * along (karaoke, DuaKaraokeEngine); tapping a line seeks the recitation there. Reading
 * content scales with the reading text-size control. Mirrors iOS SpecialDuaDetailView.
 */
@Composable
fun SpecialDuaDetailScreen(duaId: String, navController: NavHostController) {
    val colors = Theme.colors
    val warm = colors.variant == ThemeVariant.WARM_INVITING
    // The karaoke gold: bright on the dark theme; a deeper gold that stays legible on
    // the warm theme's light background.
    val karaokeGold = if (warm) Color(0xFF9A7514) else Color(0xFFECD49A)
    val scale = ReadingSettingsManager.scale
    val context = LocalContext.current
    val dua = remember(duaId) { SpecialDuasManager.byId(duaId) } ?: return

    val karaoke = remember { DuaKaraokeEngine() }
    LaunchedEffect(dua.id) {
        karaoke.configure(dua.id)
        snapshotFlow { Triple(DuaStreamPlayer.currentTime, DuaStreamPlayer.currentId, DuaStreamPlayer.duration) }
            .collect { (t, id, d) -> karaoke.update(t, id, d) }
    }

    val listState = rememberLazyListState()
    var lastUserScroll by remember { mutableStateOf(0L) }
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                lastUserScroll = System.currentTimeMillis()   // user drag/fling; not programmatic
                return Offset.Zero
            }
        }
    }

    val hasAudio = !dua.audioUrl.isNullOrEmpty()
    // back(0) + header(1) [+ listen bar] + divider, then the segments.
    val headerCount = 2 + (if (hasAudio) 1 else 0) + 1

    // Keep the recited line roughly centered while playing, unless the reader recently
    // scrolled somewhere themselves.
    LaunchedEffect(karaoke.currentSegment) {
        val seg = karaoke.currentSegment ?: return@LaunchedEffect
        if (DuaStreamPlayer.currentId != dua.id || !DuaStreamPlayer.isPlaying) return@LaunchedEffect
        if (System.currentTimeMillis() - lastUserScroll < 6000L) return@LaunchedEffect
        val vp = listState.layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
        runCatching { listState.animateScrollToItem((headerCount + seg).coerceAtLeast(0), -(vp / 3)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().statusBarsPadding().nestedScroll(nestedScroll),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 44.dp)
        ) {
            item {
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

            item { Header(dua, scale) }

            if (hasAudio) item { StreamListenBar(dua) }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.strokeColor)
                )
            }

            itemsIndexed(dua.segments) { index, seg ->
                if (seg.isNote) {
                    NoteRow(seg.note ?: "")
                } else {
                    SegmentRow(
                        seg = seg,
                        index = index,
                        karaoke = karaoke,
                        gold = karaokeGold,
                        scale = scale,
                        onTap = {
                            if (karaoke.canSeek(DuaStreamPlayer.currentId)) {
                                karaoke.timings?.segmentStart(index)?.let { DuaStreamPlayer.seek(it) }
                            }
                        }
                    )
                }
            }

            item { CreditFooter(dua) }
            item { ShareButton(dua, context) }
        }
    }
}

@Composable
private fun Header(dua: SpecialDua, scale: Float) {
    val colors = Theme.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = dua.titleEn,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            color = colors.primaryText
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors.accentColorSoft)
                .padding(horizontal = 11.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                specialDuaIcon(dua.id),
                contentDescription = null,
                tint = colors.accentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = dua.whenEn,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentColor
            )
        }
        Text(
            text = dua.attributionEn,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.secondaryText
        )
        Text(
            text = dua.introEn,
            fontSize = (16 * scale).sp,
            lineHeight = (16 * scale * 1.5f).sp,
            color = colors.secondaryText
        )
    }
}

@Composable
private fun SegmentRow(
    seg: SpecialDuaSegment,
    index: Int,
    karaoke: DuaKaraokeEngine,
    gold: Color,
    scale: Float,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val isCurrent = karaoke.currentSegment == index
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrent) gold.copy(alpha = 0.09f) else Color.Transparent)
            .clickable(onClick = onTap)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        val ar = seg.ar
        if (!ar.isNullOrEmpty()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = arabicAnnotated(ar, index, karaoke.currentWord, gold),
                    fontFamily = AmiriFamily,
                    fontSize = (26 * scale).sp,
                    lineHeight = (26 * scale * 1.7f).sp,
                    color = colors.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        val tr = seg.tr
        if (!tr.isNullOrEmpty()) {
            Text(
                text = tr,
                fontFamily = CormorantFamily,
                fontStyle = FontStyle.Italic,
                fontSize = (15 * scale).sp,
                lineHeight = (15 * scale * 1.4f).sp,
                color = colors.tertiaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        val en = seg.en
        if (!en.isNullOrEmpty()) {
            Text(
                text = en,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (16.5 * scale).sp,
                lineHeight = (16.5 * scale * 1.45f).sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** The segment's Arabic with the word currently being recited in gold. Word addressing
 *  matches the aligner (DuaArabicTokenizer); Arabic letter joining never crosses a
 *  space, so per-word coloring cannot break the script's shaping. */
private fun arabicAnnotated(
    ar: String,
    segmentIndex: Int,
    current: DuaWordPosition?,
    gold: Color
): AnnotatedString {
    if (current == null || current.segment != segmentIndex) return AnnotatedString(ar)
    val tokens = DuaArabicTokenizer.tokens(ar)
    if (current.token >= tokens.size) return AnnotatedString(ar)
    return buildAnnotatedString {
        tokens.forEachIndexed { i, token ->
            if (i > 0) append(" ")
            if (i == current.token) withStyle(SpanStyle(color = gold)) { append(token) } else append(token)
        }
    }
}

@Composable
private fun NoteRow(note: String) {
    val colors = Theme.colors
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.accentColor.copy(alpha = 0.4f)))
        Text(
            text = note,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.accentColor,
            textAlign = TextAlign.Center
        )
        Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.accentColor.copy(alpha = 0.4f)))
    }
}

@Composable
private fun CreditFooter(dua: SpecialDua) {
    val colors = Theme.colors
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val reciter = dua.reciterEn
        if (!reciter.isNullOrEmpty()) {
            Text(
                text = "Recitation by $reciter",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.tertiaryText
            )
        }
        Text(
            text = "Text & recitation courtesy of Duas.org",
            fontSize = 12.sp,
            color = colors.tertiaryText
        )
    }
}

@Composable
private fun ShareButton(dua: SpecialDua, context: android.content.Context) {
    val colors = Theme.colors
    val shareText = """
        ${dua.titleEn}
        ${dua.whenEn} · ${dua.attributionEn}

        ${dua.introEn}

        Read & listen in Thaqalayn
    """.trimIndent()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(colors.accentGradient)
            .pressable {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Share dua"))
            }
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Share, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(15.dp))
        Text(text = "Share", fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = colors.onAccentText)
    }
}

/** Play / pause / resume + buffering spinner + scrubbing bar for a streamed recitation. */
@Composable
private fun StreamListenBar(dua: SpecialDua) {
    val colors = Theme.colors
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableStateOf(0f) }

    val isThis = DuaStreamPlayer.currentId == dua.id
    val isPlayingThis = isThis && DuaStreamPlayer.isPlaying
    val isPausedThis = isThis && DuaStreamPlayer.isPaused
    val isLoadingThis = isThis && DuaStreamPlayer.isLoading

    val label = when {
        isThis && DuaStreamPlayer.failed -> "Try again"
        isLoadingThis -> "Loading"
        isPlayingThis -> "Pause"
        isPausedThis -> "Resume"
        else -> "Listen"
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(colors.accentChip)
                .border(1.dp, colors.strokeColor, RoundedCornerShape(50))
                .pressable {
                    if (isThis && (DuaStreamPlayer.isPlaying || DuaStreamPlayer.isPaused)) {
                        DuaStreamPlayer.togglePlayPause()
                    } else {
                        DuaStreamPlayer.play(dua)
                    }
                }
                .padding(vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoadingThis) {
                CircularProgressIndicator(color = colors.accentColor, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Icon(
                    if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.accentColor)
        }

        if (isThis && DuaStreamPlayer.duration > 0) {
            val dur = DuaStreamPlayer.duration.toFloat()
            Column {
                Slider(
                    value = if (isScrubbing) scrubValue else DuaStreamPlayer.currentTime.toFloat().coerceIn(0f, dur),
                    onValueChange = { isScrubbing = true; scrubValue = it },
                    onValueChangeFinished = {
                        DuaStreamPlayer.seek(scrubValue.toDouble())
                        isScrubbing = false
                    },
                    valueRange = 0f..dur
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = timeString((if (isScrubbing) scrubValue.toDouble() else DuaStreamPlayer.currentTime)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.tertiaryText
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timeString(DuaStreamPlayer.duration),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.tertiaryText
                    )
                }
            }
        }
    }
}
