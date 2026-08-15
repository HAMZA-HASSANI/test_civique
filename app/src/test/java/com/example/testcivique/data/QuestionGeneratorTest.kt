package com.example.testcivique.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionGeneratorTest {
    @Test
    fun `all three application targets use the complete bank`() {
        assertEquals(3, ExamTarget.entries.size)
        ExamTarget.entries.forEach { target ->
            assertTrue(DemoContent.questions.all { target in it.targets })
        }
    }

    @Test
    fun `bank contains at least one hundred formulations per theme and target`() {
        ExamTarget.entries.forEach { target ->
            QuestionGenerator.questionCountsByTheme(target).forEach { (_, count) ->
                assertTrue("Expected at least 100 questions, got $count", count >= 100)
            }
        }
    }

    @Test
    fun `thematic quiz contains twenty distinct concepts`() {
        CivicThemeId.entries.forEach { theme ->
            val quiz = QuestionGenerator.thematicQuiz(theme, ExamTarget.NATURALISATION, random = Random(42))
            assertEquals(20, quiz.size)
            assertEquals(20, quiz.map { it.conceptId }.distinct().size)
        }
    }

    @Test
    fun `mock exam follows official theme and situation distribution`() {
        val exam = QuestionGenerator.mockExam(ExamTarget.NATURALISATION, random = Random(7))
        assertEquals(40, exam.size)
        assertEquals(11, exam.count { it.theme == CivicThemeId.PRINCIPLES })
        assertEquals(6, exam.count { it.theme == CivicThemeId.INSTITUTIONS })
        assertEquals(11, exam.count { it.theme == CivicThemeId.RIGHTS })
        assertEquals(8, exam.count { it.theme == CivicThemeId.HISTORY })
        assertEquals(4, exam.count { it.theme == CivicThemeId.SOCIETY })
        assertEquals(12, exam.count { it.type == QuestionType.SITUATION })
        assertEquals(40, exam.map { it.conceptId }.distinct().size)
    }
}
