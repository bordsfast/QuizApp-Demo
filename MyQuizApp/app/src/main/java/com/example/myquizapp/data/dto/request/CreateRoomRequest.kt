package com.example.myquizapp.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * CreateRoomRequest.kt - DTO for creating a new quiz room
 */
@Serializable
data class CreateRoomRequest(
    @SerialName("room_code")
    val roomCode: String,
    @SerialName("host_id")
    val hostId: String,
    val status: String = "waiting",
    @SerialName("current_question_index")
    val currentQuestionIndex: Int = -1
)
