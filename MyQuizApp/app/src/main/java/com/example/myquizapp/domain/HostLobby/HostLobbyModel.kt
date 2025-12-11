package com.example.myquizapp.domain.HostLobby

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom

/**
 * HostLobbyModel.kt - Domain models for host lobby
 */
data class HostLobbyState(
    val room: QuizRoom? = null,
    val participants: List<Participant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
