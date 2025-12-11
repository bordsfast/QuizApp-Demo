package com.example.myquizapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Answer.kt - Data class representing a participant's answer
 */
@Serializable
data class Answer(
    val id: String = "",
    @SerialName("room_id")
    val roomId: String = "",
    @SerialName("participant_id")
    val participantId: String = "",
    @SerialName("question_index")
    val questionIndex: Int = 0,
    @SerialName("answer_index")
    val answerIndex: Int = 0,
    @SerialName("is_correct")
    val isCorrect: Boolean = false,
    @SerialName("answered_at")
    val answeredAt: String = "",
    @SerialName("points_earned")
    val pointsEarned: Int = 0
)
