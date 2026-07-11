package com.thaqalayn.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** The "Aa" toggle chip that opens the reading text-size panel (iOS TextSizeButton). */
@Composable
fun TextSizeButton(isOpen: Boolean, onClick: () -> Unit) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isOpen) colors.accentChip else Color.Transparent)
            .border(1.dp, colors.strokeColor, CircleShape)
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aa",
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colors.accentColor
        )
    }
}

/**
 * Overlays the floating A- / step-dots / A+ panel at top-end with an
 * outside-tap catcher (iOS textSizePanelOverlay). Host owns the flag.
 */
@Composable
fun TextSizePanelOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }
        AnimatedVisibility(
            visible = isOpen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 60.dp, end = 20.dp),
            enter = scaleIn(initialScale = 0.92f) + fadeIn(),
            exit = scaleOut(targetScale = 0.92f) + fadeOut()
        ) {
            TextSizePanel()
        }
    }
}

/** The floating A- / step-dots / A+ panel; reads + mutates ReadingSettingsManager. */
@Composable
fun TextSizePanel() {
    val colors = Theme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .shadow(16.dp, shape, spotColor = Color.Black.copy(alpha = 0.35f))
            .clip(shape)
            .background(if (colors.isDark) Color(0xFF12241D) else Color.White.copy(alpha = 0.97f))
            .border(1.dp, colors.strokeColor, shape)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "A",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (ReadingSettingsManager.canDecrease) colors.accentColor else colors.tertiaryText,
            modifier = Modifier
                .size(28.dp)
                .pressable { ReadingSettingsManager.decrease() },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(ReadingSettingsManager.stepCount) { i ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (i <= ReadingSettingsManager.stepIndex) colors.accentColor
                            else colors.strokeColorStrong
                        )
                )
            }
        }
        Text(
            text = "A",
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (ReadingSettingsManager.canIncrease) colors.accentColor else colors.tertiaryText,
            modifier = Modifier
                .size(28.dp)
                .pressable { ReadingSettingsManager.increase() },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
