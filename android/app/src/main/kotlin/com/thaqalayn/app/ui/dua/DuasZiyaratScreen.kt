package com.thaqalayn.app.ui.dua

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.audio.DuaStreamPlayer
import com.thaqalayn.app.data.SpecialDuasManager
import com.thaqalayn.app.model.SpecialDua
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.CoverHeaderBand
import com.thaqalayn.app.ui.components.DuaMiniPlayer
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-in for the iOS SpecialDuaIcon SF Symbol per dua. */
internal fun specialDuaIcon(id: String): ImageVector = when (id) {
    "kumayl" -> Icons.Filled.Bedtime          // moon.stars.fill
    "ashura" -> Icons.Filled.WaterDrop        // drop.fill
    "tawassul" -> Icons.Filled.VolunteerActivism // hands.sparkles.fill
    "nudba" -> Icons.Filled.WbTwilight        // sunrise.fill
    "ahad" -> Icons.Filled.PanTool            // hand.raised.fill
    "faraj" -> Icons.Outlined.AutoAwesome     // sparkles
    else -> Icons.AutoMirrored.Filled.MenuBook
}

/**
 * The "Duas & Ziyarat" library - the major, most-recited Shia supplications (Kumayl,
 * Ziyarat Ashura, Tawassul, Nudba, al-Ahd), each a full segmented text with a streamed
 * recitation. Sits parallel to the short everyday Daily Duas (iOS DuasZiyaratView).
 */
@Composable
fun DuasZiyaratScreen(navController: NavHostController) {
    val colors = Theme.colors
    val duas = SpecialDuasManager.duas
    val docked = DuaStreamPlayer.currentDua != null

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp,
                    bottom = if (docked) 96.dp else 40.dp
                )
            ) {
                item {
                    Box {
                        CoverHeaderBand(art = R.drawable.explore_cover_duas_ziyarat, height = 240.dp)
                        Column(modifier = Modifier.padding(top = 150.dp)) {
                            EmHeading(
                                eyebrow = "Supplications",
                                title = "Duas & Ziyarat",
                                sub = "The great supplications, with recitation"
                            )
                        }
                    }
                }
                items(duas, key = { it.id }) { dua ->
                    SpecialDuaCard(dua = dua) {
                        navController.navigate(Routes.specialDua(dua.id))
                    }
                }
            }
        }

        if (docked) {
            DuaMiniPlayer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) { d -> navController.navigate(Routes.specialDua(d.id)) }
        }
    }
}

@Composable
private fun SpecialDuaCard(dua: SpecialDua, onOpen: () -> Unit) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth().pressable(depth = 0.97f, onClick = onOpen)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmIconChip(icon = specialDuaIcon(dua.id))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = dua.titleEn,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dua.whenEn,
                    fontSize = 12.5.sp,
                    color = colors.tertiaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.tertiaryText,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
