package com.example.myquizapp.user_interface.ParticipantJoin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myquizapp.navigation.NavigationProvider
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.Red600
import com.example.myquizapp.user_interface.common.QuizButton
import com.example.myquizapp.user_interface.common.QuizTextField

/**
 * ParticipantJoinScreen.kt - Screen for participants to join a quiz
 */
@Composable
fun ParticipantJoinScreen(
    navigationProvider: NavigationProvider,
    viewModel: ParticipantJoinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ParticipantJoinUiEffect.NavigateToParticipantLobby -> {
                    navigationProvider.navigateToParticipantLobby(
                        roomId = effect.roomId,
                        participantId = effect.participantId
                    )
                }
            }
        }
    }

    ParticipantJoinContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ParticipantJoinContent(
    uiState: ParticipantJoinUiState,
    onEvent: (ParticipantJoinUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        ParticipantJoinBodyContent(
            uiState = uiState,
            onEvent = onEvent
        )
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black300.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Brand400)
            }
        }
    }
}

@Composable
private fun ParticipantJoinBodyContent(
    uiState: ParticipantJoinUiState,
    onEvent: (ParticipantJoinUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        // Room Code Input
        QuizTextField(
            value = uiState.roomCode,
            onValueChange = { onEvent(ParticipantJoinUiEvent.RoomCodeChanged(it)) },
            placeholder = "Enter Room Code",
            leadingIcon = Icons.Default.Lock,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Characters,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Name Input
        QuizTextField(
            value = uiState.participantName,
            onValueChange = { onEvent(ParticipantJoinUiEvent.NameChanged(it)) },
            placeholder = "Choose your name",
            leadingIcon = Icons.Default.Person,
            capitalization = KeyboardCapitalization.Words,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Join Button
        QuizButton(
            text = "JOIN LOBBY",
            onClick = { onEvent(ParticipantJoinUiEvent.JoinLobbyClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )
        // Error message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = error,
                color = Red600,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
