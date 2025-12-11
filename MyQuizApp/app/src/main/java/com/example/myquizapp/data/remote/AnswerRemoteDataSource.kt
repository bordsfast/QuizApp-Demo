package com.example.myquizapp.data.remote

import com.example.myquizapp.data.dto.request.SubmitAnswerRequest
import com.example.myquizapp.data.model.Answer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AnswerRemoteDataSource.kt - Remote data source for answer operations
 */
@Singleton
class AnswerRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val tableName = "answers"

    suspend fun submitAnswer(request: SubmitAnswerRequest): Answer {
        return supabaseClient.from(tableName)
            .insert(request) {
                select()
            }
            .decodeSingle<Answer>()
    }

    suspend fun getAnswersByRoomId(roomId: String): List<Answer> {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                }
            }
            .decodeList<Answer>()
    }

    suspend fun getAnswersByParticipantId(participantId: String): List<Answer> {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("participant_id", participantId)
                }
            }
            .decodeList<Answer>()
    }

    suspend fun getAnswersForQuestion(roomId: String, questionIndex: Int): List<Answer> {
        return supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                    eq("question_index", questionIndex)
                }
            }
            .decodeList<Answer>()
    }

    suspend fun hasParticipantAnswered(roomId: String, participantId: String, questionIndex: Int): Boolean {
        val answers = supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                    eq("participant_id", participantId)
                    eq("question_index", questionIndex)
                }
            }
            .decodeList<Answer>()
        return answers.isNotEmpty()
    }

    suspend fun getCorrectAnswersCount(roomId: String, questionIndex: Int): Int {
        val answers = supabaseClient.from(tableName)
            .select {
                filter {
                    eq("room_id", roomId)
                    eq("question_index", questionIndex)
                    eq("is_correct", true)
                }
            }
            .decodeList<Answer>()
        return answers.size
    }
}
