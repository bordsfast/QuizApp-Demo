package com.example.myquizapp.user_interface.Leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.myquizapp.navigation.Screen
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950
import com.example.myquizapp.user_interface.common.QuizButton
import com.example.myquizapp.user_interface.common.TopThreeStandings
// #region agent log
import android.util.Log
private const val DEBUG_TAG = "QUIZ_DEBUG_SCREEN"
// #endregion

/**
 * LeaderboardScreen.kt - Shows current rankings after each question
 */
@Composable
fun LeaderboardScreen(
    roomId: String,
    isHost: Boolean,
    navigationProvider: NavigationProvider,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId, isHost) {
        viewModel.initialize(roomId, isHost)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            // #region agent log
            Log.d(DEBUG_TAG, "LeaderboardScreen effect received: $effect")
            // #endregion
            when (effect) {
                is LeaderboardUiEffect.NavigateToHostControl -> {
                    // #region agent log
                    Log.d(DEBUG_TAG, "Navigating to HostQuizControl: roomId=${effect.roomId}")
                    // #endregion
                    navigationProvider.navigateToHostQuizControl(effect.roomId)
                }
                is LeaderboardUiEffect.NavigateToQuestion -> {
                    // #region agent log
                    Log.d(DEBUG_TAG, "Navigating BACK to Question (navigateBack called)")
                    // #endregion
                    navigationProvider.navigateBack()
                }
                is LeaderboardUiEffect.NavigateToWinners -> {
                    // #region agent log
                    Log.d(DEBUG_TAG, "Navigating to Winners: roomId=${effect.roomId}, isHost=$isHost")
                    // #endregion
                    navigationProvider.navigateToWinners(effect.roomId, isHost)
                }
            }
        }
    }

    LeaderboardContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun LeaderboardContent(
    uiState: LeaderboardUiState,
    onEvent: (LeaderboardUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        LeaderboardBodyContent(
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
private fun LeaderboardBodyContent(
    uiState: LeaderboardUiState,
    onEvent: (LeaderboardUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Title
        Text(
            text = "Top Standings",
            color = White50,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "(Question & Reach)",
            color = White950,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Top 3 Standings
        TopThreeStandings(
            participants = uiState.topParticipants,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        // Host controls
        if (uiState.isHost) {
            Text(
                text = "Host",
                color = White950,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (uiState.isLastQuestion) {
                QuizButton(
                    text = "SHOW WINNERS ▶",
                    onClick = { onEvent(LeaderboardUiEvent.EndQuizClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )
            } else {
                QuizButton(
                    text = "NEXT QUESTION ▶",
                    onClick = { onEvent(LeaderboardUiEvent.NextQuestionClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )
            }
        } else {
            // Participant waiting
            Text(
                text = "Waiting for host...",
                color = White950,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            CircularProgressIndicator(
                color = Brand400,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
