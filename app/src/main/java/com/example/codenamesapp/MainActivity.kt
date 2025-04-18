package com.example.codenamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.LobbyScreen
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable


class MainActivity : ComponentActivity() {

    private lateinit var gameManager: GameManager

    private fun loadWords(): List<String> {
        val inputStream = resources.openRawResource(R.raw.words)
        return inputStream.bufferedReader().readLines().filter { it.isNotBlank() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameManager = GameManager{loadWords()}
        gameManager = GameManager { loadWords() }

        setContent {
            val initialGameState = remember {
                gameManager.startNewGame()
                gameManager.gameState
            }

            var gameState by remember { mutableStateOf(initialGameState) }
            var screen by rememberSaveable { mutableStateOf("menu") }

            CodenamesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (screen) {
                        "menu" -> MainMenuScreen(
                            onPlayClicked = {
                                gameManager.startNewGame()
                                gameState = gameManager.gameState
                                screen = "game"
                            },

                            onRulesClicked = {
                                screen = "rules"
                            },
                            onSettingsClicked = {
                                screen = "lobby"
                            }
                        )

                        "rules" -> RulesScreen(
                            onBack = { screen = "menu" }
                        )

                        "game" -> GameBoardScreen(
                            gameState = gameState,
                            onRestartGame = {
                                gameManager.startNewGame()
                                gameState = gameManager.gameState
                            },
                            onExitToMenu = {
                                screen = "menu"
                            },
                            onBack = {
                                screen = "menu"
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "lobby" -> LobbyScreen(
                            onBackToMain = { screen = "menu" },
                            modifier = Modifier.padding(innerPadding)
                        )

                    }
                }
            }
        }

    }
}



