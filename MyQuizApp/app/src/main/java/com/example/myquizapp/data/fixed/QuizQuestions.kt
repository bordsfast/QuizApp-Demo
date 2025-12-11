package com.example.myquizapp.data.fixed

import com.example.myquizapp.data.model.Question

/**
 * QuizQuestions.kt - Constant file containing all quiz questions
 * These questions are stored locally and used for all quizzes
 */
object QuizQuestions {

    val QUESTIONS: List<Question> = listOf(
        Question(
            index = 0,
            text = "What is the capital of France?",
            options = listOf("Paris", "London", "Berlin", "Madrid"),
            correctAnswerIndex = 0
        ),
        Question(
            index = 1,
            text = "Which planet is known as the Red Planet?",
            options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
            correctAnswerIndex = 1
        ),
        Question(
            index = 2,
            text = "What is the largest ocean on Earth?",
            options = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
            correctAnswerIndex = 2
        ),
        Question(
            index = 3,
            text = "Who painted the Mona Lisa?",
            options = listOf("Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo"),
            correctAnswerIndex = 2
        ),
        Question(
            index = 4,
            text = "What is the chemical symbol for gold?",
            options = listOf("Go", "Gd", "Au", "Ag"),
            correctAnswerIndex = 2
        ),
        Question(
            index = 5,
            text = "Which country has the largest population?",
            options = listOf("India", "United States", "China", "Indonesia"),
            correctAnswerIndex = 0
        ),
        Question(
            index = 6,
            text = "What year did World War II end?",
            options = listOf("1943", "1944", "1945", "1946"),
            correctAnswerIndex = 2
        ),
        Question(
            index = 7,
            text = "What is the smallest prime number?",
            options = listOf("0", "1", "2", "3"),
            correctAnswerIndex = 2
        ),
        Question(
            index = 8,
            text = "Which element has the atomic number 1?",
            options = listOf("Helium", "Hydrogen", "Oxygen", "Carbon"),
            correctAnswerIndex = 1
        ),
        Question(
            index = 9,
            text = "What is the longest river in the world?",
            options = listOf("Amazon River", "Nile River", "Yangtze River", "Mississippi River"),
            correctAnswerIndex = 1
        )
    )

    val TOTAL_QUESTIONS: Int = QUESTIONS.size

    fun getQuestion(index: Int): Question? {
        return QUESTIONS.getOrNull(index)
    }

    fun isLastQuestion(index: Int): Boolean {
        return index >= TOTAL_QUESTIONS - 1
    }
}
