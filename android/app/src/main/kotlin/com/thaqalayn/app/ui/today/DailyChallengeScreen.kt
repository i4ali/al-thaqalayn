package com.thaqalayn.app.ui.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DailyChallengeManager
import com.thaqalayn.app.data.DailyChallengeProvider
import com.thaqalayn.app.model.DailyChallenge
import com.thaqalayn.app.model.DailyChallengeFormat
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.strings.DailyChallengeStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Interaction screen for the Daily Challenge - 4 formats (iOS DailyChallengeView). */
@Composable
fun DailyChallengeScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val challenge = DailyChallengeProvider.today ?: return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var revealed by remember { mutableStateOf(false) }
    var flipped by remember { mutableStateOf(false) }
    var flashcardGotIt by remember { mutableStateOf<Boolean?>(null) }
    var showCompletion by remember { mutableStateOf(false) }

    fun triggerCompletion() {
        val wasCorrect = when (challenge.format) {
            DailyChallengeFormat.trueFalse -> selectedIndex == (if (challenge.trueFalseAnswer == true) 1 else 0)
            else -> selectedIndex == challenge.correctIndex
        }
        DailyChallengeManager.complete(challenge, wasCorrect)
        showCompletion = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()

        if (showCompletion) {
            CompletionLayer(lang = lang) { navController.popBackStack() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header: eyebrow + topic + close
                Row(verticalAlignment = Alignment.Top) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = DailyChallengeStrings.dailyChallenge(lang).uppercase(),
                                fontSize = if (lang.isRTL) 13.sp else 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (lang.isRTL) 0.sp else 1.5.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = challenge.topic.replaceFirstChar { it.uppercase() },
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                color = colors.primaryText
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.accentColor, modifier = Modifier.size(15.dp))
                    }
                }

                // Format body
                when (challenge.format) {
                    DailyChallengeFormat.multipleChoice, DailyChallengeFormat.fillInBlank -> {
                        PromptCard(challenge.prompt.text(lang), scale, lang)
                        if (challenge.format == DailyChallengeFormat.fillInBlank && challenge.arabicText != null) {
                            ArabicCard(challenge.arabicText, scale)
                        }
                        challenge.source?.let { SourceLine(it, lang) }
                        challenge.options?.forEachIndexed { index, option ->
                            OptionRow(
                                text = option.text(lang),
                                index = index,
                                selectedIndex = selectedIndex,
                                correctIndex = challenge.correctIndex,
                                revealed = revealed,
                                lang = lang,
                                scale = scale
                            ) {
                                if (!revealed) {
                                    selectedIndex = index
                                    revealed = true
                                }
                            }
                        }
                    }
                    DailyChallengeFormat.trueFalse -> {
                        PromptCard(challenge.prompt.text(lang), scale, lang)
                        challenge.source?.let { SourceLine(it, lang) }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TrueFalseButton(
                                label = DailyChallengeStrings.trueLabel(lang),
                                answer = true,
                                challenge = challenge,
                                selectedIndex = selectedIndex,
                                revealed = revealed,
                                modifier = Modifier.weight(1f)
                            ) { selectedIndex = 1; revealed = true }
                            TrueFalseButton(
                                label = DailyChallengeStrings.falseLabel(lang),
                                answer = false,
                                challenge = challenge,
                                selectedIndex = selectedIndex,
                                revealed = revealed,
                                modifier = Modifier.weight(1f)
                            ) { selectedIndex = 0; revealed = true }
                        }
                    }
                    DailyChallengeFormat.flashcard -> {
                        FlashcardBody(
                            challenge = challenge,
                            flipped = flipped,
                            lang = lang,
                            scale = scale,
                            onFlip = { flipped = true }
                        )
                        if (flipped && flashcardGotIt == null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FlashcardGradeButton(
                                    label = DailyChallengeStrings.reviewAgain(lang),
                                    icon = Icons.Filled.Refresh,
                                    gotIt = false,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    flashcardGotIt = false
                                    DailyChallengeManager.completeFlashcard(challenge, false)
                                    showCompletion = true
                                }
                                FlashcardGradeButton(
                                    label = DailyChallengeStrings.gotIt(lang),
                                    icon = Icons.Filled.ThumbUp,
                                    gotIt = true,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    flashcardGotIt = true
                                    DailyChallengeManager.completeFlashcard(challenge, true)
                                    showCompletion = true
                                }
                            }
                        }
                        challenge.source?.let { SourceLine(it, lang) }
                    }
                }

                // Reveal section (explanation after answering)
                if (revealed && challenge.format != DailyChallengeFormat.flashcard) {
                    val wasCorrect = when (challenge.format) {
                        DailyChallengeFormat.trueFalse -> selectedIndex == (if (challenge.trueFalseAnswer == true) 1 else 0)
                        else -> selectedIndex == challenge.correctIndex
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (wasCorrect) Icons.Filled.CheckCircle else Icons.Filled.Info,
                            contentDescription = null,
                            tint = if (wasCorrect) colors.semanticGreen else colors.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (wasCorrect) DailyChallengeStrings.correct(lang) else DailyChallengeStrings.notQuite(lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (wasCorrect) colors.semanticGreen else colors.accentColor
                        )
                    }
                    challenge.explanation?.let { explanation ->
                        EmCard(modifier = Modifier.fillMaxWidth()) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                            ) {
                                Text(
                                    text = explanation.text(lang),
                                    fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (16 * scale).sp,
                                    lineHeight = (16 * scale * 1.5f).sp,
                                    color = colors.primaryText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                )
                            }
                        }
                    }
                    if (challenge.format != DailyChallengeFormat.fillInBlank && challenge.arabicText != null) {
                        ArabicCard(challenge.arabicText, scale)
                    }
                    EmGoldCTA(title = DailyChallengeStrings.doneButton(lang)) { triggerCompletion() }
                }
            }
        }
    }
}

