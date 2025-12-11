package com.example.myquizapp.data.remote

import com.example.myquizapp.data.dto.request.CreateRoomRequest
import com.example.myquizapp.data.dto.request.UpdateCurrentQuestionRequest
import com.example.myquizapp.data.dto.request.UpdateRoomStatusRequest
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.data.model.RoomStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QuizRoomRemoteDataSource.kt - Remote data source for quiz room operations
 */
@Singleton
class QuizRoomRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val tableName = "quiz_rooms"

    suspend fun createRoom(request: CreateRoomRequest): QuizRoom {
        return supabaseClient.from(tableName)
            .insert(request) {
                select()
            }
            .decodeSingle<QuizRoom>()
    }

    suspend fun getRoomByCode(roomCode: String): QuizRoom? {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_code", roomCode)
                }
            }
            .decodeSingleOrNull<QuizRoom>()
    }

    suspend fun getRoomById(roomId: String): QuizRoom? {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("id", roomId)
                }
            }
            .decodeSingleOrNull<QuizRoom>()
    }

    suspend fun updateRoomStatus(roomId: String, status: RoomStatus, questionIndex: Int? = null, questionStartedAt: String? = null) {
        val updateRequest = UpdateRoomStatusRequest(
            status = status.name.lowercase(),
            currentQuestionIndex = questionIndex,
            questionStartedAt = questionStartedAt
        )

        supabaseClient.from(tableName)
            .update(updateRequest) {
                filter {
                    eq("id", roomId)
                }
            }
    }

    suspend fun updateCurrentQuestion(roomId: String, questionIndex: Int, questionStartedAt: String) {
        val updateRequest = UpdateCurrentQuestionRequest(
            currentQuestionIndex = questionIndex,
            questionStartedAt = questionStartedAt
        )

        supabaseClient.from(tableName)
            .update(updateRequest) {
                filter {
                    eq("id", roomId)
                }
            }
    }

    fun observeRoom(roomId: String): Flow<QuizRoom> {
        return flow {
            while (true) {
                val room = getRoomById(roomId)
                if (room != null) {
                    emit(room)
                }
                delay(1000) // Poll every second
            }
        }
    }

    suspend fun subscribeToRoom(roomId: String) {
        // No-op for polling implementation
    }

    suspend fun unsubscribeFromRoom(roomId: String) {
        // No-op for polling implementation
    }
}
