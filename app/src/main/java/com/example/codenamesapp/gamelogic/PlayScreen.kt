package com.example.codenamesapp
import com.example.codenamesapp.Communication
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.Role
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.ui.theme.ButtonsGui
import com.example.codenamesapp.ui.theme.CustomBlack
import com.example.codenamesapp.ui.theme.DarkBlue
import com.example.codenamesapp.ui.theme.DarkGrey
import com.example.codenamesapp.ui.theme.DarkRed
import com.example.codenamesapp.ui.theme.LightBlue
import com.example.codenamesapp.ui.theme.LightGrey
import com.example.codenamesapp.ui.theme.LightRed


@Composable
fun GameBoardScreen(
    gameState: PayloadResponseMove, // Verwende den Payload direkt
    playerRole: Boolean,
    team: TeamRole,
    communication: Communication // Übergabe der Communication-Instanz
) { // general layout of gameboard
    LockLandscapeOrientation()

    // current player for GUI for either Spymaster or Operator
    val isSpymaster = playerRole

    // getting TeamRole, Cards and GameState
    val cardList = remember { mutableStateListOf<Card>().apply { addAll(gameState.card) } } // for isMarked cards
    val teamRole = gameState.teamRole
    val currentGameState = gameState.gameState
    val scoreRed = gameState.score.getOrNull(0) ?: 0
    val scoreBlue = gameState.score.getOrNull(1) ?: 0
    val initialHint = gameState.hint
    val initialRemainingGuesses = gameState.remainingGuesses

    val messages = remember {
        mutableStateListOf("Willkommen!", "Erster Hinweis: $initialHint ($initialRemainingGuesses).")
    }

    // for Overlay for "Give A Hint!"-Button and Input
    var showOverlay by remember { mutableStateOf(false) }

    Box (modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // ---------------------------------------------------------------------------------------
            Box( // first column with "points", hint-button, expose-button and player role
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CardsRemaining(redScore = scoreRed, blueScore = scoreBlue)
                }
                Column( // Buttons für "Expose!" und "Give A Hint!"
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                ) {
                    if (isSpymaster) {
                        ButtonsGui(
                            text = "Give A Hint!", onClick = { showOverlay = true }, Modifier
                                .width(250.dp)
                                .height(48.dp)
                                .padding(4.dp)
                        )
                    }
                    ButtonsGui(
                        text = "Expose!", onClick = { /*TODO: send word to server to check cheating*/ }, Modifier
                            .width(250.dp)
                            .height(48.dp)
                            .padding(4.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    PlayerRoleScreen(isSpymaster, teamRole)
                }
            }

            // ---------------------------------------------------------------------------------------
            Box( // second column with card grid
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                GameBoardGrid(
                    onCardClicked = { card ->
                        val index = cardList.indexOf(card)
                        if (index != -1)
                            communication.giveCard(index) // Verwende die übergebene Instanz
                    },
                    onCardMarked = { card -> card.isMarked = !card.isMarked },
                    cardList,
                    isSpymaster
                )
            }

            // ---------------------------------------------------------------------------------------
            Box( // third colum with chat
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ChatBox(messages = messages)
            }
        }
    }

    if (showOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable (enabled = false) {  },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .background(Color.White)
            ) {
                Column (
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter Hint:")
                    Spacer(modifier = Modifier.height(8.dp))

                    var hintWordInput by remember { mutableStateOf("") }
                    var hintNumberInput by remember { mutableStateOf("") }
                    TextField(
                        value = hintWordInput,
                        onValueChange = { hintWordInput = it},
                        label = { Text("Wort") })
                    TextField(
                        value = hintNumberInput,
                        onValueChange = { hintNumberInput = it.filter { c -> c.isDigit() }},
                        label = { Text("Anzahl") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                    Spacer(modifier = Modifier.height(8.dp))
                    ButtonsGui("Senden", onClick = {
                        showOverlay = false
                        if (hintWordInput.isNotBlank() && hintNumberInput.isNotBlank()) {
                            val hintArray = arrayOf(hintWordInput.trim(), hintNumberInput.trim())
                            communication.giveHint(hintArray)
                            messages.add("Dein Hinweis: ${hintArray[0]} (${hintArray[1]})")
                        }
                    }, Modifier.height(10.dp))
                }
            }
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
fun CardsRemaining (redScore: Int, blueScore: Int) { // displays how many cards each team has remaining
    Text(redScore.toString(), style = TextStyle(
        color = LightRed,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    ))
    Spacer(Modifier.width(50.dp))
    Text(blueScore.toString(), style = TextStyle(
        color = LightBlue,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    ))
}

@Composable
fun PlayerRoleScreen (
    isSpymaster: Boolean,
    teamRole: TeamRole
) { // displays the role-image and player role
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
    val textColor = if (teamRole == TeamRole.RED) DarkRed else DarkBlue
    if (isSpymaster)
        Text(text = "Spymaster", style = MaterialTheme.typography.headlineLarge.copy(color = textColor))
    else
        Text(text = "Operative", style = MaterialTheme.typography.headlineLarge.copy(color = textColor))
}


// --- column two ----------------------------------------------------------------------------------
@Composable
fun GameBoardGrid ( // layout of part/grid where cards are on
    onCardClicked : (Card) -> Unit,
    onCardMarked : (Card) -> Unit,
    cardList: List<Card>,
    isSpymaster: Boolean
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
        items(cardList) { card ->
            GameCard (
                card = card,
                onClick = { onCardClicked(card) },
                onLongClick = { onCardMarked(card) },
                isSpymaster = isSpymaster
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameCard ( // creates displayable card
    card : Card,
    onClick : () -> Unit,
    onLongClick : () -> Unit,
    isSpymaster: Boolean
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
            .height(70.dp)
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
        itemsIndexed(messages.reversed()) { index, message ->
            val backgroundColor = if (index % 2 != 0) LightGrey else Color.Transparent
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(backgroundColor)
            ) {
                Text(text = message, color = CustomBlack)
            }
        }
    }
}