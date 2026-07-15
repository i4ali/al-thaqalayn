package com.thaqalayn.app.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

// MARK: - Data model (iOS ExploreView.ExploreSection / ExploreItem)

private data class ExploreItem(
    val id: String,
    val icon: ImageVector,
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val subtitleUr: String,
    val route: String
) {
    fun title(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> titleAr
        CommentaryLanguage.URDU -> titleUr
        else -> titleEn
    }

    fun subtitle(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> subtitleAr
        CommentaryLanguage.URDU -> subtitleUr
        else -> subtitleEn
    }
}

private class ExploreSection(
    val titleEn: String,
    val titleAr: String,
    val titleUr: String,
    val items: List<ExploreItem>
) {
    fun title(language: CommentaryLanguage): String = when (language) {
        CommentaryLanguage.ARABIC -> titleAr
        CommentaryLanguage.URDU -> titleUr
        else -> titleEn
    }
}

private val exploreSections = listOf(
    ExploreSection(
        titleEn = "Life & Guidance",
        titleAr = "الحياة والهداية",
        titleUr = "زندگی و رہنمائی",
        items = listOf(
            ExploreItem(
                id = "lifeMoments",
                icon = Icons.Filled.Favorite,
                titleEn = "Life Moments",
                titleAr = "لحظات الحياة",
                titleUr = "زندگی کے لمحات",
                subtitleEn = "Find solace for any situation",
                subtitleAr = "اعثر على السكينة في كل حال",
                subtitleUr = "ہر حال میں سکون پائیں",
                route = Routes.LIFE_MOMENTS
            ),
            ExploreItem(
                id = "dailyDuas",
                icon = Icons.Filled.VolunteerActivism,
                titleEn = "Daily Duas",
                titleAr = "أدعية يومية",
                titleUr = "روزمرہ دعائیں",
                subtitleEn = "20 supplications for everyday moments",
                subtitleAr = "20 دعاءً للحظات اليومية",
                subtitleUr = "روزمرہ لمحات کے لیے 20 دعائیں",
                route = Routes.DUAS
            ),
            ExploreItem(
                id = "foods",
                icon = Icons.Filled.Spa,
                titleEn = "Foods of the Quran",
                titleAr = "أطعمة القرآن",
                titleUr = "قرآن کی غذائیں",
                subtitleEn = "Nourishment from Qur'an & Ahlul Bayt",
                subtitleAr = "غذاءٌ من القرآن وأهل البيت (ع)",
                subtitleUr = "قرآن اور اہلِ بیت سے غذا",
                route = Routes.FOODS
            ),
            ExploreItem(
                id = "fasting",
                icon = Icons.Filled.DarkMode,
                titleEn = "Fasting in the Quran",
                titleAr = "الصيام في القرآن",
                titleUr = "قرآن میں روزہ",
                subtitleEn = "Verses about fasting & Ramadan",
                subtitleAr = "آياتٌ عن الصيام ورمضان",
                subtitleUr = "روزے اور رمضان سے متعلق آیات",
                route = Routes.FASTING
            )
        )
    ),
    ExploreSection(
        titleEn = "Stories & Figures",
        titleAr = "القصص والشخصيات",
        titleUr = "قصے اور شخصیات",
        items = listOf(
            ExploreItem(
                id = "propheticStories",
                icon = Icons.Outlined.MenuBook,
                titleEn = "Prophetic Stories",
                titleAr = "قصص الأنبياء",
                titleUr = "انبیاء کے قصے",
                subtitleEn = "Accounts of the messengers",
                subtitleAr = "سِيَر الرسل",
                subtitleUr = "رسولوں کے واقعات",
                route = Routes.STORIES
            ),
            ExploreItem(
                id = "propheticParallels",
                icon = Icons.Filled.Groups,
                titleEn = "Prophetic Parallels",
                titleAr = "أمثلة الأنبياء",
                titleUr = "انبیائی مثالیں",
                subtitleEn = "You aren't alone in your struggles",
                subtitleAr = "لستَ وحدك في محنتك",
                subtitleUr = "اپنی آزمائشوں میں آپ اکیلے نہیں",
                route = Routes.PARALLELS
            ),
            ExploreItem(
                id = "ahlulbaytQuran",
                icon = Icons.Filled.Star,
                titleEn = "Ahl al-Bayt in Quran",
                titleAr = "أهل البيت في القرآن",
                titleUr = "قرآن میں اہلِ بیت",
                subtitleEn = "Verses honoring the family",
                subtitleAr = "آياتٌ في فضل آل النبي (ص)",
                subtitleUr = "آلِ رسول کی شان میں آیات",
                route = Routes.AHLULBAYT
            )
        )
    )
)

private fun eyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "اكتشف"
    CommentaryLanguage.URDU -> "دریافت"
    else -> "Discover"
}

private fun title(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "استكشف"
    CommentaryLanguage.URDU -> "تلاش کریں"
    else -> "Explore"
}

private fun subtitle(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "تأمّل حكمة القرآن الكريم"
    CommentaryLanguage.URDU -> "قرآنی حکمت پر غور کریں"
    else -> "Discover Quranic Wisdom"
}

// MARK: - View (iOS EmeraldExploreView)

/** The Explore tab: table-of-contents hub for the discovery features. */
@Composable
fun ExploreScreen(navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp)
        ) {
            item {
                EmHeading(
                    eyebrow = eyebrow(lang),
                    title = title(lang),
                    sub = subtitle(lang)
                )
            }

            exploreSections.forEach { section ->
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        EmDivider(label = section.title(lang))
                        section.items.forEach { rowItem ->
                            ExploreRow(
                                item = rowItem,
                                lang = lang,
                                onClick = { navController.navigate(rowItem.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreRow(item: ExploreItem, lang: CommentaryLanguage, onClick: () -> Unit) {
    val colors = Theme.colors
    EmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmIconChip(icon = item.icon, size = 44.dp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title(lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp,
                    color = colors.primaryText
                )
                Text(
                    text = item.subtitle(lang),
                    fontSize = 12.5.sp,
                    color = colors.tertiaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.tertiaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
