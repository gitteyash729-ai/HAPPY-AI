package com.example.happy.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(0.95f)
            tts?.setPitch(1.0f)
            tts?.language = Locale("hi", "IN")
        } else {
            Log.e("TtsManager", "TextToSpeech init failed: $status")
        }
    }

    fun speak(text: String, language: SupportedLanguage) {
        if (!isInitialized || tts == null) return

        // Remove emojis and symbols for cleaner pronunciation
        val cleanText = text.replace(Regex("[\\p{So}\\p{Cn}]"), "").trim()
        if (cleanText.isEmpty()) return

        try {
            val targetLocale = when (language) {
                SupportedLanguage.MARATHI -> Locale("mr", "IN")
                SupportedLanguage.HINDI -> Locale("hi", "IN")
                SupportedLanguage.ENGLISH -> Locale("en", "IN")
                SupportedLanguage.HINGLISH -> Locale("hi", "IN")
                SupportedLanguage.AUTO -> {
                    if (text.any { it in '\u0900'..'\u097F' }) Locale("hi", "IN") else Locale("en", "IN")
                }
            }

            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English or default
                tts?.language = Locale.ENGLISH
            }

            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "happy_speech_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TtsManager", "Speech error", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "Stop error", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e("TtsManager", "Shutdown error", e)
        }
    }
}
