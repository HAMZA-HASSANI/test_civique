package com.example.testcivique.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AttemptEntity::class, AttemptAnswerEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CivicDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao

    companion object {
        @Volatile
        private var instance: CivicDatabase? = null

        fun getInstance(context: Context): CivicDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CivicDatabase::class.java,
                    "civic-test.db",
                ).build().also { instance = it }
            }
    }
}
