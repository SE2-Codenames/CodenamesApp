package com.example.codenamesapp


import android.app.Activity
import android.content.pm.ActivityInfo
<<<<<<< HEAD
import androidx.compose.foundation.*
=======
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.ui.theme.*
import kotlinx.serialization.json.Json

@Composable
fun GameBoardScreen(
    gameState: PayloadResponseMove,
    playerRole: Boolean,
    team: TeamRole,
    communication: Communication
) {
    LockLandscapeOrientation()

    val isSpymaster = playerRole

    val cardList = remember(gameState) {
        println("📦 Empfangene Karten:")
        val preparedCards = gameState.card.map { card ->
            println("🔹 ${card.word}, role=${card.cardRole}, revealed=${card.revealed}")
            card.apply {
                isMarked = mutableStateOf(false) // 💥 explizite Initialisierung!
            }
        }

        mutableStateListOf<Card>().apply {
            addAll(preparedCards)
        }
    }

    // ✅ Punkte robust lesen
    val scoreRed = gameState.score.getOrNull(0) ?: 0
    val scoreBlue = gameState.score.getOrNull(1) ?: 0
    val initialHint = gameState.hint ?: "–"
    val initialRemainingGuesses = gameState.remainingGuesses

<<<<<<< HEAD
    // 📨 Chatnachrichten (UI)
=======
    val isPlayerTurn = !isSpymaster && teamRole == team && currentGameState == GamePhase.OPERATIVE_TURN

>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
    val messages = remember {
        mutableStateListOf("Willkommen!", "Erster Hinweis: $initialHint ($initialRemainingGuesses).")
    }

    var showOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            // Column 1
            Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                Row(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopCenter).padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CardsRemaining(redScore = scoreRed, blueScore = scoreBlue)
                }
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(8.dp)
                ) {
                    if (isSpymaster) {
                        ButtonsGui(
                            text = "Give A Hint!", onClick = { showOverlay = true },
                            modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp)
                        )
                    }
                    ButtonsGui(
                        text = "Expose!", onClick = { /* TODO */ },
                        modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp)
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    PlayerRoleScreen(isSpymaster, team)
                }
            }

            // Column 2
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                GameBoardGrid(
                    onCardClicked = { card ->
                        val index = cardList.indexOf(card)
                        if (index != -1) communication.giveCard(index)
                    },
<<<<<<< HEAD
                    onCardMarked = { card -> card.isMarked.value = !card.isMarked.value },
                    cardList = cardList,
                    isSpymaster = isSpymaster
=======
                    onCardMarked = { card -> card.isMarked = !card.isMarked },
                    cardList,
                    isSpymaster,
                    isPlayerTurn
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
                )
            }

            // Column 3
            Box(
                modifier = Modifier.weight(0.3f).fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ChatBox(messages = messages)
            }
        }
    }

    if (showOverlay) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(8.dp),
<<<<<<< HEAD
                modifier = Modifier.width(300.dp).wrapContentHeight().background(Color.White)
