package com.example.testcivique.data

import androidx.room.withTransaction
import com.example.testcivique.data.local.AttemptAnswerEntity
import com.example.testcivique.data.local.AttemptEntity
import com.example.testcivique.data.local.AttemptWithAnswers
import com.example.testcivique.data.local.CivicDatabase
import com.example.testcivique.data.local.LearningProgressEntity
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

enum class AttemptMode { THEMATIC, MOCK }

data class AnswerSnapshot(
    val question: Question,
    val selectedIndex: Int,
)

enum class MasteryStatus { INSUFFICIENT, TO_REVIEW, CONSOLIDATING, MASTERED }

data class ThemeMastery(
    val theme: CivicThemeId,
    val correct: Int,
    val total: Int,
    val percentage: Float,
    val status: MasteryStatus,
)

enum class ReadinessStatus { INSUFFICIENT, NOT_READY, ALMOST_READY, READY }

data class ProgressSnapshot(
    val attemptsCount: Int,
    val mockAttemptsCount: Int,
    val bestMockScore: Int?,
    val readinessScore: Float,
    val readinessConfidence: Float,
    val readinessStatus: ReadinessStatus,
    val themes: List<ThemeMastery>,
)

class AttemptRepository(private val database: CivicDatabase) {
    private val dao = database.attemptDao()

    val attempts: Flow<List<AttemptEntity>> = dao.observeAttempts()

    fun observeAttempt(id: String): Flow<AttemptWithAnswers?> = dao.observeAttempt(id)

    fun observeProgress(target: ExamTarget): Flow<ProgressSnapshot> =
        combine(dao.observeAttempts(), dao.observeAnswers()) { allAttempts, allAnswers ->
            val attempts = allAttempts.filter { it.target == target.name }
            val attemptIds = attempts.mapTo(mutableSetOf()) { it.id }
            val answers = allAnswers.filter { it.attemptId in attemptIds }
            ReadinessCalculator.calculate(attempts, answers)
        }

    suspend fun saveAttempt(
        id: String = UUID.randomUUID().toString(),
        mode: AttemptMode,
        target: ExamTarget,
        theme: CivicThemeId?,
        startedAt: Long,
        durationSeconds: Int,
        answers: List<AnswerSnapshot>,
    ): String {
        val score = answers.count { it.selectedIndex == it.question.correctIndex }
        val total = answers.size
        val passScore = if (mode == AttemptMode.MOCK) QuestionGenerator.MOCK_PASS_SCORE else 16
        val attempt = AttemptEntity(
            id = id,
            mode = mode.name,
            target = target.name,
            theme = theme?.name,
            startedAt = startedAt,
            completedAt = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            score = score,
            total = total,
            passed = score >= passScore,
        )
        val entities = answers.map { snapshot ->
            val question = snapshot.question
            AttemptAnswerEntity(
                attemptId = id,
                questionId = question.id,
                conceptId = question.conceptId,
                theme = question.theme.name,
                questionType = question.type.name,
                questionText = question.text,
                optionA = question.options.getOrElse(0) { "" },
                optionB = question.options.getOrElse(1) { "" },
                optionC = question.options.getOrElse(2) { "" },
                optionD = question.options.getOrElse(3) { "" },
                selectedIndex = snapshot.selectedIndex,
                correctIndex = question.correctIndex,
                isCorrect = snapshot.selectedIndex == question.correctIndex,
                explanation = question.explanation,
                sourceTitle = question.sourceTitle,
                sourceUrl = question.sourceUrl,
            )
        }
        database.withTransaction {
            dao.insertAttempt(attempt)
            dao.insertAnswers(entities)
        }
        return id
    }

    suspend fun recentConceptIds(limit: Int = 80): Set<String> = dao.recentConceptIds(limit).toSet()
    suspend fun recentConceptIds(target: ExamTarget, limit: Int = 80): Set<String> =
        dao.recentConceptIdsForTarget(target.name, limit).toSet()
    fun observeLearningProgress(target: ExamTarget): Flow<List<LearningProgressEntity>> =
        dao.observeLearningProgress(target.name)
    suspend fun setChapterCompleted(target: ExamTarget, theme: CivicThemeId, chapterIndex: Int, completed: Boolean) {
        if (completed) {
            dao.completeChapter(LearningProgressEntity(target.name, theme.name, chapterIndex, System.currentTimeMillis()))
        } else {
            dao.reopenChapter(target.name, theme.name, chapterIndex)
        }
    }
    suspend fun deleteAttempt(id: String) = dao.deleteAttempt(id)
    suspend fun deleteAll() = dao.deleteAllAttempts()
    suspend fun deleteForTarget(target: ExamTarget) = dao.deleteAttemptsForTarget(target.name)
}
