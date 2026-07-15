package com.thaqalayn.app.ui.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thaqalayn.app.ui.components.EmCard
import com.thaqalayn.app.ui.components.EmDivider
import com.thaqalayn.app.ui.components.EmHeading
import com.thaqalayn.app.ui.components.EmSectionLabel
import com.thaqalayn.app.ui.components.ThemedBackground
import com.thaqalayn.app.ui.components.pressable
import com.thaqalayn.app.ui.theme.AmiriFamily
import com.thaqalayn.app.ui.theme.CormorantFamily
import com.thaqalayn.app.ui.theme.Theme

private data class SourceItem(
    val title: String,
    val subtitle: String,
    val arabic: String? = null
)

private data class SourceSection(
    val icon: ImageVector,
    val title: String,
    val sources: List<SourceItem>
)

private val sections = listOf(
    SourceSection(
        icon = Icons.Filled.AccountBalance,
        title = "Foundation",
        sources = listOf(
            SourceItem("General Islamic Scholarship", "Historical context and foundational understanding"),
            SourceItem("Classical Tafsir Methodology", "Traditional exegetical approaches")
        )
    ),
    SourceSection(
        icon = Icons.Filled.LibraryBooks,
        title = "Classical Shia",
        sources = listOf(
            SourceItem("Tafsir al-Mizan", "Allama Muhammad Husayn Tabatabai", "تفسير الميزان"),
            SourceItem("Majma' al-Bayan", "Sheikh Abu Ali al-Fadl al-Tabrisi", "مجمع البيان"),
            SourceItem("Sharh al-Lum'a", "Classical jurisprudential commentary", "شرح اللمعة")
        )
    ),
    SourceSection(
        icon = Icons.Filled.Public,
        title = "Contemporary",
        sources = listOf(
            SourceItem("Ayatollah Naser Makarem Shirazi", "Contemporary Shia scholar"),
            SourceItem("Sheikh Mansour Leghaei", "Islamic educator and author"),
            SourceItem("Dr. Reza Shah-Kazemi", "Islamic philosopher and author")
        )
    ),
    SourceSection(
        icon = Icons.Filled.Star,
        title = "Ahlul Bayt",
        sources = listOf(
            SourceItem("Al-Kafi", "Sheikh al-Kulayni", "الكافي"),
            SourceItem("Bihar al-Anwar", "Allama Muhammad Baqir al-Majlisi", "بحار الأنوار"),
            SourceItem("Tafsir al-Qummi", "Ali ibn Ibrahim al-Qummi", "تفسير القمي"),
            SourceItem("Tafsir al-Ayyashi", "Muhammad ibn Mas'ud al-Ayyashi", "تفسير العياشي"),
            SourceItem("Al-Sahifa al-Sajjadiyya", "Imam Ali Zayn al-Abidin", "الصحيفة السجادية")
        )
    ),
    SourceSection(
        icon = Icons.Filled.Balance,
        title = "Comparative",
        sources = listOf(
            SourceItem("Classical Sunni Tafsir Traditions", "For comparative scholarly analysis"),
            SourceItem("Shia-Sunni Scholarly Dialogue", "Balanced academic perspectives")
        )
    )
)

/** The sources and scholars referenced per tafsir layer (iOS TafsirSourcesView). */
@Composable
fun TafsirSourcesScreen(navController: NavHostController) {
    val colors = Theme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        ThemedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Back
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

            EmHeading(
                eyebrow = "Scholarship",
                title = "Tafsir Sources",
                sub = "The commentary in this app draws from classical and contemporary Shia scholarship. Below are the primary sources referenced for each layer."
            )

            sections.forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmSectionLabel(icon = section.icon, text = section.title)
                    EmCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            section.sources.forEachIndexed { index, source ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = source.title,
                                            fontFamily = CormorantFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 19.sp,
                                            color = colors.primaryText
                                        )
                                        Text(
                                            text = source.subtitle,
                                            fontSize = 13.sp,
                                            color = colors.secondaryText
                                        )
                                    }
                                    if (source.arabic != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = source.arabic,
                                            fontFamily = AmiriFamily,
                                            fontSize = 20.sp,
                                            color = colors.accentColor
                                        )
                                    }
                                }
                                if (index < section.sources.size - 1) {
                                    EmDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
