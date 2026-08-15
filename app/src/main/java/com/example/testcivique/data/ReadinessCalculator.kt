package com.example.testcivique.data

import com.example.testcivique.data.local.AttemptAnswerEntity
import com.example.testcivique.data.local.AttemptEntity

internal object ReadinessCalculator {
    fun calculate(
        attempts: List<AttemptEntity>,
        answers: List<AttemptAnswerEntity>,
    ): ProgressSnapshot {
        val mockAttempts = attempts.filter { it.mode == AttemptMode.MOCK.name }
        val recentMocks = mockAttempts.take(5)
        val mockScore = weightedAverage(recentMocks.map { it.score.toFloat() / it.total.coerceAtLeast(1) })

        // Une même notion répétée plusieurs fois ne gonfle pas artificiellement la maîtrise.
        val latestAnswerByConcept = answers.distinctBy { it.conceptId }
        val themes = CivicThemeId.entries.map { theme ->
            val themeAnswers = latestAnswerByConcept.filter { it.theme == theme.name }
            val correct = themeAnswers.count { it.isCorrect }
            val total = themeAnswers.size
            val percentage = if (total == 0) 0f else correct.toFloat() / total
            ThemeMastery(
                theme = theme,
                correct = correct,
                total = total,
                percentage = percentage,
                status = when {
                    total < MIN_CONCEPTS_PER_THEME -> MasteryStatus.INSUFFICIENT
                    percentage >= 0.8f -> MasteryStatus.MASTERED
                    percentage >= 0.7f -> MasteryStatus.CONSOLIDATING
                    else -> MasteryStatus.TO_REVIEW
                },
            )
        }

        val themeScore = themes.map { it.percentage }.average().toFloat()
        val mockConfidence = (mockAttempts.size / 5f).coerceIn(0f, 1f)
        val themeConfidence = themes.map { (it.total / TARGET_CONCEPTS_PER_THEME.toFloat()).coerceIn(0f, 1f) }.average().toFloat()
        val confidence = mockConfidence * 0.55f + themeConfidence * 0.45f
        val readiness = mockScore * 0.7f + themeScore * 0.3f
        val sufficient = mockAttempts.size >= 3 && themes.all { it.total >= MIN_CONCEPTS_PER_THEME }
        val latestThreeMocks = mockAttempts.take(3)
        val ready = sufficient && confidence >= 0.72f && readiness >= 0.82f &&
            latestThreeMocks.size == 3 && latestThreeMocks.all { it.passed } &&
            themes.all { it.percentage >= 0.75f }

        return ProgressSnapshot(
            attemptsCount = attempts.size,
            mockAttemptsCount = mockAttempts.size,
            bestMockScore = mockAttempts.maxOfOrNull { it.score },
            readinessScore = readiness,
            readinessConfidence = confidence,
            readinessStatus = when {
                !sufficient -> ReadinessStatus.INSUFFICIENT
                ready -> ReadinessStatus.READY
                readiness >= 0.75f -> ReadinessStatus.ALMOST_READY
                else -> ReadinessStatus.NOT_READY
            },
            themes = themes,
        )
    }

    private fun weightedAverage(values: List<Float>): Float {
        if (values.isEmpty()) return 0f
        val weights = values.indices.map { (values.size - it).toFloat() }
        return values.zip(weights).sumOf { (value, weight) -> (value * weight).toDouble() }.toFloat() / weights.sum()
    }

    private const val MIN_CONCEPTS_PER_THEME = 10
    private const val TARGET_CONCEPTS_PER_THEME = 20
}
