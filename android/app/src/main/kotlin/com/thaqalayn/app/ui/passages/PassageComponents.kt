package com.thaqalayn.app.ui.passages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.components.pressableGentle
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

// Shared chrome for the passage reader (iOS Views/Passages): the list, hub,
// reader, Understanding and quiz screens all build from these.

/** Chrome copy for the passage hub. English only, plain spelling, no em dash. */
object PassageHubStrings {
    const val read = "Read the verses"
    const val understand = "Understand"
    const val test = "Test yourself"
    const val startReading = "Start reading"
    const val understandPassage = "Understand this passage"
    const val nextPassage = "Next passage"
    const val complete = "Passage complete"
    const val surahComplete = "Every passage finished"
    const val comingSoon = "Coming in an update"

    fun best(score: Int, total: Int) = "Best $score of $total"
    fun ringLabel(done: Int, total: Int) = "$done of $total"
    fun backToSurah(name: String) = "Back to $name"
}

/** Chrome copy for the passage quiz. English only, plain spelling, no em dash. */
object QuizStrings {
    const val title = "Test yourself"
    const val hint = "Tap an answer to check it"
    const val why = "Why"
    const val readInPassage = "Read this in the passage"
    const val next = "Next question"
    const val seeResults = "See results"
    const val missed = "What you missed"
    const val tryAgain = "Try again"
    const val backToPassage = "Back to passage"
    const val premium = "PREMIUM"

    fun question(n: Int, total: Int) = "Question $n of $total"
    fun score(score: Int, total: Int) = "$score of $total"
    fun scoreShort(score: Int, total: Int) = "$score/$total"
    fun verse(n: Int) = "Verse $n"

    /** Where "Read this in the passage" lands, in the reader's words. */
    fun anchorLabel(location: String): String {
        val parts = location.split(".")
        if (parts.firstOrNull() == "essay") return "Essay"
        if (parts.firstOrNull() == "perspectives") return "Perspectives"
        val v = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size < 3 || v == null) return ""
        if (parts[2] == "narrations") return "Narration on verse $v"
        if (parts[2] == "note") return "Note on verse $v"
        return "Verse $v"
    }

    fun verdict(score: Int, total: Int): String = when {
        score >= total - 1 -> "You understood this passage."
        score >= total / 2 -> "Most of it landed. Read the missed points again."
        else -> "Worth another read before moving on."
    }
}

/** Uppercase, tracked, bold label (iOS `.emEyebrow(size:tracking:)`). */
@Composable
internal fun Eyebrow(
    text: String,
    color: Color,
    size: TextUnit = 11.sp,
    tracking: TextUnit = 2.sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        fontSize = size,
        fontWeight = FontWeight.Bold,
        letterSpacing = tracking,
        color = color,
        modifier = modifier
    )
}

/** The outlined back chevron every passage screen opens with. */
@Composable
internal fun PassageBackButton(contentDescription: String = "Back", onClick: () -> Unit) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, colors.strokeColor, CircleShape)
            .pressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = contentDescription,
            tint = colors.accentColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

/** A 40dp glass circle holding one glyph (search, play, heart). */
@Composable
internal fun GlassCircleButton(
    icon: ImageVector,
    contentDescription: String?,
    tint: Color = Theme.colors.accentColor,
    popScale: Float = 1f,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .pressable(onClick = onClick)
            .clip(CircleShape)
            .background(colors.glassSurface)
            .border(1.dp, colors.strokeColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .size(17.dp)
                .scale(popScale)
        )
    }
}

