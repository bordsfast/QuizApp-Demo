package com.example.myquizapp.utility.function

import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * QuizUtils.kt - Utility functions for quiz operations
 */

private const val ROOM_CODE_LENGTH = 4
private const val ROOM_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

private const val BASE_POINTS = 100
private const val MAX_SPEED_BONUS = 900
private const val QUESTION_TIME_LIMIT_MILLIS = 10000L

/**
 * Generates a random room code (e.g., "QX7Y")
 */
fun generateRoomCode(): String {
    return (1..ROOM_CODE_LENGTH)
        .map { ROOM_CODE_CHARS[Random.nextInt(ROOM_CODE_CHARS.length)] }
        .joinToString("")
}

/**
 * Generates a unique host ID
 */
fun generateHostId(): String {
    return "host_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
}

/**
 * Calculates points based on remaining time
 * Faster answers earn more points (speed bonus)
 * Points = Base + (Remaining Time / Total Time) * Max Speed Bonus
 */
fun calculatePoints(remainingTimeMillis: Long): Int {
    if (remainingTimeMillis <= 0) return BASE_POINTS
    val speedBonus = ((remainingTimeMillis.toDouble() / QUESTION_TIME_LIMIT_MILLIS) * MAX_SPEED_BONUS).toInt()
    return BASE_POINTS + speedBonus
}

/**
 * Gets the current timestamp in ISO 8601 format
 */
fun getCurrentTimestamp(): String {
    return DateTimeFormatter.ISO_INSTANT.format(Instant.now())
}

/**
 * Formats points for display (e.g., "+950pts")
 */
fun formatPoints(points: Int): String {
    return "+${points}pts"
}

/**
 * Formats time remaining in seconds (e.g., "0:07s")
 */
fun formatTimeSeconds(timeMillis: Long): String {
    val seconds = (timeMillis / 1000).coerceAtLeast(0)
    return "0:${seconds.toString().padStart(2, '0')}s"
}
