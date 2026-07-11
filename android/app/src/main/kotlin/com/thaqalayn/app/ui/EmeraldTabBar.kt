package com.thaqalayn.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.Theme

data class EmeraldTabItem(
    val id: Int,
    val label: String,
    val icon: ImageVector
)

/**
 * Custom floating tab bar used in BOTH themes - a light "card" in Light,
 * emerald-black & gold in Midnight Emerald (iOS EmeraldTabBar).
 */
@Composable
fun EmeraldTabBar(
    items: List<EmeraldTabItem>,
    selection: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val isEmerald = colors.isMidnightEmerald
    val selectedColor = if (isEmerald) colors.accentBright else colors.accentColor
    val inactiveColor = if (isEmerald) colors.tertiaryText else colors.secondaryText
    val cardTint = if (isEmerald) Color(0xFF0A1512).copy(alpha = 0.94f) else Color.White.copy(alpha = 0.92f)
    val isRTL = CommentaryLanguageManager.selectedLanguage.isRTL
    val labelSize = if (isRTL) 12.sp else 10.sp
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier = modifier
            .padding(horizontal = 18.dp)
            .padding(bottom = 14.dp)
            .fillMaxWidth()
            .shadow(
                elevation = if (isEmerald) 24.dp else 16.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = if (isEmerald) 0.5f else 0.15f)
            )
            .clip(shape)
            .background(cardTint)
            .border(1.dp, colors.strokeColor, shape)
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        items.forEach { item ->
            val selected = item.id == selection
            Column(
                modifier = Modifier
                    .weight(1f)
                    .pressable { onSelect(item.id) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) selectedColor else inactiveColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = item.label,
                    fontSize = labelSize,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (selected) selectedColor else inactiveColor
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (selected) colors.accentColor else Color.Transparent)
                )
            }
        }
    }
}
