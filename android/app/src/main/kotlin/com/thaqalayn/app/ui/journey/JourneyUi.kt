package com.thaqalayn.app.ui.journey

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.DoNotTouch
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import com.thaqalayn.app.R
import com.thaqalayn.app.data.IslamicCalendarManager

/**
 * Per-journey UI behavior: the differences between the five otherwise
 * identical iOS journey screens (icon, cover art, festive vs somber wording
 * and done-styles, station labels, current-day source).
 */
internal data class JourneyUiConfig(
    val id: String,
    val icon: ImageVector,
    val coverRes: Int,
    /** Somber observances say "observed" and mute the done treatment. */
    val isObservance: Boolean,
    /** Arbaeen counts "stations", not "days". */
    val usesStations: Boolean = false,
    val currentDay: () -> Int? = { null }
)

internal fun journeyUiConfig(id: String): JourneyUiConfig = when (id) {
    "ramadan" -> JourneyUiConfig(
        id = "ramadan", icon = Icons.Filled.NightsStay,
        coverRes = R.drawable.journey_cover_ramadan, isObservance = false,
        currentDay = { IslamicCalendarManager.currentRamadanDay() }
    )
    "hajj" -> JourneyUiConfig(
        id = "hajj", icon = Icons.Filled.AccountBalance,
        coverRes = R.drawable.journey_cover_hajj, isObservance = false,
        currentDay = { IslamicCalendarManager.currentHajjDay() }
    )
    "muharram" -> JourneyUiConfig(
        id = "muharram", icon = Icons.Filled.LocalFireDepartment,
        coverRes = R.drawable.journey_cover_muharram, isObservance = true,
        currentDay = { IslamicCalendarManager.currentMuharramDay() }
    )
    "fatimiyya" -> JourneyUiConfig(
        id = "fatimiyya", icon = Icons.Filled.LocalFlorist,
        coverRes = R.drawable.journey_cover_fatimiyya, isObservance = true
    )
    else -> JourneyUiConfig(
        id = "arbaeen", icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        coverRes = R.drawable.journey_cover_arbaeen, isObservance = true,
        usesStations = true,
        currentDay = { IslamicCalendarManager.currentArbaeenStation() }
    )
}

/** Material stand-in for a journey day's SF Symbol (JourneyDay.icon). */
internal fun journeyDayIcon(name: String): ImageVector = when (name) {
    "arrow.counterclockwise" -> Icons.Filled.Refresh
    "arrow.uturn.left" -> Icons.AutoMirrored.Filled.Undo
    "book.fill" -> Icons.Filled.MenuBook
    "brain", "brain.head.profile" -> Icons.Filled.Psychology
    "building.columns" -> Icons.Filled.AccountBalance
    "calendar.badge.exclamationmark" -> Icons.Filled.Event
    "checkmark.seal.fill" -> Icons.Filled.Verified
    "circle.hexagongrid.fill" -> Icons.Filled.Hive
    "drop.fill", "drop.triangle.fill" -> Icons.Filled.WaterDrop
    "figure.walk", "figure.walk.circle.fill", "figure.walk.departure" -> Icons.AutoMirrored.Filled.DirectionsWalk
    "flag.fill" -> Icons.Filled.Flag
    "flame.fill" -> Icons.Filled.LocalFireDepartment
    "gift.fill" -> Icons.Filled.CardGiftcard
    "globe.americas.fill" -> Icons.Filled.Public
    "hand.raised.fill" -> Icons.Filled.PanTool
    "hand.raised.slash.fill" -> Icons.Filled.DoNotTouch
    "hands.and.sparkles.fill" -> Icons.Filled.CleanHands
    "hands.sparkles", "hands.sparkles.fill" -> Icons.Filled.VolunteerActivism
    "heart.circle.fill", "heart.fill" -> Icons.Filled.Favorite
    "house.fill" -> Icons.Filled.Home
    "leaf.fill" -> Icons.Filled.Eco
    "moon.stars", "moon.stars.fill" -> Icons.Filled.NightsStay
    "moon.zzz.fill" -> Icons.Filled.Bedtime
    "mountain.2.fill" -> Icons.Filled.Landscape
    "person.2.fill" -> Icons.Filled.People
    "person.3.fill" -> Icons.Filled.Groups
    "person.fill.questionmark" -> Icons.Filled.PersonSearch
    "quote.bubble" -> Icons.Filled.FormatQuote
    "scale.3d" -> Icons.Filled.Balance
    "shield.fill", "shield.lefthalf.filled" -> Icons.Filled.Shield
    "sparkles" -> Icons.Filled.AutoAwesome
    "star.circle.fill" -> Icons.Filled.Stars
    "star.fill" -> Icons.Filled.Star
    "sun.dust" -> Icons.Filled.WbSunny
    "sunrise", "sunrise.fill" -> Icons.Filled.WbTwilight
    "text.bubble.fill" -> Icons.AutoMirrored.Filled.Chat
    else -> Icons.Filled.AutoAwesome
}