/** The PREMIUM capsule in accent style; gated stages never show a lock. */
@Composable
internal fun PremiumCapsule() {
    val colors = Theme.colors
    Text(
        text = QuizStrings.premium,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = colors.accentColor,
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.accentChip)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

/**
 * A ring of one arc per stage, gold for the stages done (iOS PassageProgressRing).
 * The list rows use it small; the hub header uses it large.
 */
@Composable
fun PassageProgressRing(done: Int, total: Int, size: Dp = 20.dp, lineWidth: Dp = 2.2.dp) {
    val accent = Theme.colors.accentColor
    val count = maxOf(total, 1)
    // A gap on each side of every arc, as a fraction of the circle.
    val gap = if (count == 1) 0f else 0.035f
    Canvas(modifier = Modifier.size(size + lineWidth)) {
        val stroke = lineWidth.toPx()
        val diameter = size.toPx()
        val topLeft = Offset(stroke / 2, stroke / 2)
        for (i in 0 until count) {
            val from = i.toFloat() / count + gap
            val to = (i + 1).toFloat() / count - gap
            drawArc(
                color = if (i < done) accent else accent.copy(alpha = 0.22f),
                startAngle = -90f + from * 360f,
                sweepAngle = (to - from) * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

enum class HubStageState { DONE, NEXT, TODO, UNAVAILABLE }

/**
 * One stage on the hub's path (iOS HubStageRow): a numbered marker (a seal once
 * done, lit when it is the next step), the stage title and its subline, and a
 * chevron or the PREMIUM capsule. Done stages stay tappable, to take them again.
 */
@Composable
internal fun HubStageRow(
    number: Int,
    title: String,
    subtitle: String,
    state: HubStageState,
    gated: Boolean,
    onTap: () -> Unit
) {
    val tm = Theme.colors
    // "Best 4 of 5" reads as a result, so it takes the done colour.
    val subtitleIsScore = state == HubStageState.DONE && subtitle.startsWith("Best ")
    val dim = state == HubStageState.TODO || state == HubStageState.UNAVAILABLE
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (state == HubStageState.UNAVAILABLE) 0.55f else 1f)
            .let { if (state == HubStageState.UNAVAILABLE) it else it.pressableGentle(onClick = onTap) }
            .padding(vertical = 12.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            // Paints over the path line behind the row.
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(tm.primaryBackground))
            when (state) {
                HubStageState.DONE -> Icon(
                    Icons.Filled.Verified,
                    contentDescription = null,
                    tint = tm.semanticGreen,
                    modifier = Modifier.size(34.dp)
                )
                HubStageState.NEXT -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(8.dp, tm.accentColor.copy(alpha = 0.18f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(tm.accentChip)
                            .border(1.dp, tm.accentColor, CircleShape)
                    )
                    StageNumber(number, tm.accentBright)
                }
                else -> {
                    Box(modifier = Modifier.size(36.dp).border(1.dp, tm.strokeColorStrong, CircleShape))
                    StageNumber(number, tm.tertiaryText)
                }
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = if (dim) tm.tertiaryText else tm.primaryText
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = if (subtitleIsScore) FontWeight.SemiBold else FontWeight.Medium,
                color = if (subtitleIsScore) tm.semanticGreen else tm.secondaryText
            )
        }

        if (state != HubStageState.UNAVAILABLE) {
            if (gated) {
                PremiumCapsule()
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (state == HubStageState.NEXT) tm.accentColor else tm.quaternaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StageNumber(number: Int, color: Color) {
    Text(
        text = "$number",
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = color
    )
}

/** What the hub's pinned bar offers next. [quiet]: glass, not gold - the surah is finished. */
internal data class HubAction(
    val title: String,
    val subtitle: String,
    val gated: Boolean,
    val quiet: Boolean = false,
    val perform: () -> Unit
)

/**
 * The pinned bar at the foot of the hub (iOS HubActionBar): gold with the next
 * step's title and subline, glass with the PREMIUM capsule when that step is gated.
 */
@Composable
internal fun HubActionBar(action: HubAction, modifier: Modifier = Modifier) {
    val tm = Theme.colors
    val isGold = !action.gated && !action.quiet
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressable(onClick = action.perform)
            .shadow(
                if (isGold) 24.dp else 14.dp,
                shape,
                spotColor = if (isGold) tm.accentColor.copy(alpha = 0.28f) else Color.Black.copy(alpha = 0.28f)
            )
            .clip(shape)
            .let {
                if (isGold) it.background(tm.accentGradient)
                else it.background(tm.primaryBackground).background(tm.accentColor.copy(alpha = 0.12f)).border(1.dp, tm.accentColor, shape)
            }
            .padding(horizontal = 18.dp, vertical = 13.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = if (action.gated) "${action.title}, premium feature" else "${action.title}. ${action.subtitle}"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = action.title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = if (isGold) tm.onAccentText else tm.primaryText
            )
            Text(
                text = action.subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isGold) tm.onAccentText.copy(alpha = 0.72f) else tm.secondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (action.gated) {
            PremiumCapsule()
        } else {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isGold) tm.onAccentText else tm.accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** A quiet text link with a trailing chevron (the hub's "Next passage", quiz "Read this"). */
@Composable
internal fun QuietLink(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .pressableGentle(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = color)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}

/** Horizontal hairline in the divider colour. */
@Composable
internal fun Hairline(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Theme.colors.dividerColor)
    )
}
