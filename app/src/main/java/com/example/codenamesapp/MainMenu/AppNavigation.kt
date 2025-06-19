package com.example.codenamesapp.MainMenu

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.codenamesapp.gamelogic.GameBoardScreen
import com.example.codenamesapp.MainMenuScreen
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.network.WebSocketClient
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.gamelogic.GameStateViewModelFactory
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
    val messages = remember { mutableStateListOf<String>() }
    val gameStateViewModel: GameStateViewModel = viewModel(
        factory = GameStateViewModelFactory(GameManager())
    )

    // Setze Callback für SHOW_GAMEBOARD
    gameStateViewModel.onShowGameBoard = {
        navController.navigate("gameboard")
    }

    val socketClient = remember {
        WebSocketClient(
            gameStateViewModel = gameStateViewModel,
            navController = navController
        )
    }

    LaunchedEffect(Unit) {
        gameStateViewModel.onResetGame = {
            if (!gameStateViewModel.hasReset.value) {
                gameStateViewModel.hasReset.value = true
                socketClient.reconnect(
                    onSuccess = { navController.navigate("lobby") },
                    onError = { println("Fehler beim Reconnect: $it") },
                    onMessageReceived = { println("Server: $it") },
                    onPlayerListUpdated = { playerListState.value = it }
                )
                navController.navigate("lobby") {
                    popUpTo("menu") { inclusive = false }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = modifier
    ) {
        composable("menu") {
            MainMenuScreen(
                onPlayClicked = { navController.navigate("connection") },
                onRulesClicked = { navController.navigate("rules") }
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
                onMessageReceived = { msg ->
                    println("📨 Server: $msg")
                    messages.add(msg)
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
                gameStateViewModel = gameStateViewModel,
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
            val team = gameStateViewModel.teamTurn.value
            val isSpymaster = gameStateViewModel.playerRole.value
            val communication = socketClient.communication


            if (payload != null && team != null && isSpymaster != null) {
                GameBoardScreen(
                    viewModel = gameStateViewModel,
                    communication = communication,
                    messages = messages
                )
            }
        }

        composable("gameover") { //null check
            val gameEndResult = gameStateViewModel.gameEndResult.value
                ?: run {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

            GameOverScreen(
                navController = navController,
                winningTeam = gameEndResult.winningTeam,
                isAssassinTriggered = gameEndResult.isAssassinTriggered,
                scoreRed = gameEndResult.scoreRed,
                scoreBlue = gameEndResult.scoreBlue
            )
        }

    }
}