package com.example.myquizapp.domain.ParticipantLobby

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ParticipantLobbyUseCase.kt - Use case for participant lobby operations
 */
class ParticipantLobbyUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun getRoomById(roomId: String): Result<QuizRoom> {
        return quizRepository.getRoomById(roomId)
    }

    fun observeRoom(roomId: String): Flow<QuizRoom> {
        return quizRepository.observeRoom(roomId)
    }

    suspend fun getParticipants(roomId: String): Result<List<Participant>> {
        return quizRepository.getParticipants(roomId)
    }

    fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return quizRepository.observeParticipants(roomId)
    }

    suspend fun subscribeToRoom(roomId: String) {
        quizRepository.subscribeToRoom(roomId)
    }

    suspend fun unsubscribeFromRoom(roomId: String) {
        quizRepository.unsubscribeFromRoom(roomId)
    }

    suspend fun subscribeToParticipants(roomId: String) {
        quizRepository.subscribeToParticipants(roomId)
    }

    suspend fun unsubscribeFromParticipants(roomId: String) {
        quizRepository.unsubscribeFromParticipants(roomId)
    }
}

