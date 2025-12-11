package com.example.myquizapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Participant.kt - Data class representing a quiz participant
 */
@Serializable
data class Participant(
    val id: String = "",
    @SerialName("room_id")
    val roomId: String = "",
    val name: String = "",
    val score: Int = 0,
    @SerialName("joined_at")
    val joinedAt: String = ""
)
