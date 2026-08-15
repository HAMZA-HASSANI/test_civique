package com.example.testcivique.data

import androidx.room.withTransaction
import com.example.testcivique.data.local.AttemptAnswerEntity
import com.example.testcivique.data.local.AttemptEntity
import com.example.testcivique.data.local.AttemptWithAnswers
import com.example.testcivique.data.local.CivicDatabase
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
            val mockAttempts = attempts.filter { it.mode == AttemptMode.MOCK.name }
            val recentMocks = mockAttempts.take(5)
            val mockAverage = if (recentMocks.isEmpty()) 0f else recentMocks.map { it.score.toFloat() / it.total }.average().toFloat()
            val themes = CivicThemeId.entries.map { theme ->
                val themeAnswers = answers.filter { it.theme == theme.name }.take(100)
                val correct = themeAnswers.count { it.isCorrect }
                val total = themeAnswers.size
                val percentage = if (total == 0) 0f else correct.toFloat() / total
                ThemeMastery(
                    theme = theme,
                    correct = correct,
                    total = total,
                    percentage = percentage,
                    status = when {
                        total < 40 -> MasteryStatus.INSUFFICIENT
                        percentage >= 0.8f -> MasteryStatus.MASTERED
                        percentage >= 0.7f -> MasteryStatus.CONSOLIDATING
                        else -> MasteryStatus.TO_REVIEW
                    },
                )
            }
            val themeAverage = themes.map { it.percentage }.average().toFloat()
            val readiness = mockAverage * 0.65f + themeAverage * 0.35f
            val sufficient = mockAttempts.size >= 3 && themes.all { it.total >= 40 }
            val latestMocks = mockAttempts.take(4)
            val isReady = sufficient && readiness >= 0.82f && latestMocks.firstOrNull()?.passed == true &&
                latestMocks.count { it.passed } >= 3 && themes.all { it.percentage >= 0.75f }
            ProgressSnapshot(
                attemptsCount = attempts.size,
                mockAttemptsCount = mockAttempts.size,
                bestMockScore = mockAttempts.maxOfOrNull { it.score },
                readinessScore = readiness,
                readinessStatus = when {
                    !sufficient -> ReadinessStatus.INSUFFICIENT
                    isReady -> ReadinessStatus.READY
                    readiness >= 0.75f -> ReadinessStatus.ALMOST_READY
                    else -> ReadinessStatus.NOT_READY
                },
                themes = themes,
            )
        }

    suspend fun saveAttempt(
        mode: AttemptMode,
        target: ExamTarget,
        theme: CivicThemeId?,
        startedAt: Long,
        durationSeconds: Int,
        answers: List<AnswerSnapshot>,
    ): String {
        val id = UUID.randomUUID().toString()
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
    suspend fun deleteAttempt(id: String) = dao.deleteAttempt(id)
    suspend fun deleteAll() = dao.deleteAllAttempts()
}
