package com.example.myquizapp.domain.HostLobby

import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * HostLobbyUseCase.kt - Use case for host lobby operations
 */
class HostLobbyUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun createRoom(): Result<QuizRoom> {
        return quizRepository.createRoom()
    }

    suspend fun getParticipants(roomId: String): Result<List<Participant>> {
        return quizRepository.getParticipants(roomId)
    }

    fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return quizRepository.observeParticipants(roomId)
    }

    suspend fun startQuiz(roomId: String): Result<Unit> {
        return quizRepository.startQuiz(roomId)
    }

    suspend fun subscribeToParticipants(roomId: String) {
        quizRepository.subscribeToParticipants(roomId)
    }

    suspend fun unsubscribeFromParticipants(roomId: String) {
        quizRepository.unsubscribeFromParticipants(roomId)
    }
}
