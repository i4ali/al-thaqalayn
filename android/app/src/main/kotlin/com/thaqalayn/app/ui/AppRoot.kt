package com.thaqalayn.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.thaqalayn.app.NotificationDeepLink
import com.thaqalayn.app.NotificationDeepLinks
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.DeepDiveDescriptor
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.data.SurahExperienceDescriptor
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.settings.OnboardingManager
import com.thaqalayn.app.ui.bookmarks.BookmarksScreen
import com.thaqalayn.app.ui.onboarding.OnboardingFlowScreen
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.deepdive.DeepDiveScreen
import com.thaqalayn.app.ui.home.HomeScreen
import com.thaqalayn.app.ui.journey.AllDeepDivesScreen
import com.thaqalayn.app.ui.journey.AllJourneysScreen
import com.thaqalayn.app.ui.journey.AllSurahExperiencesScreen
import com.thaqalayn.app.ui.journey.JourneyDayDetailScreen
import com.thaqalayn.app.ui.journey.JourneyHubScreen
import com.thaqalayn.app.ui.journey.JourneyScreen
import com.thaqalayn.app.ui.journey.VeiledDayPreviewScreen
import com.thaqalayn.app.ui.paywall.PaywallScreen
import com.thaqalayn.app.ui.progress.ProgressScreen
import com.thaqalayn.app.ui.quiz.QuizScreen
import com.thaqalayn.app.ui.reader.FullScreenCommentaryScreen
import com.thaqalayn.app.ui.reader.SurahDetailScreen
import com.thaqalayn.app.ui.reader.VerseSummaryScreen
import com.thaqalayn.app.ui.notifications.NotificationsScreen
import com.thaqalayn.app.ui.settings.SettingsScreen
import com.thaqalayn.app.ui.settings.TafsirSourcesScreen
import com.thaqalayn.app.ui.strings.TabStrings
import com.thaqalayn.app.ui.dua.DuaDetailScreen
import com.thaqalayn.app.ui.dua.DuasZiyaratScreen
import com.thaqalayn.app.ui.dua.SpecialDuaDetailScreen
import com.thaqalayn.app.audio.DuaStreamPlayer
import com.thaqalayn.app.ui.components.DuaMiniPlayer
import com.thaqalayn.app.ui.explore.AhlulbaytEntryDetailScreen
import com.thaqalayn.app.ui.explore.AhlulbaytQuranScreen
import com.thaqalayn.app.ui.explore.DuasScreen
import com.thaqalayn.app.ui.explore.ExploreScreen
import com.thaqalayn.app.ui.explore.FastingCategoryDetailScreen
import com.thaqalayn.app.ui.explore.FastingVersesScreen
import com.thaqalayn.app.ui.explore.FoodDetailScreen
import com.thaqalayn.app.ui.explore.FoodsScreen
import com.thaqalayn.app.ui.explore.LifeMomentDetailScreen
import com.thaqalayn.app.ui.explore.LifeMomentsScreen
import com.thaqalayn.app.ui.explore.ParallelDetailScreen
import com.thaqalayn.app.ui.explore.PropheticParallelsScreen
import com.thaqalayn.app.ui.explore.PropheticStoriesScreen
import com.thaqalayn.app.ui.explore.StoryDetailScreen
import com.thaqalayn.app.ui.today.DailyChallengeScreen
import com.thaqalayn.app.ui.today.DailyCrosswordScreen
import com.thaqalayn.app.ui.today.TodayScreen

object Routes {
    const val MAIN = "main"
    const val SURAH = "surah/{number}?verse={verse}"
    const val COMMENTARY = "commentary/{surah}/{verse}"
    const val SUMMARY = "summary/{surah}/{verse}"
    const val BOOKMARKS = "bookmarks"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val PAYWALL = "paywall?cover={cover}"
    const val CHALLENGE = "challenge"
    const val CROSSWORD = "crossword"
    const val DUA = "dua/{id}"
    const val SPECIAL_DUA = "specialDua/{id}"
    const val QUIZ = "quiz/{surah}"

    // Explore tab destinations
    const val DUAS = "duas"
    const val DUAS_ZIYARAT = "duasZiyarat"
    const val LIFE_MOMENTS = "lifeMoments"
    const val LIFE_MOMENT = "lifeMoment/{id}"
    const val FOODS = "foods"
    const val FOOD = "food/{id}"
    const val FASTING = "fasting"
    const val FASTING_CATEGORY = "fastingCategory/{id}"
    const val STORIES = "stories"
    const val STORY = "story/{id}"
    const val PARALLELS = "parallels"
    const val PARALLEL = "parallel/{id}"
    const val AHLULBAYT = "ahlulbayt"
    const val AHLULBAYT_ENTRY = "ahlulbaytEntry/{id}"
    const val TAFSIR_SOURCES = "tafsirSources"

