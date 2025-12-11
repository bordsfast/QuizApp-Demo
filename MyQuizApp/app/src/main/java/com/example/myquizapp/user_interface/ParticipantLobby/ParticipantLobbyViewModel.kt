package com.example.myquizapp.user_interface.ParticipantLobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import com.example.myquizapp.domain.QuizSession.QuizSessionUseCase
import com.example.myquizapp.repository.QuizRepository
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
 * ParticipantLobbyViewModel.kt - ViewModel for participant waiting room
 */
@HiltViewModel
class ParticipantLobbyViewModel @Inject constructor(
    private val quizSessionUseCase: QuizSessionUseCase,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParticipantLobbyUiState())
    val uiState: StateFlow<ParticipantLobbyUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ParticipantLobbyUiEffect>()
    val uiEffect: SharedFlow<ParticipantLobbyUiEffect> = _uiEffect.asSharedFlow()

    private var roomId: String = ""
    private var participantId: String = ""

    fun initialize(roomId: String, participantId: String) {
        this.roomId = roomId
        this.participantId = participantId
        loadRoom()
        loadParticipants()
        observeRoom()
        observeParticipants()
    }

    private fun loadRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = quizSessionUseCase.getRoomById(roomId)
            result.fold(
                onSuccess = { room ->
                    _uiState.update { it.copy(room = room, isLoading = false) }
                    checkRoomStatus(room)
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message ?: "Failed to load room") 
                    }
                }
            )
        }
    }

    private fun loadParticipants() {
        viewModelScope.launch {
            val result = quizSessionUseCase.getParticipants(roomId)
            result.fold(
                onSuccess = { participants ->
                    _uiState.update { it.copy(participants = participants) }
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
                    _uiState.update { it.copy(room = room) }
                    checkRoomStatus(room)
                }
        }
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            quizRepository.subscribeToParticipants(roomId)
            quizSessionUseCase.observeParticipants(roomId)
                .catch { }
                .collect { participants ->
                    _uiState.update { it.copy(participants = participants) }
                }
        }
    }

    private fun checkRoomStatus(room: QuizRoom) {
        viewModelScope.launch {
            when (room.status) {
                RoomStatus.ACTIVE -> {
                    _uiEffect.emit(
                        ParticipantLobbyUiEffect.NavigateToQuestion(roomId, participantId)
                    )
                }
                else -> { }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            quizSessionUseCase.unsubscribeFromRoom(roomId)
            quizRepository.unsubscribeFromParticipants(roomId)
        }
    }
}

data class ParticipantLobbyUiState(
    val room: QuizRoom? = null,
    val participants: List<Participant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ParticipantLobbyUiEffect {
    data class NavigateToQuestion(
        val roomId: String,
        val participantId: String
    ) : ParticipantLobbyUiEffect()
}
