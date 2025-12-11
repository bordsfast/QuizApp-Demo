package com.example.myquizapp.user_interface.ParticipantQuestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.myquizapp.user_interface.common.AnswerOptionCard
import com.example.myquizapp.user_interface.common.SpeedBonusIndicator
import com.example.myquizapp.user_interface.common.TimerBar
// #region agent log
import android.util.Log
private const val DEBUG_TAG = "QUIZ_DEBUG_SCREEN"
// #endregion

/**
 * ParticipantQuestionScreen.kt - Participant answers questions
 */
@Composable
fun ParticipantQuestionScreen(
    roomId: String,
    participantId: String,
    navigationProvider: NavigationProvider,
    viewModel: ParticipantQuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId, participantId) {
        viewModel.initialize(roomId, participantId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            // #region agent log
            Log.d(DEBUG_TAG, "ParticipantQuestionScreen effect received: $effect")
            // #endregion
            when (effect) {
                is ParticipantQuestionUiEffect.NavigateToLeaderboard -> {
                    // #region agent log
                    Log.d(DEBUG_TAG, "Navigating to Leaderboard: roomId=${effect.roomId}")
                    // #endregion
                    navigationProvider.navigateToLeaderboard(effect.roomId, isHost = false)
                }
                is ParticipantQuestionUiEffect.NavigateToWinners -> {
                    // #region agent log
                    Log.d(DEBUG_TAG, "Navigating to Winners: roomId=${effect.roomId}")
                    // #endregion
                    navigationProvider.navigateToWinners(effect.roomId, isHost = false)
                }
            }
        }
    }

    ParticipantQuestionContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ParticipantQuestionContent(
    uiState: ParticipantQuestionUiState,
    onEvent: (ParticipantQuestionUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        ParticipantQuestionBodyContent(
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
private fun ParticipantQuestionBodyContent(
    uiState: ParticipantQuestionUiState,
    onEvent: (ParticipantQuestionUiEvent) -> Unit
) {
    // #region agent log
    LaunchedEffect(uiState.currentQuestion, uiState.isLoading) {
        Log.d(DEBUG_TAG, "ParticipantQuestionBodyContent rendering: currentQuestion=${uiState.currentQuestion?.text?.take(20)}, isLoading=${uiState.isLoading}, hasAnswered=${uiState.hasAnswered}, roomStatus=${uiState.room?.status?.name}")
    }
    // #endregion
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer Bar
        TimerBar(
            remainingTimeMillis = uiState.remainingTimeMillis,
            totalTimeMillis = ParticipantQuestionViewModel.QUESTION_TIME_LIMIT_MILLIS,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Question Card
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
            Spacer(modifier = Modifier.height(24.dp))
            // Answer Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.options.forEachIndexed { index, option ->
                    AnswerOptionCard(
                        text = option,
                        onClick = { onEvent(ParticipantQuestionUiEvent.AnswerSelected(index)) },
                        isSelected = uiState.selectedAnswerIndex == index,
                        isCorrect = if (uiState.hasAnswered && uiState.selectedAnswerIndex == index) {
                            uiState.lastAnswerCorrect
                        } else if (uiState.hasAnswered && index == question.correctAnswerIndex) {
                            true
                        } else null,
                        isEnabled = !uiState.hasAnswered
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        // Speed Bonus Indicator
        SpeedBonusIndicator(
            points = uiState.lastPointsEarned,
            isVisible = uiState.hasAnswered && uiState.lastAnswerCorrect == true,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Waiting message after answering
        if (uiState.hasAnswered) {
            Text(
                text = if (uiState.lastAnswerCorrect == true) {
                    "Correct! Waiting for next question..."
                } else if (uiState.lastAnswerCorrect == false) {
                    "Incorrect. Waiting for next question..."
                } else {
                    "Time's up! Waiting for next question..."
                },
                color = White950,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
