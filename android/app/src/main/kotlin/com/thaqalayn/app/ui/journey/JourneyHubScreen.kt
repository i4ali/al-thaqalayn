package com.thaqalayn.app.ui.journey

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassEmpty
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.thaqalayn.app.data.DeepDiveDescriptor
import com.thaqalayn.app.data.JourneyDescriptor
import com.thaqalayn.app.data.JourneyStatus
import com.thaqalayn.app.data.SurahExperienceDescriptor
import com.thaqalayn.app.model.CommentaryLanguage
import com.thaqalayn.app.premium.PremiumManager
import com.thaqalayn.app.settings.CommentaryLanguageManager
import com.thaqalayn.app.ui.Routes
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmGoldCTA
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmIconChip
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.strings.JourneyStrings
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Content of the overlay shown when a locked (non-active) journey is tapped. */
private data class LockedJourneyAlert(
    val title: String,
    val detail: String,
    val pointer: String?
)

/**
 * The Journey tab (iOS JourneyHubView): three shelves - Sacred Seasons (only
 * active journeys open), Deep Dives, and Inside the Surah (available entries
 * open their descent; premium-gated taps go to the paywall; coming-soon taps
 * reuse the locked overlay).
 */
@Composable
fun JourneyHubScreen(navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    var lockedAlert by remember { mutableStateOf<LockedJourneyAlert?>(null) }
    // Status depends on today's Hijri date + language; both are stable within a visit.
    val ordered = remember(lang) { JourneyDescriptor.orderedByStatus() }

    // System back dismisses the locked-journey overlay first.
    BackHandler(enabled = lockedAlert != null) { lockedAlert = null }

    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 12.dp, bottom = 120.dp)
            ) {
                EmHeading(
                    eyebrow = JourneyStrings.grow(lang),
                    title = JourneyStrings.journeys(lang),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                SacredSeasonsShelf(
                    ordered = ordered,
                    lang = lang,
                    modifier = Modifier.padding(top = 20.dp),
                    onSeeAll = { navController.navigate(Routes.JOURNEYS_ALL) },
                    onTap = { descriptor, status ->
                        handleJourneyTap(descriptor, status, lang, navController) { lockedAlert = it }
                    }
                )

                DiveShelf(
                    label = JourneyStrings.deepDives(lang),
                    items = DeepDiveDescriptor.all.map { d ->
                        DiveShelfItem(
                            icon = d.icon,
                            available = d.available,
                            status = diveShelfStatus(d.available, PremiumManager.canAccessDeepDive(d.id), lang),
                            title = d.title.text(lang),
                            description = d.subtitle.text(lang),
                            onTap = { handleDiveTap(d, lang, navController) { lockedAlert = it } }
                        )
                    },
                    lang = lang,
                    modifier = Modifier.padding(top = 24.dp),
                    onSeeAll = { navController.navigate(Routes.DEEP_DIVES_ALL) }
                )

                DiveShelf(
                    label = JourneyStrings.insideTheSurah(lang),
                    items = SurahExperienceDescriptor.all.map { d ->
                        DiveShelfItem(
                            icon = d.icon,
                            available = d.available,
                            status = diveShelfStatus(d.available, PremiumManager.canAccessSurahExperience(d.id), lang),
                            title = d.title.text(lang),
                            description = d.subtitle.text(lang),
                            onTap = { handleSurahExperienceTap(d, lang, navController) { lockedAlert = it } }
                        )
                    },
                    lang = lang,
                    modifier = Modifier.padding(top = 24.dp),
                    onSeeAll = { navController.navigate(Routes.SURAH_EXPERIENCES_ALL) }
                )
            }
        }

        lockedAlert?.let { alert ->
            LockedJourneyOverlay(alert = alert, lang = lang) { lockedAlert = null }
        }
    }
}

