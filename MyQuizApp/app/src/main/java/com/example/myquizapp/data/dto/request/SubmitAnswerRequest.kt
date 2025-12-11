package com.example.myquizapp.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * SubmitAnswerRequest.kt - DTO for submitting an answer to a question
 */
@Serializable
data class SubmitAnswerRequest(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("participant_id")
    val participantId: String,
    @SerialName("question_index")
    val questionIndex: Int,
    @SerialName("answer_index")
    val answerIndex: Int,
    @SerialName("is_correct")
    val isCorrect: Boolean,
    @SerialName("points_earned")
    val pointsEarned: Int
)
