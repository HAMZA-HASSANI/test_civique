package com.example.testcivique.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Query("SELECT * FROM attempts ORDER BY completedAt DESC")
    fun observeAttempts(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempt_answers ORDER BY id DESC")
    fun observeAnswers(): Flow<List<AttemptAnswerEntity>>

    @Transaction
    @Query("SELECT * FROM attempts WHERE id = :attemptId")
    fun observeAttempt(attemptId: String): Flow<AttemptWithAnswers?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AttemptAnswerEntity>)

    @Query("DELETE FROM attempts WHERE id = :attemptId")
    suspend fun deleteAttempt(attemptId: String)

    @Query("DELETE FROM attempts")
    suspend fun deleteAllAttempts()

    @Query("DELETE FROM attempts WHERE target = :target")
    suspend fun deleteAttemptsForTarget(target: String)

    @Query("SELECT conceptId FROM attempt_answers ORDER BY id DESC LIMIT :limit")
    suspend fun recentConceptIds(limit: Int): List<String>

    @Query(
        """
        SELECT answers.conceptId FROM attempt_answers AS answers
        INNER JOIN attempts ON attempts.id = answers.attemptId
        WHERE attempts.target = :target
        ORDER BY answers.id DESC
        LIMIT :limit
        """,
    )
    suspend fun recentConceptIdsForTarget(target: String, limit: Int): List<String>

    @Query("SELECT * FROM learning_progress WHERE target = :target ORDER BY completedAt DESC")
    fun observeLearningProgress(target: String): Flow<List<LearningProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun completeChapter(progress: LearningProgressEntity)

    @Query("DELETE FROM learning_progress WHERE target = :target AND theme = :theme AND chapterIndex = :chapterIndex")
    suspend fun reopenChapter(target: String, theme: String, chapterIndex: Int)
}
