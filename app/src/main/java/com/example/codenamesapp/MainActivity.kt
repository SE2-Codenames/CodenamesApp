package com.example.codenamesapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.model.Card
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.codenamesapp.model.Role


class MainActivity : ComponentActivity() {

    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameManager = GameManager(this)
        gameManager.startNewGame()

        val board = gameManager.gameState.board

        setContent {
            CodenamesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameBoardScreen(
                        modifier = Modifier.padding(innerPadding),
                        board = board
                    )
                }
            }
        }
    }
}



@Composable
fun GameBoardScreen(
    board: List<Card>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(board) { card ->
            var isRevealed by remember { mutableStateOf(card.isRevealed) }

            val backgroundColor = when {
                !isRevealed -> Color.LightGray
                card.role == Role.RED -> Color.Red
                card.role == Role.BLUE -> Color.Blue
                card.role == Role.NEUTRAL -> Color.Gray
                card.role == Role.ASSASSIN -> Color.Black
                else -> Color.White
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable(enabled = !isRevealed) {
                        isRevealed = true
                        card.isRevealed = true
                    },
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = card.word,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (backgroundColor == Color.Black) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

