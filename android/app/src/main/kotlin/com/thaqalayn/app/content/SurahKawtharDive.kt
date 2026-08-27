package com.thaqalayn.app.content

// Fixed content for the "Inside the Surah - al-Kawthar" experience. Verbatim port of
// iOS Content/SurahKawtharDive.swift; rendered by DeepDiveScreen.

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.LocalizedText

val surahKawtharDive: DeepDive = DeepDive(
    id = "surah-kawthar",
    titleEn = "al-Kawthar",
    titleAr = "الْكَوْثَر",
    subtitle = LocalizedText("The Abundance - the shortest surah, sent to answer an insult"),
    estMinutes = 11,
    acts = listOf(
        ActInfo(1, "الْعَطَاء", "al-Ata", LocalizedText("The Gift")),
        ActInfo(2, "النَّحْر", "al-Nahr", LocalizedText("The Offering")),
        ActInfo(3, "الْأَبْتَر", "al-Abtar", LocalizedText("The Verdict"))
    ),
    sections = listOf(
        DeepDiveSection.Open(
            kicker = LocalizedText("INSIDE THE SURAH"),
            titleAr = "الْكَوْثَر",
            titleEn = "al-Kawthar",
            subtitle = LocalizedText("The Abundance"),
            line = LocalizedText("Three verses. Ten words. The shortest surah in the whole Qur'an - and it came down to answer one insult, spoken in Makkah by a chief of Quraysh on a day he thought he had won. This is what God sent down when they mocked His beloved.")
        ),
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you begin"),
            promise = LocalizedText("Makkah had a word for a man with no surviving sons: abtar - cut off, a stump, a line that ends. It was the cruelest word a father could be handed, and it was said of the Prophet ﷺ. What came down in reply is the shortest surah in the Book: three verses that give a gift, ask one thing in return, and settle the matter forever."),
            leaveWith = LocalizedText("You will leave knowing what al-Kawthar is, and what God set on the other side of that one small word - and why the smallest surah in the Qur'an has outlived every man who ever laughed at it.")
        ),

        // MOVEMENT I - The Gift (verse 1)
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("Every gift in the Qur'an arrives inside a story. This one arrives inside a wound. Before you can see what God gave His Prophet ﷺ here, you have to stand where he stood in Makkah in the hard years, and hear what the chiefs of Quraysh were saying about him when he was not there."),
            bridge = null
        ),
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The Taunt"),
            source = LocalizedText("Majma al-Bayan · al-Mizan · the occasion of revelation"),
            body = LocalizedText("The Prophet ﷺ had buried his infant son Qasim, and then Abdullah - and in a city that counted a man's worth in sons, the chiefs of Quraysh saw their opening. Al-As ibn Wa'il, one of them, met the Prophet ﷺ as he came out of the sanctuary, and stood talking with him a while. When the elders inside asked him who he had been speaking to, he answered: “That abtar.” Cut off. A man with no sons to carry his name and, they told themselves, no future at all. “When he dies,” they assured one another, “his name dies with him, and we are finally rid of this whole affair.”"),
            reflection = LocalizedText("See what they reached for: the one grief a father cannot argue with - the graves of his own children - and they made it their case against him. Heaven's reply is the surah you are standing inside now. And notice that it does not begin by defending him. It begins with a gift.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Already Given"),
            surah = 108, ayah = 1,
            arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ",
            translation = LocalizedText("Truly, We have given you al-Kawthar."),
            reference = "al-Kawthar · 108 : 1",
            reflection = LocalizedText("Against their one small word, abtar, God sets another: al-Kawthar - from kathra, abundance, in the form Arabic reserves for the utmost degree of a thing. Not just a lot. Not just more. Abundance itself. Now listen to how it is given. Inna, the opening word, is Arabic's seal of certainty: this is beyond doubt. And a'taynaka - We have given you - is past tense. Al-Mizan stops at this verb: it is the language of ownership, a gift not promised but already handed over, already his. Quraysh pointed at the graves of his sons and called his future dead. God answers with a gift death cannot touch. So what is the gift? The surah does not say. It leaves the word open - and what it holds is larger than any single answer.")
        ),
        DeepDiveSection.Narration(
            act = 1,
            tag = LocalizedText("The River"),
            source = LocalizedText("The Prophet Muhammad ﷺ · Nur al-Thaqalayn"),
            body = LocalizedText("When this surah came down, the Prophet ﷺ told his companions of a river his Lord had given him in Paradise: its water whiter than milk, sweeter than honey, softer than butter. On the Day of Judgment his people will come to him at its basin - al-hawd, the Fountain. And in the narration preserved in Nur al-Thaqalayn, the Prophet ﷺ turned to Ali (a) and said: “This river is for me, and for you, and for those who love you after me.” The Shia books give the office a name: on that Day, Ali (a) is saqi al-Kawthar, the cupbearer of the Fountain, giving its water to everyone who held to the household."),
            reflection = LocalizedText("So al-Kawthar is a river. But notice what kind of answer that is. They said: when he dies, he ends. The reply came from the other direction entirely: when he dies, he will be standing at the source of the sweetest water in existence, watching the faces of millions come toward him. Their taunt reached as far as the edge of his death. The gift begins on the other side of it.")
        ),

        // MOVEMENT II - The Offering (verse 2)
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("A river waiting at the end of time, and an abundance already given - that is what He set against one word of contempt."),
            line = LocalizedText("Now the surah turns to the Prophet ﷺ himself, and to you standing behind him. When you are given what no thanks could ever equal, what is left to do? The middle verse of the shortest surah answers in two commands - and the second one turns on a word English has no room for."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("Pray, and Offer"),
            surah = 108, ayah = 2,
            arabic = "فَصَلِّ لِرَبِّكَ وَٱنْحَرْ",
            translation = LocalizedText("So pray to your Lord, and offer sacrifice."),
            reference = "al-Kawthar · 108 : 2",
            reflection = LocalizedText("The verse begins with fa - so, therefore: because you were given, now turn to the Giver. Then li-rabbika, to your Lord and no one else - in a city that slaughtered for its idols, prayer and offering both go back to the One who gave. The last word, nahr, is the strange one. It is not sacrifice in general; it names the place at the base of the throat where an offering is made. And Tabrisi records the reading passed down from the Prophet's ﷺ own household: the verse points to a gesture inside the prayer itself. Imam al-Sadiq (a) taught: raise your hands to the level of the throat at the takbir, the opening Allahu akbar, palms open toward the qibla. It is the stance of one who has come to give, and holds nothing back. The gift was everything. What is asked in return is all of you.")
        ),
        DeepDiveSection.Narration(
            act = 2,
            tag = LocalizedText("Prayer and Offering"),
            source = LocalizedText("Karbala · the day of Ashura"),
            body = LocalizedText("If you want to see this verse lived out to its last letter, look at the Prophet's ﷺ own household. At noon on the day of Ashura, with the arrows already falling, Imam Husayn (a) - the son of Fatima (a) - stopped the battle to pray. He stood the midday prayer on the sand while a handful of companions shielded him with their own bodies. And by the end of that day he had given everything a human being can give: his companions, his sons, the infant in his arms, and at last himself, offered entire on the plain of Karbala. Fa-salli li-rabbika wa-nhar: pray to your Lord, and offer. That day the verse was not recited. It was lived."),
            reflection = LocalizedText("The mockers of Makkah thought sacrifice was erasure - that a man who loses his sons is finished. Karbala turned the word inside out: giving everything was not the end of the Prophet's ﷺ house. What it was instead, the surah's last verse now says.")
        ),

        // MOVEMENT III - The Verdict (verse 3)
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("The gift was given, and the household answered it with everything they had."),
            line = LocalizedText("One verse remains - the verse the whole surah has been walking toward. God has comforted His beloved ﷺ and taught him how to give thanks. Now the surah speaks, at last, to the man outside the sanctuary, and to everyone in any century who has ever spoken like him. Four words, and there is nothing left to answer."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("The Verdict"),
            surah = 108, ayah = 3,
            arabic = "إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ",
            translation = LocalizedText("Truly, the one who hates you - he is the one cut off."),
            reference = "al-Kawthar · 108 : 3",
            reflection = LocalizedText("The insult goes back to its owner, and the door shuts behind it. Inna again: this is certain. Then huwa, he - that one and no one else - is al-abtar, the cut-off. Al-Mizan reads the sentence as a reversal: the verse does not merely deny the taunt, it lifts it off the Prophet ﷺ and sets it down on the man who said it. And history obeyed. Al-As ibn Wa'il was a chief of Quraysh, rich in sons and standing. Fourteen centuries later, he is remembered above all for one sentence - the day he called the Prophet ﷺ cut off - while the man he mocked is blessed by name, every day, in millions of prayers. The surah did not simply answer him. It made him the answer.")
        ),
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("Who Is al-Kawthar"),
            source = LocalizedText("al-Mizan · on Surah al-Kawthar"),
            arabic = "إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ",
            translation = LocalizedText("Truly, We have given you al-Kawthar."),
            body = LocalizedText("Now stand at the end of the surah and hear its first verse again - you will hear it differently now. The taunt was about children: no sons, no line, no future. Al-Mizan walks the answer out plainly. The last verse hands the insult back: your hater is the one cut off. For that reply to hold, the gift of the first verse must contain the very thing the insult denied - descendants, in abundance. And the descendant God gave was not a son. At the center of the Prophet's ﷺ abundance stood a daughter: Fatima (a), remembered in the tradition as Umm Abiha, the mother of her own father, so tender was her care for him. Through her and her sons Hasan (a) and Husayn (a) came the Imams, and a lineage that no sword, no poison, and no massacre has ever ended. Even Fakhr al-Razi, the great Sunni commentator, paused at this surah to marvel: see how many of them have been killed, he wrote, and the world is still full of them. They mocked him for the sons he had buried. God answered with a daughter, and through her filled the earth."),
            reflection = LocalizedText("This is the surah's deepest mercy, and it is not only his. The world still measures in numbers, names, and lines that last; God still gives in forms the world counts as loss. Whatever they say you lack, whatever grave you have stood beside - the abundance may already be in your house, in a form the world has not learned to count.")
        ),
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("Return"),
            prompt = LocalizedText("What has loss convinced you that you lack?"),
            placeholder = LocalizedText("a person, a closed door, a name, a child, time…"),
            subline = LocalizedText("The whole surah is God refusing to let a grief define His beloved. The mockers pointed at what was missing; He pointed at what was given. Before you go, name the one loss the world keeps pointing to in your life - and ask, honestly, what He may have already set in its place."),
            nextLabel = LocalizedText("One last thing")
        ),
        DeepDiveSection.Closing(
            tag = LocalizedText("The Close"),
            titleAr = "الْكَوْثَر",
            essence = LocalizedText("Ten words against one insult: abundance already given, a thanks worth everything, and a verdict that made the mocker a footnote and the mocked a fountain."),
            line = LocalizedText("One more thing. The Prophet ﷺ said he leaves behind two weighty things - al-thaqalayn, the Book of God and his Ahl al-Bayt - and that the two will never part until they return to him at the Fountain. The Fountain is al-Kawthar. The Book and the family, the two weighty things this app is named for, are both flowing toward this surah's river. Read its three verses again now, slowly - and this time, hear them answered.")
        )
    )
)
