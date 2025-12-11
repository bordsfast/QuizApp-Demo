package com.example.myquizapp.repository

import com.example.myquizapp.data.model.AnswerResult
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import kotlinx.coroutines.flow.Flow

/**
 * QuizRepository.kt - Interface defining quiz operations
 */
interface QuizRepository {
    suspend fun createRoom(): Result<QuizRoom>
    suspend fun joinRoom(roomCode: String, name: String): Result<Participant>
    suspend fun getRoomByCode(roomCode: String): Result<QuizRoom>
    suspend fun getRoomById(roomId: String): Result<QuizRoom>
    fun observeRoom(roomId: String): Flow<QuizRoom>
    suspend fun getParticipants(roomId: String): Result<List<Participant>>
    fun observeParticipants(roomId: String): Flow<List<Participant>>
    suspend fun startQuiz(roomId: String): Result<Unit>
    suspend fun nextQuestion(roomId: String, currentIndex: Int): Result<Unit>
    suspend fun showLeaderboard(roomId: String): Result<Unit>
    suspend fun submitAnswer(
        roomId: String,
        participantId: String,
        questionIndex: Int,
        answerIndex: Int,
        isCorrect: Boolean,
        remainingTimeMillis: Long
    ): Result<AnswerResult>
    suspend fun endQuiz(roomId: String): Result<Unit>
    suspend fun getTopParticipants(roomId: String, limit: Int = 3): Result<List<Participant>>
    suspend fun hasParticipantAnswered(roomId: String, participantId: String, questionIndex: Int): Boolean
    suspend fun subscribeToRoom(roomId: String)
    suspend fun unsubscribeFromRoom(roomId: String)
    suspend fun subscribeToParticipants(roomId: String)
    suspend fun unsubscribeFromParticipants(roomId: String)
}
