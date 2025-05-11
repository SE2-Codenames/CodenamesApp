package com.example.codenamesapp
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.codenamesapp.model.Card
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Role
import com.example.codenamesapp.ui.theme.ButtonsGui
import com.example.codenamesapp.ui.theme.CustomBlack
import com.example.codenamesapp.ui.theme.DarkBlue
import com.example.codenamesapp.ui.theme.DarkGrey
import com.example.codenamesapp.ui.theme.DarkRed
import com.example.codenamesapp.ui.theme.LightBlue
import com.example.codenamesapp.ui.theme.LightGrey
import com.example.codenamesapp.ui.theme.LightRed


// Platzhalter für jetzt als Wörter
val wordList = arrayOf("Haus", "Hund", "Katze", "Baumhaus", "Auto", "Blume", "Buch", "Wolke", "Mond", "Wasser", "Sonne", "Karte", "Glas", "Gold", "Berg", "Computer", "Fisch", "Schütze", "Feuer", "Schnee", "Sonnenblume", "Student", "Wahl", "Getränk", "Stift")
val roundRed = 5
val roundBlue = 4
var isSpymaster = true
var messages = listOf("Willkommen!", "Erster Hint: Sonnenblume.", "Team Rot hat \"Sonne\" erraten.", "Zweiter Hint: Autofahren", "Team Blau hat \"Auto\" erraten.", "Dritter Hint: Freizeit", "Team Rot hat \"Karte\" erraten.")
// Card-Class auch als Platzhalter
val cardList = listOf(Card("Haus", Role.RED), Card("Hund", Role.NEUTRAL), Card("Katze", Role.RED), Card("Baumhaus", Role.NEUTRAL), Card("Auto", Role.BLUE), Card("Blume", Role.ASSASSIN), Card("Buch", Role.BLUE), Card("Wolke", Role.NEUTRAL), Card("Mond", Role.NEUTRAL), Card("Wasser", Role.RED), Card("Sonne", Role.RED), Card("Karte", Role.NEUTRAL), Card("Glas", Role.NEUTRAL), Card("Gold", Role.BLUE), Card("Berg", Role.NEUTRAL), Card("Computer", Role.BLUE), Card("Fisch", Role.NEUTRAL), Card("Schütze", Role.NEUTRAL), Card("Feuer", Role.NEUTRAL), Card("Schnee", Role.NEUTRAL), Card("Sonnenblume", Role.NEUTRAL), Card("Student", Role.NEUTRAL), Card("Wahl", Role.NEUTRAL), Card("Getränk", Role.NEUTRAL), Card("Stift", Role.NEUTRAL))


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
        // ---------------------------------------------------------------------------------------
        Box ( // first column with "points", hint-button, expose-button and player role
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                //.background(DarkRed)
        ) {
            Row (
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardsRemaining()
            }
            Column (
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) { // TODO: fix link target!!
                if (isSpymaster) {
                    ButtonsGui(text = "Give A Hint!", onClick = { onBack() },Modifier
                        .width(250.dp)
                        .height(48.dp)
                        .padding(4.dp))
                }
                ButtonsGui(text = "Expose!", onClick = { onBack() }, Modifier
                    .width(250.dp)
                    .height(48.dp)
                    .padding(4.dp))
            }
            Column (
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                PlayerRoleScreen()
            }
        }

        // ---------------------------------------------------------------------------------------
        Box ( // second column with card grid
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                //.background(LightBlue)
        ) {
            GameBoardGrid(
                onCardClicked = { /*TODO*/ },
                onCardMarked = { /*TODO*/ }
            )
        }

    // ---------------------------------------------------------------------------------------
        Box ( // third colum with chat
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight(),
//                .background(DarkRed),
            contentAlignment = Alignment.BottomCenter
        ) {
            ChatBox(messages = messages)
        }
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


// --- column one ----------------------------------------------------------------------------------
@Composable
fun CardsRemaining () { // displays how many cards each team has remaining
    Text(roundRed.toString(), style = TextStyle(
        color = LightRed,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    )
    )
    Spacer(Modifier.width(50.dp))
    Text(roundBlue.toString(), style = TextStyle(
        color = LightBlue,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    ))
}

@Composable
fun PlayerRoleScreen () { // displays the role-image and player role
    val image = painterResource(R.drawable.muster_logo)
    Box(Modifier
        .height(80.dp)
        .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center) {
        Image(
            painter = image,
            contentDescription = null
        )
    }
    if (isSpymaster)
        Text(text = "Spymaster", style = MaterialTheme.typography.headlineLarge)
    else
        Text(text = "Operative", style = MaterialTheme.typography.headlineLarge)
}


// --- column two ----------------------------------------------------------------------------------
@Composable
fun GameBoardGrid ( // layout of part/grid where cards are on
    onCardClicked : (Card) -> Unit,
    onCardMarked : (Card) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(cardList) {card ->
            GameCard (
                card = card,
                onClick = { onCardClicked(card) },
                onLongClick = { onCardMarked(card) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameCard ( // creates displayable card
    card : Card,
    onClick : () -> Unit,
    onLongClick : () -> Unit
) {
    // if card is marked by player, card appears with thicker border
    val border = if (card.isMarked) BorderStroke(3.dp, CustomBlack) else BorderStroke(0.5.dp, CustomBlack)

    // if card is revealed or player is spymaster, cards appear with role as background, otherwise they are grey
    val backgroundImage = if (card.isRevealed || isSpymaster) {
        when (card.role) {
            Role.RED -> DarkRed
            Role.BLUE -> DarkBlue
            Role.NEUTRAL -> DarkGrey
            Role.ASSASSIN -> CustomBlack
            // später: painterResource(R.drawable.[...]) für image einfügen
        }
    } else {
        DarkGrey
    }

    // creating displayable card
    Card (
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(2.dp),
        border = border,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundImage)
        ) {
            Text (
                text = card.word,
                color = LightGrey,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
        }
    }
}

// --- column three --------------------------------------------------------------------------------
@Composable
fun ChatBox (messages : List<String>) { // displays server chat messages
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.66f)
            .padding(8.dp)
            .border(1.dp, CustomBlack, shape = RoundedCornerShape(2.dp))
            .background(Color.White),
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true
    ) {
        itemsIndexed(messages.reversed()) {
            index, messages ->

            val backgroundColor = if (index % 2 != 0) LightGrey else Color.Transparent
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(backgroundColor)
            ) {
                Text(text = messages, color = CustomBlack)
            }
        }
    }
}
