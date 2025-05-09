package com.example.codenamesapp
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Role
import com.example.codenamesapp.ui.theme.ButtonsGui
import com.example.codenamesapp.ui.theme.CustomBlack
import com.example.codenamesapp.ui.theme.DarkRed
import com.example.codenamesapp.ui.theme.LightBlue


@Composable
//fun GameBoardScreen(
//    gameState: GameState,
//    onRestartGame: () -> Unit,
//    onExitToMenu: () -> Unit,
//    onBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var isSpymaster by remember { mutableStateOf(false) }
//    var isGameOver by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        SpymasterToggleButton(isSpymaster) { isSpymaster = !isSpymaster }
//
//        Column(modifier = Modifier.weight(1f)) {
//            if (isGameOver) {
//                ShowGameOverMenu(
//                    onRestart = {
//                        isGameOver = false
//                        onRestartGame()
//                    },
//                    onExit = {
//                        isGameOver = false
//                        onExitToMenu()
//                    }
//                )
//            }
//
//            GameBoardGrid(
//                board = gameState.board,
//                isSpymaster = isSpymaster,
//                isGameOver = isGameOver,
//                onCardRevealed = { card ->
//                    card.isRevealed = true
//                    if (card.role == Role.ASSASSIN) {
//                        isGameOver = true
//                    }
//                }
//            )
//        }
//
//        ButtonsGui(text = "Go back to Main Menu", onClick = { onBack() }, Modifier.fillMaxWidth().padding(top = 12.dp))
//    }
//}



//@Composable
fun SpymasterToggleButton(isSpymaster: Boolean, onToggle: () -> Unit) {
    ButtonsGui(text = if (isSpymaster) "Switch to Player View" else "Switch to Spymaster View", onClick = { onToggle() }, Modifier.fillMaxWidth())
}

@Composable
fun ShowGameOverMenu(onRestart: () -> Unit, onExit: () -> Unit) {
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
            Button(onClick = onExit) {
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

// Platzhalter für jetzt als Wörter
val wordList = arrayOf("Haus", "Hund", "Katze", "Baumhaus", "Auto", "Blume", "Buch", "Wolke", "Mond", "Wasser", "Sonne", "Karte", "Glas", "Gold", "Berg", "Computer", "Fisch", "Schütze", "Feuer", "Schnee", "Sonnenblume", "Student", "Wahl", "Getränk", "Stift")
var isSpymaster = false

@Composable
fun GameBoardScreen (
    gameState: GameState,
    onRestartGame: () -> Unit,
    onExitToMenu: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) { // general layout of gameboard
    LockLandscapeOrientation()
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Box ( // first column with "points", hint-button, expose-button and player role
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(DarkRed)
        ) {
            PlayerRoleScreen()
        }
        Box ( // second column with card grid
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(LightBlue)
        ) {
            GameBoardCards()
        }
        Box ( // third colum with chat
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .background(CustomBlack)
        ) {

        }
    }
}

@Composable
fun GameBoardCards () { // layout of part/grid where cards are on
    //val items = {1..25}.map { "Feld $it"}
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(4.dp)
    ) {

    }
}

@Composable
fun PlayerRoleScreen () {
    Row (
        verticalAlignment = Alignment.Bottom,
    ) {
        val image = painterResource(R.drawable.muster_logo)
        Image(
            painter = image,
            contentDescription = null
        )
        if (isSpymaster)
            Text("Spymaster")
        else
            Text("Operative")
    }
}

@Composable
fun LockLandscapeOrientation() { // fixed landscape orientation
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}
