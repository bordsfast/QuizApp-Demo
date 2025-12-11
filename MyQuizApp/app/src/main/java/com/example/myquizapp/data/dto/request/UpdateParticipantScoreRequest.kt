package com.example.myquizapp.data.dto.request

import kotlinx.serialization.Serializable

/**
 * UpdateParticipantScoreRequest.kt - DTO for updating participant score
 */
@Serializable
data class UpdateParticipantScoreRequest(
    val score: Int
)