=======
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.onPrimary)
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter Hint:")
                    Spacer(modifier = Modifier.height(8.dp))

                    var hintWordInput by remember { mutableStateOf("") }
                    var hintNumberInput by remember { mutableStateOf("") }

                    TextField(
                        value = hintWordInput,
                        onValueChange = { hintWordInput = it },
                        label = { Text("Wort") }
                    )
                    TextField(
                        value = hintNumberInput,
                        onValueChange = { hintNumberInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Anzahl") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    ButtonsGui("Senden", onClick = {
                        showOverlay = false
                        if (hintWordInput.isNotBlank() && hintNumberInput.isNotBlank()) {
                            val word = hintWordInput.trim()
                            val number = hintNumberInput.trim().toIntOrNull() ?: 0
                            communication.giveHint(word, number)
                            messages.add("Dein Hinweis: $word ($number)")
                        }
                    }, modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun LockLandscapeOrientation() {
    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

@Composable
<<<<<<< HEAD
fun CardsRemaining(redScore: Int, blueScore: Int) {
    Text("$redScore", style = TextStyle(color = LightRed, fontSize = 80.sp, fontWeight = FontWeight.Bold))
    Spacer(Modifier.width(50.dp))
    Text("$blueScore", style = TextStyle(color = LightBlue, fontSize = 80.sp, fontWeight = FontWeight.Bold))
=======
fun CardsRemaining (redScore: Int, blueScore: Int) { // displays how many cards each team has remaining
    Text(redScore.toString(), style = TextStyle(
        color = MaterialTheme.colorScheme.error,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    ))
    Spacer(Modifier.width(50.dp))
    Text(blueScore.toString(), style = TextStyle(
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold
    ))
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
}

@Composable
fun PlayerRoleScreen(isSpymaster: Boolean, teamRole: TeamRole) {
    val image = painterResource(R.drawable.muster_logo)
<<<<<<< HEAD
    Box(Modifier.height(80.dp).padding(bottom = 10.dp), contentAlignment = Alignment.Center) {
        Image(painter = image, contentDescription = null)
    }
    val textColor = if (teamRole == TeamRole.RED) DarkRed else DarkBlue
    val roleText = if (isSpymaster) "Spymaster" else "Operative"
    Text(roleText, style = MaterialTheme.typography.headlineLarge.copy(color = textColor))
=======
    Box(Modifier
        .height(80.dp)
        .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.muster_logo),
            contentDescription = null
        )
    }
    val textColor = if (teamRole == TeamRole.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
    if (isSpymaster)
        Text(text = "Spymaster", style = MaterialTheme.typography.headlineLarge.copy(color = textColor))
    else
        Text(text = "Operative", style = MaterialTheme.typography.headlineLarge.copy(color = textColor))
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
}

@Composable
fun GameBoardGrid(
    onCardClicked: (Card) -> Unit,
    onCardMarked: (Card) -> Unit,
    cardList: List<Card>,
    isSpymaster: Boolean,
    isPlayerTurn : Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(cardList) { card ->
<<<<<<< HEAD
            GameCard(card, { onCardClicked(card) }, { onCardMarked(card) }, isSpymaster)
=======
            GameCard (
                card = card,
                onClick = if (isPlayerTurn) { { onCardClicked(card) } } else { {} },
                onLongClick = if (isPlayerTurn) { { onCardMarked(card) }} else { {} },
                isSpymaster = isSpymaster
            )
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
<<<<<<< HEAD
fun GameCard(card: Card, onClick: () -> Unit, onLongClick: () -> Unit, isSpymaster: Boolean) {
    val border = if (card.isMarked.value) BorderStroke(3.dp, CustomBlack) else BorderStroke(0.5.dp, CustomBlack)
    val backgroundImage = when {
        card.revealed || isSpymaster -> when (card.cardRole) {
            CardRole.RED -> DarkRed
            CardRole.BLUE -> DarkBlue
            CardRole.NEUTRAL -> DarkGrey
            CardRole.ASSASSIN -> CustomBlack
        }
        else -> DarkGrey
=======
fun GameCard ( // creates displayable card
    card : Card,
    onClick : () -> Unit,
    onLongClick : () -> Unit,
    isSpymaster: Boolean
) {
    // if card is marked by player, card appears with thicker border
    val border = if (card.isMarked) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)

    // if card is revealed or player is spymaster, cards appear with role as background, otherwise they are grey
    val backgroundImage = if (card.isRevealed || isSpymaster) {
        when (card.role) {
            Role.RED -> MaterialTheme.colorScheme.error
            Role.BLUE -> MaterialTheme.colorScheme.tertiary
            Role.NEUTRAL -> MaterialTheme.colorScheme.secondary
            Role.ASSASSIN -> CustomBlack
            // später: painterResource(R.drawable.[...]) für image einfügen
        }
    } else {
        MaterialTheme.colorScheme.secondary
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
    }

    Card(
        modifier = Modifier.height(70.dp).combinedClickable(onClick = onClick, onLongClick = onLongClick).padding(2.dp),
        border = border,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
<<<<<<< HEAD
        Box(Modifier.fillMaxSize().background(backgroundImage)) {
            Text(card.word, color = LightGrey, modifier = Modifier.align(Alignment.Center).padding(4.dp))
=======
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundImage)
        ) {
            Text (
                text = card.word,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
        }
    }
}

@Composable
<<<<<<< HEAD
fun ChatBox(messages: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.66f).padding(8.dp)
            .border(1.dp, CustomBlack, RoundedCornerShape(2.dp)).background(Color.White),
=======
fun ChatBox (messages : List<String>) { // displays server chat messages
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.66f)
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.onPrimary),
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true
    ) {
        itemsIndexed(messages.reversed()) { index, message ->
<<<<<<< HEAD
            val bg = if (index % 2 != 0) LightGrey else Color.Transparent
            Box(Modifier.fillMaxWidth().padding(4.dp).background(bg)) {
                Text(message, color = CustomBlack)
=======
            val backgroundColor = if (index % 2 != 0) MaterialTheme.colorScheme.secondary else Color.Transparent
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(backgroundColor)
            ) {
                Text(text = message, color = MaterialTheme.colorScheme.primary)
>>>>>>> 3ea1613f0e5fc685bfcea80682a8d28d867c689c
            }
        }
    }
}
