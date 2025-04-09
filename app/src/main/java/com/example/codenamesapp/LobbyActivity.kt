package com.example.codenamesapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

@Composable
fun LobbyScreen(
    onBackToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var chatEnabled by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var receivedMessages by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var client: Socket? by remember { mutableStateOf(null) }
    var out: PrintWriter? by remember { mutableStateOf(null) }
    var inStream: BufferedReader? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!connected) {
            TextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Server Host") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = port,
                onValueChange = { port = it },
                label = { Text("Port") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val socket = Socket(host, port.toInt())
                        client = socket
                        out = PrintWriter(socket.getOutputStream(), true)
                        inStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                        connected = true
                        errorMessage = ""

                        // Empfange Nachrichten vom Server
                        launch(Dispatchers.IO) {
                            try {
                                var line: String?
                                while (inStream?.readLine().also { line = it } != null) {
                                    withContext(Dispatchers.Main) {
                                        receivedMessages += "\n$line"
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("SocketError", "Fehler beim Lesen vom Server", e)
                            }
                        }
                    } catch (e: Exception) {
                        errorMessage = "Fehler beim Verbinden mit dem Server"
                        Log.e("SocketError", "Verbindungsfehler: ${e.localizedMessage}", e)
                    }
                }
            }) {
                Text("Connect")
            }
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            }
        } else if (!chatEnabled) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter Username") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                coroutineScope.launch {
                    out?.println(username)
                    chatEnabled = true
                }
            }) {
                Text("Set Username")
            }
        } else {
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Enter message") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                coroutineScope.launch {
                    out?.println(message)
                    if (message == "bye") {
                        client?.close()
                        connected = false
                        chatEnabled = false
                    }
                }
            }) {
                Text("Send Message")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Received Messages: $receivedMessages")
        }

        // Zurück zur Hauptansicht
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackToMain) {
            Text("Back to Main")
        }
    }
}

