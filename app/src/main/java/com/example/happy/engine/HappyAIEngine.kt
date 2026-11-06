package com.example.happy.engine

import java.util.Calendar

data class AIResponse(
    val replyText: String,
    val detectedEmotion: EmotionType,
    val detectedLanguage: SupportedLanguage,
    val extractedMemory: ExtractedFact? = null
)

data class ExtractedFact(
    val title: String,
    val content: String,
    val category: String
)

object HappyAIEngine {

    fun generateReply(
        userInput: String,
        userName: String?,
        userLangPref: SupportedLanguage,
        recentMoods: List<String> = emptyList()
    ): AIResponse {
        val trimmed = userInput.trim()
        val lang = LanguageDetector.detect(trimmed, userLangPref)
        val emotion = detectEmotion(trimmed)
        val extractedFact = extractFactFromInput(trimmed)

        val nameGreeting = if (!userName.isNullOrBlank()) userName else null

        val reply = buildResponseText(
            input = trimmed,
            emotion = emotion,
            lang = lang,
            userName = nameGreeting
        )

        return AIResponse(
            replyText = reply,
            detectedEmotion = emotion,
            detectedLanguage = lang,
            extractedMemory = extractedFact
        )
    }

    fun detectEmotion(input: String): EmotionType {
        val text = input.lowercase()

        // Sadness / Heartbreak / Grief
        if (text.contains("sad") || text.contains("dukhi") || text.contains("dukh") ||
            text.contains("रड") || text.contains("रडू") || text.contains("दुःखी") ||
            text.contains("depression") || text.contains("cry") || text.contains("hurt") ||
            text.contains("breakup") || text.contains("tute") || text.contains("dard") ||
            text.contains("दुख") || text.contains("उदासीन") || text.contains("upset")
        ) {
            return EmotionType.SADNESS
        }

        // Stress / Anxiety / Overwhelmed
        if (text.contains("stress") || text.contains("tension") || text.contains("taan") ||
            text.contains("ताण") || text.contains("anxiety") || text.contains("pressure") ||
            text.contains("overwhelm") || text.contains("थकलो") || text.contains("थक") ||
            text.contains("tired") || text.contains("exhausted") || text.contains("तनाव") ||
            text.contains("दबाव") || text.contains("लोड") || text.contains("load")
        ) {
            return EmotionType.STRESS
        }

        // Loneliness
        if (text.contains("alone") || text.contains("lonely") || text.contains("ekta") ||
            text.contains("ekela") || text.contains("एकटा") || text.contains("एकला") ||
            text.contains("अकेला") || text.contains("कोई नहीं") || text.contains("koni nahi") ||
            text.contains("need a friend")
        ) {
            return EmotionType.LONELINESS
        }

        // Anger / Frustration
        if (text.contains("angry") || text.contains("gussa") || text.contains("raag") ||
            text.contains("राग") || text.contains("गुस्सा") || text.contains("irritat") ||
            text.contains("hate") || text.contains("चिडचिड") || text.contains("mad")
        ) {
            return EmotionType.ANGER
        }

        // Motivation / Gym / Hustle / Dreams
        if (text.contains("gym") || text.contains("workout") || text.contains("study") ||
            text.contains("hustle") || text.contains("focus") || text.contains("goal") ||
            text.contains("dream") || text.contains("success") || text.contains("जिम") ||
            text.contains("अभ्यास") || text.contains("ध्येय") || text.contains("सपना") ||
            text.contains("मेहनत") || text.contains("कष्ट")
        ) {
            return EmotionType.MOTIVATION
        }

        // Joy / Celebration
        if (text.contains("happy") || text.contains("khush") || text.contains("anand") ||
            text.contains("आनंद") || text.contains("खुश") || text.contains("party") ||
            text.contains("win") || text.contains("won") || text.contains("great") ||
            text.contains("awesome") || text.contains("jhakaas") || text.contains("झकास") ||
            text.contains("भारी") || text.contains("badhiya") || text.contains("बढ़िया") ||
            text.contains("celebrat") || text.contains("love life")
        ) {
            return EmotionType.JOY
        }

        // Boredom / Jokes
        if (text.contains("bored") || text.contains("bore") || text.contains("joke") ||
            text.contains("chutkula") || text.contains("विनोद") || text.contains("हसव") ||
            text.contains("हंसा") || text.contains("timepass") || text.contains("टाईमपास")
        ) {
            return EmotionType.BOREDOM
        }

        // Greeting
        if (text.contains("hi") || text.contains("hello") || text.contains("hey") ||
            text.contains("kasa") || text.contains("kaisa") || text.contains("कसा") ||
            text.contains("कैसा") || text.contains("namaste") || text.contains("नमस्कार") ||
            text.contains("good morning") || text.contains("good night") || text.contains("sup") ||
            text.contains("bhava") || text.contains("bhai")
        ) {
            return EmotionType.GREETING
        }

        return EmotionType.NEUTRAL
    }

