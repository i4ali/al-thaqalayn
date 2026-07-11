package com.thaqalayn.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.thaqalayn.app.settings.ThemeVariant

/**
 * Full port of the iOS ThemeManager palette. Every property matches the Swift
 * side one-to-one so screens can be translated without color drift.
 */
class ThaqalaynColors(val variant: ThemeVariant) {

    private val warm = variant == ThemeVariant.WARM_INVITING

    // Backgrounds
    val primaryBackground = if (warm) Color(0xFFF8F5FF) else Color(0xFF0A1512)
    val secondaryBackground = if (warm) Color(0xFFFBF7FA) else Color(0xFF081310)
    val tertiaryBackground = if (warm) Color(0xFFFFF9F5) else Color(0xFF0C1D16)

    // Text
    val primaryText = if (warm) Color(0xFF2D2520) else Color(0xFFF1E8D6)
    val secondaryText = if (warm) Color(0xFF6B5D54) else Color(0xFFF1E8D6).copy(alpha = 0.74f)
    val tertiaryText = if (warm) Color(0xFF82786F) else Color(0xFFF1E8D6).copy(alpha = 0.52f)
    val quaternaryText = if (warm) Color(0xFFC7BDB5) else Color(0xFFF1E8D6).copy(alpha = 0.24f)

    // Accents
    val accentColor = if (warm) Color(0xFF9B8FBF) else Color(0xFFD6B25E)
    val accentColorDeep = if (warm) Color(0xFF8B7FA8) else Color(0xFFB8923F)
    val accentColorSoft = accentColor.copy(alpha = 0.14f)

    val accentGradient = if (warm) {
        Brush.linearGradient(listOf(Color(0xFFE89A6F), Color(0xFFD88A5F)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFECD49A), Color(0xFFB8923F)))
    }

    val purpleGradient = if (warm) {
        Brush.linearGradient(listOf(Color(0xFF9B8FBF), Color(0xFF8B7FA8)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFB8A6D9), Color(0xFF9788C2)))
    }

    // Materials / surfaces
    val glassSurface = if (warm) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.045f)
    val glassSurfaceRecessed = if (warm) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.03f)
    val screenGlowColor = if (warm) Color(0xFFE89A6F).copy(alpha = 0.08f) else Color(0xFFD6B25E).copy(alpha = 0.14f)

    // Strokes & dividers
    val strokeColor = if (warm) Color(0xFF2D2520).copy(alpha = 0.10f) else Color(0xFFD6B25E).copy(alpha = 0.16f)
    val strokeColorStrong = if (warm) Color(0xFF2D2520).copy(alpha = 0.18f) else Color(0xFFD6B25E).copy(alpha = 0.24f)
    val dividerColor = if (warm) Color(0xFF2D2520).copy(alpha = 0.06f) else Color(0xFFD6B25E).copy(alpha = 0.09f)

    // Orbs (decorative floating background circles)
    val floatingOrbColors = if (warm) listOf(
        Color(0xFF9B8FBF).copy(alpha = 0.06f),
        Color(0xFFE89A6F).copy(alpha = 0.05f),
        Color(0xFF7FB89A).copy(alpha = 0.04f)
    ) else listOf(
        Color(0xFFE89464).copy(alpha = 0.18f),
        Color(0xFFB8A6D9).copy(alpha = 0.12f),
        Color(0xFF5BC58A).copy(alpha = 0.06f)
    )

    // Semantic
    val semanticGreen = if (warm) Color(0xFF7FB89A) else Color(0xFF3E9B79)
    val semanticRed = if (warm) Color(0xFFED4799) else Color(0xFFF47875)
    val semanticBlue = if (warm) Color(0xFF6366F2) else Color(0xFF6FA5E8)
    val semanticYellow = if (warm) Color(0xFFF2C74D) else Color(0xFFF2C969)
    val semanticLilac = if (warm) Color(0xFFB8A6D9).copy(alpha = 0.7f) else Color(0xFFB8A6D9)

    // Midnight Emerald additions (same values on both variants, as on iOS)
    val accentBright = Color(0xFFECD49A)
    val accentChip = Color(0xFFD6B25E).copy(alpha = 0.14f)
    val glassSurfaceElevated = if (variant == ThemeVariant.NIGHT_SANCTUARY) Color.White.copy(alpha = 0.07f) else Color.White
    val semanticGreenChip = Color(0xFF3E9B79).copy(alpha = 0.16f)
    val onAccentText = Color(0xFF1A1408)
    val emeraldBgTop = Color(0xFF0C1D16)
    val emeraldBgBottom = Color(0xFF081310)

    val isMidnightEmerald: Boolean get() = variant == ThemeVariant.NIGHT_SANCTUARY
    val isDark: Boolean get() = variant == ThemeVariant.NIGHT_SANCTUARY
}
