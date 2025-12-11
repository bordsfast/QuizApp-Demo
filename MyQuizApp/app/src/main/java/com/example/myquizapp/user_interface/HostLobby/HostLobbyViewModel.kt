package com.example.myquizapp.user_interface.HostLobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.domain.HostLobby.HostLobbyUseCase
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
import android.util.Log

// #region agent log
private const val DEBUG_TAG = "QUIZ_DEBUG"
private fun debugLog(location: String, message: String, data: Map<String, Any?>, hypothesisId: String) {
    Log.d(DEBUG_TAG, "[$hypothesisId] $location: $message | data=$data")
}
// #endregion

/**
 * HostLobbyViewModel.kt - ViewModel for host lobby screen
 */
@HiltViewModel
class HostLobbyViewModel @Inject constructor(
    private val hostLobbyUseCase: HostLobbyUseCase,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostLobbyUiState())
    val uiState: StateFlow<HostLobbyUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HostLobbyUiEffect>()
    val uiEffect: SharedFlow<HostLobbyUiEffect> = _uiEffect.asSharedFlow()

    private var roomId: String = ""

    fun initialize(roomId: String) {
        this.roomId = roomId
        loadRoom()
        loadParticipants()
        observeParticipants()
    }

    fun onEvent(event: HostLobbyUiEvent) {
        // #region agent log
        debugLog("HostLobbyViewModel.kt:62", "onEvent called", mapOf("event" to event::class.simpleName), "E")
        // #endregion
        when (event) {
            is HostLobbyUiEvent.StartQuizClicked -> startQuiz()
            is HostLobbyUiEvent.ShareClicked -> shareRoomCode()
            is HostLobbyUiEvent.RefreshParticipants -> loadParticipants()
        }
    }

    private fun loadRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = quizRepository.getRoomById(roomId)
            result.fold(
                onSuccess = { room ->
                    _uiState.update { it.copy(room = room, isLoading = false) }
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
            val result = hostLobbyUseCase.getParticipants(roomId)
            result.fold(
                onSuccess = { participants ->
                    _uiState.update { it.copy(participants = participants) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            hostLobbyUseCase.subscribeToParticipants(roomId)
            hostLobbyUseCase.observeParticipants(roomId)
                .catch { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
                .collect { participants ->
                    _uiState.update { it.copy(participants = participants) }
                }
        }
    }

    private fun startQuiz() {
        // #region agent log
        debugLog("HostLobbyViewModel.kt:116", "startQuiz called", mapOf("roomId" to roomId), "B")
        // #endregion
        viewModelScope.launch {
            // #region agent log
            debugLog("HostLobbyViewModel.kt:120", "startQuiz coroutine started", mapOf("roomId" to roomId), "B")
            // #endregion
            _uiState.update { it.copy(isLoading = true) }
            val result = hostLobbyUseCase.startQuiz(roomId)
            // #region agent log
            debugLog("HostLobbyViewModel.kt:125", "startQuiz result received", mapOf(
                "isSuccess" to result.isSuccess,
                "isFailure" to result.isFailure,
                "errorMessage" to result.exceptionOrNull()?.message
            ), "B")
            // #endregion
            result.fold(
                onSuccess = {
                    // #region agent log
                    debugLog("HostLobbyViewModel.kt:134", "startQuiz success, emitting effect", mapOf("roomId" to roomId), "C")
                    // #endregion
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(HostLobbyUiEffect.NavigateToQuizControl(roomId))
                    // #region agent log
                    debugLog("HostLobbyViewModel.kt:139", "Effect emitted successfully", mapOf("roomId" to roomId), "C")
                    // #endregion
                },
                onFailure = { error ->
                    // #region agent log
                    debugLog("HostLobbyViewModel.kt:144", "startQuiz failure", mapOf(
                        "errorMessage" to error.message,
                        "errorType" to error::class.simpleName
                    ), "B")
                    // #endregion
                    _uiState.update { 
                        it.copy(isLoading = false, error = error.message ?: "Failed to start quiz") 
                    }
                }
            )
        }
    }

    private fun shareRoomCode() {
        viewModelScope.launch {
            _uiState.value.room?.roomCode?.let { code ->
                _uiEffect.emit(HostLobbyUiEffect.ShareRoomCode(code))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            hostLobbyUseCase.unsubscribeFromParticipants(roomId)
        }
    }
}

data class HostLobbyUiState(
    val room: QuizRoom? = null,
    val participants: List<Participant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HostLobbyUiEvent {
    data object StartQuizClicked : HostLobbyUiEvent()
    data object ShareClicked : HostLobbyUiEvent()
    data object RefreshParticipants : HostLobbyUiEvent()
}

sealed class HostLobbyUiEffect {
    data class NavigateToQuizControl(val roomId: String) : HostLobbyUiEffect()
    data class ShareRoomCode(val code: String) : HostLobbyUiEffect()
}