    // Journey tab destinations
    const val JOURNEYS_ALL = "journeysAll"
    const val JOURNEY = "journey/{id}"
    const val JOURNEY_DAY = "journeyDay/{id}/{day}"
    const val JOURNEY_DAY_PREVIEW = "journeyDayPreview/{id}/{day}"
    const val DEEP_DIVES_ALL = "deepDivesAll"
    const val DEEP_DIVE = "deepDive/{id}"
    const val SURAH_EXPERIENCES_ALL = "surahExperiencesAll"
    const val SURAH_EXPERIENCE = "surahExperience/{id}"

    fun surah(number: Int, verse: Int? = null) =
        "surah/$number" + (verse?.let { "?verse=$it" } ?: "")

    /**
     * Paywall, optionally carrying the locked entry's cover as hero context
     * (a journey / deep-dive / surah-experience id; see paywallContextCover).
     */
    fun paywall(coverKey: String? = null) =
        "paywall" + (coverKey?.let { "?cover=$it" } ?: "")

    fun quiz(surah: Int) = "quiz/$surah"

    fun dua(id: String) = "dua/$id"
    fun specialDua(id: String) = "specialDua/$id"
    fun journey(id: String) = "journey/$id"
    fun journeyDay(id: String, day: Int) = "journeyDay/$id/$day"
    fun journeyDayPreview(id: String, day: Int) = "journeyDayPreview/$id/$day"
    fun deepDive(id: String) = "deepDive/$id"
    fun surahExperience(id: String) = "surahExperience/$id"
    fun lifeMoment(id: String) = "lifeMoment/$id"
    fun food(id: String) = "food/$id"
    fun fastingCategory(id: String) = "fastingCategory/$id"
    fun story(id: String) = "story/$id"
    fun parallel(id: String) = "parallel/$id"
    fun ahlulbaytEntry(id: String) = "ahlulbaytEntry/$id"
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    // Preload the surah catalog once at startup (mirrors iOS DataManager preload).
    LaunchedEffect(Unit) {
        ProgressManager.attachSurahs(DataManager.shared.surahs())
    }

