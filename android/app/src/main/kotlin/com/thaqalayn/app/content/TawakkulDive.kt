package com.thaqalayn.app.content

// Fixed content for a theme deep dive. Verbatim Swift->Kotlin port of the
// iOS Content/*.swift source; rendered by DeepDiveScreen. English-only
// prose (LocalizedText falls back to English); Arabic is byte-for-byte.

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.LocalizedText

val tawakkulDive: DeepDive = DeepDive(
    id = "tawakkul",
    titleEn = "Tawakkul",
    titleAr = "تَوَكُّل",
    subtitle = LocalizedText("Reliance - a descent through three motions"),
    estMinutes = 5,
    stageNoun = "Motion",
    acts = listOf(
        ActInfo(1, "العَزْم", "al-'Azm", LocalizedText("The Doing")),
        ActInfo(2, "التَّفْوِيض", "al-Tafwid", LocalizedText("The Handing Over")),
        ActInfo(3, "الكِفَايَة", "al-Kifaya", LocalizedText("The Sufficiency")),
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE"),
            titleAr = "تَوَكُّل",
            titleEn = "Tawakkul",
            subtitle = LocalizedText("Reliance"),
            line = LocalizedText("A descent through the Qur’an and the Ahl al-Bayt - what the hands must do, and what they must let go.")
        ),

        // 02. Before you descend - how this works + the promise
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you descend"),
            promise = LocalizedText("Three motions of reliance lie below - to do your part, to hand the outcome over, and to be carried."),
            leaveWith = LocalizedText("You’ll leave with a map of reliance - and a prayer that hands your affair to the One who holds it.")
        ),

        // 03. Threshold - The Three Motions (overview map, before the descent)
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Motions"),
            reference = "Al Imran 3:159 · al-Talaq 65:3",
            items = listOf(
                Depth("العَزْم", "al-'Azm", LocalizedText("The Doing"), LocalizedText("To rise and take the means at hand - resolve, work, do everything that is yours to do."), null, LocalizedText("the hand that works")),
                Depth("التَّفْوِيض", "al-Tafwid", LocalizedText("The Handing Over"), LocalizedText("When the means end, to place the outcome in His hands - and keep walking."), "40:44", LocalizedText("the hand that releases")),
                Depth("الكِفَايَة", "al-Kifaya", LocalizedText("The Sufficiency"), LocalizedText("To be carried by the One you trusted - when every means has ended."), "65:3", LocalizedText("the family who was carried")),
            )
        ),

        // 04. Movement I - The Doing (movement card)
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins in the hands. Tawakkul is not the folding of arms - it is the work done fully, then signed over to the One who holds the result."),
            bridge = null
        ),

        // 05. Movement I - Resolve, Then Rely (Al Imran 3:159)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Resolve, Then Rely"),
            surah = 3,
            ayah = 159,
            arabic = "فَإِذَا عَزَمْتَ فَتَوَكَّلْ عَلَى اللَّهِ ۚ إِنَّ اللَّهَ يُحِبُّ الْمُتَوَكِّلِينَ",
            translation = LocalizedText("“And when you have resolved, rely upon God. Indeed God loves those who rely.”"),
            reference = "Al Imran · 3 : 159",
            reflection = LocalizedText("The sequence of the verse is the whole teaching. Consult your companions, the Prophet is told in its opening words; then resolve; then rely. Trust is what the resolved hand does with the outcome - not what the idle hand does instead of the work.")
        ),

        // 06. Movement I - The Unanswered Prayer (Imam al-Sadiq)
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Unanswered Prayer"),
            source = LocalizedText("Imam Ja'far al-Sadiq · al-Kafi, on those whose prayer is not answered"),
            body = LocalizedText("Four there are, said the Imam, whose prayer returns to them unanswered. One is the man who sits at home and says, “O God, provide for me” - and is told: have I not commanded you to seek?"),
            reflection = LocalizedText("Tawakkul that skips the work is not trust - it is a request that God do your part, when He has already asked it of you.")
        ),

        // 07. Movement II - opening card (thread: DO -> HAND OVER)
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have done what is yours."),
            line = LocalizedText("Now - the harder motion. Open the hand. Trust is proven not while there is still something you can do, but at the moment your means run out: the sea in front, the army behind."),
            bridge = null
        ),

        // 08. Movement II - The Sea in Front (al-Shu'ara 26:62, Musa)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Sea in Front"),
            surah = 26,
            ayah = 62,
            arabic = "قَالَ كَلَّا ۖ إِنَّ مَعِيَ رَبِّي سَيَهْدِينِ",
            translation = LocalizedText("“He said: Never - indeed my Lord is with me; He will guide me.”"),
            reference = "al-Shu'ara · 26 : 62",
            reflection = LocalizedText("“We are overtaken!” cry the people behind Musa. The sea has not yet split when he answers - trust speaks before the way appears.")
        ),

        // 09. Movement II - The Entrusted Affair (Ghafir 40:44, the believer of Pharaoh's house)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Entrusted Affair"),
            surah = 40,
            ayah = 44,
            arabic = "وَأُفَوِّضُ أَمْرِي إِلَى اللَّهِ ۚ إِنَّ اللَّهَ بَصِيرٌ بِالْعِبَادِ",
            translation = LocalizedText("“And I entrust my affair to God. Indeed God is ever seeing of His servants.”"),
            reference = "Ghafir · 40 : 44",
            reflection = LocalizedText("The same court Musa fled - Pharaoh's - now a lone believer speaks from within it. His warning finished, he hands the consequence over. The very next verse answers him: so God protected him from the evils they plotted.")
        ),

        // 10. Movement III - opening card with bridge verse (thread: HAND OVER -> BE CARRIED)
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have opened the hand."),
            line = LocalizedText("Now - what receives it. On the other side of the release is not a void but a Trustee - and His promise is not always the outcome you asked for. It is Himself."),
            bridge = BridgeVerse(
                surah = 65,
                ayah = 3,
                arabic = "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
                translation = LocalizedText("And whoever relies upon God - He is sufficient for him."),
                reference = "al-Talaq · 65 : 3"
            )
        ),

        // 11. Movement III - He Answers (revelation to Dawud, al-Kafi)
        DeepDiveSection.Response(
            act = 3,
            replyingTo = LocalizedText("To the one who lets go of every rope but His"),
            arabic = "جَعَلْتُ لَهُ الْمَخْرَجَ مِنْ بَيْنِهِنَّ",
            words = LocalizedText("“Whenever a servant of Mine takes refuge in Me and not in My creation - and I know it from his intention - then even if the heavens and the earth and all within them plotted against him, I would make for him a way out from among them all.”"),
            source = LocalizedText("His revelation to the prophet Dawud · al-Kafi"),
            reflection = LocalizedText("Not that the plot stops - but that the way out is His to make. And whoever grips creation's ropes instead finds that the rope from heaven has been cut.")
        ),

        // 12. Movement III - The Verse He Answered With (al-A'raf 7:196, morning of Ashura)
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("His Answer at Dawn"),
            surah = 7,
            ayah = 196,
            arabic = "إِنَّ وَلِيِّيَ اللَّهُ الَّذِي نَزَّلَ الْكِتَابَ ۖ وَهُوَ يَتَوَلَّى الصَّالِحِينَ",
            translation = LocalizedText("“Indeed my Protector is God, who sent down the Book - and He takes care of the righteous.”"),
            reference = "al-A'raf · 7 : 196",
            reflection = LocalizedText("Dawn at Karbala. The army is arrayed, and the histories record Husayn ending his address to it with this verse. He does not count their swords - he names his Protector.")
        ),

        // 13. Movement III - The Trust in Every Distress (Imam al-Husayn)
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("The Trust in Every Distress"),
            source = LocalizedText("Imam al-Husayn, the morning of Ashura - al-Irshad of al-Mufid"),
            arabic = "اللَّهُمَّ أَنْتَ ثِقَتِي فِي كُلِّ كَرْبٍ، وَرَجَائِي فِي كُلِّ شِدَّةٍ",
            translation = LocalizedText("“O God, You are my trust in every distress, and my hope in every hardship.”"),
            body = LocalizedText("As the army closed in, he raised his hands - not for rescue, but to name the One who held him: in every distress You are my trust, in every hardship my hope."),
            reflection = LocalizedText("No sea split that morning; no rescue came. The way out promised to Dawud was made - not through the army, but through Him: the trust did not break, because it had never been placed in the outcome. It was placed in Him.")
        ),

        // 14. The Close - the release (interactive entrusting)
        DeepDiveSection.Release(
            tag = LocalizedText("The Release"),
            prompt = LocalizedText("What are you gripping?"),
            subline = LocalizedText("A decision, a diagnosis, a debt, a child. Name it in your heart - you have carried it long enough."),
            arabic = "أُفَوِّضُ أَمْرِي إِلَى اللَّهِ",
            translation = LocalizedText("I entrust my affair to God."),
            reference = "Ghafir · 40 : 44",
            note = LocalizedText("It is in His hands now - the hands that do not drop what they hold."),
            nextLabel = LocalizedText("And one prayer")
        ),

        // 15. The Close - a prayer of fleeing to Him (Imam al-Sajjad)
        DeepDiveSection.Dua(
            tag = LocalizedText("A Prayer of Fleeing to Him"),
            intro = LocalizedText("After the sea, after Karbala - one prayer, in the voice of the fourth Imam: the whole descent in two lines."),
            arabic = "اللَّهُمَّ إِنِّي أَخْلَصْتُ بِانْقِطَاعِي إِلَيْكَ، وَأَقْبَلْتُ بِكُلِّي عَلَيْكَ",
            translation = LocalizedText("“My God, I have cut myself off from all but You, and turned toward You with the whole of myself.”"),
            source = LocalizedText("Imam Ali ibn al-Husayn · al-Sahifa al-Sajjadiyya, Dua 28"),
            note = LocalizedText("The great entrustings are not asked of you this morning. Only this: one grip loosened, one affair signed over - and the rest left to the One who is enough."),
            close = LocalizedText("The trust is yours to keep.")
        ),
    )
)
