package com.example.codenamesapp.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient

@Composable
fun LobbyScreen(
    playerName: String?,
    playerList: List<Player>,
    socketClient: WebSocketClient,
    onBackToConnection: () -> Unit,
    onStartGame: () -> Unit
) {
    val localPlayer = playerList.find {
        it.name.trim().equals(playerName?.trim(), ignoreCase = true)
    }

    Text("localPlayer gefunden: ${localPlayer?.name ?: "NEIN"}")

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
            TeamColumn("Red Team", TeamRole.RED, playerList)
            TeamColumn("Blue Team", TeamRole.BLUE, playerList)
        }

        Spacer(modifier = Modifier.height(16.dp))

        localPlayer?.let { player ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    socketClient.send("JOIN_TEAM:${player.name}:RED")
                    println("📤 ${player.name} will zu RED")
                }) {
                    Text("Join Red")
                }
                Button(onClick = {
                    socketClient.send("JOIN_TEAM:${player.name}:BLUE")
                    println("📤 ${player.name} will zu BLUE")
                }) {
                    Text("Join Blue")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                socketClient.send("SPYMASTER_TOGGLE:${player.name}")
                println("📤 ${player.name} toggelt Spymaster")
            }) {
                Text(if (player.isSpymaster) "Unset Spymaster" else "Set Spymaster")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onStartGame) {
            Text("Spiel starten")
        }

        Button(onClick = onBackToConnection) {
            Text("Zurück")
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
