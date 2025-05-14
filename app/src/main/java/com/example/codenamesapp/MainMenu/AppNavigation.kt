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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // State für die Spielerliste, der über die Navigation hinweg erhalten bleibt
    val playerListState = rememberSaveable { mutableStateOf(listOf<Player>()) }
    val coroutineScope = rememberCoroutineScope() // Scope für Coroutines

    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = modifier
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
                onBackToMain = { navController.popBackStack() },
                onPlayerListChanged = { newList ->
                    playerListState.value = newList
                },
                coroutineScope = coroutineScope // Übergib den CoroutineScope
            )
        }
        composable("lobby") {
            val connectionScreenState = navController.previousBackStackEntry?.savedStateHandle
            val playerName = connectionScreenState?.get<String>("playerName") ?: ""
            val socketWriter = connectionScreenState?.get<PrintWriter>("socketWriter")

            LobbyScreen(
                playerName = playerName,
                playerList = playerListState.value,
                onTeamJoin = { team ->
                    coroutineScope.launch(Dispatchers.IO) {
                        socketWriter?.println("JOIN_TEAM:$team")
                        socketWriter?.flush() // Sende die Nachricht sofort
                        println("Sende JOIN_TEAM:$team an den Server") // Log
                        // Aktualisiere die Spielerliste im State, nachdem die Serverantwort empfangen wurde (siehe Connection.kt)
                    }
                    val updatedList = playerListState.value.map {
                        if (it.name == playerName) it.copy(team = team) else it
                    }
                    playerListState.value = updatedList

                },
                onSpymasterToggle = {
                    coroutineScope.launch(Dispatchers.IO) {
                        socketWriter?.println("SPYMASTER_TOGGLE")
                        socketWriter?.flush()
                        println("Sende SPYMASTER_TOGGLE an den Server")
                    }
                    val updatedList = playerListState.value.map {
                        if (it.name == playerName) it.copy(isSpymaster = !it.isSpymaster) else it
                    }
                    playerListState.value = updatedList
                },
                onBackToConnection = { navController.popBackStack() },
                onStartGame = {
                    coroutineScope.launch(Dispatchers.IO) {
                        socketWriter?.println("START_GAME")
                        socketWriter?.flush()
                        println("Sende START_GAME an den Server")
                    }
                    println("Spiel startet")
                }
            )
        }
    }
}
