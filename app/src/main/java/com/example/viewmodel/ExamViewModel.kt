package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActiveExamEntity
import com.example.data.ExamDatabase
import com.example.data.ExamHistoryEntity
import com.example.data.ExamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class Screen {
    data object Splash : Screen()
    data object Home : Screen()
    data object Setup : Screen()
    data object Step1Exam : Screen()
    data object Step2AnswerKey : Screen()
    data class Result(val historyId: Long) : Screen()
    data class HistoryAnalytics(val initialTab: Int = 0) : Screen()
    data class ExamReviewDetail(val historyId: Long) : Screen()
    data object About : Screen()
}

data class SetupConfig(
    val title: String = "মডেল টেস্ট",
    val totalQuestions: Int = 100,
    val durationMinutes: Int = 60,
    val negativeMarkRate: Float = 0.50f,
    val passPercentage: Float = 80f
)

data class ActiveExamUiState(
    val isActive: Boolean = false,
    val title: String = "",
    val totalQuestions: Int = 100,
    val durationMinutes: Int = 60,
    val remainingSeconds: Long = 3600,
    val negativeMarkRate: Float = 0.50f,
    val passPercentage: Float = 80f,
    val step: Int = 1, // 1: Exam Phase, 2: Answer Key Phase
    val userAnswers: Map<Int, Int> = emptyMap(), // question (1..N) -> option (0..3)
    val answerKeys: Map<Int, Int> = emptyMap(),   // question (1..N) -> option (0..3)
    val isTimerRunning: Boolean = false,
    val startTimeMillis: Long = 0L
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExamRepository
    private val vibrator: Vibrator?

    init {
        val db = ExamDatabase.getDatabase(application)
        repository = ExamRepository(db.examDao())

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Navigation Stack
    private val screenStack = mutableListOf<Screen>(Screen.Splash)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Setup Config
    private val _setupConfig = MutableStateFlow(SetupConfig())
    val setupConfig: StateFlow<SetupConfig> = _setupConfig.asStateFlow()

    // Active Exam
    private val _examState = MutableStateFlow(ActiveExamUiState())
    val examState: StateFlow<ActiveExamUiState> = _examState.asStateFlow()

    // Saved Active Exam from Room (for resume notification / banner)
    val savedActiveExam: StateFlow<ActiveExamEntity?> = repository.activeExam
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // History Flow
    val allHistory: StateFlow<List<ExamHistoryEntity>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Events (e.g. snackbars, toasts)
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        // Check for any ongoing exam
        viewModelScope.launch {
            val existing = repository.getActiveExamOnce()
            if (existing != null) {
                // Preload it
                _examState.value = ActiveExamUiState(
                    isActive = true,
                    title = existing.title,
                    totalQuestions = existing.totalQuestions,
                    durationMinutes = existing.durationMinutes,
                    remainingSeconds = existing.remainingSeconds,
                    negativeMarkRate = existing.negativeMarkRate,
                    passPercentage = existing.passPercentage,
                    step = existing.step,
                    userAnswers = ExamRepository.decodeMap(existing.userAnswersJson),
                    answerKeys = ExamRepository.decodeMap(existing.answerKeysJson),
                    isTimerRunning = false,
                    startTimeMillis = existing.startTimeMillis
                )
            }
        }
    }

    // Navigation methods
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun popBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun navigateToHome() {
        screenStack.clear()
        screenStack.add(Screen.Home)
        _currentScreen.value = Screen.Home
    }

    // Setup Config updates
    fun updateSetupTitle(title: String) {
        _setupConfig.update { it.copy(title = title) }
    }

    fun updateSetupQuestions(count: Int) {
        _setupConfig.update { it.copy(totalQuestions = count.coerceIn(1, 500)) }
    }

    fun updateSetupDuration(minutes: Int) {
        _setupConfig.update { it.copy(durationMinutes = minutes.coerceIn(1, 720)) }
    }

    fun updateSetupNegativeRate(rate: Float) {
        _setupConfig.update { it.copy(negativeMarkRate = rate) }
    }

    fun updateSetupPassPercentage(percentage: Float) {
        _setupConfig.update { it.copy(passPercentage = percentage.coerceIn(1f, 100f)) }
    }

    // Start Exam from Setup
    fun startNewExam() {
        val config = _setupConfig.value
        val totalSecs = config.durationMinutes * 60L
        val now = System.currentTimeMillis()

        _examState.value = ActiveExamUiState(
            isActive = true,
            title = config.title.ifBlank { "RIP-Exam টেস্ট" },
            totalQuestions = config.totalQuestions,
            durationMinutes = config.durationMinutes,
            remainingSeconds = totalSecs,
            negativeMarkRate = config.negativeMarkRate,
            passPercentage = config.passPercentage,
            step = 1,
            userAnswers = emptyMap(),
            answerKeys = emptyMap(),
            isTimerRunning = true,
            startTimeMillis = now
        )

        // Save immediately to Room for crash/call protection
        persistActiveExam()
        startTimer()

        screenStack.add(Screen.Step1Exam)
        _currentScreen.value = Screen.Step1Exam
    }

    // Resume an active saved exam
    fun resumeActiveExam() {
        val current = _examState.value
        if (!current.isActive) return

        if (current.step == 1) {
            startTimer()
            navigateTo(Screen.Step1Exam)
        } else {
            navigateTo(Screen.Step2AnswerKey)
        }
    }

    // Discard active exam
    fun discardActiveExam() {
        stopTimer()
        _examState.value = ActiveExamUiState()
        viewModelScope.launch {
            repository.clearActiveExam()
        }
    }

    // Phase 1: One-Touch answer selection
    fun selectUserAnswer(questionNum: Int, optionIndex: Int) {
        val state = _examState.value
        if (state.step != 1) return

        // STRICT ONE-TOUCH CONSTRAINT: If question is already answered, cannot change!
        if (state.userAnswers.containsKey(questionNum)) {
            vibrateSubtle(short = true)
            viewModelScope.launch {
                _toastEvent.emit("১ম ধাপে একবার উত্তর সিলেক্ট করার পর তা আর পরিবর্তন করা যাবে না!")
            }
            return
        }

        // Add answer and lock
        val updatedAnswers = state.userAnswers.toMutableMap()
        updatedAnswers[questionNum] = optionIndex
        _examState.update { it.copy(userAnswers = updatedAnswers) }

        vibrateSubtle(short = false)
        persistActiveExam()
    }

    // Advance to Phase 2 (Answer Key)
    fun advanceToStep2() {
        stopTimer()
        _examState.update { it.copy(step = 2, isTimerRunning = false) }
        persistActiveExam()
        vibrateSubtle(short = false)

        screenStack.add(Screen.Step2AnswerKey)
        _currentScreen.value = Screen.Step2AnswerKey
    }

    // Phase 2: Answer Key selection (User CAN change answer keys freely)
    fun selectAnswerKey(questionNum: Int, optionIndex: Int) {
        val state = _examState.value
        if (state.step != 2) return

        val updatedKeys = state.answerKeys.toMutableMap()
        updatedKeys[questionNum] = optionIndex
        _examState.update { it.copy(answerKeys = updatedKeys) }

        vibrateSubtle(short = false)
        persistActiveExam()
    }

    // Submit Final Result
    fun submitFinalResult() {
        val state = _examState.value
        val total = state.totalQuestions
        val userAns = state.userAnswers
        val ansKeys = state.answerKeys

        var correct = 0
        var wrong = 0
        var skipped = 0

        for (q in 1..total) {
            val u = userAns[q]
            val k = ansKeys[q]

            if (u == null) {
                skipped++
            } else if (k != null) {
                if (u == k) {
                    correct++
                } else {
                    wrong++
                }
            } else {
                // Key not provided for this question, count as skipped
                skipped++
            }
        }

        val negativeDeducted = wrong * state.negativeMarkRate
        val finalScore = (correct * 1.0f) - negativeDeducted
        val passThreshold = total * (state.passPercentage / 100f)
        val isPassed = finalScore >= passThreshold

        val timeSpent = (state.durationMinutes * 60L) - state.remainingSeconds

        viewModelScope.launch {
            val history = ExamHistoryEntity(
                title = state.title,
                totalQuestions = total,
                durationMinutes = state.durationMinutes,
                timeSpentSeconds = timeSpent.coerceAtLeast(0L),
                negativeMarkRate = state.negativeMarkRate,
                passPercentage = state.passPercentage,
                correctCount = correct,
                wrongCount = wrong,
                skippedCount = skipped,
                negativeMarksDeducted = negativeDeducted,
                finalScore = finalScore,
                isPassed = isPassed,
                userAnswersJson = ExamRepository.encodeMap(userAns),
                answerKeysJson = ExamRepository.encodeMap(ansKeys),
                timestamp = System.currentTimeMillis()
            )

            val insertedId = repository.insertHistory(history)
            repository.clearActiveExam()
            _examState.value = ActiveExamUiState() // Reset active state

            screenStack.add(Screen.Result(insertedId))
            _currentScreen.value = Screen.Result(insertedId)
        }
    }

    suspend fun getHistoryItem(id: Long): ExamHistoryEntity? {
        return repository.getHistoryById(id)
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    // Live Timer
    private fun startTimer() {
        timerJob?.cancel()
        _examState.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            while (isActive && _examState.value.isTimerRunning && _examState.value.remainingSeconds > 0) {
                delay(1000L)
                val newRemaining = _examState.value.remainingSeconds - 1
                _examState.update { it.copy(remainingSeconds = newRemaining) }

                // Periodic autosave every 5 seconds to reduce flash writes while guaranteeing crash protection
                if (newRemaining % 5 == 0L) {
                    persistActiveExam()
                }

                if (newRemaining <= 0) {
                    // Time up! Auto-advance to Step 2
                    vibrateAlert()
                    advanceToStep2()
                    _toastEvent.emit("সময় শেষ! ১ম ধাপ লক হয়ে ২য় ধাপে চলে এসেছে।")
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _examState.update { it.copy(isTimerRunning = false) }
    }

    private fun persistActiveExam() {
        val state = _examState.value
        if (!state.isActive) return

        viewModelScope.launch {
            val entity = ActiveExamEntity(
                id = 1,
                title = state.title,
                totalQuestions = state.totalQuestions,
                durationMinutes = state.durationMinutes,
                remainingSeconds = state.remainingSeconds,
                negativeMarkRate = state.negativeMarkRate,
                passPercentage = state.passPercentage,
                step = state.step,
                userAnswersJson = ExamRepository.encodeMap(state.userAnswers),
                answerKeysJson = ExamRepository.encodeMap(state.answerKeys),
                startTimeMillis = state.startTimeMillis,
                lastSavedMillis = System.currentTimeMillis()
            )
            repository.saveActiveExam(entity)
        }
    }

    private fun vibrateSubtle(short: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = if (short) 30L else 50L
                val amplitude = if (short) 80 else 140
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (short) 30L else 50L)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateAlert() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 200, 100, 200)
                val amplitudes = intArrayOf(0, 255, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 200, 100, 200), -1)
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
