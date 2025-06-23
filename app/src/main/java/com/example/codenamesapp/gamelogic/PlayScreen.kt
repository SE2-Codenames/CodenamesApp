package com.example.codenamesapp.gamelogic

import android.app.Activity
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codenamesapp.model.*
import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.ui.theme.*
import com.example.codenamesapp.R
import com.example.codenamesapp.model.GamePhase.*
import kotlin.math.sqrt
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.codenamesapp.MainMenu.GameEndResult


@Composable
fun GameBoardScreen(
    navController: NavHostController,
    viewModel: GameStateViewModel,
    communication: Communication,
    messages: SnapshotStateList<String>
) {
    LockLandscapeOrientation()
    DetectShake(viewModel, communication)
    println("Spielerrolle vom Server (IsSpymaster): $viewModel.myIsSpymaster")


    var showOverlay by remember { mutableStateOf(false) }
    var showSkipDialog by remember { mutableStateOf(false) }
    var showExpose by remember { mutableStateOf(false) }
    val canExpose = viewModel.teamTurn.value != viewModel.myTeam.value && viewModel.gameState == OPERATIVE_TURN

    GradientBoxBorder(
        modifier = Modifier
            .fillMaxSize(),
        viewModel = viewModel
    ) {

        if (canExpose) {
            DetectThreeFinger {
                showExpose = true
            }
        }


        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            // Column 1: Info & Buttons
            Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                Row(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopCenter).padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CardsRemaining(viewModel = viewModel)
                }
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(8.dp)
                ) {
                    if (viewModel.myIsSpymaster.value) {
                        val hintButtonClickable = (viewModel.teamTurn.value == viewModel.myTeam.value) && (viewModel.gameState == SPYMASTER_TURN)
                        ButtonsGui(
                            text = "Give A Hint!", onClick = {
                                if (hintButtonClickable) {
                                    showOverlay = true
                                }
                            },
                            enabled = hintButtonClickable,
                            modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp)
                        )
                    } else {
                        val skipButtonClickable = (viewModel.teamTurn.value == viewModel.myTeam.value) && (viewModel.gameState == OPERATIVE_TURN)
                        ButtonsGui(text = "Skip!", onClick = {
                            if (skipButtonClickable) {
                                showSkipDialog = true
                            }
                        }, enabled = skipButtonClickable, modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp))
                    }

                    ButtonsGui(
                        text = "Expose!",
                        onClick = { if (canExpose) showExpose = true },
                        modifier = Modifier
                            .width(250.dp)
                            .height(48.dp)
                            .padding(4.dp),
                        enabled = canExpose
                    )

                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    PlayerRoleScreen(viewModel = viewModel)
                }
            }

            // Column 2: Card Grid
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                GameBoardGrid(
                    onCardClicked = { card ->
                        val index = viewModel.cardList.indexOf(card)
                        if (index != -1) viewModel.handleCardClick(index, communication)
                    },
                    onCardMarked = { card ->
                        val index = viewModel.cardList.indexOf(card)
                        val isAlreadyMarked = card.isMarked.value

                        val isOperativeTurn = viewModel.isPlayerTurn
                        val isSpymaster = viewModel.myIsSpymaster.value

                        if (index != -1 && isOperativeTurn && !isSpymaster) {
                            if (!isAlreadyMarked){
                                card.isMarked.value = true
                            }else{
                                card.isMarked.value = false
                            }
                            viewModel.markCard(index, communication)
                        }
                    },
                    viewModel = viewModel
                )
            }

            // Column 3: ChatBox
            Box(
                modifier = Modifier.weight(0.3f).fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ChatBox(messages = messages)
            }
        }
    }

    // HINT Screen if showOverlay = true
    if (showOverlay) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier .width(300.dp).wrapContentHeight().background(MaterialTheme.colorScheme.onPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter Hint:")
                    Spacer(modifier = Modifier.height(8.dp))

                    var hintWordInput by remember { mutableStateOf("") }
                    var hintNumberInput by remember { mutableStateOf("") }

                    TextField(
                        value = hintWordInput,
                        onValueChange = { hintWordInput = it },
                        label = { Text("Word") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Zähler für Hint-Anzahl (statt Dropdown)
                    val maxHintNumber = if (viewModel.teamTurn.value == TeamRole.RED) {
                        viewModel.scoreRed.value
                    } else {
                        viewModel.scoreBlue.value
                    }

                    // Init, falls leer
                    if (hintNumberInput.isBlank()) hintNumberInput = "1"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        val current = hintNumberInput.toIntOrNull() ?: 1
                        OutlinedButton(
                            onClick = {
                                if (current > 1) {
                                    hintNumberInput = (current - 1).toString()
                                }
                            },
                            enabled = current > 1,
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text("-", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                        }

                        Text(
                            text = hintNumberInput,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                if (current < maxHintNumber) {
                                    hintNumberInput = (current + 1).toString()
                                }
                            },
                            enabled = current < maxHintNumber,
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text("+", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ButtonsGui("Send", onClick = {
                        showOverlay = false
                        val number = hintNumberInput.toIntOrNull() ?: 0
                        if (hintWordInput.isNotBlank() && number > 0) {
                            viewModel.sendHint(hintWordInput.trim(), number, communication)
                        }
                    }, modifier = Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp))
                }
            }
        }
    }

    //Skipp Screen
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = { Text("Skip Turn") },
            text = { Text("Are you sure you want to skip your turn?") },
            confirmButton = {
                TextButton(onClick = {
                    showSkipDialog = false
                    communication.send("SKIP_TURN")
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSkipDialog = false
                }) {
                    Text("No")
                }
            }
        )
    }

    // EXPOSE Screen
    if (showExpose) {
        ExposeDialog(
            onConfirm = {
                showExpose = false
                communication.expose()
            },
            onDismiss = {
                showExpose = false
            }
        )
    }
    val gameEndResult = viewModel.gameEndResult.value

    LaunchedEffect(gameEndResult) {
        gameEndResult?.let { result ->
            navController.navigate(
                "gameover?team=${result.winningTeam}&assassin=${result.isAssassinTriggered}" +
                        "&scoreRed=${result.scoreRed}&scoreBlue=${result.scoreBlue}"
            ) {
                popUpTo("game") { inclusive = true }
            }
        }
    }
}

