package com.example.myquizapp.data.model

/**
 * AnswerResult.kt - Data class representing the result of submitting an answer
 */
data class AnswerResult(
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val newTotalScore: Int
)
