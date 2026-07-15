package com.thaqalayn.app.data

// Static registries of the immersive "Deep Dives" and "Inside the Surah"
// experiences shown in the Journey tab (iOS DeepDiveCatalog.swift +
// SurahExperienceCatalog.swift). Mirrors JourneyCatalog's shape but carries no
// calendar logic - an entry is simply available or coming soon. SF symbols are
// mapped to Material icons here, following the per-file mapping convention.

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import com.thaqalayn.app.content.sabrDive
import com.thaqalayn.app.content.surahAliImranDive
import com.thaqalayn.app.content.surahBaqaraDive
import com.thaqalayn.app.content.surahFatihaDive
import com.thaqalayn.app.content.surahNisaDive
import com.thaqalayn.app.content.surahYusufDive
import com.thaqalayn.app.content.yaqinDive
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.LocalizedText

/** One deep dive in the hub. Static registry - see [DeepDiveDescriptor.all]. */
data class DeepDiveDescriptor(
    val id: String,
    val title: LocalizedText,
    val titleAr: String,
    val icon: ImageVector,
    val subtitle: LocalizedText,
    /** True when the dive is built and openable. False = "coming soon" placeholder. */
    val available: Boolean,
    /** The dive content, present only when [available]. */
    val dive: DeepDive?
) {
    companion object {
        val all: List<DeepDiveDescriptor> = listOf(
            DeepDiveDescriptor(
                id = "yaqin",
                title = LocalizedText("Yaqin · Certainty", "یقین", "اليقين"),
                titleAr = "يَقِين",
                icon = Icons.Filled.Visibility,
                subtitle = LocalizedText(
                    "A descent through three depths - Qur'an to Karbala",
                    "تین گہرائیوں میں اترتا ایک سفر - قرآن سے کربلا تک",
                    "نزولٌ عبر ثلاثة أعماق - من القرآن إلى كربلاء"
                ),
                available = true, dive = yaqinDive
            ),
            DeepDiveDescriptor(
                id = "sabr",
                title = LocalizedText("Sabr · Patience", "صبر", "الصبر"),
                titleAr = "صَبْر",
                icon = Icons.Filled.HourglassEmpty,
                subtitle = LocalizedText(
                    "A descent through three stations - Qur'an to Karbala",
                    "تین منزلوں میں اترتا ایک سفر - قرآن سے کربلا تک",
                    "نزولٌ عبر ثلاث محطات - من القرآن إلى كربلاء"
                ),
                available = true, dive = sabrDive
            ),
            DeepDiveDescriptor(
                id = "tawakkul",
                title = LocalizedText("Tawakkul · Reliance", "توکل", "التوكّل"),
                titleAr = "تَوَكُّل",
                icon = Icons.Filled.CleanHands,
                subtitle = LocalizedText(
                    "Trusting God with the outcome",
                    "انجام کو اللہ کے سپرد کر دینا",
                    "أن تُسلّم النتيجة لله"
                ),
                available = false, dive = null
            ),
            DeepDiveDescriptor(
                id = "shukr",
                title = LocalizedText("Shukr · Gratitude", "شکر", "الشكر"),
                titleAr = "شُكْر",
                icon = Icons.Filled.VolunteerActivism,
                subtitle = LocalizedText(
                    "Turning every blessing into remembrance",
                    "ہر نعمت کو یاد میں بدل دینا",
                    "تحويل كل نعمة إلى ذِكر"
                ),
                available = false, dive = null
            ),
            DeepDiveDescriptor(
                id = "ikhlas",
                title = LocalizedText("Ikhlas · Sincerity", "اخلاص", "الإخلاص"),
                titleAr = "إِخْلَاص",
                icon = Icons.Filled.WaterDrop,
                subtitle = LocalizedText(
                    "Purifying the intention for God alone",
                    "نیت کو صرف اللہ کے لیے خالص کرنا",
                    "إخلاص النية لله وحده"
                ),
                available = false, dive = null
            ),
            DeepDiveDescriptor(
                id = "taqwa",
                title = LocalizedText("Taqwa · God-consciousness", "تقویٰ", "التقوى"),
                titleAr = "تَقْوَىٰ",
                icon = Icons.Filled.Shield,
                subtitle = LocalizedText(
                    "The awareness that guards the heart",
                    "وہ شعور جو دل کی حفاظت کرے",
                    "الوعي الذي يحرس القلب"
                ),
                available = false, dive = null
            ),
            DeepDiveDescriptor(
                id = "rida",
                title = LocalizedText("Rida · Contentment", "رضا", "الرضا"),
                titleAr = "رِضَا",
                icon = Icons.Filled.Favorite,
                subtitle = LocalizedText(
                    "Meeting God's decree with a still heart",
                    "اللہ کے فیصلے کو مطمئن دل سے قبول کرنا",
                    "لقاء قضاء الله بقلبٍ مطمئن"
                ),
                available = false, dive = null
            )
        )

        fun byId(id: String): DeepDiveDescriptor? = all.firstOrNull { it.id == id }
    }
}

