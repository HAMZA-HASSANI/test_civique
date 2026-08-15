package com.example.testcivique.data

import com.example.testcivique.data.local.AttemptAnswerEntity
import com.example.testcivique.data.local.AttemptEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadinessCalculatorTest {
    @Test
    fun `repeated formulations of one concept count only once`() {
        val attempts = listOf(attempt("a1", AttemptMode.THEMATIC, 20, 20))
        val latestCorrect = answer("a1", "p1", CivicThemeId.PRINCIPLES, true)
        val olderWrong = answer("a1", "p1", CivicThemeId.PRINCIPLES, false)

        val snapshot = ReadinessCalculator.calculate(attempts, listOf(latestCorrect, olderWrong))
        val principles = snapshot.themes.first { it.theme == CivicThemeId.PRINCIPLES }

        assertEquals(1, principles.total)
        assertEquals(1, principles.correct)
    }

    @Test
    fun `strong varied and recent results produce a ready estimate`() {
        val attempts = (1..5).map { index -> attempt("mock-$index", AttemptMode.MOCK, 36, 40) }
        val answers = attempts.flatMap { attempt ->
            CivicThemeId.entries.flatMap { theme ->
                (1..10).map { concept -> answer(attempt.id, "${theme.name}-$concept", theme, true) }
            }
        }

        val snapshot = ReadinessCalculator.calculate(attempts, answers)

        assertEquals(ReadinessStatus.READY, snapshot.readinessStatus)
        assertTrue(snapshot.readinessScore >= 0.9f)
        assertTrue(snapshot.readinessConfidence >= 0.7f)
    }

    private fun attempt(id: String, mode: AttemptMode, score: Int, total: Int) = AttemptEntity(
        id = id,
        mode = mode.name,
        target = ExamTarget.NATURALISATION.name,
        theme = null,
        startedAt = 1L,
        completedAt = 2L,
        durationSeconds = 60,
        score = score,
        total = total,
        passed = score >= (total * 0.8f),
    )

    private fun answer(attemptId: String, conceptId: String, theme: CivicThemeId, correct: Boolean) =
        AttemptAnswerEntity(
            attemptId = attemptId,
            questionId = "$conceptId-v1",
            conceptId = conceptId,
            theme = theme.name,
            questionType = QuestionType.KNOWLEDGE.name,
            questionText = "Question ?",
            optionA = "A",
            optionB = "B",
            optionC = "C",
            optionD = "D",
            selectedIndex = if (correct) 0 else 1,
            correctIndex = 0,
            isCorrect = correct,
            explanation = "Explication",
            sourceTitle = "Source",
            sourceUrl = "https://example.invalid",
        )
}