/** The "All 5" full list pushed from the shelf header (iOS SectionFullList). */
@Composable
fun AllJourneysScreen(navController: NavHostController) {
    val colors = Theme.colors
    val lang = CommentaryLanguageManager.selectedLanguage
    var lockedAlert by remember { mutableStateOf<LockedJourneyAlert?>(null) }
    val ordered = remember(lang) { JourneyDescriptor.orderedByStatus() }
    val nextUpId = nextUpId(ordered)

    // System back dismisses the locked-journey overlay first.
    BackHandler(enabled = lockedAlert != null) { lockedAlert = null }

    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        com.thaqalayn.app.ui.components.ThemedBackground()
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = JourneyStrings.sacredSeasons(lang),
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = colors.primaryText
                    )
                }

                ordered.forEach { (descriptor, status) ->
                    JourneyCard(
                        descriptor = descriptor,
                        status = status,
                        isNextUp = descriptor.id == nextUpId,
                        lang = lang,
                        onTap = {
                            handleJourneyTap(descriptor, status, lang, navController) { lockedAlert = it }
                        }
                    )
                }
            }
        }

        lockedAlert?.let { alert ->
            LockedJourneyOverlay(alert = alert, lang = lang) { lockedAlert = null }
        }
    }
}

/** The "All N" full list for Deep Dives (iOS SectionFullList). */
@Composable
fun AllDeepDivesScreen(navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    var lockedAlert by remember { mutableStateOf<LockedJourneyAlert?>(null) }
    BackHandler(enabled = lockedAlert != null) { lockedAlert = null }

    SectionFullList(
        title = JourneyStrings.deepDives(lang),
        lang = lang,
        navController = navController,
        lockedAlert = lockedAlert,
        onDismissAlert = { lockedAlert = null }
    ) {
        DeepDiveDescriptor.all.forEach { d ->
            DiveCard(
                icon = d.icon,
                eyebrow = JourneyStrings.deepDiveEyebrow(lang),
                locked = d.available && !PremiumManager.canAccessDeepDive(d.id),
                title = d.title.text(lang),
                subtitle = d.subtitle.text(lang),
                available = d.available,
                lang = lang,
                onTap = { handleDiveTap(d, lang, navController) { lockedAlert = it } }
            )
        }
    }
}

/** The "All N" full list for Inside the Surah experiences (iOS SectionFullList). */
@Composable
fun AllSurahExperiencesScreen(navController: NavHostController) {
    val lang = CommentaryLanguageManager.selectedLanguage
    var lockedAlert by remember { mutableStateOf<LockedJourneyAlert?>(null) }
    BackHandler(enabled = lockedAlert != null) { lockedAlert = null }

    SectionFullList(
        title = JourneyStrings.insideTheSurah(lang),
        lang = lang,
        navController = navController,
        lockedAlert = lockedAlert,
        onDismissAlert = { lockedAlert = null }
    ) {
        SurahExperienceDescriptor.all.forEach { d ->
            DiveCard(
                icon = d.icon,
                eyebrow = JourneyStrings.surahJourneyEyebrow(lang),
                locked = d.available && !PremiumManager.canAccessSurahExperience(d.id),
                title = d.title.text(lang),
                subtitle = d.subtitle.text(lang),
                available = d.available,
                lang = lang,
                onTap = { handleSurahExperienceTap(d, lang, navController) { lockedAlert = it } }
            )
        }
    }
}

/** Shared scaffold for a pushed "All N" section list: back chip + serif title. */
@Composable
private fun SectionFullList(
    title: String,
    lang: CommentaryLanguage,
    navController: NavHostController,
    lockedAlert: LockedJourneyAlert?,
    onDismissAlert: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val direction = if (lang.isRTL) LayoutDirection.Rtl else LayoutDirection.Ltr

    Box(modifier = Modifier.fillMaxSize()) {
        com.thaqalayn.app.ui.components.ThemedBackground()
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .pressable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = title,
                        fontFamily = CormorantFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = colors.primaryText
                    )
                }
                content()
            }
        }

        lockedAlert?.let { alert ->
            LockedJourneyOverlay(alert = alert, lang = lang, onDismiss = onDismissAlert)
        }
    }
}

