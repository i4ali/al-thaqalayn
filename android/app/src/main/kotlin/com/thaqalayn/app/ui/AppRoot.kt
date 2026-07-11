package com.thaqalayn.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.thaqalayn.app.data.DataManager
import com.thaqalayn.app.data.ProgressManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.bookmarks.BookmarksScreen
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.home.HomeScreen
import com.thaqalayn.app.ui.paywall.PaywallScreen
import com.thaqalayn.app.ui.placeholder.PlaceholderTabScreen
import com.thaqalayn.app.ui.reader.FullScreenCommentaryScreen
import com.thaqalayn.app.ui.reader.SurahDetailScreen
import com.thaqalayn.app.ui.reader.VerseSummaryScreen
import com.thaqalayn.app.ui.settings.SettingsScreen
import com.thaqalayn.app.ui.strings.TabStrings

object Routes {
    const val MAIN = "main"
    const val SURAH = "surah/{number}?verse={verse}"
    const val COMMENTARY = "commentary/{surah}/{verse}"
    const val SUMMARY = "summary/{surah}/{verse}"
    const val BOOKMARKS = "bookmarks"
    const val SETTINGS = "settings"
    const val PAYWALL = "paywall"

    fun surah(number: Int, verse: Int? = null) =
        "surah/$number" + (verse?.let { "?verse=$it" } ?: "")
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    // Preload the surah catalog once at startup (mirrors iOS DataManager preload).
    LaunchedEffect(Unit) {
        ProgressManager.attachSurahs(DataManager.shared.surahs())
    }

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
        composable(Routes.PAYWALL) {
            PaywallScreen(navController)
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
            0 -> PlaceholderTabScreen(TabStrings.today(lang))
            1 -> HomeScreen(navController)
            2 -> PlaceholderTabScreen(TabStrings.explore(lang))
            3 -> PlaceholderTabScreen(TabStrings.progress(lang))
            else -> PlaceholderTabScreen(TabStrings.journey(lang))
        }
        EmeraldTabBar(
            items = items,
            selection = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
