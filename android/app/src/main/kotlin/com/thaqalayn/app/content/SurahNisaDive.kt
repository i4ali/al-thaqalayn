package com.thaqalayn.app.content

// Fixed content for the "Inside the Surah - al-Nisa" experience. Verbatim port
// of iOS Content/SurahNisaDive.swift; rendered by DeepDiveScreen. One spine -
// al-amana, the trust owed to its rightful owner - across three movements
// (al-Ri'aya / al-Qist / al-Amr), closing on a Closing beat that hands off to
// reading the full surah. English-only for now: every LocalizedText is a bare
// English string (ur/ar fall back to English).

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.LocalizedText

val surahNisaDive: DeepDive = DeepDive(
    id = "surah-nisa",
    titleEn = "al-Nisa",
    titleAr = "النِّسَاء",
    subtitle = LocalizedText("The Women - where God's justice begins, and where it comes to rest"),
    estMinutes = 12,
    acts = listOf(
        ActInfo(1, "الرِّعَايَة", "al-Ri'aya", LocalizedText("The Guardian's Trust")),
        ActInfo(2, "الْقِسْط", "al-Qist", LocalizedText("The Judge's Trust")),
        ActInfo(3, "الْأَمْر", "al-Amr", LocalizedText("The Trust of Authority"))
    ),
    sections = listOf(
        DeepDiveSection.Open(
            kicker = LocalizedText("INSIDE THE SURAH"),
            titleAr = "النِّسَاء",
            titleEn = "al-Nisa",
            subtitle = LocalizedText("The Women"),
            line = LocalizedText("The fourth surah, and among the longest in the Qur'an - a hundred and seventy-six verses of law: inheritance, marriage, warfare, judgment. And of every name it could have borne, God gave it this one: al-Nisa, The Women. That name is the first clue to what the surah is really about. Because God begins His justice not with the strong, but with the ones who could not demand it.")
        ),
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you begin"),
            promise = LocalizedText("One command runs beneath this whole surah, and at its center God says it plainly: render every trust back to the one it belongs to. Watch it work. First the surah presses that command downward, to the weakest - the orphan robbed of his inheritance, the woman the world gave nothing, the stranger no one will defend. Then it lifts the same command upward, to the gravest trust of all: the authority by which a whole religion is steered."),
            leaveWith = LocalizedText("You will leave seeing al-Nisa whole - not a list of scattered rulings but a single descent of one command, from the coin owed to an orphan to the authority a religion cannot do without. And you will know the name the surah has been quietly carrying from its first verse to its last.")
        ),

        // ───────────── Movement I - al-Ri'aya · The Guardian's Trust ─────────────
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("The surah does not open with a rule. It opens with a reminder of where you came from. Before God asks you to be just to anyone, He tells you the truth about every person you could ever wrong: they are made of exactly what you are made of. There is only one family standing here."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("One Soul"),
            surah = 4, ayah = 1,
            arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ يَٰٓأَيُّهَا ٱلنَّاسُ ٱتَّقُوا۟ رَبَّكُمُ ٱلَّذِى خَلَقَكُم مِّن نَّفْسٍۢ وَٰحِدَةٍۢ وَخَلَقَ مِنْهَا زَوْجَهَا وَبَثَّ مِنْهُمَا رِجَالًۭا كَثِيرًۭا وَنِسَآءًۭ ۚ وَٱتَّقُوا۟ ٱللَّهَ ٱلَّذِى تَسَآءَلُونَ بِهِۦ وَٱلْأَرْحَامَ ۚ إِنَّ ٱللَّهَ كَانَ عَلَيْكُمْ رَقِيبًۭا",
            translation = LocalizedText("In the name of Allah, the All-Merciful, the Ever-Merciful. O mankind, be mindful of your Lord, who created you from a single soul, and created from it its mate, and scattered from the two of them countless men and women. And be mindful of Allah, in whose name you ask of one another, and of the ties of the womb. Surely Allah is ever watching over you."),
            reference = "al-Nisa · 4 : 1",
            reflection = LocalizedText("Before a single law, God levels everyone standing in the surah. You were made from one soul, and so was the person you are tempted to cheat - the same origin, the same return. Imam al-Baqir (alayhi al-salam) said that whoever truly understands this verse can never again oppress another or imagine himself above them. Then God names the very first trust: al-arham, the ties of the womb. Honor them, He says in the same breath as honoring Himself - and Imam al-Sadiq (alayhi al-salam) taught that keeping these bonds lengthens a life, while cutting them cuts you off. And the verse ends with one word held over your head like a lamp: Raqib. He is watching. Every trust the surah is about to name, He already sees whether you keep.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Fire in the Belly"),
            surah = 4, ayah = 10,
            arabic = "إِنَّ ٱلَّذِينَ يَأْكُلُونَ أَمْوَٰلَ ٱلْيَتَٰمَىٰ ظُلْمًا إِنَّمَا يَأْكُلُونَ فِى بُطُونِهِمْ نَارًۭا ۖ وَسَيَصْلَوْنَ سَعِيرًۭا",
            translation = LocalizedText("Those who consume the property of orphans unjustly are only swallowing fire into their bellies, and they will burn in a Blaze."),
            reference = "al-Nisa · 4 : 10",
            reflection = LocalizedText("Of all the trusts a person can betray, the surah reaches first for the most defenseless: the orphan, a child with no father to stand for him and no strength to take back what is his. God does not merely call stealing his inheritance a sin. He calls it fire - swallowed now, into the belly, long before the Day it blazes out. Imam al-Rida (alayhi al-salam) explained the severity in one line: because the orphan cannot defend himself, God appoints Himself his defender and his avenger. And Imam Ali (alayhi al-salam) warned that a single dirham wrongly taken from an orphan will rise and testify against the hand that took it. This is where the surah plants its flag. Justice is measured exactly where the world looks away.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("A Share, Fixed"),
            surah = 4, ayah = 7,
            arabic = "لِّلرِّجَالِ نَصِيبٌۭ مِّمَّا تَرَكَ ٱلْوَٰلِدَانِ وَٱلْأَقْرَبُونَ وَلِلنِّسَآءِ نَصِيبٌۭ مِّمَّا تَرَكَ ٱلْوَٰلِدَانِ وَٱلْأَقْرَبُونَ مِمَّا قَلَّ مِنْهُ أَوْ كَثُرَ ۚ نَصِيبًۭا مَّفْرُوضًۭا",
            translation = LocalizedText("For men is a share of what the parents and near relatives leave, and for women is a share of what the parents and near relatives leave - be it little or much - a share ordained."),
            reference = "al-Nisa · 4 : 7",
            reflection = LocalizedText("The surah is named, remember, for the women, and here is what it does for them. In the world these verses fell into, a woman did not inherit wealth - in some tribes she was wealth, handed on with the estate like the livestock. Into that world God says: for women a share, of everything the parents and kinfolk leave, little or much. Not a kindness the family may grant or withhold, but nasiban mafrudan - a portion fixed and made binding by God Himself. Al-Mizan notes what that quietly accomplished: it placed property in the hands of the very people custom had erased. This is why the surah carries her name. Its justice does not begin at the top and trickle down. It begins at the bottom, with the one who had no claim at all, and turns her claim into a command.")
        ),
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The Widening Circle"),
            surah = 4, ayah = 36,
            arabic = "۞ وَٱعْبُدُوا۟ ٱللَّهَ وَلَا تُشْرِكُوا۟ بِهِۦ شَيْـًۭٔا ۖ وَبِٱلْوَٰلِدَيْنِ إِحْسَٰنًۭا وَبِذِى ٱلْقُرْبَىٰ وَٱلْيَتَٰمَىٰ وَٱلْمَسَٰكِينِ وَٱلْجَارِ ذِى ٱلْقُرْبَىٰ وَٱلْجَارِ ٱلْجُنُبِ وَٱلصَّاحِبِ بِٱلْجَنۢبِ وَٱبْنِ ٱلسَّبِيلِ وَمَا مَلَكَتْ أَيْمَٰنُكُمْ ۗ إِنَّ ٱللَّهَ لَا يُحِبُّ مَن كَانَ مُخْتَالًۭا فَخُورًا",
            translation = LocalizedText("Worship Allah and associate nothing with Him. And be good to parents, to relatives, to orphans, to the needy, to the near neighbor and the neighbor farther off, to the companion at your side and the traveler, and to those your right hands possess. Surely Allah does not love the arrogant and boastful."),
            reference = "al-Nisa · 4 : 36",
            reflection = LocalizedText("Watch how the verse is built. It begins at the center, with worshipping God alone, and then moves outward in rings: parents, kin, the orphan, the needy, the neighbor near and far, the traveler, the servant beneath your own roof. Imam Ali (alayhi al-salam) said the rights spread from you like ripples from a stone dropped in water, each ring real, none of them optional. Imam al-Sadiq (alayhi al-salam) weighed the neighbor's claim so carefully - three rights for the neighbor who is also kin, two for the Muslim neighbor, one for any neighbor at all - and the Prophet ﷺ said Gabriel kept pressing him about the neighbor until he thought neighbors would be made heirs. Imam Zayn al-Abidin (alayhi al-salam) gathered the whole of it into his Treatise on Rights, the Risalat al-Huquq, on a single principle: to recognize what you owe another is itself an act of worship. Only one kind of person is blind to all of it - the arrogant, named at the verse's close, too swollen with himself to notice what he owes.")
        ),

        // ───────────── Movement II - al-Qist · The Judge's Trust ─────────────
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have seen the trust owed to those in your care - the orphan, the woman, the neighbor, the stranger at your side."),
            line = LocalizedText("Now the surah moves you from the one who owes to the one who decides. It sits you in the hardest chair a person can occupy: the seat of the judge, where being just is no longer a matter of generosity but of telling the truth when the truth will cost you. And it teaches you there with a scandal - a real crime, in Madina, that reached the court of the Prophet ﷺ."),
            bridge = null
        ),
        DeepDiveSection.Narration(
            act = 2,
            tag = LocalizedText("The Theft in Madina"),
            source = LocalizedText("Occasion of revelation - al-Mizan (Tabatabai); Majma al-Bayan (Tabrisi)"),
            body = LocalizedText("A man named Ta'imah ibn Ubayriq, of the Ansar, stole a coat of armor and hid it in the house of an innocent Jewish neighbor. When the trail led back to him, he swore he was clean and let the blame fall on the Jew. Then his whole clan came to the Prophet ﷺ, pressing him to rule for their man and clear the Muslim's name - it would shame the community, they argued, to take a Jew's side against one of our own. It was a trap dressed as loyalty: they meant to use the Prophet's own court to wash a theft clean. And God would not allow it. He sent down verse after verse exposing the scheme, refusing to let His Messenger be made an advocate for a liar, and vindicating the very man they had all agreed to sacrifice."),
            reflection = LocalizedText("Sit with how close it came. The pressure was not from enemies but from believers, wrapped in the language of community and honor. That is how injustice usually arrives - not as an obvious evil, but as loyalty to your own. The surah freezes the moment to show you the standard God then set against it, in the next verse.")
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("By What God Shows You"),
            surah = 4, ayah = 105,
            arabic = "إِنَّآ أَنزَلْنَآ إِلَيْكَ ٱلْكِتَٰبَ بِٱلْحَقِّ لِتَحْكُمَ بَيْنَ ٱلنَّاسِ بِمَآ أَرَىٰكَ ٱللَّهُ ۚ وَلَا تَكُن لِّلْخَآئِنِينَ خَصِيمًۭا",
            translation = LocalizedText("We have sent down to you the Book with the truth, so that you may judge between people by what God has shown you. So do not be an advocate for the treacherous."),
            reference = "al-Nisa · 4 : 105",
            reflection = LocalizedText("Read the standard, and how high it stands: judge by what God has shown you. Not by what your people want, not by what protects the community's name, not by which party shares your faith - bima araka Allah, by the truth as God reveals it. Al-Mizan draws out the astonishing consequence the Madina scandal proved: God's justice can require a Muslim judge to rule for a Jew against a Muslim, when that is where the truth lies. Your tribe is not your side. The truth is your side. And notice what the verse tells you about the Prophet ﷺ himself: he does not rule from his own instinct, which men had just tried to bend, but from what God shows him. His judgment could not be captured, because it was never only his.")
        ),
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("Even Against Yourself"),
            surah = 4, ayah = 135,
            arabic = "۞ يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوا۟ كُونُوا۟ قَوَّٰمِينَ بِٱلْقِسْطِ شُهَدَآءَ لِلَّهِ وَلَوْ عَلَىٰٓ أَنفُسِكُمْ أَوِ ٱلْوَٰلِدَيْنِ وَٱلْأَقْرَبِينَ ۚ إِن يَكُنْ غَنِيًّا أَوْ فَقِيرًۭا فَٱللَّهُ أَوْلَىٰ بِهِمَا ۖ فَلَا تَتَّبِعُوا۟ ٱلْهَوَىٰٓ أَن تَعْدِلُوا۟ ۚ وَإِن تَلْوُۥٓا۟ أَوْ تُعْرِضُوا۟ فَإِنَّ ٱللَّهَ كَانَ بِمَا تَعْمَلُونَ خَبِيرًۭا",
            translation = LocalizedText("O you who believe, be ever upholders of justice, witnesses for God, even against yourselves, or your parents, or your kin. Whether the person is rich or poor, God is nearer to both. So do not follow desire, lest you swerve. And if you distort or turn away, surely God is aware of all you do."),
            reference = "al-Nisa · 4 : 135",
            reflection = LocalizedText("Here the seat of judgment turns to face you. Be qawwamin bil-qist, God says - not people who happen to be fair, but people who stand justice upright and hold it there, witnesses for God even when the testimony falls against your own self, your parents, your family. Rich or poor, God is nearer to both than you are, so it was never your place to tip the scale toward either. The one thing that bends it, the verse warns, is hawa - your own wanting. Imam Ali (alayhi al-salam) gave the hardest proof of it. When his own brother Aqil, poor and burdened with children, begged him for a little more than his share from the public treasury, Ali refused - and to answer the plea, he brought a piece of iron heated in the fire near his brother's hand until Aqil cried out. Do you scream, he asked, from an iron a man heated in play, while you would drag me toward a Fire God kindled in His wrath? Not one coin of what belonged to the Muslims would he hand his own brother. That is what a witness for God looks like. Justice that costs you nothing was never really justice.")
        ),

        // ───────────── Movement III - al-Amr · The Trust of Authority ─────────────
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have sat in the seat of judgment and seen what it demands - sight that cannot be bought, a witness who will testify against his own blood."),
            line = LocalizedText("So ask the question the surah has been walking you toward. If justice needs a judge who cannot be captured and cannot be bent, then everything hangs on who holds that seat. Who can be trusted with it - not for a single afternoon, but for a whole people, for a religion, after the Prophet ﷺ is gone? The surah has an answer. And it gives it exactly the way it has given everything else: as a trust, to be rendered to the one it belongs to."),
            bridge = null
        ),
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("Render the Trust"),
            surah = 4, ayah = 58,
            arabic = "۞ إِنَّ ٱللَّهَ يَأْمُرُكُمْ أَن تُؤَدُّوا۟ ٱلْأَمَٰنَٰتِ إِلَىٰٓ أَهْلِهَا وَإِذَا حَكَمْتُم بَيْنَ ٱلنَّاسِ أَن تَحْكُمُوا۟ بِٱلْعَدْلِ ۚ إِنَّ ٱللَّهَ نِعِمَّا يَعِظُكُم بِهِۦٓ ۗ إِنَّ ٱللَّهَ كَانَ سَمِيعًۢا بَصِيرًۭا",
            translation = LocalizedText("Surely God commands you to render trusts back to those they belong to, and, when you judge between people, to judge with justice. How excellent is what God instructs you. Surely God is ever Hearing, Seeing."),
            reference = "al-Nisa · 4 : 58",
            reflection = LocalizedText("Here is the verse the whole surah has been building toward, and it does two things in a single breath: render trusts to those they belong to, and judge between people with justice. The orphan's coin and the seat of the judge, named together, as one command - because they are one command. Al-Mizan says the word here, al-amanat, reaches far past money and borrowed goods: the greatest of all trusts is authority itself - leadership, judgment, the sacred knowledge by which a religion is steered - and it too may be placed only with the one it belongs to. And the narrations of the Ahl al-Bayt make that owner explicit: this verse, they teach, came down about the returning of leadership - wilaya - to Ali (alayhi al-salam), to whom God had entrusted it. Imam al-Baqir (alayhi al-salam) said the trust includes divine knowledge and authority, to be handed to, and accepted from, those God appointed, and no one else. The key has turned. The same law that guarded a child's inheritance now governs who may inherit the authority of a prophet.")
        ),
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("Those in Authority"),
            source = LocalizedText("al-Nisa · 4 : 59"),
            arabic = "يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوٓا۟ أَطِيعُوا۟ ٱللَّهَ وَأَطِيعُوا۟ ٱلرَّسُولَ وَأُو۟لِى ٱلْأَمْرِ مِنكُمْ ۖ فَإِن تَنَٰزَعْتُمْ فِى شَىْءٍۢ فَرُدُّوهُ إِلَى ٱللَّهِ وَٱلرَّسُولِ إِن كُنتُمْ تُؤْمِنُونَ بِٱللَّهِ وَٱلْيَوْمِ ٱلْءَاخِرِ ۚ ذَٰلِكَ خَيْرٌۭ وَأَحْسَنُ تَأْوِيلًا",
            translation = LocalizedText("O you who believe, obey God, and obey the Messenger, and those in authority among you. Then if you dispute over anything, refer it to God and the Messenger, if you truly believe in God and the Last Day. That is best, and fairest in outcome."),
            body = LocalizedText("Stop at the strangest thing in the verse - a silence most readers walk straight past. God says atiu, obey, and repeats it: obey God, obey the Messenger. But when He reaches those in authority, He does not say it a third time. Uli al-amr is simply joined to the Messenger, carrying no verb of obedience of its own. Al-Mizan reads the grammar exactly: their obedience is not a second, separate loyalty - it is folded into the Messenger's, and can never for a moment pull away from God. Now hold that beside the other thing the verse does not say. It commands this obedience with no condition at all - not “obey them if they are just,” not “unless they err.” Absolute. And here Imam al-Rida (alayhi al-salam) laid down an argument that has never been answered: God, who is just, could not possibly command you to obey - with no condition - a man who might command you to sin. So whoever these people are, they cannot be rulers who err. They must be men God Himself keeps free of error. Not everyone “among you,” then - the tyrant and the schemer are ruled out by the very word. Only those God has purified. And in all the Qur'an, God says He wholly removed impurity from one household alone."),
            reflection = LocalizedText("There is a word God speaks over only one household in all the Qur'an. It is the word the angels spoke over Maryam (alayha al-salam), and the word He sent down over the family gathered beneath the cloak: “God only desires to remove all impurity from you, People of the House, and to purify you completely.” The ones fit to be obeyed without condition are the ones God made pure without exception - the Ahl al-Bayt, the purified household of the Prophet ﷺ. This is the trust of the verse before it, given a face at last: the authority by which a religion is steered, rendered to the household God prepared to carry it. Not raised up by an army, not chosen by a vote - purified by God, and so safe to follow all the way down.")
        ),
        DeepDiveSection.Verse(
            act = 3,
            tag = LocalizedText("In Their Company"),
            surah = 4, ayah = 69,
            arabic = "وَمَن يُطِعِ ٱللَّهَ وَٱلرَّسُولَ فَأُو۟لَٰٓئِكَ مَعَ ٱلَّذِينَ أَنْعَمَ ٱللَّهُ عَلَيْهِم مِّنَ ٱلنَّبِيِّۦنَ وَٱلصِّدِّيقِينَ وَٱلشُّهَدَآءِ وَٱلصَّٰلِحِينَ ۚ وَحَسُنَ أُو۟لَٰٓئِكَ رَفِيقًۭا",
            translation = LocalizedText("Whoever obeys God and the Messenger - they are with those God has blessed: the prophets, the truthful, the martyrs, and the righteous. And how excellent are these as companions."),
            reference = "al-Nisa · 4 : 69",
            reflection = LocalizedText("And obedience like that is not a leash - it is a doorway. Whoever obeys God and His Messenger, the verse promises, is gathered in the end with alladhina an'ama Allahu alayhim, those God has blessed: the prophets, the truthful, the martyrs, the righteous. If you have ever prayed al-Fatiha, you have already asked for exactly this - sirat alladhina an'amta alayhim, the path of those You have blessed. Here the surah tells you who walks that path and where it arrives: the truthful who never once bent the truth, the martyrs who bought it with their blood - Imam Husayn (alayhi al-salam) and his companions at Karbala among them - and the promise the Prophet ﷺ made plain, that a person will be, on that Day, with the ones he loved. Obey the authority God purified, and you are not merely governed by them. You are kept with them, forever.")
        ),

        // ───────────── The Close (act 4) ─────────────
        DeepDiveSection.Narration(
            act = 4,
            tag = LocalizedText("The Two He Left"),
            source = LocalizedText("al-Nisa · 4 : 174-175 · Hadith al-Thaqalayn"),
            body = LocalizedText("As the surah ends, it puts something in your hands to carry out of it. “O mankind,” God says, “a clear proof has now come to you from your Lord, and We have sent down to you a shining light.” Al-Mizan reads the two: the burhan, the proof no one can argue down, is the Prophet ﷺ himself - his life and his message; and the nur, the shining light, is the Qur'an. And the Ahl al-Bayt read them together, through the Prophet's own farewell: “I am leaving among you two weighty things - the Book of God, and my family, my Ahl al-Bayt. Hold fast to them both, and you will never go astray, for the two will never part until they return to me at the Fountain.” The proof and the light are the very pair the surah has spent itself defending: the Book, and the purified house entrusted to carry it."),
            reflection = LocalizedText("And then the last promise: those who hold fast to God, He will bring into mercy from Himself and bounty, and “guide them to Himself on a straight path.” You asked for that path once, at the very opening of the Book - ihdina al-sirat al-mustaqim, guide us to the straight path. Here, at the close of the surah of justice, God tells you it was never an abstraction. The straight path is the Book you are holding and the household that never leaves it. Hold to both, and every trust in the surah - the orphan's coin, the woman's right, the judge's honesty, the authority of the pure - comes to rest, at last, in the same two hands God appointed to keep it.")
        ),
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("The Return"),
            prompt = LocalizedText("Which trust is yours to render?"),
            placeholder = LocalizedText("A right you owe someone weaker, a truth you must tell against your own side…"),
            subline = LocalizedText("You have followed one command down the whole length of the surah - render every trust to the one it belongs to. Somewhere in your own life is a trust with your name on it: something owed to a person who cannot make you give it, or a truth you would have to tell against your own interest, your own people, your own self. Name it. That is your amana."),
            nextLabel = LocalizedText("One last thing")
        ),
        DeepDiveSection.Closing(
            tag = LocalizedText("The Close"),
            titleAr = "النِّسَاء",
            essence = LocalizedText("A surah named for the powerless, to teach that justice is one unbroken trust - from the coin owed to an orphan, to the authority owed to those God made pure."),
            line = LocalizedText("You have seen al-Nisa whole - not a book of scattered rules, but one command widening outward: be just to the weak, judge without a side, and render the greatest trust of all to the household God prepared to hold it. Read the surah now in its own words, unhurried, and watch the single thread run through all hundred and seventy-six verses - every trust, returned to the one it belongs to.")
        )
    )
)
