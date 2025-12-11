package com.example.myquizapp.dependency_injection

import com.example.myquizapp.data.remote.AnswerRemoteDataSource
import com.example.myquizapp.data.remote.ParticipantRemoteDataSource
import com.example.myquizapp.data.remote.QuizRoomRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

/**
 * DataSourceModule.kt - Provides data source dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideQuizRoomRemoteDataSource(
        supabaseClient: SupabaseClient
    ): QuizRoomRemoteDataSource {
        return QuizRoomRemoteDataSource(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideParticipantRemoteDataSource(
        supabaseClient: SupabaseClient
    ): ParticipantRemoteDataSource {
        return ParticipantRemoteDataSource(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideAnswerRemoteDataSource(
        supabaseClient: SupabaseClient
    ): AnswerRemoteDataSource {
        return AnswerRemoteDataSource(supabaseClient)
    }
}
