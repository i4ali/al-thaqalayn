package com.thaqalayn.app.ui.passages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.TafsirReader
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageStageStore
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Narration
import com.thaqalayn.app.model.Passage
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.PassageSource
import com.thaqalayn.app.model.PassageVerseEntry
import com.thaqalayn.app.model.QuizAnchor
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.availableLanguages
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmJourneyToggleButton
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.TextSizeButton
import com.thaqalayn.app.ui.components.TextSizePanelOverlay
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The passage commentary read in full (iOS UnderstandingView): the essay with its
 * citation markers, then verse by verse notes and narrations, perspectives where
 * present, the bibliography, and one Finish control that seals the Understand
 * stage and returns to the hub. Tapping a marker, a narration's source line or a
 * bibliography row opens the source sheet. [scrollTarget] is a block id to land
 * on (from a quiz anchor); see [understandingScrollId].
 */
@Composable
fun UnderstandingScreen(
    surahNumber: Int,
    passageIndex: Int,
    scrollTarget: String?,
    navController: NavHostController
) {
    val surah by produceState<Surah?>(initialValue = null, surahNumber) {
        value = DataManager.shared.surah(surahNumber)
    }
    LaunchedEffect(surahNumber) { PassageStore.load(surahNumber) }
    val ref = DataManager.shared.passageIndex?.passage(surahNumber, passageIndex)
    val passage = PassageStore.passage(surahNumber, passageIndex)

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        val s = surah
        if (s != null && ref != null && passage != null) {
            UnderstandingContent(s, ref, passage, scrollTarget, navController)
        }
    }
}

/** One scrollable unit of the screen; prose blocks carry their listen/scroll id. */
private sealed interface UBlock {
    val key: String
    data object Title : UBlock { override val key = "title" }
    data object Pills : UBlock { override val key = "pills" }
    data class Label(val text: String) : UBlock { override val key = "label.$text" }
    data class Prose(override val key: String, val text: String) : UBlock
    data class VerseHead(val entry: PassageVerseEntry) : UBlock { override val key = "v${entry.verse}.heading" }
    data class Note(val verse: Int, val text: String) : UBlock { override val key = "v$verse.note" }
    data class NarrationBlock(val verse: Int, val narration: Narration) : UBlock { override val key = "v$verse.${narration.id}" }
    data class SourceRow(val source: PassageSource, val last: Boolean) : UBlock { override val key = "src.${source.id}" }
    data object Finish : UBlock { override val key = "finish" }
}

/** One block of prose as the voice reads it: its id, its marker-stripped text and its span in the whole. */
private data class ListenPart(val id: String, val spoken: String, val start: Int) {
    val end: Int get() = start + spoken.length
}

