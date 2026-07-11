package com.thaqalayn.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Port of iOS EmPressStyle: a deep, smooth, no-bounce press (scale 0.92 + slight
 * dim) with a light haptic on press-down. Used on every tappable card/button.
 */
fun Modifier.pressable(
    depth: Float = 0.92f,
    dim: Float = 0.90f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (pressed) depth else 1f,
        animationSpec = if (pressed) tween(durationMillis = 70) else spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "pressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) dim else 1f,
        animationSpec = if (pressed) tween(durationMillis = 70) else spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "pressAlpha"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .clickable(interactionSource = interactionSource, indication = null) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}

/** Gentler preset for full-width rows (iOS EmPressStyle.gentle). */
fun Modifier.pressableGentle(onClick: () -> Unit): Modifier =
    pressable(depth = 0.97f, dim = 0.94f, onClick = onClick)
