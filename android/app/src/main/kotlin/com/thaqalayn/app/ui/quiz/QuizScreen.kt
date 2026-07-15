package com.thaqalayn.app.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.QuizManager
import com.thaqalayn.app.model.QuizQuestion
import com.thaqalayn.app.model.QuizQuestionType
import com.thaqalayn.app.model.QuizResult
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.SurahQuiz
import com.thaqalayn.app.model.TafsirLayer
import com.thaqalayn.app.model.UnderstandingLevel
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.EmSectionLabel
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.reader.layerChip
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Surah quiz flow: intro -> questions with feedback -> results (iOS QuizView + QuizResultsView). */
@Composable
fun QuizScreen(surahNumber: Int, navController: NavHostController) {
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    val surah by produceState<Surah?>(initialValue = null, key1 = surahNumber) {
        value = DataManager.shared.surah(surahNumber)
    }
    val quiz by produceState<SurahQuiz?>(initialValue = null, key1 = surahNumber) {
        value = QuizManager.loadQuiz(surahNumber)
        loading = false
    }

    // -1 = intro screen (iOS QuizState convention).
    var currentIndex by remember { mutableIntStateOf(-1) }
    val answers = remember { mutableStateMapOf<String, String>() }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<QuizResult?>(null) }

    fun dismiss() {
        navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()

        val loadedQuiz = quiz
        val loadedSurah = surah
        when {
            loading -> LoadingView()
            loadedQuiz == null || loadedSurah == null ->
                QuizErrorView(onDismiss = ::dismiss)
            result != null -> QuizResultsContent(
                surah = loadedSurah,
                result = result!!,
                quiz = loadedQuiz,
                answers = answers,
                onRetry = {
                    currentIndex = -1
                    answers.clear()
                    selectedAnswer = null
                    showFeedback = false
                    result = null
                },
                onDismiss = ::dismiss
            )
            currentIndex == -1 -> IntroView(
                surah = loadedSurah,
                quiz = loadedQuiz,
                onBegin = { currentIndex = 0 },
                onDismiss = ::dismiss
            )
            else -> QuestionView(
                quiz = loadedQuiz,
                index = currentIndex,
                selectedAnswer = selectedAnswer,
                showFeedback = showFeedback,
                onSelect = { answer, question ->
                    if (!showFeedback) {
                        selectedAnswer = answer
                        answers[question.id] = answer
                        // Short beat before the explanation slides in (iOS 0.3s).
                        scope.launch {
                            delay(300)
                            showFeedback = true
                        }
                    }
                },
                onNext = {
                    if (currentIndex == loadedQuiz.questions.size - 1) {
                        val score = QuizManager.calculateScore(loadedQuiz, answers)
                        val newResult = QuizResult.create(
                            surahNumber = loadedSurah.number,
                            score = score,
                            totalQuestions = loadedQuiz.questions.size
                        )
                        QuizManager.saveResult(newResult)
                        result = newResult
                    } else {
                        currentIndex += 1
                        selectedAnswer = null
                        showFeedback = false
                    }
                },
                onDismiss = ::dismiss
            )
        }
    }
}

// MARK: - Loading / error

@Composable
private fun LoadingView() {
    val colors = Theme.colors
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Loading Quiz...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.secondaryText
        )
    }
}

@Composable
private fun QuizErrorView(onDismiss: () -> Unit) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Icon(
            Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = Color(0xFFF2A65A),
            modifier = Modifier.size(60.dp)
        )
        Text(
            text = "Quiz Unavailable",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
        Text(
            text = "No quiz available for this surah yet. Check back soon!",
            fontSize = 16.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )
        EmGoldCTA(title = "Go Back", onClick = onDismiss)
    }
}

// MARK: - Shared header chrome

@Composable
private fun QuizTopBar(title: String, onDismiss: () -> Unit, center: @Composable () -> Unit = {
    val colors = Theme.colors
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp,
        color = colors.accentColor
    )
}) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, colors.strokeColor, CircleShape)
                .pressable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                tint = colors.accentColor,
                modifier = Modifier.size(15.dp)
            )
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { center() }
        Spacer(modifier = Modifier.size(40.dp))
    }
}

