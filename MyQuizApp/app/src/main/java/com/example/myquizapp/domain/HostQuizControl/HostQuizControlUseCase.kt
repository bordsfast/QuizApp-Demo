package com.example.myquizapp.domain.HostQuizControl

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * HostQuizControlUseCase.kt - Use case for host quiz control operations
 */
class HostQuizControlUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun getRoomById(roomId: String): Result<QuizRoom> {
        return quizRepository.getRoomById(roomId)
    }

    fun observeRoom(roomId: String): Flow<QuizRoom> {
        return quizRepository.observeRoom(roomId)
    }

    fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return quizRepository.observeParticipants(roomId)
    }

    suspend fun showLeaderboard(roomId: String): Result<Unit> {
        return quizRepository.showLeaderboard(roomId)
    }

    suspend fun nextQuestion(roomId: String, currentIndex: Int): Result<Unit> {
        return quizRepository.nextQuestion(roomId, currentIndex)
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
}

