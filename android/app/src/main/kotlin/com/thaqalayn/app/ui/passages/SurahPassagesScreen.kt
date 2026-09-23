package com.thaqalayn.app.ui.passages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageProgress
import com.thaqalayn.app.data.PassageStageStore
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.QuizResultsStore
import com.thaqalayn.app.data.QuizStore
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.PassageStages
import com.thaqalayn.app.model.SurahWithTafsir
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.reader.SurahAudioPlayerBar
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A surah as the list of its passages, one per ruku (iOS SurahPassagesView). Every
 * navigation into a surah lands here. Tapping a row opens the passage hub; each
 * row's ring shows how many of the passage's stages are done. A leading swipe
 * reveals Save / Unsave for the whole passage; read state only ever changes from
 * the reader. A target verse (deep link, go-to-verse) opens the reader for the
 * passage that holds it; a target passage index opens that passage's hub.
 */
@Composable
fun SurahPassagesScreen(
    surahNumber: Int,
    targetVerse: Int?,
    targetPassageIndex: Int?,
    navController: NavHostController
) {
    val colors = Theme.colors

    val data by produceState<SurahWithTafsir?>(initialValue = null, surahNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        value = DataManager.shared.loadSurahWithTafsir(surah)
    }
    LaunchedEffect(surahNumber) {
        PassageStore.load(surahNumber)
        QuizStore.load(surahNumber)
    }

    val index = DataManager.shared.passageIndex
    val passages = index?.passages(surahNumber).orEmpty()
    val ready = data != null && index != null &&
        PassageStore.isLoaded(surahNumber) && QuizStore.isLoaded(surahNumber)

    // A deep link pushes once; coming back from the reader must not push it again.
    var didOpenTarget by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(ready) {
        if (!ready || didOpenTarget) return@LaunchedEffect
        if (targetPassageIndex != null) {
            if (index?.passage(surahNumber, targetPassageIndex) != null) {
                didOpenTarget = true
                navController.navigate(Routes.passageHub(surahNumber, targetPassageIndex))
            }
        } else if (targetVerse != null) {
            val ref = index?.passageContaining(surahNumber, targetVerse) ?: return@LaunchedEffect
            didOpenTarget = true
            navController.navigate(Routes.passage(surahNumber, ref.index, targetVerse))
        }
    }

    var showGoToVerse by remember { mutableStateOf(false) }
    var revealedIndex by remember { mutableStateOf<Int?>(null) }
    val haptics = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            val surahWithTafsir = data
            // Header: back, then go-to-verse and play-the-surah chips; title + subline.
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PassageBackButton { navController.popBackStack() }
                    Spacer(modifier = Modifier.weight(1f))
                    GlassCircleButton(Icons.Filled.Search, contentDescription = "Go to verse") {
                        if (surahWithTafsir != null) showGoToVerse = true
                    }
                    GlassCircleButton(Icons.Filled.PlayArrow, contentDescription = "Play surah") {
                        surahWithTafsir?.let { AudioManager.playVerseSequence(it.verses, it.surah) }
                    }
                }
                if (surahWithTafsir != null) {
                    val readCount = PassageProgress.readCount(passages, ProgressManager.readVerseKeys)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = surahWithTafsir.surah.englishName,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 30.sp,
                            color = colors.primaryText
                        )
                        val count = passages.size
                        val passagesLabel = if (count == 1) "1 passage" else "$count passages"
                        Text(
                            text = "${surahWithTafsir.surah.englishNameTranslation} · ${surahWithTafsir.surah.revelationType} · $passagesLabel · $readCount read",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.secondaryText
                        )
                    }
                }
            }

            if (ready && surahWithTafsir != null) {
                // Computed once per recomposition; every row reads from them.
                val readVerseKeys = ProgressManager.readVerseKeys
                val savedIndices = BookmarkManager.bookmarkedPassageIndices(surahNumber)
                val quizIndices = QuizStore.quizIndices(surahNumber)
                val bestScores = QuizResultsStore.bestScores(surahNumber)
                val understood = PassageStageStore.understoodIndices(surahNumber)
                val readingRef = ProgressManager.lastReadInfo
                    ?.takeIf { it.surahNumber == surahNumber }
                    ?.let { index?.passageContaining(surahNumber, it.verseNumber) }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp)
                ) {
                    items(passages, key = { it.id }) { ref ->
                        val isSaved = ref.index in savedIndices
                        val hasCommentary = PassageStore.hasCommentary(surahNumber, ref.index)
                        val stages = PassageStages(
                            isRead = PassageProgress.isRead(ref, readVerseKeys),
                            isUnderstood = ref.index in understood,
                            bestScore = bestScores[ref.index],
                            hasCommentary = hasCommentary,
                            hasQuiz = ref.index in quizIndices
                        )
                        Column {
                            if (ref.index > 1) {
                                Hairline(modifier = Modifier.padding(start = 54.dp))
                            }
                            SwipeToSaveRow(
                                isSaved = isSaved,
                                isRevealed = revealedIndex == ref.index,
                                onRevealChange = { revealed ->
                                    revealedIndex = if (revealed) ref.index else revealedIndex.takeIf { it != ref.index }
                                },
                                onSave = {
                                    revealedIndex = null
                                    val arabic = surahWithTafsir.verses.firstOrNull { it.number == ref.start }?.arabicText ?: ""
                                    when (
                                        BookmarkManager.togglePassageBookmark(
                                            ref, surahWithTafsir.surah.englishName, PassageStore.title(ref), arabic
                                        )
                                    ) {
                                        BookmarkManager.PassageToggleResult.SAVED -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                        BookmarkManager.PassageToggleResult.REFUSED -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
                                        BookmarkManager.PassageToggleResult.REMOVED -> {}
                                    }
                                },
                                onTap = {
                                    if (revealedIndex != null) {
                                        revealedIndex = null
                                    } else {
                                        navController.navigate(Routes.passageHub(surahNumber, ref.index))
                                    }
                                }
                            ) {
                                PassageRow(
                                    ref = ref,
                                    title = PassageStore.title(ref),
                                    hasCommentary = hasCommentary,
                                    stages = stages,
                                    isSaved = isSaved,
                                    isReading = ref == readingRef
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom audio player overlay
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

    if (showGoToVerse) {
        GoToVerseDialog(
            versesCount = data?.surah?.versesCount ?: 0,
            onDismiss = { showGoToVerse = false },
            onGo = { verseNumber ->
                showGoToVerse = false
                val ref = index?.passageContaining(surahNumber, verseNumber)
                if (ref != null) navController.navigate(Routes.passage(surahNumber, ref.index, verseNumber))
            }
        )
    }
}

/**
 * One passage in the list: glyph, title, verse range, and on the trailing edge the
 * best quiz score once taken, a heart when bookmarked, "reading" when it holds the
 * reading position, and the ring of its stages, gold for each one done.
 */
@Composable
private fun PassageRow(
    ref: PassageRef,
    title: String,
    hasCommentary: Boolean,
    stages: PassageStages,
    isSaved: Boolean,
    isReading: Boolean
) {
    val tm = Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isReading) tm.accentColor.copy(alpha = 0.08f) else Color.Transparent)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "ع",
            fontFamily = AmiriFamily,
            fontSize = 20.sp,
            color = tm.accentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = tm.primaryText
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = ref.rangeLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = tm.secondaryText)
                if (!hasCommentary) {
                    Text(text = " · coming soon", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = tm.tertiaryText)
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stages.bestScore?.let { best ->
                Text(
                    text = QuizStrings.scoreShort(best, stages.quizTotal),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tm.tertiaryText,
                    modifier = Modifier.semantics { contentDescription = "Quiz best score $best of ${stages.quizTotal}" }
                )
            }
            if (isSaved) {
                Icon(Icons.Filled.Favorite, contentDescription = "Saved", tint = tm.accentBright, modifier = Modifier.size(13.dp))
            }
            if (isReading) {
                Text(text = "reading", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = tm.accentColor)
            }
            Box(modifier = Modifier.semantics { contentDescription = "${stages.doneCount} of ${stages.total} stages done" }) {
                PassageProgressRing(done = stages.doneCount, total = stages.total)
            }
        }
    }
}

