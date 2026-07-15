package com.thaqalayn.app.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DailyCrosswordManager
import com.thaqalayn.app.data.DailyCrosswordProvider
import com.thaqalayn.app.model.CellPos
import com.thaqalayn.app.model.CrosswordEntry
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.DailyCrosswordStrings
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme
import kotlinx.coroutines.delay

/** Full-screen play view for the Daily Crossword (iOS DailyCrosswordView). */
@Composable
fun DailyCrosswordScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val puzzle = DailyCrosswordProvider.today ?: return

    val entered = remember { mutableStateMapOf<CellPos, Char>() }
    val firstEntry = remember(puzzle) { puzzle.entries.firstOrNull { it.isAcross } ?: puzzle.entries.first() }
    var selected by remember { mutableStateOf(firstEntry.cellAt(0)) }
    var acrossMode by remember { mutableStateOf(firstEntry.isAcross) }
    var seconds by remember { mutableIntStateOf(0) }
    var usedHint by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    var showSolved by remember { mutableStateOf(false) }

    val letterCells = remember(puzzle) {
        buildSet {
            for (e in puzzle.entries) {
                for (i in e.answer.indices) add(e.cellAt(i))
            }
        }
    }
    val solution = remember(puzzle) { puzzle.solution }
    val orderedEntries = remember(puzzle) {
        puzzle.entries.filter { it.isAcross }.sortedBy { it.num } +
            puzzle.entries.filter { !it.isAcross }.sortedBy { it.num }
    }

    fun entriesContaining(p: CellPos): List<CrosswordEntry> =
        puzzle.entries.filter { e -> e.answer.indices.any { e.cellAt(it) == p } }

    val activeEntry: CrosswordEntry? = run {
        val here = entriesContaining(selected)
        here.firstOrNull { it.isAcross == acrossMode } ?: here.firstOrNull()
    }
    val activeCells: Set<CellPos> =
        activeEntry?.let { e -> e.answer.indices.map { e.cellAt(it) }.toSet() } ?: emptySet()

    fun checkSolved() {
        val nowSolved = letterCells.all { entered[it] == solution[it] }
        if (nowSolved && !solved) {
            solved = true
            DailyCrosswordManager.complete(puzzle.id, seconds, usedHint)
            showSolved = true
        } else {
            solved = nowSolved
        }
    }

    fun typeLetter(ch: Char) {
        val e = activeEntry ?: return
        entered[selected] = ch
        val cells = e.answer.indices.map { e.cellAt(it) }
        val idx = cells.indexOf(selected)
        if (idx >= 0 && idx + 1 < cells.size) selected = cells[idx + 1]
        checkSolved()
    }

    fun backspace() {
        val e = activeEntry
        if (e == null) {
            entered.remove(selected)
            return
        }
        val cells = e.answer.indices.map { e.cellAt(it) }
        if (entered[selected] == null) {
            val idx = cells.indexOf(selected)
            if (idx > 0) {
                val prev = cells[idx - 1]
                selected = prev
                entered.remove(prev)
            }
        } else {
            entered.remove(selected)
        }
    }

    fun step(delta: Int) {
        if (orderedEntries.isEmpty()) return
        val currentIndex = activeEntry?.let { e -> orderedEntries.indexOfFirst { it.id == e.id } } ?: 0
        val nextIndex = ((currentIndex + delta) % orderedEntries.size + orderedEntries.size) % orderedEntries.size
        val next = orderedEntries[nextIndex]
        acrossMode = next.isAcross
        val cells = next.answer.indices.map { next.cellAt(it) }
        selected = cells.firstOrNull { entered[it] == null } ?: cells.first()
    }

    fun tapCell(p: CellPos) {
        val here = entriesContaining(p)
        if (here.isEmpty()) return
        if (p == selected && here.size > 1) {
            acrossMode = !acrossMode
        } else {
            selected = p
            if (here.none { it.isAcross == acrossMode }) {
                acrossMode = here.first().isAcross
            }
        }
    }

    // Timer, paused once solved.
    LaunchedEffect(solved) {
        while (!solved) {
            delay(1000)
            seconds += 1
        }
    }

    fun timeString(s: Int) = "%02d:%02d".format(s / 60, s % 60)

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()

        if (showSolved) {
            // Solved overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .shadow(20.dp, CircleShape, spotColor = colors.accentColor.copy(alpha = 0.35f))
                        .clip(CircleShape)
                        .background(colors.accentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.size(26.dp))
                Text(
                    text = DailyCrosswordStrings.solved(lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 40.sp,
                    color = colors.accentBright
                )
                Text(
                    text = "${puzzle.cols}×${puzzle.rows} · ${puzzle.entries.size} ${DailyCrosswordStrings.words(lang)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.secondaryText
                )
                Spacer(modifier = Modifier.size(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatPill(icon = Icons.Filled.AccessTime, text = timeString(DailyCrosswordManager.lastCompletion?.seconds ?: seconds))
                    StatPill(text = "🔥 ${DailyCrosswordStrings.streakLabel(DailyCrosswordManager.streak.currentStreak, lang)}")
                }
                Spacer(modifier = Modifier.weight(1f))
                EmGoldCTA(title = DailyCrosswordStrings.done(lang), icon = Icons.Filled.Check) {
                    navController.popBackStack()
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = DailyCrosswordStrings.comeBackTomorrow(lang),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = DailyCrosswordStrings.dailyCrossword(lang),
                            fontFamily = CormorantFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = colors.primaryText,
                            maxLines = 1
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "🔥 ${DailyCrosswordManager.streak.currentStreak}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentColor
                            )
                            Text(
                                text = timeString(seconds),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.secondaryText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, RoundedCornerShape(12.dp))
                            .pressable {
                                solution[selected]?.let { sol ->
                                    entered[selected] = sol
                                    usedHint = true
                                    checkSolved()
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(16.dp))
                        Text(
                            text = DailyCrosswordStrings.hint(lang),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = colors.accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Grid
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    val spacing = 6.dp
                    val cell = (maxWidth - spacing * (puzzle.cols - 1)) / puzzle.cols
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        for (r in 0 until puzzle.rows) {
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                                for (c in 0 until puzzle.cols) {
                                    val p = CellPos(r, c)
                                    if (letterCells.contains(p)) {
                                        val isSelected = p == selected
                                        val isActive = activeCells.contains(p)
                                        val radius = cell * 0.16f
                                        Box(
                                            modifier = Modifier
                                                .size(cell)
                                                .clip(RoundedCornerShape(radius))
                                                .background(
                                                    when {
                                                        isSelected -> colors.accentColor.copy(alpha = 0.30f)
                                                        isActive -> colors.accentColor.copy(alpha = 0.16f)
                                                        colors.isDark -> Color.White.copy(alpha = 0.05f)
                                                        else -> Color.White.copy(alpha = 0.85f)
                                                    }
                                                )
                                                .border(
                                                    if (isSelected) 2.dp else 1.dp,
                                                    when {
                                                        isSelected -> colors.accentBright
                                                        isActive -> colors.accentColor.copy(alpha = 0.55f)
                                                        else -> colors.strokeColor
                                                    },
                                                    RoundedCornerShape(radius)
                                                )
                                                .pressable(depth = 0.96f) { tapCell(p) }
                                        ) {
                                            puzzle.numberAt(p)?.let { n ->
                                                Text(
                                                    text = "$n",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = colors.secondaryText,
                                                    modifier = Modifier
                                                        .align(Alignment.TopStart)
                                                        .padding(start = 4.dp, top = 2.dp)
                                                )
                                            }
                                            entered[p]?.let { ch ->
                                                Text(
                                                    text = ch.toString(),
                                                    fontFamily = CormorantFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = (cell.value * 0.52f).sp,
                                                    color = if (isSelected || isActive) colors.accentBright else colors.primaryText,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(cell))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Clue bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.glassSurface)
                        .border(1.dp, colors.strokeColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClueNavButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft) { step(-1) }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        activeEntry?.let { e ->
                            val dir = if (e.isAcross) DailyCrosswordStrings.across(lang) else DailyCrosswordStrings.down(lang)
                            Text(
                                text = "${e.num} ${dir.uppercase()} · (${e.answer.length})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = colors.accentColor
                            )
                            Text(
                                text = e.clue.text(lang),
                                fontFamily = CormorantFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp,
                                lineHeight = 21.sp,
                                color = colors.primaryText,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                    ClueNavButton(Icons.AutoMirrored.Filled.KeyboardArrowRight) { step(1) }
                }

                // Keyboard
                CrosswordKeyboard(
                    onLetter = { typeLetter(it) },
                    onBackspace = { backspace() }
                )
            }
        }
    }
}

@Composable
private fun StatPill(icon: androidx.compose.ui.graphics.vector.ImageVector? = null, text: String) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.accentChip)
            .border(1.dp, colors.strokeColor, CircleShape)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = colors.secondaryText, modifier = Modifier.size(13.dp))
        }
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.secondaryText)
    }
}