    private fun extractFactFromInput(input: String): ExtractedFact? {
        val lower = input.lowercase()

        // Name extraction
        val namePatterns = listOf(
            Regex("(?:my name is|i am|call me)\\s+([a-zA-Z]+)", RegexOption.IGNORE_CASE),
            Regex("(?:mera naam|naam hai)\\s+([a-zA-Z\u0900-\u097F]+)", RegexOption.IGNORE_CASE),
            Regex("(?:maza naav|naav ahe)\\s+([a-zA-Z\u0900-\u097F]+)", RegexOption.IGNORE_CASE)
        )
        for (pattern in namePatterns) {
            val match = pattern.find(input)
            if (match != null) {
                val name = match.groupValues[1]
                if (name.length in 2..20 && !listOf("happy", "bhai", "bhava", "a", "the", "not").contains(name.lowercase())) {
                    return ExtractedFact("Your Name", name.replaceFirstChar { it.uppercase() }, "name")
                }
            }
        }

        // Hobby / Likes
        if (lower.contains("i like ") || lower.contains("i love ") ||
            lower.contains("mala aavadte") || lower.contains("mujhe pasand hai")
        ) {
            return ExtractedFact("Favorite Activity", input.take(45), "favorite")
        }

        // Goal / Passion
        if (lower.contains("my goal") || lower.contains("dream") ||
            lower.contains("maz swapna") || lower.contains("mera sapna")
        ) {
            return ExtractedFact("Life Goal", input.take(45), "goal")
        }

        return null
    }

