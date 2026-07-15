package com.thaqalayn.app.content

// Fixed content for the "Sabr" deep dive - a descent through the three stations
// of the patient heart (al-Sabr / al-Rida / al-Nafs al-Mutma'inna). Verbatim
// port of iOS Content/SabrDeepDive.swift; rendered by DeepDiveScreen. Prose is
// mostly English-only (LocalizedText falls back to English when ur/ar are null);
// Qur'an Arabic, references, and surah/ayah numbers are language-neutral.

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.LocalizedText

val sabrDive: DeepDive = DeepDive(
    id = "sabr",
    titleEn = "Sabr",
    titleAr = "صَبْر",
    subtitle = LocalizedText("Patience - a descent through three stations"),
    estMinutes = 5,
    acts = listOf(
        ActInfo(1, "الصَّبْر", "al-Sabr", LocalizedText("The Enduring")),
        ActInfo(2, "الرِّضَا", "al-Rida", LocalizedText("The Accepting")),
        ActInfo(3, "النَّفْس المُطْمَئِنَّة", "al-Nafs al-Mutma'inna", LocalizedText("The Soul at Peace"))
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE"),
            titleAr = "صَبْر",
            titleEn = "Sabr",
            subtitle = LocalizedText("Patience"),
            line = LocalizedText("A descent through the Qur’an and the Ahl al-Bayt - through three stations of the patient heart.")
        ),

        // 02. Before you descend - how this works + the promise
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you descend"),
            promise = LocalizedText("Three stations of the heart lie below - to endure the decree, to accept it, and to find peace within it."),
            leaveWith = LocalizedText("You’ll leave with a map of patience - and a prayer to carry you through your own trial.")
        ),

        // 02b. Threshold - The Three Stations (overview map, before the descent)
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Stations"),
            reference = "al-Kafi · al-Fajr 89:27",
            items = listOf(
                Depth("الصَّبْر", "al-Sabr", LocalizedText("The Enduring"), LocalizedText("To bear the decree without breaking - and to complain of it to God alone."), null, LocalizedText("the heart that holds firm")),
                Depth("الرِّضَا", "al-Rida", LocalizedText("The Accepting"), LocalizedText("To stop wishing the decree were otherwise - and be pleased with it."), null, LocalizedText("the soul that yields")),
                Depth("النَّفْس المُطْمَئِنَّة", "al-Nafs al-Mutma'inna", LocalizedText("The Soul at Peace"), LocalizedText("To return to God serene in the very loss - pleased, and pleasing to Him."), "89:27", LocalizedText("the family who bore it"))
            )
        ),

        // 03. Movement I - The Enduring (movement card)
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins with the clenched heart. Before patience can become contentment, it is simply this: to hold firm, to restrain the self, to bear what has come - and to carry the grief to God alone."),
            bridge = null
        ),

        // 04. Movement I - Those Who Return (al-Baqarah 2:156)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Those Who Return"),
            surah = 2, ayah = 156,
            arabic = "الَّذِينَ إِذَا أَصَابَتْهُم مُّصِيبَةٌ قَالُوا إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ",
            translation = LocalizedText("Those who, when calamity strikes them, say: “Indeed we belong to God, and indeed to Him we return.”"),
            reference = "al-Baqarah · 2 : 156",
            reflection = LocalizedText("This is the first breath of patience - not that the blow does not land, but that the heart, even as it breaks, remembers where it is going. To Him we belong; to Him we return.")
        ),

        // 06. Movement I - The Beautiful Patience (Yusuf 12:86, Ya'qub)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The Beautiful Patience"),
            surah = 12, ayah = 86,
            arabic = "قَالَ إِنَّمَا أَشْكُو بَثِّي وَحُزْنِي إِلَى اللَّهِ",
            translation = LocalizedText("He said: “I complain of my anguish and my grief only to God.”"),
            reference = "Yusuf · 12 : 86",
            reflection = LocalizedText("Ya'qub wept for Yusuf until the light left his eyes - yet in all those years he laid his grief before no one but God. This is sabrun jamil, beautiful patience: not a heart that does not ache, but a grief carried to the right door.")
        ),

        // 07. Movement II - opening card (thread: ENDURE -> ACCEPT)
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have learned to endure the decree."),
            line = LocalizedText("Now - accept it. Rida is the station past endurance: not merely to bear God’s will, but to be pleased with it - to stop wishing the decree were other than it is."),
            bridge = null
        ),

        // 08. Movement II - The Willing Son (al-Saffat 37:102, Isma'il)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Willing Son"),
            surah = 37, ayah = 102,
            arabic = "قَالَ يَا أَبَتِ افْعَلْ مَا تُؤْمَرُ ۖ سَتَجِدُنِي إِن شَاءَ اللَّهُ مِنَ الصَّابِرِينَ",
            translation = LocalizedText("He said: “O my father, do as you are commanded. You will find me, if God wills, among the patient.”"),
            reference = "al-Saffat · 37 : 102",
            reflection = LocalizedText("The son does not merely submit to the knife - he urges his father toward the command, and names himself patient before the trial has even begun. This is the leap from sabr to rida: from “I will bear it” to “do as you are commanded.”")
        ),

        // 09. Movement II - The Excellent Servant (Saad 38:44, Ayyub)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Excellent Servant"),
            surah = 38, ayah = 44,
            arabic = "إِنَّا وَجَدْنَاهُ صَابِرًا ۚ نِّعْمَ الْعَبْدُ ۖ إِنَّهُ أَوَّابٌ",
            translation = LocalizedText("“Indeed We found him patient - an excellent servant. Truly he turned ever back to Us.”"),
            reference = "Saad · 38 : 44",
            reflection = LocalizedText("Stripped of his health, his wealth, his children, Ayyub never once resented his Lord - he only turned back to Him, and back again. And so God Himself names him: an excellent servant. Rida is what turns loss into nearness.")
        ),

        // 10. Movement III - opening card with bridge verse (thread: ACCEPT -> BE AT PEACE)
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have learned to accept it."),
            line = LocalizedText("Now - the summit. Where patience has become peace, and the soul, stripped of everything, returns to its Lord not broken but serene - pleased, and pleasing to Him."),
            bridge = BridgeVerse(
                surah = 89, ayah = 27,
                arabic = "يَا أَيَّتُهَا النَّفْسُ الْمُطْمَئِنَّةُ ارْجِعِي إِلَىٰ رَبِّكِ رَاضِيَةً مَّرْضِيَّةً",
                translation = LocalizedText("O tranquil soul, return to your Lord, pleased and pleasing."),
                reference = "al-Fajr · 89 : 27-28"
            )
        ),

        // 11. Movement III - The Last Night (night before Ashura)
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Last Night"),
            source = LocalizedText("The night before Ashura - al-Irshad of al-Mufid"),
            body = LocalizedText("On the last night, Husayn gathered those with him and lifted his oath from their shoulders: the darkness is a curtain - take it, and go; they want no one but me. Not one of them left. And they passed that night in prayer, standing and bowing, their voices murmuring low - like the humming of bees."),
            reflection = LocalizedText("This is rida made visible. Not people trapped into patience, but people who chose it with open eyes, knowing the morning - and turned their last night on earth into worship.")
        ),

        // 12. Movement III - The Last Prostration (Imam al-Husayn)
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("The Last Prostration"),
            source = LocalizedText("Imam al-Husayn, in his final moments at Karbala - al-Luhuf of Ibn Tawus"),
            arabic = "صَبْرًا عَلَىٰ قَضَائِكَ يَا رَبِّ، لَا إِلَٰهَ سِوَاكَ",
            translation = LocalizedText("“Patience upon Your decree, O my Lord. There is no god but You.”"),
            body = LocalizedText("Bereaved of every son and brother and companion, his body wounded past counting, he lowered his face to the earth of Karbala. No word of complaint left him - only surrender: patience with the decree, and there is no god but You."),
            reflection = LocalizedText("This is the summit - al-nafs al-mutma'inna. Not that the loss had stopped wounding, but that the heart, in the very fire, had returned to its Lord: pleased, and pleasing. Patience had become peace.")
        ),

        // 13. The Close - reflection prompt
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("Return"),
            prompt = LocalizedText("What are you being asked to bear?"),
            placeholder = LocalizedText("An illness, a loss, a long wait, an injustice…"),
            subline = LocalizedText(
                "You've descended all three stations - enduring, accepting, at peace. The map is yours. Before the prayer, name the trial you are carrying.",
                "آپ تینوں منزلوں سے گزر چکے ہیں - صبر، رضا، اطمینان۔ نقشہ اب آپ کا ہے۔ دعا سے پہلے، اُس آزمائش کا نام لیں جو آپ اٹھائے ہوئے ہیں۔",
                "لقد نزلتَ المحطات الثلاث - صبرًا ورضًا وطمأنينة. الخريطة لك. قبل الدعاء، سمِّ البلاء الذي تحمله."
            ),
            nextLabel = LocalizedText("And one prayer", "اور ایک دعا", "ودعاءٌ واحد")
        ),

        // 14. The Close - a prayer in trial (Imam Ja'far al-Sadiq)
        DeepDiveSection.Dua(
            tag = LocalizedText("A Prayer in Trial"),
            intro = LocalizedText("After the prophets, after Karbala - one prayer, in the voice of the sixth Imam, that asks nothing but honesty about our own small patience."),
            arabic = "رَبِّ كَمْ مِنْ نِعْمَةٍ أَنْعَمْتَ بِهَا عَلَيَّ قَلَّ عِنْدَهَا شُكْرِي، وَكَمْ مِنْ بَلِيَّةٍ ابْتَلَيْتَنِي بِهَا قَلَّ لَكَ عِنْدَهَا صَبْرِي، فَيَا مَنْ قَلَّ عِنْدَ نِعْمَتِهِ شُكْرِي فَلَمْ يَحْرِمْنِي، وَيَا مَنْ قَلَّ عِنْدَ بَلِيَّتِهِ صَبْرِي فَلَمْ يَخْذُلْنِي",
            translation = LocalizedText("“My Lord - how many a blessing You gave me, and how little my thanks; how many a trial You tested me with, and how little my patience. O You who did not deprive me though my thanks was little, and did not forsake me though my patience was little.”"),
            source = LocalizedText("Imam Ja'far al-Sadiq · al-Amali of al-Saduq"),
            note = LocalizedText("The towering patience of Karbala is not asked of you. Only this: to bear a little, to return to Him - and to trust that the One who never forsook the patient will not forsake you either."),
            close = LocalizedText("The patience is yours to keep.")
        )
    )
)
