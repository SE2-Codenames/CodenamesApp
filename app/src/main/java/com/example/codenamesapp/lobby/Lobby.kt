package com.example.codenamesapp.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import com.example.codenamesapp.R
import androidx.compose.ui.res.painterResource

@Composable
fun LobbyScreen(
    playerName: String?,
    playerList: List<Player>,
    socketClient: WebSocketClient,
    gameStateViewModel: GameStateViewModel,
    onBackToConnection: () -> Unit,
    onStartGame: () -> Unit,
    sendMessage: (String) -> Unit = { socketClient.send(it) }
) {
    val localPlayer = playerList.find {
        it.name.trim().equals(playerName?.trim(), ignoreCase = true)
    }
    val isReady = remember { mutableStateOf(false) }
    val minPlayersRequired = 2
    val enoughPlayers = playerList.size >= minPlayersRequired
    val allReady = enoughPlayers && playerList.all { it.isReady }


    //Text("localPlayer gefunden: ${localPlayer?.name ?: "NEIN"}")
    //gameStateViewModel.player.value = localPlayer.name

    Image(
        painter = painterResource(R.drawable.muster_logo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        contentScale = ContentScale.Fit,
        alpha = 0.05f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Lobby", style = MaterialTheme.typography.headlineLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TeamColumn("Red Team", TeamRole.RED, playerList, playerName)
            TeamColumn("Blue Team", TeamRole.BLUE, playerList, playerName)
        }

        Spacer(modifier = Modifier.height(16.dp))

        localPlayer?.let { player ->
            Text("Wähle dein Team:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColoredToggleButton(
                    label = "Red",
                    isSelected = gameStateViewModel.myTeam.value == TeamRole.RED,
                    selectedColor = Color.Red,
                    onClick = {
                        sendMessage("JOIN_TEAM:${player.name}:RED")
                        gameStateViewModel.myTeam.value = TeamRole.RED
                        gameStateViewModel.myIsSpymaster.value = false
                    },
                    modifier = Modifier.testTag("Button_Red"),
                )
                ColoredToggleButton(
                    label = "Blue",
                    isSelected = gameStateViewModel.myTeam.value == TeamRole.BLUE,
                    selectedColor = Color.Blue,
                    onClick = {
                        sendMessage("JOIN_TEAM:${player.name}:BLUE")
                        gameStateViewModel.myTeam.value = TeamRole.BLUE
                        gameStateViewModel.myIsSpymaster.value = false
                    },
                    modifier = Modifier.testTag("Button_Blue")
                )
            }

            if (gameStateViewModel.myTeam.value != null) {

                Spacer(modifier = Modifier.height(8.dp))


                Text("Wähle deine Rolle:")

                val team = gameStateViewModel.myTeam.value!!
                val isSpymasterTaken = playerList.any {
                    it.team == team && it.isSpymaster && it.name != player.name
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColoredToggleButton(
                        label = "Spymaster",
                        isSelected = gameStateViewModel.myIsSpymaster.value == true,
                        selectedColor = if (team == TeamRole.RED) Color.Red else Color.Blue,
                        onClick = {
                            if (!gameStateViewModel.myIsSpymaster.value && isSpymasterTaken) {
                                return@ColoredToggleButton
                            }
                            sendMessage("SPYMASTER_TOGGLE:${player.name}")
                            gameStateViewModel.myIsSpymaster.value = true
                        },
                        modifier = Modifier.testTag("Button_Spymaster"),
                        enabled = !isSpymasterTaken || gameStateViewModel.myIsSpymaster.value == true
                    )
                    ColoredToggleButton(
                        label = "Operative",
                        isSelected = gameStateViewModel.myIsSpymaster.value == false,
                        selectedColor = if (team == TeamRole.RED) Color.Red else Color.Blue,
                        onClick = {
                            if (gameStateViewModel.myIsSpymaster.value == true) {
                                sendMessage("SPYMASTER_TOGGLE:${player.name}")
                            }
                            gameStateViewModel.myIsSpymaster.value = false
                        },
                        modifier = Modifier.testTag("Button_Operative")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    sendMessage("READY:${player.name}")
                    isReady.value = true
                },
                enabled = !isReady.value,
                modifier = Modifier.testTag("ReadyButton")
            ) {
                Text(if (isReady.value) "Bereit" else "Bereit ?")
            }

        }

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = onStartGame,
            enabled = allReady,
            modifier = Modifier.testTag("StartGame")
        ) {
            Text("Spiel starten")
        }

        if (!allReady) {
            Text("Warte auf alle Spieler...", style = MaterialTheme.typography.bodyMedium)
        }


        Button(onClick = onBackToConnection,  modifier = Modifier.testTag("BackButton")) {
            Text("Zurück")
        }
    }
}

@Composable
fun ColoredToggleButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) {
        Text(label)
        if (isSelected) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Check, contentDescription = "Selected")
        }
    }
}


@Composable
fun TeamColumn(
    title: String,
    team: TeamRole,
    players: List<Player>,
    playerName: String?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        players.filter { it.team == team }.forEach { player ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                val icon_name = when {
                    player.team == TeamRole.RED && player.isSpymaster -> R.drawable.icon_red_spymaster
                    player.team == TeamRole.RED && !player.isSpymaster -> R.drawable.icon_red_operative
                    player.team == TeamRole.BLUE && player.isSpymaster -> R.drawable.icon_blue_spymaster
                    player.team == TeamRole.BLUE && !player.isSpymaster -> R.drawable.icon_blue_operative
                    else -> R.drawable.icon_blue_operative
                }
                Image(
                    painter = painterResource(id = icon_name),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                //Text(player.name)

                Text(
                    buildString {
                        append(player.name)
                        if (player.name.trim().equals(playerName?.trim(), ignoreCase = true)) {
                            append(" (You)")
                        }
                    }
                )
            }
        }
    }
}
