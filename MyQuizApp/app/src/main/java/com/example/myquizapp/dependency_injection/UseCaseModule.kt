package com.example.myquizapp.dependency_injection

import com.example.myquizapp.domain.HostLobby.HostLobbyUseCase
import com.example.myquizapp.domain.Leaderboard.LeaderboardUseCase
import com.example.myquizapp.domain.ParticipantJoin.ParticipantJoinUseCase
import com.example.myquizapp.domain.QuizSession.QuizSessionUseCase
import com.example.myquizapp.domain.RoleSelection.RoleSelectionUseCase
import com.example.myquizapp.domain.Winners.WinnersUseCase
import com.example.myquizapp.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCaseModule.kt - Provides use case dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideRoleSelectionUseCase(): RoleSelectionUseCase {
        return RoleSelectionUseCase()
    }

    @Provides
    @Singleton
    fun provideHostLobbyUseCase(
        quizRepository: QuizRepository
    ): HostLobbyUseCase {
        return HostLobbyUseCase(quizRepository)
    }

    @Provides
    @Singleton
    fun provideParticipantJoinUseCase(
        quizRepository: QuizRepository
    ): ParticipantJoinUseCase {
        return ParticipantJoinUseCase(quizRepository)
    }

    @Provides
    @Singleton
    fun provideQuizSessionUseCase(
        quizRepository: QuizRepository
    ): QuizSessionUseCase {
        return QuizSessionUseCase(quizRepository)
    }

    @Provides
    @Singleton
    fun provideLeaderboardUseCase(
        quizRepository: QuizRepository
    ): LeaderboardUseCase {
        return LeaderboardUseCase(quizRepository)
    }

    @Provides
    @Singleton
    fun provideWinnersUseCase(
        quizRepository: QuizRepository
    ): WinnersUseCase {
        return WinnersUseCase(quizRepository)
    }
}
