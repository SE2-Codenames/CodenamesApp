package com.example.codenamesapp.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole

@Composable
fun LobbyScreen(
    onBackToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(listOf<Player>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Rotes Team
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Red Team", color = MaterialTheme.colorScheme.error)
                players.filter { it.team == TeamRole.RED }.forEach { player ->
                    PlayerItem(player, players) { updatedPlayer ->
                        players = players.map { if (it.name == updatedPlayer.name) updatedPlayer else it }
                    }
                }
            }
            // Blaues Team
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Blue Team", color = MaterialTheme.colorScheme.primary)
                players.filter { it.team == TeamRole.BLUE }.forEach { player ->
                    PlayerItem(player, players) { updatedPlayer ->
                        players = players.map { if (it.name == updatedPlayer.name) updatedPlayer else it }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter Username") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (username.isNotBlank()) {
                    players = players + Player(name = username, team = TeamRole.RED) // Standard RED
                    username = ""
                }
            }) {
                Text("Add Player")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBackToMain) {
            Text("Back to Main")
        }
    }
}

@Composable
fun PlayerItem(player: Player, allPlayers: List<Player>, onPlayerUpdated: (Player) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(player.name)

        Row {
            Button(onClick = {
                val newTeam = if (player.team == TeamRole.RED) TeamRole.BLUE else TeamRole.RED
                onPlayerUpdated(player.copy(team = newTeam))
            }) {
                Text("Switch Team")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val isAlreadySpymaster = allPlayers.any { it.team == player.team && it.isSpymaster }
                    if (!isAlreadySpymaster || player.isSpymaster) {
                        onPlayerUpdated(player.copy(isSpymaster = !player.isSpymaster))
                    }
                }
            ) {
                Text(if (player.isSpymaster) "Unset Spymaster" else "Set Spymaster")
            }
        }
    }
}
