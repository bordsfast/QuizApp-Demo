package com.example.myquizapp.user_interface.Winners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.domain.Winners.WinnersUseCase
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
 * WinnersViewModel.kt - ViewModel for winners screen
 */
@HiltViewModel
class WinnersViewModel @Inject constructor(
    private val winnersUseCase: WinnersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WinnersUiState())
    val uiState: StateFlow<WinnersUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<WinnersUiEffect>()
    val uiEffect: SharedFlow<WinnersUiEffect> = _uiEffect.asSharedFlow()

    fun initialize(roomId: String, isHost: Boolean) {
        _uiState.update { it.copy(isHost = isHost) }
        loadWinners(roomId)
    }

    fun onEvent(event: WinnersUiEvent) {
        when (event) {
            is WinnersUiEvent.ExitToHomeClicked -> exitToHome()
        }
    }

    private fun loadWinners(roomId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = winnersUseCase.getWinners(roomId)
            result.fold(
                onSuccess = { winners ->
                    _uiState.update { 
                        it.copy(
                            firstPlace = winners.getOrNull(0),
                            secondPlace = winners.getOrNull(1),
                            thirdPlace = winners.getOrNull(2),
                            isLoading = false
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun exitToHome() {
        viewModelScope.launch {
            _uiEffect.emit(WinnersUiEffect.NavigateToHome)
        }
    }
}

data class WinnersUiState(
    val firstPlace: Participant? = null,
    val secondPlace: Participant? = null,
    val thirdPlace: Participant? = null,
    val isHost: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class WinnersUiEvent {
    data object ExitToHomeClicked : WinnersUiEvent()
}

sealed class WinnersUiEffect {
    data object NavigateToHome : WinnersUiEffect()
}