/** Shelf status eyebrow for a dive/experience card: READY | PREMIUM | SOON. */
private fun diveShelfStatus(available: Boolean, canAccess: Boolean, lang: CommentaryLanguage): String =
    when {
        available && canAccess -> JourneyStrings.ready(lang)
        available -> JourneyStrings.premium(lang)
        else -> JourneyStrings.soon(lang)
    }

/**
 * Available dives open their full-screen descent; premium-gated taps go to the
 * paywall; coming-soon dives reuse the locked overlay with an "on its way" note.
 */
private fun handleDiveTap(
    d: DeepDiveDescriptor,
    lang: CommentaryLanguage,
    navController: NavHostController,
    showAlert: (LockedJourneyAlert) -> Unit
) {
    if (d.available) {
        if (PremiumManager.canAccessDeepDive(d.id)) {
            navController.navigate(Routes.deepDive(d.id))
        } else {
            navController.navigate(Routes.PAYWALL)
        }
    } else {
        showAlert(
            LockedJourneyAlert(
                title = JourneyStrings.comingSoon(lang),
                detail = JourneyStrings.deepDiveOnItsWay(d.title.text(lang), lang),
                pointer = null
            )
        )
    }
}

/** Same handling for Inside-the-Surah experiences. */
private fun handleSurahExperienceTap(
    d: SurahExperienceDescriptor,
    lang: CommentaryLanguage,
    navController: NavHostController,
    showAlert: (LockedJourneyAlert) -> Unit
) {
    if (d.available) {
        if (PremiumManager.canAccessSurahExperience(d.id)) {
            navController.navigate(Routes.surahExperience(d.id))
        } else {
            navController.navigate(Routes.PAYWALL)
        }
    } else {
        showAlert(
            LockedJourneyAlert(
                title = JourneyStrings.comingSoon(lang),
                detail = JourneyStrings.deepDiveOnItsWay(d.title.text(lang), lang),
                pointer = null
            )
        )
    }
}

/**
 * The journey to flag as "next up": the soonest journey to open - but only
 * when nothing is currently active (an active card is the sole highlight).
 */
private fun nextUpId(ordered: List<Pair<JourneyDescriptor, JourneyStatus>>): String? {
    if (ordered.any { it.second.isActive }) return null
    return ordered.firstOrNull()?.first?.id
}

private fun handleJourneyTap(
    descriptor: JourneyDescriptor,
    status: JourneyStatus,
    lang: CommentaryLanguage,
    navController: NavHostController,
    showAlert: (LockedJourneyAlert) -> Unit
) {
    if (status.isActive) {
        navController.navigate(Routes.journey(descriptor.id))
        return
    }
    val title = JourneyStrings.title(descriptor.id, lang)
    val (alertTitle, detail) = when (status) {
        is JourneyStatus.Ended -> JourneyStrings.hasEnded(title, lang) to status.returnsLabel
        is JourneyStatus.ComingSoon -> JourneyStrings.notOpenYet(title, lang) to status.startsLabel
        is JourneyStatus.Active -> title to ""
    }
    showAlert(LockedJourneyAlert(alertTitle, detail, pointerLine(descriptor, lang)))
}

/**
 * "Up next: X · in N days" (or "X is open now") for the soonest journey to
 * open, excluding the tapped one. Null when the tapped journey IS the soonest.
 */
private fun pointerLine(tapped: JourneyDescriptor, lang: CommentaryLanguage): String? {
    val rows = JourneyDescriptor.all.map { it to it.status() }
    val soonest = rows.minByOrNull { it.second.opensIn } ?: return null
    if (soonest.first.id == tapped.id) return null
    val title = JourneyStrings.title(soonest.first.id, lang)
    if (soonest.second.isActive) return JourneyStrings.isOpenNow(title, lang)
    val days = soonest.second.opensIn
    if (days <= 0) return JourneyStrings.upNextToday(title, lang)
    return JourneyStrings.upNextInDays(title, days, lang)
}

