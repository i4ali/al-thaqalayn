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

val shukrDive: DeepDive = DeepDive(
    id = "shukr",
    titleEn = "Shukr",
    titleAr = "شُكْر",
    subtitle = LocalizedText("Gratitude - a descent through three tongues"),
    estMinutes = 5,
    stageNoun = "Tongue",
    acts = listOf(
        ActInfo(1, "القَلْب", "al-Qalb", LocalizedText("The Recognizing")),
        ActInfo(2, "اللِّسَان", "al-Lisan", LocalizedText("The Saying")),
        ActInfo(3, "الجَوَارِح", "al-Jawarih", LocalizedText("The Doing")),
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE"),
            titleAr = "شُكْر",
            titleEn = "Shukr",
            subtitle = LocalizedText("Gratitude"),
            line = LocalizedText("A descent through the Qur'an and the Ahl al-Bayt - what the heart must see, what the tongue must say, what the body must answer.")
        ),

        // 02. Before you descend
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you descend"),
            promise = LocalizedText("Three tongues of thanks lie below - the heart that recognizes, the tongue that praises, and the limbs that answer."),
            leaveWith = LocalizedText("You'll leave with a map of gratitude - and a prayer that hands the rest back to Him.")
        ),

        // 03. Threshold - The Three Tongues
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Tongues"),
            reference = "al-Kafi · Saba 34:13",
            items = listOf(
                Depth("القَلْب", "al-Qalb", LocalizedText("The Recognizing"), LocalizedText("To see the gift as gift - and the Giver behind it. Thanks begins before a word is said."), null, LocalizedText("the heart that sees the Giver")),
                Depth("اللِّسَان", "al-Lisan", LocalizedText("The Saying"), LocalizedText("To speak the praise aloud - and tell of the blessing."), "93:11", LocalizedText("the tongue that praises")),
                Depth("الجَوَارِح", "al-Jawarih", LocalizedText("The Doing"), LocalizedText("To answer the gift with the body - to stand, to serve, to give."), "34:13", LocalizedText("the family who answered with everything")),
            )
        ),

        // 04. Movement I - The Recognizing
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins behind the ribs. Before gratitude has words, it is a kind of seeing - the gift caught in the act of arriving, and the Giver's hand still on it."),
            bridge = null
        ),

        // 05. Movement I - The First Gifts (al-Nahl 16:78)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The First Gifts"),
            surah = 16,
            ayah = 78,
            arabic = "وَاللَّهُ أَخْرَجَكُم مِّن بُطُونِ أُمَّهَاتِكُمْ لَا تَعْلَمُونَ شَيْئًا وَجَعَلَ لَكُمُ السَّمْعَ وَالْأَبْصَارَ وَالْأَفْئِدَةَ لَعَلَّكُمْ تَشْكُرُونَ",
            translation = LocalizedText("And God brought you out of your mothers' wombs knowing nothing - and He made for you hearing, and sight, and hearts, that you might give thanks."),
            reference = "al-Nahl · 16 : 78",
            reflection = LocalizedText("You arrived owning nothing - not even the knowing. Hearing, sight, a heart: the verse lists the first gifts, then names what they were for. Gratitude is not an ornament on the equipment. It is what the equipment was issued for.")
        ),

        // 06. Movement I - The Thanks of the Heart (al-Kafi)
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Thanks of the Heart"),
            source = LocalizedText("Imam Ja'far al-Sadiq · al-Kafi, the book of thanks"),
            body = LocalizedText("When God grants a servant a blessing, said the Imam, and he recognizes it with his heart - he has already given its thanks."),
            reflection = LocalizedText("Thanks is born before a word is spoken - in the heart, where the gift is seen and the Giver named. Born, not finished - two tongues remain.")
        ),

        // 07. Movement II - The Saying
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have seen the Giver."),
            line = LocalizedText("Now - say it. What the heart knows in silence, the tongue brings into the open: the blessing told, the Giver named aloud."),
            bridge = null
        ),

        // 08. Movement II - The Covenant of Increase (Ibrahim 14:7)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Covenant of Increase"),
            surah = 14,
            ayah = 7,
            arabic = "وَإِذْ تَأَذَّنَ رَبُّكُمْ لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            translation = LocalizedText("And when your Lord proclaimed: If you give thanks, I will surely increase you."),
            reference = "Ibrahim · 14 : 7",
            reflection = LocalizedText("Proclaimed - not whispered. Thanks is the one debt that grows by being paid: thank Him, and He gives you more to thank for. And the increase is not always more of the gift. Sometimes it is more of the seeing.")
        ),

        // 09. Movement II - Tell of It (al-Duha 93:11)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("Tell of It"),
            surah = 93,
            ayah = 11,
            arabic = "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ",
            translation = LocalizedText("And as for the blessing of your Lord - tell of it."),
            reference = "al-Duha · 93 : 11",
            reflection = LocalizedText("Four words, spoken first to the Prophet - an orphan who had just been given everything. Gratitude has a voice - not the boast that forgets the Giver, but the telling that names Him: this came from my Lord.")
        ),

        // 10. Movement II - He Answers (Musa, al-Kafi)
        DeepDiveSection.Response(
            act = 2,
            replyingTo = LocalizedText("To Musa, who asked: how shall I thank You, when even my thanks is itself Your gift?"),
            arabic = "الْآنَ شَكَرْتَنِي حِينَ عَلِمْتَ أَنَّ ذَلِكَ مِنِّي",
            words = LocalizedText("Now you have thanked Me - now that you know that even the thanks is from Me."),
            source = LocalizedText("His words to Musa · al-Kafi"),
            reflection = LocalizedText("The ladder of thanks has no top rung - every thanks is another gift, owing another thanks. He does not ask you to reach the top. He asks you to know where the ladder stands.")
        ),

        // 11. Movement III - The Doing + bridge 34:13
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have said it."),
            line = LocalizedText("Now - past the saying. The command to the most gifted house on earth was not to say thanks but to work it. In the end, thanks is something the body does."),
            bridge = BridgeVerse(
                surah = 34,
                ayah = 13,
                arabic = "اعْمَلُوا آلَ دَاوُودَ شُكْرًا وَقَلِيلٌ مِّنْ عِبَادِيَ الشَّكُورُ",
                translation = LocalizedText("Work, O family of Dawud, in thanks - and few of My servants are deeply grateful."),
                reference = "Saba · 34 : 13"
            )
        ),

        // 12. Movement III - The Grateful Servant (al-Kafi)
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Grateful Servant"),
            source = LocalizedText("The Messenger of God ﷺ · narrated from Imam al-Baqir, al-Kafi"),
            body = LocalizedText("The Prophet ﷺ stood in the night prayer on the tips of his toes, until standing itself was a labor. He was asked: but you are already forgiven - everything past, everything to come - why this? He said: “Shall I not be a grateful servant?”"),
            reflection = LocalizedText("Forgiveness did not retire his worship - it changed what the worship was. No longer a plea; a thank-you. The most truthful tongue on earth was not enough for him. He thanked with his feet.")
        ),

        // 13. Movement III - The Thanks That Returns (al-Insan 76:22)
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("The Thanks That Returns"),
            surah = 76,
            ayah = 22,
            arabic = "إِنَّ هَٰذَا كَانَ لَكُمْ جَزَاءً وَكَانَ سَعْيُكُم مَّشْكُورًا",
            translation = LocalizedText("Indeed this is a reward for you - and your striving has been thanked."),
            reference = "al-Insan · 76 : 22",
            reflection = LocalizedText("Spoken in the surah given to the Prophet's own house - the family who fed the hungry for three nights and asked nothing back. Read it slowly: God, who needs nothing, thanks. The thanks you send up does not vanish. It returns.")
        ),

        // 14. Movement III - The Praise in the Dark (eve of Ashura)
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("The Praise in the Dark"),
            source = LocalizedText("Imam al-Husayn, the eve of Ashura - al-Irshad of al-Mufid"),
            arabic = "أُثْنِي عَلَى اللَّهِ أَحْسَنَ الثَّنَاءِ، وَأَحْمَدُهُ عَلَى السَّرَّاءِ وَالضَّرَّاءِ",
            translation = LocalizedText("“I praise God with the best of praise, and I thank Him in ease and in hardship.”"),
            body = LocalizedText("The army is across the plain and the morning is known. He gathers his family and companions at nightfall - and opens with praise: for prophethood, for the Qur'an. For hearing, and sight, and hearts."),
            reflection = LocalizedText("Hearing, sight, hearts - the first gifts, where this descent began. He named them in thanks on the last night they would be his. Anyone can give thanks for the gift. He thanked the Giver while the gifts were being taken.")
        ),

        // 15. The Count (new interactive beat)
        DeepDiveSection.Count(
            tag = LocalizedText("The Count"),
            prompt = LocalizedText("Count what He has given you."),
            subline = LocalizedText("He counted his blessings on the night they were being taken. Yours are still in your hands - this breath, your sight, a person who loves you. Tap: one blessing at a time."),
            arabic = "وَإِن تَعُدُّوا نِعْمَةَ اللَّهِ لَا تُحْصُوهَا",
            translation = LocalizedText("And if you count the blessings of God, you cannot number them."),
            reference = "al-Nahl · 16 : 18",
            note = LocalizedText("The count was never going to finish. It was only ever going to point - at the One whose giving outruns it."),
            nextLabel = LocalizedText("And one prayer")
        ),

        // 16. The Close - a prayer of the unfinished thanks (Sahifa 37)
        DeepDiveSection.Dua(
            tag = LocalizedText("A Prayer of the Unfinished Thanks"),
            intro = LocalizedText("After the heart, the tongue, the limbs - after Karbala - one prayer, in the voice of the fourth Imam: the confession that thanks never reaches its end."),
            arabic = "اللَّهُمَّ إِنَّ أَحَدًا لَا يَبْلُغُ مِنْ شُكْرِكَ غَايَةً إِلَّا حَصَلَ عَلَيْهِ مِنْ إِحْسَانِكَ مَا يُلْزِمُهُ شُكْرًا",
            translation = LocalizedText("“O God, no one ever reaches an end in thanking You - for with every thanks, more of Your goodness settles upon him, and binds him to thank You again.”"),
            source = LocalizedText("Imam Ali ibn al-Husayn · al-Sahifa al-Sajjadiyya, Dua 37"),
            note = LocalizedText("The full count is not asked of you tonight. Only this: one blessing seen, one alhamdulillah said aloud - and the rest left to the One who accepts the little and gives the much."),
            close = LocalizedText("The thanks is yours to keep.")
        ),
    )
)
