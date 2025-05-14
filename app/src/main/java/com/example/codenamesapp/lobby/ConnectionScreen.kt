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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ConnectionScreen(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    onConnectionEstablished: () -> Unit,
    onMessageReceived: (String) -> Unit,
    onPlayerListUpdated: (List<Player>) -> Unit,
    socketClient: WebSocketClient,
    modifier: Modifier = Modifier
) {
    var host by remember { mutableStateOf("10.0.2.2") }
    var port by remember { mutableStateOf("8081") }
    var playerName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verbindung zum Server", style = MaterialTheme.typography.headlineMedium)

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
            label = { Text("Spielername") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val url = "ws://$host:$port"
                    socketClient.setUrl(url)
                    socketClient.setPlayerName(playerName)
                    socketClient.connect(
                        onSuccess = {
                            onConnectionEstablished()
                            navController.navigate("lobby")
                        },
                        onError = {
                            error = "Verbindungsfehler: $it"
                        },
                        onMessageReceived = onMessageReceived,
                        onPlayerListUpdated = onPlayerListUpdated
                    )
                } catch (e: Exception) {
                    error = "Fehler: ${e.localizedMessage}"
                    Log.e("ConnectionScreen", "Verbindungsaufbau fehlgeschlagen", e)
                }
            }
        }) {
            Text("Verbinden")
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
