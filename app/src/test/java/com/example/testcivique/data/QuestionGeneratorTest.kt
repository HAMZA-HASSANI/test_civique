package com.example.testcivique.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionGeneratorTest {
    @Test
    fun `all three application targets use tailored question pools`() {
        assertEquals(3, ExamTarget.entries.size)
        CivicThemeId.entries.forEach { theme ->
            val conceptsByTarget = ExamTarget.entries.associateWith { target ->
                DemoContent.questions
                    .filter { it.theme == theme && target in it.targets }
                    .map { it.conceptId }
                    .toSet()
            }
            conceptsByTarget.values.forEach { concepts ->
                assertEquals("Each target needs twenty concepts for $theme", 20, concepts.size)
            }
            assertEquals(
                "The three target pools must differ for $theme",
                ExamTarget.entries.size,
                conceptsByTarget.values.toSet().size,
            )
        }
    }

    @Test
    fun `bank contains at least one hundred formulations per theme and target`() {
        ExamTarget.entries.forEach { target ->
            QuestionGenerator.questionCountsByTheme(target).forEach { (_, count) ->
                assertEquals("Expected 100 tailored questions, got $count", 100, count)
            }
        }
    }

    @Test
    fun `thematic quiz contains twenty distinct concepts`() {
        CivicThemeId.entries.forEach { theme ->
            ExamTarget.entries.forEach { target ->
                val quiz = QuestionGenerator.thematicQuiz(theme, target, random = Random(42))
                assertEquals(20, quiz.size)
                assertEquals(20, quiz.map { it.conceptId }.distinct().size)
                assertTrue(quiz.all { target in it.targets })
            }
        }
    }

    @Test
    fun `mock exam follows official theme and situation distribution`() {
        ExamTarget.entries.forEach { target ->
            val exam = QuestionGenerator.mockExam(target, random = Random(7))
            assertEquals(40, exam.size)
            assertEquals(11, exam.count { it.theme == CivicThemeId.PRINCIPLES })
            assertEquals(6, exam.count { it.theme == CivicThemeId.INSTITUTIONS })
            assertEquals(11, exam.count { it.theme == CivicThemeId.RIGHTS })
            assertEquals(8, exam.count { it.theme == CivicThemeId.HISTORY })
            assertEquals(4, exam.count { it.theme == CivicThemeId.SOCIETY })
            assertEquals(12, exam.count { it.type == QuestionType.SITUATION })
            assertEquals(40, exam.map { it.conceptId }.distinct().size)
            assertTrue(exam.all { target in it.targets })
        }
    }
}
