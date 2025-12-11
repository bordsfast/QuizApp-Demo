package com.example.myquizapp.data.remote

import com.example.myquizapp.data.dto.request.JoinRoomRequest
import com.example.myquizapp.data.model.Participant
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ParticipantRemoteDataSource.kt - Remote data source for participant operations
 */
@Singleton
class ParticipantRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val tableName = "participants"

    suspend fun joinRoom(request: JoinRoomRequest): Participant {
        return supabaseClient.from(tableName)
            .insert(request) {
                select()
            }
            .decodeSingle<Participant>()
    }

    suspend fun getParticipantsByRoomId(roomId: String): List<Participant> {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                }
            }
            .decodeList<Participant>()
    }

    suspend fun getParticipantById(participantId: String): Participant? {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("id", participantId)
                }
            }
            .decodeSingleOrNull<Participant>()
    }

    suspend fun updateParticipantScore(participantId: String, newScore: Int) {
        supabaseClient.from(tableName)
            .update(mapOf("score" to newScore)) {
                filter {
                    eq("id", participantId)
                }
            }
    }

    fun observeParticipants(roomId: String): Flow<List<Participant>> {
        return flow {
            while (true) {
                val participants = getParticipantsByRoomId(roomId)
                emit(participants)
                delay(1000) // Poll every second
            }
        }
    }

    suspend fun subscribeToParticipants(roomId: String) {
        // No-op for polling implementation
    }

    suspend fun unsubscribeFromParticipants(roomId: String) {
        // No-op for polling implementation
    }

    suspend fun getTopParticipants(roomId: String, limit: Int = 3): List<Participant> {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                }
                order("score", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<Participant>()
    }
}
