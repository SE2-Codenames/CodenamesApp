package com.example.codenamesapp.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.R
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient
import com.example.codenamesapp.ui.theme.ButtonsGui

@Composable
fun LobbyScreen(
    playerList: List<Player>,
    socketClient: WebSocketClient,
    gameStateViewModel: GameStateViewModel,
    onBackToConnection: () -> Unit,
    onStartGame: () -> Unit,
    sendMessage: (String) -> Unit = { socketClient.send(it) }
) {
    val ownName by gameStateViewModel.ownPlayerName
    val localPlayer = playerList.find {
        it.name.trim().equals(ownName?.trim(), ignoreCase = true)
    }

    val minPlayersRequired = 2
    val enoughPlayers = playerList.size >= minPlayersRequired
    val allReady = enoughPlayers && playerList.all { it.isReady }

    val isAlreadyReady = localPlayer?.isReady == true

    Image(
        painter = painterResource(if (isSystemInDarkTheme()) R.drawable.muster_logo_black else R.drawable.muster_logo_white),
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("LobbyScrollArea"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Lobby", style = MaterialTheme.typography.headlineLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TeamColumn("Red Team", TeamRole.RED, playerList, ownName, gameStateViewModel)
            TeamColumn("Blue Team", TeamRole.BLUE, playerList, ownName, gameStateViewModel)

        }

        Spacer(modifier = Modifier.height(16.dp))

        localPlayer?.let { player ->
            Text("Choose your Team:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColoredToggleButton(
                    label = "Red",
                    isSelected = gameStateViewModel.myTeam.value == TeamRole.RED,
                    selectedColor = MaterialTheme.colorScheme.error,
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
                    selectedColor = MaterialTheme.colorScheme.tertiary,
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
                Text("Choose your Role:")

                val team = gameStateViewModel.myTeam.value!!
               val isSpymasterTaken = playerList.any {
                    it.team == team && it.isSpymaster && it.name != player.name
                }

                val isLocalPlayerSpymaster = gameStateViewModel.myIsSpymaster.value
                val spymasterButtonEnabled = !isSpymasterTaken || isLocalPlayerSpymaster




                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColoredToggleButton(
                        label = "Spymaster",
                        isSelected = isLocalPlayerSpymaster,
                        selectedColor = if (team == TeamRole.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            // Always toggle the spymaster state
                            sendMessage("SPYMASTER_TOGGLE:${player.name}")
                            gameStateViewModel.myIsSpymaster.value = !isLocalPlayerSpymaster
                        },
                        modifier = Modifier.testTag("Button_Spymaster"),
                        enabled = spymasterButtonEnabled
                    )

                    ColoredToggleButton(
                        label = "Operative",
                        isSelected = !isLocalPlayerSpymaster,
                        selectedColor = if (team == TeamRole.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            if (isLocalPlayerSpymaster) {
                                sendMessage("SPYMASTER_TOGGLE:${player.name}")
                            }
                            gameStateViewModel.myIsSpymaster.value = false
                        },
                        modifier = Modifier.testTag("Button_Operative")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ButtonsGui(text = if (isAlreadyReady) "Ready" else "Ready ?", onClick = {
                if (!isAlreadyReady) {
                    sendMessage("READY:${player.name}")
                }
            }, modifier = Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp),
                enabled = !isAlreadyReady)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ButtonsGui(text = "Start Game", onClick = onStartGame, modifier = Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp), enabled = allReady)

        if (!allReady) {
            Text("Waiting for all players...", style = MaterialTheme.typography.bodyMedium)
        }

        ButtonsGui(text = "Back", onClick = onBackToConnection, modifier = Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp))
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
            containerColor = if (isSelected) selectedColor else MaterialTheme.colorScheme.secondary,
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
    playerName: String?,
    gameStateViewModel: GameStateViewModel // 🆕 added parameter
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineSmall)

        players.filter { it.team == team }.forEach { player ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                val isLocalPlayer = player.name.trim().equals(playerName?.trim(), ignoreCase = true)

                // 🆕 fallback logic: for the local player, override with current selected state
                val effectiveIsSpymaster = if (isLocalPlayer) {
                    gameStateViewModel.myIsSpymaster.value
                } else {
                    player.isSpymaster
                }

                val iconResId = when {
                    team == TeamRole.RED && effectiveIsSpymaster -> R.drawable.icon_red_spymaster
                    team == TeamRole.RED && !effectiveIsSpymaster -> R.drawable.icon_red_operative
                    team == TeamRole.BLUE && effectiveIsSpymaster -> R.drawable.icon_blue_spymaster
                    team == TeamRole.BLUE && !effectiveIsSpymaster -> R.drawable.icon_blue_operative
                    else -> R.drawable.icon_blue_operative // fallback
                }

                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    buildString {
                        append(player.name)
                        if (isLocalPlayer) {
                            append(" (You)")
                        }
                    }
                )
            }
        }
    }
}

