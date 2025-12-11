package com.example.myquizapp.domain.QuizSession

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus

/**
 * QuizSessionModel.kt - Domain models for quiz session
 */
data class QuizSessionState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val remainingTimeMillis: Long = 10000L,
    val selectedAnswerIndex: Int? = null,
    val hasAnswered: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val lastPointsEarned: Int = 0,
    val totalScore: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HostQuizState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val participants: List<Participant> = emptyList(),
    val answersCount: Int = 0,
    val isLastQuestion: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
