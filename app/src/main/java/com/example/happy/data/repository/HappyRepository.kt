package com.example.happy.data.repository

import com.example.happy.data.dao.HappyDao
import com.example.happy.data.entity.MemoryEntity
import com.example.happy.data.entity.MessageEntity
import com.example.happy.data.entity.Sender
import kotlinx.coroutines.flow.Flow

class HappyRepository(private val dao: HappyDao) {

    val allMessages: Flow<List<MessageEntity>> = dao.getAllMessages()
    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()
    val messageCount: Flow<Int> = dao.getMessageCount()

    suspend fun saveUserMessage(text: String, emotion: String, lang: String): Long {
        return dao.insertMessage(
            MessageEntity(
                text = text,
                sender = Sender.USER,
                detectedEmotion = emotion,
                language = lang
            )
        )
    }

    suspend fun saveHappyMessage(text: String, emotion: String, lang: String): Long {
        return dao.insertMessage(
            MessageEntity(
                text = text,
                sender = Sender.HAPPY,
                detectedEmotion = emotion,
                language = lang
            )
        )
    }

    suspend fun saveMemory(title: String, content: String, category: String = "general") {
        dao.insertMemory(
            MemoryEntity(
                title = title,
                content = content,
                category = category
            )
        )
    }

    suspend fun deleteMemory(memory: MemoryEntity) {
        dao.deleteMemory(memory)
    }

    suspend fun clearHistory() {
        dao.clearAllMessages()
    }

    suspend fun clearMemories() {
        dao.clearAllMemories()
    }
}
