package com.thaqalayn.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.model.AudioPlayerState
import com.thaqalayn.app.model.Surah
import com.thaqalayn.app.model.VerseWithTafsir
import com.thaqalayn.app.ui.theme.Theme

/**
 * Play / pause the real recitation of one verse (iOS VerseRecitationButton). The
 * chip turns gold while this verse is loaded in the player. [quiet] draws a bare
 * glyph at reduced strength while idle and only grows its chip once the verse is
 * loaded, for rails that sit beside reading text on every verse.
 */
@Composable
fun VerseRecitationButton(
    surah: Surah,
    verse: VerseWithTafsir,
    size: Dp = 36.dp,
    quiet: Boolean = false
) {
    val colors = Theme.colors
    val playback = AudioManager.currentPlayback
    val isActive = playback?.surahNumber == surah.number && playback.verseNumber == verse.number
    val state = AudioManager.playerState
    val isPlaying = isActive && state == AudioPlayerState.PLAYING
    val isLoading = isActive && (state == AudioPlayerState.LOADING || state == AudioPlayerState.BUFFERING)
    val isBare = quiet && !isActive
    val shape = RoundedCornerShape(11.dp)

    Box(
        modifier = Modifier
            .size(size)
            .pressable { AudioManager.playVerse(verse, surah) }
            .let {
                when {
                    isBare -> it
                    isActive -> it.clip(shape).background(colors.accentGradient)
                    else -> it.clip(shape).background(colors.accentChip).border(1.dp, colors.strokeColor, shape)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = colors.onAccentText,
                strokeWidth = 1.5.dp,
                modifier = Modifier.size(size * 0.4f)
            )
        } else {
            Icon(
                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause recitation" else "Play recitation",
                tint = if (isActive) colors.onAccentText else colors.accentColor,
                modifier = Modifier
                    .size(size * 0.42f)
                    .alpha(if (isBare) 0.55f else 1f)
            )
        }
    }
}
