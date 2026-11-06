package com.example.happy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Sender {
    USER,
    HAPPY
}

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "auto", // "mr", "hi", "en", "hinglish"
    val detectedEmotion: String = "NEUTRAL",
    val isFavorite: Boolean = false
)
