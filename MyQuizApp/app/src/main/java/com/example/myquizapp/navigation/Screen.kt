package com.example.myquizapp.navigation

/**
 * Screen.kt - Sealed class defining all navigation destinations
 */
sealed class Screen(val route: String) {

    /**
     * Role Selection screen - Main entry point where user chooses Host or Participant
     */
    data object RoleSelection : Screen(route = "role_selection")

    /**
     * Host Lobby - Shows room code and participants list for host
     */
    data object HostLobby : Screen(route = "host_lobby/{roomId}") {
        fun createRoute(roomId: String): String = "host_lobby/$roomId"
    }

    /**
     * Participant Join - Screen for participants to enter room code and name
     */
    data object ParticipantJoin : Screen(route = "participant_join")

    /**
     * Participant Lobby - Waiting room for participants
     */
    data object ParticipantLobby : Screen(route = "participant_lobby/{roomId}/{participantId}") {
        fun createRoute(roomId: String, participantId: String): String = 
            "participant_lobby/$roomId/$participantId"
    }

    /**
     * Host Quiz Control - Host controls quiz flow (next question, view rankings)
     */
    data object HostQuizControl : Screen(route = "host_quiz_control/{roomId}") {
        fun createRoute(roomId: String): String = "host_quiz_control/$roomId"
    }

    /**
     * Participant Question - Participant answers questions
     */
    data object ParticipantQuestion : Screen(route = "participant_question/{roomId}/{participantId}") {
        fun createRoute(roomId: String, participantId: String): String = 
            "participant_question/$roomId/$participantId"
    }

    /**
     * Leaderboard - Shows current rankings after each question
     */
    data object Leaderboard : Screen(route = "leaderboard/{roomId}/{isHost}") {
        fun createRoute(roomId: String, isHost: Boolean): String = 
            "leaderboard/$roomId/$isHost"
    }

    /**
     * Winners - Final results screen showing top 3 winners
     */
    data object Winners : Screen(route = "winners/{roomId}/{isHost}") {
        fun createRoute(roomId: String, isHost: Boolean): String = 
            "winners/$roomId/$isHost"
    }
}

