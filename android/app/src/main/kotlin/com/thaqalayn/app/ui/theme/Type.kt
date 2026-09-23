package com.thaqalayn.app.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.thaqalayn.app.R

/** Amiri: Arabic serif used for Qur'an text and Arabic content. */
val AmiriFamily = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

/** Cormorant Garamond: the app's display/reading serif ("EmType.serif" on iOS). */
val CormorantFamily = FontFamily(
    Font(R.font.cormorant_garamond_medium, FontWeight.Medium),
    Font(R.font.cormorant_garamond_semibold, FontWeight.SemiBold),
    // Every italic is the SemiBold Italic face, as on iOS 9.0 (EmType.serifItalic): the
    // Medium Italic was unreadable in cream and secondary grey on the dark grounds.
    Font(R.font.cormorant_garamond_semibold_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.cormorant_garamond_semibold_italic, FontWeight.SemiBold, FontStyle.Italic)
)