@Composable
private fun ClueNavButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(colors.accentChip)
            .border(1.dp, colors.strokeColor, CircleShape)
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(18.dp))
    }
}

private val KB_ROWS = listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")

@Composable
private fun CrosswordKeyboard(onLetter: (Char) -> Unit, onBackspace: () -> Unit) {
    val colors = Theme.colors
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .padding(bottom = 8.dp)
    ) {
        val keySpacing = 5.dp
        val keyW = (maxWidth - keySpacing * 9 - 8.dp) / 10
        val keyH = keyW * 1.35f

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            KB_ROWS.forEachIndexed { idx, row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(keySpacing, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { ch ->
                        Box(
                            modifier = Modifier
                                .width(keyW)
                                .height(keyH)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.9f))
                                .border(0.75.dp, colors.strokeColor, RoundedCornerShape(8.dp))
                                .pressable(depth = 0.94f) { onLetter(ch) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ch.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (colors.isDark) Color.White.copy(alpha = 0.92f) else colors.primaryText
                            )
                        }
                    }
                    if (idx == KB_ROWS.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(keyW * 1.6f)
                                .height(keyH)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (colors.isDark) Color.White.copy(alpha = 0.13f) else Color.White.copy(alpha = 0.7f))
                                .border(0.75.dp, colors.strokeColor, RoundedCornerShape(8.dp))
                                .pressable(depth = 0.94f) { onBackspace() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = if (colors.isDark) Color.White.copy(alpha = 0.92f) else colors.primaryText,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
