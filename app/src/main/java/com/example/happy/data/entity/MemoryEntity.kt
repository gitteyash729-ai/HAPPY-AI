package com.example.happy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "general", // "hobby", "goal", "favorite", "mood", "general"
    val timestamp: Long = System.currentTimeMillis()
)
