package com.thaqalayn.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.data.QuizManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.SurahWithTafsir
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmNumeralCircle
import com.thaqalayn.app.ui.components.TextSizeButton
import com.thaqalayn.app.ui.components.TextSizePanelOverlay
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** The reading screen: surah header + verse cards (iOS SurahDetailView). */
@Composable
fun SurahDetailScreen(
    surahNumber: Int,
    targetVerse: Int?,
    navController: NavHostController
) {
    val colors = Theme.colors

    val surahWithTafsir by produceState<SurahWithTafsir?>(initialValue = null, key1 = surahNumber) {
        val surah = DataManager.shared.surah(surahNumber) ?: return@produceState
        value = DataManager.shared.loadSurahWithTafsir(surah)
    }

    var showTextSizePanel by remember { mutableStateOf(false) }
    var showGoToVerse by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val hasQuiz by produceState(initialValue = false, key1 = surahNumber) {
        value = QuizManager.hasQuiz(surahNumber)
    }

    // Deep-link scroll: verse cards start at list index 1 (header is item 0).
    LaunchedEffect(surahWithTafsir, targetVerse) {
        val target = targetVerse ?: return@LaunchedEffect
        if (surahWithTafsir != null) {
            listState.animateScrollToItem(target.coerceAtLeast(1))
        }
    }

    // Auto-scroll to the currently playing verse.
    val playingVerse = AudioManager.currentPlayback?.takeIf { it.surahNumber == surahNumber }?.verseNumber
    LaunchedEffect(playingVerse) {
        if (playingVerse != null && surahWithTafsir != null) {
            listState.animateScrollToItem(playingVerse.coerceAtLeast(1))
        }
    }

    TextSizePanelOverlay(
        isOpen = showTextSizePanel,
        onDismiss = { showTextSizePanel = false }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThemedBackground()
            val data = surahWithTafsir
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Pinned chrome: back + text size stay put while the surah scrolls
                // (same fixed-header layout as FullScreenCommentaryScreen).
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable(onClick = { navController.popBackStack() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    TextSizeButton(isOpen = showTextSizePanel) { showTextSizePanel = !showTextSizePanel }
                }
                if (data != null) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp
                        )
                    ) {
                        item(key = "header") {
                            SurahHeader(
                                surah = data.surah,
                                hasQuiz = hasQuiz,
                                onListen = { AudioManager.playVerseSequence(data.verses, data.surah) },
                                onGoToVerse = { showGoToVerse = true },
                                onQuiz = {
                                    if (PremiumManager.canAccessQuiz(data.surah.number)) {
                                        navController.navigate(Routes.quiz(data.surah.number))
                                    } else {
                                        navController.navigate(Routes.PAYWALL)
                                    }
                                }
                            )
                        }
                        itemsIndexed(data.verses, key = { _, v -> v.number }) { _, verse ->
                            VerseCard(
                                verse = verse,
                                surah = data.surah,
                                onGems = {
                                    if (!PremiumManager.canAccessOverview(data.surah.number)) {
                                        navController.navigate(Routes.PAYWALL)
                                    } else if (verse.tafsir != null) {
                                        navController.navigate("summary/${data.surah.number}/${verse.number}")
                                    }
                                },
                                onInDepth = {
                                    if (!PremiumManager.canAccessTafsir(data.surah.number)) {
                                        navController.navigate(Routes.PAYWALL)
                                    } else if (verse.tafsir != null) {
                                        navController.navigate("commentary/${data.surah.number}/${verse.number}")
                                    }
                                }
                            )
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
    }

    if (showGoToVerse) {
        GoToVerseDialogContent(
            versesCount = surahWithTafsir?.surah?.versesCount ?: 0,
            onDismiss = { showGoToVerse = false },
            onGo = { verseNumber ->
                showGoToVerse = false
                scope.launch { listState.animateScrollToItem(verseNumber.coerceAtLeast(1)) }
            }
        )
    }
}

@Composable
private fun SurahHeader(
    surah: Surah,
    hasQuiz: Boolean,
    onListen: () -> Unit,
    onGoToVerse: () -> Unit,
    onQuiz: () -> Unit
) {
    val colors = Theme.colors
    EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = surah.arabicName,
                fontFamily = AmiriFamily,
                fontSize = 38.sp,
                lineHeight = 56.sp,
                color = colors.accentBright,
                textAlign = TextAlign.Center
            )
            Text(
                text = surah.englishNameTranslation,
                fontFamily = CormorantFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                color = colors.secondaryText
            )
            EmDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${surah.versesCount} Verses · ${surah.revelationType}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText
                )
            }

            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen (gold CTA)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(12.dp, RoundedCornerShape(15.dp), spotColor = colors.accentColor.copy(alpha = 0.28f))
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.accentGradient)
                        .pressable(onClick = onListen)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(16.dp))
                    Text("Listen", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.onAccentText)
                }

                HeaderChip(icon = Icons.Filled.Search, onClick = onGoToVerse)
                if (hasQuiz) {
                    HeaderChip(icon = Icons.Filled.Psychology, onClick = onQuiz)
                }
                LanguageChip()
            }
        }
    }
}

