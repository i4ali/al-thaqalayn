package com.thaqalayn.app.ui.passages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.SurahWithTafsir
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.model.wordCount
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmJourneyToggleButton
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.TextSizeButton
import com.thaqalayn.app.ui.components.TextSizePanelOverlay
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.VerseRecitationButton
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.reader.SurahAudioPlayerBar
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * One passage (a ruku) read in full (iOS PassageView): its verses in order, then
 * one control, Finish reading, which marks the passage read and returns to the
 * hub. Only that (or reading every verse elsewhere) marks the passage read;
 * scrolling to the end does not. The header heart saves the whole passage; each
 * verse keeps its own heart in the rail.
 */
@Composable
fun PassageScreen(
    surahNumber: Int,
    passageIndex: Int,
    scrollToVerse: Int?,
    navController: NavHostController
) {
    val tm = Theme.colors
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val data by produceState<SurahWithTafsir?>(initialValue = null, surahNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        value = DataManager.shared.loadSurahWithTafsir(surah)
    }
    LaunchedEffect(surahNumber) { PassageStore.load(surahNumber) }
    val index = DataManager.shared.passageIndex
    val ref = index?.passage(surahNumber, passageIndex)

    var showTextSizePanel by remember { mutableStateOf(false) }
    var savePop by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(if (savePop) 1.25f else 1f, spring(dampingRatio = 0.6f, stiffness = 500f), label = "heart")
    LaunchedEffect(savePop) { if (savePop) { delay(300); savePop = false } }

    val listState = rememberLazyListState()
    // The scroll target is handled once; returning from Gems must not scroll again.
    var didHandleScrollTarget by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current

    // Opening the passage puts the reading position at its first verse (unless deeper).
    LaunchedEffect(ref) { ref?.let { ProgressManager.enterPassage(it) } }

    TextSizePanelOverlay(isOpen = showTextSizePanel, onDismiss = { showTextSizePanel = false }) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThemedBackground()
            val surahWithTafsir = data
            if (ref == null || surahWithTafsir == null) return@Box
            val surah = surahWithTafsir.surah
            val versesInRange = remember(surahWithTafsir, ref) {
                surahWithTafsir.verses.filter { it.number in ref.start..ref.end }
            }
            val title = PassageStore.title(ref)
            val passageCount = index.passages(surahNumber).size
            val readingMinutes = maxOf(1, versesInRange.sumOf { it.translation.wordCount() } / 200 + 1)
            val subline = "${if (ref.verseCount == 1) "1 verse" else "${ref.verseCount} verses"} · $readingMinutes min"
            val isSaved = BookmarkManager.isPassageBookmarked(surahNumber, ref.index)
            val isRead = ProgressManager.isPassageRead(ref)
            val playingVerse = AudioManager.currentPlayback?.takeIf { it.surahNumber == surahNumber }?.verseNumber

            // Deep links land on a verse; the first verse already sits under the title.
            LaunchedEffect(Unit) {
                val target = scrollToVerse ?: return@LaunchedEffect
                if (didHandleScrollTarget) return@LaunchedEffect
                didHandleScrollTarget = true
                if (target != ref.start) {
                    delay(400)
                    val item = target - ref.start + 1
                    val offset = -with(density) { 160.dp.roundToPx() }
                    listState.animateScrollToItem(item, offset)
                }
            }

            // Follow playback through this passage.
            LaunchedEffect(playingVerse) {
                val n = playingVerse ?: return@LaunchedEffect
                if (n !in ref.start..ref.end) return@LaunchedEffect
                listState.animateScrollToItem(n - ref.start + 1, -with(density) { 160.dp.roundToPx() })
            }

            // The verse at the top of the screen is recorded as the reading position
            // when the reader leaves, so Continue Reading returns here.
            var topVerse by remember { mutableStateOf<Int?>(null) }
            LaunchedEffect(listState, ref) {
                val threshold = with(density) { 80.dp.roundToPx() }
                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                        item.index >= 1 && item.index <= ref.verseCount && item.offset + item.size > threshold
                    }?.index
                }.collect { itemIndex -> topVerse = itemIndex?.let { ref.start + it - 1 } }
            }
            DisposableEffect(ref) {
                onDispose { topVerse?.let { ProgressManager.leavePassage(ref, it) } }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
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
                    GlassCircleButton(
                        icon = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isSaved) "Remove passage bookmark" else "Save passage",
                        tint = if (isSaved) tm.accentBright else tm.accentColor,
                        popScale = heartScale
                    ) {
                        val arabic = versesInRange.firstOrNull { it.number == ref.start }?.arabicText ?: ""
                        when (BookmarkManager.togglePassageBookmark(ref, surah.englishName, title, arabic)) {
                            BookmarkManager.PassageToggleResult.SAVED -> {
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                savePop = true
                            }
                            BookmarkManager.PassageToggleResult.REFUSED -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
                            BookmarkManager.PassageToggleResult.REMOVED -> {}
                        }
                    }
                    GlassCircleButton(Icons.Filled.PlayArrow, contentDescription = "Play passage") {
                        AudioManager.playVerseSequence(versesInRange, surah)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 48.dp)
                ) {
                    item(key = "title") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Eyebrow("Passage ${ref.index} of $passageCount · verses ${ref.rangeLabel}", tm.accentColor)
                            Text(
                                text = title,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 30.sp,
                                lineHeight = 34.sp,
                                color = tm.primaryText
                            )
                            Text(text = subline, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = tm.secondaryText)
                        }
                    }
                    items(versesInRange, key = { it.number }) { verse ->
                        PassageVerseRow(
                            verse = verse,
                            surah = surah,
                            isPlaying = playingVerse == verse.number,
                            onGems = {
                                if (PremiumManager.canAccessOverview(surahNumber)) {
                                    navController.navigate("summary/$surahNumber/${verse.number}")
                                } else {
                                    navController.navigate(Routes.paywall())
                                }
                            }
                        )
                    }
                    item(key = "finish") {
                        // Finish reading marks the passage read and, once the seal has
                        // been seen, returns to the hub. Tapping the green state unmarks.
                        EmJourneyToggleButton(
                            isDone = isRead,
                            doneLabel = "Marked as read",
                            todoLabel = "Finish reading",
                            doneTint = tm.semanticGreen,
                            modifier = Modifier.padding(top = 28.dp)
                        ) {
                            if (isRead) {
                                ProgressManager.unmarkPassageRead(ref)
                            } else {
                                ProgressManager.markPassageRead(ref)
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

            AnimatedVisibility(
                visible = AudioManager.currentPlayback != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SurahAudioPlayerBar()
            }
        }
    }
}