// MARK: - Intro

@Composable
private fun IntroView(
    surah: Surah,
    quiz: SurahQuiz,
    onBegin: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        QuizTopBar(title = "TEST YOUR KNOWLEDGE", onDismiss = onDismiss)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            // Brain icon on a radial accent glow
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(colors.accentColor.copy(alpha = 0.18f), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(colors.accentChip)
                        .border(1.dp, colors.accentColor, RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Psychology,
                        contentDescription = null,
                        tint = colors.accentBright,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = surah.englishName,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 38.sp,
                    color = colors.primaryText,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = surah.arabicName,
                    fontFamily = AmiriFamily,
                    fontSize = 26.sp,
                    lineHeight = 40.sp,
                    color = colors.accentColor,
                    textAlign = TextAlign.Center
                )
            }

            // Questions | Minutes stats card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.glassSurface)
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(18.dp))
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IntroStat(
                    icon = { Icon(Icons.Filled.HelpOutline, null, tint = colors.accentColor, modifier = Modifier.size(18.dp)) },
                    value = "${quiz.questions.size}",
                    label = "Questions",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 44.dp)
                        .background(colors.strokeColor)
                )
                IntroStat(
                    icon = { Icon(Icons.Filled.Schedule, null, tint = colors.accentColor, modifier = Modifier.size(18.dp)) },
                    value = "~3",
                    label = "Minutes",
                    modifier = Modifier.weight(1f)
                )
            }

            val bestResult = QuizManager.bestResult(surah.number)
            if (bestResult != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "BEST SCORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = colors.tertiaryText
                    )
                    Text(
                        text = "${bestResult.score}/${bestResult.totalQuestions}",
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = colors.accentBright
                    )
                    Text(
                        text = bestResult.level.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.secondaryText
                    )
                }
            }
        }

        EmGoldCTA(
            title = "Begin Quiz",
            onClick = onBegin,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun IntroStat(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = value,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = colors.accentBright
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.tertiaryText
        )
    }
}

// MARK: - Question flow

@Composable
private fun QuestionView(
    quiz: SurahQuiz,
    index: Int,
    selectedAnswer: String?,
    showFeedback: Boolean,
    onSelect: (String, QuizQuestion) -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val question = quiz.questions[index]
    val isLast = index == quiz.questions.size - 1
    val progress by animateFloatAsState(
        targetValue = (index + 1) / quiz.questions.size.toFloat(),
        animationSpec = tween(300),
        label = "quizProgress"
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        QuizTopBar(title = "", onDismiss = onDismiss) {
            Text(
                text = "${index + 1} of ${quiz.questions.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = colors.accentColor
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.accentGradient)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))
            LayerBadge(layer = question.layer)

            Text(
                text = question.question,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (question.type == QuizQuestionType.TRUE_FALSE) {
                    AnswerButton("True", "true", question, selectedAnswer, showFeedback) { onSelect("true", question) }
                    AnswerButton("False", "false", question, selectedAnswer, showFeedback) { onSelect("false", question) }
                } else {
                    val letters = listOf("A", "B", "C", "D")
                    question.options?.forEachIndexed { i, option ->
                        val letter = letters.getOrNull(i) ?: return@forEachIndexed
                        AnswerButton(option, letter, question, selectedAnswer, showFeedback, letter) {
                            onSelect(letter, question)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showFeedback,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                FeedbackCard(question = question, selectedAnswer = selectedAnswer)
            }
        }

        if (showFeedback) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.strokeColor)
                )
                EmGoldCTA(
                    title = if (isLast) "See Results" else "Next Question",
                    onClick = onNext,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                )
            }
        }
    }
}