@Composable
fun GradientBoxBorder (modifier: Modifier, viewModel: GameStateViewModel, content: @Composable BoxScope.() -> Unit) {
    var teamColor = when (viewModel.teamTurn.value) {
        TeamRole.RED -> DarkRed
        TeamRole.BLUE -> DarkBlue
        else -> DarkGrey
    }
    teamColor = teamColor.copy(alpha = 0.5f)

    Box(modifier = modifier) {
        // oben
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(teamColor, Color.Transparent)
                    )
                )
        )
        // unten
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, teamColor)
                    )
                )
        )
        // links
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(teamColor, Color.Transparent)
                    )
                )
        )
        // rechts
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .align(Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, teamColor)
                    )
                )
        )
        // content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            content = content
        )
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
fun DetectShake(viewModel:GameStateViewModel, communication: Communication) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var lastShakeTime by remember { mutableStateOf(0L) }
    val shakeThreshold = 12f

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val (x, y, z) = it.values
                    val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
                    val currentTime = System.currentTimeMillis()

                    if (acceleration > shakeThreshold && currentTime - lastShakeTime > 1000) {
                        lastShakeTime = currentTime

                        if (!viewModel.myIsSpymaster.value && viewModel.isPlayerTurn) {
                            println("Shake erkannt – sende clearMarks")
                            communication.sendClearMarks()
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

// Column 1: Remaining Cards, Player Role, Logo ----------------------------------------------------
@Composable
fun CardsRemaining(viewModel: GameStateViewModel) {
    Text(viewModel.scoreRed.value.toString(), style = TextStyle(color = MaterialTheme.colorScheme.error, fontSize = 80.sp, fontWeight = FontWeight.Bold))
    Spacer(Modifier.width(50.dp))
    Text(viewModel.scoreBlue.value.toString(), style = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 80.sp, fontWeight = FontWeight.Bold))
}

@Composable
fun PlayerRoleScreen(viewModel: GameStateViewModel) {
    val textColor = if (viewModel.myTeam.value == TeamRole.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = if (viewModel.myIsSpymaster.value) "Spymaster" else "Operative",
            style = MaterialTheme.typography.headlineLarge.copy(color = textColor)
        )
        Image(
            painter = painterResource(if (!isSystemInDarkTheme()) R.drawable.muster_logo_white else R.drawable.muster_logo_black),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 48.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.05f
        )
    }
}

