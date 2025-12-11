package com.example.myquizapp.navigation

import androidx.navigation.NavHostController

/**
 * NavigationProvider.kt - Provides navigation actions throughout the app
 * Encapsulates navigation logic for cleaner screen implementations
 */
class NavigationProvider(
    private val navController: NavHostController
) {
    /**
     * Navigate to a specific screen
     */
    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route)
    }

    /**
     * Navigate to a route string (for parameterized routes)
     */
    fun navigateToRoute(route: String) {
        navController.navigate(route)
    }

    /**
     * Navigate back to the previous screen
     */
    fun navigateBack() {
        navController.popBackStack()
    }

    /**
     * Navigate to a screen and clear the back stack
     */
    fun navigateAndClearBackStack(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Navigate to a route and clear the back stack
     */
    fun navigateToRouteAndClearBackStack(route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Navigate to a screen and pop up to a specific destination
     */
    fun navigateWithPopUp(screen: Screen, popUpTo: Screen, inclusive: Boolean = false) {
        navController.navigate(screen.route) {
            popUpTo(popUpTo.route) { this.inclusive = inclusive }
        }
    }

    /**
     * Navigate to Host Lobby screen
     */
    fun navigateToHostLobby(roomId: String) {
        navController.navigate(Screen.HostLobby.createRoute(roomId))
    }

    /**
     * Navigate to Participant Lobby screen
     */
    fun navigateToParticipantLobby(roomId: String, participantId: String) {
        navController.navigate(Screen.ParticipantLobby.createRoute(roomId, participantId))
    }

    /**
     * Navigate to Host Quiz Control screen
     */
    fun navigateToHostQuizControl(roomId: String) {
        navController.navigate(Screen.HostQuizControl.createRoute(roomId)) {
            popUpTo(Screen.HostLobby.route) { inclusive = true }
        }
    }

    /**
     * Navigate to Participant Question screen
     */
    fun navigateToParticipantQuestion(roomId: String, participantId: String) {
        navController.navigate(Screen.ParticipantQuestion.createRoute(roomId, participantId)) {
            popUpTo(Screen.ParticipantLobby.route) { inclusive = true }
        }
    }

    /**
     * Navigate to Leaderboard screen
     */
    fun navigateToLeaderboard(roomId: String, isHost: Boolean) {
        navController.navigate(Screen.Leaderboard.createRoute(roomId, isHost))
    }

    /**
     * Navigate to Winners screen
     */
    fun navigateToWinners(roomId: String, isHost: Boolean) {
        navController.navigate(Screen.Winners.createRoute(roomId, isHost)) {
            popUpTo(Screen.RoleSelection.route) { inclusive = false }
        }
    }

    /**
     * Navigate to Role Selection (Home) and clear back stack
     */
    fun navigateToHome() {
        navController.navigate(Screen.RoleSelection.route) {
            popUpTo(0) { inclusive = true }
        }
    }
}

