package com.example

import com.example.happy.engine.EmotionType
import com.example.happy.engine.HappyAIEngine
import com.example.happy.engine.LanguageDetector
import com.example.happy.engine.SupportedLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLanguageDetection() {
        val marathiInput = "कसा आहेस भावा?"
        assertEquals(SupportedLanguage.MARATHI, LanguageDetector.detect(marathiInput, SupportedLanguage.AUTO))

        val hindiInput = "कैसा है भाई? क्या हाल चाल?"
        assertEquals(SupportedLanguage.HINDI, LanguageDetector.detect(hindiInput, SupportedLanguage.AUTO))

        val englishInput = "Hey brother, how are you doing today?"
        assertEquals(SupportedLanguage.ENGLISH, LanguageDetector.detect(englishInput, SupportedLanguage.AUTO))
    }

    @Test
    fun testEmotionDetection() {
        val sadMsg = "I am feeling very sad and heartbroken"
        assertEquals(EmotionType.SADNESS, HappyAIEngine.detectEmotion(sadMsg))

        val stressMsg = "खूप ताण आलाय कामाचा"
        assertEquals(EmotionType.STRESS, HappyAIEngine.detectEmotion(stressMsg))

        val gymMsg = "Time for gym workout hustle"
        assertEquals(EmotionType.MOTIVATION, HappyAIEngine.detectEmotion(gymMsg))

        val jokeMsg = "Bhai bore ho raha hu koi joke suna"
        assertEquals(EmotionType.BOREDOM, HappyAIEngine.detectEmotion(jokeMsg))
    }

    @Test
    fun testHappyResponseGeneration() {
        val response = HappyAIEngine.generateReply(
            userInput = "My name is Vaibhav and I need a friend",
            userName = null,
            userLangPref = SupportedLanguage.ENGLISH
        )
        assertNotNull(response.replyText)
        assertTrue(response.replyText.isNotEmpty())
        assertEquals(EmotionType.LONELINESS, response.detectedEmotion)
        assertEquals("Vaibhav", response.extractedMemory?.content)
    }

    @Test
    fun testMarathiBrotherlyResponse() {
        val response = HappyAIEngine.generateReply(
            userInput = "कसा आहेस भावा?",
            userName = "Vaibhav",
            userLangPref = SupportedLanguage.MARATHI
        )
        assertNotNull(response.replyText)
        assertTrue(response.replyText.contains("Vaibhav") || response.replyText.contains("भावा") || response.replyText.contains("मित्रा"))
    }
}
