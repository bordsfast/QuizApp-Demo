package com.example.myquizapp.dependency_injection

import com.example.myquizapp.repository.QuizRepository
import com.example.myquizapp.repository.QuizRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule.kt - Provides repository bindings
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository
}
