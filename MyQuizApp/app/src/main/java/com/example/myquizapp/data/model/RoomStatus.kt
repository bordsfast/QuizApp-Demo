package com.example.myquizapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * RoomStatus.kt - Enum representing the status of a quiz room
 */
@Serializable
enum class RoomStatus {
    @SerialName("waiting")
    WAITING,
    @SerialName("active")
    ACTIVE,
    @SerialName("showing_leaderboard")
    SHOWING_LEADERBOARD,
    @SerialName("finished")
    FINISHED
}