    // Notification-tap deep links (iOS NavigateToVerse / NavigateToJourney).
    val pendingDeepLink = NotificationDeepLinks.pending
    LaunchedEffect(pendingDeepLink) {
        when (pendingDeepLink) {
            is NotificationDeepLink.Verse ->
                navController.navigate(Routes.surah(pendingDeepLink.surah, pendingDeepLink.verse))
            is NotificationDeepLink.Journey ->
                navController.navigate(Routes.journey(pendingDeepLink.journeyId))
            null -> {}
        }
        if (pendingDeepLink != null) NotificationDeepLinks.clear()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavHost(navController)
        // First-launch onboarding drawn over the app (iOS fullScreenCover);
        // completion flips hasShownWelcome and reveals the app beneath.
        if (!OnboardingManager.hasShownWelcome) {
            OnboardingFlowScreen()
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        composable(Routes.MAIN) {
            MainTabs(navController)
        }
        composable(
            route = Routes.SURAH,
            arguments = listOf(
                navArgument("number") { type = NavType.IntType },
                navArgument("verse") { type = NavType.IntType; defaultValue = -1 }
            )
        ) { entry ->
            val number = entry.arguments?.getInt("number") ?: 1
            val verse = entry.arguments?.getInt("verse")?.takeIf { it > 0 }
            SurahDetailScreen(
                surahNumber = number,
                targetVerse = verse,
                navController = navController
            )
        }
        composable(
            route = Routes.COMMENTARY,
            arguments = listOf(
                navArgument("surah") { type = NavType.IntType },
                navArgument("verse") { type = NavType.IntType }
            )
        ) { entry ->
            FullScreenCommentaryScreen(
                surahNumber = entry.arguments?.getInt("surah") ?: 1,
                verseNumber = entry.arguments?.getInt("verse") ?: 1,
                navController = navController
            )
        }
        composable(
            route = Routes.SUMMARY,
            arguments = listOf(
                navArgument("surah") { type = NavType.IntType },
                navArgument("verse") { type = NavType.IntType }
            )
        ) { entry ->
            VerseSummaryScreen(
                surahNumber = entry.arguments?.getInt("surah") ?: 1,
                verseNumber = entry.arguments?.getInt("verse") ?: 1,
                navController = navController
            )
        }
        composable(Routes.BOOKMARKS) {
            BookmarksScreen(navController)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController)
        }
        composable(Routes.TAFSIR_SOURCES) {
            TafsirSourcesScreen(navController)
        }
        composable(Routes.JOURNEYS_ALL) {
            AllJourneysScreen(navController)
        }
        composable(
            route = Routes.JOURNEY,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            JourneyScreen(
                journeyId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(
            route = Routes.JOURNEY_DAY_PREVIEW,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("day") { type = NavType.IntType }
            )
        ) { entry ->
            VeiledDayPreviewScreen(
                journeyId = entry.arguments?.getString("id") ?: "",
                dayNumber = entry.arguments?.getInt("day") ?: 1,
                navController = navController
            )
        }
        composable(
            route = Routes.JOURNEY_DAY,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("day") { type = NavType.IntType }
            )
        ) { entry ->
            JourneyDayDetailScreen(
                journeyId = entry.arguments?.getString("id") ?: "",
                dayNumber = entry.arguments?.getInt("day") ?: 1,
                navController = navController
            )
        }
        composable(Routes.DEEP_DIVES_ALL) {
            AllDeepDivesScreen(navController)
        }
        composable(Routes.SURAH_EXPERIENCES_ALL) {
            AllSurahExperiencesScreen(navController)
        }
        composable(
            route = Routes.DEEP_DIVE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            val descriptor = DeepDiveDescriptor.byId(id)
            descriptor?.dive?.let { dive ->
                DeepDiveScreen(
                    dive = dive,
                    onClose = { navController.popBackStack() },
                    coverRes = descriptor.coverRes,
                    // Non-subscribers preview the opening beats, then the veil.
                    locked = !PremiumManager.canAccessDeepDive(descriptor.id),
                    onUnlock = { navController.navigate(Routes.paywall(coverKey = descriptor.id)) }
                )
            }
        }
        composable(
            route = Routes.SURAH_EXPERIENCE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            val descriptor = SurahExperienceDescriptor.byId(id)
            descriptor?.dive?.let { dive ->
                DeepDiveScreen(
                    dive = dive,
                    onClose = { navController.popBackStack() },
                    onReadSurah = {
                        // Dismiss the descent, then hand off to the surah reader
                        // (iOS posts .navigateToVerse; here we just swap screens).
                        navController.popBackStack()
                        navController.navigate(Routes.surah(descriptor.surahNumber, 1))
                    },
                    coverRes = descriptor.coverRes,
                    locked = !PremiumManager.canAccessSurahExperience(descriptor.id),
                    onUnlock = { navController.navigate(Routes.paywall(coverKey = descriptor.id)) }
                )
            }
        }
        composable(Routes.PARALLELS) {
            PropheticParallelsScreen(navController)
        }
        composable(
            route = Routes.PARALLEL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            ParallelDetailScreen(
                parallelId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.AHLULBAYT) {
            AhlulbaytQuranScreen(navController)
        }
        composable(
            route = Routes.AHLULBAYT_ENTRY,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            AhlulbaytEntryDetailScreen(
                entryId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(
            route = Routes.PAYWALL,
            arguments = listOf(
                navArgument("cover") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            PaywallScreen(
                navController = navController,
                contextCoverKey = entry.arguments?.getString("cover")
            )
        }
        composable(Routes.CHALLENGE) {
            DailyChallengeScreen(navController)
        }
        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument("surah") { type = NavType.IntType })
        ) { entry ->
            QuizScreen(
                surahNumber = entry.arguments?.getInt("surah") ?: 1,
                navController = navController
            )
        }
        composable(Routes.CROSSWORD) {
            DailyCrosswordScreen(navController)
        }
        composable(
            route = Routes.DUA,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            DuaDetailScreen(
                duaId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.DUAS) {
            DuasScreen(navController)
        }
        composable(Routes.DUAS_ZIYARAT) {
            DuasZiyaratScreen(navController)
        }
        composable(
            route = Routes.SPECIAL_DUA,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            SpecialDuaDetailScreen(
                duaId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.LIFE_MOMENTS) {
            LifeMomentsScreen(navController)
        }
        composable(
            route = Routes.LIFE_MOMENT,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            LifeMomentDetailScreen(
                momentId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.FOODS) {
            FoodsScreen(navController)
        }
        composable(
            route = Routes.FOOD,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            FoodDetailScreen(
                foodId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.STORIES) {
            PropheticStoriesScreen(navController)
        }
        composable(
            route = Routes.STORY,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            StoryDetailScreen(
                storyId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
        composable(Routes.FASTING) {
            FastingVersesScreen(navController)
        }
        composable(
            route = Routes.FASTING_CATEGORY,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            FastingCategoryDetailScreen(
                categoryId = entry.arguments?.getString("id") ?: "",
                navController = navController
            )
        }
    }
}

@Composable
private fun MainTabs(navController: NavHostController) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val lang = CommentaryLanguageManager.selectedLanguage

    val items = listOf(
        EmeraldTabItem(0, TabStrings.today(lang), Icons.Outlined.WbSunny),
        EmeraldTabItem(1, TabStrings.quran(lang), Icons.Outlined.MenuBook),
        EmeraldTabItem(2, TabStrings.explore(lang), Icons.Outlined.AutoAwesome),
        EmeraldTabItem(3, TabStrings.progress(lang), Icons.Outlined.BarChart),
        EmeraldTabItem(4, TabStrings.journey(lang), Icons.Outlined.Map)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        when (selectedTab) {
            0 -> TodayScreen(navController)
            1 -> HomeScreen(navController)
            2 -> ExploreScreen(navController)
            3 -> ProgressScreen(navController)
            else -> JourneyHubScreen(navController)
        }
        EmeraldTabBar(
            items = items,
            selection = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // Docked recitation mini-player: rides above the tab bar whenever a Duas &
        // Ziyarat stream is loaded but its reader isn't on top (iOS MainTabView).
        if (DuaStreamPlayer.currentDua != null) {
            DuaMiniPlayer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 84.dp)
            ) { dua -> navController.navigate(Routes.specialDua(dua.id)) }
        }
    }
}
