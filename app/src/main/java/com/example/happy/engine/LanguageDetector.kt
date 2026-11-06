package com.example.happy.engine

object LanguageDetector {

    private val marathiKeywords = setOf(
        "कसा", "कशी", "भावा", "मित्रा", "भाऊ", "काळजी", "नको", "आहेस", "काय", "चाललंय",
        "जेवलास", "झकास", "भारी", "विषय", "ताण", "मला", "तुला", "नाही", "होय", "सांग",
        "नक्की", "एकदम", "दादा", "मित्रा", "दोस्त", "आपण", "कधी", "कुठे", "केलं", "आज",
        "उद्या", "आनंद", "दुःख", "हस", "बोल", "बोलतोय", "तुकडे", "पोरगा", "गावाकडं"
    )

    private val marathiLatinKeywords = setOf(
        "kasa", "kashi", "bhava", "mitra", "bhau", "kalji", "nako", "ahes", "kay", "chalalay",
        "jevalas", "jhakaas", "bhari", "vishey", "taan", "mala", "tula", "nahi", "hoy", "sang",
        "nakki", "ekdam", "dada", "boltoy", "tujhyashi", "apan", "kadhi", "kuthe", "udya", "aaj"
    )

    private val hindiKeywords = setOf(
        "कैसा", "कैसी", "भाई", "दोस्त", "यार", "चिंता", "मत", "हो", "क्या", "हाल",
        "खाना", "बढ़िया", "बात", "तनाव", "मुझे", "तुझे", "नहीं", "हाँ", "बता", "बिल्कुल",
        "चल", "आज", "कल", "खुश", "दुःखी", "रो", "हंस", "बोल", "भाईजान", "जिंदगी", "प्यार"
    )

    private val hindiLatinKeywords = setOf(
        "kaisa", "kaisi", "bhai", "dost", "yaar", "chinta", "mat", "kya", "haal",
        "badhiya", "tension", "mujhe", "tujhe", "bata", "bilkul", "chal", "aaj", "kal",
        "khush", "dukhi", "bhaijaan", "zindagi", "pyaar", "karega", "raha", "rahi"
    )

    fun detect(text: String, userPreference: SupportedLanguage): SupportedLanguage {
        if (userPreference != SupportedLanguage.AUTO) {
            return userPreference
        }

        val lower = text.lowercase()
        val tokens = lower.split(Regex("[\\s,?.!]+"))

        var marathiScore = 0
        var hindiScore = 0
        var englishScore = 0

        // Check devanagari characters
        val containsDevanagari = text.any { it in '\u0900'..'\u097F' }

        for (token in tokens) {
            if (marathiKeywords.contains(token) || marathiLatinKeywords.contains(token)) {
                marathiScore += 2
            }
            if (hindiKeywords.contains(token) || hindiLatinKeywords.contains(token)) {
                hindiScore += 2
            }
            if (token in listOf("the", "you", "are", "bro", "brother", "friend", "how", "what", "feel", "sad", "happy", "tired", "work")) {
                englishScore += 1
            }
        }

        return when {
            marathiScore > hindiScore && marathiScore > 0 -> SupportedLanguage.MARATHI
            hindiScore > marathiScore && hindiScore > 0 -> SupportedLanguage.HINDI
            containsDevanagari && marathiScore >= hindiScore -> SupportedLanguage.MARATHI
            containsDevanagari -> SupportedLanguage.HINDI
            englishScore > 0 && marathiScore == 0 && hindiScore == 0 -> SupportedLanguage.ENGLISH
            (marathiScore > 0 || hindiScore > 0) && englishScore > 0 -> SupportedLanguage.HINGLISH
            else -> SupportedLanguage.ENGLISH
        }
    }
}
