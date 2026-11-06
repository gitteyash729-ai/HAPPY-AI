package com.example.happy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.happy.data.dao.HappyDao
import com.example.happy.data.entity.MemoryEntity
import com.example.happy.data.entity.MessageEntity

@Database(
    entities = [MessageEntity::class, MemoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HappyDatabase : RoomDatabase() {
    abstract fun happyDao(): HappyDao

    companion object {
        @Volatile
        private var INSTANCE: HappyDatabase? = null

        fun getDatabase(context: Context): HappyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HappyDatabase::class.java,
                    "happy_offline_private.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
