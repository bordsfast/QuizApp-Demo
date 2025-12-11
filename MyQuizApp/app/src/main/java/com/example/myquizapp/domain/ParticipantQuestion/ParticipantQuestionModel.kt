package com.example.myquizapp.domain.ParticipantQuestion

import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom

/**
 * ParticipantQuestionModel.kt - Domain models for participant question screen
 */
data class ParticipantQuestionState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val remainingTimeMillis: Long = 0L,
    val hasAnswered: Boolean = false,
    val selectedAnswerIndex: Int? = null
)

data class AnswerSubmission(
    val roomId: String,
    val participantId: String,
    val questionIndex: Int,
    val answerIndex: Int,
    val remainingTimeMillis: Long
)

sealed class ParticipantQuestionStatus {
    data object AnsweringQuestion : ParticipantQuestionStatus()
    data object WaitingForResults : ParticipantQuestionStatus()
    data object ShowingLeaderboard : ParticipantQuestionStatus()
    data object QuizFinished : ParticipantQuestionStatus()
}

