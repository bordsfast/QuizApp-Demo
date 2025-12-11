package com.example.myquizapp.user_interface.Leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.fixed.QuizQuestions
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import com.example.myquizapp.domain.Leaderboard.LeaderboardUseCase
import com.example.myquizapp.domain.QuizSession.QuizSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
 * LeaderboardViewModel.kt - ViewModel for leaderboard screen
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardUseCase: LeaderboardUseCase,
    private val quizSessionUseCase: QuizSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LeaderboardUiEffect>()
    val uiEffect: SharedFlow<LeaderboardUiEffect> = _uiEffect.asSharedFlow()

    private var roomId: String = ""
    private var isHost: Boolean = false
    private var lastProcessedStatus: RoomStatus? = null

    fun initialize(roomId: String, isHost: Boolean) {
        this.roomId = roomId
        this.isHost = isHost
        _uiState.update { it.copy(isHost = isHost) }
        loadLeaderboard()
        loadRoom()
        if (!isHost) {
            observeRoom()
        }
    }

    fun onEvent(event: LeaderboardUiEvent) {
        when (event) {
            is LeaderboardUiEvent.NextQuestionClicked -> nextQuestion()
            is LeaderboardUiEvent.EndQuizClicked -> endQuiz()
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val topResult = leaderboardUseCase.getTopParticipants(roomId, 3)
            val allResult = leaderboardUseCase.getAllParticipants(roomId)
            topResult.fold(
                onSuccess = { top ->
                    _uiState.update { it.copy(topParticipants = top) }
                },
                onFailure = { }
            )
            allResult.fold(
                onSuccess = { all ->
                    _uiState.update { 
                        it.copy(allParticipants = all.sortedByDescending { p -> p.score }, isLoading = false) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun loadRoom() {
        viewModelScope.launch {
            val result = quizSessionUseCase.getRoomById(roomId)
            result.fold(
                onSuccess = { room ->
                    val isLastQuestion = QuizQuestions.isLastQuestion(room.currentQuestionIndex)
                    _uiState.update { it.copy(room = room, isLastQuestion = isLastQuestion) }
                },
                onFailure = { }
            )
        }
    }

    private fun observeRoom() {
        viewModelScope.launch {
            quizSessionUseCase.subscribeToRoom(roomId)
            quizSessionUseCase.observeRoom(roomId)
                .catch { }
                .collect { room ->
                    // #region agent log
                    debugLog("LeaderboardVM.observeRoom", "Room update received", mapOf(
                        "roomStatus" to room.status.name,
                        "roomQuestionIndex" to room.currentQuestionIndex,
                        "isHost" to isHost,
                        "lastProcessedStatus" to lastProcessedStatus?.name,
                        "roomId" to room.id
                    ), "A_C")
                    // #endregion
                    
                    // CRITICAL: Check-and-set status SYNCHRONOUSLY before any async operations
                    // This prevents race conditions when multiple updates arrive rapidly
                    val statusChanged = room.status != lastProcessedStatus
                    val shouldNavigateToQuestion = room.status == RoomStatus.ACTIVE && statusChanged
                    val shouldNavigateToWinners = room.status == RoomStatus.FINISHED && statusChanged
                    
                    // Update lastProcessedStatus SYNCHRONOUSLY
                    if (shouldNavigateToQuestion || shouldNavigateToWinners || room.status == RoomStatus.SHOWING_LEADERBOARD) {
                        lastProcessedStatus = room.status
                    }
                    
                    _uiState.update { it.copy(room = room) }
                    
                    when (room.status) {
                        RoomStatus.ACTIVE -> {
                            if (shouldNavigateToQuestion) {
                                // #region agent log
                                debugLog("LeaderboardVM.observeRoom", "ACTIVE - emitting NavigateToQuestion (status changed)", mapOf(
                                    "roomId" to roomId
                                ), "C")
                                // #endregion
                                _uiEffect.emit(LeaderboardUiEffect.NavigateToQuestion(roomId))
                            }
                            // #region agent log
                            else {
                                debugLog("LeaderboardVM.observeRoom", "ACTIVE - SKIPPED (already processed)", mapOf(
                                    "roomId" to roomId
                                ), "C")
                            }
                            // #endregion
                        }
                        RoomStatus.FINISHED -> {
                            if (shouldNavigateToWinners) {
                                // #region agent log
                                debugLog("LeaderboardVM.observeRoom", "FINISHED - emitting NavigateToWinners (status changed)", mapOf(
                                    "roomId" to roomId
                                ), "A")
                                // #endregion
                                _uiEffect.emit(LeaderboardUiEffect.NavigateToWinners(roomId))
                            }
                        }
                        else -> { }
                    }
                }
        }
    }

    private fun nextQuestion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentIndex = _uiState.value.room?.currentQuestionIndex ?: 0
            val result = leaderboardUseCase.nextQuestion(roomId, currentIndex)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(LeaderboardUiEffect.NavigateToHostControl(roomId))
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun endQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = leaderboardUseCase.endQuiz(roomId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(LeaderboardUiEffect.NavigateToWinners(roomId))
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!isHost) {
            viewModelScope.launch {
                quizSessionUseCase.unsubscribeFromRoom(roomId)
            }
        }
    }
}

data class LeaderboardUiState(
    val room: QuizRoom? = null,
    val topParticipants: List<Participant> = emptyList(),
    val allParticipants: List<Participant> = emptyList(),
    val isHost: Boolean = false,
    val isLastQuestion: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LeaderboardUiEvent {
    data object NextQuestionClicked : LeaderboardUiEvent()
    data object EndQuizClicked : LeaderboardUiEvent()
}

sealed class LeaderboardUiEffect {
    data class NavigateToHostControl(val roomId: String) : LeaderboardUiEffect()
    data class NavigateToQuestion(val roomId: String) : LeaderboardUiEffect()
    data class NavigateToWinners(val roomId: String) : LeaderboardUiEffect()
}
