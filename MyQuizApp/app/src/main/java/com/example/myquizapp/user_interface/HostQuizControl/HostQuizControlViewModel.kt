package com.example.myquizapp.user_interface.HostQuizControl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.fixed.QuizQuestions
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
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

/**
 * HostQuizControlViewModel.kt - ViewModel for host quiz control screen
 */
@HiltViewModel
class HostQuizControlViewModel @Inject constructor(
    private val quizSessionUseCase: QuizSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostQuizControlUiState())
    val uiState: StateFlow<HostQuizControlUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HostQuizControlUiEffect>()
    val uiEffect: SharedFlow<HostQuizControlUiEffect> = _uiEffect.asSharedFlow()

    private var roomId: String = ""

    fun initialize(roomId: String) {
        this.roomId = roomId
        loadRoom()
        observeRoom()
        observeParticipants()
    }

    fun onEvent(event: HostQuizControlUiEvent) {
        when (event) {
            is HostQuizControlUiEvent.NextQuestionClicked -> showLeaderboardOrNextQuestion()
            is HostQuizControlUiEvent.EndQuizClicked -> endQuiz()
        }
    }

    private fun loadRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = quizSessionUseCase.getRoomById(roomId)
            result.fold(
                onSuccess = { room ->
                    val question = quizSessionUseCase.getQuestion(room.currentQuestionIndex)
                    val isLast = quizSessionUseCase.isLastQuestion(room.currentQuestionIndex)
                    _uiState.update { 
                        it.copy(
                            room = room, 
                            currentQuestion = question,
                            isLastQuestion = isLast,
                            isLoading = false
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message) 
                    }
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
                    val question = quizSessionUseCase.getQuestion(room.currentQuestionIndex)
                    val isLast = quizSessionUseCase.isLastQuestion(room.currentQuestionIndex)
                    _uiState.update { 
                        it.copy(
                            room = room,
                            currentQuestion = question,
                            isLastQuestion = isLast
                        )
                    }
                    checkRoomStatus(room)
                }
        }
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            quizSessionUseCase.observeParticipants(roomId)
                .catch { }
                .collect { participants ->
                    _uiState.update { it.copy(participants = participants.sortedByDescending { p -> p.score }) }
                }
        }
    }

    private fun checkRoomStatus(room: QuizRoom) {
        viewModelScope.launch {
            when (room.status) {
                RoomStatus.SHOWING_LEADERBOARD -> {
                    _uiEffect.emit(HostQuizControlUiEffect.NavigateToLeaderboard(roomId))
                }
                RoomStatus.FINISHED -> {
                    _uiEffect.emit(HostQuizControlUiEffect.NavigateToWinners(roomId))
                }
                else -> { }
            }
        }
    }

    private fun showLeaderboardOrNextQuestion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = quizSessionUseCase.showLeaderboard(roomId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
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
            val result = quizSessionUseCase.endQuiz(roomId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            quizSessionUseCase.unsubscribeFromRoom(roomId)
        }
    }
}

data class HostQuizControlUiState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val participants: List<Participant> = emptyList(),
    val isLastQuestion: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HostQuizControlUiEvent {
    data object NextQuestionClicked : HostQuizControlUiEvent()
    data object EndQuizClicked : HostQuizControlUiEvent()
}

sealed class HostQuizControlUiEffect {
    data class NavigateToLeaderboard(val roomId: String) : HostQuizControlUiEffect()
    data class NavigateToWinners(val roomId: String) : HostQuizControlUiEffect()
}
