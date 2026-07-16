package com.thaqalayn.app.ui.components

// Shared premium cover-art treatments (iOS EmCoverBand / EmCoverTile /
// ShelfCard.posterFace / VeiledDayPreview veil recipe). Two distinct fades are
// used and must not be mixed up: header bands fade the ART ITSELF to
// transparent via a DstIn alpha mask; posters and veils layer a black
// scrim/overlay ON TOP of the art.

import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/**
 * Fixed-height full-bleed cover band placed BEHIND a screen's header text,
 * top-aligned (iOS emCoverHeaderBand). The art itself fades to transparent at
 * the bottom via an alpha mask - there is NO dark overlay on top. Decorative:
 * non-interactive and hidden from accessibility. Midnight Emerald only - gate
 * at the call site.
 */
@Composable
fun CoverHeaderBand(
    @DrawableRes art: Int,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val maskBrush = remember {
        Brush.verticalGradient(
            0.00f to Color.Black.copy(alpha = 0.92f),
            0.18f to Color.Black,
            0.62f to Color.Black,
            1.00f to Color.Transparent
        )
    }
    Image(
        painter = painterResource(art),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(brush = maskBrush, blendMode = BlendMode.DstIn)
            }
    )
}

/**
 * The shared veil recipe (iOS VeiledDayPreview / descent veil): cover
 * fill-cropped full size, overscanned 1.22x so the blur never samples the
 * edges, blur radius 44, then a plain black overlay - 0.52 for the journey
 * locked-day preview, 0.46 for the descent veil. Blur needs API 31; below
 * that the overlay deepens to 0.7 instead (never skip the veil).
 */
@Composable
fun CoverVeil(
    @DrawableRes art: Int,
    overlayAlpha: Float,
    modifier: Modifier = Modifier
) {
    val blurSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(modifier = modifier.clipToBounds().background(Color.Black)) {
        Image(
            painter = painterResource(art),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { scaleX = 1.22f; scaleY = 1.22f }
                .blur(44.dp)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = if (blurSupported) overlayAlpha else 0.7f))
        )
    }
}

/** Eyebrow variants for [PosterCard]. */
sealed interface PosterEyebrow {
    /** PREMIUM accent chip (never a lock icon - house rule). */
    data class Premium(val label: String) : PosterEyebrow

    /** Small-caps status: LIVE / "in N days" / ENDED / READY / SOON. */
    data class Status(val label: String, val active: Boolean) : PosterEyebrow
}

/**
 * Hub shelf poster face (iOS ShelfCard.posterFace): 190x238 (4:5) cover card
 * with a top scrim and the eyebrow + serif title in the art's dark sky.
 * Renders in BOTH themes - art, scrim, and title are theme-independent; only
 * the unavailable-state border comes from the theme.
 */
@Composable
fun PosterCard(
    @DrawableRes cover: Int,
    title: String,
    eyebrow: PosterEyebrow,
    available: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(18.dp)
    // Top scrim IS an overlay (unlike the header bands): top edge to center.
    val scrim = remember {
        Brush.verticalGradient(
            0.0f to Color.Black.copy(alpha = 0.60f),
            0.25f to Color.Black.copy(alpha = 0.26f),
            0.5f to Color.Transparent,
            1.0f to Color.Transparent
        )
    }

    Box(
        modifier = modifier
            .size(width = 190.dp, height = 238.dp)
            // Unavailable posters dim lighter than text rows on purpose.
            .alpha(if (available) 1f else 0.82f)
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.34f),
                spotColor = Color.Black.copy(alpha = 0.34f)
            )
            .clip(shape)
            .border(
                1.dp,
                if (available) colors.accentColor.copy(alpha = 0.4f) else colors.strokeColor,
                shape
            )
            .pressable(onClick = onTap)
    ) {
        Image(
            painter = painterResource(cover),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(modifier = Modifier.matchParentSize().background(scrim))

        Column(modifier = Modifier.padding(14.dp)) {
            when (eyebrow) {
                is PosterEyebrow.Premium -> PremiumChipOnArt(eyebrow.label)
                is PosterEyebrow.Status -> Text(
                    text = eyebrow.label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = if (eyebrow.active) colors.accentBright else Color.White.copy(alpha = 0.65f)
                )
            }
            Text(
                text = title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.55f),
                        offset = Offset(0f, 2f),
                        blurRadius = 8f
                    )
                ),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

/**
 * PREMIUM capsule sitting ON ART: fixed light gold regardless of theme (the
 * poster sky is dark in both themes).
 */
@Composable
fun PremiumChipOnArt(label: String) {
    val colors = Theme.colors
    Text(
        text = label.uppercase(),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.4.sp,
        color = colors.accentBright,
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.accentBright.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/**
 * Mini poster thumbnail for "All N" list rows (iOS EmCoverTile): 54x68 (4:5),
 * fill-cropped, hairline theme stroke. Renders in both themes.
 */
@Composable
fun EmCoverTile(
    @DrawableRes cover: Int,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false
) {
    val colors = Theme.colors
    val shape = RoundedCornerShape(12.dp)
    Image(
        painter = painterResource(cover),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 54.dp, height = 68.dp)
            .alpha(if (dimmed) 0.7f else 1f)
            .clip(shape)
            .border(1.dp, colors.strokeColor, shape)
    )
}

/**
 * Escape the parent's horizontal content padding so a band can run edge to
 * edge inside a padded column (the iOS `.padding(.horizontal, -20)` idiom).
 */
fun Modifier.fullBleed(horizontal: Dp): Modifier = layout { measurable, constraints ->
    val extra = (horizontal * 2).roundToPx()
    val placeable = measurable.measure(
        constraints.copy(maxWidth = constraints.maxWidth + extra)
    )
    layout(placeable.width - extra, placeable.height) {
        placeable.place(-horizontal.roundToPx(), 0)
    }
}

/**
 * True when the system remove-animations accessibility setting is on. Ken
 * Burns drift, entrance cascades, and the onboarding video are all skipped.
 */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
}
