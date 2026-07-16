package com.thaqalayn.app.ui.journey

// Full-screen preview shown when a non-subscriber taps a locked journey day
// (iOS VeiledDayPreview.swift): the journey cover as a heavily veiled backdrop
// (1.22x, blur 44, black 0.52), then the day's theme, what waits inside, a
// premium note, and the upgrade CTA. Always dark regardless of app theme;
// gold/cream descent palette.

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.JourneyManagers
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.CoverVeil
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.deepdive.DeepDivePalette
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily

@Composable
fun VeiledDayPreviewScreen(journeyId: String, dayNumber: Int, navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    val manager = JourneyManagers.byId(journeyId) ?: return
    val config = journeyUiConfig(journeyId)
    val day = manager.day(dayNumber)

    // A purchase made from the paywall lands back here: swap the veil for the
    // real day so the user never returns to a lock they just opened.
    LaunchedEffect(PremiumManager.isPremium) {
        if (PremiumManager.canAccessJourneyDay(dayNumber)) {
            navController.popBackStack()
            navController.navigate(Routes.journeyDay(journeyId, dayNumber))
        }
    }

    val dayLabel =
        if (config.usesStations) JourneyStrings.stationN(dayNumber, lang)
        else JourneyStrings.dayN(dayNumber, lang)
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        CoverVeil(art = config.coverRes, overlayAlpha = 0.52f, modifier = Modifier.fillMaxSize())

        androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = 30.dp)
                    .padding(top = 66.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$dayLabel · ${JourneyStrings.premium(lang)}".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.gold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = day?.localizedTheme(lang) ?: JourneyStrings.title(journeyId, lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 34.sp,
                    lineHeight = 38.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.cream
                )
                if (day != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = day.themeArabic,
                        fontFamily = AmiriFamily,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = DeepDivePalette.goldBright
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(1.dp)
                        .background(DeepDivePalette.gold.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = JourneyStrings.waitsInside(lang).uppercase(),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.4.sp,
                    color = DeepDivePalette.mute
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    WaitsRow(Icons.Filled.FormatQuote, JourneyStrings.aDuaForThisDay(lang))
                    WaitsRow(
                        Icons.AutoMirrored.Filled.MenuBook,
                        JourneyStrings.versesWithReflections(day?.verses?.size ?: 3, lang)
                    )
                    WaitsRow(Icons.Outlined.Lightbulb, JourneyStrings.aGuidedReflection(lang))
                }

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = JourneyStrings.premiumDayNote(lang),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    color = DeepDivePalette.mute,
                    modifier = Modifier.widthIn(max = 300.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepDivePalette.gold, DeepDivePalette.goldBright)
                            )
                        )
                        .pressable {
                            navController.navigate(Routes.paywall(coverKey = journeyId))
                        }
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = JourneyStrings.unlockPremium(lang),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF1F1708)
                    )
                }
            }
        }

        // Close chevron, matching the descent's chrome.
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, DeepDivePalette.gold.copy(alpha = 0.25f), CircleShape)
                .pressable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Close",
                tint = DeepDivePalette.cream,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WaitsRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = DeepDivePalette.gold,
            modifier = Modifier.size(16.dp)
        )
        Text(text = text, fontSize = 13.5.sp, color = DeepDivePalette.cream.copy(alpha = 0.92f))
    }
}