// Colum 2: Card Grid ------------------------------------------------------------------------------
@Composable
fun GameBoardGrid(
    onCardClicked: (Card) -> Unit,
    onCardMarked: (Card) -> Unit,
    viewModel: GameStateViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(viewModel.cardList) { card ->
            GameCard(
                viewModel= viewModel,
                card = card,
                onClick = { onCardMarked(card) },
                onLongClick = { onCardClicked(card) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameCard(viewModel: GameStateViewModel, card: Card, onClick: () -> Unit, onLongClick: () -> Unit) {
    val border = if (card.isMarked.value) {
        BorderStroke(3.dp, MaterialTheme.colorScheme.onSecondary)
    } else {
        BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
    }

    val backgroundImage = remember(card.word) {
        when (card.cardRole) {
            CardRole.RED -> viewModel.redCards.random()
            CardRole.BLUE -> viewModel.blueCards.random()
            CardRole.NEUTRAL -> viewModel.neutralCards.random()
            CardRole.ASSASSIN -> viewModel.assasinCard
        }
    }

    val backgroundModifier = when {
        card.revealed -> {
            Modifier.background(Color.Transparent).paint(
                painterResource(backgroundImage),
                contentScale = ContentScale.Crop
            )
                .graphicsLayer(alpha = 0.5f)
        }
        viewModel.myIsSpymaster.value -> {
            Modifier.background(
                when (card.cardRole) {
                    CardRole.RED -> MaterialTheme.colorScheme.error
                    CardRole.BLUE -> MaterialTheme.colorScheme.tertiary
                    CardRole.NEUTRAL -> MaterialTheme.colorScheme.secondary
                    CardRole.ASSASSIN -> CustomBlack
                }
            )
        }
        else -> Modifier.background(MaterialTheme.colorScheme.secondary)
    }

    val canMark = viewModel.isPlayerTurn

    val actualOnClick = if (!card.revealed && canMark) onClick else ({})
    val actualOnLongClick = if (!card.revealed && canMark) onLongClick else ({})

    Card(
        modifier = Modifier
            .height(70.dp)
            .combinedClickable(onClick = actualOnClick, onLongClick = actualOnLongClick)
            .padding(2.dp),
        border = border,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().then(backgroundModifier)) {
            Text(
                text = card.word,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center).padding(4.dp)
            )
        }
    }
}


// Column 3: ChatBox -------------------------------------------------------------------------------
@Composable
fun ChatBox(messages: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.66f)
            .padding(8.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp)),
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true
    ) {
        itemsIndexed(messages.reversed()) { index, message ->
            val backgroundColor = if (index % 2 != 0) MaterialTheme.colorScheme.secondary else Color.Transparent
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(backgroundColor)
            ) {
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ExposeDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Expose Team") },
        text = { Text("Did the opposing team cheat?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun DetectThreeFinger(onTripleTap: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val downCount = event.changes.count { it.pressed }
                        if (downCount == 3) {
                            onTripleTap()
                        }
                    }
                }
            }
    )
}