/** Which tafsir layer the question tests (iOS layerBadge). */
@Composable
private fun LayerBadge(layer: Int) {
    val tafsirLayer = TafsirLayer.entries.getOrNull(layer - 1)
    val (tint, iconRes, name) = if (tafsirLayer != null) {
        val chip = layerChip(tafsirLayer)
        Triple(chip.fg, chip.icon, quizLayerName(tafsirLayer))
    } else {
        Triple(Theme.colors.secondaryText, R.drawable.ph_book_open, "General")
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PhosphorIcon(resId = iconRes, size = 14.dp, tint = tint)
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint
        )
    }
}

private fun quizLayerName(layer: TafsirLayer): String = when (layer) {
    TafsirLayer.FOUNDATION -> "Foundation"
    TafsirLayer.CLASSICAL -> "Classical"
    TafsirLayer.CONTEMPORARY -> "Contemporary"
    TafsirLayer.AHLUL_BAYT -> "Ahlul Bayt"
    TafsirLayer.COMPARATIVE -> "Comparative"
}

@Composable
private fun AnswerButton(
    text: String,
    answer: String,
    question: QuizQuestion,
    selectedAnswer: String?,
    showFeedback: Boolean,
    letter: String? = null,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val isSelected = selectedAnswer == answer
    val isCorrect = answer.equals(question.correctAnswer, ignoreCase = true)

    val backgroundColor = when {
        showFeedback && isCorrect -> colors.semanticGreen.copy(alpha = 0.2f)
        showFeedback && isSelected -> colors.semanticRed.copy(alpha = 0.2f)
        isSelected -> colors.accentChip
        else -> colors.glassSurface
    }
    val borderColor = when {
        showFeedback && isCorrect -> colors.semanticGreen
        showFeedback && isSelected -> colors.semanticRed
        isSelected -> colors.accentColor
        else -> colors.strokeColor
    }
    val emphasized = isSelected || (showFeedback && isCorrect)
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(if (emphasized) 2.dp else 1.dp, borderColor, shape)
            .pressable { if (!showFeedback) onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (letter != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (emphasized) borderColor else colors.secondaryText
                )
            }
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.primaryText,
            modifier = Modifier.weight(1f)
        )
        if (showFeedback && isCorrect) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Correct", tint = colors.semanticGreen, modifier = Modifier.size(20.dp))
        } else if (showFeedback && isSelected) {
            Icon(Icons.Filled.Cancel, contentDescription = "Incorrect", tint = colors.semanticRed, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FeedbackCard(question: QuizQuestion, selectedAnswer: String?) {
    val colors = Theme.colors
    val isCorrect = selectedAnswer?.equals(question.correctAnswer, ignoreCase = true) == true
    val accent = if (isCorrect) colors.semanticGreen else colors.accentColor
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(accent.copy(alpha = 0.1f))
            .border(1.dp, accent.copy(alpha = 0.3f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Info,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (isCorrect) "Correct!" else "Explanation",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
        Text(
            text = question.explanation,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = colors.primaryText
        )
    }
}

// MARK: - Results

@Composable
private fun QuizResultsContent(
    surah: Surah,
    result: QuizResult,
    quiz: SurahQuiz,
    answers: Map<String, String>,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val levelColor = levelColor(result.level)
    val isGood = result.score.toDouble() / result.totalQuestions >= 0.6

    var showDetails by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        showDetails = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        QuizTopBar(title = "RESULTS", onDismiss = onDismiss)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    initialScale = 0.3f,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
                ) + fadeIn()
            ) {
                ResultCard(surah = surah, result = result, levelColor = levelColor)
            }

            AnimatedVisibility(
                visible = showDetails,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                BreakdownCard(quiz = quiz, answers = answers)
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmGoldCTA(title = "Try Again", icon = Icons.Filled.Refresh, onClick = onRetry)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(colors.glassSurface)
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(15.dp))
                    .pressable(onClick = onDismiss)
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Done",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText
                )
            }
        }
    }

    if (isGood) {
        ConfettiOverlay()
    }
}

