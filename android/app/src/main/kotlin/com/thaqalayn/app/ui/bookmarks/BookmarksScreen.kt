package com.thaqalayn.app.ui.bookmarks

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.R
import com.thaqalayn.app.data.BookmarkManager
import com.thaqalayn.app.model.Bookmark
import com.thaqalayn.app.model.BookmarkSortOrder
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.PhosphorIcon
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.Theme

/** Saved verses (iOS BookmarksView), local-only on Android. */
@Composable
fun BookmarksScreen(navController: NavHostController) {
    val colors = Theme.colors
    val bookmarks = BookmarkManager.sortedBookmarks(BookmarkSortOrder.DATE_DESCENDING)

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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 60.dp
                )
            ) {
                item {
                    EmHeading(eyebrow = "Saved Verses", title = "Bookmarks")
                }
                item {
                    EmDivider(label = "${bookmarks.size} of ${BookmarkManager.BOOKMARK_LIMIT}")
                }
                if (bookmarks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PhosphorIcon(resId = R.drawable.ph_heart_fill, size = 32.dp, tint = colors.tertiaryText)
                            Text(
                                "No bookmarks yet",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.secondaryText
                            )
                            Text(
                                "Tap the heart on any verse to save it here.",
                                fontSize = 14.sp,
                                color = colors.tertiaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(bookmarks, key = { it.id }) { bookmark ->
                        BookmarkCard(bookmark = bookmark) {
                            navController.navigate(Routes.surah(bookmark.surahNumber, bookmark.verseNumber))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkCard(bookmark: Bookmark, onOpen: () -> Unit) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)
    val translation = rememberBookmarkTranslation(bookmark)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, shape)
            .pressable(depth = 0.97f, onClick = onOpen)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${bookmark.surahName.uppercase()} · ${bookmark.verseReference}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = colors.accentColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Remove bookmark",
                tint = colors.tertiaryText,
                modifier = Modifier
                    .size(28.dp)
                    .pressable { BookmarkManager.removeBookmark(bookmark.id) }
                    .padding(5.dp)
            )
        }
        Text(
            text = bookmark.verseText,
            fontFamily = AmiriFamily,
            fontSize = 20.sp,
            lineHeight = 34.sp,
            color = colors.primaryText,
            textAlign = TextAlign.End,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = translation,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colors.secondaryText,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
