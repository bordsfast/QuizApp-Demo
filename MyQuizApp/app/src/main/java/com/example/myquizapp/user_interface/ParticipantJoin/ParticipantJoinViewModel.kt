package com.example.myquizapp.user_interface.ParticipantJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.domain.ParticipantJoin.ParticipantJoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ParticipantJoinViewModel.kt - ViewModel for participant join screen
 */
@HiltViewModel
class ParticipantJoinViewModel @Inject constructor(
    private val participantJoinUseCase: ParticipantJoinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParticipantJoinUiState())
    val uiState: StateFlow<ParticipantJoinUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ParticipantJoinUiEffect>()
    val uiEffect: SharedFlow<ParticipantJoinUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: ParticipantJoinUiEvent) {
        when (event) {
            is ParticipantJoinUiEvent.RoomCodeChanged -> {
                _uiState.update { it.copy(roomCode = event.code.uppercase(), error = null) }
            }
            is ParticipantJoinUiEvent.NameChanged -> {
                _uiState.update { it.copy(participantName = event.name, error = null) }
            }
            is ParticipantJoinUiEvent.JoinLobbyClicked -> joinLobby()
        }
    }

    private fun joinLobby() {
        val roomCode = _uiState.value.roomCode.trim()
        val name = _uiState.value.participantName.trim()

        if (roomCode.isEmpty()) {
            _uiState.update { it.copy(error = "Please enter a room code") }
            return
        }
        if (name.isEmpty()) {
            _uiState.update { it.copy(error = "Please enter your name") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = participantJoinUseCase.joinRoom(roomCode, name)
            result.fold(
                onSuccess = { participant ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(
                        ParticipantJoinUiEffect.NavigateToParticipantLobby(
                            roomId = participant.roomId,
                            participantId = participant.id
                        )
                    )
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message ?: "Failed to join room") 
                    }
                }
            )
        }
    }
}

data class ParticipantJoinUiState(
    val roomCode: String = "",
    val participantName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ParticipantJoinUiEvent {
    data class RoomCodeChanged(val code: String) : ParticipantJoinUiEvent()
    data class NameChanged(val name: String) : ParticipantJoinUiEvent()
    data object JoinLobbyClicked : ParticipantJoinUiEvent()
}

sealed class ParticipantJoinUiEffect {
    data class NavigateToParticipantLobby(
        val roomId: String,
        val participantId: String
    ) : ParticipantJoinUiEffect()
}
