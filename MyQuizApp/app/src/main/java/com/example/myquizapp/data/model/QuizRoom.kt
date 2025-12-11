package com.example.myquizapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * QuizRoom.kt - Data class representing a quiz room
 */
@Serializable
data class QuizRoom(
    val id: String = "",
    @SerialName("room_code")
    val roomCode: String = "",
    @SerialName("host_id")
    val hostId: String = "",
    val status: RoomStatus = RoomStatus.WAITING,
    @SerialName("current_question_index")
    val currentQuestionIndex: Int = -1,
    @SerialName("question_started_at")
    val questionStartedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String = ""
)
