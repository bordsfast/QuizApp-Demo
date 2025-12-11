package com.example.myquizapp.domain.Winners

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.repository.QuizRepository
import javax.inject.Inject

/**
 * WinnersUseCase.kt - Use case for winners operations
 */
class WinnersUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun getWinners(roomId: String): Result<List<Participant>> {
        return quizRepository.getTopParticipants(roomId, 3)
    }
}
