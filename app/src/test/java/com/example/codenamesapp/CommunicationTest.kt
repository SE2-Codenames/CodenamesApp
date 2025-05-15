package com.example.codenamesapp

import com.example.codenamesapp.network.Communication
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
}