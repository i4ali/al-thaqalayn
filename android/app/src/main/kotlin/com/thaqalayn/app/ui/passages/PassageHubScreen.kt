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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageStageStore
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.QuizResultsStore
import com.thaqalayn.app.data.QuizStore
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.PassageStages
import com.thaqalayn.app.model.SurahWithTafsir
import com.thaqalayn.app.model.wordCount
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.reader.SurahAudioPlayerBar
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay

/**
 * A passage's home (iOS PassageHubView): its title and opening verse, then its
 * stages - Read, Understand, Test yourself - laid out as a path, each sealing
 * once finished, and a bottom bar that always carries the next step. The reader,
 * Understanding and the quiz are pushed from here and pop back here.
 */
@Composable
fun PassageHubScreen(
    surahNumber: Int,
    passageIndex: Int,
    navController: NavHostController
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val haptics = LocalHapticFeedback.current

    val data by produceState<SurahWithTafsir?>(initialValue = null, surahNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        value = DataManager.shared.loadSurahWithTafsir(surah)
    }
    LaunchedEffect(surahNumber) {
        PassageStore.load(surahNumber)
        QuizStore.load(surahNumber)
    }
    val index = DataManager.shared.passageIndex
    val ref = index?.passage(surahNumber, passageIndex)
    val surahWithTafsir = data

    var savePop by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(if (savePop) 1.25f else 1f, spring(dampingRatio = 0.6f, stiffness = 500f), label = "heart")
    LaunchedEffect(savePop) { if (savePop) { delay(300); savePop = false } }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        if (ref == null || surahWithTafsir == null ||
            !PassageStore.isLoaded(surahNumber) || !QuizStore.isLoaded(surahNumber)
        ) return@Box

        val surah = surahWithTafsir.surah
        val passage = PassageStore.passage(surahNumber, ref.index)
        val quiz = QuizStore.quiz(ref)
        val versesInRange = surahWithTafsir.verses.filter { it.number in ref.start..ref.end }
        val firstVerse = versesInRange.firstOrNull { it.number == ref.start }
        val passageCount = index.passages(surahNumber).size
        val nextRef = index.next(ref)
        val title = PassageStore.title(ref)
        val best = QuizResultsStore.best(ref)

        // Minutes to read the verses themselves at 200 words a minute, at least 1.
        val readingMinutes = maxOf(1, versesInRange.sumOf { it.translation.wordCount() } / 200 + 1)
        val readSubline = "${if (ref.verseCount == 1) "1 verse" else "${ref.verseCount} verses"} · $readingMinutes min"
        val understandSubline = passage?.let {
            val narrations = if (it.narrationCount == 1) "1 narration" else "${it.narrationCount} narrations"
            "Essay · $narrations · ${it.readingMinutes} min"
        } ?: PassageHubStrings.comingSoon
        val quizSubline = best?.let { PassageHubStrings.best(it.score, it.total) } ?: run {
            val count = quiz?.questions?.size ?: 0
            if (count == 1) "1 question" else "$count questions"
        }

        val stages = PassageStages(
            isRead = ProgressManager.isPassageRead(ref),
            isUnderstood = PassageStageStore.isUnderstood(ref),
            bestScore = best?.score,
            quizTotal = quiz?.questions?.size ?: 5,
            hasCommentary = passage != null,
            hasQuiz = quiz != null
        )
        val understandingGated = !PremiumManager.canAccessUnderstanding(surahNumber)
        val quizGated = !PremiumManager.canAccessQuiz(surahNumber)
        val isSaved = BookmarkManager.isPassageBookmarked(surahNumber, ref.index)
        val isPlaying = AudioManager.currentPlayback != null

        val openReader = { navController.navigate(Routes.passage(surahNumber, ref.index)) }
        val openUnderstanding = {
            if (passage != null) {
                if (understandingGated) navController.navigate(Routes.paywall())
                else navController.navigate(Routes.understanding(surahNumber, ref.index))
            }
        }
        val openQuiz = {
            if (quiz != null) {
                if (quizGated) navController.navigate(Routes.paywall())
                else navController.navigate(Routes.passageQuiz(surahNumber, ref.index))
            }
        }
        val openNext = { nextRef?.let { navController.navigate(Routes.passageHub(surahNumber, it.index)) } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: back, then the passage heart and play-the-passage.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PassageBackButton { navController.popBackStack() }
                Spacer(modifier = Modifier.weight(1f))
                GlassCircleButton(
                    icon = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isSaved) "Remove passage bookmark" else "Save passage",
                    tint = if (isSaved) tm.accentBright else tm.accentColor,
                    popScale = heartScale
                ) {
                    when (
                        BookmarkManager.togglePassageBookmark(ref, surah.englishName, title, firstVerse?.arabicText ?: "")
                    ) {
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp, bottom = if (isPlaying) 220.dp else 140.dp)
            ) {
                // Title block with the stage ring.
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Eyebrow("Passage ${ref.index} of $passageCount · verses ${ref.rangeLabel}", tm.accentColor)
                        Text(
                            text = title,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 34.sp,
                            lineHeight = 38.sp,
                            color = tm.primaryText
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PassageProgressRing(done = stages.doneCount, total = stages.total, size = 44.dp, lineWidth = 3.4.dp)
                        Text(
                            text = PassageHubStrings.ringLabel(stages.doneCount, stages.total),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = tm.tertiaryText
                        )
                    }
                }

                if (stages.isComplete) {
                    // The seal replaces the opening verse once every stage is done.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = null,
                            tint = tm.accentColor,
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(18.dp, spotColor = tm.accentColor.copy(alpha = 0.35f), shape = androidx.compose.foundation.shape.CircleShape)
                        )
                        Text(
                            text = PassageHubStrings.complete,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            color = tm.primaryText,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        val parts = buildList {
                            add("Read")
                            if (stages.hasCommentary) add("Understood")
                            best?.let { add("${it.score} of ${it.total} on the quiz") }
                        }
                        Text(
                            text = parts.joinToString(" · "),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = tm.secondaryText,
                            textAlign = TextAlign.Center
                        )
                        Hairline(modifier = Modifier.padding(top = 16.dp))
                    }
                } else if (firstVerse != null) {
                    // The opening verse, so the passage is recognisable before it is opened.
                    Column(
                        modifier = Modifier.padding(top = 18.dp),
                        verticalArrangement = Arrangement.spacedBy((10 * scale).dp)
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = firstVerse.arabicText,
                                fontFamily = AmiriFamily,
                                fontSize = (24 * scale).sp,
                                lineHeight = (24 * scale * 1.9f).sp,
                                color = tm.primaryText,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text(
                            text = firstVerse.translation,
                            fontFamily = CormorantFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16 * scale).sp,
                            lineHeight = (16 * scale * 1.35f).sp,
                            color = tm.secondaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Hairline(modifier = Modifier.padding(top = 12.dp))
                    }
                }

                // The stages on a path.
                Box(modifier = Modifier.padding(top = 22.dp)) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(start = 17.5.dp, top = 30.dp, bottom = 30.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxSize()
                                .background(tm.strokeColorStrong)
                        )
                    }
                    Column {
                        HubStageRow(
                            number = 1,
                            title = PassageHubStrings.read,
                            subtitle = readSubline,
                            state = stageState(stages, PassageStages.Stage.READ),
                            gated = false,
                            onTap = openReader
                        )
                        HubStageRow(
                            number = 2,
                            title = PassageHubStrings.understand,
                            subtitle = understandSubline,
                            state = if (stages.hasCommentary) stageState(stages, PassageStages.Stage.UNDERSTAND) else HubStageState.UNAVAILABLE,
                            gated = stages.hasCommentary && understandingGated,
                            onTap = openUnderstanding
                        )
                        if (stages.hasQuiz) {
                            HubStageRow(
                                number = 3,
                                title = PassageHubStrings.test,
                                subtitle = quizSubline,
                                state = stageState(stages, PassageStages.Stage.TEST),
                                gated = quizGated,
                                onTap = openQuiz
                            )
                        }
                    }
                }

                // A quiet way past this passage for readers who only want the verses.
                if (!stages.isComplete && nextRef != null) {
                    QuietLink(
                        text = PassageHubStrings.nextPassage,
                        color = tm.tertiaryText,
                        modifier = Modifier.padding(start = 50.dp, top = 8.dp),
                        onClick = { openNext() }
                    )
                }
            }
        }

        // Pinned next-step bar, above the player when it is up.
        val action = when {
            stages.isComplete && nextRef != null -> HubAction(
                title = PassageHubStrings.nextPassage,
                subtitle = "${PassageStore.title(nextRef)} · verses ${nextRef.rangeLabel}",
                gated = false,
                perform = { openNext() }
            )
            stages.isComplete -> HubAction(
                title = PassageHubStrings.backToSurah(surah.englishName),
                subtitle = PassageHubStrings.surahComplete,
                gated = false,
                quiet = true,
                perform = { navController.popBackStack() }
            )
            else -> when (stages.next) {
                PassageStages.Stage.READ -> HubAction(PassageHubStrings.startReading, readSubline, false, perform = openReader)
                PassageStages.Stage.UNDERSTAND -> HubAction(PassageHubStrings.understandPassage, understandSubline, understandingGated, perform = openUnderstanding)
                PassageStages.Stage.TEST -> HubAction(PassageHubStrings.test, quizSubline, quizGated, perform = openQuiz)
                null -> null
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            action?.let {
                HubActionBar(
                    action = it,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = if (isPlaying) 0.dp else 12.dp)
                )
            }
            AnimatedVisibility(
                visible = isPlaying,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SurahAudioPlayerBar()
            }
        }
    }
}

private fun stageState(stages: PassageStages, stage: PassageStages.Stage): HubStageState = when {
    stages.isDone(stage) -> HubStageState.DONE
    stages.next == stage -> HubStageState.NEXT
    else -> HubStageState.TODO
}
