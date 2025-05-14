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
        Log.d("WebSocketHandler", "✅ Verbindung aufgebaut")
        onConnectionEstablished?.invoke()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocketHandler", "📩 Nachricht empfangen: $text")

        if (text.startsWith("PLAYERS:")) {
            val players = parsePlayers(text.removePrefix("PLAYERS:"))
            onPlayerListUpdate(players)
        } else {
            onMessageReceived(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("WebSocketHandler", "🔣 ByteMessage empfangen: ${bytes.utf8()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketHandler", "⚠️ Verbindung wird geschlossen: $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocketHandler", "❌ Fehler bei WebSocket-Verbindung", t)
    }

    private fun parsePlayers(data: String): List<Player> {
        return data.split(";")
            .filter { it.isNotBlank() }
            .map { entry ->
                val parts = entry.split(",")
                val name = parts.getOrNull(0)?.trim().orEmpty()
                val team = parts.getOrNull(1)?.trim()?.let {
                    if (it.equals("RED", ignoreCase = true)) TeamRole.RED
                    else if (it.equals("BLUE", ignoreCase = true)) TeamRole.BLUE
                    else null
                }
                val isSpymaster = parts.getOrNull(2)?.trim()?.toBooleanStrictOrNull() ?: false
                Player(name, team, isSpymaster)
            }
    }
}
