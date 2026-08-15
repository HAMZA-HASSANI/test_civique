package com.example.testcivique.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankEditorialTest {
    private val forbiddenMetaPhrases = listOf(
        "révision ciblée",
        "choisissez la bonne réponse",
        "choisis une réponse",
        "question de préparation",
        "dans le cadre du test civique",
        "pour le test civique",
    )

    @Test
    fun `all displayed questions are autonomous and well formed`() {
        assertEquals(600, DemoContent.questions.size)
        assertEquals(600, DemoContent.questions.map { it.text }.distinct().size)

        DemoContent.questions.forEach { question ->
            val normalized = question.text.lowercase()
            assertTrue("Meta wording found in: ${question.text}", forbiddenMetaPhrases.none { it in normalized })
            assertTrue("Question must be trimmed: ${question.id}", question.text == question.text.trim())
            assertTrue("Question must end with ? or : (${question.id})", question.text.endsWith('?') || question.text.endsWith(':'))
            assertTrue("Question must start with an uppercase letter: ${question.id}", question.text.first().isUpperCase())
        }
    }

    @Test
    fun `every concept has five distinct editorial variants`() {
        DemoContent.questions.groupBy { it.conceptId }.forEach { (conceptId, variants) ->
            assertEquals("Wrong variant count for $conceptId", 5, variants.size)
            assertEquals("Duplicate wording for $conceptId", 5, variants.map { it.text }.distinct().size)
        }
    }

    @Test
    fun `answers and explanations are structurally valid`() {
        DemoContent.questions.forEach { question ->
            assertEquals("Each question needs four options: ${question.id}", 4, question.options.size)
            assertEquals("Options must be distinct: ${question.id}", 4, question.options.distinct().size)
            assertTrue("Blank option: ${question.id}", question.options.none { it.isBlank() })
            assertTrue("Invalid correct answer index: ${question.id}", question.correctIndex in question.options.indices)
            assertTrue("Missing explanation: ${question.id}", question.explanation.isNotBlank())
            assertTrue("Missing source title: ${question.id}", question.sourceTitle.isNotBlank())
        }
    }

    @Test
    fun `audited prompts match the expected answer form`() {
        val canonical = DemoContent.canonicalQuestions.associateBy { it.id }
        assertEquals("Oui, il doit rester neutre et traiter tous les usagers également", canonical.getValue("p15").options[canonical.getValue("p15").correctIndex])
        assertEquals("Toute personne présente en France", canonical.getValue("d2").options[canonical.getValue("d2").correctIndex])
        assertEquals("Aider ou alerter les secours sans se mettre en danger sérieux", canonical.getValue("d10").options[canonical.getValue("d10").correctIndex])
        assertEquals("Aux personnes mineures", canonical.getValue("d14").options[canonical.getValue("d14").correctIndex])
        assertEquals("Le conseil municipal", canonical.getValue("i08").options[canonical.getValue("i08").correctIndex])
    }
}
