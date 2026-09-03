package com.thaqalayn.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.R
import com.thaqalayn.app.audio.DuaStreamPlayer
import com.thaqalayn.app.model.SpecialDua
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

private fun timeString(t: Double): String {
    if (t.isNaN() || t < 0) return "0:00"
    val total = t.toInt()
    return "%d:%02d".format(total / 60, total % 60)
}

/**
 * A compact, docked now-playing bar for a streamed dua/ziyarat recitation. Appears
 * whenever a recitation is loaded but its reader screen is not on top, so the listener
 * always has pause / stop / reopen within reach (iOS DuaMiniPlayer). Purely a control
 * surface; DuaStreamPlayer drives audio. [onOpen] reopens the dua's reader.
 */
@Composable
fun DuaMiniPlayer(modifier: Modifier = Modifier, onOpen: (SpecialDua) -> Unit) {
    val dua = DuaStreamPlayer.currentDua ?: return
    val colors = Theme.colors
    val shape = RoundedCornerShape(20.dp)

    val progress = if (DuaStreamPlayer.duration > 0)
        (DuaStreamPlayer.currentTime / DuaStreamPlayer.duration).coerceIn(0.0, 1.0).toFloat()
    else 0f

    val subtitle = when {
        DuaStreamPlayer.isLoading -> "Loading"
        DuaStreamPlayer.duration > 0 ->
            "${timeString(DuaStreamPlayer.currentTime)} / ${timeString(DuaStreamPlayer.duration)}"
        else -> "Now reciting"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, shape, spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f))
            .clip(shape)
            .background(colors.glassSurfaceElevated)
            .border(1.dp, colors.strokeColor, shape)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Leading tappable region: reopen the reader.
            Row(
                modifier = Modifier.weight(1f).pressableGentle { onOpen(dua) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.explore_cover_duas_ziyarat),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.strokeColor, RoundedCornerShape(10.dp))
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = dua.titleEn,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = colors.primaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.secondaryText,
                        maxLines = 1
                    )
                }
            }

            // Play / pause.
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.accentGradient)
                    .pressable { DuaStreamPlayer.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                if (DuaStreamPlayer.isLoading) {
                    CircularProgressIndicator(
                        color = colors.onAccentText,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        if (DuaStreamPlayer.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (DuaStreamPlayer.isPlaying) "Pause" else "Play",
                        tint = colors.onAccentText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Stop.
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.glassSurface)
                    .border(1.dp, colors.strokeColor, CircleShape)
                    .pressable { DuaStreamPlayer.stop() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Stop",
                    tint = colors.secondaryText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Progress hairline along the bottom edge.
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(progress)
                .height(2.dp)
                .background(colors.accentColor)
        )
    }
}
