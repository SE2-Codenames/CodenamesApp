package com.example.codenamesapp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.network.WebSocketClient
import com.example.codenamesapp.ui.theme.ButtonsGui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ConnectionScreen(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    onConnectionEstablished: (String) -> Unit,
    onMessageReceived: (String) -> Unit,
    onPlayerListUpdated: (List<Player>) -> Unit,
    socketClient: WebSocketClient,
    modifier: Modifier = Modifier
) {
    val defaultHost = System.getenv("SERVER_IP") ?: "10.0.2.2"  // needs to be 10.0.2.2 if testing on emulator  192.168.0.99
    var host by remember { mutableStateOf(defaultHost) }
    var port by remember { mutableStateOf("8081") }
    var playerName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showUsernameTakenDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Connection to Server", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Host") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = port,
            onValueChange = { port = it.filter { c -> c.isDigit() } },
            label = { Text("Port") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Player Name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        ButtonsGui(text = "Connect", onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val url = "ws://$host:$port"
                    socketClient.setUrl(url)
                    socketClient.setPlayerName(playerName)
                    socketClient.connect(
                        onSuccess = {
                            //onConnectionEstablished(playerName)
                            //navController.navigate("lobby")
                        },
                        onError = {
                            error = "Connection Error: $it"
                        },
                        onMessageReceived = { message ->
                            when (message) {
                                "USERNAME_TAKEN" -> {
                                    showUsernameTakenDialog = true
                                }
                                "USERNAME_OK" -> {
                                    onConnectionEstablished(playerName)
                                    navController.navigate("lobby")
                                }
                                else -> onMessageReceived(message)
                            }
                        },
                        onPlayerListUpdated = onPlayerListUpdated
                    )
                } catch (e: Exception) {
                    error = "Error: ${e.localizedMessage}"
                    Log.e("ConnectionScreen", "Connection to Server failed", e)
                }
            }
        }, modifier = Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp))

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back") // or "Go back"
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        if (showUsernameTakenDialog) {
            AlertDialog(
                onDismissRequest = { showUsernameTakenDialog = false },
                confirmButton = {
                    TextButton(onClick = { showUsernameTakenDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Enter Player Name") },
                text = { Text("The entered Player Name is already taken. Please choose another one.") }
            )
        }
    }
}
