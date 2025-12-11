package com.example.myquizapp.user_interface.RoleSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.domain.HostLobby.HostLobbyUseCase
import com.example.myquizapp.domain.RoleSelection.UserRole
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
 * RoleSelectionViewModel.kt - ViewModel for role selection screen
 * Follows MVI pattern with UiState, UiEvent, and UiEffect
 */
@HiltViewModel
class RoleSelectionViewModel @Inject constructor(
    private val hostLobbyUseCase: HostLobbyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoleSelectionUiState())
    val uiState: StateFlow<RoleSelectionUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<RoleSelectionUiEffect>()
    val uiEffect: SharedFlow<RoleSelectionUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: RoleSelectionUiEvent) {
        when (event) {
            is RoleSelectionUiEvent.CreateQuizClicked -> createQuiz()
            is RoleSelectionUiEvent.JoinQuizClicked -> joinQuiz()
        }
    }

    private fun createQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = hostLobbyUseCase.createRoom()
            result.fold(
                onSuccess = { room ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(RoleSelectionUiEffect.NavigateToHostLobby(room.id))
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message ?: "Failed to create room") 
                    }
                }
            )
        }
    }

    private fun joinQuiz() {
        viewModelScope.launch {
            _uiEffect.emit(RoleSelectionUiEffect.NavigateToParticipantJoin)
        }
    }
}

data class RoleSelectionUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RoleSelectionUiEvent {
    data object CreateQuizClicked : RoleSelectionUiEvent()
    data object JoinQuizClicked : RoleSelectionUiEvent()
}

sealed class RoleSelectionUiEffect {
    data class NavigateToHostLobby(val roomId: String) : RoleSelectionUiEffect()
    data object NavigateToParticipantJoin : RoleSelectionUiEffect()
}
