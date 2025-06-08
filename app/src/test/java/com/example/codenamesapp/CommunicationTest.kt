package com.example.codenamesapp

import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.network.Message
import io.mockk.mockk
import io.mockk.verify
import okhttp3.WebSocket
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommunicationTest {
    private lateinit var mock: WebSocket
    private lateinit var communication: Communication

    @BeforeEach
    fun setUp() {
        mock = mockk(relaxed = true)
        communication = Communication(mock)
    }

    @Test
    fun testSendHint() {
        communication.giveHint("dog",3)
        verify { mock.send("HINT:dog:3")}
    }

    @Test
    fun testSendCards() {
        communication.giveCard(4)
        verify {mock.send("SELECT:4")}
    }

    @Test
    fun testSendChat() {
        communication.sendChat("Hello")
        verify { mock.send("CHAT:Hello") }
    }

    @Test
    fun testSendUsername() {
        communication.sendUsername("Mihi")
        verify{mock.send("USER:Mihi")}
    }

    @Test
    fun testJoinTeam() {
        communication.joinTeam("Mihi", "RED")
        verify { mock.send("JOIN_TEAM:Mihi:RED") }
    }

    @Test
    fun testSpymasterToggle() {
        communication.toggleSpymaster("Mihi")
        verify{mock.send("SPYMASTER_TOGGLE:Mihi")}
    }

    @Test
    fun testGameStart() {
        communication.gameStart()
        verify { mock.send("START_GAME") }
    }

    @Test
    fun testSendRaw() {
        communication.send("RAW_MESSAGE")
        verify { mock.send("RAW_MESSAGE") }
    }

    @Test
    fun testMessageDataClass() {
        val message = Message(
            type = "TEST_TYPE",
            data = mapOf("key1" to "value1", "key2" to "value2")
        )

        // Test properties
        assert(message.type == "TEST_TYPE")
        assert(message.data == mapOf("key1" to "value1", "key2" to "value2"))

        // Test toString()
        assert(message.toString().contains("TEST_TYPE"))
        assert(message.toString().contains("key1"))

        // Test copy()
        val copied = message.copy(type = "NEW_TYPE")
        assert(copied.type == "NEW_TYPE")
        assert(copied.data == message.data)

        // Test equals()
        val same = Message("TEST_TYPE", mapOf("key1" to "value1", "key2" to "value2"))
        assert(message == same)

        val different = Message("OTHER_TYPE", emptyMap())
        assert(message != different)
    }
}