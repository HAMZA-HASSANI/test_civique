package com.example.testcivique.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AttemptEntity::class, AttemptAnswerEntity::class, LearningProgressEntity::class],
    version = 2,
    exportSchema = true,
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
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }

        internal val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS learning_progress (
                        target TEXT NOT NULL,
                        theme TEXT NOT NULL,
                        chapterIndex INTEGER NOT NULL,
                        completedAt INTEGER NOT NULL,
                        PRIMARY KEY(target, theme, chapterIndex)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
