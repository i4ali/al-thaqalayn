package com.thaqalayn.app.ui.onboarding

// Story-driven onboarding flow coordinator (iOS OnboardingFlowView).
// 13 swipeable pages with a dot indicator, a Skip pill on the middle pages,
// and completion that applies the notification opt-ins collected along the way
// (requesting POST_NOTIFICATIONS first when needed) before marking
// hasShownWelcome - the same sequence as iOS completeOnboarding().

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.notifications.NotificationManager
import com.thaqalayn.app.settings.OnboardingManager
import com.thaqalayn.app.ui.components.pressable
import kotlinx.coroutines.launch

private const val TOTAL_PAGES = 13

@Composable
fun OnboardingFlowScreen() {
    val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember { mutableStateOf(false) }
    var progressNotificationsEnabled by remember { mutableStateOf(false) }

    // Applies the collected opt-ins and dismisses. The daily-verse enable is
    // gated on the runtime grant (matching the Settings screen); progress
    // preferences persist regardless - their workers check permission when
    // they arm, exactly like iOS applies them outside the permission block.
    fun applyAndFinish(granted: Boolean) {
        if (notificationsEnabled && granted) {
            NotificationManager.updatePreferences(
                NotificationManager.preferences.copy(enabled = true)
            )
        }
        ProgressManager.updatePreferences(
            ProgressManager.preferences.copy(
                notificationsEnabled = progressNotificationsEnabled,
                celebrationsEnabled = progressNotificationsEnabled
            )
        )
        OnboardingManager.markShown()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> applyAndFinish(granted) }

    fun completeOnboarding() {
        val wantsNotifications = notificationsEnabled || progressNotificationsEnabled
        val needsRequest = wantsNotifications &&
            Build.VERSION.SDK_INT >= 33 &&
            !NotificationManager.hasPermission()
        if (needsRequest) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            applyAndFinish(granted = NotificationManager.hasPermission())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> HadithPage(
                    isCurrent = pagerState.currentPage == 0,
                    onAdvance = { scope.launch { pagerState.animateScrollToPage(1) } }
                )
                1 -> MissionPage()
                2 -> DeepDiveTeaserPage()
                3 -> SurahExperienceTeaserPage()
                4 -> FiveLayersPage()
                5 -> QuickGemsPage()
                6 -> ProgressTrackingPage()
                7 -> QuizFeaturePage()
                8 -> SeasonalFeaturesPage()
                9 -> DailyVersePage(
                    notificationsEnabled = notificationsEnabled,
                    onToggle = { notificationsEnabled = !notificationsEnabled }
                )
                10 -> ProgressNotificationsPage(
                    progressNotificationsEnabled = progressNotificationsEnabled,
                    onToggle = { progressNotificationsEnabled = !progressNotificationsEnabled }
                )
                11 -> PersonalizePage(
                    onContinue = { scope.launch { pagerState.animateScrollToPage(12) } }
                )
                else -> FinalPage(onComplete = { completeOnboarding() })
            }
        }

        // Skip pill - hidden on the auto-advancing first page and the last page.
        if (pagerState.currentPage in 1 until TOTAL_PAGES - 1) {
            Text(
                text = "Skip",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnbPalette.secondaryText,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 10.dp, end = 20.dp)
                    .pressable { completeOnboarding() }
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }

        // Page indicator dots (iOS .page index style).
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 10.dp)
        ) {
            repeat(TOTAL_PAGES) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) OnbPalette.gold
                            else Color.White.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}
