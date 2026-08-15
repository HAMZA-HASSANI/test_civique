package com.example.testcivique.data

import kotlin.random.Random

object QuestionGenerator {
    const val THEMATIC_QUESTION_COUNT = 20
    const val MOCK_QUESTION_COUNT = 40
    const val MOCK_PASS_SCORE = 32

    fun thematicQuiz(
        theme: CivicThemeId,
        target: ExamTarget,
        recentConceptIds: Set<String> = emptySet(),
        random: Random = Random.Default,
    ): List<Question> {
        return select(
            pool = DemoContent.questions.filter { it.theme == theme && target in it.targets },
            count = THEMATIC_QUESTION_COUNT,
            recentConceptIds = recentConceptIds,
            random = random,
        ).shuffled(random)
    }

    fun mockExam(
        target: ExamTarget,
        recentConceptIds: Set<String> = emptySet(),
        random: Random = Random.Default,
    ): List<Question> {
        val eligible = DemoContent.questions.filter { target in it.targets }
        val selected = buildList {
            addAll(select(eligible.forTheme(CivicThemeId.PRINCIPLES).ofType(QuestionType.KNOWLEDGE), 5, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.PRINCIPLES).ofType(QuestionType.SITUATION), 6, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.INSTITUTIONS), 6, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.RIGHTS).ofType(QuestionType.KNOWLEDGE), 5, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.RIGHTS).ofType(QuestionType.SITUATION), 6, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.HISTORY), 8, recentConceptIds, random))
            addAll(select(eligible.forTheme(CivicThemeId.SOCIETY), 4, recentConceptIds, random))
        }
        check(selected.size == MOCK_QUESTION_COUNT) { "L'examen blanc doit contenir 40 questions." }
        return selected.shuffled(random)
    }

    fun questionCountsByTheme(target: ExamTarget): Map<CivicThemeId, Int> =
        CivicThemeId.entries.associateWith { theme ->
            DemoContent.questions.count { it.theme == theme && target in it.targets }
        }

    private fun select(
        pool: List<Question>,
        count: Int,
        recentConceptIds: Set<String>,
        random: Random,
    ): List<Question> {
        val variantsByConcept = pool.groupBy { it.conceptId }
        require(variantsByConcept.size >= count) {
            "Banque insuffisante : ${variantsByConcept.size} concepts disponibles pour $count demandés."
        }
        val concepts = variantsByConcept.keys
        val prioritized = concepts.filterNot { it in recentConceptIds }.shuffled(random) +
            concepts.filter { it in recentConceptIds }.shuffled(random)
        return prioritized.take(count).map { conceptId ->
            variantsByConcept.getValue(conceptId).random(random).shuffleOptions(random)
        }
    }

    private fun Question.shuffleOptions(random: Random): Question {
        val indexed = options.mapIndexed { index, value -> index to value }.shuffled(random)
        return copy(
            options = indexed.map { it.second },
            correctIndex = indexed.indexOfFirst { it.first == correctIndex },
        )
    }

    private fun List<Question>.forTheme(theme: CivicThemeId) = filter { it.theme == theme }
    private fun List<Question>.ofType(type: QuestionType) = filter { it.type == type }
}
