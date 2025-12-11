package com.example.myquizapp.repository

import com.example.myquizapp.data.dto.request.CreateRoomRequest
import com.example.myquizapp.data.dto.request.JoinRoomRequest
import com.example.myquizapp.data.dto.request.SubmitAnswerRequest
import com.example.myquizapp.data.model.AnswerResult
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import com.example.myquizapp.data.remote.AnswerRemoteDataSource
import com.example.myquizapp.data.remote.ParticipantRemoteDataSource
import com.example.myquizapp.data.remote.QuizRoomRemoteDataSource
import com.example.myquizapp.utility.function.generateRoomCode
import com.example.myquizapp.utility.function.generateHostId
import com.example.myquizapp.utility.function.calculatePoints
import com.example.myquizapp.utility.function.getCurrentTimestamp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

// #region agent log
private const val DEBUG_TAG = "QUIZ_DEBUG"
private fun debugLog(location: String, message: String, data: Map<String, Any?>, hypothesisId: String) {
    Log.d(DEBUG_TAG, "[$hypothesisId] $location: $message | data=$data")
}
// #endregion

/**
 * QuizRepositoryImpl.kt - Implementation of QuizRepository
 */
@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizRoomRemoteDataSource: QuizRoomRemoteDataSource,
    private val participantRemoteDataSource: ParticipantRemoteDataSource,
    private val answerRemoteDataSource: AnswerRemoteDataSource
) : QuizRepository {

    override suspend fun createRoom(): Result<QuizRoom> {
        return try {
            val roomCode = generateRoomCode()
            val hostId = generateHostId()
            val request = CreateRoomRequest(
                roomCode = roomCode,
                hostId = hostId
            )
            val room = quizRoomRemoteDataSource.createRoom(request)
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinRoom(roomCode: String, name: String): Result<Participant> {
        return try {
            val room = quizRoomRemoteDataSource.getRoomByCode(roomCode)
                ?: return Result.failure(Exception("Room not found"))
            if (room.status != RoomStatus.WAITING) {
                return Result.failure(Exception("Quiz has already started"))
            }
            val request = JoinRoomRequest(
                roomId = room.id,
                name = name
            )
            val participant = participantRemoteDataSource.joinRoom(request)
            Result.success(participant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoomByCode(roomCode: String): Result<QuizRoom> {
        return try {
            val room = quizRoomRemoteDataSource.getRoomByCode(roomCode)
                ?: return Result.failure(Exception("Room not found"))
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoomById(roomId: String): Result<QuizRoom> {
        return try {
            val room = quizRoomRemoteDataSource.getRoomById(roomId)
                ?: return Result.failure(Exception("Room not found"))
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeRoom(roomId: String): Flow<QuizRoom> {
        return quizRoomRemoteDataSource.observeRoom(roomId)
    }

    override suspend fun getParticipants(roomId: String): Result<List<Participant>> {
        return try {
            val participants = participantRemoteDataSource.getParticipantsByRoomId(roomId)
            Result.success(participants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return participantRemoteDataSource.observeParticipants(roomId)
    }

    override suspend fun startQuiz(roomId: String): Result<Unit> {
        // #region agent log
        debugLog("QuizRepositoryImpl.kt:119", "startQuiz called", mapOf("roomId" to roomId), "B")
        // #endregion
        return try {
            // #region agent log
            debugLog("QuizRepositoryImpl.kt:123", "Calling updateRoomStatus", mapOf(
                "roomId" to roomId,
                "status" to RoomStatus.ACTIVE.name,
                "questionIndex" to 0
            ), "B")
            // #endregion
            quizRoomRemoteDataSource.updateRoomStatus(
                roomId = roomId,
                status = RoomStatus.ACTIVE,
                questionIndex = 0,
                questionStartedAt = getCurrentTimestamp()
            )
            // #region agent log
            debugLog("QuizRepositoryImpl.kt:136", "updateRoomStatus completed successfully", mapOf("roomId" to roomId), "B")
            // #endregion
            Result.success(Unit)
        } catch (e: Exception) {
            // #region agent log
            debugLog("QuizRepositoryImpl.kt:141", "startQuiz exception", mapOf(
                "errorMessage" to e.message,
                "errorType" to e::class.simpleName,
                "stackTrace" to e.stackTraceToString().take(500)
            ), "B")
            // #endregion
            Result.failure(e)
        }
    }

    override suspend fun nextQuestion(roomId: String, currentIndex: Int): Result<Unit> {
        return try {
            quizRoomRemoteDataSource.updateCurrentQuestion(
                roomId = roomId,
                questionIndex = currentIndex + 1,
                questionStartedAt = getCurrentTimestamp()
            )
            quizRoomRemoteDataSource.updateRoomStatus(
                roomId = roomId,
                status = RoomStatus.ACTIVE
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun showLeaderboard(roomId: String): Result<Unit> {
        return try {
            quizRoomRemoteDataSource.updateRoomStatus(
                roomId = roomId,
                status = RoomStatus.SHOWING_LEADERBOARD
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitAnswer(
        roomId: String,
        participantId: String,
        questionIndex: Int,
        answerIndex: Int,
        isCorrect: Boolean,
        remainingTimeMillis: Long
    ): Result<AnswerResult> {
        return try {
            val pointsEarned = if (isCorrect) {
                calculatePoints(remainingTimeMillis)
            } else {
                0
            }
            val request = SubmitAnswerRequest(
                roomId = roomId,
                participantId = participantId,
                questionIndex = questionIndex,
                answerIndex = answerIndex,
                isCorrect = isCorrect,
                pointsEarned = pointsEarned
            )
            answerRemoteDataSource.submitAnswer(request)
            val participant = participantRemoteDataSource.getParticipantById(participantId)
                ?: return Result.failure(Exception("Participant not found"))
            val newScore = participant.score + pointsEarned
            participantRemoteDataSource.updateParticipantScore(participantId, newScore)
            val result = AnswerResult(
                isCorrect = isCorrect,
                pointsEarned = pointsEarned,
                newTotalScore = newScore
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endQuiz(roomId: String): Result<Unit> {
        return try {
            quizRoomRemoteDataSource.updateRoomStatus(
                roomId = roomId,
                status = RoomStatus.FINISHED
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopParticipants(roomId: String, limit: Int): Result<List<Participant>> {
        return try {
            val participants = participantRemoteDataSource.getTopParticipants(roomId, limit)
            Result.success(participants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasParticipantAnswered(
        roomId: String,
        participantId: String,
        questionIndex: Int
    ): Boolean {
        return answerRemoteDataSource.hasParticipantAnswered(roomId, participantId, questionIndex)
    }

    override suspend fun subscribeToRoom(roomId: String) {
        quizRoomRemoteDataSource.subscribeToRoom(roomId)
    }

    override suspend fun unsubscribeFromRoom(roomId: String) {
        quizRoomRemoteDataSource.unsubscribeFromRoom(roomId)
    }

    override suspend fun subscribeToParticipants(roomId: String) {
        participantRemoteDataSource.subscribeToParticipants(roomId)
    }

    override suspend fun unsubscribeFromParticipants(roomId: String) {
        participantRemoteDataSource.unsubscribeFromParticipants(roomId)
    }
}