// MARK: - Sacred Seasons shelf (iOS JourneyShelf + ShelfCard)

@Composable
private fun SacredSeasonsShelf(
    ordered: List<Pair<JourneyDescriptor, JourneyStatus>>,
    lang: CommentaryLanguage,
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit,
    onTap: (JourneyDescriptor, JourneyStatus) -> Unit
) {
    val colors = Theme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = JourneyStrings.sacredSeasons(lang).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = colors.accentColor,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.pressable(onClick = onSeeAll),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = JourneyStrings.allCount(ordered.size, lang),
                    fontSize = 12.5.sp,
                    color = colors.secondaryText
                )
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Plain Row + IntrinsicSize so all five cards share the tallest height
        // (iOS pins every shelf card to the tallest measured card).
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ordered.forEach { (descriptor, status) ->
                ShelfCard(
                    descriptor = descriptor,
                    status = status,
                    lang = lang,
                    onTap = { onTap(descriptor, status) }
                )
            }
        }
    }
}

@Composable
private fun ShelfCard(
    descriptor: JourneyDescriptor,
    status: JourneyStatus,
    lang: CommentaryLanguage,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)
    val available = status.isActive
    val statusText = when (status) {
        is JourneyStatus.Active -> JourneyStrings.live(lang)
        is JourneyStatus.ComingSoon -> JourneyStrings.inDaysShort(status.daysUntil, lang)
        is JourneyStatus.Ended -> JourneyStrings.endedShort(lang)
    }

    Column(
        modifier = Modifier
            .width(190.dp)
            .fillMaxHeight()
            .clip(shape)
            .background(if (available) colors.glassSurfaceElevated else colors.glassSurface)
            .border(
                1.dp,
                if (available) colors.accentColor.copy(alpha = 0.4f) else colors.strokeColor,
                shape
            )
            .pressable(onClick = onTap)
            .padding(14.dp)
    ) {
        EmIconChip(icon = journeyUiConfig(descriptor.id).icon, size = 40.dp, active = available)
        Text(
            text = statusText.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp,
            color = if (available) colors.accentColor else colors.tertiaryText,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = JourneyStrings.title(descriptor.id, lang),
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = colors.primaryText,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = JourneyStrings.seasonTagline(descriptor.id, lang),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            maxLines = 2,
            color = if (available) colors.secondaryText else colors.tertiaryText,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

// MARK: - Deep Dive / Inside-the-Surah shelf (iOS JourneyShelf reuse)

/** One compact shelf card's data for the Deep Dives / Inside-the-Surah rows. */
private data class DiveShelfItem(
    val icon: ImageVector,
    val available: Boolean,
    val status: String,
    val title: String,
    val description: String,
    val onTap: () -> Unit
)

@Composable
private fun DiveShelf(
    label: String,
    items: List<DiveShelfItem>,
    lang: CommentaryLanguage,
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit
) {
    val colors = Theme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = colors.accentColor,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.pressable(onClick = onSeeAll),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = JourneyStrings.allCount(items.size, lang),
                    fontSize = 12.5.sp,
                    color = colors.secondaryText
                )
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item -> DiveShelfCard(item) }
        }
    }
}

