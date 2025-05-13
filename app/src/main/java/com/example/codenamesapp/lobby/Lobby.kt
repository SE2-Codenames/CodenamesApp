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
    playerName: String,
    playerList: List<Player>,
    onTeamJoin: (TeamRole) -> Unit,
    onSpymasterToggle: () -> Unit,
    onBackToConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPlayer = playerList.find { it.name == playerName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Team-Anzeigen (oben)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TeamDisplay(
                team = TeamRole.RED,
                players = playerList.filter { it.team == TeamRole.RED },
                currentPlayerName = playerName,
                onSpymasterToggle = onSpymasterToggle.takeIf { currentPlayer?.team == TeamRole.RED }
            )
            TeamDisplay(
                team = TeamRole.BLUE,
                players = playerList.filter { it.team == TeamRole.BLUE },
                currentPlayerName = playerName,
                onSpymasterToggle = onSpymasterToggle.takeIf { currentPlayer?.team == TeamRole.BLUE }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spieler ohne Team (Mitte)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("Spieler ohne Team", style = MaterialTheme.typography.headlineSmall)
            playerList.filter { it.team == null }.forEach { player ->
                Text(player.name)
            }
            if (currentPlayer?.team == null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Button(onClick = { onTeamJoin(TeamRole.RED) }) {
                        Text("Join Red Team")
                    }
                    Button(onClick = { onTeamJoin(TeamRole.BLUE) }) {
                        Text("Join Blue Team")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .width(250.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Spiel starten")
            }

            Button(
                onClick = onBackToConnection,
                modifier = Modifier
                    .width(250.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Verbindung trennen")
            }
        }
    }
}

@Composable
fun TeamDisplay(
    team: TeamRole,
    players: List<Player>,
    currentPlayerName: String,
    onSpymasterToggle: (() -> Unit)?
) {
    val isCurrentUserInTeam = players.any { it.name == currentPlayerName }
    val isSpymasterSet = players.any { it.isSpymaster }
    val isCurrentUserSpymaster = players.find { it.name == currentPlayerName }?.isSpymaster == true

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "${team.name} Team",
            color = when (team) {
                TeamRole.RED -> MaterialTheme.colorScheme.error
                TeamRole.BLUE -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.headlineSmall
        )
        players.forEach { player ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(player.name)
                if (player.isSpymaster) {
                    Text(" (Spymaster)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (onSpymasterToggle != null && isCurrentUserInTeam) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSpymasterToggle,
                enabled = !isSpymasterSet || isCurrentUserSpymaster
            ) {
                Text(if (isCurrentUserSpymaster) "Unset Spymaster" else "Set Spymaster")
            }
        }
    }
}