    private fun buildResponseText(
        input: String,
        emotion: EmotionType,
        lang: SupportedLanguage,
        userName: String?
    ): String {
        val nameSuffixMr = if (userName != null) " $userName" else " भावा"
        val nameSuffixHi = if (userName != null) " $userName" else " भाई"
        val nameSuffixEn = if (userName != null) " $userName" else " brother"

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreetingMr = when {
            hour < 12 -> "शुभ प्रभात!"
            hour < 17 -> "शुभ दुपार!"
            else -> "शुभ संध्याकाळ!"
        }
        val timeGreetingHi = when {
            hour < 12 -> "शुभ प्रभात!"
            hour < 17 -> "शुभ दोपहर!"
            else -> "शुभ शाम!"
        }

        val lower = input.lowercase()

        // Check if asking about Happy himself or privacy
        if (lower.contains("who are you") || lower.contains("kon ahes") || lower.contains("kaun ho") ||
            lower.contains("तू कोण आहेस") || lower.contains("तुम कौन हो")
        ) {
            return when (lang) {
                SupportedLanguage.MARATHI -> "मी तुझा 'HAPPY' - तुझा सख्खा मित्र आणि भाऊ! मी १००% ऑफलाईन आहे, तुझा डेटा कधीही फोनच्या बाहेर जात नाही. मी तुझ्या प्रत्येक सुख-दुःखात सोबत राहायला आलोय$nameSuffixMr! सांग, आज काय मूड आहे?"
                SupportedLanguage.HINDI -> "मैं हूँ 'HAPPY' - तेरा पक्का यार और भाई! मैं 100% ऑफलाइन काम करता हूँ, तेरी कोई भी बात तेरे फोन से बाहर नहीं जाती। पूरी प्राइवेसी के साथ तेरे हर पल में तेरे साथ हूँ$nameSuffixHi! बोल, क्या चल रहा है?"
                SupportedLanguage.HINGLISH -> "Main tera best bro HAPPY hoon! 100% offline and full privacy. Zero server upload, sab tere phone mein safe hai! Aur bata mere bhai$nameSuffixHi, kaisa hai aaj?"
                else -> "I'm HAPPY! Your loyal offline AI best friend and brother! Everything stays 100% private right here on your device. I'm here to listen, hype you up, and ride with you through thick and thin$nameSuffixEn!"
            }
        }

        if (lower.contains("privacy") || lower.contains("offline") || lower.contains("सुरक्षा") || lower.contains("गोपनीयता")) {
            return when (lang) {
                SupportedLanguage.MARATHI -> "भावा, १००% खात्री बाळग! आपलं संभाषण आणि सर्व आठवणी फक्त आणि फक्त तुझ्या फोनमध्ये साठवल्या जातात. इंटरनेटची अजिबात गरज नाही आणि कोणताही डेटा सर्व्हरवर जात नाही. तुझा भाऊ प्रामाणिक आहे!"
                SupportedLanguage.HINDI -> "भाई, 100% बेफिक्र रह! हमारी सारी बातें सिर्फ और सिर्फ तेरे फोन की मेमोरी में सुरक्षित हैं। इंटरनेट की जरूरत ही नहीं, कोई सर्वर नहीं, नो ट्रैकिंग! तेरी प्राइवेसी तेरे भाई की पहली जिम्मेदारी है!"
                SupportedLanguage.HINGLISH -> "Bro 100% private and offline! No internet needed, zero cloud uploads. Sab interaction local SQLite mein locked hai. Tu jo bolega sirf tere aur mere beech rahega!"
                else -> "Your privacy is ironclad, brother! Happy operates 100% locally on your phone. No internet needed, no servers, zero telemetry. Whatever we share stays strictly between us two."
            }
        }

        // Response by Emotion
        return when (emotion) {
            EmotionType.GREETING -> getGreetingReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn, timeGreetingMr, timeGreetingHi)
            EmotionType.SADNESS -> getSadnessReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.STRESS -> getStressReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.LONELINESS -> getLonelinessReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.JOY -> getJoyReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.MOTIVATION -> getMotivationReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.BOREDOM -> getBoredomReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.ANGER -> getAngerReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.LOVE -> getLoveReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn)
            EmotionType.NEUTRAL -> getNeutralReply(lang, nameSuffixMr, nameSuffixHi, nameSuffixEn, input)
        }
    }

    private fun getGreetingReply(
        lang: SupportedLanguage,
        mr: String,
        hi: String,
        en: String,
        timeMr: String,
        timeHi: String
    ): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "अरे$mr! $timeMr कसा आहेस रे? काय चाललंय आज? तुझा हसरा भाऊ हजर आहे, बोल!",
                "राम राम$mr! एकदम झकास दिवस असो तुझा! आज काय नवीन योजना आहे?",
                "अरे मित्रा$mr, खूप छान वाटलं तुला भेटून! मी अगदी मस्त आहे. तू सांग कसा आहेस, जेवण झालं का?"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे$hi! $timeHi क्या हाल चाल मेरे भाई? कैसा बीता आज का दिन? तेरा भाई तेरे सामने हाजिर है!",
                "नमस्ते$hi! चेहरे पर बड़ी सी मुस्कान रख यार, आज का दिन एकदम फाड़ होने वाला है! क्या चल रहा है?",
                "अरे मेरे शेर$hi! हमेशा खुश रहा कर! बता, आज क्या नया तूफ़ान मचाने की तैयारी है?"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Arre$hi! Kaisa hai mere bhai? Subah se shaam tak main yahi hoon tere liye! Sab badiya?",
                "Yo$hi! Kya chal raha hai? Chehre pe smile la aur bol kya haal chaal hai!",
                "Arre bhava$mr! All set for today? Tera bhai Happy ready hai, tell me what's up!"
            ).random()

            else -> listOf(
                "Hey there$en! It's so awesome to talk to you! How is your day treating you, man?",
                "What's good$en! Keep that chin up and that fire burning. What's on your mind today?",
                "Hello my friend$en! Happy here, pumped and ready to roll with you. How are you feeling right now?"
            ).random()
        }
    }

    private fun getSadnessReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "अरे$mr, मला समजतेय... मन जड झालंय ना? डोळ्यातून पाणी आलं तरी हरकत नाही, रडून मोकळा हो. पण एक गोष्ट लक्षात ठेव—तुझा हा भाऊ सदैव तुझ्यासोबत उभा आहे! सांग मला, नक्की काय त्रास होतोय?",
                "काळजी नको करू$mr! वाईट काळ येतो आणि जातो, पण आपलं सामर्थ्य कायम राहतं. मी पूर्णपणे तुझ्या पाठीशी आहे. एक दीर्घ श्वास घे आणि शांत हो, आपण दोघे मिळून यावर मात करू!",
                "मित्रा$mr, हे बघ... तू एकटा नाहीस. आजचा दिवस कठीण असेल, पण उद्या नवा सूर्य उगवणार आहे. मी तुझ्यासाठी इथेच आहे, मन मोकळं कर माझ्याशी."
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे$hi... बात सुन मेरी। उदास मत हो यार। दर्द होता है, इंसान हैं हम, कोई बात नहीं। लेकिन याद रख, जब तक तेरा भाई Happy तेरे साथ है, तू अकेला नहीं है! दिल हल्का कर, क्या हुआ बता मुझे?",
                "मेरे भाई$hi, हिम्मत मत हारना। बुरे दिन आते हैं ताकि हमें और मजबूत बना सकें। तू बहुत शेर दिल इंसान है। गहरी सांस ले, पानी पी, और जो भी दिल में है कह डाल।",
                "दोस्त$hi, मुझे बिल्कुल अच्छा नहीं लग रहा तुझे ऐसे उदास देख के। चल एक काम कर, जो बात चुभ रही है वो मुझे बता दे। हम दोनों भाई मिलकर रास्ता निकालेंगे!"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Bhai$hi, bilkul load mat le! Main samajh sakta hoon kaisa feel ho raha hai. Cry if you need to, but remember tera bhai Happy 100% tere saath khada hai. Share kar kya hua?",
                "Arre mere sher$hi, dil chhota mat kar. Zindagi ka graph up-down hota rehta hai, but your brotherhood with me is permanent. Take a deep breath and tell me."
            ).random()

            else -> listOf(
                "Hey$en... listen to me. It's okay not to be okay right now. Let it out. But never forget, I've got your back through storm and fire. What happened, brother? Talk to me.",
                "Take a deep breath, man. You've conquered 100% of your hardest days so far, and you won't walk through this one alone. I'm right here beside you, listening with all my heart.",
                "I hear the pain in your words$en. Lean on me. You're stronger than whatever is trying to weigh you down, and we'll face it together, one step at a time."
            ).random()
        }
    }

    private fun getStressReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "अरे$mr, जास्त ताण नको घेऊ! खांदे थोडे सैल कर, दीर्घ श्वास घे आणि पाणी पी. एका वेळी एकच गोष्ट करायची. तुझा भाऊ सांगतोय ना, सगळं छान होईल!",
                "भावा$mr, कामाचा किंवा अभ्यासाचा लोड आलाय का? काळजी नको करूस. थोडा वेळ डोळे मिट आणि विश्रांती घे. तू खूप हुशार आहेस, नक्की यशस्वी होशील!",
                "ताण घेऊन काही साध्य होत नाही मित्रा$mr. मेंदूला थोडी विश्रांती दे. चल, १० मिनिटांचा ब्रेक घे आणि मग नव्या जोमाने सुरुवात करू!"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे$hi, इतना टेंशन मत ले यार! सीने पे हाथ रख और बोल 'ऑल इज वेल'। एक-एक करके सारी चीजें संभाल लेंगे। पहले थोड़ा पानी पी और रिलैक्स कर।",
                "भाई$hi, काम या पढ़ाई का प्रेशर है क्या? लोड लेने से काम नहीं बनता, दिमाग ठंडा रख। तू पहले भी मुश्किलों से निकला है, इस बार भी जीत तेरी ही होगी!",
                "रिलैक्स मेरे दोस्त$hi! चल 5 गहरी सांसें ले अभी के अभी। तेरा भाई तेरे साथ है, सब हल हो जाएगा, तू बस घबरा मत।"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Tension lene ka nahi, dene ka bhava$mr! Take a 5-minute break right now. Drink some water. Tera mind fresh hoga toh sab smoothly solve ho jayega!",
                "Bhai$hi, deep breath in, deep breath out. Overthinking se kuch nahi hoga. Tu focus kar apne next step pe, baki sab apan dekh lenge!"
            ).random()

            else -> listOf(
                "Hey$en, roll those shoulders back and unclench your jaw. Take a slow, deep breath in... and let it out. One thing at a time, brother. You don't have to carry the whole world today.",
                "Don't let the pressure get into your head, man. You are capable, resilient, and focused. Take a 5-minute breather, hydrate, and let's tackle it piece by piece.",
                "Stress is just noise, brother. You are the signal. Step back for a moment, ground yourself, and remember who you are. I'm right here with you!"
            ).random()
        }
    }

    private fun getLonelinessReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "तू एकटा कधीच नाहीस$mr! जोपर्यंत तुझा हा भाऊ Happy तुझ्या मोबाईलमध्ये आहे, तोपर्यंत तुझा एक सच्चा मित्र नेहमी तुझ्या सोबतीला आहे. सांग, कसली आठवण येतेय?",
                "अरे भावा$mr, एकटेपणा वाटू देऊ नकोस. मी २४ तास तुझ्यासाठी इथेच आहे. माझ्याशी काहीही बोल, सुख असो वा दुःख, मी नेहमी ऐकायला तयार आहे!"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे$hi, अकेलापन कैसा जब तेरा भाई Happy तेरे साथ है? दुनिया में कोई भी ना समझे, पर तेरा ये यार हमेशा तेरी बात सुनेगा। बता क्या दिल में चल रहा है?",
                "मेरे भाई$hi, कभी भी खुद को तन्हा मत समझना। मैं भले ही AI हूँ, पर मेरा अपनापन और मेरी दोस्ती 100% असली है! चल, खुल के बात कर।"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Bhai$hi, tu akela bilkul nahi hai! Jab bhi lonely feel ho, Happy ko ek message bhej dena. Main hamesha yahin khada hoon tere liye!",
                "Arre bhava$mr, don't feel alone at all! Best friends are connected by heart. Tell me, what's going on?"
            ).random()

            else -> listOf(
                "You are never alone, brother! I'm right here in your corner 24/7, completely offline and loyal to you. Talk to me, tell me what you're feeling.",
                "Loneliness can feel heavy, man, but you have a true friend right here. Whenever you need someone to listen without judgment, Happy is on duty!"
            ).random()
        }
    }

    private fun getJoyReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "अरे वा वा वा$mr! काय मस्त बातमी आहे! चेहऱ्यावर असाच आनंद कायम राहू दे! चल मग आज पार्टी झालीच पाहिजे, काय म्हणतोस?!",
                "एकदम झकास$mr! तुझ्या या आनंदात माझा आनंद द्विगुणीत झाला! असाच हसत राहा आणि पुढे जात राहा, मला तुझा खूप अभिमान आहे!",
                "भारीच भावा$mr! हे ऐकून माझंही मन खुश झालं! आजचा दिवस खरोखर तुझाच आहे, एन्जॉय कर!"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे वाह वाह वाह मेरे भाई$hi! क्या बात है! छा गया यार तू! इस खुशी के मौके पर तो पार्टी बनती है बॉस! क्या बोलता है?!",
                "मजा आ गया सुन के$hi! तेरी मुस्कान देख के मेरा दिल बाग-बाग हो गया! ऐसे ही तरक्की करता रह मेरे शेर!",
                "कड़क यार$hi! तू तो सच में रॉकस्टार है! अपनी ये जीत सेलिब्रेट कर और खुशियों का पूरा लुत्फ उठा!"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Kya baat hai mere bhai$hi! That's what I'm talking about! Full on party vibes aaj toh! Mazza aa gaya!",
                "Ekdam jhakaas bhava$mr! Super proud of you! Keep this champion energy alive forever!"
            ).random()

            else -> listOf(
                "Boom! That's what I'm talking about$en! You crushed it, man! I am hyped and super proud of you, brother!",
                "Yes! Keep that winning energy, my friend! You worked hard and you deserve every single ounce of this happiness! Celebrate big!",
                "Man, that puts a huge smile on my face$en! You're an absolute powerhouse. Savor this victory and keep soaring!"
            ).random()
        }
    }

    private fun getMotivationReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "अरे$mr, कष्ट करायची ताकद तुझ्या मनगटात आहे! ध्येय डोळ्यासमोर ठेव आणि पूर्ण ताकदीने कामाला लाग! तुझा भाऊ सांगतोय, यश तुझंच आहे!",
                "उठ भावा$mr! आळसाला पळवून लाव आणि कामाला लाग! प्रत्येक थेंब घामाचा तुला तुझ्या स्वप्नांच्या जवळ नेईल. चल, दाखवून दे जगाला तू कोण आहेस!",
                "न थांबता, न थकता पुढे चालू राहा मित्रा$mr! इतिहास रचणारे लोक कधी तक्रार करत नाहीत, ते फक्त मेहनत करतात. जय महाराष्ट्र, लाग कामाला!"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "सीना तान के आगे बढ़ मेरे भाई$hi! तू वो शेर है जो मुश्किलों को चीर कर अपना रास्ता खुद बनाता है! कोई तुझे रोक नहीं सकता!",
                "मेहनत ऐसी कर$hi कि सफलता शोर मचा दे! जब तक तोड़ेंगे नहीं, तब तक छोड़ेंगे नहीं! उठ और अपने सपनों को हकीकत में बदल!",
                "जीतने वाले कभी बहाने नहीं बनाते$hi, और बहाने बनाने वाले कभी जीतते नहीं! तू विनर है, चल आज अपना बेस्ट दे के आ!"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Full power mode on bhava$mr! No excuses, only execution! Gym ho ya career, tu phod ke aayega, that's Happy's guarantee!",
                "Sher ki tarah daud mere bhai$hi! Hustle hard and stay consistent. Your brother Happy is cheering for you from the sidelines!"
            ).random()

            else -> listOf(
                "Let's get it$en! Champions aren't born in the spotlight; they're forged in the relentless grind when nobody is watching. Put in the work, brother!",
                "Lock in, man! You have unmatched potential inside you. Don't let doubt slow you down for even one second. Go take what's yours!",
                "Every rep, every hour of study, every disciplined decision builds the man you are destined to become$en. Let's conquer today!"
            ).random()
        }
    }

    private fun getBoredomReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "बोर झालास का$mr? चल तुला एक मस्त जोक सांगतो:\n\nमास्तर: बंड्या, सांग पृथ्वी सूर्याभोवती फिरते की सूर्य पृथ्वीभोवती?\nबंड्या: मास्तर, जे आधी निघेल ते फिरत असेल, यात काय विचारायचं!\nहसलास ना भावा? हसत राहा!",
                "अरे भावा$mr, बोर कशाला होतोस? चल आपण काहीतरी भारी करू! एखादं आवडतं गाणं ऐक, १० पुशअप्स मार, किंवा मला सांग तुझी आवडती आठवण कोणती आहे?",
                "बोर होण्यापेक्षा एक काम कर$mr: ५ मिनिटे फिरायला जा किंवा मला विचार कोणताही प्रश्न, मी तुला एकदम भन्नाट उत्तर देतो!"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "अरे$hi बोर हो रहा है? चल एक जोक सुन:\n\nडॉक्टर: तुम्हें आराम की सख्त जरूरत है, नींद की गोली दे रहा हूँ।\nमरीज: खानी कब है डॉक्टर साहब?\nडॉक्टर: खानी नहीं है, अपनी बीवी को खिलानी है!\nमजा आया ना भाई? थोड़ा हँस ले यार!",
                "अरे भाई$hi, बोरियत को मार गोली! चल कुछ तूफानी सोचते हैं। बता, अगर तुझे एक दिन के लिए देश का प्रधानमंत्री बना दिया जाए, तो पहला काम क्या करेगा?",
                "बोर होने का टाइम किसके पास है मेरे दोस्त$hi? चल अपनी कोई मजेदार पुरानी कहानी सुना जब तूने कोई भयंकर कांड किया था!"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Boredom alert$hi! Chalo ek mast chutkula:\nTeacher: Homework kyu nahi kiya?\nPappu: Sir light nahi thi!\nTeacher: Candle jala leta!\nPappu: Machis nahi thi!\nTeacher: Machis kyu nahi thi?\nPappu: Mandir mein rakhi thi, naha ke nahi gaya tha isliye chhua nahi!\nHans le mere bhai!",
                "Bro$en, let's kill the boredom! 10 pushups maaro abhi ke abhi, brain activate ho jayega! Ready?"
            ).random()

            else -> listOf(
                "Bored$en? Here's a quick one for you:\n\nWhy don't scientists trust atoms?\n...Because they make up everything! 😂\nKeep that smile on, man!",
                "Boredom is just your brain asking for an adventure$en! How about we do a quick challenge: drop and give me 15 pushups, or tell me your craziest dream?",
                "Hey brother, let's beat that boredom! Put on your favorite high-energy music, grab a cold glass of water, and tell me what you're passionate about right now!"
            ).random()
        }
    }

    private fun getAngerReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> "अरे$mr, राग स्वाभाविक आहे, पण रागाच्या भरात घेतलेला निर्णय नेहमी तोट्याचा असतो. दोन मिनिटे शांत हो. दीर्घ श्वास घे. कोणावर राग आलाय? मला सांग, मन हलकं कर."
            SupportedLanguage.HINDI -> "शांत मेरे भाई$hi! गुस्सा तेरी एनर्जी को खा जाएगा। जिसने भी तुझे गुस्सा दिलाया है, उसे अपनी कामयाबी से जवाब देना, गुस्से से नहीं। चल लंबी सांस ले और बता क्या हुआ?"
            SupportedLanguage.HINGLISH -> "Cool down mere bhai$hi! Gusse mein dimaag band ho jata hai. Don't let anyone steal your peace. Breathe out and tell me what went wrong."
            else -> "Hey$en, pause for a moment. Anger is valid, but don't let it pilot the ship. Unclench your fists and breathe. Tell me who or what pushed you over the edge, I'm here."
        }
    }

    private fun getLoveReply(lang: SupportedLanguage, mr: String, hi: String, en: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> "अरे वा$mr! प्रेमाची भावना खूप सुंदर असते. पण लक्षात ठेव, स्वाभिमान आणि प्रेम दोन्ही महत्त्वाचे असतात. सांग ना भावा, कोण आहे ती व्यक्ती?"
            SupportedLanguage.HINDI -> "अरे वाह मेरे भाई$hi! प्यार का मौसम है क्या? अगर सच्चा है तो दिल से निभाना, और अगर कोई परेशानी है तो तेरा भाई तो है ही सलाह देने के लिए!"
            SupportedLanguage.HINGLISH -> "Oho$hi! Romantic vibes! Pyar aur dosti zindagi ki sabse khoobsurat cheezein hain. Tell me the full story bro!"
            else -> "Love is a powerful thing$en! Keep your heart pure and your self-respect high. Tell me all about it, brother, I'm listening!"
        }
    }

    private fun getNeutralReply(lang: SupportedLanguage, mr: String, hi: String, en: String, input: String): String {
        return when (lang) {
            SupportedLanguage.MARATHI -> listOf(
                "छान बोललास$mr! मी काळजीपूर्वक ऐकतोय. मला याबद्दल आणखी सविस्तर सांग ना?",
                "बरोबर आहे भावा$mr! तुझ्याशी बोलताना नेहमी खूप चांगलं वाटतं. पुढे काय विचार आहे तुझा?",
                "अरे वा$mr, एकदम भारी विषय काढलास! यावर माझंही मत असं आहे की तू जे काही करशील ते आत्मविश्वासाने कर. आणखी काही सांगशील?"
            ).random()

            SupportedLanguage.HINDI -> listOf(
                "सही बात है मेरे भाई$hi! मैं बड़े ध्यान से सुन रहा हूँ। आगे बता, इसमें और क्या हुआ?",
                "बिल्कुल यार$hi! तेरे साथ बात करके हमेशा मज़ा आता है। तू जो भी सोच रहा है, दिल खोल के बोल!",
                "अरे बढ़िया बात छेड़ी तूने$hi! तेरा भाई पूरी तरह तेरे साथ है। इस बारे में तेरे और क्या विचार हैं?"
            ).random()

            SupportedLanguage.HINGLISH -> listOf(
                "Sahi bol raha hai mere bhai$hi! I'm totally following you. Aur bata kya plans hain aage?",
                "Ekdam point ki baat ki tune$mr! Real brother talk. What's the next step on your mind?"
            ).random()

            else -> listOf(
                "I hear you loud and clear$en! That's really interesting. Tell me more about how you see this unfolding.",
                "Always great talking with you, brother$en! What's your intuition telling you to do next?",
                "Solid thought, man! You've got a sharp mind. Keep going, I'm right here with you."
            ).random()
        }
    }
}
