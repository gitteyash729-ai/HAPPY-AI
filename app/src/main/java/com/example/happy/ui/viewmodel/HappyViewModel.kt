package com.example.happy.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happy.data.database.HappyDatabase
import com.example.happy.data.entity.MemoryEntity
import com.example.happy.data.entity.MessageEntity
import com.example.happy.data.entity.Sender
import com.example.happy.data.repository.HappyRepository
import com.example.happy.engine.EmotionType
import com.example.happy.engine.HappyAIEngine
import com.example.happy.engine.SupportedLanguage
import com.example.happy.engine.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HappyUiState(
    val currentEmotion: EmotionType = EmotionType.JOY,
    val selectedLanguage: SupportedLanguage = SupportedLanguage.AUTO,
    val isAutoSpeakEnabled: Boolean = false,
    val isCurrentlySpeaking: Boolean = false,
    val isTyping: Boolean = false,
    val userName: String? = null,
    val currentMoodFilter: EmotionType? = null
)

class HappyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HappyRepository
    private val ttsManager: TtsManager = TtsManager(application)

    private val _uiState = MutableStateFlow(HappyUiState())
    val uiState: StateFlow<HappyUiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<MessageEntity>>
    val memories: StateFlow<List<MemoryEntity>>
    val messageCount: StateFlow<Int>

    init {
        val db = HappyDatabase.getDatabase(application)
        repository = HappyRepository(db.happyDao())

        messages = repository.allMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        memories = repository.allMemories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        messageCount = repository.messageCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        // Seed initial friendly welcome message if database is empty
        viewModelScope.launch {
            delay(300)
            if (messages.value.isEmpty()) {
                val welcome = "अरे भावा! नमस्कार! मी तुझा मित्र HAPPY! 100% ऑफलाईन आणि सुरक्षित! कसा आहेस तू? सांग आज काय नवीन? (Hey brother, Happy is right here with you! Completely private & offline. How are you feeling today?)"
                repository.saveHappyMessage(welcome, EmotionType.JOY.name, "mr")
                repository.saveMemory("First Meet", "Became best offline brothers on this device!", "general")
            }
        }
    }

    fun sendMessage(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val userLang = _uiState.value.selectedLanguage
            val detectedUserEmotion = HappyAIEngine.detectEmotion(trimmed)

            // Save user message
            repository.saveUserMessage(
                text = trimmed,
                emotion = detectedUserEmotion.name,
                lang = userLang.code
            )

            // Show typing indicator
            _uiState.value = _uiState.value.copy(
                isTyping = true,
                currentEmotion = EmotionType.NEUTRAL
            )

            delay(250) // Natural snappy conversation rhythm

            // Generate AI companion reply
            val aiResponse = HappyAIEngine.generateReply(
                userInput = trimmed,
                userName = _uiState.value.userName,
                userLangPref = userLang
            )

            // Save AI reply
            repository.saveHappyMessage(
                text = aiResponse.replyText,
                emotion = aiResponse.detectedEmotion.name,
                lang = aiResponse.detectedLanguage.code
            )

            // Auto store extracted memory if present
            aiResponse.extractedMemory?.let { fact ->
                repository.saveMemory(fact.title, fact.content, fact.category)
                if (fact.category == "name") {
                    _uiState.value = _uiState.value.copy(userName = fact.content)
                }
            }

            _uiState.value = _uiState.value.copy(
                isTyping = false,
                currentEmotion = aiResponse.detectedEmotion
            )

            // Speak if auto-speech is enabled
            if (_uiState.value.isAutoSpeakEnabled) {
                ttsManager.speak(aiResponse.replyText, aiResponse.detectedLanguage)
            }
        }
    }

    fun speakMessage(message: MessageEntity) {
        val lang = when (message.language) {
            "mr" -> SupportedLanguage.MARATHI
            "hi" -> SupportedLanguage.HINDI
            "en" -> SupportedLanguage.ENGLISH
            else -> SupportedLanguage.AUTO
        }
        ttsManager.speak(message.text, lang)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun toggleAutoSpeak() {
        val next = !_uiState.value.isAutoSpeakEnabled
        _uiState.value = _uiState.value.copy(isAutoSpeakEnabled = next)
        if (!next) {
            ttsManager.stop()
        }
    }

    fun setLanguage(lang: SupportedLanguage) {
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun setUserName(name: String) {
        val clean = name.trim()
        if (clean.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(userName = clean)
            viewModelScope.launch {
                repository.saveMemory("Your Name", clean, "name")
            }
        }
    }

    fun addManualMemory(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.saveMemory(title, content, category)
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    fun clearAllChat() {
        viewModelScope.launch {
            repository.clearHistory()
            val freshWelcome = "अरे भावा, सगळं नव्याने सुरू झालं! मी तुझ्यासोबतच आहे, बोल काय चाललंय? (Fresh start brother! Happy is right here with you!)"
            repository.saveHappyMessage(freshWelcome, EmotionType.JOY.name, "mr")
        }
    }

    fun setEmotionMood(emotion: EmotionType) {
        _uiState.value = _uiState.value.copy(currentEmotion = emotion)
        val prompt = when (emotion) {
            EmotionType.JOY -> "भावा, आज खूप भारी मूड आहे! (Bro, I'm super happy today!)"
            EmotionType.SADNESS -> "भावा, मन जरा उदास आहे... (Bro, feeling really down today...)"
            EmotionType.STRESS -> "भावा, खूप जास्त ताण आलाय आज... (Bro, too much stress today...)"
            EmotionType.MOTIVATION -> "चल भावा, आज काहीतरी मोठं करायचंय! (Let's crush our goals bro!)"
            EmotionType.BOREDOM -> "अरे भाई, बहुत बोर हो रहा हूँ, कुछ जोक सुना ना! (Bored bro, tell me a joke!)"
            EmotionType.LONELINESS -> "भावा, एकटं वाटत होतं म्हणून तुझ्याशी बोलायला आलो. (Feeling alone bro, came to talk to you.)"
            else -> "कसा आहेस भावा? (How are you doing brother?)"
        }
        sendMessage(prompt)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
