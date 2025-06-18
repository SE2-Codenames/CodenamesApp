package com.example.codenamesapp

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.*
import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.ui.theme.*
import com.example.codenamesapp.R
import com.example.codenamesapp.model.GamePhase.*
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import kotlin.math.sqrt
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun GameBoardScreen(
    viewModel: GameStateViewModel,
    communication: Communication,
    messages: SnapshotStateList<String>
) {
    LockLandscapeOrientation()
    DetectShake(viewModel, communication)
    println("Spielerrolle vom Server (IsSpymaster): $viewModel.myIsSpymaster")


    var showOverlay by remember { mutableStateOf(false) }
    var showExpose by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val teamColor = when (viewModel.teamTurn.value) {
                    TeamRole.RED -> DarkRed
                    TeamRole.BLUE -> DarkBlue
                    else -> DarkGrey
                }

                drawRect(
                    brush = Brush.radialGradient(
                        0.0f to Color.White,
                        0.95f to Color.White,
                        1.0f to teamColor.copy(alpha = 0.2f),
                        center = center,
                        radius = size.minDimension * 1.22f
                    ),
                    size = size
                )
            }
            //.padding(WindowInsets.systemBars.asPaddingValues())
            //.consumeWindowInsets(WindowInsets.systemBars)
    ) {

        DetectThreeFinger {
            if (!viewModel.myIsSpymaster.value && viewModel.isPlayerTurn.value) {
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
                        ButtonsGui(
                            text = "Give A Hint!", onClick = {
                                if ((viewModel.teamTurn.value == viewModel.myTeam.value) && (viewModel.gameState == SPYMASTER_TURN)) {
                                    showOverlay = true
                                }
                            },
                            modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp)
                        )
                    }
                    ButtonsGui(
                        text = "Expose!", onClick = { showExpose = true },
                        modifier = Modifier.width(250.dp).height(48.dp).padding(4.dp)
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

                        val isOperativeTurn = viewModel.isPlayerTurn.value
                        val isSpymaster = viewModel.myIsSpymaster.value

                        if (index != -1 && !isAlreadyMarked && isOperativeTurn && !isSpymaster) {
                            card.isMarked.value = true
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
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter Hint:")
                    Spacer(modifier = Modifier.height(8.dp))

                    var hintWordInput by remember { mutableStateOf("") }
                    var hintNumberInput by remember { mutableStateOf("") }

                    TextField(
                        value = hintWordInput,
                        onValueChange = { hintWordInput = it },
                        label = { Text("Word") }
                    )
                    // dropdown for hintNumberInput
                    val options = if (viewModel.teamTurn.value == TeamRole.RED) {
                        (1..viewModel.scoreRed).map { it.toString() }
                    } else {
                        (1..viewModel.scoreBlue).map { it.toString() }
                    }
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .clickable { expanded = true }
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = hintNumberInput,
                            onValueChange = { },
                            label = { Text("Count") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        hintNumberInput = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    ButtonsGui("Send", onClick = {
                        showOverlay = false
                        if (hintWordInput.isNotBlank() && hintNumberInput.isNotBlank()) {
                            val word = hintWordInput.trim()
                            val number = hintNumberInput.trim().toIntOrNull() ?: 0
                            viewModel.sendHint(word, number, communication)
                            messages.add("Your Hint: $word ($number)")
                        }
                    }, modifier = Modifier.height(10.dp))
                }
            }
        }
    }

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

                        if (!viewModel.myIsSpymaster.value && viewModel.isPlayerTurn.value) {
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
    Text(viewModel.scoreRed.toString(), style = TextStyle(color = MaterialTheme.colorScheme.error, fontSize = 80.sp, fontWeight = FontWeight.Bold))
    Spacer(Modifier.width(50.dp))
    Text(viewModel.scoreBlue.toString(), style = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 80.sp, fontWeight = FontWeight.Bold))
}

@Composable
fun PlayerRoleScreen(viewModel: GameStateViewModel) {
    val image = painterResource(R.drawable.muster_logo)
    val textColor = if (viewModel.myTeam.value == TeamRole.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
    val roleText = if (viewModel.myIsSpymaster.value) "Spymaster" else "Operative"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = roleText,
            style = MaterialTheme.typography.headlineLarge.copy(color = textColor)
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
        BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    } else if (card.revealed && viewModel.myIsSpymaster.value) {
        BorderStroke(5.dp, MaterialTheme.colorScheme.error)
    } else {
        BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
    }

    val backgroundImage = if (card.revealed || viewModel.myIsSpymaster.value) {
        when (card.cardRole) {
            CardRole.RED -> MaterialTheme.colorScheme.error
            CardRole.BLUE -> MaterialTheme.colorScheme.tertiary
            CardRole.NEUTRAL -> MaterialTheme.colorScheme.secondary
            CardRole.ASSASSIN -> CustomBlack
        }
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val actualOnClick = if (!card.revealed) onClick else ({ })
    val actualOnLongClick = if (!card.revealed) onLongClick else ({ })

    Card(
        modifier = Modifier
            .height(70.dp)
            .combinedClickable(onClick = actualOnClick, onLongClick = actualOnLongClick)
            .padding(2.dp),
        border = border,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(backgroundImage)) {
            Text(
                text = card.word,
                color = Color.White,
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
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.onPrimary),
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
        title = { Text("Team entlarven") },
        text = { Text("Hat das gegnerische Team geschummelt?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Ja")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Nein")
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

