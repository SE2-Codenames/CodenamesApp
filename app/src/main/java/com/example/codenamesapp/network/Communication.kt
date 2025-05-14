package com.example.codenamesapp

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.PrintWriter

@Serializable
data class Message(
    val type: String,
    val data: Map<String, String>
)

class Communication(
    private val writer: PrintWriter,
    private val reader: BufferedReader
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var lastMessage: Message? = null

    // Client -> Server: Kommandos senden
    fun sendCommand(type: String, data: Map<String, String> = emptyMap()) {
        val message = Message(type, data)
        writer.println(json.encodeToString(message))
        writer.flush()
    }

    fun sendUsername(name: String) = sendCommand("USERNAME", mapOf("name" to name))
    fun joinTeam(team: String) = sendCommand("JOIN_TEAM", mapOf("team" to team))
    fun toggleSpymaster() = sendCommand("SPYMASTER_TOGGLE")
    fun gameStart() = sendCommand("START_GAME")
    fun giveHint(word: String, number: Int) = sendCommand("HINT", mapOf("word" to word, "number" to number.toString()))
    fun giveCard(index: Int) = sendCommand("SELECT", mapOf("index" to index.toString()))
    fun sendChat(text: String) = sendCommand("CHAT", mapOf("message" to text))

    // Server -> Client: Empfängt neue Nachricht und speichert sie zwischen
    fun receiveNextMessage(): Message? {
        val line = reader.readLine() ?: return null
        return try {
            lastMessage = json.decodeFromString<Message>(line)
            lastMessage
        } catch (e: Exception) {
            println("Fehler beim Parsen: ${e.message}")
            null
        }
    }

    // Zugriff auf letzten Zustand für Logik
    fun getLastInput(): String? = lastMessage?.type

    fun giveHint(): Array<String> {
        if (lastMessage?.type == "HINT") {
            val word = lastMessage?.data?.get("word") ?: ""
            val number = lastMessage?.data?.get("number") ?: "0"
            return arrayOf(word, number)
        }
        return arrayOf("", "0")
    }

    fun getSelectedCard(): Int {
        if (lastMessage?.type == "SELECT") {
            return lastMessage?.data?.get("index")?.toIntOrNull() ?: -1
        }
        return -1
    }

    fun isGameStartRequested(): Boolean = lastMessage?.type == "START_GAME"
}
