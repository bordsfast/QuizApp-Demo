package com.example.myquizapp.domain.Leaderboard

import com.example.myquizapp.data.model.Participant

/**
 * LeaderboardModel.kt - Domain models for leaderboard
 */
data class LeaderboardState(
    val topParticipants: List<Participant> = emptyList(),
    val allParticipants: List<Participant> = emptyList(),
    val isHost: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