/**
 * A row whose leading swipe reveals one Save / Unsave button, the Android form of
 * the iOS swipe action. No full swipe: a bookmark should be a deliberate tap, not
 * a flick, so the row only ever rests closed or open on the button.
 */
@Composable
private fun SwipeToSaveRow(
    isSaved: Boolean,
    isRevealed: Boolean,
    onRevealChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onTap: () -> Unit,
    content: @Composable () -> Unit
) {
    val tm = Theme.colors
    val density = LocalDensity.current
    val revealWidth = 92.dp
    val revealPx = with(density) { revealWidth.toPx() }
    val offset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isRevealed) {
        offset.animateTo(if (isRevealed) revealPx else 0f, spring(dampingRatio = 0.85f, stiffness = 500f))
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // The action behind the row, drawn only while the row is pulled aside (the
        // row is transparent over the page ground, so it would show through).
        if (offset.value > 0.5f) Box(
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 6.dp)
                .alpha((offset.value / revealPx).coerceIn(0f, 1f))
        ) {
            Column(
                modifier = Modifier
                    .width(revealWidth - 8.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSaved) tm.secondaryText else tm.accentColor)
                    .pressable(onClick = onSave),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    if (isSaved) Icons.Filled.HeartBroken else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = tm.onAccentText,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isSaved) "Unsave" else "Save",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tm.onAccentText
                )
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(isRevealed) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val open = offset.value > revealPx / 2
                            scope.launch { offset.animateTo(if (open) revealPx else 0f, spring(dampingRatio = 0.85f, stiffness = 500f)) }
                            onRevealChange(open)
                        },
                        onDragCancel = {
                            scope.launch { offset.animateTo(if (isRevealed) revealPx else 0f) }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        scope.launch { offset.snapTo((offset.value + dragAmount).coerceIn(0f, revealPx * 1.15f)) }
                    }
                }
                .pressableGentle(onClick = onTap)
        ) {
            content()
        }
    }
}

@Composable
private fun GoToVerseDialog(
    versesCount: Int,
    onDismiss: () -> Unit,
    onGo: (Int) -> Unit
) {
    val colors = Theme.colors
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colors.tertiaryBackground)
                .border(1.dp, colors.strokeColor, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(40.dp))
            Text("Go to Verse", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
            Text(
                "Enter a verse number (1-$versesCount)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryText
            )
            TextField(
                value = text,
                onValueChange = { newValue ->
                    error = null
                    text = newValue.filter { it.isDigit() }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primaryText,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.glassSurface,
                    unfocusedContainerColor = colors.glassSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colors.accentColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            error?.let {
                Text(it, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.semanticRed)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.accentGradient)
                    .pressable {
                        val number = text.toIntOrNull()
                        when {
                            number == null -> error = "Please enter a valid number"
                            number < 1 -> error = "Verse number must be at least 1"
                            number > versesCount -> error = "This surah only has $versesCount verses"
                            else -> onGo(number)
                        }
                    }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Go", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = colors.onAccentText)
            }
        }
    }
}
