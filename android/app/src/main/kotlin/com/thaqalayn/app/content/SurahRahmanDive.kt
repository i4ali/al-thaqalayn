package com.thaqalayn.app.content

// Fixed content for the "Inside the Surah - al-Rahman" experience. Verbatim port of
// iOS Content/SurahRahmanDive.swift; rendered by DeepDiveScreen.

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.LocalizedText

val surahRahmanDive: DeepDive = DeepDive(
    id = "surah-rahman",
    titleEn = "al-Rahman",
    titleAr = "الرَّحْمَٰن",
    subtitle = LocalizedText("The Bride of the Qur'an - one question, asked thirty-one times"),
    estMinutes = 13,
    stageNoun = "Wave",
    acts = listOf(
        ActInfo(1, "التَّعْلِيم", "al-Ta'lim", LocalizedText("The Teaching")),
        ActInfo(2, "الْبَحْرَانِ", "al-Bahran", LocalizedText("The Two Seas")),
        ActInfo(3, "الْوَجْه", "al-Wajh", LocalizedText("The Face")),
        ActInfo(4, "الْجَنَّتَانِ", "al-Jannatan", LocalizedText("The Two Gardens")),
    ),
    sections = listOf(
        DeepDiveSection.Open(
            kicker = LocalizedText("INSIDE THE SURAH"),
            titleAr = "الرَّحْمَٰن",
            titleEn = "al-Rahman",
            subtitle = LocalizedText("The Bride of the Qur'an"),
            line = LocalizedText("They called it the bride of the Qur'an - the surah where God recites His own gifts, one after another, like a litany, a long song of praise sung over the worlds. But it is not a list to admire. Thirty-one times it stops, turns to face you, and asks the same question. This time, you will answer it.")
        ),
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you begin"),
            promise = LocalizedText("This surah opens with a name of pure mercy - al-Rahman - and then the favors pour out in four waves: the teaching, the pairs of creation, what remains when everything passes, and the gardens. After each wave comes one returning question: then which of the favors of your Lord do you both deny? It is not filler between verses. It is addressed to you, and it expects something back."),
            leaveWith = LocalizedText("You will leave having answered the question yourself - and you will never again hear the refrain as repetition.")
        ),
        DeepDiveSection.Act(
            act = 1, connector = null,
            line = LocalizedText("The surah opens with a single word, a name: al-Rahman. And the first gift it counts is not the sun, not the sky, not even your life. It is the Qur'an - named first, before the creation of man is even mentioned. Mercy began speaking before there was anyone to hear."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 1, tag = LocalizedText("The First Gift"), surah = 55, ayah = 1,
            arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ٱلرَّحْمَٰنُ عَلَّمَ ٱلْقُرْءَانَ خَلَقَ ٱلْإِنسَٰنَ عَلَّمَهُ ٱلْبَيَانَ",
            translation = LocalizedText("In the name of Allah, the All-Merciful, the Ever-Merciful. The All-Merciful - He taught the Qur'an. He created man. He taught him speech."),
            reference = "al-Rahman · 55 : 1-4",
            reflection = LocalizedText("Listen to the order. Taught the Qur'an, then created man. Al-Mizan, Tabatabai's great commentary, calls the sequence deliberate: guidance was prepared before the one who would need it, the way a cradle is made ready before a birth. You were not created and then handed a Book as an afterthought. The Book was waiting for you. And then a second teaching: bayan, speech - the power to mean something and to say it. The first thing the Merciful ever did for you was teach.")
        ),
        DeepDiveSection.Verse(
            act = 1, tag = LocalizedText("The Balance"), surah = 55, ayah = 7,
            arabic = "وَٱلسَّمَآءَ رَفَعَهَا وَوَضَعَ ٱلْمِيزَانَ",
            translation = LocalizedText("And the sky - He raised it, and He set down the balance."),
            reference = "al-Rahman · 55 : 7",
            reflection = LocalizedText("The sun and the moon run on exact reckoning; the star and the tree bow down; the sky is raised - and in the same breath, a balance is set. Then the surah turns the cosmos into a command: do not transgress in the balance, weigh with justice, do not cheat the scale. Al-Mizan reads it plainly: the fairness you owe in your smallest dealing hangs from the same beam that holds the heavens. To cheat a scale is not to break a rule. It is to step outside an order that everything else in creation obeys.")
        ),
        DeepDiveSection.Refrain(
            act = 1, tag = LocalizedText("The Question"), surah = 55, ayah = 13,
            arabic = "فَبِأَىِّ ءَالَآءِ رَبِّكُمَا تُكَذِّبَانِ",
            translation = LocalizedText("Then which of the favors of your Lord do you both deny?"),
            reference = "al-Rahman · 55 : 13",
            intro = LocalizedText("Here the litany stops for the first time and looks up from the gifts, straight at its listeners. The verse speaks to two at once - your Lord, you both - because it addresses mankind and the jinn together: the two creations who can hear a question and owe an answer. It will be asked thirty-one times. And the Ahl al-Bayt did not leave you to sit through it in silence. Imam Ja'far al-Sadiq (alayhi al-salam) taught exactly what to say back."),
            teachSource = LocalizedText("Imam Ja'far al-Sadiq · Thawab al-A'mal"),
            replyArabic = "لَا بِشَيْءٍ مِنْ آلَائِكَ رَبِّ أُكَذِّبُ",
            replyTransliteration = "La bi shay'in min ala'ika Rabbi ukadhdhib",
            replyTranslation = LocalizedText("None of Your favors, my Lord, do I deny."),
            reflection = LocalizedText("Say it once and the whole surah changes shape. It stops being a recitation you listen to and becomes a conversation you are standing inside: He counts a favor, you answer. He counts another, you answer again. Thirty-one times, the door opens from His side. From here on, every asking in this descent is yours to answer.")
        ),
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have answered Him once."),
            line = LocalizedText("Now the litany turns from the sky to the things He made - and everything begins arriving in twos. Man from dry clay, like pottery; the jinn from smokeless fire: the two listeners of the surah, named side by side. Two easts and two wests. And then two seas, sent flowing toward each other."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 2, tag = LocalizedText("The Two Seas"), surah = 55, ayah = 19,
            arabic = "مَرَجَ ٱلْبَحْرَيْنِ يَلْتَقِيَانِ بَيْنَهُمَا بَرْزَخٌۭ لَّا يَبْغِيَانِ",
            translation = LocalizedText("He released the two seas, meeting - and between them a barrier neither of them crosses."),
            reference = "al-Rahman · 55 : 19-20",
            reflection = LocalizedText("Two seas sent flowing into each other, sweet and salt, meeting without merging - held apart by a barzakh, a barrier, that neither may cross. Al-Mizan pauses on one thing: how that barrier is held. It is not a wall built once and left; it is a boundary kept in place moment by moment, the way all order in creation is kept. Joined, and still themselves. Meeting, without one dissolving the other. Keep that picture in your hands. The Ahl al-Bayt saw something in it that this surah will not let you forget.")
        ),
        DeepDiveSection.Narration(
            act = 2, tag = LocalizedText("The Household Reading"),
            source = LocalizedText("Tafsir al-Qummi; Majma al-Bayan; Nur al-Thaqalayn"),
            body = LocalizedText("Asked about these verses, Imam Ja'far al-Sadiq (alayhi al-salam) gave a reading passed down through many chains of narrators: the two seas are Ali and Fatima (alayhima al-salam) - two oceans of one light, joined in marriage, neither one diminishing the other. And other chains - Salman al-Farsi and Ibn Abbas among their narrators - name the barzakh that stands between the two seas: the Prophet himself ﷺ."),
            reflection = LocalizedText("The literal seas stay true; this is a second depth beneath them, the Qur'an's way of carrying more than one favor in a single image. Read this way, the surah sets the household of the Prophet in the middle of its litany of gifts - counted out among the sun, the sky, and the seas. A marriage listed alongside the heavens, as if to say: this, too, He gave you.")
        ),
        DeepDiveSection.Verse(
            act = 2, tag = LocalizedText("Pearl and Coral"), surah = 55, ayah = 22,
            arabic = "يَخْرُجُ مِنْهُمَا ٱللُّؤْلُؤُ وَٱلْمَرْجَانُ",
            translation = LocalizedText("From the two of them emerge the pearl and the coral."),
            reference = "al-Rahman · 55 : 22",
            reflection = LocalizedText("Now the verse speaks twice at once. From two seas: treasures born exactly where different waters meet - the way a pearl begins as a grain of pain inside the shell, and the sea slowly turns it into light, under pressure, in the dark. And the reading of Imam al-Sadiq (alayhi al-salam) completes here: the pearl and the coral are al-Hasan and al-Husayn (alayhima al-salam), the two jewels of the house of the Prophet ﷺ. Either way the pattern holds. What God joins, He joins fruitfully. The meeting places of His creation are where the treasures come from.")
        ),
        DeepDiveSection.Refrain(
            act = 2, tag = LocalizedText("The Question Returns"), surah = 55, ayah = 23,
            arabic = "فَبِأَىِّ ءَالَآءِ رَبِّكُمَا تُكَذِّبَانِ",
            translation = LocalizedText("Then which of the favors of your Lord do you both deny?"),
            reference = "al-Rahman · 55 : 23",
            intro = LocalizedText("The seas, the barrier they honor, the pearl and the coral - and the household carried inside the image. He asks again."),
            teachSource = null,
            replyArabic = "لَا بِشَيْءٍ مِنْ آلَائِكَ رَبِّ أُكَذِّبُ",
            replyTransliteration = "La bi shay'in min ala'ika Rabbi ukadhdhib",
            replyTranslation = LocalizedText("None of Your favors, my Lord, do I deny."),
            reflection = LocalizedText("The same words as before - but never the same question. Al-Mizan says the refrain is not repetition: each return gathers up the favors just counted and lays them before you, fresh. Last time you answered for the sky and the balance. This time you answer for the seas, the pearl, the coral - and for a family given to the worlds as a mercy. The question grows heavier each time. So does the answer.")
        ),
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("Twice now, you have answered."),
            line = LocalizedText("And here the hymn of gifts does what no one expects: it counts an ending among the favors. The sky, the seas, the faces you love - everything this litany has praised - the surah now says plainly: all of it will pass. What kind of gift is that? Hold the question. The surah is about to answer it."),
            bridge = BridgeVerse(
                surah = 55, ayah = 26,
                arabic = "كُلُّ مَنْ عَلَيْهَا فَانٍۢ",
                translation = LocalizedText("All who are upon it will pass away."),
                reference = "al-Rahman · 55 : 26"
            )
        ),
        DeepDiveSection.Verse(
            act = 3, tag = LocalizedText("What Remains"), surah = 55, ayah = 27,
            arabic = "وَيَبْقَىٰ وَجْهُ رَبِّكَ ذُو ٱلْجَلَٰلِ وَٱلْإِكْرَامِ",
            translation = LocalizedText("And the Face of your Lord remains - Owner of Majesty and Honor."),
            reference = "al-Rahman · 55 : 27",
            reflection = LocalizedText("Everything passes; the Face remains. Al-Mizan says the Face is not a feature, the way a human face is. It is the side of God that is turned toward you - His attention, facing His creation, never looking away. And hear the two names it carries. Jalal, the majesty that needs nothing. Ikram, the honor that keeps giving anyway. They are held together on purpose, because what outlasts every gift is not a cold survivor - it is generosity that stays. That is how an ending can sit inside a litany of favors: nothing you lose was ever what held you. He was.")
        ),
        DeepDiveSection.Verse(
            act = 3, tag = LocalizedText("Never Finished Giving"), surah = 55, ayah = 29,
            arabic = "يَسْـَٔلُهُۥ مَن فِى ٱلسَّمَٰوَٰتِ وَٱلْأَرْضِ ۚ كُلَّ يَوْمٍ هُوَ فِى شَأْنٍۢ",
            translation = LocalizedText("All who are in the heavens and the earth ask of Him. Every day He is upon a matter."),
            reference = "al-Rahman · 55 : 29",
            reflection = LocalizedText("Whoever is in the heavens and the earth asks of Him - asks by praying, and asks just by existing, since every heartbeat is a request for the next one. And every day He is upon a matter: forgiving someone, feeding someone, mending something, answering someone. Imam Ali (alayhi al-salam) taught that if His attention left creation for a single instant, it would cease to be. The Face that remains is not a monument to outlasting. It is the busiest mercy in existence - and one of its matters, today, is you.")
        ),
        DeepDiveSection.Verse(
            act = 3, tag = LocalizedText("The Other Answer"), surah = 55, ayah = 43,
            arabic = "هَٰذِهِۦ جَهَنَّمُ ٱلَّتِى يُكَذِّبُ بِهَا ٱلْمُجْرِمُونَ",
            translation = LocalizedText("This is Jahannam, which the guilty deny."),
            reference = "al-Rahman · 55 : 43",
            reflection = LocalizedText("In the verses just before this one, the turn is announced: We shall attend to you, O two weighty ones - mankind and jinn, summoned to account. Then the litany darkens: the flame, and then this - Jahannam, which the guilty deny. Listen to the word the verse chooses: yukadhdhibu. Deny. The refrain's own verb, the one you have been answering all this time. There were always two replies to this surah's question. One says: none of Your favors do I deny. The other never says anything - it just lives as if the question was never asked. And that, says the verse, is a denial too.")
        ),
        DeepDiveSection.Refrain(
            act = 3, tag = LocalizedText("The Hardest Asking"), surah = 55, ayah = 45,
            arabic = "فَبِأَىِّ ءَالَآءِ رَبِّكُمَا تُكَذِّبَانِ",
            translation = LocalizedText("Then which of the favors of your Lord do you both deny?"),
            reference = "al-Rahman · 55 : 45",
            intro = LocalizedText("Straight after the Fire - not after a garden, not after a pearl - the question comes again, unchanged. Can it still be answered here?"),
            teachSource = null,
            replyArabic = "لَا بِشَيْءٍ مِنْ آلَائِكَ رَبِّ أُكَذِّبُ",
            replyTransliteration = "La bi shay'in min ala'ika Rabbi ukadhdhib",
            replyTranslation = LocalizedText("None of Your favors, my Lord, do I deny."),
            reflection = LocalizedText("Al-Mizan resolves the shock: even the warning is a favor. A fence at the cliff's edge is not a threat - it is mercy in its sternest clothing, from the One who taught you before He created you and has no wish to lose you now. A god who warned no one would be a god who did not care where you ended up. So the answer does not change at the edge of the Fire. It deepens. None of Your favors, my Lord - not even this one - do I deny.")
        ),
        DeepDiveSection.Act(
            act = 4,
            connector = LocalizedText("You did not stop answering, even at the Fire."),
            line = LocalizedText("And for the one who carried the question honestly - who feared the day of standing before his Lord, and let that fear steer him - the litany opens its final gift: gardens. And true to this surah of pairs, not just one."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 4, tag = LocalizedText("For the One Who Feared"), surah = 55, ayah = 46,
            arabic = "وَلِمَنْ خَافَ مَقَامَ رَبِّهِۦ جَنَّتَانِ",
            translation = LocalizedText("And for whoever feared the standing before his Lord - two gardens."),
            reference = "al-Rahman · 55 : 46",
            reflection = LocalizedText("Fear of the maqam - the standing before your Lord - is not terror of a tyrant. It is the awake awe of someone who never quite forgot that one day they will stand before Him. And for that fear, two gardens. Imam Ja'far al-Sadiq (alayhi al-salam) warned: do not shrink this promise. Do not say Paradise is one garden, or one level - beyond these two come two more, and some stations rise above others. The surah of pairs keeps its signature to the very end. Even its rewards refuse to arrive alone.")
        ),
        DeepDiveSection.Climax(
            act = 4, tag = LocalizedText("The Whole Surah in One Line"), source = LocalizedText("al-Rahman · 55 : 60"),
            arabic = "هَلْ جَزَآءُ ٱلْإِحْسَٰنِ إِلَّا ٱلْإِحْسَٰنُ",
            translation = LocalizedText("Is the reward of beautiful doing anything but beautiful giving?"),
            body = LocalizedText("Deep inside the description of the gardens, the surah suddenly compresses itself into a single breath: is the reward of ihsan anything but ihsan? Beauty answered with beauty. The Prophet ﷺ unfolded its depth by relating his Lord's own words: is there any reward for the one I favored with tawhid - with knowing Me as One - except Paradise? And Imam Ali (alayhi al-salam) opened ihsan to the whole of a life: worship Him as if you see Him - for even if you do not see Him, He sees you."),
            reflection = LocalizedText("Now look back down the whole descent. He taught before He created. He hung the sky on a balance. He joined the seas and gave the worlds a household. He fenced the cliff. He doubled the gardens. Every wave of the litany was ihsan - beauty arriving before you ever asked for it. The question was never whether He gives. It was whether you would see it - and say so.")
        ),
        DeepDiveSection.Refrain(
            act = 4, tag = LocalizedText("The Last Asking"), surah = 55, ayah = 77,
            arabic = "فَبِأَىِّ ءَالَآءِ رَبِّكُمَا تُكَذِّبَانِ",
            translation = LocalizedText("Then which of the favors of your Lord do you both deny?"),
            reference = "al-Rahman · 55 : 77",
            intro = LocalizedText("The thirty-first asking - the last one the surah will ever ask you. Answer it the way the Imam taught. And mean it."),
            teachSource = null,
            replyArabic = "لَا بِشَيْءٍ مِنْ آلَائِكَ رَبِّ أُكَذِّبُ",
            replyTransliteration = "La bi shay'in min ala'ika Rabbi ukadhdhib",
            replyTranslation = LocalizedText("None of Your favors, my Lord, do I deny."),
            reflection = LocalizedText("Imam al-Sadiq (alayhi al-salam) promised: whoever recites this surah, gives this answer at every asking, and then dies - dies a martyr. Not because the words are a charm. It is what the words make of the one who means them: a soul that saw its Lord's generosity everywhere, and said so, out loud, to His face. The Arabic word for martyr, shahid, means exactly that - a witness. The question will find you again, in the surah and in the world. You know the answer now.")
        ),
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("The Return"),
            prompt = LocalizedText("Name the favor you had stopped seeing."),
            placeholder = LocalizedText("This breath, a person, a rescue you called ordinary, the Book itself…"),
            subline = LocalizedText("The surah asks, again and again, because we go blind to gifts by owning them. You have answered Him four times today. Before you go, take one favor out of the dark - the one you had stopped counting - and look at it until it looks like what it is."),
            nextLabel = LocalizedText("One last thing")
        ),
        DeepDiveSection.Closing(
            tag = LocalizedText("The Close"),
            titleAr = "الرَّحْمَٰن",
            essence = LocalizedText("The surah seals itself with the same two names that survived the passing of everything: Blessed is the name of your Lord, Owner of Majesty and Honor - majesty that needs nothing, generosity that stays."),
            line = LocalizedText("Read al-Rahman now in its own words, all seventy-eight verses, with the answer ready on your tongue. Imam al-Sadiq (alayhi al-salam) said: do not abandon it, for it comes on the Day of Rising in the most beautiful of forms, to name before its Lord the one who kept it close. Let it recognize you.")
        ),
    )
)
