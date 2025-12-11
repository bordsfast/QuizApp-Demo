package com.example.myquizapp.user_interface.ParticipantLobby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myquizapp.navigation.NavigationProvider
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950
import com.example.myquizapp.user_interface.common.ParticipantAvatar

/**
 * ParticipantLobbyScreen.kt - Waiting room for participants
 */
@Composable
fun ParticipantLobbyScreen(
    roomId: String,
    participantId: String,
    navigationProvider: NavigationProvider,
    viewModel: ParticipantLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId, participantId) {
        viewModel.initialize(roomId, participantId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ParticipantLobbyUiEffect.NavigateToQuestion -> {
                    navigationProvider.navigateToParticipantQuestion(
                        roomId = effect.roomId,
                        participantId = effect.participantId
                    )
                }
            }
        }
    }

    ParticipantLobbyContent(uiState = uiState)
}

@Composable
private fun ParticipantLobbyContent(
    uiState: ParticipantLobbyUiState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        ParticipantLobbyBodyContent(uiState = uiState)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParticipantLobbyBodyContent(
    uiState: ParticipantLobbyUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        // Room Code
        uiState.room?.let { room ->
            Text(
                text = "Room ${room.roomCode}",
                color = White50,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Waiting message
        Text(
            text = "Waiting for host to start...",
            color = White950,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        CircularProgressIndicator(
            color = Brand400,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Players count
        Text(
            text = "Players Ready: ${uiState.participants.size}",
            color = White50,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Participants grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Top
        ) {
            uiState.participants.forEachIndexed { index, participant ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    ParticipantAvatar(
                        name = participant.name,
                        size = 48.dp,
                        colorIndex = index
                    )
                    Text(
                        text = participant.name,
                        color = White950,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
