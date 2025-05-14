package com.example.codenamesapp

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.io.IOException

@Composable
fun Connection(
    navController: NavHostController,
    onBackToMain: () -> Unit,
    onPlayerListChanged: (List<Player>) -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var host by remember { mutableStateOf("10.0.2.2") }
    var port by remember { mutableStateOf("8081") }
    var username by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var connecting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var client: Socket? by remember { mutableStateOf(null) }
    val outState = remember { mutableStateOf<PrintWriter?>(null) }
    var inStream: BufferedReader? by remember { mutableStateOf(null) }
    var playerListFromServer by remember { mutableStateOf(listOf<Player>()) }
    // val out = outState.value  // Nicht hier, da es sich ändern kann
    var navigateToLobby by remember { mutableStateOf(false) }

    // Funktion zum Zurücksetzen der Verbindungszustände
    val resetConnection = {
        connected = false
        connecting = false
        client = null
        outState.value = null
        inStream = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Server Host") }
            )
            TextField(
                value = port,
                onValueChange = { port = it },
                label = { Text("Port") }
            )
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Benutzername") }
            )
            Button(
                onClick = {
                    if (!connecting && !connected) {
                        connecting = true
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val socket = Socket()
                                socket.connect(InetSocketAddress(host, port.toInt()), 5000)

                                withContext(Dispatchers.Main) {
                                    client = socket
                                    val printWriter = PrintWriter(socket.getOutputStream(), true)
                                    outState.value = printWriter
                                    inStream = BufferedReader(InputStreamReader(socket.getInputStream()))
                                    connected = true
                                    errorMessage = ""
                                    connecting = false
                                }

                                val out = outState.value // Jetzt holen wir es hier
                                out?.println(username)
                                out?.flush()
                                Log.d("ClientSend", "Username gesendet: $username")

                                var connectedToServer = true
                                var line: String? = null
                                try {
                                    while (connectedToServer && inStream?.readLine().also { line = it } != null) {
                                        Log.d("ServerMessage", "Received: $line")
                                        if (line?.startsWith("PLAYERS:") == true) {
                                            Log.d("ServerMessage", "Verarbeite Spielerliste: $line")
                                            val playerStrings = line!!.substringAfter("PLAYERS:").split(";")
                                            val updatedPlayers = playerStrings.mapNotNull { playerString ->
                                                val parts = playerString.split(",")
                                                if (parts.size == 3) {
                                                    Player(
                                                        name = parts[0],
                                                        team = if (parts[1].isNotEmpty()) TeamRole.valueOf(parts[1]) else null,
                                                        isSpymaster = parts[2].toBoolean()
                                                    )
                                                } else {
                                                    null
                                                }
                                            }
                                            withContext(Dispatchers.Main) {
                                                playerListFromServer = updatedPlayers
                                                onPlayerListChanged(updatedPlayers)
                                            }
                                        } else if (line == "USERNAME_OK") {
                                            Log.d("ServerMessage", "USERNAME_OK erhalten.")
                                            // Sende JOIN_TEAM und SPYMASTER_TOGGLE nach erfolgreichem Login, falls zutreffend
                                            val out = outState.value
                                            coroutineScope.launch(Dispatchers.IO) {
                                                // Beispiel: Trete dem Roten Team bei
                                                out?.println("JOIN_TEAM:$username:RED")
                                                out?.flush()
                                                // Beispiel: Setze den Spieler als Spymaster
                                                out?.println("SPYMASTER_TOGGLE:$username:true")
                                                out?.flush()
                                            }

                                            withContext(Dispatchers.Main) {
                                                navController.currentBackStackEntry?.savedStateHandle?.set("playerName", username)
                                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                                    "playerList",
                                                    playerListFromServer
                                                )
                                                navigateToLobby = true
                                            }
                                        } else if (line?.startsWith("MESSAGE:") == true) {
                                            val receivedMessage = line!!.substringAfter("MESSAGE:")
                                            Log.d("ChatMessage", "Received: $receivedMessage")
                                        } else if (line?.startsWith("JOIN_TEAM:") == true) {
                                            val teamString = line.substringAfter("JOIN_TEAM:")
                                            val playerNameFromServer = teamString.substringBefore(":")
                                            val teamRoleString = line.substringAfter(":")
                                            val teamRole =
                                                if (teamRoleString.isNotEmpty()) TeamRole.valueOf(teamRoleString) else null

                                            Log.d(
                                                "ServerMessage",
                                                "JOIN_TEAM received for $playerNameFromServer, team: $teamRole"
                                            )
                                            val updatedPlayers = playerListFromServer.map { player ->
                                                if (player.name == playerNameFromServer) {
                                                    player.copy(team = teamRole, isSpymaster = false)
                                                } else {
                                                    player
                                                }
                                            }
                                            withContext(Dispatchers.Main) {
                                                playerListFromServer = updatedPlayers
                                                onPlayerListChanged(updatedPlayers)
                                            }
                                        } else if (line?.startsWith("SPYMASTER_TOGGLE:") == true) {
                                            val playerNameFromMessage =
                                                line.substringAfter("SPYMASTER_TOGGLE:").substringBefore(":")
                                            val isSpymasterString = line.substringAfter("SPYMASTER_TOGGLE:")
                                                .substringAfter(":")
                                            val isSpymaster = isSpymasterString.toBoolean()

                                            Log.d(
                                                "ServerMessage",
                                                "SPYMASTER_TOGGLE received for $playerNameFromMessage, isSpymaster: $isSpymaster"
                                            )
                                            val updatedPlayers = playerListFromServer.map { player ->
                                                if (player.name == playerNameFromMessage) {
                                                    player.copy(isSpymaster = isSpymaster)
                                                } else {
                                                    player
                                                }
                                            }
                                            withContext(Dispatchers.Main) {
                                                playerListFromServer = updatedPlayers
                                                onPlayerListChanged(updatedPlayers)
                                            }
                                        }
                                    }
                                    Log.d("ServerMessage", "Leseschleife beendet. Verbindung möglicherweise getrennt.")
                                } catch (e: IOException) {
                                    Log.e(
                                        "SocketError",
                                        "Fehler beim Lesen vom Server: ${e.localizedMessage}",
                                        e
                                    )
                                    withContext(Dispatchers.Main) {
                                        errorMessage =
                                            "Verbindung zum Server unterbrochen (Lesefehler): ${e.localizedMessage}"
                                        resetConnection() // Verbindung zurücksetzen
                                    }
                                    connectedToServer = false
                                } catch (e: Exception) {
                                    Log.e("SocketError", "Fehler beim Lesen vom Server: ${e.localizedMessage}", e)
                                    withContext(Dispatchers.Main) {
                                        errorMessage =
                                            "Verbindung zum Server unterbrochen: ${e.localizedMessage}"
                                        resetConnection() // Verbindung zurücksetzen
                                    }
                                    connectedToServer = false
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    errorMessage =
                                        "Fehler beim Verbinden mit dem Server: ${e.localizedMessage}"
                                    connecting = false
                                    Log.e("SocketError", "Verbindungsfehler: ${e.localizedMessage}", e)
                                }
                            } finally {
                                Log.d("SocketLifecycle", "Finally-Block ausgeführt. connected = $connected")
                                if (!connected) {
                                    client?.closeSafely()
                                    inStream?.closeSafely()
                                    outState.value?.closeSafely()
                                    outState.value = null
                                    Log.d("SocketLifecycle", "Socket und Streams geschlossen.")
                                }
                            }
                        }
                    }
                },
                enabled = !connecting && !connected
            ) {
                Text(if (connecting) "Verbinde..." else "Verbinden")
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onBackToMain) {
                Text("Zurück zum Hauptmenü")
            }
        }
    }

    // Navigiere zur Lobby, nachdem die Verbindung hergestellt wurde
    if (navigateToLobby) {
        LaunchedEffect(Unit) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            navController.currentBackStackEntry?.savedStateHandle?.set("playerName", username)
            navController.currentBackStackEntry?.savedStateHandle?.set("playerList", playerListFromServer)
            navigateToLobby = false
            navController.navigate("lobby")
        }
    }
}

fun Socket?.closeSafely() {
    try {
        this?.close()
    } catch (e: Exception) {
        Log.e("SocketError", "Fehler beim Schließen des Sockets", e)
    }
}

fun BufferedReader?.closeSafely() {
    try {
        this?.close()
    } catch (e: Exception) {
        Log.e("IOError", "Fehler beim Schließen des InputStreams", e)
    }
}

fun PrintWriter?.closeSafely() {
    try {
        this?.close()
    } catch (e: Exception) {
        Log.e("IOError", "Fehler beim Schließen des OutputStreams", e)
    }
}
