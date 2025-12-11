package com.example.myquizapp.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * UpdateRoomStatusRequest.kt - DTO for updating room status
 */
@Serializable
data class UpdateRoomStatusRequest(
    val status: String,
    @SerialName("current_question_index")
    val currentQuestionIndex: Int? = null,
    @SerialName("question_started_at")
    val questionStartedAt: String? = null
)
