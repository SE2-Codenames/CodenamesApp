package com.example.codenamesapp.MainMenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.codenamesapp.Connection
import com.example.codenamesapp.GameBoardScreen
import com.example.codenamesapp.MainMenuScreen
import com.example.codenamesapp.PayloadResponseMove
import com.example.codenamesapp.RulesScreen
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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

        composable("settings") {
            // Beispiel-Daten
            val exampleGameState = GamePhase.SPYMASTER_TURN
            val exampleTeamRole = TeamRole.BLUE
            val exampleCardList = listOf(
                // Rote Karten
                Card("Feuer", Role.RED), Card("Blut", Role.RED), Card("Rose", Role.RED),
                Card("Apfel", Role.RED), Card("Kirsche", Role.RED), Card("Erdbeere", Role.RED),
                Card("Tomate", Role.RED), Card("Rubin", Role.RED), Card("Wein", Role.RED),
                // Blaue Karten
                Card("Wasser", Role.BLUE), Card("Himmel", Role.BLUE), Card("Ozean", Role.BLUE),
                Card("Saphir", Role.BLUE), Card("Eis", Role.BLUE), Card("See", Role.BLUE),
                Card("Blume", Role.BLUE), Card("Jeans", Role.BLUE),
                // Neutrale Karten
                Card("Tisch", Role.NEUTRAL), Card("Stuhl", Role.NEUTRAL), Card("Lampe", Role.NEUTRAL),
                Card("Buch", Role.NEUTRAL), Card("Haus", Role.NEUTRAL), Card("Baum", Role.NEUTRAL),
                Card("Sonne", Role.NEUTRAL),
                // Assassine
                Card("Schatten", Role.ASSASSIN)
            )
            val exampleScoreArray = arrayOf(9, 8)
            val exampleString = ""
            val exampleInt = 5

            val payloadResponseMoveObject = PayloadResponseMove(
                gameState = exampleGameState,
                teamRole = exampleTeamRole,
                card = exampleCardList,
                score = exampleScoreArray,
                hint = exampleString,
                remainingGuesses = exampleInt
            )

            // Daten an die nächste Composable übergeben über SavedStateHandle
            navController.currentBackStackEntry?.savedStateHandle?.set("payload", payloadResponseMoveObject)
            navController.currentBackStackEntry?.savedStateHandle?.set("playerRole", "X")
            navController.currentBackStackEntry?.savedStateHandle?.set("team", "Y")

            // Weiterleiten zur Gameboard-Screen
            navController.navigate("gameboard")
        }

        composable("connection") {
            Connection(
                navController = navController,
                onBackToMain = { navController.popBackStack() }
            )
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

        composable("gameboard") {
            val settingsState = navController.previousBackStackEntry?.savedStateHandle
            val payload = settingsState?.get<PayloadResponseMove>("payload")
            val playerRole = true
            val team = TeamRole.BLUE

            if (payload != null) {
                GameBoardScreen(
                    gameState = payload,
                    playerRole = playerRole,
                    team = team,
                    helper = TODO()
                )
            } else {
                // Optional: Fehleranzeige oder zurück
                println("Fehler: Payload nicht gefunden")
            }
        }
    }
}