/**
 * One "Inside the Surah" experience in the hub, also surfaced as the
 * Read & Tafsir | Journey toggle strip on the matching surah's list card.
 * All entries are premium-gated except al-Fatiha, the free flagship teaser
 * (PremiumManager.canAccessSurahExperience).
 */
data class SurahExperienceDescriptor(
    val id: String,
    /** The surah this experience belongs to - drives the list-row strip lookup. */
    val surahNumber: Int,
    val title: LocalizedText,
    val titleAr: String,
    val icon: ImageVector,
    val subtitle: LocalizedText,
    /** True when the experience is built and openable. False = "coming soon". */
    val available: Boolean,
    /** The experience content, present only when [available]. */
    val dive: DeepDive?
) {
    companion object {
        val all: List<SurahExperienceDescriptor> = listOf(
            SurahExperienceDescriptor(
                id = "surah-fatiha",
                surahNumber = 1,
                title = LocalizedText("Surah al-Fatiha"),
                titleAr = "الْفَاتِحَة",
                icon = Icons.Filled.Book,
                subtitle = LocalizedText("The Opening - the prayer beneath every prayer"),
                available = true, dive = surahFatihaDive
            ),
            SurahExperienceDescriptor(
                id = "surah-baqara",
                surahNumber = 2,
                title = LocalizedText("Surah al-Baqara"),
                titleAr = "الْبَقَرَة",
                icon = Icons.Filled.AutoAwesome,
                subtitle = LocalizedText("The Cow - the mirror inside the mightiest surah"),
                available = true, dive = surahBaqaraDive
            ),
            SurahExperienceDescriptor(
                id = "surah-ali-imran",
                surahNumber = 3,
                title = LocalizedText("Surah Al Imran"),
                titleAr = "آلِ عِمْرَان",
                icon = Icons.Filled.Groups,
                subtitle = LocalizedText("The Family of Imran - one chosen house, and the house that answered it"),
                available = true, dive = surahAliImranDive
            ),
            SurahExperienceDescriptor(
                id = "surah-nisa",
                surahNumber = 4,
                title = LocalizedText("Surah al-Nisa"),
                titleAr = "النِّسَاء",
                icon = Icons.Filled.AccountBalance,
                subtitle = LocalizedText("The Women - one trust, from the orphan's coin to the seat of authority"),
                available = true, dive = surahNisaDive
            ),
            SurahExperienceDescriptor(
                id = "surah-yusuf",
                surahNumber = 12,
                title = LocalizedText("Surah Yusuf", "سورۂ یوسف", "سورة يوسف"),
                titleAr = "يُوسُف",
                icon = Icons.Filled.NightsStay,
                subtitle = LocalizedText(
                    "The most beautiful of stories - loss, patience, reunion",
                    "بہترین قصہ - جدائی، صبر، وصال",
                    "أحسن القصص - فقدٌ وصبرٌ ولقاء"
                ),
                available = true, dive = surahYusufDive
            ),
            SurahExperienceDescriptor(
                id = "surah-yasin",
                surahNumber = 36,
                title = LocalizedText("Surah Yasin", "سورۂ یٰسین", "سورة يس"),
                titleAr = "يس",
                icon = Icons.Filled.FavoriteBorder,
                subtitle = LocalizedText(
                    "The heart of the Qur'an - and what it keeps asking you",
                    "قرآن کا دل - اور اس کا آپ سے سوال",
                    "قلب القرآن - وما يسألك عنه"
                ),
                available = false, dive = null
            ),
            SurahExperienceDescriptor(
                id = "surah-rahman",
                surahNumber = 55,
                title = LocalizedText("Surah al-Rahman", "سورۂ رحمٰن", "سورة الرحمن"),
                titleAr = "الرَّحْمَٰن",
                icon = Icons.Filled.Waves,
                subtitle = LocalizedText(
                    "One question, asked thirty-one times",
                    "ایک سوال، اکتیس بار",
                    "سؤالٌ واحد، إحدى وثلاثون مرة"
                ),
                available = false, dive = null
            ),
            SurahExperienceDescriptor(
                id = "surah-mulk",
                surahNumber = 67,
                title = LocalizedText("Surah al-Mulk", "سورۂ ملک", "سورة الملك"),
                titleAr = "الْمُلْك",
                icon = Icons.Filled.WorkspacePremium,
                subtitle = LocalizedText(
                    "The protector - whose hand holds the kingdom",
                    "محافظ سورہ - بادشاہی کس کے ہاتھ میں ہے",
                    "السورة الحامية - بيد مَن الملك"
                ),
                available = false, dive = null
            )
        )

        fun byId(id: String): SurahExperienceDescriptor? = all.firstOrNull { it.id == id }
        fun bySurahNumber(n: Int): SurahExperienceDescriptor? = all.firstOrNull { it.surahNumber == n }
    }
}
