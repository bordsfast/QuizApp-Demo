package com.example.myquizapp.domain.HostQuizControl

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom

/**
 * HostQuizControlModel.kt - Domain models for host quiz control
 */
data class HostQuizControlState(
    val room: QuizRoom? = null,
    val currentQuestion: Question? = null,
    val participants: List<Participant> = emptyList(),
    val isLastQuestion: Boolean = false
)

sealed class HostQuizControlAction {
    data object ShowLeaderboard : HostQuizControlAction()
    data object NextQuestion : HostQuizControlAction()
    data object EndQuiz : HostQuizControlAction()
}

