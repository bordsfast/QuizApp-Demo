package com.example.myquizapp.domain.Leaderboard

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.repository.QuizRepository
import javax.inject.Inject

/**
 * LeaderboardUseCase.kt - Use case for leaderboard operations
 */
class LeaderboardUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun getTopParticipants(roomId: String, limit: Int = 3): Result<List<Participant>> {
        return quizRepository.getTopParticipants(roomId, limit)
    }

    suspend fun getAllParticipants(roomId: String): Result<List<Participant>> {
        return quizRepository.getParticipants(roomId)
    }

    suspend fun nextQuestion(roomId: String, currentIndex: Int): Result<Unit> {
        return quizRepository.nextQuestion(roomId, currentIndex)
    }

    suspend fun endQuiz(roomId: String): Result<Unit> {
        return quizRepository.endQuiz(roomId)
    }
}
