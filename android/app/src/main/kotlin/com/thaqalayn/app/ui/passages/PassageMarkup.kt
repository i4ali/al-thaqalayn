package com.thaqalayn.app.ui.passages

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Citation markers "[n]" inside passage prose (iOS PassageMarkup). Rendered as
 * small raised accent numbers; a tap opens the source sheet for source s<n>.
 */
object PassageMarkup {
    sealed interface Segment {
        data class Text(val text: String) : Segment
        data class Marker(val number: Int) : Segment
    }

    private val markerRegex = Regex("""\[(\d+)]""")

    fun segments(text: String): List<Segment> {
        if (text.isEmpty()) return emptyList()
        val out = mutableListOf<Segment>()
        var cursor = 0
        for (m in markerRegex.findAll(text)) {
            if (cursor < m.range.first) out += Segment.Text(text.substring(cursor, m.range.first))
            out += Segment.Marker(m.groupValues[1].toIntOrNull() ?: 0)
            cursor = m.range.last + 1
        }
        if (cursor < text.length) out += Segment.Text(text.substring(cursor))
        return out
    }

    /** The text as the voice reads it: citation markers removed. */
    fun stripMarkers(text: String): String =
        segments(text).filterIsInstance<Segment.Text>().joinToString("") { it.text }

    /**
     * Prose with markers turned into raised accent numbers that call [onMarker].
     * [highlight] is the word being spoken, as a range over the marker-stripped
     * text (the string the voice reads), painted with [highlightColor].
     */
    fun annotated(
        text: String,
        fontSize: TextUnit,
        accent: Color,
        highlight: IntRange?,
        highlightColor: Color,
        onMarker: (Int) -> Unit
    ): AnnotatedString = buildAnnotatedString {
        var spokenOffset = 0
        for (seg in segments(text)) {
            when (seg) {
                is Segment.Text -> {
                    val s = seg.text
                    val localStart = highlight?.let { maxOf(it.first, spokenOffset) - spokenOffset }
                    val localEnd = highlight?.let { minOf(it.last + 1, spokenOffset + s.length) - spokenOffset }
                    if (localStart != null && localEnd != null && localEnd > localStart) {
                        append(s.substring(0, localStart))
                        withStyle(SpanStyle(background = highlightColor)) { append(s.substring(localStart, localEnd)) }
                        append(s.substring(localEnd))
                    } else {
                        append(s)
                    }
                    spokenOffset += s.length
                }
                is Segment.Marker -> {
                    val n = seg.number
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "source:$n",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = accent,
                                    fontSize = (fontSize.value * 0.62f).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    baselineShift = BaselineShift(0.56f)
                                )
                            ),
                            linkInteractionListener = { onMarker(n) }
                        )
                    ) {
                        append("$n")
                    }
                }
            }
        }
    }
}
