package com.example.myquizapp.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * UpdateCurrentQuestionRequest.kt - DTO for updating current question
 */
@Serializable
data class UpdateCurrentQuestionRequest(
    @SerialName("current_question_index")
    val currentQuestionIndex: Int,
    @SerialName("question_started_at")
    val questionStartedAt: String
)
