package com.example.testcivique.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testcivique.data.AnswerSnapshot
import com.example.testcivique.data.AttemptMode
import com.example.testcivique.data.AttemptRepository
import com.example.testcivique.data.CivicThemeId
import com.example.testcivique.data.ExamTarget
import com.example.testcivique.data.Question
import com.example.testcivique.data.QuestionGenerator
import java.util.UUID
import kotlin.math.ceil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class QuizUiState(
    val loading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: List<Int> = emptyList(),
    val validatedIndices: Set<Int> = emptySet(),
    val finished: Boolean = false,
    val remainingSeconds: Int = 0,
    val savedAttemptId: String? = null,
) {
    val selectedIndex: Int get() = answers.getOrElse(currentIndex) { -1 }
    val score: Int get() = questions.indices.count { answers.getOrElse(it) { -1 } == questions[it].correctIndex }
    val answeredCount: Int get() = answers.count { it >= 0 }
}

internal class QuizViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AttemptRepository,
    private val target: ExamTarget,
    private val theme: CivicThemeId?,
) : ViewModel() {
    private val isMock = theme == null
    private val _uiState = MutableStateFlow(restoreState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    init {
        if (_uiState.value.questions.isEmpty()) {
            createSeries()
        } else {
            if (_uiState.value.finished) saveIfNeeded() else startTimer()
        }
    }

    fun selectAnswer(index: Int) {
        val state = _uiState.value
        if (state.finished || state.currentIndex in state.validatedIndices || index !in 0..3) return
        val updated = state.answers.toMutableList().also { it[state.currentIndex] = index }
        update(state.copy(answers = updated))
    }

    fun validateCurrent() {
        val state = _uiState.value
        if (isMock || state.selectedIndex < 0 || state.finished) return
        update(state.copy(validatedIndices = state.validatedIndices + state.currentIndex))
    }

    fun goTo(index: Int) {
        val state = _uiState.value
        if (index !in state.questions.indices || state.finished) return
        update(state.copy(currentIndex = index))
    }

    fun previous() = goTo(_uiState.value.currentIndex - 1)

    fun next() {
        val state = _uiState.value
        if (state.currentIndex < state.questions.lastIndex) goTo(state.currentIndex + 1)
    }

    fun finish() {
        val state = _uiState.value
        if (state.finished || state.questions.isEmpty()) return
        timerJob?.cancel()
        update(state.copy(finished = true, remainingSeconds = remainingSeconds()))
        saveIfNeeded()
    }

    fun retry() {
        timerJob?.cancel()
        savedStateHandle.remove<ArrayList<Question>>(KEY_QUESTIONS)
        savedStateHandle.remove<IntArray>(KEY_ANSWERS)
        savedStateHandle.remove<IntArray>(KEY_VALIDATED)
        savedStateHandle.remove<String>(KEY_SAVED_ATTEMPT)
        savedStateHandle[KEY_SESSION_ID] = UUID.randomUUID().toString()
        savedStateHandle[KEY_STARTED_AT] = System.currentTimeMillis()
        savedStateHandle[KEY_FINISHED] = false
        _uiState.value = QuizUiState()
        createSeries()
    }

    private fun createSeries() {
        viewModelScope.launch {
            val recentConcepts = repository.recentConceptIds(target)
            val questions = if (isMock) {
                QuestionGenerator.mockExam(target, recentConcepts)
            } else {
                QuestionGenerator.thematicQuiz(requireNotNull(theme), target, recentConcepts)
            }
            val now = System.currentTimeMillis()
            savedStateHandle[KEY_STARTED_AT] = now
            savedStateHandle[KEY_DEADLINE_AT] = if (isMock) now + EXAM_DURATION_MS else 0L
            val state = QuizUiState(
                loading = false,
                questions = questions,
                answers = List(questions.size) { -1 },
                remainingSeconds = if (isMock) EXAM_DURATION_SECONDS else 0,
            )
            update(state)
            startTimer()
        }
    }

    private fun startTimer() {
        if (!isMock || _uiState.value.finished || _uiState.value.questions.isEmpty()) return
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!_uiState.value.finished) {
                val remaining = remainingSeconds()
                if (remaining <= 0) {
                    finish()
                    break
                }
                _uiState.value = _uiState.value.copy(remainingSeconds = remaining)
                delay(500)
            }
        }
    }

    private fun remainingSeconds(): Int {
        if (!isMock) return 0
        val deadline = savedStateHandle.get<Long>(KEY_DEADLINE_AT) ?: return EXAM_DURATION_SECONDS
        return ceil((deadline - System.currentTimeMillis()).coerceAtLeast(0L) / 1_000.0).toInt()
    }

    private fun saveIfNeeded() {
        val state = _uiState.value
        if (!state.finished || state.questions.isEmpty() || state.savedAttemptId != null) return
        val sessionId = savedStateHandle.get<String>(KEY_SESSION_ID)
            ?: UUID.randomUUID().toString().also { savedStateHandle[KEY_SESSION_ID] = it }
        val startedAt = savedStateHandle.get<Long>(KEY_STARTED_AT) ?: System.currentTimeMillis()
        val elapsedSeconds = ((System.currentTimeMillis() - startedAt) / 1_000).toInt().coerceAtLeast(0)
        viewModelScope.launch {
            repository.saveAttempt(
                id = sessionId,
                mode = if (isMock) AttemptMode.MOCK else AttemptMode.THEMATIC,
                target = target,
                theme = theme,
                startedAt = startedAt,
                durationSeconds = if (isMock) elapsedSeconds.coerceAtMost(EXAM_DURATION_SECONDS) else elapsedSeconds,
                answers = state.questions.mapIndexed { index, question ->
                    AnswerSnapshot(question, state.answers.getOrElse(index) { -1 })
                },
            )
            savedStateHandle[KEY_SAVED_ATTEMPT] = sessionId
            _uiState.value = _uiState.value.copy(savedAttemptId = sessionId)
        }
    }

    private fun update(state: QuizUiState) {
        _uiState.value = state
        savedStateHandle[KEY_QUESTIONS] = ArrayList(state.questions)
        savedStateHandle[KEY_CURRENT_INDEX] = state.currentIndex
        savedStateHandle[KEY_ANSWERS] = state.answers.toIntArray()
        savedStateHandle[KEY_VALIDATED] = state.validatedIndices.toIntArray()
        savedStateHandle[KEY_FINISHED] = state.finished
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreState(): QuizUiState {
        val questions = savedStateHandle.get<ArrayList<Question>>(KEY_QUESTIONS).orEmpty()
        if (questions.isEmpty()) return QuizUiState()
        val answers = savedStateHandle.get<IntArray>(KEY_ANSWERS)?.toList()
            ?.takeIf { it.size == questions.size } ?: List(questions.size) { -1 }
        return QuizUiState(
            loading = false,
            questions = questions,
            currentIndex = (savedStateHandle.get<Int>(KEY_CURRENT_INDEX) ?: 0).coerceIn(questions.indices),
            answers = answers,
            validatedIndices = savedStateHandle.get<IntArray>(KEY_VALIDATED)?.toSet().orEmpty(),
            finished = savedStateHandle.get<Boolean>(KEY_FINISHED) ?: false,
            remainingSeconds = remainingSeconds(),
            savedAttemptId = savedStateHandle.get(KEY_SAVED_ATTEMPT),
        )
    }

    companion object {
        private const val EXAM_DURATION_SECONDS = 45 * 60
        private const val EXAM_DURATION_MS = EXAM_DURATION_SECONDS * 1_000L
        private const val KEY_QUESTIONS = "quiz.questions"
        private const val KEY_CURRENT_INDEX = "quiz.currentIndex"
        private const val KEY_ANSWERS = "quiz.answers"
        private const val KEY_VALIDATED = "quiz.validated"
        private const val KEY_FINISHED = "quiz.finished"
        private const val KEY_STARTED_AT = "quiz.startedAt"
        private const val KEY_DEADLINE_AT = "quiz.deadlineAt"
        private const val KEY_SESSION_ID = "quiz.sessionId"
        private const val KEY_SAVED_ATTEMPT = "quiz.savedAttemptId"

        fun factory(
            repository: AttemptRepository,
            target: ExamTarget,
            theme: CivicThemeId?,
        ) = viewModelFactory {
            initializer {
                QuizViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = repository,
                    target = target,
                    theme = theme,
                )
            }
        }
    }
}
