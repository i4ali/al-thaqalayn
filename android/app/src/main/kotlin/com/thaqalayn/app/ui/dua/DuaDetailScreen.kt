package com.thaqalayn.app.ui.dua

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DuasManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.DuaListenButton
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Per-dua detail: Arabic + Listen + transliteration + translation + source + share. */
@Composable
fun DuaDetailScreen(duaId: String, navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val scale = ReadingSettingsManager.scale
    val context = LocalContext.current
    val dua = remember(duaId) { DuasManager.byId(duaId) } ?: return

    val shareText = """
        ${dua.situation(lang)}

        ${dua.arabic}

        ${dua.transliteration}

        ${dua.translation(lang)}

        — Source: ${dua.source}
        Sent via Thaqalayn
    """.trimIndent()

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Back
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, colors.strokeColor, CircleShape)
                    .pressable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Header
            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = dua.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang.isRTL) 0.sp else 3.sp,
                        color = colors.accentColor
                    )
                    Text(
                        text = dua.situation(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        color = colors.primaryText
                    )
                }
            }

            // Arabic
            EmCard(glow = true, modifier = Modifier.fillMaxWidth()) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = dua.arabic,
                        fontFamily = AmiriFamily,
                        fontSize = (30 * scale).sp,
                        lineHeight = (30 * scale * 1.8f).sp,
                        color = colors.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    )
                }
            }

            DuaListenButton(arabic = dua.arabic)

            // Transliteration
            Text(
                text = dua.transliteration,
                fontFamily = CormorantFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = (17 * scale).sp,
                lineHeight = (17 * scale * 1.4f).sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Translation
            EmCard(modifier = Modifier.fillMaxWidth()) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (lang == CommentaryLanguage.URDU) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    Text(
                        text = dua.translation(lang),
                        fontFamily = if (lang == CommentaryLanguage.URDU) AmiriFamily else CormorantFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (17 * scale).sp,
                        lineHeight = (17 * scale * 1.5f).sp,
                        color = colors.primaryText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }

            // Source (tappable when the dua is Qur'anic)
            val linked = dua.surahNumber != null && dua.verseNumber != null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .let {
                        if (linked) it.pressable {
                            navController.navigate(Routes.surah(dua.surahNumber!!, dua.verseNumber))
                        } else it
                    },
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source · ${dua.source}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (linked) colors.accentColor else colors.tertiaryText
                )
                if (linked) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = colors.accentColor, modifier = Modifier.size(12.dp))
                }
            }

            // Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, RoundedCornerShape(15.dp), spotColor = colors.accentColor.copy(alpha = 0.28f))
                    .clip(RoundedCornerShape(15.dp))
                    .background(colors.accentGradient)
                    .pressable {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share dua"))
                    }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = colors.onAccentText, modifier = Modifier.size(15.dp))
                Text(
                    text = "Share",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    color = colors.onAccentText
                )
            }
        }
    }
}
