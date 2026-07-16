package com.thaqalayn.app.ui.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.audio.AudioManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.Reciter
import com.thaqalayn.app.notifications.NotificationManager
import com.thaqalayn.app.premium.BillingManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.ReadingSettingsManager
import com.thaqalayn.app.settings.ThemeManager
import com.thaqalayn.app.settings.ThemeVariant
import com.thaqalayn.app.settings.UserProfileManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.TextSizePanel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.Theme

/** Settings: appearance, language, reading, notifications, audio, premium, about (iOS SettingsView core). */
@Composable
fun SettingsScreen(navController: NavHostController) {
    val colors = Theme.colors
    var showTimePicker by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showResetConfirm by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // POST_NOTIFICATIONS runtime permission (API 33+); on grant, flip the toggle on.
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            NotificationManager.updatePreferences(NotificationManager.preferences.copy(enabled = true))
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
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 60.dp
                )
            ) {
                item {
                    EmHeading(eyebrow = "Preferences", title = "Settings")
                }

                // Your name (Today-tab greeting)
                item {
                    SettingsSection(title = "Your Name") {
                        androidx.compose.material3.TextField(
                            value = UserProfileManager.displayName,
                            onValueChange = { UserProfileManager.setName(it) },
                            placeholder = {
                                Text("Shown in the Today greeting", fontSize = 14.sp, color = colors.tertiaryText)
                            },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = colors.primaryText),
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = colors.glassSurfaceRecessed,
                                unfocusedContainerColor = colors.glassSurfaceRecessed,
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                cursorColor = colors.accentColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Appearance
                item {
                    SettingsSection(title = "Appearance") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Theme", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                                Text("Light or Dark", fontSize = 12.sp, color = colors.tertiaryText)
                            }
                            SegmentedPicker(
                                options = ThemeVariant.entries.map { it.displayName },
                                selectedIndex = ThemeVariant.entries.indexOf(ThemeManager.selectedTheme),
                                onSelect = { ThemeManager.setTheme(ThemeVariant.entries[it]) }
                            )
                        }
                    }
                }

                // Language
                item {
                    SettingsSection(title = "Language") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Translations, duas & commentary",
                                fontSize = 12.sp,
                                color = colors.tertiaryText
                            )
                            SegmentedPicker(
                                options = CommentaryLanguage.supportedTafsirLanguages.map { it.displayName },
                                selectedIndex = CommentaryLanguage.supportedTafsirLanguages
                                    .indexOf(com.thaqalayn.app.settings.CommentaryLanguageManager.selectedLanguage)
                                    .coerceAtLeast(0),
                                onSelect = {
                                    com.thaqalayn.app.settings.CommentaryLanguageManager.setLanguage(
                                        CommentaryLanguage.supportedTafsirLanguages[it]
                                    )
                                }
                            )
                        }
                    }
                }

                // Reading text size
                item {
                    SettingsSection(title = "Reading") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Verses, translation & commentary text size",
                                fontSize = 12.sp,
                                color = colors.tertiaryText
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextSizePanel()
                            }
                            Text(
                                "Current: ${(ReadingSettingsManager.scale * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = colors.secondaryText,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Daily verse notifications (iOS "Daily Verse" section)
                item {
                    SettingsSection(title = "Daily Verse") {
                        val notifPrefs = NotificationManager.preferences
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            SettingsToggleRow(
                                title = "Daily Notifications",
                                subtitle = if (notifPrefs.enabled) "Enabled" else "Tap to enable",
                                checked = notifPrefs.enabled,
                                onCheckedChange = { newValue ->
                                    if (newValue && !NotificationManager.hasPermission()) {
                                        if (android.os.Build.VERSION.SDK_INT >= 33) {
                                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        // Blocked at channel level below 33 - nothing to request.
                                    } else {
                                        NotificationManager.updatePreferences(notifPrefs.copy(enabled = newValue))
                                    }
                                }
                            )

                            if (notifPrefs.enabled) {
                                SettingsNavRow(
                                    title = "Notification Time",
                                    subtitle = formatNotificationTime(notifPrefs.hour, notifPrefs.minute)
                                ) { showTimePicker = true }

                                SettingsNavRow(
                                    title = "Language",
                                    subtitle = notifPrefs.language.displayName
                                ) {
                                    // iOS toggleNotificationLanguage: English <-> Urdu
                                    val next = if (notifPrefs.language == CommentaryLanguage.ENGLISH)
                                        CommentaryLanguage.URDU else CommentaryLanguage.ENGLISH
                                    NotificationManager.updatePreferences(notifPrefs.copy(language = next))
                                }

                                SettingsToggleRow(
                                    title = "Include Commentary",
                                    subtitle = if (notifPrefs.includeTafsir) "Brief tafsir shown" else "Verse only",
                                    checked = notifPrefs.includeTafsir,
                                    onCheckedChange = {
                                        NotificationManager.updatePreferences(notifPrefs.copy(includeTafsir = it))
                                    }
                                )

                                // Today's verse preview
                                val todayVerse = NotificationManager.selectTodayVerse()
                                val monthData = NotificationManager.currentMonthData()
                                if (todayVerse != null && monthData != null) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colors.glassSurfaceRecessed)
                                            .border(1.dp, colors.strokeColor, RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("★", fontSize = 12.sp, color = colors.accentColor)
                                            Text(
                                                "Today's Verse (${monthData.name})",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = colors.primaryText
                                            )
                                        }
                                        Text(
                                            "Surah ${todayVerse.surah}, Verse ${todayVerse.verse}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.secondaryText
                                        )
                                        Text(todayVerse.theme, fontSize = 12.sp, color = colors.tertiaryText)
                                    }
                                }
                            }
                        }
                    }
                }

                // Reading progress (iOS "Reading Progress" section)
                item {
                    SettingsSection(title = "Reading Progress") {
                        val progressPrefs = ProgressManager.preferences
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Current Streak", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                                Text(
                                    "${ProgressManager.streak.currentStreak} days",
                                    fontSize = 12.sp,
                                    color = colors.tertiaryText
                                )
                            }

                            SettingsToggleRow(
                                title = "Progress Notifications",
                                subtitle = if (progressPrefs.notificationsEnabled) "Motivational reminders" else "Tap to enable",
                                checked = progressPrefs.notificationsEnabled,
                                onCheckedChange = {
                                    ProgressManager.updatePreferences(progressPrefs.copy(notificationsEnabled = it))
                                }
                            )

                            SettingsToggleRow(
                                title = "Badge Celebrations",
                                subtitle = if (progressPrefs.celebrationsEnabled) "Show celebrations" else "Quiet mode",
                                checked = progressPrefs.celebrationsEnabled,
                                onCheckedChange = {
                                    ProgressManager.updatePreferences(progressPrefs.copy(celebrationsEnabled = it))
                                }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressable { showResetConfirm = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                                    Text("Reset Progress", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.semanticRed)
                                    Text("Clear all reading progress", fontSize = 12.sp, color = colors.tertiaryText)
                                }
                            }
                        }
                    }
                }

                // Audio
                item {
                    SettingsSection(title = "Audio") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Reciter", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                            Reciter.popularReciters.forEach { reciter ->
                                val selected = AudioManager.selectedReciter.id == reciter.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) colors.accentChip else androidx.compose.ui.graphics.Color.Transparent)
                                        .pressable(depth = 0.98f) { AudioManager.setReciter(reciter) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            reciter.nameEnglish,
                                            fontSize = 14.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) colors.accentColor else colors.primaryText
                                        )
                                        Text(reciter.description, fontSize = 11.sp, color = colors.tertiaryText)
                                    }
                                    if (selected) {
                                        Text(
                                            "✓",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.accentColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Premium
                item {
                    SettingsSection(title = "Premium") {
                        if (PremiumManager.isPremium) {
                            Text(
                                "Premium unlocked. Thank you for supporting Thaqalayn.",
                                fontSize = 14.sp,
                                color = colors.semanticGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.accentGradient)
                                        .pressable { navController.navigate(Routes.paywall()) }
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Unlock Premium",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onAccentText
                                    )
                                }
                                Text(
                                    "Restore purchases",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.accentColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pressable { BillingManager.restorePurchases() }
                                        .padding(vertical = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // About
                item {
                    SettingsSection(title = "About") {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressable { navController.navigate(Routes.TAFSIR_SOURCES) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tafsir Sources", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                                    Text("Books and scholars referenced", fontSize = 12.sp, color = colors.tertiaryText)
                                }
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = colors.tertiaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Thaqalayn for Android", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
                                Text(
                                    "The Holy Quran with 5-layer Shia tafsir in English, Urdu & Arabic.",
                                    fontSize = 12.sp,
                                    color = colors.tertiaryText
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showTimePicker) {
            NotificationTimePickerDialog(
                hour = NotificationManager.preferences.hour,
                minute = NotificationManager.preferences.minute,
                onConfirm = { h, m ->
                    NotificationManager.updatePreferences(
                        NotificationManager.preferences.copy(hour = h, minute = m)
                    )
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

        if (showResetConfirm) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                containerColor = colors.glassSurfaceElevated,
                title = { Text("Reset Progress?", color = colors.primaryText) },
                text = {
                    Text(
                        "This clears all read verses, streaks, badges and sawab. This cannot be undone.",
                        color = colors.secondaryText
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        ProgressManager.resetProgress()
                        showResetConfirm = false
                    }) {
                        Text("Reset", color = colors.semanticRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showResetConfirm = false }) {
                        Text("Cancel", color = colors.accentColor)
                    }
                }
            )
        }
    }
}

@kotlin.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTimePickerDialog(
    hour: Int,
    minute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val state = androidx.compose.material3.rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = false
    )
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.glassSurfaceElevated,
        title = { Text("Notification Time", color = colors.primaryText) },
        text = {
            androidx.compose.material3.TimePicker(state = state)
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text("Set", color = colors.accentColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.secondaryText)
            }
        }
    )
}

/** "9:00 AM" style label (iOS formatTime, .timeStyle = .short). */
private fun formatNotificationTime(hour: Int, minute: Int): String {
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.HOUR_OF_DAY, hour)
    cal.set(java.util.Calendar.MINUTE, minute)
    return java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(cal.time)
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = Theme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
            Text(subtitle, fontSize = 12.sp, color = colors.tertiaryText)
        }
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedTrackColor = colors.accentColor,
                checkedThumbColor = colors.onAccentText,
                uncheckedTrackColor = colors.glassSurfaceRecessed,
                uncheckedThumbColor = colors.tertiaryText,
                uncheckedBorderColor = colors.strokeColor
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressable(depth = 0.98f, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.primaryText)
            Text(subtitle, fontSize = 12.sp, color = colors.tertiaryText)
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = colors.tertiaryText,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor,
            modifier = Modifier.padding(start = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.glassSurface)
                .border(1.dp, colors.strokeColor, shape)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SegmentedPicker(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(colors.glassSurfaceRecessed)
            .border(1.dp, colors.strokeColor, shape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Text(
                text = option,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) colors.onAccentText else colors.secondaryText,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .let {
                        if (selected) it.background(colors.accentGradient)
                        else it
                    }
                    .pressable(depth = 0.97f) { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
