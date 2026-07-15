package com.thaqalayn.app.content

// Fixed content for the "Inside the Surah - Yusuf" experience. Verbatim port of
// iOS Content/SurahYusufDive.swift; rendered by DeepDiveScreen. Trilingual
// (EN/UR/AR); Qur'an Arabic is verbatim from quran_data.json. No dua beat.
// NOTE: UR/AR are English placeholders for now (translations deferred).

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.LocalizedText

val surahYusufDive: DeepDive = DeepDive(
    id = "surah-yusuf",
    titleEn = "Yusuf",
    titleAr = "يُوسُف",
    subtitle = LocalizedText("The best of stories - a dream, a pit, and the long road home", "The best of stories - a dream, a pit, and the long road home", "The best of stories - a dream, a pit, and the long road home"),
    estMinutes = 12,
    acts = listOf(
        ActInfo(1, "الرُّؤْيَا", "al-Ru'ya", LocalizedText("The Dream", "The Dream", "The Dream")),
        ActInfo(2, "الْبَلَاء", "al-Bala", LocalizedText("The Test", "The Test", "The Test")),
        ActInfo(3, "اللِّقَاء", "al-Liqa", LocalizedText("The Reunion", "The Reunion", "The Reunion"))
    ),
    sections = listOf(
        DeepDiveSection.Open(
            kicker = LocalizedText("INSIDE THE SURAH", "INSIDE THE SURAH", "INSIDE THE SURAH"),
            titleAr = "يُوسُف",
            titleEn = "Yusuf",
            subtitle = LocalizedText("The Best of Stories", "The Best of Stories", "The Best of Stories"),
            line = LocalizedText("A boy tells his father a dream, and his father tells him to keep it secret. So begins the best of stories - the long descent from a well in the dark to a throne in Egypt, and the God who was there the whole way down.", "A boy tells his father a dream, and his father tells him to keep it secret. So begins the best of stories - the long descent from a well in the dark to a throne in Egypt, and the God who was there the whole way down.", "A boy tells his father a dream, and his father tells him to keep it secret. So begins the best of stories - the long descent from a well in the dark to a throne in Egypt, and the God who was there the whole way down.")
        ),
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you descend", "Before you descend", "Before you descend"),
            promise = LocalizedText("This is the surah God sent down in the Year of Sorrow, to console His Prophet after he lost Khadija and Abu Talib - and He called it the best of stories. Below lies its whole arc, where every fall becomes a rise.", "This is the surah God sent down in the Year of Sorrow, to console His Prophet after he lost Khadija and Abu Talib - and He called it the best of stories. Below lies its whole arc, where every fall becomes a rise.", "This is the surah God sent down in the Year of Sorrow, to console His Prophet after he lost Khadija and Abu Talib - and He called it the best of stories. Below lies its whole arc, where every fall becomes a rise."),
            leaveWith = LocalizedText("You will leave understanding the soul of the surah - that God-consciousness and beautiful patience carry a person from the bottom of a well to the throne - and why the Ahl al-Bayt taught that whoever keeps to its recitation is raised in the light of Yusuf.", "You will leave understanding the soul of the surah - that God-consciousness and beautiful patience carry a person from the bottom of a well to the throne - and why the Ahl al-Bayt taught that whoever keeps to its recitation is raised in the light of Yusuf.", "You will leave understanding the soul of the surah - that God-consciousness and beautiful patience carry a person from the bottom of a well to the throne - and why the Ahl al-Bayt taught that whoever keeps to its recitation is raised in the light of Yusuf.")
        ),
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Map of the Surah", "The Map of the Surah", "The Map of the Surah"),
            reference = "Yusuf · 12",
            items = listOf(
                Depth("الْجُبّ", "al-Jubb", LocalizedText("The Well", "The Well", "The Well"), LocalizedText("Thrown into the dark by his own brothers, then raised out of it into the house of Egypt's ruler.", "Thrown into the dark by his own brothers, then raised out of it into the house of Egypt's ruler.", "Thrown into the dark by his own brothers, then raised out of it into the house of Egypt's ruler."), "12:15", LocalizedText("loss that becomes the road", "loss that becomes the road", "loss that becomes the road")),
                Depth("السِّجْن", "al-Sijn", LocalizedText("The Prison", "The Prison", "The Prison"), LocalizedText("Cast down again by a lie he would not answer with sin, then raised from the cell to the throne.", "Cast down again by a lie he would not answer with sin, then raised from the cell to the throne.", "Cast down again by a lie he would not answer with sin, then raised from the cell to the throne."), "12:33", LocalizedText("the pit that becomes power", "the pit that becomes power", "the pit that becomes power")),
                Depth("اللِّقَاء", "al-Liqa", LocalizedText("The Reunion", "The Reunion", "The Reunion"), LocalizedText("Long years of famine and grief, and then the family gathered and the childhood dream made real.", "Long years of famine and grief, and then the family gathered and the childhood dream made real.", "Long years of famine and grief, and then the family gathered and the childhood dream made real."), "12:100", LocalizedText("separation that becomes home", "separation that becomes home", "separation that becomes home"))
            )
        ),
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins in a father's love and a night vision. A boy is favored, a dream is given, and a jealousy is kindled that will cast him down into the dark - where the story, and God's plan, truly begin.", "It begins in a father's love and a night vision. A boy is favored, a dream is given, and a jealousy is kindled that will cast him down into the dark - where the story, and God's plan, truly begin.", "It begins in a father's love and a night vision. A boy is favored, a dream is given, and a jealousy is kindled that will cast him down into the dark - where the story, and God's plan, truly begin."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The Dream", "The Dream", "The Dream"),
            surah = 12, ayah = 4,
            arabic = "إِذْ قَالَ يُوسُفُ لِأَبِيهِ يَٰٓأَبَتِ إِنِّى رَأَيْتُ أَحَدَ عَشَرَ كَوْكَبًۭا وَٱلشَّمْسَ وَٱلْقَمَرَ رَأَيْتُهُمْ لِى سَٰجِدِينَ",
            translation = LocalizedText("When Joseph said to his father, “O my father, I saw eleven stars, and the sun and the moon - I saw them prostrating to me.”", "When Joseph said to his father, “O my father, I saw eleven stars, and the sun and the moon - I saw them prostrating to me.”", ""),
            reference = "Yusuf · 12 : 4",
            reflection = LocalizedText("One dream, and the whole surah is set in motion. The Ahl al-Bayt read the prostrating stars as a sign of spiritual rank, not worldly power, and said the believers are like stars that borrow their light from the suns of the prophets. Hold this dream the way Ya'qub told his son to hold it: a promise from God that will not come true for forty years.", "One dream, and the whole surah is set in motion. The Ahl al-Bayt read the prostrating stars as a sign of spiritual rank, not worldly power, and said the believers are like stars that borrow their light from the suns of the prophets. Hold this dream the way Ya'qub told his son to hold it: a promise from God that will not come true for forty years.", "One dream, and the whole surah is set in motion. The Ahl al-Bayt read the prostrating stars as a sign of spiritual rank, not worldly power, and said the believers are like stars that borrow their light from the suns of the prophets. Hold this dream the way Ya'qub told his son to hold it: a promise from God that will not come true for forty years.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Into the Well", "Into the Well", "Into the Well"),
            surah = 12, ayah = 15,
            arabic = "فَلَمَّا ذَهَبُوا۟ بِهِۦ وَأَجْمَعُوٓا۟ أَن يَجْعَلُوهُ فِى غَيَٰبَتِ ٱلْجُبِّ ۚ وَأَوْحَيْنَآ إِلَيْهِ لَتُنَبِّئَنَّهُم بِأَمْرِهِمْ هَٰذَا وَهُمْ لَا يَشْعُرُونَ",
            translation = LocalizedText("So when they took him away and agreed to cast him into the bottom of the well… We revealed to him, “You will surely tell them of this deed of theirs, when they do not perceive who you are.”", "So when they took him away and agreed to cast him into the bottom of the well… We revealed to him, “You will surely tell them of this deed of theirs, when they do not perceive who you are.”", ""),
            reference = "Yusuf · 12 : 15",
            reflection = LocalizedText("At the very bottom - betrayed, alone, a child in the dark - the first voice he hears is God's. Not rescue, but a promise: one day you will stand before them, and they will not know you. Imam Ali said God is nearest to His servant in the hardest hour. The well was not the end of the dream. It was the first step into it.", "At the very bottom - betrayed, alone, a child in the dark - the first voice he hears is God's. Not rescue, but a promise: one day you will stand before them, and they will not know you. Imam Ali said God is nearest to His servant in the hardest hour. The well was not the end of the dream. It was the first step into it.", "At the very bottom - betrayed, alone, a child in the dark - the first voice he hears is God's. Not rescue, but a promise: one day you will stand before them, and they will not know you. Imam Ali said God is nearest to His servant in the hardest hour. The well was not the end of the dream. It was the first step into it.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Beautiful Patience", "Beautiful Patience", "Beautiful Patience"),
            surah = 12, ayah = 18,
            arabic = "وَجَآءُو عَلَىٰ قَمِيصِهِۦ بِدَمٍۢ كَذِبٍۢ ۚ قَالَ بَلْ سَوَّلَتْ لَكُمْ أَنفُسُكُمْ أَمْرًۭا ۖ فَصَبْرٌۭ جَمِيلٌۭ ۖ وَٱللَّهُ ٱلْمُسْتَعَانُ عَلَىٰ مَا تَصِفُونَ",
            translation = LocalizedText("And they brought his shirt stained with false blood. He said, “Rather, your souls have enticed you to something. So beautiful patience. And God is the One whose help is sought against what you describe.”", "And they brought his shirt stained with false blood. He said, “Rather, your souls have enticed you to something. So beautiful patience. And God is the One whose help is sought against what you describe.”", ""),
            reference = "Yusuf · 12 : 18",
            reflection = LocalizedText("The shirt is whole, untorn by any wolf, and Ya'qub knows at once. Yet he does not rage. He says sabrun jamil, beautiful patience. The Ahl al-Bayt defined it as patience that complains to no one but God. Not a heart that does not break - a grief carried to the right door.", "The shirt is whole, untorn by any wolf, and Ya'qub knows at once. Yet he does not rage. He says sabrun jamil, beautiful patience. The Ahl al-Bayt defined it as patience that complains to no one but God. Not a heart that does not break - a grief carried to the right door.", "The shirt is whole, untorn by any wolf, and Ya'qub knows at once. Yet he does not rage. He says sabrun jamil, beautiful patience. The Ahl al-Bayt defined it as patience that complains to no one but God. Not a heart that does not break - a grief carried to the right door.")
        ),
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have watched him fall into the well, and heard God promise he would rise.", "You have watched him fall into the well, and heard God promise he would rise.", "You have watched him fall into the well, and heard God promise he would rise."),
            line = LocalizedText("Now a second pit, and a subtler one: the ruler's house, its comfort, its temptation, and a prison that will hide, inside its walls, the next turn of God's plan.", "Now a second pit, and a subtler one: the ruler's house, its comfort, its temptation, and a prison that will hide, inside its walls, the next turn of God's plan.", "Now a second pit, and a subtler one: the ruler's house, its comfort, its temptation, and a prison that will hide, inside its walls, the next turn of God's plan."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Proof of His Lord", "The Proof of His Lord", "The Proof of His Lord"),
            surah = 12, ayah = 24,
            arabic = "وَلَقَدْ هَمَّتْ بِهِۦ ۖ وَهَمَّ بِهَا لَوْلَآ أَن رَّءَا بُرْهَٰنَ رَبِّهِۦ ۚ كَذَٰلِكَ لِنَصْرِفَ عَنْهُ ٱلسُّوٓءَ وَٱلْفَحْشَآءَ ۚ إِنَّهُۥ مِنْ عِبَادِنَا ٱلْمُخْلَصِينَ",
            translation = LocalizedText("And she certainly desired him, and he would have desired her - had he not seen the proof of his Lord. So it was, that We might turn away from him evil and indecency. Indeed, he was among Our chosen servants.", "And she certainly desired him, and he would have desired her - had he not seen the proof of his Lord. So it was, that We might turn away from him evil and indecency. Indeed, he was among Our chosen servants.", ""),
            reference = "Yusuf · 12 : 24",
            reflection = LocalizedText("The most delicate verse in the surah, and the Ahl al-Bayt read it exactly: he saw the proof of his Lord, and so he never inclined at all. This is isma - not a chain that holds a prophet back, but a purity so complete that sin finds no foothold. He was of the mukhlasin, the ones God has purified for Himself.", "The most delicate verse in the surah, and the Ahl al-Bayt read it exactly: he saw the proof of his Lord, and so he never inclined at all. This is isma - not a chain that holds a prophet back, but a purity so complete that sin finds no foothold. He was of the mukhlasin, the ones God has purified for Himself.", "The most delicate verse in the surah, and the Ahl al-Bayt read it exactly: he saw the proof of his Lord, and so he never inclined at all. This is isma - not a chain that holds a prophet back, but a purity so complete that sin finds no foothold. He was of the mukhlasin, the ones God has purified for Himself.")
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("Prison, Not Sin", "Prison, Not Sin", "Prison, Not Sin"),
            surah = 12, ayah = 33,
            arabic = "قَالَ رَبِّ ٱلسِّجْنُ أَحَبُّ إِلَىَّ مِمَّا يَدْعُونَنِىٓ إِلَيْهِ ۖ وَإِلَّا تَصْرِفْ عَنِّى كَيْدَهُنَّ أَصْبُ إِلَيْهِنَّ وَأَكُن مِّنَ ٱلْجَٰهِلِينَ",
            translation = LocalizedText("He said, “My Lord, prison is dearer to me than that to which they call me. And if You do not turn their scheming away from me, I may incline to them and be of the ignorant.”", "He said, “My Lord, prison is dearer to me than that to which they call me. And if You do not turn their scheming away from me, I may incline to them and be of the ignorant.”", ""),
            reference = "Yusuf · 12 : 33",
            reflection = LocalizedText("A prophet who will not pretend he is safe from himself. He does not say “I am too pure to fall.” He says: keep me from it, or I might. Imam al-Baqir said the angels marveled that he chose the darkness of a prison over the darkness of sin. Strength is not never being tempted. It is knowing which darkness to fear.", "A prophet who will not pretend he is safe from himself. He does not say “I am too pure to fall.” He says: keep me from it, or I might. Imam al-Baqir said the angels marveled that he chose the darkness of a prison over the darkness of sin. Strength is not never being tempted. It is knowing which darkness to fear.", "A prophet who will not pretend he is safe from himself. He does not say “I am too pure to fall.” He says: keep me from it, or I might. Imam al-Baqir said the angels marveled that he chose the darkness of a prison over the darkness of sin. Strength is not never being tempted. It is knowing which darkness to fear.")
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Soul That Commands", "The Soul That Commands", "The Soul That Commands"),
            surah = 12, ayah = 53,
            arabic = "۞ وَمَآ أُبَرِّئُ نَفْسِىٓ ۚ إِنَّ ٱلنَّفْسَ لَأَمَّارَةٌۢ بِٱلسُّوٓءِ إِلَّا مَا رَحِمَ رَبِّىٓ ۚ إِنَّ رَبِّى غَفُورٌۭ رَّحِيمٌۭ",
            translation = LocalizedText("“And I do not absolve my own self. Indeed, the soul is ever commanding to evil, except those on whom my Lord has mercy. Indeed, my Lord is Forgiving, Merciful.”", "“And I do not absolve my own self. Indeed, the soul is ever commanding to evil, except those on whom my Lord has mercy. Indeed, my Lord is Forgiving, Merciful.”", ""),
            reference = "Yusuf · 12 : 53",
            reflection = LocalizedText("Even now, cleared of all blame and about to be raised to power, Yusuf will not flatter himself. From this verse the Ahl al-Bayt drew the whole map of the self: the soul that commands evil, the soul that reproaches itself, and at last the soul at peace - and no one crosses that ground by their own strength, but only by the mercy of their Lord.", "Even now, cleared of all blame and about to be raised to power, Yusuf will not flatter himself. From this verse the Ahl al-Bayt drew the whole map of the self: the soul that commands evil, the soul that reproaches itself, and at last the soul at peace - and no one crosses that ground by their own strength, but only by the mercy of their Lord.", "Even now, cleared of all blame and about to be raised to power, Yusuf will not flatter himself. From this verse the Ahl al-Bayt drew the whole map of the self: the soul that commands evil, the soul that reproaches itself, and at last the soul at peace - and no one crosses that ground by their own strength, but only by the mercy of their Lord.")
        ),
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have seen him tested, kept pure, and raised at last to the throne.", "You have seen him tested, kept pure, and raised at last to the throne.", "You have seen him tested, kept pure, and raised at last to the throne."),
            line = LocalizedText("Now the famine he foretold drives them back to Egypt: the brothers who sold him, standing before him for bread, not knowing the minister is the boy from the well. And an old father, still waiting, still refusing to despair.", "Now the famine he foretold drives them back to Egypt: the brothers who sold him, standing before him for bread, not knowing the minister is the boy from the well. And an old father, still waiting, still refusing to despair.", "Now the famine he foretold drives them back to Egypt: the brothers who sold him, standing before him for bread, not knowing the minister is the boy from the well. And an old father, still waiting, still refusing to despair."),
            bridge = BridgeVerse(
                surah = 12, ayah = 87,
                arabic = "يَٰبَنِىَّ ٱذْهَبُوا۟ فَتَحَسَّسُوا۟ مِن يُوسُفَ وَأَخِيهِ وَلَا تَا۟يْـَٔسُوا۟ مِن رَّوْحِ ٱللَّهِ ۖ إِنَّهُۥ لَا يَا۟يْـَٔسُ مِن رَّوْحِ ٱللَّهِ إِلَّا ٱلْقَوْمُ ٱلْكَٰفِرُونَ",
                translation = LocalizedText("“…and do not despair of God's relief. Indeed, no one despairs of God's relief except the disbelieving people.”", "“…and do not despair of God's relief. Indeed, no one despairs of God's relief except the disbelieving people.”", ""),
                reference = "Yusuf · 12 : 87"
            )
        ),
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("No Blame Upon You Today", "No Blame Upon You Today", "No Blame Upon You Today"),
            surah = 12, ayah = 92,
            arabic = "قَالَ لَا تَثْرِيبَ عَلَيْكُمُ ٱلْيَوْمَ ۖ يَغْفِرُ ٱللَّهُ لَكُمْ ۖ وَهُوَ أَرْحَمُ ٱلرَّٰحِمِينَ",
            translation = LocalizedText("He said, “No blame upon you today. May God forgive you, and He is the most merciful of the merciful.”", "He said, “No blame upon you today. May God forgive you, and He is the most merciful of the merciful.”", ""),
            reference = "Yusuf · 12 : 92",
            reflection = LocalizedText("He holds all the power now, and every right to revenge. Instead: no blame today. Not “I will try, in time, to forgive” - the door is thrown open the very moment it can be. And even his forgiveness he hands upward: may God forgive you. Mercy, at the exact instant it costs the most.", "He holds all the power now, and every right to revenge. Instead: no blame today. Not “I will try, in time, to forgive” - the door is thrown open the very moment it can be. And even his forgiveness he hands upward: may God forgive you. Mercy, at the exact instant it costs the most.", "He holds all the power now, and every right to revenge. Instead: no blame today. Not “I will try, in time, to forgive” - the door is thrown open the very moment it can be. And even his forgiveness he hands upward: may God forgive you. Mercy, at the exact instant it costs the most.")
        ),
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Strength to Forgive", "The Strength to Forgive", "The Strength to Forgive"),
            source = LocalizedText("Imam Ja'far al-Sadiq · Ilal al-Shara'i", "Imam Ja'far al-Sadiq · Ilal al-Shara'i", "Imam Ja'far al-Sadiq · Ilal al-Shara'i"),
            body = LocalizedText("Standing at the height of his power over the very brothers who had thrown him into the well, Yusuf forgave them completely. Imam Ja'far al-Sadiq taught that this was not weakness but the highest form of strength - the strength to overcome the self's own hunger for revenge. The soul that once “commands to evil” is answered, at the last, by a mercy that asks nothing back.", "Standing at the height of his power over the very brothers who had thrown him into the well, Yusuf forgave them completely. Imam Ja'far al-Sadiq taught that this was not weakness but the highest form of strength - the strength to overcome the self's own hunger for revenge. The soul that once “commands to evil” is answered, at the last, by a mercy that asks nothing back.", "Standing at the height of his power over the very brothers who had thrown him into the well, Yusuf forgave them completely. Imam Ja'far al-Sadiq taught that this was not weakness but the highest form of strength - the strength to overcome the self's own hunger for revenge. The soul that once “commands to evil” is answered, at the last, by a mercy that asks nothing back."),
            reflection = LocalizedText("To forgive from weakness is only to be unable to strike. To forgive from the throne - holding every power to punish, and setting it down - is the victory the whole surah has been climbing toward.", "To forgive from weakness is only to be unable to strike. To forgive from the throne - holding every power to punish, and setting it down - is the victory the whole surah has been climbing toward.", "To forgive from weakness is only to be unable to strike. To forgive from the throne - holding every power to punish, and setting it down - is the victory the whole surah has been climbing toward.")
        ),
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("The Dream Fulfilled", "The Dream Fulfilled", "The Dream Fulfilled"),
            surah = 12, ayah = 100,
            arabic = "وَرَفَعَ أَبَوَيْهِ عَلَى ٱلْعَرْشِ وَخَرُّوا۟ لَهُۥ سُجَّدًۭا ۖ وَقَالَ يَٰٓأَبَتِ هَٰذَا تَأْوِيلُ رُءْيَٰىَ مِن قَبْلُ قَدْ جَعَلَهَا رَبِّى حَقًّۭا ۖ وَقَدْ أَحْسَنَ بِىٓ إِذْ أَخْرَجَنِى مِنَ ٱلسِّجْنِ وَجَآءَ بِكُم مِّنَ ٱلْبَدْوِ مِنۢ بَعْدِ أَن نَّزَغَ ٱلشَّيْطَٰنُ بَيْنِى وَبَيْنَ إِخْوَتِىٓ ۚ إِنَّ رَبِّى لَطِيفٌۭ لِّمَا يَشَآءُ ۚ إِنَّهُۥ هُوَ ٱلْعَلِيمُ ٱلْحَكِيمُ",
            translation = LocalizedText("“…and they fell down before him in prostration. And he said, ‘O my father, this is the meaning of my vision of long ago. My Lord has made it real. He was good to me when He brought me out of the prison and brought you from the desert, after Satan had sown discord between me and my brothers…’”", "“…and they fell down before him in prostration. And he said, ‘O my father, this is the meaning of my vision of long ago. My Lord has made it real. He was good to me when He brought me out of the prison and brought you from the desert, after Satan had sown discord between me and my brothers…’”", ""),
            reference = "Yusuf · 12 : 100",
            reflection = LocalizedText("Forty years later, the eleven and the sun and the moon bow down - exactly as the boy had dreamed. And notice how he tells it: he says God brought him out of the prison, and never once out of the well; he blames Satan for the rift, and names no brother. Even the memory, he tells with mercy.", "Forty years later, the eleven and the sun and the moon bow down - exactly as the boy had dreamed. And notice how he tells it: he says God brought him out of the prison, and never once out of the well; he blames Satan for the rift, and names no brother. Even the memory, he tells with mercy.", "Forty years later, the eleven and the sun and the moon bow down - exactly as the boy had dreamed. And notice how he tells it: he says God brought him out of the prison, and never once out of the well; he blames Satan for the rift, and names no brother. Even the memory, he tells with mercy.")
        ),
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("Why It All Held", "Why It All Held", "Why It All Held"),
            source = LocalizedText("Yusuf · 12 : 90"),
            arabic = "إِنَّهُۥ مَن يَتَّقِ وَيَصْبِرْ فَإِنَّ ٱللَّهَ لَا يُضِيعُ أَجْرَ ٱلْمُحْسِنِينَ",
            translation = LocalizedText("Indeed, whoever is mindful of God and is patient - God does not let the reward of those who do good be lost.", "Indeed, whoever is mindful of God and is patient - God does not let the reward of those who do good be lost.", ""),
            body = LocalizedText("When his brothers finally ask, “Are you really Yusuf?”, he gives them the meaning of everything: God has favored us. And then the line that holds the whole surah together - whoever is mindful of God and patient, God does not let the reward of the doers of good be lost. The dream, the well, the slavery, the prison, the long wait: none of it was wasted. Not one hour of it.", "When his brothers finally ask, “Are you really Yusuf?”, he gives them the meaning of everything: God has favored us. And then the line that holds the whole surah together - whoever is mindful of God and patient, God does not let the reward of the doers of good be lost. The dream, the well, the slavery, the prison, the long wait: none of it was wasted. Not one hour of it.", "When his brothers finally ask, “Are you really Yusuf?”, he gives them the meaning of everything: God has favored us. And then the line that holds the whole surah together - whoever is mindful of God and patient, God does not let the reward of the doers of good be lost. The dream, the well, the slavery, the prison, the long wait: none of it was wasted. Not one hour of it."),
            reflection = LocalizedText("This is the soul of the surah. Beautiful patience is not the absence of pain. It is the certainty that God is keeping an account no darkness can erase. Every pit was a road. Every year was counted.", "This is the soul of the surah. Beautiful patience is not the absence of pain. It is the certainty that God is keeping an account no darkness can erase. Every pit was a road. Every year was counted.", "This is the soul of the surah. Beautiful patience is not the absence of pain. It is the certainty that God is keeping an account no darkness can erase. Every pit was a road. Every year was counted.")
        ),
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("Return", "Return", "Return"),
            prompt = LocalizedText("What is the well you are in?", "What is the well you are in?", "What is the well you are in?"),
            placeholder = LocalizedText("A loss, a long wait, an injustice, a door that will not open…", "A loss, a long wait, an injustice, a door that will not open…", "A loss, a long wait, an injustice, a door that will not open…"),
            subline = LocalizedText("You have walked the whole descent - the dream, the well, the prison, the reunion. Yusuf's story says no faithful patience is ever wasted. Before you go, name the pit you are in, and the dream you are being asked to keep trusting.", "You have walked the whole descent - the dream, the well, the prison, the reunion. Yusuf's story says no faithful patience is ever wasted. Before you go, name the pit you are in, and the dream you are being asked to keep trusting.", "You have walked the whole descent - the dream, the well, the prison, the reunion. Yusuf's story says no faithful patience is ever wasted. Before you go, name the pit you are in, and the dream you are being asked to keep trusting."),
            nextLabel = LocalizedText("One last thing", "One last thing", "One last thing")
        ),
        DeepDiveSection.Closing(
            tag = LocalizedText("The Close", "The Close", "The Close"),
            titleAr = "يُوسُف",
            essence = LocalizedText("At the summit of all his power, Yusuf asked God for one thing only: to die in submission, and to be joined with the righteous.", "At the summit of all his power, Yusuf asked God for one thing only: to die in submission, and to be joined with the righteous.", "At the summit of all his power, Yusuf asked God for one thing only: to die in submission, and to be joined with the righteous."),
            line = LocalizedText("That is where the best of stories comes to rest - not on a throne, but on a heart that gave every rise back to God. Read the surah now in its own words, and let the dream unfold in full.", "That is where the best of stories comes to rest - not on a throne, but on a heart that gave every rise back to God. Read the surah now in its own words, and let the dream unfold in full.", "That is where the best of stories comes to rest - not on a throne, but on a heart that gave every rise back to God. Read the surah now in its own words, and let the dream unfold in full.")
        )
    )
)
