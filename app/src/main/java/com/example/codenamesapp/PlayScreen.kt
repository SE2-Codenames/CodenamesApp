package com.example.codenamesapp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.codenamesapp.model.Card
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Role
import android.app.Activity
import androidx.compose.ui.platform.LocalContext


@Composable
fun GameBoardScreen(
    gameState: GameState,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var isSpymaster by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        SpymasterToggleButton(isSpymaster) { isSpymaster = !isSpymaster }

        Spacer(modifier = Modifier.height(12.dp))

        if (isGameOver) {
            ShowGameOverMenu(
                onRestart = {
                    onRestart()
                    isGameOver = false
                },
                onClose = { activity?.finish() }
            )
        }

        GameBoardGrid(
            board = gameState.board,
            isSpymaster = isSpymaster,
            isGameOver = isGameOver,
            onCardRevealed = { card ->
                card.isRevealed = true
                if (isAssassin(card)) {
                    isGameOver = true
                }
            }
        )
    }
}

@Composable
fun SpymasterToggleButton(isSpymaster: Boolean, onToggle: () -> Unit) {
    Button(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = if (isSpymaster) "Switch to Player View" else "Switch to Spymaster View")
    }
}

@Composable
fun ShowGameOverMenu(onRestart: () -> Unit, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Game Over!") },
        text = { Text("The assassin was revealed!") },
        confirmButton = {
            Button(onClick = onRestart) {
                Text("Play Again")
            }
        },
        dismissButton = {
            Button(onClick = onClose) {
                Text("Exit")
            }
        }
    )
}

@Composable
fun GameBoardGrid(
    board: List<Card>,
    isSpymaster: Boolean,
    isGameOver: Boolean,
    onCardRevealed: (Card) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(board) { card ->
            GameBoardTiles(
                card = card,
                isSpymaster = isSpymaster,
                isGameOver = isGameOver,
                onClick = { onCardRevealed(card) }
            )
        }
    }
}

@Composable
fun GameBoardTiles(
    card: Card,
    isSpymaster: Boolean,
    isGameOver: Boolean,
    onClick: () -> Unit
) {
    val isRevealed = card.isRevealed
    val backgroundColor = getCardColor(card, isRevealed, isSpymaster)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = isCardClickable(isRevealed, isSpymaster, isGameOver)) {
                onClick()
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
                color = if (backgroundColor == Color.Black) Color.White else Color.Black
            )
        }
    }
}

private fun isAssassin(card: Card): Boolean = card.role == Role.ASSASSIN

private fun isCardClickable(isRevealed: Boolean, isSpymaster: Boolean, isGameOver: Boolean): Boolean {
    return !isRevealed && !isSpymaster && !isGameOver
}

private fun getCardColor(card: Card, isRevealed: Boolean, isSpymaster: Boolean): Color {
    return when {
        isSpymaster || isRevealed -> when (card.role) {
            Role.RED -> Color.Red
            Role.BLUE -> Color.Blue
            Role.NEUTRAL -> Color.Gray
            Role.ASSASSIN -> Color.Black
        }
        else -> Color.LightGray
    }
}
