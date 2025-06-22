package com.example.codenamesapp.network

import android.util.Log
import androidx.navigation.NavHostController
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.Player
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val gameStateViewModel: GameStateViewModel,
    private val navController: NavHostController
) {
    private var webSocket: WebSocket? = null
    val communication: Communication
        get() = Communication(webSocket!!)
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .build()

    private var url: String = ""
    private var playerName: String = ""

    var onMessageReceived: ((String) -> Unit)? = null

    fun setUrl(url: String) {
        this.url = url
    }

    fun setPlayerName(name: String) {
        this.playerName = name
    }

    fun connect(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onMessageReceived: (String) -> Unit,
        onPlayerListUpdated: (List<Player>) -> Unit
    ){
        if (url.isBlank() || playerName.isBlank()) {
            onError("URL oder Spielername fehlt.")
            return
        }

        val request = Request.Builder().url(url).build()
        val listener = CodenamesWebSocketListener(
            onMessage = { msg -> onMessageReceived?.invoke(msg) },
            onPlayersUpdated = { playerList ->
                onPlayerListUpdated(playerList)

                val current = navController.currentDestination?.route
                if (current != "lobby" && current != "gameover") {
                    navController.navigate("lobby")
                }
            },
            onConnectionEstablished = {
                send("USER:$playerName")
                gameStateViewModel.ownPlayerName.value = playerName
                onSuccess()
            },
            onShowGameBoard = {
                navController.navigate("gameboard")
            },
            gameStateViewModel = gameStateViewModel,
            onError = onError
        )

        webSocket = client.newWebSocket(request, listener)
        Log.d("WebSocketClient", "🔌 Verbindung aufgebaut zu $url")
    }

    fun send(message: String) {
        Log.d("WebSocketClient", "📤 Sende: $message")
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Manuell beendet")
        webSocket = null
    }

    fun reconnect(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onMessageReceived: (String) -> Unit,
        onPlayerListUpdated: (List<Player>) -> Unit
    ) {
        this.onMessageReceived = onMessageReceived
        close()
        connect(onSuccess, onError, onMessageReceived, onPlayerListUpdated)
    }

}