package com.thaqalayn.app.content

// Fixed content for the "Yaqin" deep dive - a descent through the three depths
// of certainty (Ilm / Ayn / Haqq al-Yaqin). Verbatim port of iOS
// Content/YaqinDeepDive.swift; rendered by DeepDiveScreen. Prose is trilingual
// (EN/UR/AR); Qur'an Arabic, references, and surah/ayah numbers are
// language-neutral (LocalizedText falls back to English when ur/ar are null).

import com.thaqalayn.app.model.ActInfo
import com.thaqalayn.app.model.BridgeVerse
import com.thaqalayn.app.model.DeepDive
import com.thaqalayn.app.model.DeepDiveSection
import com.thaqalayn.app.model.Depth
import com.thaqalayn.app.model.LocalizedText

val yaqinDive: DeepDive = DeepDive(
    id = "yaqin",
    titleEn = "Yaqin",
    titleAr = "يَقِين",
    subtitle = LocalizedText("Certainty - a descent through three depths", "یقین - تین گہرائیوں میں اترتا ایک سفر", "اليقين - نزولٌ عبر ثلاثة أعماق"),
    estMinutes = 5,
    acts = listOf(
        ActInfo(1, "عِلْمُ الْيَقِين", "'Ilm al-Yaqin", LocalizedText("The Knowing", "جاننا", "المعرفة")),
        ActInfo(2, "عَيْنُ الْيَقِين", "'Ayn al-Yaqin", LocalizedText("The Witnessing", "دیکھنا", "المشاهدة")),
        ActInfo(3, "حَقُّ الْيَقِين", "Haqq al-Yaqin", LocalizedText("The Living", "جینا", "المعايشة"))
    ),
    sections = listOf(
        // 01. Opening
        DeepDiveSection.Open(
            kicker = LocalizedText("A DEEP DIVE", "ایک گہرا مطالعہ", "غوص عميق"),
            titleAr = "يَقِين",
            titleEn = "Yaqin",
            subtitle = LocalizedText("Certainty", "یقین", "اليقين"),
            line = LocalizedText("A descent through the Qur'an and the Ahl al-Bayt - in three depths.", "قرآن اور اہل بیتؑ کی معیت میں ایک سفر - تین گہرائیوں کے اندر اترتا ہوا۔", "نزولٌ عبر القرآن الكريم وأهل البيت عليهم السلام - في ثلاثة أعماق.")
        ),

        // 02. Before you descend
        DeepDiveSection.Orientation(
            eyebrow = LocalizedText("Before you descend", "اترنے سے پہلے", "قبل أن تنزل"),
            promise = LocalizedText("Three depths of certainty lie below - to know it, to see it, to live it.", "نیچے یقین کی تین گہرائیاں موجود ہیں - اسے جاننا، اسے دیکھنا، اور اسے جینا۔", "ثلاثةُ أعماقٍ من اليقين تكمن أدناه - أن تعرفه، وأن تراه، وأن تحياه."),
            leaveWith = LocalizedText("You'll leave with a map of certainty - and a prayer to deepen your own.", "آپ یہاں سے یقین کا ایک نقشہ لے کر جائیں گے - اور ایک ایسی دعا بھی، جو آپ کے اپنے یقین کو مزید گہرا کر دے۔", "ستخرج بخريطةٍ لليقين - ودعاءٍ يعمّق يقينَك أنت.")
        ),

        // 02b. Threshold - The Three Depths (overview map, before the descent)
        DeepDiveSection.Depths(
            act = 0,
            tag = LocalizedText("The Three Depths", "تین گہرائیاں", "الأعماق الثلاثة"),
            reference = "al-Takathur · al-Waqi'ah",
            items = listOf(
                Depth("عِلْمُ الْيَقِين", "‘Ilm al-Yaqin", LocalizedText("Knowledge of Certainty", "علم الیقین", "عِلْمُ اليَقِين"), LocalizedText("To know the fire exists - by the smoke on the horizon.", "آگ کا وجود جاننا - افق پر اٹھتے دھوئیں سے۔", "أن تعلم أنّ النار موجودة - من الدخان المتصاعد في الأفق."), null, LocalizedText("the mind that reasons", "غور و فکر کرنے والا ذہن", "العقل الذي يستدلّ")),
                Depth("عَيْنُ الْيَقِين", "‘Ayn al-Yaqin", LocalizedText("Eye of Certainty", "عین الیقین", "عَيْنُ اليَقِين"), LocalizedText("To see the fire with your own eyes.", "آگ کو اپنی آنکھوں سے دیکھنا۔", "أن ترى النار بعينك."), "102:7", LocalizedText("the prophets who saw", "وہ انبیاءؑ جنہوں نے دیکھا", "الأنبياء الذين رأوا")),
                Depth("حَقُّ الْيَقِين", "Haqq al-Yaqin", LocalizedText("Truth of Certainty", "حق الیقین", "حَقُّ اليَقِين"), LocalizedText("To stand within the flame itself.", "خود شعلے کے اندر کھڑا ہونا۔", "أن تقف داخل اللهب نفسه."), "56:95", LocalizedText("the family who lived it", "وہ خاندان جس نے اسے جیا", "الأسرة التي عاشته"))
            )
        ),

        // 03. Movement I - The Knowing (movement card)
        DeepDiveSection.Act(
            act = 1,
            connector = null,
            line = LocalizedText("It begins in the mind. Before certainty can be witnessed or lived, it must first be known - reasoned out and held as true, though the eyes have not yet seen.", "یہ ذہن سے شروع ہوتا ہے۔ یقین کو دیکھنے یا جینے سے پہلے، اسے پہلے جاننا ضروری ہے - عقل سے سمجھا جائے اور سچ تسلیم کیا جائے، اگرچہ آنکھوں نے ابھی اسے دیکھا نہ ہو۔", "يبدأ اليقينُ في العقل. فقبل أن يُشاهَد أو يُعاش، لا بدّ أن يُعرَف أولاً - أن يُستدلّ عليه ويُعتقَد صدقُه، وإن لم تره العينُ بعد."),
            bridge = null
        ),

        // 04. Movement I - The Question (al-Takathur 102:5)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("The Question", "سوال", "السؤال"),
            surah = 102, ayah = 5,
            arabic = "كَلَّا لَوْ تَعْلَمُونَ عِلْمَ الْيَقِينِ",
            translation = LocalizedText("No - if only you knew with the knowledge of certainty…", "ہرگز نہیں! کاش تم علمِ یقین کے ساتھ جان لیتے…", ""),
            reference = "al-Takathur · 102 : 5",
            reflection = LocalizedText("Before certainty can be lived, it must be understood. The Qur'an says it arrives in depths - three of them.", "یقین کو جینے سے پہلے، اسے سمجھنا ضروری ہے۔ قرآن کہتا ہے کہ یہ گہرائیوں کی صورت میں آتا ہے - اور یہ گہرائیاں تین ہیں۔", "قبل أن يُعاش اليقين، لا بدّ أن يُفهَم. يخبرنا القرآن أنه يأتي في أعماق - ثلاثة أعماق.")
        ),

        // 06. Movement I - Ibrahim Reasons His Way (al-An'am 6:76)
        DeepDiveSection.Verse(
            act = 1,
            tag = LocalizedText("Ibrahim Reasons His Way", "ابراہیمؑ عقل سے راہ پاتے ہیں", "إبراهيم عليه السلام يهتدي بالاستدلال"),
            surah = 6, ayah = 76,
            arabic = "فَلَمَّا جَنَّ عَلَيْهِ اللَّيْلُ رَأَىٰ كَوْكَبًا ۖ قَالَ هَٰذَا رَبِّي ۖ فَلَمَّا أَفَلَ قَالَ لَا أُحِبُّ الْآفِلِينَ",
            translation = LocalizedText("When night covered him, he saw a star. He said, “This is my Lord.” But when it set, he said, “I love not those that set.”", "پھر جب رات نے اسے ڈھانپ لیا تو انہوں نے ایک ستارہ دیکھا۔ انہوں نے کہا، “یہ میرا رب ہے۔” پھر جب وہ غروب ہو گیا تو انہوں نے کہا، “میں غروب ہونے والوں سے محبت نہیں رکھتا۔”", ""),
            reference = "al-An'am · 6 : 76",
            reflection = LocalizedText("God showed him the kingdom of the heavens and the earth, 'that he might be of those of certainty.' Star, moon, sun - each one sets, until Ibrahim turns his face to the One who never does: the smoke on the horizon that proves the fire.", "اللہ نے انہیں آسمانوں اور زمین کی بادشاہت دکھائی، ‘تاکہ وہ یقین رکھنے والوں میں سے ہو جائیں۔’ ستارہ، چاند، سورج - ہر ایک غروب ہو جاتا ہے، یہاں تک کہ حضرت ابراہیمؑ اپنا رخ اس ذات کی طرف موڑ لیتے ہیں جو کبھی غروب نہیں ہوتی: وہی افق پر اٹھتا دھواں جو آگ کے ہونے کی دلیل ہے۔", "أرى اللهُ إبراهيمَ عليه السلام ملكوتَ السماوات والأرض 'وَلِيَكُونَ مِنَ الْمُوقِنِينَ'. النجمُ والقمرُ والشمسُ - كلٌّ منها يأفل ويغيب، حتى يُولّي إبراهيمُ عليه السلام وجهَه نحو مَن لا يأفل أبداً: الدخانُ في الأفق الذي يدلّ على النار.")
        ),

        // 07. Movement II - opening card
        DeepDiveSection.Act(
            act = 2,
            connector = LocalizedText("You have known it by proof.", "آپ نے اسے دلیل کے ذریعے جان لیا ہے۔", "لقد عرفتَه بالبرهان."),
            line = LocalizedText("Now - see it. The Qur'an did not leave the prophets or those God chose to merely believe; it let them witness, with their own eyes.", "اب - اسے دیکھیے۔ قرآن نے انبیاءؑ اور اللہ کے برگزیدہ بندوں کو محض ایمان لانے پر نہیں چھوڑا؛ بلکہ انہیں اپنی آنکھوں سے مشاہدہ کرنے دیا۔", "والآن - شاهِدْه. لم يترك القرآنُ الأنبياءَ ولا مَن اصطفاهم اللهُ على مجرد الإيمان؛ بل جعلهم يشهدون بأعينهم."),
            bridge = null
        ),

        // 08. Movement II - Ibrahim Asks to See (al-Baqarah 2:260)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("Ibrahim Asks to See", "ابراہیمؑ دیکھنے کی درخواست کرتے ہیں", "إبراهيم عليه السلام يطلب أن يرى"),
            surah = 2, ayah = 260,
            arabic = "قَالَ أَوَلَمْ تُؤْمِن ۖ قَالَ بَلَىٰ وَلَٰكِن لِّيَطْمَئِنَّ قَلْبِي",
            translation = LocalizedText("“Do you not believe?” He said: “Yes - but so that my heart may be at rest.”", "“کیا تم ایمان نہیں رکھتے؟” انہوں نے کہا: “کیوں نہیں - مگر یہ چاہتا ہوں کہ میرا دل مطمئن ہو جائے۔”", ""),
            reference = "al-Baqarah · 2 : 260",
            reflection = LocalizedText("Even the Friend of God, who already believed, longed to witness. He is asking to move from the first depth to the second.", "اللہ کے خلیل بھی، جو پہلے سے ایمان رکھتے تھے، مشاہدے کے آرزو مند تھے۔ وہ پہلی گہرائی سے دوسری گہرائی کی طرف بڑھنے کی درخواست کر رہے ہیں۔", "حتى خليلُ اللهِ عليه السلام، الذي كان مؤمناً بالفعل، تاق إلى المشاهدة. إنه يطلب الانتقال من العمق الأول إلى الثاني.")
        ),

        // 09. Movement II - And Then He Sees (al-Anbiya 21:69)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("And Then He Sees", "اور پھر وہ دیکھتے ہیں", "ثم يرى"),
            surah = 21, ayah = 69,
            arabic = "قُلْنَا يَا نَارُ كُونِي بَرْدًا وَسَلَامًا عَلَىٰ إِبْرَاهِيمَ",
            translation = LocalizedText("We said: “O fire - be coolness and peace upon Ibrahim.”", "ہم نے فرمایا: “اے آگ! تو ابراہیمؑ پر ٹھنڈک اور سلامتی بن جا۔”", ""),
            reference = "al-Anbiya · 21 : 69",
            reflection = LocalizedText("He had asked to witness. Now, cast into the flames, he does - and the fire itself submits to his certainty.", "انہوں نے مشاہدے کی درخواست کی تھی۔ اب، آگ میں ڈالے جانے کے بعد، وہ مشاہدہ کر رہے ہیں - اور خود آگ ان کے یقین کے سامنے سرِ تسلیم خم کر دیتی ہے۔", "لقد طلب أن يشهد. والآن، وهو يُلقى في اللهب، يشهد - والنارُ نفسُها تستسلم ليقينه.")
        ),

        // 10. Movement II - The Mother of Musa (al-Qasas 28:7)
        DeepDiveSection.Verse(
            act = 2,
            tag = LocalizedText("The Mother of Musa", "موسیٰؑ کی والدہ", "أمّ موسى عليه السلام"),
            surah = 28, ayah = 7,
            arabic = "فَإِذَا خِفْتِ عَلَيْهِ فَأَلْقِيهِ فِي الْيَمِّ وَلَا تَخَافِي",
            translation = LocalizedText("When you fear for him, cast him into the river - and do not fear.", "جب تجھے اس کے بارے میں خوف ہو تو اسے دریا میں ڈال دے - اور خوف نہ کر۔", ""),
            reference = "al-Qasas · 28 : 7",
            reflection = LocalizedText("To lay her infant upon the water on nothing but God's word - certainty is no longer a thought. It has become an act.", "اپنے شیرخوار بچے کو محض اللہ کے کلام پر بھروسہ کرتے ہوئے پانی کے سپرد کر دینا - یہاں یقین محض ایک خیال نہیں رہتا۔ یہ ایک عمل بن جاتا ہے۔", "أن تضع رضيعَها على الماء بمجرد كلمةِ الله - لم يعد اليقينُ حينئذٍ فكرةً، بل أصبح فعلاً.")
        ),

        // 11. Movement III - opening card with bridge verse
        DeepDiveSection.Act(
            act = 3,
            connector = LocalizedText("You have seen it in the prophets.", "آپ نے اسے انبیاءؑ میں دیکھ لیا ہے۔", "لقد رأيتَه في الأنبياء."),
            line = LocalizedText("Now - live it. For most, certainty comes only at death. But one family, the Prophet's own household, was asked to stand inside the flame - while still alive.", "اب - اسے جیئے۔ اکثر لوگوں کے لیے یقین موت کے وقت ہی آتا ہے۔ لیکن ایک خاندان سے - خود رسول اللہﷺ کے اہل بیتؑ سے - یہ طلب کیا گیا کہ وہ زندہ رہتے ہوئے ہی شعلے کے اندر کھڑے ہوں۔", "والآن - عِشْه. فعند أكثر الناس، لا يأتي اليقينُ إلا عند الموت. غير أنّ أسرةً واحدة، هي أهلُ بيت النبي صلى الله عليه وآله وسلم أنفسهم، طُلب منها أن تقف داخل اللهب - وهي على قيد الحياة."),
            bridge = BridgeVerse(
                surah = 15, ayah = 99,
                arabic = "وَاعْبُدْ رَبَّكَ حَتَّىٰ يَأْتِيَكَ الْيَقِينُ",
                translation = LocalizedText("And worship your Lord until certainty comes to you.", "اور اپنے رب کی عبادت کرتے رہو یہاں تک کہ تمہارے پاس یقین آ جائے۔", ""),
                reference = "al-Hijr · 15 : 99"
            )
        ),

        // 12. Movement III - The Morning of Ashura
        DeepDiveSection.Narration(
            act = 3,
            tag = LocalizedText("The Morning of Ashura", "عاشورہ کی صبح", "صباح عاشوراء"),
            source = LocalizedText("Radiance at Karbala - narrated of Hilal ibn Nafi", "کربلا کی تابانی - ہلال بن نافع سے مروی", "إشراقٌ في كربلاء - رُوي عن هلال بن نافع"),
            body = LocalizedText("They said that as the arrows fell thicker upon them, the face of Husayn only grew more luminous and calm - so much so that those who looked on were struck by its light.", "کہا گیا ہے کہ جوں جوں تیر ان پر کثرت سے برستے گئے، امام حسینؑ کا چہرہ اور بھی روشن اور پرسکون ہوتا گیا - یہاں تک کہ دیکھنے والے اس کے نور سے مبہوت رہ گئے۔", "قالوا إنه كلما اشتدّ وقعُ السهام عليهم، ازداد وجهُ الحسين عليه السلام إشراقاً وسكينةً - حتى إنّ مَن نظر إليه بُهر بنوره."),
            reflection = LocalizedText("The closer the meeting with the Beloved, the brighter the certainty. This is no longer witnessing from the outside - it is standing within the very fire the depths spoke of.", "محبوبِ حقیقی سے ملاقات جتنی قریب آتی، یقین اتنا ہی روشن ہوتا جاتا۔ یہ اب باہر سے مشاہدہ کرنا نہیں رہا - یہ خود اسی آگ کے اندر کھڑا ہونا ہے جس کا ذکر تینوں گہرائیوں میں ہوا۔", "كلما اقترب اللقاءُ بالمحبوب، ازداد اليقينُ تألقاً. لم تعد هذه مشاهدةً من الخارج - إنها الوقوفُ داخل النار نفسِها التي تحدثت عنها الأعماقُ.")
        ),

        // 13. Movement III - The Court (Sayyida Zaynab)
        DeepDiveSection.Climax(
            act = 3,
            tag = LocalizedText("The Court", "دربار", "المجلس"),
            source = LocalizedText("Sayyida Zaynab, in the court of Ibn Ziyad - Kufa", "سیدہ زینبؑ، ابن زیاد کے دربار میں - کوفہ", "السيدة زينب عليها السلام، في مجلس ابن زياد - الكوفة"),
            arabic = "مَا رَأَيْتُ إِلَّا جَمِيلًا",
            translation = LocalizedText("I saw nothing but beauty.", "میں نے سوائے خوبصورتی کے کچھ نہیں دیکھا۔", ""),
            body = LocalizedText("After the sons. After the brothers. After the tents burned and the caravan was driven in chains - she stood in the court of the tyrant and said she had witnessed nothing but beauty.", "بیٹوں کے بعد۔ بھائیوں کے بعد۔ خیمے جلائے جانے اور قافلے کو زنجیروں میں جکڑ کر لے جانے کے بعد - وہ ظالم کے دربار میں کھڑی ہوئیں اور فرمایا کہ انہوں نے سوائے خوبصورتی کے کچھ نہیں دیکھا۔", "بعد فقد الأبناء. بعد فقد الإخوة. بعد أن أُحرقت الخيام وسِيق الركبُ مكبّلاً بالأغلال - وقفت في مجلس الطاغية، وقالت: ما رأت إلا جميلاً."),
            reflection = LocalizedText("This is Haqq al-Yaqin - the truth of certainty. Not the absence of grief, but the certainty that sees the divine beauty through it.", "یہی حق الیقین ہے - یقین کی حقیقت۔ یہ غم کی عدم موجودگی نہیں، بلکہ وہ یقین ہے جو غم کے اندر سے بھی الٰہی خوبصورتی کو دیکھ لیتا ہے۔", "هذا هو حَقُّ اليَقِين. ليس غيابَ الحزن، بل اليقينُ الذي يرى الجمالَ الإلهي من خلاله.")
        ),

        // 14. The Close - reflection prompt
        DeepDiveSection.ReflectionPrompt(
            tag = LocalizedText("Return", "واپسی", "العودة"),
            prompt = LocalizedText("Where is your yaqin?", "آپ کا یقین کہاں ہے؟", "أين يَقِينُكَ؟"),
            placeholder = LocalizedText("Faith, a decision, a loss, the unseen ahead…", "ایمان، کوئی فیصلہ، کوئی نقصان، سامنے کا اَن دیکھا…", "الإيمان، قرارٌ، فقدٌ، الغيب الذي ينتظر…"),
            subline = LocalizedText(
                "You've descended all three depths - knowing, witnessing, living. The map is yours. Before the prayer, name the certainty you long for.",
                "آپ تینوں گہرائیوں میں اتر چکے ہیں - جاننا، دیکھنا، جینا۔ نقشہ اب آپ کا ہے۔ دعا سے پہلے، اُس یقین کا نام لیں جس کی آپ کو تلاش ہے۔",
                "لقد نزلتَ الأعماق الثلاثة - أن تعلم، أن ترى، أن تعيش. الخريطة لك. قبل الدعاء، سمِّ اليقين الذي تشتاق إليه."
            ),
            nextLabel = LocalizedText("And one prayer", "اور ایک دعا", "ودعاءٌ واحد")
        ),

        // 15. The Close - a prayer for certainty
        DeepDiveSection.Dua(
            tag = LocalizedText("A Prayer for Certainty", "یقین کے لیے ایک دعا", "دعاءٌ لليقين"),
            intro = LocalizedText("After all of it - one prayer, from the family you just stood beside.", "اس سب کے بعد - ایک دعا، اسی خاندان کی طرف سے جس کے ساتھ آپ ابھی کھڑے تھے۔", "بعد كل هذا - دعاءٌ واحد، من الأسرة التي وقفتَ بجوارها للتو."),
            arabic = "وَبَلِّغْ بِإِيمَانِي أَكْمَلَ الْإِيمَانِ، وَاجْعَلْ يَقِينِي أَفْضَلَ الْيَقِينِ",
            translation = LocalizedText("“Bring my faith to the most perfect faith, and make my certainty the most excellent certainty.”", "“میرے ایمان کو کامل ترین ایمان تک پہنچا دے، اور میرے یقین کو بہترین یقین بنا دے۔”", ""),
            source = LocalizedText("Imam Ali ibn al-Husayn · al-Sahifa al-Sajjadiyya", "امام علی ابن الحسینؑ · الصحیفہ السجادیہ", "الإمام علي بن الحسين عليه السلام · الصحيفة السجادية"),
            note = LocalizedText("The son of Husayn - present at Karbala, the one who lived to carry it. He witnessed certainty's severest trial, and still asked God to deepen his own.", "امام حسینؑ کے فرزند - جو کربلا میں موجود تھے اور زندہ رہ کر اسے آگے لے جانے والے تھے۔ انہوں نے یقین کی سب سے سخت آزمائش کا مشاہدہ کیا، اور پھر بھی اللہ سے اپنے یقین کو مزید گہرا کرنے کی دعا مانگی۔", "ابنُ الحسين عليه السلام - كان حاضراً في كربلاء، وهو مَن عاش ليحمل أمانتَها. شهد أقسى اختبارٍ لليقين، ومع ذلك سأل اللهَ أن يزيد يقينَه عمقاً."),
            close = LocalizedText("The certainty is yours to keep.")
        )
    )
)
