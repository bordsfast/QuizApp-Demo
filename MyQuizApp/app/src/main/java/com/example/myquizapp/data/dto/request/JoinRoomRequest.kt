package com.example.myquizapp.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JoinRoomRequest.kt - DTO for joining a quiz room as a participant
 */
@Serializable
data class JoinRoomRequest(
    @SerialName("room_id")
    val roomId: String,
    val name: String,
    val score: Int = 0
)
