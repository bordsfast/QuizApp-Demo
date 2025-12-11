package com.example.myquizapp.domain.ParticipantJoin

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import javax.inject.Inject

/**
 * ParticipantJoinUseCase.kt - Use case for participant join operations
 */
class ParticipantJoinUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun validateRoomCode(roomCode: String): Result<QuizRoom> {
        return quizRepository.getRoomByCode(roomCode)
    }

    suspend fun joinRoom(roomCode: String, name: String): Result<Participant> {
        return quizRepository.joinRoom(roomCode, name)
    }
}
