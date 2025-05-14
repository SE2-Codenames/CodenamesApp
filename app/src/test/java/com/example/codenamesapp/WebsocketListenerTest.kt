package com.example.codenamesapp

import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.network.CodenamesWebSocketListener
import com.example.codenamesapp.model.PayloadResponseMove
import org.junit.jupiter.api.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.codenamesapp.model.TeamRole
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.mockk
import com.google.gson.Gson
import okhttp3.WebSocket
import org.junit.Rule


class WebsocketListenerTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dummyMessageCallback: (String) -> Unit = {}
    private val dummyPlayersCallback: (List<com.example.codenamesapp.model.Player>) -> Unit = {}
    private val dummyConnectionCallback: () -> Unit = {}
    private val dummyShowGameBoardCallback: () -> Unit = {}

    @Test
    fun `GAME_STATE message updates GameStateViewModel correctly`() {
        val mockViewModel = GameStateViewModel()
        val listener = CodenamesWebSocketListener(
            onMessage = dummyMessageCallback,
            onPlayersUpdated = dummyPlayersCallback,
            onConnectionEstablished = dummyConnectionCallback,
            onShowGameBoard = dummyShowGameBoardCallback,
            gameStateViewModel = mockViewModel
        )

        val jsonPayload = PayloadResponseMove(
            score = listOf(1, 2),
            teamRole = TeamRole.RED,
            gameState = GamePhase.SPYMASTER_TURN,
            remainingGuesses = 3,
            hint = "TestHint",
            card = emptyList(),
            isSpymaster = true
        )

        val jsonMessage = "GAME_STATE:" + Gson().toJson(jsonPayload)
        val mockWebSocket = mockk<WebSocket>()

        listener.onMessage(mockWebSocket, jsonMessage)

        assertEquals(jsonPayload, mockViewModel.payload.value)
        assertEquals(jsonPayload.teamRole, mockViewModel.team.value)
        assertEquals(jsonPayload.isSpymaster, mockViewModel.playerRole.value)
    }
}