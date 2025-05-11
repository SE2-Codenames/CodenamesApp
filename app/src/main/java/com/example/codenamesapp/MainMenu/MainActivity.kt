package com.example.codenamesapp.MainMenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.codenamesapp.R
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import com.example.codenamesapp.gamelogic.GameManager

class MainActivity : ComponentActivity() {

    private lateinit var gameManager: GameManager

    private fun loadWords(): List<String> {
        val inputStream = resources.openRawResource(R.raw.words)
        return inputStream.bufferedReader().readLines().filter { it.isNotBlank() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameManager = GameManager { loadWords() }

        setContent {
            val navController = rememberNavController()
            CodenamesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Verwende innerPadding hier, um den Hauptinhalt einzurücken
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}