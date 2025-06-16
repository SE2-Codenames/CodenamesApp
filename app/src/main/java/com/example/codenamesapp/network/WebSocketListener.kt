package com.example.codenamesapp.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.ChatMessage
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class CodenamesWebSocketListener(
    private val onMessage: (String) -> Unit,
    private val onPlayersUpdated: (List<Player>) -> Unit,
    private val onConnectionEstablished: () -> Unit,
    private val onShowGameBoard: () -> Unit,
    private val gameStateViewModel: GameStateViewModel,
    private val onError: (String) -> Unit
) : WebSocketListener() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val gson = Gson()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("CodenamesWebSocket", "✅ Verbindung geöffnet: ${response.message}")
        mainHandler.post { onConnectionEstablished() }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("CodenamesWebSocket", "📨 Nachricht empfangen: $text")

        when {
            text.startsWith("PLAYERS:") -> {
                val playerList = text.removePrefix("PLAYERS:")
                    .split(";")
                    .filter { it.isNotBlank() }
                    .mapNotNull { entry ->
                        val parts = entry.split(",")
                        if (parts.size == 3) {
                            Player(
                                name = parts[0],
                                team = parts[1].takeIf { it.isNotEmpty() }?.let { TeamRole.valueOf(it) },
                                isSpymaster = parts[2].toBoolean()
                            )
                        } else null
                    }

                mainHandler.post { onPlayersUpdated(playerList) }
            }

            text.startsWith("GAME_STATE:") -> {
                val json = text.removePrefix("GAME_STATE:")
                try {
                    val payload = gson.fromJson(json, PayloadResponseMove::class.java)
                    gameStateViewModel.payload.value = payload
                    gameStateViewModel.team.value = payload.teamRole
                    gameStateViewModel.playerRole.value = payload.isSpymaster

                    gameStateViewModel.loadGame(payload)
                    Log.d("DEBUG", "✅ Karten empfangen: ${payload.card.size}")
                } catch (e: Exception) {
                    Log.e("CodenamesWebSocket", "Fehler beim Parsen von GAME_STATE: $json", e)
                }
            }

            text.startsWith("MARKED:") -> {
                val json = text.removePrefix("MARKED:")
                try {
                    val markedMap = gson.fromJson(json, Map::class.java)
                    val rawList = markedMap["markedCards"] as? List<*>

                    if (rawList != null) {
                        val markedBooleans = rawList.map { it as Boolean }
                        mainHandler.post {
                            gameStateViewModel.updateMarkedCards(markedBooleans)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("WebSocket", "❌ Fehler beim Parsen der MARKED-Nachricht: $json", e)
                }
            }

            text.startsWith("SHOW_GAMEBOARD") -> {
                mainHandler.post {
                    onShowGameBoard()
                }
            }

            text.startsWith("RESET") -> {
                gameStateViewModel.resetState()
                gameStateViewModel.onResetGame?.invoke()
            }

            text.startsWith("CHAT:") -> {
                val json = text.removePrefix("CHAT:")
                try {
                    val payload = gson.fromJson(json, ChatMessage::class.java)
                    mainHandler.post {
                        handleChatMessage(payload)
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Fehler beim Parsen der CHAT-Nachricht: $json", e)
                }
            }


            else -> {
                mainHandler.post { onMessage(text) }
            }
        }
    }

    private fun handleChatMessage(msg: ChatMessage) {
        val formatted = when (msg.type) {
            "hint" -> "Hint: ${msg.hint} (${msg.number})"
            "card" -> "Chosen Card: ${msg.card?.word ?: "?"}"
            "expose" -> "Expose: ${msg.message}"
            "win" -> "${msg.message} (${msg.team}, Points: ${msg.score})"
            else -> "Unknown message: ${msg.type}"
        }
        onMessage(formatted)
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("CodenamesWebSocket", "❌ Fehler: ${t.localizedMessage}", t)
        val errorMsg = " Verbindung fehlgeschlagen: ${t.localizedMessage ?: "Unbekannter Fehler"}"
        Log.e("CodenamesWebSocket", errorMsg, t)
        onError(errorMsg)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("CodenamesWebSocket", "❎ Verbindung geschlossen: $reason")
    }
}