@Composable
private fun UnderstandingContent(
    surah: Surah,
    ref: PassageRef,
    passage: Passage,
    scrollTarget: String?,
    navController: NavHostController
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var showTextSizePanel by remember { mutableStateOf(false) }
    var openedSource by remember { mutableStateOf<PassageSource?>(null) }

    val available = passage.essay.availableLanguages
    val selected = CommentaryLanguageManager.selectedLanguage
    // The reader's language, or English when this passage has not been translated into it.
    val lang = if (selected in available) selected else CommentaryLanguage.ENGLISH
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    val essayParagraphs = remember(passage, lang) { paragraphs(passage.essay.text(lang)) }
    val perspectiveParagraphs = remember(passage, lang) { passage.perspectives?.let { paragraphs(it.text(lang)) }.orEmpty() }
    val orderedSources = remember(passage) { passage.sources.sortedBy { it.number } }

    // Everything on screen in reading order, one part per prose block, so a spoken
    // range can be traced back to the block that shows it.
    val listenParts = remember(passage, lang) {
        val texts = mutableListOf<Pair<String, String>>()
        essayParagraphs.forEachIndexed { i, p -> texts += "essay.$i" to p }
        for (entry in passage.verses) {
            entry.heading?.let { texts += "v${entry.verse}.heading" to it.text(lang) }
            entry.note?.let { texts += "v${entry.verse}.note" to it.text(lang) }
            entry.narrations.forEach { n -> texts += "v${entry.verse}.${n.id}" to n.text.text(lang) }
        }
        perspectiveParagraphs.forEachIndexed { i, p -> texts += "persp.$i" to p }
        var offset = 0
        texts.map { (id, text) ->
            val spoken = PassageMarkup.stripMarkers(text)
            ListenPart(id, spoken, offset).also { offset += spoken.length + 2 } // the "\n\n" between parts
        }
    }
    val listenText = remember(listenParts) { listenParts.joinToString("\n\n") { it.spoken } }

    val isListeningToThis = TafsirReader.currentText == listenText
    val isPlayingThis = isListeningToThis && TafsirReader.isPlaying
    val isPausedThis = isListeningToThis && TafsirReader.isPaused
    // The block being spoken and the word within it, while this passage plays or pauses.
    val spoken: Pair<String, IntRange>? = run {
        if (!isListeningToThis || !(TafsirReader.isPlaying || TafsirReader.isPaused)) return@run null
        val range = TafsirReader.highlightRange ?: return@run null
        val part = listenParts.firstOrNull { range.first >= it.start && range.first < it.end } ?: return@run null
        val localStart = range.first - part.start
        val localEnd = minOf(range.last + 1, part.end) - part.start
        part.id to (localStart until localEnd)
    }

    val blocks = remember(passage, lang, orderedSources) {
        buildList {
            add(UBlock.Title)
            if (available.size > 1) add(UBlock.Pills)
            essayParagraphs.forEachIndexed { i, p -> add(UBlock.Prose("essay.$i", p)) }
            if (passage.verses.isNotEmpty()) {
                add(UBlock.Label("Verse by verse"))
                for (entry in passage.verses) {
                    add(UBlock.VerseHead(entry))
                    entry.note?.let { add(UBlock.Note(entry.verse, it.text(lang))) }
                    entry.narrations.forEach { add(UBlock.NarrationBlock(entry.verse, it)) }
                }
            }
            if (perspectiveParagraphs.isNotEmpty()) {
                add(UBlock.Label("Perspectives"))
                perspectiveParagraphs.forEachIndexed { i, p -> add(UBlock.Prose("persp.$i", p)) }
            }
            if (orderedSources.isNotEmpty()) {
                add(UBlock.Label("Sources"))
                orderedSources.forEachIndexed { i, src -> add(UBlock.SourceRow(src, i == orderedSources.lastIndex)) }
            }
            add(UBlock.Finish)
        }
    }
    // Block id -> list index, for quiz-anchor landing and following the voice.
    val indexOfId = remember(blocks) { blocks.withIndex().associate { (i, b) -> b.key to i } }

    val listState = rememberLazyListState()

    // Land on the block a quiz question pointed at, once.
    var didScrollToTarget by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(scrollTarget) {
        val target = scrollTarget ?: return@LaunchedEffect
        if (didScrollToTarget) return@LaunchedEffect
        didScrollToTarget = true
        val i = indexOfId[target] ?: return@LaunchedEffect
        delay(350)
        listState.animateScrollToItem(i)
    }

    // Follow the voice: bring each block into view as the reading reaches it.
    val spokenId = spoken?.first
    LaunchedEffect(spokenId) {
        val id = spokenId ?: return@LaunchedEffect
        if (!TafsirReader.isPlaying) return@LaunchedEffect
        val i = indexOfId[id] ?: return@LaunchedEffect
        val visible = listState.layoutInfo.visibleItemsInfo.any { it.index == i && it.offset >= 0 }
        if (!visible) listState.animateScrollToItem(i, -200)
    }

    // A language change or leaving the screen stops the reading rather than finishing it
    // (the effect is keyed on the language, so a change disposes it with the old text).
    DisposableEffect(lang) {
        onDispose { if (TafsirReader.currentText == listenText) TafsirReader.stop() }
    }

    val openSourceNumber: (Int) -> Unit = { n ->
        passage.sources.firstOrNull { it.number == n }?.let { openedSource = it }
    }
    val proseStyle = { size: Float, italic: Boolean, weight: FontWeight ->
        TextStyle(
            fontFamily = CormorantFamily,
            fontWeight = weight,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            fontSize = size.sp,
            lineHeight = (size * 1.4f).sp
        )
    }

    TextSizePanelOverlay(isOpen = showTextSizePanel, onDismiss = { showTextSizePanel = false }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: back, text size, Listen chip.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PassageBackButton { navController.popBackStack() }
                Spacer(modifier = Modifier.weight(1f))
                TextSizeButton(isOpen = showTextSizePanel) { showTextSizePanel = !showTextSizePanel }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .pressable {
                            if (isListeningToThis && (TafsirReader.isPlaying || TafsirReader.isPaused)) {
                                TafsirReader.togglePlayPause()
                            } else {
                                TafsirReader.speak(listenText, lang)
                            }
                        }
                        .clip(CircleShape)
                        .background(tm.accentChip)
                        .border(1.dp, tm.strokeColor, CircleShape)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlayingThis) "Pause reading" else "Listen to the commentary",
                        tint = tm.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = when {
                            isPlayingThis -> "Pause"
                            isPausedThis -> "Resume"
                            else -> "Listen"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tm.accentColor
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 48.dp)
            ) {
                // Index-prefixed keys: a duplicate id in future data must not crash the list.
                itemsIndexed(blocks, key = { i, b -> "$i.${b.key}" }) { i, block ->
                    val previous = blocks.getOrNull(i - 1)
                    CompositionLocalProvider(LocalLayoutDirection provides direction) {
                        when (block) {
                            UBlock.Title -> Column(
                                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Eyebrow("Understanding · ${surah.englishName} ${ref.rangeLabel}", tm.accentColor)
                                }
                                Text(
                                    text = passage.title.text(lang),
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 30.sp,
                                    lineHeight = 34.sp,
                                    color = tm.primaryText,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            UBlock.Pills -> Row(
                                modifier = Modifier.padding(bottom = 18.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (language in available) {
                                    val isSel = language == lang
                                    Text(
                                        text = language.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSel) tm.onAccentText else tm.accentColor,
                                        modifier = Modifier
                                            .pressable {
                                                if (TafsirReader.currentText == listenText) TafsirReader.stop()
                                                CommentaryLanguageManager.setLanguage(language)
                                            }
                                            .clip(CircleShape)
                                            .let {
                                                if (isSel) it.background(tm.accentGradient)
                                                else it.background(tm.accentChip).border(1.dp, tm.strokeColor, CircleShape)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }

                            is UBlock.Label -> EmDivider(
                                label = block.text,
                                modifier = Modifier.padding(top = 30.dp, bottom = if (block.text == "Sources") 14.dp else 18.dp)
                            )

                            is UBlock.Prose -> MarkedText(
                                text = block.text,
                                style = proseStyle(17f * scale, false, FontWeight.Medium),
                                color = tm.primaryText,
                                highlight = spoken?.takeIf { it.first == block.key }?.second,
                                onMarker = openSourceNumber,
                                modifier = Modifier.padding(bottom = if (blocks.getOrNull(i + 1) is UBlock.Prose) (14 * scale).dp else 0.dp)
                            )

                            is UBlock.VerseHead -> Row(
                                modifier = Modifier.padding(top = if (previous is UBlock.Label) 0.dp else 22.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                EmNumeralCircle(n = block.entry.verse, size = 26.dp)
                                block.entry.heading?.let { heading ->
                                    MarkedText(
                                        text = heading.text(lang),
                                        style = proseStyle(17f, false, FontWeight.SemiBold),
                                        color = tm.primaryText,
                                        highlight = spoken?.takeIf { it.first == block.key }?.second,
                                        onMarker = openSourceNumber
                                    )
                                }
                            }

                            is UBlock.Note -> MarkedText(
                                text = block.text,
                                style = proseStyle(16f * scale, true, FontWeight.Medium),
                                color = tm.secondaryText,
                                highlight = spoken?.takeIf { it.first == block.key }?.second,
                                onMarker = openSourceNumber,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            is UBlock.NarrationBlock -> NarrationView(
                                narration = block.narration,
                                source = passage.source(block.narration.source),
                                lang = lang,
                                style = proseStyle(16f * scale, false, FontWeight.Medium),
                                highlight = spoken?.takeIf { it.first == block.key }?.second,
                                onMarker = openSourceNumber,
                                onOpenSource = { openedSource = it }
                            )

                            is UBlock.SourceRow -> SourceListRow(block.source, block.last) { openedSource = block.source }

                            UBlock.Finish -> {
                                // Finish seals the Understand stage and returns to the hub (or the
                                // quiz question that opened this screen); green once sealed, tap to unseal.
                                val isUnderstood = PassageStageStore.isUnderstood(ref)
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    EmJourneyToggleButton(
                                        isDone = isUnderstood,
                                        doneLabel = "Understood",
                                        todoLabel = "Finish",
                                        doneTint = tm.semanticGreen,
                                        modifier = Modifier.padding(top = 30.dp)
                                    ) {
                                        if (isUnderstood) {
                                            PassageStageStore.unmarkUnderstood(ref)
                                        } else {
                                            PassageStageStore.markUnderstood(ref)
                                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                            scope.launch {
                                                delay(500)
                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    openedSource?.let { source ->
        SourceSheet(
            source = source,
            passage = passage,
            surahName = surah.englishName,
            onDismiss = { openedSource = null }
        )
    }
}

/** Prose whose "[n]" markers become tappable raised numbers, with the spoken word painted. */
@Composable
private fun MarkedText(
    text: String,
    style: TextStyle,
    color: Color,
    highlight: IntRange?,
    onMarker: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tm = Theme.colors
    val annotated = remember(text, highlight, style.fontSize, tm.accentColor) {
        PassageMarkup.annotated(
            text = text,
            fontSize = style.fontSize,
            accent = tm.accentColor,
            highlight = highlight,
            highlightColor = tm.accentColor.copy(alpha = 0.28f),
            onMarker = onMarker
        )
    }
    Text(text = annotated, style = style, color = color, modifier = modifier.fillMaxWidth())
}

/**
 * A narration: speaker, text and the source line, behind a thin accent rule. The
 * verbatim Arabic and the chain live on the source sheet.
 */
@Composable
private fun NarrationView(
    narration: Narration,
    source: PassageSource?,
    lang: CommentaryLanguage,
    style: TextStyle,
    highlight: IntRange?,
    onMarker: (Int) -> Unit,
    onOpenSource: (PassageSource) -> Unit
) {
    val tm = Theme.colors
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(1.dp))
                .background(tm.accentColor)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SpeakerLine(narration)
            MarkedText(
                text = narration.text.text(lang),
                style = style,
                color = tm.primaryText,
                highlight = highlight,
                onMarker = onMarker
            )
            if (source != null) {
                Text(
                    text = "Sourced · ${source.work} · ${source.locus}".uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = tm.accentColor,
                    modifier = Modifier.pressableGentle { onOpenSource(source) }
                )
            }
        }
    }
}

/** "Speaker to addressee", the speaker in semibold. */
@Composable
internal fun SpeakerLine(narration: Narration) {
    val tm = Theme.colors
    val line = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = tm.primaryText)) { append(narration.speaker) }
        val addressee = narration.addressee
        if (!addressee.isNullOrEmpty()) {
            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = tm.secondaryText)) { append(" to $addressee") }
        }
    }
    Text(text = line, fontFamily = CormorantFamily, fontSize = 15.sp)
}

@Composable
private fun SourceListRow(source: PassageSource, last: Boolean, onClick: () -> Unit) {
    val tm = Theme.colors
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableGentle(onClick = onClick)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "[${source.number}]",
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = tm.accentColor,
                modifier = Modifier.width(36.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = source.work, fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = tm.primaryText)
                Text(text = source.author, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = tm.secondaryText)
                Text(text = source.locus, fontSize = 12.sp, color = tm.tertiaryText)
            }
        }
        if (!last) Hairline()
    }
}

private fun paragraphs(text: String): List<String> =
    text.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }

/**
 * Maps a quiz anchor onto the block ids the Understanding screen tags its content
 * with (iOS UnderstandingView.scrollId). Essay and perspectives anchors land on
 * the paragraph that holds the quote, falling back to the first paragraph; verse
 * anchors land on the verse's heading, note or first narration.
 */
fun understandingScrollId(anchor: QuizAnchor, passage: Passage, lang: CommentaryLanguage): String? {
    val parts = anchor.location.split(".")
    return when (parts.firstOrNull()) {
        "essay" -> "essay.${paragraphIndex(anchor.quote, paragraphs(passage.essay.text(lang)))}"
        "perspectives" -> passage.perspectives?.let {
            "persp.${paragraphIndex(anchor.quote, paragraphs(it.text(lang)))}"
        }
        "verses" -> {
            val verse = parts.getOrNull(1)?.toIntOrNull()
            if (parts.size < 3 || verse == null) return null
            if (parts[2] == "narrations" && parts.size >= 4) return "v$verse.${parts[3]}"
            if (parts[2] == "note") return "v$verse.note"
            // translation or heading: the nearest block shown for that verse
            val entry = passage.entry(verse) ?: return null
            when {
                entry.heading != null -> "v$verse.heading"
                entry.note != null -> "v$verse.note"
                entry.narrations.isNotEmpty() -> "v$verse.${entry.narrations.first().id}"
                else -> null
            }
        }
        else -> null
    }
}

private fun paragraphIndex(quote: String, paragraphs: List<String>): Int {
    val needle = normalized(quote)
    return paragraphs.indexOfFirst { normalized(it).contains(needle) }.takeIf { it >= 0 } ?: 0
}

/** Straight quotes and single spaces, lowercased, so a quote survives curly punctuation and wrapping. */
private fun normalized(s: String): String =
    s.replace('‘', '\'').replace('’', '\'').replace('“', '"').replace('”', '"')
        .split(Regex("\\s+")).filter { it.isNotEmpty() }.joinToString(" ").lowercase()
