package com.thaqalayn.app.ui.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.PropheticStoriesManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.PropheticStory
import com.thaqalayn.app.model.StoryCategory
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-ins for the iOS StoryCategory SF Symbol icons. */
internal fun storyCategoryIcon(category: StoryCategory): ImageVector = when (category) {
    StoryCategory.PATIENCE -> Icons.Filled.Schedule
    StoryCategory.COURAGE -> Icons.Filled.Shield
    StoryCategory.FAITH -> Icons.Filled.Star
    StoryCategory.SACRIFICE -> Icons.Filled.Favorite
    StoryCategory.LEADERSHIP -> Icons.Filled.WorkspacePremium
    StoryCategory.WISDOM -> Icons.Filled.Psychology
}

private fun eyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "من القرآن"
    CommentaryLanguage.URDU -> "قرآن سے"
    else -> "From the Qur'an"
}

private fun title(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "قصص الأنبياء"
    CommentaryLanguage.URDU -> "انبیاء کے قصے"
    else -> "Prophetic Stories"
}

private fun subtitle(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "روايات قرآنية عن الرسل"
    CommentaryLanguage.URDU -> "رسولوں کے قرآنی واقعات"
    else -> "Quranic accounts of the messengers"
}

/** Prophetic stories list - searchable, category-filtered, grouped by category (iOS PropheticStoriesView). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropheticStoriesScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val stories = PropheticStoriesManager.stories
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<StoryCategory?>(null) }

    // iOS PropheticStoriesManager.search: match query against titles/prophets/short titles in all languages.
    val filtered = remember(stories, searchText, selectedCategory) {
        val query = searchText.trim()
        val searched = if (query.isEmpty()) stories else stories.filter { story ->
            listOfNotNull(
                story.titleEn, story.titleAr, story.titleUr,
                story.prophetEn, story.prophetAr, story.prophetUr,
                story.shortTitleEn, story.shortTitleAr, story.shortTitleUr
            ).any { it.contains(query, ignoreCase = true) }
        }
        val category = selectedCategory
        if (category != null) searched.filter { it.category == category } else searched
    }
    val grouped = remember(filtered) {
        filtered.groupBy { it.category }.entries.sortedBy { it.key.displayName }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Back
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp)
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
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = eyebrow(lang).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang.isRTL) 0.sp else 3.sp,
                        color = colors.accentColor
                    )
                    Text(
                        text = title(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 36.sp,
                        lineHeight = 40.sp,
                        color = colors.primaryText
                    )
                    Text(
                        text = subtitle(lang),
                        fontSize = 13.5.sp,
                        color = colors.secondaryText
                    )
                }
            }

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.glassSurface)
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search stories or prophets...",
                            fontSize = 16.sp,
                            color = colors.secondaryText
                        )
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 16.sp, color = colors.primaryText),
                        cursorBrush = SolidColor(colors.accentColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Category filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StoryCategoryChip(
                    title = "All",
                    icon = Icons.Filled.GridView,
                    isSelected = selectedCategory == null
                ) { selectedCategory = null }
                StoryCategory.entries.forEach { category ->
                    StoryCategoryChip(
                        title = category.displayName,
                        icon = storyCategoryIcon(category),
                        isSelected = selectedCategory == category
                    ) { selectedCategory = category }
                }
            }

            if (filtered.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Icon(
                        Icons.Filled.Book,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No stories found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )
                    Text(
                        text = "Try adjusting your search or category filter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                ) {
                    grouped.forEach { (category, categoryStories) ->
                        stickyHeader(key = "header-${category.name}") {
                            CategoryHeader(category)
                        }
                        items(categoryStories, key = { it.id }) { story ->
                            StoryCard(story = story, lang = lang, direction = direction) {
                                navController.navigate(Routes.story(story.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: StoryCategory) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.primaryBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            storyCategoryIcon(category),
            contentDescription = null,
            tint = colors.accentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = category.displayName,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = colors.primaryText
        )
    }
}

@Composable
private fun StoryCategoryChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .let {
                if (isSelected) it.background(colors.accentGradient)
                else it.background(colors.accentChip).border(1.dp, colors.strokeColor, CircleShape)
            }
            .pressable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) colors.onAccentText else colors.accentColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) colors.onAccentText else colors.accentColor
        )
    }
}

@Composable
private fun StoryCard(
    story: PropheticStory,
    lang: CommentaryLanguage,
    direction: LayoutDirection,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    EmCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressable(onClick = onClick)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                EmIconChip(icon = storyCategoryIcon(story.category))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = story.prophet(lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = colors.accentColor
                    )
                    Text(
                        text = story.title(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        color = colors.primaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${story.verseCount} verse${if (story.verseCount == 1) "" else "s"} · ${story.category.displayName}",
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.tertiaryText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
