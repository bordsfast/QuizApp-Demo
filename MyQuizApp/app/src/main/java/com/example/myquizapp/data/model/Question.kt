package com.example.myquizapp.data.model

/**
 * Question.kt - Data class representing a quiz question
 * Questions are stored locally in a constant file
 */
data class Question(
    val index: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)
