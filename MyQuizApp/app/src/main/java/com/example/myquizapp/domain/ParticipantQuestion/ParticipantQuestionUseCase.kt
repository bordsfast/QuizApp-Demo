package com.example.myquizapp.domain.ParticipantQuestion

import com.example.myquizapp.data.fixed.QuizQuestions
import com.example.myquizapp.data.model.AnswerResult
import com.example.myquizapp.data.model.Question
import com.example.myquizapp.data.model.QuizRoom
import com.example.myquizapp.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ParticipantQuestionUseCase.kt - Use case for participant question operations
 */
class ParticipantQuestionUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend fun getRoomById(roomId: String): Result<QuizRoom> {
        return quizRepository.getRoomById(roomId)
    }

    fun observeRoom(roomId: String): Flow<QuizRoom> {
        return quizRepository.observeRoom(roomId)
    }

    fun getQuestion(index: Int): Question? {
        return QuizQuestions.getQuestion(index)
    }

    suspend fun submitAnswer(
        roomId: String,
        participantId: String,
        questionIndex: Int,
        answerIndex: Int,
        remainingTimeMillis: Long
    ): Result<AnswerResult> {
        val question = getQuestion(questionIndex)
        val isCorrect = question?.correctAnswerIndex == answerIndex
        return quizRepository.submitAnswer(
            roomId = roomId,
            participantId = participantId,
            questionIndex = questionIndex,
            answerIndex = answerIndex,
            isCorrect = isCorrect,
            remainingTimeMillis = remainingTimeMillis
        )
    }

    suspend fun hasParticipantAnswered(
        roomId: String,
        participantId: String,
        questionIndex: Int
    ): Boolean {
        return quizRepository.hasParticipantAnswered(roomId, participantId, questionIndex)
    }

    suspend fun subscribeToRoom(roomId: String) {
        quizRepository.subscribeToRoom(roomId)
    }

    suspend fun unsubscribeFromRoom(roomId: String) {
        quizRepository.unsubscribeFromRoom(roomId)
    }
}

