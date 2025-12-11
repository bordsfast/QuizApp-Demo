package com.example.myquizapp.user_interface.HostLobby

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myquizapp.navigation.NavigationProvider
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950
import com.example.myquizapp.user_interface.common.ParticipantAvatar
import com.example.myquizapp.user_interface.common.QuizButton
import com.example.myquizapp.user_interface.common.RoomCodeDisplay
import android.util.Log

// #region agent log
private const val DEBUG_TAG = "QUIZ_DEBUG"
private fun debugLog(location: String, message: String, data: Map<String, Any?>, hypothesisId: String) {
    Log.d(DEBUG_TAG, "[$hypothesisId] $location: $message | data=$data")
}
// #endregion

/**
 * HostLobbyScreen.kt - Host lobby showing room code and participants
 */
@Composable
fun HostLobbyScreen(
    roomId: String,
    navigationProvider: NavigationProvider,
    viewModel: HostLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(roomId) {
        viewModel.initialize(roomId)
    }

    LaunchedEffect(Unit) {
        // #region agent log
        debugLog("HostLobbyScreen.kt:54", "Effect collector started", mapOf("roomId" to roomId), "C")
        // #endregion
        viewModel.uiEffect.collect { effect ->
            // #region agent log
            debugLog("HostLobbyScreen.kt:57", "Effect received", mapOf("effectType" to effect::class.simpleName), "C")
            // #endregion
            when (effect) {
                is HostLobbyUiEffect.NavigateToQuizControl -> {
                    // #region agent log
                    debugLog("HostLobbyScreen.kt:62", "Navigating to HostQuizControl", mapOf("roomId" to effect.roomId), "D")
                    // #endregion
                    navigationProvider.navigateToHostQuizControl(effect.roomId)
                    // #region agent log
                    debugLog("HostLobbyScreen.kt:66", "Navigation call completed", mapOf("roomId" to effect.roomId), "D")
                    // #endregion
                }
                is HostLobbyUiEffect.ShareRoomCode -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Join my quiz! Room code: ${effect.code}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share room code"))
                }
            }
        }
    }

    HostLobbyContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HostLobbyContent(
    uiState: HostLobbyUiState,
    onEvent: (HostLobbyUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        HostLobbyBodyContent(
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HostLobbyBodyContent(
    uiState: HostLobbyUiState,
    onEvent: (HostLobbyUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Room Code Display
        uiState.room?.let { room ->
            RoomCodeDisplay(
                roomCode = room.roomCode,
                onShareClick = { onEvent(HostLobbyUiEvent.ShareClicked) }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Players Ready count
        Text(
            text = "Players Ready: ${uiState.participants.size}",
            color = White50,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Participants grid
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                        size = 56.dp,
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
        // Start Quiz Button
        // #region agent log
        val isButtonEnabled = uiState.participants.isNotEmpty() && !uiState.isLoading
        debugLog("HostLobbyScreen.kt:173", "Button render state", mapOf(
            "isButtonEnabled" to isButtonEnabled,
            "participantsCount" to uiState.participants.size,
            "isLoading" to uiState.isLoading,
            "error" to uiState.error
        ), "A")
        // #endregion
        QuizButton(
            text = "START QUIZ ▶",
            onClick = {
                // #region agent log
                debugLog("HostLobbyScreen.kt:184", "START QUIZ button clicked", mapOf("participantsCount" to uiState.participants.size), "E")
                // #endregion
                onEvent(HostLobbyUiEvent.StartQuizClicked)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}
