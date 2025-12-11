package com.example.myquizapp.domain.ParticipantJoin

import com.example.myquizapp.data.model.Participant

/**
 * ParticipantJoinModel.kt - Domain models for participant join
 */
data class ParticipantJoinState(
    val roomCode: String = "",
    val participantName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val joinedParticipant: Participant? = null
)
