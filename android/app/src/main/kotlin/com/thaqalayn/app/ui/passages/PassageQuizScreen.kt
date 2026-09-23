package com.thaqalayn.app.ui.passages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.PassageStore
import com.thaqalayn.app.data.QuizResultsStore
import com.thaqalayn.app.data.QuizStore
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.PassageQuiz
import com.thaqalayn.app.model.PassageQuizResult
import com.thaqalayn.app.model.PassageRef
import com.thaqalayn.app.model.QuizAnchor
import com.thaqalayn.app.model.QuizQuestion
import com.thaqalayn.app.model.QuizQuestionType
import com.thaqalayn.app.model.availableLanguages
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.TextSizeButton
import com.thaqalayn.app.ui.components.TextSizePanelOverlay
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * Five questions on one passage, answered one tap at a time, then results (iOS
 * QuizView). Tapping an option answers it at once; the explanation and a link
 * into the passage appear beneath, then Next question. The best score is kept
 * locally in QuizResultsStore.
 */
@Composable
fun PassageQuizScreen(
    surahNumber: Int,
    passageIndex: Int,
    navController: NavHostController
) {
    LaunchedEffect(surahNumber) {
        PassageStore.load(surahNumber)
        QuizStore.load(surahNumber)
    }
    val ref = DataManager.shared.passageIndex?.passage(surahNumber, passageIndex)
    val quiz = ref?.let { QuizStore.quiz(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        if (ref != null && quiz != null && quiz.questions.isNotEmpty()) {
            QuizContent(ref, quiz, navController)
        }
    }
}

@Composable
private fun QuizContent(ref: PassageRef, quiz: PassageQuiz, navController: NavHostController) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val haptics = LocalHapticFeedback.current

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var answers by rememberSaveable { mutableStateOf(List<Int?>(quiz.questions.size) { null }) }
    var showingResults by rememberSaveable { mutableStateOf(false) }
    var showTextSizePanel by remember { mutableStateOf(false) }

    val passage = PassageStore.passage(ref.surah, ref.index)
    val surahName = DataManager.shared.quranDataOrNull?.surahs?.firstOrNull { it.number == ref.surah }?.englishName
        ?: "Surah ${ref.surah}"
    val eyebrow = "$surahName · ${PassageStore.title(ref)}"
    val score = quiz.questions.indices.count { answers[it] == quiz.questions[it].answer }

    // The reader's language when this passage carries it, else English; the same rule
    // Understanding applies, so the anchor resolves against the text it will show.
    val understandingLanguage = passage?.let {
        val selected = CommentaryLanguageManager.selectedLanguage
        if (selected in it.essay.availableLanguages) selected else CommentaryLanguage.ENGLISH
    } ?: CommentaryLanguage.ENGLISH
    val openUnderstanding: (QuizAnchor) -> Unit = { anchor ->
        if (passage != null) {
            val target = understandingScrollId(anchor, passage, understandingLanguage)
            navController.navigate(Routes.understanding(ref.surah, ref.index, target))
        }
    }

    AnimatedContent(
        targetState = showingResults,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        label = "quizResults"
    ) { results ->
        if (results) {
            QuizResults(
                eyebrow = eyebrow,
                quiz = quiz,
                answers = answers,
                onReadAnchor = openUnderstanding,
                onTryAgain = {
                    answers = List(quiz.questions.size) { null }
                    currentIndex = 0
                    showingResults = false
                },
                onBack = { navController.popBackStack() }
            )
        } else {
            val question = quiz.questions[currentIndex]
            val picked = answers[currentIndex]
            val isLast = currentIndex == quiz.questions.lastIndex
            TextSizePanelOverlay(isOpen = showTextSizePanel, onDismiss = { showTextSizePanel = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PassageBackButton { navController.popBackStack() }
                        Spacer(modifier = Modifier.weight(1f))
                        TextSizeButton(isOpen = showTextSizePanel) { showTextSizePanel = !showTextSizePanel }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 40.dp)
                    ) {
                        Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Eyebrow(eyebrow, tm.accentColor)
                            Text(
                                text = QuizStrings.title,
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 30.sp,
                                color = tm.primaryText
                            )
                            Text(
                                text = QuizStrings.question(currentIndex + 1, quiz.questions.size),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = tm.secondaryText
                            )
                        }

                        // Progress capsules.
                        Row(
                            modifier = Modifier.padding(top = 14.dp, bottom = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quiz.questions.indices.forEach { i ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                i < currentIndex -> tm.accentColor
                                                i == currentIndex -> tm.accentBright
                                                else -> tm.strokeColor
                                            }
                                        )
                                )
                            }
                        }

                        EmCard(elevated = true, cornerRadius = 20.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Eyebrow(question.type.label, tm.accentColor)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = QuizStrings.verse(question.verse), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = tm.tertiaryText)
                                }
                                QuestionPrompt(question, scale)
                            }
                        }

                        // Options: two tiles for true or false, a 2x2 grid for fill the gap, rows otherwise.
                        val onPick: (Int) -> Unit = { option ->
                            if (answers[currentIndex] == null) {
                                answers = answers.toMutableList().also { it[currentIndex] = option }
                                haptics.performHapticFeedback(
                                    if (option == question.answer) HapticFeedbackType.Confirm else HapticFeedbackType.Reject
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            when (question.type) {
                                QuizQuestionType.trueFalse -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    question.options.forEachIndexed { i, opt ->
                                        QuizOptionRow(opt.en, optionState(i, picked, question), centered = true, modifier = Modifier.weight(1f)) { onPick(i) }
                                    }
                                }
                                QuizQuestionType.fillGap -> question.options.indices.chunked(2).forEach { pair ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        pair.forEach { i ->
                                            QuizOptionRow(question.options[i].en, optionState(i, picked, question), centered = true, modifier = Modifier.weight(1f)) { onPick(i) }
                                        }
                                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                else -> question.options.forEachIndexed { i, opt ->
                                    QuizOptionRow(opt.en, optionState(i, picked, question)) { onPick(i) }
                                }
                            }
                        }

                        if (picked != null) {
                            Column(modifier = Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Eyebrow(QuizStrings.why, if (picked == question.answer) tm.semanticGreen else tm.semanticRed)
                                Text(
                                    text = question.explanation.en,
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (16 * scale).sp,
                                    lineHeight = (16 * scale * 1.35f).sp,
                                    color = tm.primaryText
                                )
                            }
                            // Read this in the passage.
                            Row(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .fillMaxWidth()
                                    .heightIn(min = 44.dp)
                                    .alpha(if (passage == null) 0.45f else 1f)
                                    .let { if (passage != null) it.pressableGentle { openUnderstanding(question.anchor) } else it },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = tm.accentColor, modifier = Modifier.size(18.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = QuizStrings.readInPassage, fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = tm.primaryText)
                                    Text(text = QuizStrings.anchorLabel(question.anchor.location), fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = tm.secondaryText)
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = tm.accentColor, modifier = Modifier.size(20.dp))
                            }
                            GoldButton(
                                text = if (isLast) QuizStrings.seeResults else QuizStrings.next,
                                modifier = Modifier.padding(top = 20.dp)
                            ) {
                                if (isLast) {
                                    QuizResultsStore.record(
                                        PassageQuizResult(
                                            surah = ref.surah,
                                            index = ref.index,
                                            score = score,
                                            total = quiz.questions.size,
                                            completedAt = System.currentTimeMillis()
                                        )
                                    )
                                    showingResults = true
                                } else {
                                    currentIndex += 1
                                }
                            }
                        } else {
                            Text(
                                text = QuizStrings.hint,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = tm.tertiaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The fill-the-gap prompt shows its blank as an underlined space; other types show the prompt as is. */
@Composable
private fun QuestionPrompt(question: QuizQuestion, scale: Float) {
    val tm = Theme.colors
    val prompt = question.prompt.en
    val gap = prompt.indexOf("____")
    val text = if (question.type == QuizQuestionType.fillGap && gap >= 0) {
        buildAnnotatedString {
            append(prompt.substring(0, gap))
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = tm.accentColor)) { append("    ") }
            append(prompt.substring(gap + 4))
        }
    } else {
        buildAnnotatedString { append(prompt) }
    }
    Text(
        text = text,
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (20 * scale).sp,
        lineHeight = (20 * scale * 1.3f).sp,
        color = tm.primaryText
    )
}

private enum class OptionState { IDLE, CORRECT, WRONG, DIMMED }

private fun optionState(option: Int, picked: Int?, question: QuizQuestion): OptionState = when {
    picked == null -> OptionState.IDLE
    option == question.answer -> OptionState.CORRECT
    option == picked -> OptionState.WRONG
    else -> OptionState.DIMMED
}

/**
 * One answer option, for every framing (iOS QuizOptionRow): an empty ring before
 * answering, a green seal on the right answer, a red ring with a cross on a wrong
 * pick, dimmed for the rest.
 */
@Composable
private fun QuizOptionRow(
    text: String,
    state: OptionState,
    centered: Boolean = false,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val shape = RoundedCornerShape(14.dp)
    val fill = when (state) {
        OptionState.CORRECT -> tm.semanticGreen.copy(alpha = 0.16f)
        OptionState.WRONG -> tm.semanticRed.copy(alpha = 0.14f)
        else -> tm.glassSurface
    }
    val border = when (state) {
        OptionState.CORRECT -> tm.semanticGreen.copy(alpha = 0.55f)
        OptionState.WRONG -> tm.semanticRed.copy(alpha = 0.55f)
        else -> tm.strokeColor
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .alpha(if (state == OptionState.DIMMED) 0.45f else 1f)
            .let { if (state == OptionState.IDLE) it.pressableGentle(onClick = onTap) else it }
            .clip(shape)
            .background(fill)
            .border(1.dp, border, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centered) Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally) else Arrangement.spacedBy(12.dp)
    ) {
        when (state) {
            OptionState.CORRECT -> Icon(Icons.Filled.Verified, contentDescription = null, tint = tm.semanticGreen, modifier = Modifier.size(22.dp))
            OptionState.WRONG -> Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(1.5.dp, tm.semanticRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = tm.semanticRed, modifier = Modifier.size(12.dp))
            }
            else -> Box(modifier = Modifier.size(22.dp).border(1.5.dp, tm.strokeColor, CircleShape))
        }
        Text(
            text = text,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (17 * scale).sp,
            lineHeight = (17 * scale * 1.3f).sp,
            color = tm.primaryText,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            modifier = if (centered) Modifier else Modifier.weight(1f)
        )
    }
}

