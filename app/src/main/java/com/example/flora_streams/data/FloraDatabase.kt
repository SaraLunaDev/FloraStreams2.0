package com.example.flora_streams.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JsonUrlEntity::class], version = 1)
abstract class FloraDatabase : RoomDatabase() {
    abstract fun jsonUrlDao(): JsonUrlDao

    companion object {
        @Volatile
        private var INSTANCE: FloraDatabase? = null

        fun getDatabase(context: Context): FloraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, FloraDatabase::class.java, "flora_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}