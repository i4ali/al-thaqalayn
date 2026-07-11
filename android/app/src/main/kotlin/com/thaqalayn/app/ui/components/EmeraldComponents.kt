package com.thaqalayn.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Tintable phosphor icon (converted from the iOS asset catalog SVGs). */
@Composable
fun PhosphorIcon(
    resId: Int,
    size: Dp = 18.dp,
    tint: Color = Theme.colors.accentColor,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier.size(size)
    )
}

/** Glass card: rounded 20, glass fill, hairline stroke, soft shadow (iOS EmCard). */
@Composable
fun EmCard(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    glow: Boolean = false,
    cornerRadius: Dp = 20.dp,
    borderColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(
                elevation = if (glow) 24.dp else 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (glow) 0.45f else 0.28f),
                spotColor = Color.Black.copy(alpha = if (glow) 0.45f else 0.28f)
            )
            .clip(shape)
            .background(if (elevated || glow) colors.glassSurfaceElevated else colors.glassSurface)
            .border(1.dp, borderColor ?: colors.strokeColor, shape)
    ) {
        content()
    }
}

/** Eyebrow + serif heading (iOS EmHeading). */
@Composable
fun EmHeading(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    sub: String? = null,
    center: Boolean = false
) {
    val colors = Theme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        if (eyebrow != null) {
            Text(
                text = eyebrow.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = colors.accentColor,
                textAlign = if (center) TextAlign.Center else TextAlign.Start
            )
        }
        Text(
            text = title,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.2.sp,
            color = colors.primaryText,
            textAlign = if (center) TextAlign.Center else TextAlign.Start
        )
        if (sub != null) {
            Text(
                text = sub,
                fontSize = 13.5.sp,
                color = colors.secondaryText,
                textAlign = if (center) TextAlign.Center else TextAlign.Start
            )
        }
    }
}

/** Gold numeral circle (iOS EmNumeralCircle). */
@Composable
fun EmNumeralCircle(n: Int, size: Dp = 46.dp) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.accentChip)
            .border(1.dp, colors.accentColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$n",
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value * 0.42f).sp,
            color = colors.accentBright
        )
    }
}

/** Icon chip: rounded-14 gold-chip square with an icon (iOS EmIconChip). */
@Composable
fun EmIconChip(
    icon: ImageVector,
    size: Dp = 46.dp,
    active: Boolean = false
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .let {
                if (active) it.background(colors.accentGradient)
                else it.background(colors.accentChip).border(1.dp, colors.strokeColor, shape)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) colors.onAccentText else colors.accentColor,
            modifier = Modifier.size(size * 0.44f)
        )
    }
}

/** Full-width gold gradient CTA (iOS EmGoldCTA). */
@Composable
fun EmGoldCTA(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    small: Boolean = false,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressable(onClick = onClick)
            .shadow(14.dp, shape, spotColor = colors.accentColor.copy(alpha = 0.28f))
            .clip(shape)
            .background(colors.accentGradient)
            .padding(vertical = if (small) 12.dp else 16.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(if (small) 15.dp else 17.dp))
        }
        Text(
            text = title,
            fontSize = if (small) 14.sp else 15.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            color = colors.onAccentText
        )
    }
}

/** Ornamental divider: gradient hairlines around a label or gold diamond (iOS EmDivider). */
@Composable
fun EmDivider(modifier: Modifier = Modifier, label: String? = null) {
    val colors = Theme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, colors.strokeColor)))
        )
        if (label != null) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                color = colors.tertiaryText
            )
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .rotate(45f)
                    .background(colors.accentColor)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(colors.strokeColor, Color.Transparent)))
        )
    }
}

/** Gold eyebrow row: line icon + uppercase tracked label (iOS EmSectionLabel). */
@Composable
fun EmSectionLabel(icon: ImageVector, text: String) {
    val colors = Theme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(13.dp))
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor
        )
    }
}

/**
 * Full-screen themed background. Midnight Emerald: radial emerald gradient +
 * gold top glow (iOS EmeraldBackground). Light: warm base + soft orbs.
 */
@Composable
fun ThemedBackground(modifier: Modifier = Modifier) {
    val colors = Theme.colors
    if (colors.isMidnightEmerald) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.primaryBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            0.0f to colors.emeraldBgTop,
                            0.55f to colors.primaryBackground,
                            1.0f to colors.emeraldBgBottom,
                            center = androidx.compose.ui.geometry.Offset(0.5f, 0f),
                            radius = 2200f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(colors.accentColor.copy(alpha = 0.14f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(540f, -90f),
                            radius = 700f
                        )
                    )
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(colors.primaryBackground, colors.secondaryBackground, colors.tertiaryBackground)
                    )
                )
        )
    }
}
