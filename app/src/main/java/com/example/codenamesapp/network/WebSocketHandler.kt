package com.example.codenamesapp.network

import android.util.Log
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketHandler(
    private val onPlayerListUpdate: (List<Player>) -> Unit,
    private val onMessageReceived: (String) -> Unit,
    private val onConnectionEstablished: (() -> Unit)? = null
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocketHandler", "Verbindung aufgebaut")
        onConnectionEstablished?.invoke()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocketHandler", "Nachricht empfangen: $text")

        if (text == "USERNAME_TAKEN") {
            onMessageReceived("USERNAME_TAKEN")
        } else if (text.startsWith("PLAYERS:")) {
            val players = parsePlayers(text.removePrefix("PLAYERS:"))
            onPlayerListUpdate(players)
        } else if (text == "RESET") {
            onMessageReceived("RESET")
        } else {
            onMessageReceived(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("WebSocketHandler", "ByteMessage empfangen: ${bytes.utf8()}")

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketHandler", "Verbindung wird geschlossen: $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocketHandler", "Fehler bei WebSocket-Verbindung", t)
    }

    fun parsePlayers(playersMessage: String): List<Player> {
        val playersString = playersMessage.removePrefix("PLAYERS:")
        if (playersString.isBlank()) return emptyList()

        val players = mutableListOf<Player>()
        val playerEntries = playersString.split(";").filter { it.isNotBlank() }

        for (entry in playerEntries) {
            val parts = entry.split(",")
            if (parts.size < 4) {
                continue
            }

            val name = parts[0].trim()
            val team = parts[1].trim().uppercase().let {
                when (it) {
                    "RED" -> TeamRole.RED
                    "BLUE" -> TeamRole.BLUE
                    else -> null
                }
            }
            val spymaster = parts[2].trim().toBoolean()
            val ready = parts[3].trim().toBoolean()

            players.add(Player(name, team, spymaster, ready))
        }

        return players
    }





}
