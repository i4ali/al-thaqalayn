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
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Waves
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
import com.thaqalayn.app.data.PropheticParallelsManager
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.model.ParallelCategory
import com.thaqalayn.app.model.PropheticParallel
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Material stand-in for the PropheticParallel.icon SF Symbol. */
private fun sfIcon(name: String): ImageVector = when (name) {
    "arrow.triangle.2.circlepath" -> Icons.Filled.Sync
    "arrow.uturn.backward.circle.fill" -> Icons.AutoMirrored.Filled.Undo
    "banknote" -> Icons.Filled.Payments
    "bubble.left.and.exclamationmark.bubble.right.fill" -> Icons.Filled.Forum
    "clock.badge.questionmark" -> Icons.AutoMirrored.Filled.HelpOutline
    "crown.fill" -> Icons.Filled.WorkspacePremium
    "ear.trianglebadge.exclamationmark" -> Icons.Filled.Hearing
    "figure.2.and.child.holdinghands" -> Icons.Filled.FamilyRestroom
    "figure.stand" -> Icons.Filled.Accessibility
    "figure.stand.line.dotted.figure.stand" -> Icons.Filled.SocialDistance
    "figure.walk.departure" -> Icons.AutoMirrored.Filled.DirectionsWalk
    "figure.walk.motion" -> Icons.AutoMirrored.Filled.DirectionsRun
    "heart.fill" -> Icons.Filled.Favorite
    "heart.slash" -> Icons.Filled.HeartBroken
    "hourglass.bottomhalf.filled" -> Icons.Filled.HourglassBottom
    "person.3.fill" -> Icons.Filled.Groups
    "person.crop.circle.badge.questionmark" -> Icons.Filled.PersonSearch
    "person.fill.xmark" -> Icons.Filled.PersonOff
    "scale.3d" -> Icons.Filled.Balance
    "water.waves" -> Icons.Filled.Waves
    else -> Icons.Filled.Favorite
}

/** Material stand-in for the iOS ParallelCategory.icon SF Symbol. */
private fun parallelCategoryIcon(category: ParallelCategory): ImageVector = when (category) {
    ParallelCategory.EMOTIONAL_STRUGGLES -> Icons.Filled.HeartBroken
    ParallelCategory.FAMILY_CHALLENGES -> Icons.Filled.Home
    ParallelCategory.FAITH_TESTS -> Icons.Filled.LocalFireDepartment
    ParallelCategory.WORLDLY_PRESSURES -> Icons.Filled.Public
    ParallelCategory.ISOLATION -> Icons.Filled.PersonSearch
    ParallelCategory.PERSECUTION -> Icons.Filled.GppBad
}

private fun eyebrow(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "أمثلة الأنبياء"
    CommentaryLanguage.URDU -> "انبیائی مثالیں"
    else -> "Prophetic Parallels"
}

private fun title(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "لستَ وحدك"
    CommentaryLanguage.URDU -> "آپ اکیلے نہیں ہیں"
    else -> "You Aren't Alone"
}

private fun subtitle(language: CommentaryLanguage): String = when (language) {
    CommentaryLanguage.ARABIC -> "قصصُ أنبياءَ ساروا الطريق نفسه"
    CommentaryLanguage.URDU -> "انہی راہوں پر چلنے والے انبیاء کی داستانیں"
    else -> "Stories of Prophets who walked the same road"
}

/** Prophetic parallels list - searchable, category-filtered, grouped by category (iOS PropheticParallelsView). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropheticParallelsScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    val parallels = PropheticParallelsManager.parallels
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ParallelCategory?>(null) }

    // iOS PropheticParallelsManager.search: situation/prophet/connection/story summary across EN/AR/UR.
    val filtered = remember(parallels, searchText, selectedCategory) {
        val query = searchText.trim()
        val searched = if (query.isEmpty()) parallels else parallels.filter { p ->
            listOf(
                p.situationEn, p.situationAr, p.situationUr,
                p.prophetEn, p.prophetAr, p.prophetUr,
                p.connectionEn, p.connectionAr, p.connectionUr,
                p.storySummaryEn, p.storySummaryAr, p.storySummaryUr
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
                        .padding(top = 16.dp, bottom = 14.dp),
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.glassSurface)
                    .border(1.dp, colors.strokeColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(15.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search situations or prophets...",
                            fontSize = 15.sp,
                            color = colors.secondaryText
                        )
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 15.sp, color = colors.primaryText),
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
                ParallelCategoryChip(
                    title = "All",
                    icon = Icons.Filled.GridView,
                    isSelected = selectedCategory == null
                ) { selectedCategory = null }
                ParallelCategory.entries.forEach { category ->
                    ParallelCategoryChip(
                        title = category.displayName,
                        icon = parallelCategoryIcon(category),
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
                        Icons.Filled.Groups,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No parallels found",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                ) {
                    grouped.forEach { (category, categoryParallels) ->
                        stickyHeader(key = "header-${category.name}") {
                            CategoryHeader(category)
                        }
                        items(categoryParallels, key = { it.id }) { parallel ->
                            ParallelCard(parallel = parallel, lang = lang, direction = direction) {
                                navController.navigate(Routes.parallel(parallel.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: ParallelCategory) {
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
            parallelCategoryIcon(category),
            contentDescription = null,
            tint = colors.accentColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = category.displayName.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = colors.accentColor
        )
    }
}

@Composable
private fun ParallelCategoryChip(
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
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) colors.onAccentText else colors.accentColor
        )
    }
}

// Prophet capsule + situation + connection preview + verse count (iOS PropheticParallelCard).
@Composable
private fun ParallelCard(
    parallel: PropheticParallel,
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
                EmIconChip(icon = sfIcon(parallel.icon))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = parallel.prophet(lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = colors.accentColor
                        )
                    }
                    Text(
                        text = parallel.situation(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        color = colors.primaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = parallel.connection(lang),
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${parallel.verses.size} verse${if (parallel.verses.size == 1) "" else "s"} · ${parallel.category.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.tertiaryText
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
