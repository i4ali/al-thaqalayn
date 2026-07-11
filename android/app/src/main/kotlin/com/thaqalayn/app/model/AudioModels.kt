package com.thaqalayn.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reciter(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val serverURL: String,
    val description: String,
    val isPopular: Boolean = true,
    val isPremium: Boolean = false
) {
    /** Full-surah audio URL (mp3quran.net). */
    fun surahAudioUrl(surahNumber: Int): String =
        "$serverURL/${surahNumber.toString().padStart(3, '0')}.mp3"

    /**
     * Individual verse audio URL (everyayah.com), best available quality per
     * reciter - mirrors VerseWithTafsir.audioURL on iOS.
     */
    fun verseAudioUrl(surahNumber: Int, verseNumber: Int): String {
        val s = surahNumber.toString().padStart(3, '0')
        val v = verseNumber.toString().padStart(3, '0')
        val folder = when (id) {
            "mishary_rashid_alafasy" -> "Alafasy_128kbps"
            "abdul_rahman_al_sudais" -> "Abdurrahmaan_As-Sudais_192kbps"
            "saad_al_ghamidi" -> "Ghamadi_40kbps"
            "ahmad_ibn_ali_al_ajamy" -> "ahmed_ibn_ali_al_ajamy_128kbps"
            "maher_al_muaiqly" -> "MaherAlMuaiqly128kbps"
            "yasser_al_dosari" -> "Yasser_Ad-Dussary_128kbps"
            else -> return surahAudioUrl(surahNumber)
        }
        return "https://www.everyayah.com/data/$folder/$s$v.mp3"
    }

    companion object {
        val popularReciters = listOf(
            Reciter(
                id = "mishary_rashid_alafasy",
                nameArabic = "مشاري بن راشد العفاسي",
                nameEnglish = "Mishary Rashid Alafasy",
                serverURL = "https://server8.mp3quran.net/afs",
                description = "One of the most popular reciters worldwide with a beautiful voice"
            ),
            Reciter(
                id = "abdul_rahman_al_sudais",
                nameArabic = "عبد الرحمن السديس",
                nameEnglish = "Abdul Rahman Al-Sudais",
                serverURL = "https://server11.mp3quran.net/sds",
                description = "Imam of the Grand Mosque in Mecca"
            ),
            Reciter(
                id = "saad_al_ghamidi",
                nameArabic = "سعد الغامدي",
                nameEnglish = "Saad Al-Ghamidi",
                serverURL = "https://server7.mp3quran.net/s_gmd2",
                description = "Known for his emotional and beautiful recitation"
            ),
            Reciter(
                id = "ahmad_ibn_ali_al_ajamy",
                nameArabic = "أحمد بن علي العجمي",
                nameEnglish = "Ahmad Ibn Ali Al-Ajamy",
                serverURL = "https://server10.mp3quran.net/ajm",
                description = "Young reciter with a distinctive melodious voice"
            ),
            Reciter(
                id = "maher_al_muaiqly",
                nameArabic = "ماهر المعيقلي",
                nameEnglish = "Maher Al-Muaiqly",
                serverURL = "https://server12.mp3quran.net/maher",
                description = "Imam of the Prophet's Mosque in Medina"
            ),
            Reciter(
                id = "yasser_al_dosari",
                nameArabic = "ياسر الدوسري",
                nameEnglish = "Yasser Al-Dosari",
                serverURL = "https://server14.mp3quran.net/yasir",
                description = "Known for his powerful and emotional recitation"
            )
        )

        val default = popularReciters.first()

        fun byId(id: String?): Reciter =
            popularReciters.firstOrNull { it.id == id } ?: default
    }
}

enum class AudioPlayerState {
    STOPPED, LOADING, PLAYING, PAUSED, BUFFERING, ERROR
}

data class CurrentPlayback(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val reciter: Reciter,
    val currentTime: Long,
    val duration: Long
) {
    val progress: Double
        get() = if (duration > 0) currentTime.toDouble() / duration else 0.0
}
