package com.thaqalayn.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.thaqalayn.app.settings.ThemeManager
import com.thaqalayn.app.settings.ThemeVariant

val LocalThaqalaynColors = staticCompositionLocalOf { ThaqalaynColors(ThemeVariant.NIGHT_SANCTUARY) }

/** Shorthand accessor used across screens: `Theme.colors.primaryText` etc. */
object Theme {
    val colors: ThaqalaynColors
        @Composable get() = LocalThaqalaynColors.current
}

@Composable
fun ThaqalaynTheme(content: @Composable () -> Unit) {
    val variant = ThemeManager.selectedTheme
    val colors = remember(variant) { ThaqalaynColors(variant) }

    val materialScheme: ColorScheme = if (colors.isDark) {
        darkColorScheme(
            primary = colors.accentColor,
            onPrimary = colors.onAccentText,
            background = colors.primaryBackground,
            onBackground = colors.primaryText,
            surface = colors.tertiaryBackground,
            onSurface = colors.primaryText,
            surfaceVariant = colors.tertiaryBackground,
            onSurfaceVariant = colors.secondaryText,
            outline = colors.strokeColor
        )
    } else {
        lightColorScheme(
            primary = colors.accentColor,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            background = colors.primaryBackground,
            onBackground = colors.primaryText,
            surface = colors.secondaryBackground,
            onSurface = colors.primaryText,
            surfaceVariant = colors.tertiaryBackground,
            onSurfaceVariant = colors.secondaryText,
            outline = colors.strokeColor
        )
    }

    CompositionLocalProvider(LocalThaqalaynColors provides colors) {
        MaterialTheme(colorScheme = materialScheme, content = content)
    }
}
