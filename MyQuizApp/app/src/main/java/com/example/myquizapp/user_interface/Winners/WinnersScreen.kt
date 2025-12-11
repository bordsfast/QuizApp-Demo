package com.example.myquizapp.user_interface.Winners

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myquizapp.navigation.NavigationProvider
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.BrandAccent
import com.example.myquizapp.theme.White50
import com.example.myquizapp.user_interface.common.PodiumView
import com.example.myquizapp.user_interface.common.QuizButton

/**
 * WinnersScreen.kt - Final results screen showing top 3 winners
 */
@Composable
fun WinnersScreen(
    roomId: String,
    isHost: Boolean,
    navigationProvider: NavigationProvider,
    viewModel: WinnersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId, isHost) {
        viewModel.initialize(roomId, isHost)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WinnersUiEffect.NavigateToHome -> {
                    navigationProvider.navigateToHome()
                }
            }
        }
    }

    WinnersContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun WinnersContent(
    uiState: WinnersUiState,
    onEvent: (WinnersUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        WinnersBodyContent(
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
private fun WinnersBodyContent(
    uiState: WinnersUiState,
    onEvent: (WinnersUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        // Trophy/Title
        Text(
            text = "🏆",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Congratulations!",
            color = BrandAccent,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(48.dp))
        // Podium View
        PodiumView(
            firstPlace = uiState.firstPlace,
            secondPlace = uiState.secondPlace,
            thirdPlace = uiState.thirdPlace,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        // Exit Button
        QuizButton(
            text = "EXIT TO HOME",
            onClick = { onEvent(WinnersUiEvent.ExitToHomeClicked) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}
