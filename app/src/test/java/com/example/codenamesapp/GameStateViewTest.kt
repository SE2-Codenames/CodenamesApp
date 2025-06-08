package com.example.codenamesapp

import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.network.Communication
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertNull
import okhttp3.WebSocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameStateViewTest {

    private lateinit var viewModel: GameStateViewModel
    private lateinit var gameManager: GameManager
    private lateinit var communication: Communication
    private lateinit var socket: WebSocket

    private val sampleCards = listOf(
        Card("Apple", CardRole.RED, false),
        Card("Sky", CardRole.BLUE, false)
    )

    private val payload = PayloadResponseMove(
        score = listOf(3, 4),
        teamRole = TeamRole.RED,
        gameState = GamePhase.SPYMASTER_TURN,
        remainingGuesses = 2,
        hint = "fruit",
        card = sampleCards,
        isSpymaster = false
    )

    @BeforeEach
    fun setUp() {
        gameManager = mockk(relaxed = true)
        socket = mockk(relaxed = true)
        communication = Communication(socket)
        every { gameManager.getScore(TeamRole.RED) } returns 3
        every { gameManager.getScore(TeamRole.BLUE) } returns 4
        viewModel = GameStateViewModel(gameManager)
    }

    @Test
    fun testInitialStates() {
        assertNull(viewModel.payload.value)
        assertNull(viewModel.team.value)
        assertFalse(viewModel.playerRole.value)
        assertNull(viewModel.myTeam.value)
        assertFalse(viewModel.myIsSpymaster.value)
    }

    @Test
    fun testLoadGame () {
        viewModel.loadGame(payload)

        assertEquals(payload, viewModel.payload.value)
        assertEquals(2, viewModel.cardList.size)
        assertEquals("Apple", viewModel.cardList[0].word)
        assertEquals(CardRole.RED, viewModel.cardList[0].cardRole)
        assertFalse(viewModel.cardList[0].revealed)
    }

    @Test
    fun testTeamScores () {
        viewModel.loadGame(payload)

        assertEquals(3, viewModel.scoreRed)
        assertEquals(4, viewModel.scoreBlue)
    }

    @Test
    fun testSetMyTeamAndSpymaster() {
        viewModel.myTeam.value = TeamRole.RED
        viewModel.myIsSpymaster.value = true

        assertEquals(TeamRole.RED, viewModel.myTeam.value)
        assertTrue(viewModel.myIsSpymaster.value)
    }

    @Test
    fun testOnShowGameBoardCallbackIsInvoked() {
        var wasCalled = false
        viewModel.onShowGameBoard = { wasCalled = true }

        viewModel.onShowGameBoard?.invoke()

        assertTrue(wasCalled)
    }

    @Test
    fun testLoadCardsFromGameState () {
        viewModel.loadCardsFromGameState(payload)

        assertEquals(2, viewModel.cardList.size)
        assertFalse(viewModel.cardList[0].isMarked.value)
        assertFalse(viewModel.cardList[1].isMarked.value)
    }

    @Test
    fun testHandleCardClick () {
        val index = 3
        viewModel.handleCardClick(index, communication)

        verify { socket.send("SELECT:$index") }
    }

    @Test
    fun testSendHint () {
        val word = "sun"
        val number = 2
        viewModel.sendHint(word, number, communication)

        verify { socket.send("HINT:$word:$number") }
    }
}