@Composable
private fun ResultCard(surah: Surah, result: QuizResult, levelColor: Color) {
    val colors = Theme.colors
    EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(levelColor.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(levelColor.copy(alpha = 0.18f))
                        .border(2.5.dp, levelColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        levelIcon(result.level),
                        contentDescription = null,
                        tint = levelColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${result.score}/${result.totalQuestions}",
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 48.sp,
                    color = colors.accentBright
                )
                Text(
                    text = result.level.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
                Text(
                    text = result.level.arabicTitle,
                    fontFamily = AmiriFamily,
                    fontSize = 22.sp,
                    lineHeight = 34.sp,
                    color = colors.secondaryText
                )
            }

            Text(
                text = result.level.message,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = surah.englishName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText
                )
                Text(
                    text = surah.arabicName,
                    fontFamily = AmiriFamily,
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    color = colors.accentColor
                )
            }
        }
    }
}

@Composable
private fun BreakdownCard(quiz: SurahQuiz, answers: Map<String, String>) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmSectionLabel(icon = Icons.AutoMirrored.Filled.List, text = "Question Breakdown")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quiz.questions.forEachIndexed { index, question ->
                    val userAnswer = answers[question.id] ?: ""
                    val isCorrect = userAnswer.equals(question.correctAnswer, ignoreCase = true)
                    val tone = if (isCorrect) colors.semanticGreen else colors.semanticRed
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentChip.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(tone.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tone
                            )
                        }
                        Text(
                            text = question.question,
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            lineHeight = 19.sp,
                            color = colors.primaryText,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = tone,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// MARK: - Level helpers (iOS UnderstandingLevel.icon/color)

private fun levelIcon(level: UnderstandingLevel) = when (level) {
    UnderstandingLevel.HAFIZ -> Icons.Filled.WorkspacePremium
    UnderstandingLevel.SCHOLAR -> Icons.Filled.MenuBook
    UnderstandingLevel.STUDENT -> Icons.Filled.School
    UnderstandingLevel.SEEKER -> Icons.Filled.Search
    UnderstandingLevel.BEGINNER -> Icons.Filled.Eco
}

@Composable
private fun levelColor(level: UnderstandingLevel): Color {
    val colors = Theme.colors
    return when (level) {
        UnderstandingLevel.HAFIZ -> colors.semanticYellow
        UnderstandingLevel.SCHOLAR -> colors.semanticLilac
        UnderstandingLevel.STUDENT -> colors.semanticBlue
        UnderstandingLevel.SEEKER -> colors.semanticGreen
        UnderstandingLevel.BEGINNER -> Color.Gray
    }
}

// MARK: - Confetti (iOS QuizConfettiPiece)

private val confettiColors = listOf(
    Color(0xFFF47875), Color(0xFFF2A65A), Color(0xFFF2C969),
    Color(0xFF3E9B79), Color(0xFF6FA5E8), Color(0xFFB8A6D9), Color(0xFFED4799)
)

@Composable
private fun ConfettiOverlay() {
    val screenHeight = LocalConfiguration.current.screenHeightDp.toFloat()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        repeat(30) { index ->
            ConfettiPiece(
                delayMillis = index * 30L,
                screenHeight = screenHeight,
                seed = index
            )
        }
    }
}

@Composable
private fun ConfettiPiece(delayMillis: Long, screenHeight: Float, seed: Int) {
    val random = remember { Random(seed) }
    val color = remember { confettiColors[random.nextInt(confettiColors.size)] }
    val xStart = remember { random.nextInt(-150, 151).toFloat() }
    val endRotation = remember { random.nextInt(0, 361) * 3f }

    val yOffset = remember { Animatable(-100f) }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis)
        launch { yOffset.animateTo(screenHeight, tween(2000, easing = LinearEasing)) }
        progress.animateTo(1f, tween(2000, easing = LinearEasing))
    }

    Box(
        modifier = Modifier
            .offset(x = xStart.dp, y = yOffset.value.dp)
            .rotate(endRotation * progress.value)
            .alpha(1f - progress.value)
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}