@Composable
private fun PromptCard(text: String, scale: Float, lang: com.thaqalayn.app.model.CommentaryLanguage) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Text(
                text = text,
                fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (18 * scale).sp,
                lineHeight = (18 * scale * 1.45f).sp,
                color = colors.primaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun ArabicCard(arabic: String, scale: Float) {
    val colors = Theme.colors
    EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = arabic,
                fontFamily = AmiriFamily,
                fontSize = (28 * scale).sp,
                lineHeight = (28 * scale * 1.7f).sp,
                color = colors.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun SourceLine(source: String, lang: com.thaqalayn.app.model.CommentaryLanguage) {
    Text(
        text = source,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Theme.colors.tertiaryText,
        textAlign = if (lang.isRTL) TextAlign.End else TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun OptionRow(
    text: String,
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int?,
    revealed: Boolean,
    lang: com.thaqalayn.app.model.CommentaryLanguage,
    scale: Float,
    onSelect: () -> Unit
) {
    val colors = Theme.colors
    val green = colors.semanticGreen
    val red = colors.semanticRed

    val isCorrect = revealed && index == correctIndex
    val isWrong = revealed && index == selectedIndex && index != correctIndex

    val rowFill = when {
        isCorrect -> green.copy(alpha = if (index == selectedIndex) 0.16f else 0.12f)
        isWrong -> red.copy(alpha = 0.10f)
        else -> if (colors.isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.7f)
    }
    val rowBorder = when {
        isCorrect -> green.copy(alpha = if (index == selectedIndex) 0.70f else 0.55f)
        isWrong -> red.copy(alpha = 0.45f)
        else -> colors.strokeColor
    }
    val bubbleFill = when {
        isCorrect -> green.copy(alpha = 0.25f)
        isWrong -> red.copy(alpha = 0.20f)
        else -> colors.accentChip
    }
    val bubbleText = when {
        isCorrect -> green
        isWrong -> red
        else -> colors.accentColor
    }

    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(rowFill)
            .border(1.5.dp, rowBorder, shape)
            .let { if (revealed) it else it.pressableGentle(onClick = onSelect) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(bubbleFill),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ('A' + index).toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = bubbleText
            )
        }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Text(
                text = text,
                fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (16 * scale).sp,
                lineHeight = (16 * scale * 1.4f).sp,
                color = if (isWrong) colors.secondaryText else colors.primaryText,
                modifier = Modifier.weight(1f)
            )
        }
        if (revealed && (isCorrect || isWrong)) {
            Icon(
                if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (isCorrect) green else red,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TrueFalseButton(
    label: String,
    answer: Boolean,
    challenge: DailyChallenge,
    selectedIndex: Int?,
    revealed: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val colors = Theme.colors
    val green = colors.semanticGreen
    val red = colors.semanticRed

    val chosen = selectedIndex == (if (answer) 1 else 0)
    val correct = challenge.trueFalseAnswer == answer
    val isCorrectResult = revealed && correct
    val isWrongResult = revealed && chosen && !correct

    val iconColor = when {
        isCorrectResult -> green
        isWrongResult -> red
        revealed -> colors.tertiaryText
        answer -> green
        else -> red
    }
    val labelColor = when {
        isCorrectResult -> green
        isWrongResult -> red
        revealed -> colors.tertiaryText
        else -> colors.primaryText
    }
    val bgFill = when {
        isCorrectResult -> green.copy(alpha = 0.12f)
        isWrongResult -> red.copy(alpha = 0.10f)
        revealed -> Color.Transparent
        else -> if (colors.isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.7f)
    }
    val borderColor = when {
        isCorrectResult -> green.copy(alpha = 0.55f)
        isWrongResult -> red.copy(alpha = 0.45f)
        else -> colors.strokeColor
    }

    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(bgFill)
            .border(1.5.dp, borderColor, shape)
            .let { if (revealed) it else it.pressableGentle(onClick = onSelect) }
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            if (answer) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Text(text = label, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = labelColor)
    }
}

@Composable
private fun FlashcardBody(
    challenge: DailyChallenge,
    flipped: Boolean,
    lang: com.thaqalayn.app.model.CommentaryLanguage,
    scale: Float,
    onFlip: () -> Unit
) {
    val colors = Theme.colors
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 200f),
        label = "flip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .let { if (flipped) it else it.pressable(depth = 0.97f, onClick = onFlip) }
    ) {
        if (rotation <= 90f) {
            // Front: prompt + optional arabic + flip hint
            EmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    challenge.arabicText?.let { arabic ->
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = arabic,
                                fontFamily = AmiriFamily,
                                fontSize = (28 * scale).sp,
                                lineHeight = (28 * scale * 1.7f).sp,
                                color = colors.primaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    ) {
                        Text(
                            text = challenge.prompt.text(lang),
                            fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (17 * scale).sp,
                            lineHeight = (17 * scale * 1.45f).sp,
                            color = colors.primaryText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(
                        text = DailyChallengeStrings.flipCard(lang).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang.isRTL) 0.sp else 1.2.sp,
                        color = colors.accentColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Back: answer + explanation (mirror-flipped so it reads correctly)
            EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                        .graphicsLayer { rotationY = 180f }
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            challenge.answer?.let { answer ->
                                Text(
                                    text = answer.text(lang),
                                    fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = (18 * scale).sp,
                                    lineHeight = (18 * scale * 1.4f).sp,
                                    color = colors.accentBright,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            challenge.explanation?.let { explanation ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .background(colors.strokeColor)
                                        .size(1.dp)
                                )
                                Text(
                                    text = explanation.text(lang),
                                    fontFamily = if (lang.isRTL) AmiriFamily else CormorantFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (15 * scale).sp,
                                    lineHeight = (15 * scale * 1.45f).sp,
                                    color = colors.secondaryText,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashcardGradeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gotIt: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .let {
                if (gotIt) it.background(colors.accentGradient)
                else it.background(colors.accentChip).border(1.dp, colors.strokeColor, shape)
            }
            .pressable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (gotIt) colors.onAccentText else colors.accentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (gotIt) colors.onAccentText else colors.accentColor
        )
    }
}

@Composable
private fun CompletionLayer(lang: com.thaqalayn.app.model.CommentaryLanguage, onDone: () -> Unit) {
    val colors = Theme.colors
    AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn(initialScale = 0.95f) + slideInVertically()) {}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(colors.accentChip),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = colors.accentBright, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.size(14.dp))
        Text(
            text = DailyChallengeStrings.completionTitle(lang),
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            color = colors.primaryText
        )
        Spacer(modifier = Modifier.size(28.dp))
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.accentChip)
                .border(1.dp, colors.strokeColor, CircleShape)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🔥", fontSize = 14.sp)
            Text(
                text = DailyChallengeStrings.streakLabel(DailyChallengeManager.streak.currentStreak, lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.secondaryText
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        EmGoldCTA(title = DailyChallengeStrings.doneForToday(lang), icon = Icons.Filled.Check) { onDone() }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
