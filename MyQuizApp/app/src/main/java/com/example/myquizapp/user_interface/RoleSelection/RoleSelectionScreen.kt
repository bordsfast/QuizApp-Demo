package com.example.myquizapp.user_interface.RoleSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.myquizapp.theme.Brand700
import com.example.myquizapp.theme.BrandAccent
import com.example.myquizapp.theme.Red600
import com.example.myquizapp.theme.White50
import com.example.myquizapp.user_interface.common.QuizButton
import com.example.myquizapp.user_interface.common.QuizOutlinedButton

/**
 * RoleSelectionScreen.kt - Start screen where user chooses Host or Participant
 * Follows 3-section pattern: Screen() → Content() → BodyContent()
 */
@Composable
fun RoleSelectionScreen(
    navigationProvider: NavigationProvider,
    viewModel: RoleSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RoleSelectionUiEffect.NavigateToHostLobby -> {
                    navigationProvider.navigateToHostLobby(effect.roomId)
                }
                is RoleSelectionUiEffect.NavigateToParticipantJoin -> {
                    navigationProvider.navigateTo(Screen.ParticipantJoin)
                }
            }
        }
    }

    RoleSelectionContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun RoleSelectionContent(
    uiState: RoleSelectionUiState,
    onEvent: (RoleSelectionUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black300)
    ) {
        RoleSelectionBodyContent(
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
private fun RoleSelectionBodyContent(
    uiState: RoleSelectionUiState,
    onEvent: (RoleSelectionUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo/Title
        Text(
            text = "MyQuiz",
            color = White50,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Live",
            color = BrandAccent,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(80.dp))
        // Create Quiz Button (Host)
        QuizButton(
            text = "Create a Quiz",
            onClick = { onEvent(RoleSelectionUiEvent.CreateQuizClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )
        Text(
            text = "(Host)",
            color = White50.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Join Quiz Button (Participant)
        QuizOutlinedButton(
            text = "Join a Quiz",
            onClick = { onEvent(RoleSelectionUiEvent.JoinQuizClicked) },
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
    }
}
