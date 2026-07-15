package com.thaqalayn.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.audio.TafsirReader
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.ui.theme.Theme

/**
 * Reusable "Listen" control for a supplication's Arabic text. Speaks the dua via
 * the shared TTS reader and reflects Listen / Pause / Resume state. Required on
 * every screen that shows a dua/ziyarat's Arabic (project rule).
 */
@Composable
fun DuaListenButton(arabic: String, modifier: Modifier = Modifier) {
    val colors = Theme.colors
    val isThisDua = TafsirReader.currentText == arabic

    // Stop speaking when the screen goes away, but only if it was this dua.
    DisposableEffect(arabic) {
        onDispose {
            if (TafsirReader.currentText == arabic) TafsirReader.stop()
        }
    }

    val label = when {
        isThisDua && TafsirReader.isPlaying -> "Pause"
        isThisDua && TafsirReader.isPaused -> "Resume"
        else -> "Listen"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp)
            .clip(CircleShape)
            .background(colors.accentChip)
            .border(1.dp, colors.strokeColor, CircleShape)
            .pressable {
                if (isThisDua && (TafsirReader.isPlaying || TafsirReader.isPaused)) {
                    TafsirReader.togglePlayPause()
                } else {
                    TafsirReader.speak(text = arabic, language = CommentaryLanguage.ARABIC)
                }
            }
            .padding(horizontal = 20.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isThisDua && TafsirReader.isPlaying) Icons.Filled.Pause else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            tint = colors.accentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.accentColor
        )
    }
}
