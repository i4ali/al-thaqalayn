package com.thaqalayn.app.ui.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

/** Temporary stub for tabs that are ported in later phases. */
@Composable
fun PlaceholderTabScreen(title: String) {
    val colors = Theme.colors
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            Text(
                text = title,
                fontFamily = CormorantFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )
            EmDivider(modifier = Modifier.padding(vertical = 18.dp))
            Text(
                text = "Coming soon to Android",
                fontSize = 14.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}
