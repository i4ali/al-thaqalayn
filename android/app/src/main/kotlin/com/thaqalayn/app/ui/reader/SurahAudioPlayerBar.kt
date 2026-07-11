package com.thaqalayn.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.Reciter
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Floating mini audio player (iOS SurahAudioPlayerView emerald mini bar). */
@Composable
fun SurahAudioPlayerBar() {
    val colors = Theme.colors
    val playback = AudioManager.currentPlayback ?: return
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, shape, spotColor = Color.Black.copy(alpha = 0.45f))
                .clip(shape)
                .background(if (colors.isDark) Color(0xFF10201A) else Color.White)
                .border(1.dp, colors.strokeColor, shape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play/pause gold circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(12.dp, CircleShape, spotColor = colors.accentColor.copy(alpha = 0.35f))
                        .clip(CircleShape)
                        .background(colors.accentGradient)
                        .pressable { AudioManager.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (AudioManager.playerState == AudioPlayerState.PLAYING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play or pause",
                        tint = colors.onAccentText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = playback.surahName,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        color = colors.primaryText,
                        maxLines = 1
                    )
                    Text(
                        text = "Verse ${playback.verseNumber} · ${playback.reciter.nameEnglish}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.secondaryText,
                        maxLines = 1
                    )
                }

                // Progress circle
                Box(contentAlignment = Alignment.Center) {
                    val progress = playback.progress.toFloat().coerceIn(0f, 1f)
                    Canvas(modifier = Modifier.size(32.dp)) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.10f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFFD6B25E),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentBright
                    )
                }

                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Expand player",
                    tint = colors.accentColor,
                    modifier = Modifier
                        .size(28.dp)
                        .pressable { expanded = !expanded }
                )
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Stop playback",
                    tint = colors.tertiaryText,
                    modifier = Modifier
                        .size(28.dp)
                        .pressable { AudioManager.stop() }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.SkipPrevious,
                            contentDescription = "Previous verse",
                            tint = colors.accentColor,
                            modifier = Modifier
                                .size(32.dp)
                                .pressable { AudioManager.skipToPrevious() }
                        )
                        Icon(
                            Icons.Filled.SkipNext,
                            contentDescription = "Next verse",
                            tint = colors.accentColor,
                            modifier = Modifier
                                .size(32.dp)
                                .pressable { AudioManager.skipToNext() }
                        )
                    }
                    ReciterPickerRow()
                }
            }
        }
    }
}

/** Horizontal reciter selector shown in the expanded player. */
@Composable
private fun ReciterPickerRow() {
    val colors = Theme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "RECITER",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = colors.accentColor
        )
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Reciter.popularReciters.size) { i ->
                val reciter = Reciter.popularReciters[i]
                val selected = AudioManager.selectedReciter.id == reciter.id
                val chipShape = RoundedCornerShape(12.dp)
                Text(
                    text = reciter.nameEnglish,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) colors.onAccentText else colors.accentColor,
                    modifier = Modifier
                        .clip(chipShape)
                        .let {
                            if (selected) it.background(colors.accentGradient)
                            else it.background(colors.accentChip).border(1.dp, colors.strokeColor, chipShape)
                        }
                        .pressable { AudioManager.setReciter(reciter) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
