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

val salahDive: DeepDive = DeepDive(
    id = "salah",
    titleEn = "Salah",
    titleAr = "صَلَاة",
    subtitle = LocalizedText("Prayer - an ascent through three names"),
    estMinutes = 5,
    stageNoun = "Name",
    descendCta = "Ascend",
    beginCta = "Begin the ascent",
    mapLine = "The map for everything above.",
    stageWord = "Name",
    endLine = "The ascent ends.",
    scrollHint = "Scroll to climb higher",
    scrollHintAscending = true,
    acts = listOf(
        ActInfo(1, "المِعْرَاج", "al-Mi'raj", LocalizedText("The Ascent")),
        ActInfo(2, "المُنَاجَاة", "al-Munajat", LocalizedText("The Conversation")),
        ActInfo(3, "القُرْبَان", "al-Qurban", LocalizedText("The Offering")),
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE"),
            titleAr = "صَلَاة",
            titleEn = "Salah",
            subtitle = LocalizedText("Prayer"),
            line = LocalizedText("An ascent through the Qur'an and the Ahl al-Bayt - the household of the Prophet ﷺ - where the prayer was given, what it truly is, and what it is worth.")
        ),

        // 02. Before you climb - how this works + the promise
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you climb"),
            promise = LocalizedText("The tradition gave the prayer three names - the ascent, the conversation, the offering. The climb ahead passes through all three."),
            leaveWith = LocalizedText("You'll leave seeing the five prayers differently - and with a gift from the Prophet's ﷺ family to carry into every one.")
        ),

        // 03. Threshold - The Three Names (overview map, before the climb)
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Names"),
            reference = "al-Faqih · Uyun al-Rida · Nahj al-Balagha",
            items = listOf(
                Depth("المِعْرَاج", "al-Mi'raj", LocalizedText("The Ascent"), LocalizedText("Given above the seven heavens, on the night the Prophet ﷺ rose past them - and carried down for you."), null, LocalizedText("the Prophet ﷺ who carried it down")),
                Depth("المُنَاجَاة", "al-Munajat", LocalizedText("The Conversation"), LocalizedText("To stand and speak - and be spoken back to."), null, LocalizedText("the servant who is answered")),
                Depth("القُرْبَان", "al-Qurban", LocalizedText("The Offering"), LocalizedText("An offering to draw near to Him - guarded, whatever it costs."), null, LocalizedText("the family who paid its price")),
            )
        ),

        // 04. Name I - The Ascent (movement card)
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins at the top. Every revelation came down to the Prophet ﷺ - once, he went up. What he carried back down from that night above the heavens was this prayer - and the name never left it: the tradition still calls the prayer the believer's ascent."),
            bridge = null
        ),

        // 05. Name I - The Night of Fifty (the Mi'raj origin)
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Night of Fifty"),
            source = LocalizedText("Imam Ali ibn al-Husayn · Man la yahduruh al-Faqih"),
            body = LocalizedText("On the night the Prophet ﷺ was taken up through the heavens, fifty prayers were written upon his people. He would not ask his Lord for less - it was Musa, whom he had passed among the heavens, who pressed him to go back and ask that the number be lightened. And when fifty had become five, the word came down: “They are five, worth fifty. The word is not changed with Me.”"),
            reflection = LocalizedText("This is its birth: not a burden imposed, but a mercy pleaded down - with the full reward left attached. Five, carrying fifty: lightened in number, whole in reward. The prayer arrived as a gift twice over.")
        ),

        // 06. Name I - What It Is For (Ta-Ha 20:14)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("What It Is For"),
            surah = 20,
            ayah = 14,
            arabic = "إِنَّنِي أَنَا اللَّهُ لَا إِلَٰهَ إِلَّا أَنَا فَاعْبُدْنِي وَأَقِمِ الصَّلَاةَ لِذِكْرِي",
            translation = LocalizedText("Indeed I - I am God; there is no god but Me. So worship Me, and establish the prayer for My remembrance."),
            reference = "Ta-Ha · 20 : 14",
            reflection = LocalizedText("Musa, alone in the sacred valley, called by a Voice out of a fire. The command that follows “I am God” is worship - and the one act it names is the prayer. For My remembrance: not because He forgets you, but because you forget Him. Five times a day, the forgetting is interrupted.")
        ),

        // 07. Name II - opening card (thread: GIVEN -> STAND INSIDE IT)
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have seen where it was given."),
            line = LocalizedText("Now - stand inside it. The second name means the intimate conversation. You thought you were reciting into silence - the tradition says the one who prays is conversing with his Lord. And He answers back."),
            bridge = null
        ),

        // 08. Name II - Before Whom You Stand (Imam Zayn al-Abidin)
        DeepDiveSection.Narration(
            act = 2,
            tag = LocalizedText("Before Whom You Stand"),
            source = LocalizedText("Imam Ali ibn al-Husayn · al-Irshad of al-Mufid"),
            body = LocalizedText("When Imam Ali ibn al-Husayn, the fourth Imam, made the ablution before prayer, his face would turn pale. His family asked: what is this that comes over you? He said: “Do you know before Whom I am preparing to stand?”"),
            reflection = LocalizedText("He was not afraid of the prayer - he was awake to it. The words are the same ones you say. The difference is that he knew Who was listening.")
        ),

        // 09. Name II - He Answers (the divided Fatiha, Uyun Akhbar al-Rida)
        DeepDiveSection.Response(
            act = 2,
            replyingTo = LocalizedText("To the servant who stands and says: All praise belongs to God, Lord of the worlds"),
            arabic = "حَمِدَنِي عَبْدِي",
            words = LocalizedText("“I have divided the Opening of the Book between Me and My servant - half is Mine, half is his, and his is what he asks. When he begins with My name: it is binding on Me to complete his affairs. When he praises Me: My servant has praised Me.”"),
            source = LocalizedText("The hadith qudsi of the Fatiha · Uyun Akhbar al-Rida of al-Saduq"),
            reflection = LocalizedText("The Opening of the Book - the Fatiha you recite in every prayer - was never a monologue. You spoke one half; He answered the other, line for line, even the ones you rushed half-asleep. You have never once prayed unanswered.")
        ),

        // 10. Name II - The Nearest Point (al-Alaq 96:19, excerpt)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Nearest Point"),
            surah = 96,
            ayah = 19,
            arabic = "وَاسْجُدْ وَاقْتَرِبْ",
            translation = LocalizedText("Prostrate - and draw near."),
            reference = "al-Alaq · 96 : 19",
            reflection = LocalizedText("Imam al-Rida said: a servant is never nearer to God than in prostration - and he named this verse as the proof. The ladder runs inverted: its highest rung is the floor. The world calls it lowering yourself. The prayer calls it arriving.")
        ),

        // 11. Name III - opening card with bridge verse (thread: ANSWERED -> THE COST)
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have heard Him answer - and felt how near He lets you come."),
            line = LocalizedText("Now - the costly name. The prayer, said Imam Ali, is the offering of every God-conscious soul. An offering is weighed by what it costs the one who brings it."),
            bridge = BridgeVerse(
                surah = 2,
                ayah = 238,
                arabic = "حَافِظُوا عَلَى الصَّلَوَاتِ وَالصَّلَاةِ الْوُسْطَىٰ وَقُومُوا لِلَّهِ قَانِتِينَ",
                translation = LocalizedText("Guard the prayers - and the middle prayer - and stand before God devoutly."),
                reference = "al-Baqarah · 2 : 238"
            )
        ),

        // 12. Name III - What Ibrahim Paid (Ibrahim 14:37, excerpt)
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("What Ibrahim Paid"),
            surah = 14,
            ayah = 37,
            arabic = "رَبَّنَا إِنِّي أَسْكَنتُ مِن ذُرِّيَّتِي بِوَادٍ غَيْرِ ذِي زَرْعٍ عِندَ بَيْتِكَ الْمُحَرَّمِ رَبَّنَا لِيُقِيمُوا الصَّلَاةَ",
            translation = LocalizedText("Our Lord, I have settled some of my descendants in a valley without cultivation, by Your sacred House - our Lord, that they may establish the prayer."),
            reference = "Ibrahim · 14 : 37",
            reflection = LocalizedText("A wife and an infant, left in a dead valley - and the reason he gives God is the prayer. Makkah, the House, the direction you face five times a day: a city exists because one man thought the prayer worth that much.")
        ),

        // 13. Name III - The Last Sentence (Imam al-Sadiq's deathbed)
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Last Sentence"),
            source = LocalizedText("Imam Ja'far al-Sadiq, in his final moments · al-Amali of al-Saduq"),
            body = LocalizedText("In his final moments, Imam Ja'far al-Sadiq, the sixth Imam, opened his eyes and said: gather to me every relative of mine. When they had assembled, he looked at them and said: “Our intercession will not reach one who takes the prayer lightly.”"),
            reflection = LocalizedText("A man spends his last sentence on the heaviest thing he knows. Intercession - the Imams' pleading before God for their own - was the inheritance of the family gathered in that room; and even for them, he tied it to one condition: the prayer, held at its full weight.")
        ),

        // 14. Name III - The Prayer Under Arrows (Zuhr of Ashura)
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("The Prayer Under Arrows"),
            source = LocalizedText("Imam al-Husayn to Abu Thumama, noon of Ashura · Tarikh al-Tabari · al-Luhuf of Ibn Tawus"),
            arabic = "ذَكَرْتَ الصَّلَاةَ، جَعَلَكَ اللَّهُ مِنَ الْمُصَلِّينَ الذَّاكِرِينَ",
            translation = LocalizedText("“You remembered the prayer - may God place you among the praying, the remembering.”"),
            body = LocalizedText("Noon on Ashura - the tenth of Muharram, on the plain of Karbala. Most of Imam Husayn's men already lie dead when Abu Thumama, one of his last companions, notices the sun at its height: I would love to meet my Lord having prayed this one last prayer. They ask for the fighting to pause while they pray; it does not pause. So the prayer is prayed under the arrows. Sa'id ibn Abdullah stands in front of the Imam, taking the arrows with his own body, and falls at last with thirteen arrows in him: O God - convey my greeting to Your Prophet, and tell him what I met of the pain of these wounds."),
            reflection = LocalizedText("God's own law would have excused a delay - a battlefield is reason enough. But the third name is the offering, and they offered it on time, at the price of a man. On that plain, nobody thought the prayer was a ritual. It was the thing being defended.")
        ),

        // 15. The Last Rung (interactive sujud close)
        DeepDiveSection.Sujud(
            tag = LocalizedText("The Last Rung"),
            prompt = LocalizedText("Go down - and draw near."),
            subline = LocalizedText("Press and hold - and let the stillness stand in for the sajdah, the prostration."),
            arabic = "وَاسْجُدْ وَاقْتَرِبْ",
            translation = LocalizedText("Prostrate - and draw near."),
            reference = "al-Alaq · 96 : 19",
            note = LocalizedText("The nearest point is yours five times a day. What they guarded under arrows asks of you only a floor, a forehead, and the willingness to arrive."),
            nextLabel = LocalizedText("And one gift")
        ),

        // 16. The Close - the Tasbih of Fatima
        DeepDiveSection.Dua(
            tag = LocalizedText("The Gift After Every Prayer"),
            intro = LocalizedText("After the summit - one gift to carry home. When the hand-mill had blistered the hands of Fatima, the Prophet's ﷺ daughter, her husband Imam Ali sent her to ask her father for a servant. Instead of a servant, her father came to them himself: shall I not teach you both something better?"),
            arabic = "اللَّهُ أَكْبَرُ، وَالْحَمْدُ لِلَّهِ، وَسُبْحَانَ اللَّهِ",
            translation = LocalizedText("“God is greater - thirty-four times. All praise belongs to God - thirty-three. Glory be to God - thirty-three.”"),
            source = LocalizedText("The Prophet's ﷺ gift to Fatima al-Zahra · Man la yahduruh al-Faqih"),
            note = LocalizedText("Say it after every prayer, before you rise from your place. Imam al-Sadiq called it dearer than a thousand rak'ahs - a thousand cycles of prayer - each day, and said that whoever keeps it is forgiven. One hundred small words, the whole ascent walked again. Begin tonight."),
            close = LocalizedText("The prayer is yours to keep.")
        ),
    )
)
