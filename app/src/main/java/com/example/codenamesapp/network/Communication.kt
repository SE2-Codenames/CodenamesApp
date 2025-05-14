package com.example.codenamesapp.network

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.WebSocket
import java.io.BufferedReader
import java.io.PrintWriter

@Serializable
data class Message(
    val type: String,
    val data: Map<String, String>
)

class Communication(
    private val socket: WebSocket
) {
    fun send(message: String) {
        socket.send(message)
    }

    fun sendUsername(name: String) {
        send("USER:$name")
    }

    fun joinTeam(name: String, team: String) {
        send("JOIN_TEAM:$name:$team")
    }

    fun toggleSpymaster(name: String) {
        send("SPYMASTER_TOGGLE:$name")
    }

    fun gameStart() {
        send("START_GAME")
    }

    fun giveHint(word: String, number: Int) {
        send("HINT:$word:$number")
    }

    fun giveCard(index: Int) {
        send("SELECT:$index")
    }

    fun sendChat(message: String) {
        send("CHAT:$message")
    }
}