package com.example.myquizapp.user_interface.HostQuizControl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myquizapp.data.fixed.QuizQuestions
import com.example.myquizapp.navigation.NavigationProvider
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Black500
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950
import com.example.myquizapp.user_interface.common.LeaderboardItem
import com.example.myquizapp.user_interface.common.QuizButton

/**
 * HostQuizControlScreen.kt - Host controls quiz flow
 */
@Composable
fun HostQuizControlScreen(
    roomId: String,
    navigationProvider: NavigationProvider,
    viewModel: HostQuizControlViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId) {
        viewModel.initialize(roomId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HostQuizControlUiEffect.NavigateToLeaderboard -> {
                    navigationProvider.navigateToLeaderboard(effect.roomId, isHost = true)
                }
                is HostQuizControlUiEffect.NavigateToWinners -> {
                    navigationProvider.navigateToWinners(effect.roomId, isHost = true)
                }
            }
        }
    }

    HostQuizControlContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HostQuizControlContent(
    uiState: HostQuizControlUiState,
    onEvent: (HostQuizControlUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        HostQuizControlBodyContent(
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
private fun HostQuizControlBodyContent(
    uiState: HostQuizControlUiState,
    onEvent: (HostQuizControlUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Current Question Card
        uiState.currentQuestion?.let { question ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Black500)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Question ${question.index + 1}/${QuizQuestions.TOTAL_QUESTIONS}:",
                        color = White950,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.text,
                        color = White50,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Live Rankings Title
        Text(
            text = "Live Rankings",
            color = White50,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Participants List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(uiState.participants) { index, participant ->
                LeaderboardItem(
                    participant = participant,
                    rank = index + 1
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Action Button
        if (uiState.isLastQuestion) {
            QuizButton(
                text = "END QUIZ",
                onClick = { onEvent(HostQuizControlUiEvent.EndQuizClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        } else {
            QuizButton(
                text = "SHOW STANDINGS ▶",
                onClick = { onEvent(HostQuizControlUiEvent.NextQuestionClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        }
    }
}