@Composable
private fun DiveShelfCard(item: DiveShelfItem) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .width(190.dp)
            .fillMaxHeight()
            .clip(shape)
            .background(if (item.available) colors.glassSurfaceElevated else colors.glassSurface)
            .border(
                1.dp,
                if (item.available) colors.accentColor.copy(alpha = 0.4f) else colors.strokeColor,
                shape
            )
            .pressable(onClick = item.onTap)
            .padding(14.dp)
    ) {
        EmIconChip(icon = item.icon, size = 40.dp, active = item.available)
        Text(
            text = item.status.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp,
            color = if (item.available) colors.accentColor else colors.tertiaryText,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = item.title,
            fontFamily = CormorantFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = colors.primaryText,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = item.description,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            maxLines = 2,
            color = if (item.available) colors.secondaryText else colors.tertiaryText,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

// MARK: - Full-width dive/experience card (iOS DeepDiveCard / SurahExperienceCard)

/**
 * Full-width card for the "All N" lists. Same EmCard layout as JourneyCard so
 * the sections read as peers. Available cards glow + chevron; premium-gated
 * cards swap the eyebrow for a PREMIUM chip (never a lock); coming-soon cards
 * are dimmed with a "Soon" marker.
 */
@Composable
private fun DiveCard(
    icon: ImageVector,
    eyebrow: String,
    locked: Boolean,
    title: String,
    subtitle: String,
    available: Boolean,
    lang: CommentaryLanguage,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    EmCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (available) 1f else 0.72f),
        glow = available,
        borderColor = if (available) colors.accentColor.copy(alpha = 0.4f) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onTap)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmIconChip(icon = icon, active = available)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (locked) {
                    Text(
                        text = JourneyStrings.premium(lang).uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = colors.accentColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentChip)
                            .border(1.dp, colors.strokeColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                } else {
                    Text(
                        text = eyebrow.uppercase(),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = colors.accentColor
                    )
                }
                Text(
                    text = title,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colors.primaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    color = colors.secondaryText
                )
            }
            if (available) {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = JourneyStrings.soon(lang),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.4.sp,
                    color = colors.tertiaryText,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, colors.strokeColor, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// MARK: - Full-width journey card (iOS JourneyCard)

@Composable
private fun JourneyCard(
    descriptor: JourneyDescriptor,
    status: JourneyStatus,
    isNextUp: Boolean,
    lang: CommentaryLanguage,
    onTap: () -> Unit
) {
    val colors = Theme.colors
    val detailLine = when (status) {
        is JourneyStatus.Active -> status.line
        is JourneyStatus.ComingSoon -> JourneyStrings.comingSoonInDays(status.daysUntil, lang)
        is JourneyStatus.Ended ->
            if (isNextUp) status.returnsLabel else JourneyStrings.endedReturns(status.returnsLabel, lang)
    }

    EmCard(
        modifier = Modifier.fillMaxWidth(),
        glow = status.isActive,
        borderColor = if (isNextUp) colors.accentColor.copy(alpha = 0.4f) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressable(onClick = onTap)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmIconChip(icon = journeyUiConfig(descriptor.id).icon, active = status.isActive)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isNextUp) {
                    Text(
                        text = JourneyStrings.nextUp(lang),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.6.sp,
                        color = colors.onAccentText,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.accentGradient)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                } else {
                    Text(
                        text = JourneyStrings.eyebrow(descriptor.id, descriptor.eyebrow, lang).uppercase(),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = colors.accentColor
                    )
                }
                Text(
                    text = JourneyStrings.title(descriptor.id, lang),
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colors.primaryText
                )
                Text(
                    text = detailLine,
                    fontSize = 13.sp,
                    color = colors.secondaryText
                )
            }
            if (status.isActive) {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// MARK: - Locked overlay (iOS LockedJourneyOverlay)

@Composable
private fun LockedJourneyOverlay(
    alert: LockedJourneyAlert,
    lang: CommentaryLanguage,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f))
            .pressable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .widthIn(max = 290.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.tertiaryBackground)
                .border(1.dp, colors.strokeColor, RoundedCornerShape(24.dp))
                // Swallow taps on the card so only the backdrop dismisses.
                .pressable { }
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmIconChip(icon = Icons.Filled.HourglassEmpty, size = 52.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = alert.title,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = colors.primaryText
                )
                if (alert.detail.isNotEmpty()) {
                    Text(
                        text = alert.detail,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = colors.secondaryText
                    )
                }
                alert.pointer?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = colors.accentColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            EmGoldCTA(title = JourneyStrings.gotIt(lang), small = true) { onDismiss() }
        }
    }
}
