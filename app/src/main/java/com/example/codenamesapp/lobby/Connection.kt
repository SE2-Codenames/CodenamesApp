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
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

@Composable
fun Connection(
    navController: NavHostController,
    onBackToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var host by remember { mutableStateOf("10.0.2.2") } // Verwende 10.0.2.2 für Emulator
    var port by remember { mutableStateOf("8081") }
    var username by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var connecting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var client: Socket? by remember { mutableStateOf(null) }
    var out: PrintWriter? by remember { mutableStateOf(null) }
    var inStream: BufferedReader? by remember { mutableStateOf(null) }
    var playerListFromServer by remember { mutableStateOf(listOf<Player>()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Oberer Bereich für die Eingabefelder und den Verbinden-Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth() // Fülle die Breite des oberen Bereichs
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
                    connecting = true
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val socket = Socket()
                            socket.connect(InetSocketAddress(host, port.toInt()), 5000)

                            withContext(Dispatchers.Main) {
                                client = socket
                                out = PrintWriter(socket.getOutputStream(), true)
                                inStream = BufferedReader(InputStreamReader(socket.getInputStream()))
                                connected = true
                                errorMessage = ""
                                connecting = false
                            }

                            out?.println(username)
                            Log.d("ClientSend", "Username gesendet: $username")

                            try {
                                var line: String?
                                while (inStream?.readLine().also { line = it } != null) {
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
                                        }
                                    } else if (line == "USERNAME_OK") {
                                        Log.d("ServerMessage", "USERNAME_OK erhalten.")
                                        withContext(Dispatchers.Main) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("playerName", username)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("playerList", playerListFromServer)

                                            navController.navigate("lobby")
                                            coroutineScope.launch(Dispatchers.IO) {
                                                out?.println("CLIENT_READY")
                                                Log.d("ClientSend", "CLIENT_READY gesendet.")
                                            }
                                        }
                                    } else if (line?.startsWith("MESSAGE:") == true) {
                                        val receivedMessage = line!!.substringAfter("MESSAGE:")
                                        Log.d("ChatMessage", "Received: $receivedMessage")
                                    }
                                }
                                Log.d("ServerMessage", "Leseschleife beendet. Verbindung möglicherweise getrennt.")
                            } catch (e: Exception) {
                                Log.e("SocketError", "Fehler beim Lesen vom Server: ${e.localizedMessage}", e)
                                withContext(Dispatchers.Main) {
                                    connected = false
                                    errorMessage = "Verbindung zum Server unterbrochen"
                                }
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Fehler beim Verbinden mit dem Server: ${e.localizedMessage}"
                                connecting = false
                                Log.e("SocketError", "Verbindungsfehler: ${e.localizedMessage}", e)
                            }
                        } finally {
                            Log.d("SocketLifecycle", "Finally-Block ausgeführt. connected = $connected")
                            if (!connected) {
                                client?.closeSafely()
                                inStream?.closeSafely()
                                out?.closeSafely()
                                Log.d("SocketLifecycle", "Socket und Streams geschlossen.")
                            }
                        }
                    }
                },
                enabled = !connecting
            ) {
                Text(if (connecting) "Verbinde..." else "Verbinden")
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            }
        }

        // Unterer Bereich für den Zurück-Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onBackToMain) {
                Text("Zurück zum Hauptmenü")
            }
        }
    }

    // Zeige den LobbyScreen, wenn connected ist
    if (connected) {
        LaunchedEffect(Unit) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        LobbyScreen(
            playerName = username,
            playerList = playerListFromServer,
            onTeamJoin = { team ->
                coroutineScope.launch(Dispatchers.IO) {
                    out?.println("JOIN_TEAM:$team")
                    Log.d("ClientSend", "JOIN_TEAM:$team gesendet.")
                }
            },
            onSpymasterToggle = {
                coroutineScope.launch(Dispatchers.IO) {
                    out?.println("SPYMASTER_TOGGLE")
                    Log.d("ClientSend", "SPYMASTER_TOGGLE gesendet.")
                }
            },
            onStartGame = {
                coroutineScope.launch(Dispatchers.IO) {
                    out?.println("START_GAME")
                    Log.d("ClientSend", "START_GAME gesendet.")
                }
            },
            onBackToConnection = {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                connected = false
                client?.closeSafely()
                inStream?.closeSafely()
                out?.closeSafely()
                navController.popBackStack()
            }
        )

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