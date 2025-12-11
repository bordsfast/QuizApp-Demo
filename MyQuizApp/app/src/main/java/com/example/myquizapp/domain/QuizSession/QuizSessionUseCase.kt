package com.example.myquizapp.domain.QuizSession

import com.example.myquizapp.data.fixed.QuizQuestions
import com.example.myquizapp.data.model.AnswerResult
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * QuizSessionUseCase.kt - Use case for quiz session operations
 */
class QuizSessionUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    fun getQuestion(index: Int): Question? {
        return QuizQuestions.getQuestion(index)
    }

    fun isLastQuestion(index: Int): Boolean {
        return QuizQuestions.isLastQuestion(index)
    }

    fun getTotalQuestions(): Int {
        return QuizQuestions.TOTAL_QUESTIONS
    }

    suspend fun getRoomById(roomId: String): Result<QuizRoom> {
        return quizRepository.getRoomById(roomId)
    }

    fun observeRoom(roomId: String): Flow<QuizRoom> {
        return quizRepository.observeRoom(roomId)
    }

    suspend fun submitAnswer(
        roomId: String,
        participantId: String,
        questionIndex: Int,
        answerIndex: Int,
        remainingTimeMillis: Long
    ): Result<AnswerResult> {
        val question = getQuestion(questionIndex)
            ?: return Result.failure(Exception("Question not found"))
        val isCorrect = answerIndex == question.correctAnswerIndex
        return quizRepository.submitAnswer(
            roomId = roomId,
            participantId = participantId,
            questionIndex = questionIndex,
            answerIndex = answerIndex,
            isCorrect = isCorrect,
            remainingTimeMillis = remainingTimeMillis
        )
    }

    suspend fun hasParticipantAnswered(
        roomId: String,
        participantId: String,
        questionIndex: Int
    ): Boolean {
        return quizRepository.hasParticipantAnswered(roomId, participantId, questionIndex)
    }

    suspend fun nextQuestion(roomId: String, currentIndex: Int): Result<Unit> {
        return quizRepository.nextQuestion(roomId, currentIndex)
    }

    suspend fun showLeaderboard(roomId: String): Result<Unit> {
        return quizRepository.showLeaderboard(roomId)
    }

    suspend fun endQuiz(roomId: String): Result<Unit> {
        return quizRepository.endQuiz(roomId)
    }

    suspend fun subscribeToRoom(roomId: String) {
        quizRepository.subscribeToRoom(roomId)
    }

    suspend fun unsubscribeFromRoom(roomId: String) {
        quizRepository.unsubscribeFromRoom(roomId)
    }

    fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return quizRepository.observeParticipants(roomId)
    }

    suspend fun getParticipants(roomId: String): Result<List<Participant>> {
        return quizRepository.getParticipants(roomId)
    }
}