/**
 * One verse (iOS PassageVerseRow): the numeral row, whose right side carries the
 * verse's tools (listen, save, gems) as quiet glyphs, then the Arabic and the
 * translation. The tools are chrome and keep a fixed size; the reading text scales.
 */
@Composable
private fun PassageVerseRow(
    verse: VerseWithTafsir,
    surah: Surah,
    isPlaying: Boolean,
    onGems: () -> Unit
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val isBookmarked = BookmarkManager.isBookmarked(surah.number, verse.number)
    // Idle glyphs sit at this strength so the page still reads as a page.
    val idleOpacity = 0.55f
    var pop by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(if (pop) 1.25f else 1f, spring(dampingRatio = 0.6f, stiffness = 500f), label = "verseHeart")
    LaunchedEffect(pop) { if (pop) { delay(300); pop = false } }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isPlaying) tm.accentColor.copy(alpha = 0.08f) else Color.Transparent)
                .padding(vertical = 18.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmNumeralCircle(n = verse.number, size = 30.dp)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    VerseRecitationButton(surah = surah, verse = verse, size = 32.dp, quiet = true)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .pressable {
                                val wasSaved = isBookmarked
                                BookmarkManager.toggleBookmark(
                                    surah.number, verse.number, surah.englishName, verse.arabicText, verse.translation
                                )
                                if (!wasSaved && BookmarkManager.isBookmarked(surah.number, verse.number)) pop = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark verse",
                            tint = if (isBookmarked) tm.accentBright else tm.accentColor,
                            modifier = Modifier
                                .size(15.dp)
                                .alpha(if (isBookmarked) 1f else idleOpacity)
                                .scale(heartScale)
                        )
                    }
                    val hasGems = verse.tafsir?.quickOverview != null
                    Row(
                        modifier = Modifier
                            .height(32.dp)
                            .let { if (hasGems) it.pressable(onClick = onGems) else it }
                            .padding(horizontal = 6.dp)
                            .alpha(if (hasGems) idleOpacity else 0.28f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Gems for this verse", tint = tm.accentColor, modifier = Modifier.size(13.dp))
                        Text(text = "Gems", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = tm.accentColor)
                    }
                }
            }

            // Laid out right to left, so Start is the right edge.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = verse.arabicText,
                    fontFamily = AmiriFamily,
                    fontSize = (27 * scale).sp,
                    lineHeight = (27 * scale * 1.9f).sp,
                    color = tm.primaryText,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = verse.translation,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (17 * scale).sp,
                lineHeight = (17 * scale * 1.4f).sp,
                color = tm.secondaryText,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Hairline(modifier = Modifier.padding(horizontal = 12.dp))
    }
}