/**
 * The end of a passage quiz (iOS QuizResultsView): the score, five seals, the missed
 * questions with their right answer and a link into the passage, then Try again and
 * Back to passage.
 */
@Composable
private fun QuizResults(
    eyebrow: String,
    quiz: PassageQuiz,
    answers: List<Int?>,
    onReadAnchor: (QuizAnchor) -> Unit,
    onTryAgain: () -> Unit,
    onBack: () -> Unit
) {
    val tm = Theme.colors
    val scale = ReadingSettingsManager.scale
    val haptics = LocalHapticFeedback.current
    val total = quiz.questions.size
    val score = quiz.questions.indices.count { answers[it] == quiz.questions[it].answer }
    val missed = quiz.questions.filterIndexed { i, q -> answers[i] != q.answer }

    LaunchedEffect(Unit) { haptics.performHapticFeedback(HapticFeedbackType.Confirm) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp)) {
            PassageBackButton(contentDescription = "Back to passage", onClick = onBack)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Eyebrow(eyebrow, tm.accentColor)
            Text(
                text = QuizStrings.score(score, total),
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 64.sp,
                color = tm.primaryText,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = QuizStrings.verdict(score, total),
                fontFamily = CormorantFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                color = tm.secondaryText,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Seals.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quiz.questions.forEachIndexed { i, q ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (answers[i] == q.answer) {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = tm.semanticGreen, modifier = Modifier.size(34.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .border(1.5.dp, tm.semanticRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = tm.semanticRed, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = "Q${i + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = tm.tertiaryText)
                    }
                }
            }

            if (missed.isNotEmpty()) {
                Eyebrow(QuizStrings.missed, tm.accentColor, modifier = Modifier.padding(top = 30.dp))
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (q in missed) {
                        EmCard(elevated = true, cornerRadius = 20.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Eyebrow("${q.id} · ${q.type.label}", tm.tertiaryText, tracking = 1.5.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = QuizStrings.verse(q.verse), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = tm.tertiaryText)
                                }
                                Text(
                                    text = q.prompt.en,
                                    fontFamily = CormorantFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (17 * scale).sp,
                                    lineHeight = (17 * scale * 1.3f).sp,
                                    color = tm.primaryText
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Filled.Verified, contentDescription = null, tint = tm.semanticGreen, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = q.options.getOrNull(q.answer)?.en ?: "",
                                        fontFamily = CormorantFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = (17 * scale).sp,
                                        lineHeight = (17 * scale * 1.3f).sp,
                                        color = tm.primaryText
                                    )
                                }
                                QuietLink(text = QuizStrings.readInPassage, color = tm.accentColor) { onReadAnchor(q.anchor) }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .pressable(onClick = onTryAgain)
                        .clip(RoundedCornerShape(15.dp))
                        .background(tm.glassSurfaceElevated)
                        .border(1.dp, tm.strokeColor, RoundedCornerShape(15.dp))
                        .padding(vertical = 17.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = tm.accentColor, modifier = Modifier.size(17.dp))
                    Text(text = QuizStrings.tryAgain, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp, color = tm.accentColor)
                }
                GoldButton(text = QuizStrings.backToPassage, showArrow = false, modifier = Modifier.weight(1f), onClick = onBack)
            }
        }
    }
}

@Composable
private fun GoldButton(text: String, modifier: Modifier = Modifier, showArrow: Boolean = true, onClick: () -> Unit) {
    val tm = Theme.colors
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressable(onClick = onClick)
            .shadow(14.dp, shape, spotColor = tm.accentColor.copy(alpha = 0.28f))
            .clip(shape)
            .background(tm.accentGradient)
            .padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp, color = tm.onAccentText)
        if (showArrow) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = tm.onAccentText, modifier = Modifier.size(17.dp))
        }
    }
}
