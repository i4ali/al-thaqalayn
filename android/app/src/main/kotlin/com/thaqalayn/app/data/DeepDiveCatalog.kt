package com.thaqalayn.app.data

// Static registries of the immersive "Deep Dives" and "Inside the Surah"
// experiences shown in the Journey tab (iOS DeepDiveCatalog.swift +
// SurahExperienceCatalog.swift). Mirrors JourneyCatalog's shape but carries no
// calendar logic - an entry is simply available or coming soon. SF symbols are
// mapped to Material icons here, following the per-file mapping convention.

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import com.thaqalayn.app.R
import com.thaqalayn.app.content.ikhlasDive
import com.thaqalayn.app.content.kisaDive
import com.thaqalayn.app.content.sabrDive
import com.thaqalayn.app.content.salahDive
import com.thaqalayn.app.content.shukrDive
import com.thaqalayn.app.content.surahAliImranDive
import com.thaqalayn.app.content.surahAnamDive
import com.thaqalayn.app.content.surahAnfalDive
import com.thaqalayn.app.content.surahArafDive
import com.thaqalayn.app.content.surahBaqaraDive
import com.thaqalayn.app.content.surahFatihaDive
import com.thaqalayn.app.content.surahHijrDive
import com.thaqalayn.app.content.surahHudDive
import com.thaqalayn.app.content.surahIbrahimDive
import com.thaqalayn.app.content.surahKawtharDive
import com.thaqalayn.app.content.surahMaidaDive
import com.thaqalayn.app.content.surahMulkDive
import com.thaqalayn.app.content.surahNisaDive
import com.thaqalayn.app.content.surahRadDive
import com.thaqalayn.app.content.surahRahmanDive
import com.thaqalayn.app.content.surahTawbaDive
import com.thaqalayn.app.content.surahYasinDive
import com.thaqalayn.app.content.surahYunusDive
import com.thaqalayn.app.content.surahYusufDive
import com.thaqalayn.app.content.taqwaDive
import com.thaqalayn.app.content.tawakkulDive
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
    val dive: DeepDive?,
    /**
     * Premium cover art. ONE field feeds every surface: shelf poster, list
     * tile, descent threshold, descent veil, and the paywall hero context.
     */
    @DrawableRes val coverRes: Int
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
                available = true, dive = yaqinDive,
                coverRes = R.drawable.dive_cover_yaqin
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
                available = true, dive = sabrDive,
                coverRes = R.drawable.dive_cover_sabr
            ),
            DeepDiveDescriptor(
                id = "tawakkul",
                title = LocalizedText("Tawakkul · Reliance", "توکل", "التوكّل"),
                titleAr = "تَوَكُّل",
                icon = Icons.Filled.CleanHands,
                subtitle = LocalizedText(
                    "A descent through three motions - Qur'an to Karbala",
                    "تین حرکتوں میں اترتا ایک سفر - قرآن سے کربلا تک",
                    "نزولٌ عبر ثلاث حركات - من القرآن إلى كربلاء"
                ),
                available = true, dive = tawakkulDive,
                coverRes = R.drawable.dive_cover_tawakkul
            ),
            DeepDiveDescriptor(
                id = "shukr",
                title = LocalizedText("Shukr · Gratitude", "شکر", "الشكر"),
                titleAr = "شُكْر",
                icon = Icons.Filled.VolunteerActivism,
                subtitle = LocalizedText(
                    "A descent through three tongues - Qur'an to Karbala",
                    "تین زبانوں میں اترتا ایک سفر - قرآن سے کربلا تک",
                    "نزولٌ عبر ثلاثة ألسنة - من القرآن إلى كربلاء"
                ),
                available = true, dive = shukrDive,
                coverRes = R.drawable.dive_cover_shukr
            ),
            DeepDiveDescriptor(
                id = "salah",
                title = LocalizedText("Salah · Prayer", "نماز", "الصلاة"),
                titleAr = "صَلَاة",
                icon = Icons.Filled.Stairs,
                subtitle = LocalizedText(
                    "An ascent through three names - Qur'an to Karbala",
                    "تین ناموں میں چڑھتا ایک سفر - قرآن سے کربلا تک",
                    "صعودٌ عبر ثلاثة أسماء - من القرآن إلى كربلاء"
                ),
                available = true, dive = salahDive,
                coverRes = R.drawable.dive_cover_salah
            ),
            DeepDiveDescriptor(
                id = "ikhlas",
                title = LocalizedText("Ikhlas · Sincerity", "اخلاص", "الإخلاص"),
                titleAr = "إِخْلَاص",
                icon = Icons.Filled.WaterDrop,
                subtitle = LocalizedText(
                    "A descent through three purities - Qur'an to the house of Fatima",
                    "تین پاکیزگیوں میں اترتا ایک سفر - قرآن سے خانۂ فاطمہؑ تک",
                    "نزولٌ عبر ثلاث صفاءات - من القرآن إلى بيت فاطمة عليها السلام"
                ),
                available = true, dive = ikhlasDive,
                coverRes = R.drawable.dive_cover_ikhlas
            ),
            DeepDiveDescriptor(
                id = "taqwa",
                title = LocalizedText("Taqwa · God-consciousness", "تقویٰ", "التقوى"),
                titleAr = "تَقْوَىٰ",
                icon = Icons.Filled.Shield,
                subtitle = LocalizedText(
                    "A descent through three guards - Qur'an to Karbala",
                    "تین پہروں میں اترتا ایک سفر - قرآن سے کربلا تک",
                    "نزولٌ عبر ثلاثة حُرّاس - من القرآن إلى كربلاء"
                ),
                available = true, dive = taqwaDive,
                coverRes = R.drawable.dive_cover_taqwa
            ),
            DeepDiveDescriptor(
                id = "kisa",
                title = LocalizedText("al-Kisa · The Cloak", "حدیث کساء", "الكساء"),
                titleAr = "الكِسَاء",
                icon = Icons.Filled.NightsStay,
                subtitle = LocalizedText(
                    "A gathering beneath one cloak - told in the voice of Fatima",
                    "ایک چادر تلے ایک اجتماع - فاطمہ زہرا کی زبانی",
                    "اجتماعٌ تحت كساءٍ واحد - بلسان فاطمة الزهراء"
                ),
                available = true, dive = kisaDive,
                coverRes = R.drawable.dive_cover_kisa
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
                available = false, dive = null,
                coverRes = R.drawable.dive_cover_rida
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
    val dive: DeepDive?,
    /**
     * Premium cover art. ONE field feeds every surface: shelf poster, list
     * tile, descent threshold, descent veil, and the paywall hero context.
     */
    @DrawableRes val coverRes: Int
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
                available = true, dive = surahFatihaDive,
                coverRes = R.drawable.dive_cover_fatiha
            ),
            SurahExperienceDescriptor(
                id = "surah-baqara",
                surahNumber = 2,
                title = LocalizedText("Surah al-Baqara"),
                titleAr = "الْبَقَرَة",
                icon = Icons.Filled.AutoAwesome,
                subtitle = LocalizedText("The Cow - the mirror inside the mightiest surah"),
                available = true, dive = surahBaqaraDive,
                coverRes = R.drawable.dive_cover_baqara
            ),
            SurahExperienceDescriptor(
                id = "surah-ali-imran",
                surahNumber = 3,
                title = LocalizedText("Surah Al Imran"),
                titleAr = "آلِ عِمْرَان",
                icon = Icons.Filled.Groups,
                subtitle = LocalizedText("The Family of Imran - one chosen house, and the house that answered it"),
                available = true, dive = surahAliImranDive,
                coverRes = R.drawable.dive_cover_aliimran
            ),
            SurahExperienceDescriptor(
                id = "surah-nisa",
                surahNumber = 4,
                title = LocalizedText("Surah al-Nisa"),
                titleAr = "النِّسَاء",
                icon = Icons.Filled.AccountBalance,
                subtitle = LocalizedText("The Women - one trust, from the orphan's coin to the seat of authority"),
                available = true, dive = surahNisaDive,
                coverRes = R.drawable.dive_cover_nisa
            ),
            SurahExperienceDescriptor(
                id = "surah-maida",
                surahNumber = 5,
                title = LocalizedText("Surah al-Maida"),
                titleAr = "الْمَائِدَة",
                icon = Icons.Filled.Link,
                subtitle = LocalizedText("The Table Spread - a bond made, broken, and sealed"),
                available = true, dive = surahMaidaDive,
                coverRes = R.drawable.dive_cover_maida
            ),
            SurahExperienceDescriptor(
                id = "surah-anam",
                surahNumber = 6,
                title = LocalizedText("Surah al-An'am"),
                titleAr = "الْأَنْعَام",
                icon = Icons.Filled.WbSunny,
                subtitle = LocalizedText("The Cattle - no partner, and no share"),
                available = true, dive = surahAnamDive,
                coverRes = R.drawable.dive_cover_anam
            ),
            SurahExperienceDescriptor(
                id = "surah-araf",
                surahNumber = 7,
                title = LocalizedText("Surah al-A'raf"),
                titleAr = "الْأَعْرَاف",
                icon = Icons.Filled.Terrain,
                subtitle = LocalizedText("The Heights - the pledge you gave before you were born"),
                available = true, dive = surahArafDive,
                coverRes = R.drawable.dive_cover_araf
            ),
            SurahExperienceDescriptor(
                id = "surah-anfal",
                surahNumber = 8,
                title = LocalizedText("Surah al-Anfal"),
                titleAr = "الْأَنْفَال",
                icon = Icons.Filled.PanTool,
                subtitle = LocalizedText("The Spoils of War - the day a victory was handed back to God"),
                available = true, dive = surahAnfalDive,
                coverRes = R.drawable.dive_cover_anfal
            ),
            SurahExperienceDescriptor(
                id = "surah-tawba",
                surahNumber = 9,
                title = LocalizedText("Surah al-Tawba"),
                titleAr = "التَّوْبَة",
                icon = Icons.Filled.MeetingRoom,
                subtitle = LocalizedText("The Repentance - the surah with no Bismillah, named for the door God holds open"),
                available = true, dive = surahTawbaDive,
                coverRes = R.drawable.dive_cover_tawba
            ),
            SurahExperienceDescriptor(
                id = "surah-yunus",
                surahNumber = 10,
                title = LocalizedText("Surah Yunus"),
                titleAr = "يُونُس",
                icon = Icons.Filled.Waves,
                subtitle = LocalizedText("Jonah - how late is too late to turn back to God?"),
                available = true, dive = surahYunusDive,
                coverRes = R.drawable.dive_cover_yunus
            ),
            SurahExperienceDescriptor(
                id = "surah-hud",
                surahNumber = 11,
                title = LocalizedText("Surah Hud"),
                titleAr = "هُود",
                icon = Icons.Filled.Person,
                subtitle = LocalizedText("Hud - the surah the Prophet ﷺ said turned his hair gray"),
                available = true, dive = surahHudDive,
                coverRes = R.drawable.dive_cover_hud
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
                available = true, dive = surahYusufDive,
                coverRes = R.drawable.dive_cover_yusuf
            ),
            SurahExperienceDescriptor(
                id = "surah-rad",
                surahNumber = 13,
                title = LocalizedText("Surah al-Ra'd"),
                titleAr = "الرَّعْد",
                icon = Icons.Filled.Bolt,
                subtitle = LocalizedText("The Thunder - where is the sign you have been asking for?"),
                available = true, dive = surahRadDive,
                coverRes = R.drawable.dive_cover_rad
            ),
            SurahExperienceDescriptor(
                id = "surah-ibrahim",
                surahNumber = 14,
                title = LocalizedText("Surah Ibrahim"),
                titleAr = "إِبْرَاهِيم",
                icon = Icons.Filled.Forest,
                subtitle = LocalizedText("Abraham - what one word planted in dead ground can become"),
                available = true, dive = surahIbrahimDive,
                coverRes = R.drawable.dive_cover_ibrahim
            ),
            SurahExperienceDescriptor(
                id = "surah-hijr",
                surahNumber = 15,
                title = LocalizedText("Surah al-Hijr"),
                titleAr = "الْحِجْر",
                icon = Icons.Filled.Terrain,
                subtitle = LocalizedText("The Rock City - what can mockery touch, and what can it never reach?"),
                available = true, dive = surahHijrDive,
                coverRes = R.drawable.dive_cover_hijr
            ),
            SurahExperienceDescriptor(
                id = "surah-yasin",
                surahNumber = 36,
                title = LocalizedText("Surah Yasin", "سورۂ یٰسین", "سورة يس"),
                titleAr = "يس",
                icon = Icons.Filled.Favorite,
                subtitle = LocalizedText(
                    "The heart of the Qur'an - and what it keeps asking you",
                    "قرآن کا دل - اور اس کا آپ سے سوال",
                    "قلب القرآن - وما يسألك عنه"
                ),
                available = true, dive = surahYasinDive,
                coverRes = R.drawable.dive_cover_yasin
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
                available = true, dive = surahRahmanDive,
                coverRes = R.drawable.dive_cover_rahman
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
                available = true, dive = surahMulkDive,
                coverRes = R.drawable.dive_cover_mulk
            ),
            SurahExperienceDescriptor(
                id = "surah-kawthar",
                surahNumber = 108,
                title = LocalizedText("Surah al-Kawthar"),
                titleAr = "الْكَوْثَر",
                icon = Icons.Filled.WaterDrop,
                subtitle = LocalizedText("The shortest surah - God's answer to a taunt"),
                available = true, dive = surahKawtharDive,
                coverRes = R.drawable.dive_cover_kawthar
            )
        )

        fun byId(id: String): SurahExperienceDescriptor? = all.firstOrNull { it.id == id }

        /** Hub/list surfaces read [all]; every entry is now live (iOS parity). */
        fun bySurahNumber(n: Int): SurahExperienceDescriptor? = all.firstOrNull { it.surahNumber == n }
    }
}
