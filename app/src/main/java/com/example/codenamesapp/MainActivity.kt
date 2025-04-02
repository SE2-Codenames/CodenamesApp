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
import androidx.compose.runtime.*



class MainActivity : ComponentActivity() {

    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameManager = GameManager(this)
        gameManager.startNewGame()

        setContent {
            var gameState by remember { mutableStateOf(gameManager.gameState) }

            CodenamesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameBoardScreen(
                        gameState = gameState,
                        onRestart = {
                            gameManager.startNewGame()
                            gameState = gameManager.gameState
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}



