package com.example.testcivique.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CivicDatabaseMigrationTest {
    @Test
    fun migrate1To2PreservesAttemptsAndAddsLearningProgress() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(TEST_DATABASE)
        val version1 = helper(
            version = 1,
            onCreate = { database ->
                database.execSQL(
                    """
                    CREATE TABLE attempts (
                        id TEXT NOT NULL PRIMARY KEY,
                        mode TEXT NOT NULL,
                        target TEXT NOT NULL,
                        theme TEXT,
                        startedAt INTEGER NOT NULL,
                        completedAt INTEGER NOT NULL,
                        durationSeconds INTEGER NOT NULL,
                        score INTEGER NOT NULL,
                        total INTEGER NOT NULL,
                        passed INTEGER NOT NULL,
                        contentVersion INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "INSERT INTO attempts VALUES ('kept', 'MOCK', 'NATURALISATION', NULL, 1, 2, 60, 35, 40, 1, 1)",
                )
            },
        )
        version1.writableDatabase.close()
        version1.close()

        val version2 = helper(
            version = 2,
            onUpgrade = { database -> CivicDatabase.MIGRATION_1_2.migrate(database) },
        )
        val migrated = version2.writableDatabase
        migrated.query("SELECT COUNT(*) FROM attempts WHERE id = 'kept'").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        migrated.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'learning_progress'").use { cursor ->
            assertTrue(cursor.moveToFirst())
        }
        version2.close()
        context.deleteDatabase(TEST_DATABASE)
    }

    private fun helper(
        version: Int,
        onCreate: (SupportSQLiteDatabase) -> Unit = {},
        onUpgrade: (SupportSQLiteDatabase) -> Unit = {},
    ): SupportSQLiteOpenHelper {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DATABASE)
            .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                override fun onCreate(db: SupportSQLiteDatabase) = onCreate(db)
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(db)
            })
            .build()
        return FrameworkSQLiteOpenHelperFactory().create(configuration)
    }

    companion object {
        private const val TEST_DATABASE = "migration-test.db"
    }
}
