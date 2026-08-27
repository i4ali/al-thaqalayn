package com.thaqalayn.app.content

// Fixed content for a theme deep dive. Verbatim Swift->Kotlin port of the
// iOS Content/*.swift source; rendered by DeepDiveScreen. English-only
// prose (LocalizedText falls back to English); Arabic is byte-for-byte.

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.LocalizedText

val kisaDive: DeepDive = DeepDive(
    id = "kisa",
    titleEn = "al-Kisa",
    titleAr = "الكِسَاء",
    subtitle = LocalizedText("The Cloak - a gathering through three circles"),
    estMinutes = 5,
    stageNoun = "Circle",
    descendCta = "Enter",
    beginCta = "Enter the gathering",
    mapLine = "One cloak. Three circles around it.",
    stageWord = "Circle",
    endLine = "The gathering disperses.",
    scrollHint = "Scroll to draw nearer",
    acts = listOf(
        ActInfo(1, "البَيْت", "al-Bayt", LocalizedText("The House")),
        ActInfo(2, "الخَمْسَة", "al-Khamsa", LocalizedText("The Five")),
        ActInfo(3, "المَحْفِل", "al-Mahfil", LocalizedText("The Gathering")),
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE"),
            titleAr = "الكِسَاء",
            titleEn = "al-Kisa",
            subtitle = LocalizedText("The Cloak"),
            line = LocalizedText("One day in Madina, one cloak, five beneath it. The story is told by the Prophet's ﷺ own daughter, Fatima al-Zahra - and it ends in a promise to every gathering that retells it.")
        ),

        // 02. Before you enter
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you enter"),
            promise = LocalizedText("Three circles lie ahead - the house that gathered a family one by one, the five people God says creation itself was made for, and the gathering that has never ended."),
            leaveWith = LocalizedText("You'll leave inside the reach of a promise sworn beneath the cloak - and with the Prophet's ﷺ own prayer over his family, to keep.")
        ),

        // 03. Threshold - The Three Circles
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Circles"),
            reference = "Hadith al-Kisa · Awalim al-Ulum",
            items = listOf(
                Depth("البَيْت", "al-Bayt", LocalizedText("The House"), LocalizedText("The Prophet ﷺ, feeling a weakness in his body, asks his daughter to cover him. Then, one by one, the family arrives - each met by a fragrance they know, each entering by permission."), null, LocalizedText("the door love knocks on")),
                Depth("الخَمْسَة", "al-Khamsa", LocalizedText("The Five"), LocalizedText("The two ends of the cloak lifted toward the sky - and heaven tells the angels why anything was made."), null, LocalizedText("the family creation was made for")),
                Depth("المَحْفِل", "al-Mahfil", LocalizedText("The Gathering"), LocalizedText("A promise sworn twice over: wherever this story is retold, mercy descends on the ones who tell it."), "Awalim al-Ulum", LocalizedText("the circle that has never closed")),
            )
        ),

        // 04. Circle I - The House
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins with the smallest mercy a house knows. The Messenger of God ﷺ comes to his daughter's door - and what happened inside was kept for us in her voice. Everyone beneath that cloak could have told this story. The one who tells it is Fatima."),
            bridge = null
        ),

        // 05. Circle I - The Weakness
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Weakness"),
            source = LocalizedText("Fatima al-Zahra · Hadith al-Kisa - Awalim al-Ulum"),
            body = LocalizedText("My father came to me, she says, and greeted me: peace be upon you, Fatima. Then he said: I feel in my body a weakness. Bring me the Yemeni cloak, and cover me with it. So I covered him - and as I did, I looked, and his face was shining beneath it like the full moon on its fullest night."),
            reflection = LocalizedText("The story opens with the Prophet ﷺ asking to be covered - the one the whole world leans on, asking his daughter for shelter. First the cloak is only that: warmth laid over a tired father. Heaven is about to make something immense of it - and it begins as the plainest kindness any house knows.")
        ),

        // 06. Circle I - The Fragrance (Yusuf 12:94)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The Fragrance"),
            surah = 12,
            ayah = 94,
            arabic = "قَالَ أَبُوهُمْ إِنِّي لَأَجِدُ رِيحَ يُوسُفَ",
            translation = LocalizedText("[When the caravan set out,] their father said: truly, I find the fragrance of Yusuf."),
            reference = "Yusuf · 12 : 94",
            reflection = LocalizedText("Yusuf's brothers were carrying his shirt home when their father, far away, caught the scent of his lost son - love recognizes before sight does. Hold that. In this house, each one who now comes to the door will know, by a fragrance, who is beneath the cloak - before anyone says a word.")
        ),

        // 07. Circle I - The Arrivals
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Arrivals"),
            source = LocalizedText("Fatima al-Zahra · Hadith al-Kisa - Awalim al-Ulum"),
            body = LocalizedText("Within the hour, she says, Hasan was at the door. He greeted me - and then: mother, I smell a sweet fragrance here, like the fragrance of my grandfather, the Messenger of God. Yes, I told him - your grandfather is beneath the cloak. And he did not run to him. He stopped at the cloak's edge and asked permission to enter. My son, my father answered, master of my Fountain - I give you permission. Husayn came, and knew the same fragrance: my son, who will plead for my nation. Then Ali, my husband: my brother and my successor. Then I rose myself - and asked permission, in my own house, to sit beneath a cloak with my own family: my daughter, part of me. Each of us heard the same words: I give you permission."),
            reflection = LocalizedText("No one sent for them; each arrived on an ordinary errand, and a fragrance told them who was there. And at the edge of the cloak every one of them - the grandsons, the Commander of the Faithful, the daughter of the house - stopped and asked. Around this cloak, love and reverence are one motion: no one enters unasked.")
        ),

        // 08. Circle II - The Five
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("The house is full."),
            line = LocalizedText("Five beneath one Yemeni cloak - a grandfather, his daughter, her husband, their two sons. Then, she says, my father took hold of the two ends of the cloak, and pointed with his right hand toward the sky."),
            bridge = null
        ),

        // 09. Circle II - The Two Ends
        DeepDiveSection.Narration(
            act = 2,
            tag = LocalizedText("The Two Ends"),
            source = LocalizedText("The Messenger of God ﷺ, beneath the cloak · Hadith al-Kisa"),
            body = LocalizedText("O God, he prays, these are the people of my house - my own and my nearest kin. Their flesh is my flesh and their blood is my blood; what pains them pains me, and what grieves them grieves me. They are of me, and I am of them. Then comes the plea the whole prayer has been rising toward: he asks God to remove every impurity from them, and to purify them completely."),
            reflection = LocalizedText("He binds five lives into one flesh, then lifts the whole of it upward - the two ends of the cloak raised like an offering. And hold on to the last words of that prayer. His voice will not be the last to say them.")
        ),

        // 10. Circle II - He answers the Prophet ﷺ (the creation inventory)
        DeepDiveSection.Response(
            act = 2,
            replyingTo = LocalizedText("To the prayer raised beneath the cloak"),
            arabic = "مَا خَلَقْتُ سَمَاءً مَبْنِيَّةً وَلَا أَرْضًا مَدْحِيَّةً إِلَّا فِي مَحَبَّةِ هٰؤُلَاءِ الْخَمْسَةِ",
            words = LocalizedText("“O My angels, O dwellers of My heavens: I did not create a built sky, nor an outstretched earth, nor an illumined moon, nor a shining sun, nor a turning heaven, nor a flowing sea, nor a sailing ship - except for the love of these Five beneath the cloak.”"),
            source = LocalizedText("His word to the angels · Hadith al-Kisa - Awalim al-Ulum"),
            reflection = LocalizedText("The answer is not sent down to the house - it is announced across the heavens. He names creation piece by piece - sky, earth, moon, sun, sea - and gives every piece the same reason: love of these five. The sky you have lived your whole life under was raised for the five beneath that cloak.")
        ),

        // 11. Circle II - THE SUMMIT - The Naming
        DeepDiveSection.Climax(
            act = 2,
            tag = LocalizedText("The Naming"),
            source = LocalizedText("Hadith al-Kisa - Awalim al-Ulum"),
            arabic = "هُمْ فَاطِمَةُ وَأَبُوهَا وَبَعْلُهَا وَبَنُوهَا",
            translation = LocalizedText("“They are Fatima, her father, her husband, and her sons.”"),
            body = LocalizedText("The prayer beneath the cloak has scarcely ended when a question rises among the angels. It is Jibra'il who asks - the trusted angel who carries God's word down to His prophets: my Lord, who is beneath the cloak?"),
            reflection = LocalizedText("Before the naming, God gives them a title: the household of prophethood, the wellspring of the message. Then the names - and every one leans on hers: her father, her husband, her sons. The one who kept this story for us is the one the answer is built around. Then Jibra'il asks what no angel has ever asked: permission to descend, to be the sixth of them. It is granted - and even he stops at the cloak's edge, as the children did, and asks again. He enters carrying a verse.")
        ),

        // 12. Circle II - What the Sixth Carried (al-Ahzab 33:33)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("What the Sixth Carried"),
            surah = 33,
            ayah = 33,
            arabic = "إِنَّمَا يُرِيدُ اللَّهُ لِيُذْهِبَ عَنكُمُ الرِّجْسَ أَهْلَ الْبَيْتِ وَيُطَهِّرَكُمْ تَطْهِيرًا",
            translation = LocalizedText("God only desires to remove all impurity from you, O People of the House, and to purify you, a thorough purifying."),
            reference = "al-Ahzab · 33 : 33",
            reflection = LocalizedText("The words he prayed beneath the cloak come back as revelation - now spoken as God's own desire. And the Arabic turns as it lands: the verses around this clause address the Prophet's ﷺ wives in the feminine; here it shifts to the masculine plural - the form the tradition hears as the five beneath the cloak. For months afterward, on his way to the dawn prayer, he would stop at Fatima's door and call: to prayer, O People of the House! - and recite this clause at the one door it named.")
        ),

        // 13. Circle III - The Gathering
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("The verse has been delivered."),
            line = LocalizedText("Beneath the cloak one question remains, and it is Ali who asks it: O Messenger of God, what merit does this sitting of ours beneath one cloak have in the sight of God?"),
            bridge = null
        ),

        // 14. Circle III - The Only Reward (al-Shura 42:23)
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("The Only Reward"),
            surah = 42,
            ayah = 23,
            arabic = "قُل لَّا أَسْأَلُكُمْ عَلَيْهِ أَجْرًا إِلَّا الْمَوَدَّةَ فِي الْقُرْبَىٰ",
            translation = LocalizedText("Say: I ask of you no reward for it - only love of the nearest kin."),
            reference = "al-Shura · 42 : 23",
            reflection = LocalizedText("Before the Prophet ﷺ answers, hold one verse beside Ali's question. For the whole weight of prophethood, one payment is named - not tribute, not rank: love of the nearest kin. The five beneath the cloak are that kin. And this love, as he is about to swear, is not a debt collected from you - it is a door opened to you.")
        ),

        // 15. Circle III - The Promise
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Promise"),
            source = LocalizedText("The Messenger of God ﷺ · Hadith al-Kisa - Awalim al-Ulum"),
            body = LocalizedText("By Him who sent me with the truth, he swears: wherever this story of ours is told, in any gathering of people who love us, mercy comes down on them - and the angels gather around them, asking forgiveness for them, until they rise to leave. Then he swears a second time: no one sits there carrying a worry without God lifting it; no one grieving without God easing the grief; no one who came with a need without God granting it. And Ali answers after each oath, his second answer wider than the first: then we have won, and our Shia - those who hold to us - have won, in this world and the next, by God, the Lord of the Ka'ba."),
            reflection = LocalizedText("The story has just been told - here. And the oath was sworn with no date and no limit: over every gathering that would ever retell it, in rooms not yet built, in centuries not yet come. The gathering in the oath is this one. Whatever worry you carried in with you, whatever need - the promise was made wide enough to reach it, long before you were born.")
        ),

        // 16. The Five Names (new interactive beat)
        DeepDiveSection.Salawat(
            tag = LocalizedText("The Five Names"),
            prompt = LocalizedText("Answer the way every gathering answers."),
            subline = LocalizedText("When the five are named, no gathering stays silent. Five lights wait - one for each soul beneath the cloak. Tap, and they light one by one, in the order the cloak gathered them."),
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَآلِ مُحَمَّدٍ",
            translation = LocalizedText("“O God, bless Muhammad and the family of Muhammad.”"),
            reference = "The salawat of every gathering",
            note = LocalizedText("God said He made creation itself for love of these five. Greeting them by name is the smallest act of that love there is - and by the promise sworn beneath the cloak, no gathering that says them is left alone."),
            nextLabel = LocalizedText("And the prayer to keep")
        ),

        // 17. The Close - The Prayer of the Cloak
        DeepDiveSection.Dua(
            tag = LocalizedText("The Prayer of the Cloak"),
            intro = LocalizedText("Before the verse, before the angel - the Prophet ﷺ prayed over his family as a father, holding the two ends of the cloak toward the sky. Here is that prayer, whole - yours to keep."),
            arabic = "اللَّهُمَّ إِنَّ هٰؤُلَاءِ أَهْلُ بَيْتِي وَخَاصَّتِي وَحَامَّتِي، لَحْمُهُمْ لَحْمِي وَدَمُهُمْ دَمِي، يُؤْلِمُنِي مَا يُؤْلِمُهُمْ وَيَحْزُنُنِي مَا يَحْزُنُهُمْ، أَنَا حَرْبٌ لِمَنْ حَارَبَهُمْ وَسِلْمٌ لِمَنْ سَالَمَهُمْ، وَعَدُوٌّ لِمَنْ عَادَاهُمْ وَمُحِبٌّ لِمَنْ أَحَبَّهُمْ، إِنَّهُمْ مِنِّي وَأَنَا مِنْهُمْ، فَاجْعَلْ صَلَوَاتِكَ وَبَرَكَاتِكَ وَرَحْمَتَكَ وَغُفْرَانَكَ وَرِضْوَانَكَ عَلَيَّ وَعَلَيْهِمْ، وَأَذْهِبْ عَنْهُمُ الرِّجْسَ وَطَهِّرْهُمْ تَطْهِيرًا",
            translation = LocalizedText("“O God, these are the people of my house, my own and my nearest kin. Their flesh is my flesh and their blood is my blood; what pains them pains me, and what grieves them grieves me. I am at war with whoever wars on them, at peace with whoever is at peace with them; an enemy to whoever is their enemy, and a lover of whoever loves them. They are of me and I am of them - so set Your blessings, Your graces, Your mercy, Your forgiveness and Your good pleasure upon me and upon them, and remove from them all impurity, and purify them, a thorough purifying.”"),
            source = LocalizedText("The Messenger of God ﷺ · Hadith al-Kisa - Awalim al-Ulum of al-Bahrani"),
            note = LocalizedText("Families keep this story for Thursday night - the eve of Friday - reciting it in homes where a need is carried or a grief is heavy, for the sake of the promise sworn beneath the cloak. You have heard it whole now. Wherever you retell it, that room joins the gatherings he swore over."),
            close = LocalizedText("The promise is yours to keep.")
        ),
    )
)