@Composable
private fun HeaderChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(shape)
            .background(colors.accentChip)
            .border(1.dp, colors.strokeColor, shape)
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(18.dp))
    }
}

/** EN/UR/AR translation-language toggle chip. */
@Composable
private fun LanguageChip() {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .height(50.dp)
            .clip(shape)
            .background(colors.accentChip)
            .border(1.dp, colors.strokeColor, shape)
            .pressable { CommentaryLanguageManager.toggleLanguage() }
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(Icons.Filled.Language, contentDescription = "Translation language", tint = colors.accentColor, modifier = Modifier.size(15.dp))
        Text(
            text = CommentaryLanguageManager.selectedLanguage.shortCode,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accentColor
        )
    }
}

/** One verse card: number, actions, Arabic, translation, Gems / In-Depth (iOS ModernVerseCard). */
@Composable
private fun VerseCard(
    verse: VerseWithTafsir,
    surah: Surah,
    onGems: () -> Unit,
    onInDepth: () -> Unit
) {
    val colors = Theme.colors
    val scale = ReadingSettingsManager.scale
    val lang = CommentaryLanguageManager.selectedLanguage
    val shape = RoundedCornerShape(20.dp)

    val isBookmarked = BookmarkManager.isBookmarked(surah.number, verse.number)
    val isRead = ProgressManager.isVerseRead(surah.number, verse.number)
    val playback = AudioManager.currentPlayback
    val isCurrentlyPlaying = playback?.surahNumber == surah.number &&
        playback.verseNumber == verse.number &&
        AudioManager.playerState == AudioPlayerState.PLAYING

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                if (isCurrentlyPlaying) 16.dp else 10.dp,
                shape,
                spotColor = if (isCurrentlyPlaying) colors.accentColor.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.28f)
            )
            .clip(shape)
            .background(colors.glassSurface)
            .background(if (isCurrentlyPlaying) colors.accentColor.copy(alpha = 0.08f) else Color.Transparent)
            .border(
                if (isCurrentlyPlaying) 1.5.dp else 1.dp,
                if (isCurrentlyPlaying) colors.accentColor.copy(alpha = 0.6f) else colors.strokeColor,
                shape
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmNumeralCircle(n = verse.number, size = 34.dp)
            Spacer(modifier = Modifier.weight(1f))
            VerseChip(
                icon = if (isCurrentlyPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                active = isCurrentlyPlaying
            ) { AudioManager.playVerse(verse, surah) }
            VerseChip(
                icon = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                active = isBookmarked
            ) {
                BookmarkManager.toggleBookmark(
                    surah.number, verse.number, surah.englishName, verse.arabicText, verse.translation
                )
            }
            VerseChip(icon = Icons.Filled.Check, active = isRead) {
                if (isRead) ProgressManager.unmarkVerseAsRead(surah.number, verse.number)
                else ProgressManager.markVerseAsRead(surah.number, verse.number)
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = verse.arabicText,
                fontFamily = AmiriFamily,
                fontSize = (27 * scale).sp,
                lineHeight = (27 * scale * 1.9f).sp,
                color = colors.primaryText,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val showUrdu = verse.usesUrduTranslation(lang)
        CompositionLocalProvider(
            LocalLayoutDirection provides if (showUrdu) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Text(
                text = verse.displayTranslation(lang),
                fontFamily = if (showUrdu) AmiriFamily else CormorantFamily,
                fontWeight = if (showUrdu) FontWeight.Normal else FontWeight.Medium,
                fontSize = ((if (showUrdu) 19 else 17) * scale).sp,
                lineHeight = ((if (showUrdu) 19 * 1.6f else 17 * 1.35f) * scale).sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val hasTafsir = verse.tafsir != null
            // Gems (quick overview)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentChip)
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(12.dp))
                    .let { if (hasTafsir) it.pressable(onClick = onGems) else it }
                    .padding(vertical = 11.dp)
                    .let { if (hasTafsir) it else it },
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = colors.accentColor.copy(alpha = if (hasTafsir) 1f else 0.45f), modifier = Modifier.size(14.dp))
                Text(
                    "Gems",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentColor.copy(alpha = if (hasTafsir) 1f else 0.45f)
                )
            }
            // In-Depth (5-layer commentary)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .shadow(if (hasTafsir) 8.dp else 0.dp, RoundedCornerShape(12.dp), spotColor = colors.accentColor.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentGradient)
                    .let { if (hasTafsir) it.pressable(onClick = onInDepth) else it }
                    .padding(vertical = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.MenuBook, contentDescription = null, tint = colors.onAccentText.copy(alpha = if (hasTafsir) 1f else 0.45f), modifier = Modifier.size(14.dp))
                Text(
                    "In-Depth",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onAccentText.copy(alpha = if (hasTafsir) 1f else 0.45f)
                )
            }
        }
    }
}

@Composable
private fun VerseChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean = false,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(11.dp)
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(shape)
            .let {
                if (active) it.background(colors.accentGradient)
                else it.background(colors.accentChip).border(1.dp, colors.strokeColor, shape)
            }
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (active) colors.onAccentText else colors.accentColor,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun GoToVerseDialogContent(
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
            if (error != null) {
                Text(error!!, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.semanticRed)
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
