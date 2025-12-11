package com.example.myquizapp.domain.ParticipantLobby

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom

/**
 * ParticipantLobbyModel.kt - Domain models for participant lobby
 */
data class ParticipantLobbyState(
    val room: QuizRoom? = null,
    val participants: List<Participant> = emptyList(),
    val currentParticipantId: String = ""
)

sealed class ParticipantLobbyStatus {
    data object Waiting : ParticipantLobbyStatus()
    data object QuizStarted : ParticipantLobbyStatus()
    data object RoomClosed : ParticipantLobbyStatus()
}

