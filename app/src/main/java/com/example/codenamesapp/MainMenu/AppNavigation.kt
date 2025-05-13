package com.example.codenamesapp.MainMenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.codenamesapp.Connection
import com.example.codenamesapp.GameBoardScreen
import com.example.codenamesapp.MainMenuScreen
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Player
import java.io.PrintWriter

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier // Füge den modifier Parameter hier hinzu
) {
    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = modifier // Übergib den Modifier an NavHost
    ) {
        composable("menu") {
            MainMenuScreen(
                onPlayClicked = { navController.navigate("connection") },
                onRulesClicked = { navController.navigate("rules") },
                onSettingsClicked = { navController.navigate("settings") }
            )
        }
        composable("rules") {
            RulesScreen(onBack = { navController.popBackStack() })
        }
        composable("connection") {
            Connection(
                navController = navController,
                onBackToMain = { navController.popBackStack() })
        }
        composable("lobby") {
            val connectionScreenState = navController.previousBackStackEntry?.savedStateHandle
            val playerName = connectionScreenState?.get<String>("playerName") ?: ""
            val playerList = connectionScreenState?.get<List<Player>>("playerList") ?: emptyList()

            LobbyScreen(
                playerName = playerName,
                playerList = playerList,
                onTeamJoin = { team -> println("Beitreten des Teams: $team") },
                onSpymasterToggle = { println("Spymaster-Toggle") },
                onBackToConnection = { navController.popBackStack() }
            )
        }
    }
}