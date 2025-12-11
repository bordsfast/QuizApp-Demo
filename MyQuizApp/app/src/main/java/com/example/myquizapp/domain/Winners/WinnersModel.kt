package com.example.myquizapp.domain.Winners

import com.example.myquizapp.data.model.Participant

/**
 * WinnersModel.kt - Domain models for winners screen
 */
data class WinnersState(
    val firstPlace: Participant? = null,
    val secondPlace: Participant? = null,
    val thirdPlace: Participant? = null,
    val isHost: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
