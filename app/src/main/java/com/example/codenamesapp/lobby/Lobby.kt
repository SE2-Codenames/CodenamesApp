package com.example.codenamesapp.lobby
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient

@Composable
fun LobbyScreen(
    playerName: String,
    playerList: List<Player>,
    socketClient: WebSocketClient,
    onBackToConnection: () -> Unit,
    onStartGame: () -> Unit,
    sendMessage: (String) -> Unit = { socketClient.send(it) }
) {
    var selectedTeam by remember { mutableStateOf<TeamRole?>(null) }
    var isSpymaster by remember { mutableStateOf<Boolean?>(null) }

    val playerReady = selectedTeam != null && isSpymaster != null

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

        // Show current team layout
        /*Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TeamColumn("Red Team", TeamRole.RED, playerList)
            TeamColumn("Blue Team", TeamRole.BLUE, playerList)
        }*/

        Spacer(modifier = Modifier.height(16.dp))

        Text("Choose your team:")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleButton(
                text = "Red",
                isSelected = selectedTeam == TeamRole.RED,
                onClick = {
                    sendMessage("JOIN_TEAM:$playerName:RED")
                    selectedTeam = TeamRole.RED
                },
                selectedColor = Color(0xFFD32F2F)
            )
            ToggleButton(
                text = "Blue",
                isSelected = selectedTeam == TeamRole.BLUE,
                onClick = {
                    sendMessage("JOIN_TEAM:$playerName:BLUE")
                    selectedTeam = TeamRole.BLUE
                },
                selectedColor = Color(0xFF1976D2)
            )
        }

        if (selectedTeam != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Choose your role:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleButton(
                    text = "Spymaster",
                    isSelected = isSpymaster == true,
                    onClick = {
                        sendMessage("SPYMASTER_TOGGLE:$playerName")
                        isSpymaster = true
                    },
                    selectedColor = Color.Gray
                )
                ToggleButton(
                    text = "Operative",
                    isSelected = isSpymaster == false,
                    onClick = {
                        sendMessage("SPYMASTER_TOGGLE:$playerName")
                        isSpymaster = false
                    },
                    selectedColor = when (selectedTeam) {
                        TeamRole.RED -> Color(0xFFD32F2F)
                        TeamRole.BLUE -> Color(0xFF1976D2)
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartGame,
            enabled = playerReady,
            modifier = Modifier.testTag("StartGame")
        ) {
            Text("Spiel starten")
        }

        Button(onClick = onBackToConnection) {
            Text("Zurück")
        }
    }
}

@Composable
fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier.testTag("Button_$text"),
        elevation = ButtonDefaults.elevatedButtonElevation(),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Text(text)
        if (isSelected) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.Check, contentDescription = "Selected")
        }
    }
}



@Composable
fun TeamColumn(
    title: String,
    team: TeamRole,
    players: List<Player>
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        players.filter { it.team == team }.forEach { player ->
            Row {
                Text(player.name)
                if (player.isSpymaster) {
                    Text(" (Spymaster)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}