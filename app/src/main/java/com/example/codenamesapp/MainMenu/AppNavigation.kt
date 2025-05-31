package com.example.codenamesapp.MainMenu

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.codenamesapp.MainMenuScreen
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.network.WebSocketClient
import com.example.codenamesapp.GameBoardScreen
import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.screens.ConnectionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val playerNameState = remember { mutableStateOf<String?>(null) }
    val playerListState = remember { mutableStateOf(listOf<Player>()) }
    val gameStateViewModel: GameStateViewModel = viewModel()

    // Setze Callback für SHOW_GAMEBOARD
    gameStateViewModel.onShowGameBoard = {
        navController.navigate("gameboard")
    }

    LaunchedEffect(Unit) {
        gameStateViewModel.onResetGame = {
            if (!gameStateViewModel.hasReset.value) {
                gameStateViewModel.hasReset.value = true
                navController.navigate("lobby") {
                    popUpTo("menu") { inclusive = false }
                }
            }
        }
    }

    val socketClient = remember {
        WebSocketClient(
            gameStateViewModel = gameStateViewModel,
            navController = navController
        )
    }

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
            RulesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("connection") {
            ConnectionScreen(
                navController = navController,
                coroutineScope = coroutineScope,
                socketClient = socketClient,
                onConnectionEstablished = { name ->
                    println("✅ Verbindung steht")
                    playerNameState.value = name
                    navController.navigate("lobby")
                },
                onMessageReceived = {
                    println("📨 Server: $it")
                },
                onPlayerListUpdated = {
                    playerListState.value = it
                    if (playerNameState.value != null && it.any { p -> p.name == playerNameState.value }) {
                        navController.navigate("lobby")
                    }
                }
            )
        }

        composable("lobby") {
            LobbyScreen(
                playerName = playerNameState.value ?: "",
                playerList = playerListState.value,
                socketClient = socketClient,
                onBackToConnection = { navController.popBackStack() },
                onStartGame = {
                    coroutineScope.launch(Dispatchers.IO) {
                        socketClient.send("START_GAME")
                    }
                }
            )
        }

        composable("gameboard") {
            val payload = gameStateViewModel.payload.value
            val team = gameStateViewModel.team.value
            val isSpymaster = gameStateViewModel.playerRole.value
            val communication = socketClient.communication


            if (payload != null && team != null && isSpymaster != null) {
                GameBoardScreen(
                    gameState = payload,
                    team = team,
                    playerRole = isSpymaster,
                    communication = communication
                )
            }
        }
    }
}
