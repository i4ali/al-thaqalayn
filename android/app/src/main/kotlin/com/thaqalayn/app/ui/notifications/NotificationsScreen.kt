package com.thaqalayn.app.ui.notifications

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.model.NotificationItem
import com.thaqalayn.app.model.NotificationType
import com.thaqalayn.app.notifications.NotificationInboxStore
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Notification inbox (iOS NotificationsView) - the recorded history of delivered notifications. */
@Composable
fun NotificationsScreen(navController: NavHostController) {
    val colors = Theme.colors
    var notifications by remember { mutableStateOf(NotificationInboxStore.load()) }

    fun persist(updated: List<NotificationItem>) {
        notifications = updated
        NotificationInboxStore.save(updated)
    }

    fun handleTap(notification: NotificationItem) {
        persist(notifications.map { if (it.id == notification.id) it.copy(isRead = true) else it })

        // Journey-start notification -> open the journey; verse types -> the verse.
        val journeyId = notification.journeyId
        if (notification.type == NotificationType.journey && journeyId != null) {
            navController.navigate(Routes.journey(journeyId))
            return
        }
        val surah = notification.surahNumber
        val verse = notification.verseNumber
        if (surah != null && verse != null) {
            navController.navigate(Routes.surah(surah, verse))
        }
    }

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
                Spacer(modifier = Modifier.weight(1f))
                if (notifications.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.semanticRed,
                        modifier = Modifier
                            .pressable { persist(emptyList()) }
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                    )
                }
            }

            if (notifications.isEmpty()) {
                // Empty state (iOS "All Caught Up")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp)
                        .padding(bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmIconChip(icon = Icons.Outlined.NotificationsOff, size = 72.dp)
                    Text(
                        text = "All Caught Up",
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        color = colors.primaryText
                    )
                    Text(
                        text = "Notifications will appear here when you receive them.",
                        fontSize = 15.sp,
                        color = colors.secondaryText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp, end = 20.dp, top = 8.dp, bottom = 40.dp
                    )
                ) {
                    item {
                        EmHeading(eyebrow = "Your Inbox", title = "Notifications")
                    }
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(notification = notification, onTap = { handleTap(notification) })
                    }
                }
            }
        }
    }
}

private fun typeIcon(type: NotificationType): ImageVector = when (type) {
    NotificationType.dailyVerse -> Icons.Filled.MenuBook
    NotificationType.streak -> Icons.Filled.LocalFireDepartment
    NotificationType.milestone -> Icons.Filled.Star
    NotificationType.nudge -> Icons.Filled.Favorite
    NotificationType.nearCompletion -> Icons.Filled.CheckCircle
    NotificationType.journey -> Icons.Filled.Map
}

@Composable
private fun NotificationCard(notification: NotificationItem, onTap: () -> Unit) {
    val colors = Theme.colors
    EmCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableGentle(onClick = onTap)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            EmIconChip(icon = typeIcon(notification.type), size = 46.dp)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        color = colors.primaryText,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.accentColor)
                        )
                    }
                }

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = colors.secondaryText,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Text(
                    text = formatTimestamp(notification.timestamp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.tertiaryText,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

/** "Just now" / "N minutes ago" / "N hours ago" / "Yesterday" / "N days ago" (iOS formatTimestamp). */
private fun formatTimestamp(timestamp: Long): String {
    val elapsedMinutes = (System.currentTimeMillis() - timestamp) / 60_000L
    val hours = elapsedMinutes / 60
    val days = hours / 24
    return when {
        days > 1 -> "$days days ago"
        days == 1L -> "Yesterday"
        hours > 1 -> "$hours hours ago"
        hours == 1L -> "1 hour ago"
        elapsedMinutes > 1 -> "$elapsedMinutes minutes ago"
        elapsedMinutes == 1L -> "1 minute ago"
        else -> "Just now"
    }
}
