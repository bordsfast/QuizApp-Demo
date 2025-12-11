package com.example.myquizapp.user_interface.ParticipantQuestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import com.example.myquizapp.domain.QuizSession.QuizSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
// #region agent log
import java.io.File
import org.json.JSONObject
private fun debugLog(location: String, message: String, data: Map<String, Any?>, hypothesisId: String) {
    try {
        val logFile = File("/Users/michaelxernanbordonada/Documents/Files/RND/MyQuizApp/.cursor/debug.log")
        val json = JSONObject().apply {
            put("location", location)
            put("message", message)
            put("data", JSONObject(data.mapValues { it.value?.toString() }))
            put("hypothesisId", hypothesisId)
            put("timestamp", System.currentTimeMillis())
            put("sessionId", "debug-session")
        }
        logFile.appendText(json.toString() + "\n")
    } catch (e: Exception) { }
}
// #endregion

/**
 * ParticipantQuestionViewModel.kt - ViewModel for participant question screen
 */
@HiltViewModel
class ParticipantQuestionViewModel @Inject constructor(
    private val quizSessionUseCase: QuizSessionUseCase
) : ViewModel() {

    companion object {
        const val QUESTION_TIME_LIMIT_MILLIS = 10000L
        const val TIMER_TICK_MILLIS = 100L
    }

    private val _uiState = MutableStateFlow(ParticipantQuestionUiState())
    val uiState: StateFlow<ParticipantQuestionUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ParticipantQuestionUiEffect>()
    val uiEffect: SharedFlow<ParticipantQuestionUiEffect> = _uiEffect.asSharedFlow()

    private var roomId: String = ""
    private var participantId: String = ""
    private var timerJob: Job? = null
    private var currentQuestionIndex: Int = -1
    private var lastProcessedStatus: RoomStatus? = null

    fun initialize(roomId: String, participantId: String) {
        // #region agent log
        debugLog("ParticipantQuestionVM.initialize", "Initialize called", mapOf(
            "roomId" to roomId,
            "participantId" to participantId,
            "currentQuestionIndex" to currentQuestionIndex,
            "existingRoomId" to this.roomId
        ), "D")
        // #endregion
        this.roomId = roomId
        this.participantId = participantId
        loadRoom()
        observeRoom()
    }

    fun onEvent(event: ParticipantQuestionUiEvent) {
        when (event) {
            is ParticipantQuestionUiEvent.AnswerSelected -> selectAnswer(event.answerIndex)
        }
    }

    private fun loadRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = quizSessionUseCase.getRoomById(roomId)
            result.fold(
                onSuccess = { room ->
                    handleRoomUpdate(room)
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun observeRoom() {
        viewModelScope.launch {
            quizSessionUseCase.subscribeToRoom(roomId)
            quizSessionUseCase.observeRoom(roomId)
                .catch { }
                .collect { room ->
                    handleRoomUpdate(room)
                }
        }
    }

    private fun handleRoomUpdate(room: QuizRoom) {
        // #region agent log
        debugLog("ParticipantQuestionVM.handleRoomUpdate", "Room update received", mapOf(
            "roomStatus" to room.status.name,
            "roomQuestionIndex" to room.currentQuestionIndex,
            "localQuestionIndex" to currentQuestionIndex,
            "lastProcessedStatus" to lastProcessedStatus?.name,
            "roomId" to room.id
        ), "A_E")
        // #endregion
        
        // CRITICAL: Check-and-set status SYNCHRONOUSLY before launching coroutine
        // This prevents race conditions when multiple updates arrive rapidly
        val statusChanged = room.status != lastProcessedStatus
        val shouldNavigateToLeaderboard = room.status == RoomStatus.SHOWING_LEADERBOARD && statusChanged
        val shouldNavigateToWinners = room.status == RoomStatus.FINISHED && statusChanged
        
        // Update lastProcessedStatus SYNCHRONOUSLY for navigation-triggering statuses
        if (shouldNavigateToLeaderboard || shouldNavigateToWinners) {
            lastProcessedStatus = room.status
            timerJob?.cancel()
        } else if (room.status == RoomStatus.ACTIVE) {
            lastProcessedStatus = RoomStatus.ACTIVE
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(room = room) }
            when (room.status) {
                RoomStatus.ACTIVE -> {
                    // #region agent log
                    debugLog("ParticipantQuestionVM.handleRoomUpdate", "ACTIVE status - checking question index", mapOf(
                        "roomQuestionIndex" to room.currentQuestionIndex,
                        "localQuestionIndex" to currentQuestionIndex,
                        "willLoadNewQuestion" to (room.currentQuestionIndex != currentQuestionIndex)
                    ), "B")
                    // #endregion
                    if (room.currentQuestionIndex != currentQuestionIndex) {
                        currentQuestionIndex = room.currentQuestionIndex
                        loadNewQuestion(room.currentQuestionIndex)
                    }
                }
                RoomStatus.SHOWING_LEADERBOARD -> {
                    if (shouldNavigateToLeaderboard) {
                        // #region agent log
                        debugLog("ParticipantQuestionVM.handleRoomUpdate", "SHOWING_LEADERBOARD - emitting navigation (status changed)", mapOf(
                            "roomId" to roomId
                        ), "A")
                        // #endregion
                        _uiEffect.emit(ParticipantQuestionUiEffect.NavigateToLeaderboard(roomId))
                    }
                    // #region agent log
                    else {
                        debugLog("ParticipantQuestionVM.handleRoomUpdate", "SHOWING_LEADERBOARD - SKIPPED (already processed)", mapOf(
                            "roomId" to roomId
                        ), "A")
                    }
                    // #endregion
                }
                RoomStatus.FINISHED -> {
                    if (shouldNavigateToWinners) {
                        // #region agent log
                        debugLog("ParticipantQuestionVM.handleRoomUpdate", "FINISHED - emitting navigation to winners (status changed)", mapOf(
                            "roomId" to roomId
                        ), "A")
                        // #endregion
                        _uiEffect.emit(ParticipantQuestionUiEffect.NavigateToWinners(roomId))
                    }
                }
                else -> { }
            }
        }
    }

    private fun loadNewQuestion(questionIndex: Int) {
        viewModelScope.launch {
            // #region agent log
            debugLog("ParticipantQuestionVM.loadNewQuestion", "Loading question", mapOf(
                "questionIndex" to questionIndex,
                "roomId" to roomId,
                "participantId" to participantId
            ), "B")
            // #endregion
            val question = quizSessionUseCase.getQuestion(questionIndex)
            val hasAnswered = quizSessionUseCase.hasParticipantAnswered(roomId, participantId, questionIndex)
            // #region agent log
            debugLog("ParticipantQuestionVM.loadNewQuestion", "Question loaded result", mapOf(
                "questionIndex" to questionIndex,
                "questionIsNull" to (question == null),
                "questionText" to (question?.text ?: "NULL"),
                "hasAnswered" to hasAnswered
            ), "B")
            // #endregion
            _uiState.update { 
                it.copy(
                    currentQuestion = question,
                    selectedAnswerIndex = null,
                    hasAnswered = hasAnswered,
                    lastAnswerCorrect = null,
                    lastPointsEarned = 0,
                    remainingTimeMillis = QUESTION_TIME_LIMIT_MILLIS
                ) 
            }
            if (!hasAnswered) {
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = QUESTION_TIME_LIMIT_MILLIS
            while (remaining > 0 && !_uiState.value.hasAnswered) {
                delay(TIMER_TICK_MILLIS)
                remaining -= TIMER_TICK_MILLIS
                _uiState.update { it.copy(remainingTimeMillis = remaining.coerceAtLeast(0)) }
            }
            if (!_uiState.value.hasAnswered && remaining <= 0) {
                _uiState.update { it.copy(hasAnswered = true) }
            }
        }
    }

    private fun selectAnswer(answerIndex: Int) {
        if (_uiState.value.hasAnswered) return
        val question = _uiState.value.currentQuestion ?: return
        val remainingTime = _uiState.value.remainingTimeMillis

        _uiState.update { 
            it.copy(
                selectedAnswerIndex = answerIndex,
                hasAnswered = true
            ) 
        }
        timerJob?.cancel()

        viewModelScope.launch {
            val result = quizSessionUseCase.submitAnswer(
                roomId = roomId,
                participantId = participantId,
                questionIndex = question.index,
                answerIndex = answerIndex,
                remainingTimeMillis = remainingTime
            )
            result.fold(
                onSuccess = { answerResult ->
                    _uiState.update { 
                        it.copy(
                            lastAnswerCorrect = answerResult.isCorrect,
                            lastPointsEarned = answerResult.pointsEarned,
                            totalScore = answerResult.newTotalScore
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        viewModelScope.launch {
            quizSessionUseCase.unsubscribeFromRoom(roomId)
        }
    }
}

data class ParticipantQuestionUiState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val remainingTimeMillis: Long = 10000L,
    val selectedAnswerIndex: Int? = null,
    val hasAnswered: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val lastPointsEarned: Int = 0,
    val totalScore: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ParticipantQuestionUiEvent {
    data class AnswerSelected(val answerIndex: Int) : ParticipantQuestionUiEvent()
}

sealed class ParticipantQuestionUiEffect {
    data class NavigateToLeaderboard(val roomId: String) : ParticipantQuestionUiEffect()
    data class NavigateToWinners(val roomId: String) : ParticipantQuestionUiEffect()
}
