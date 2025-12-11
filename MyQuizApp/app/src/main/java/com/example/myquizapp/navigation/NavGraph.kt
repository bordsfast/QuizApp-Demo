package com.example.myquizapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myquizapp.user_interface.RoleSelection.RoleSelectionScreen
import com.example.myquizapp.user_interface.HostLobby.HostLobbyScreen
import com.example.myquizapp.user_interface.ParticipantJoin.ParticipantJoinScreen
import com.example.myquizapp.user_interface.ParticipantLobby.ParticipantLobbyScreen
import com.example.myquizapp.user_interface.HostQuizControl.HostQuizControlScreen
import com.example.myquizapp.user_interface.ParticipantQuestion.ParticipantQuestionScreen
import com.example.myquizapp.user_interface.Leaderboard.LeaderboardScreen
import com.example.myquizapp.user_interface.Winners.WinnersScreen

/**
 * NavGraph.kt - Main navigation graph for the app
 * Defines all navigation routes and their corresponding composables
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.RoleSelection.route
) {
    val navigationProvider = remember(navController) {
        NavigationProvider(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Role Selection (Start Screen)
        composable(route = Screen.RoleSelection.route) {
            RoleSelectionScreen(navigationProvider = navigationProvider)
        }

        // Host Lobby
        composable(
            route = Screen.HostLobby.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            HostLobbyScreen(
                roomId = roomId,
                navigationProvider = navigationProvider
            )
        }

        // Participant Join
        composable(route = Screen.ParticipantJoin.route) {
            ParticipantJoinScreen(navigationProvider = navigationProvider)
        }

        // Participant Lobby
        composable(
            route = Screen.ParticipantLobby.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("participantId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val participantId = backStackEntry.arguments?.getString("participantId") ?: ""
            ParticipantLobbyScreen(
                roomId = roomId,
                participantId = participantId,
                navigationProvider = navigationProvider
            )
        }

        // Host Quiz Control
        composable(
            route = Screen.HostQuizControl.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            HostQuizControlScreen(
                roomId = roomId,
                navigationProvider = navigationProvider
            )
        }

        // Participant Question
        composable(
            route = Screen.ParticipantQuestion.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("participantId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val participantId = backStackEntry.arguments?.getString("participantId") ?: ""
            ParticipantQuestionScreen(
                roomId = roomId,
                participantId = participantId,
                navigationProvider = navigationProvider
            )
        }

        // Leaderboard
        composable(
            route = Screen.Leaderboard.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("isHost") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val isHost = backStackEntry.arguments?.getBoolean("isHost") ?: false
            LeaderboardScreen(
                roomId = roomId,
                isHost = isHost,
                navigationProvider = navigationProvider
            )
        }

        // Winners
        composable(
            route = Screen.Winners.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("isHost") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val isHost = backStackEntry.arguments?.getBoolean("isHost") ?: false
            WinnersScreen(
                roomId = roomId,
                isHost = isHost,
                navigationProvider = navigationProvider
            )
        }
    }
}

