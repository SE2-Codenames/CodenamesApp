package com.example.codenamesapp

import com.example.codenamesapp.MainMenu.GameEndResult
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
import com.example.codenamesapp.model.Player

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
        isSpymaster = false,
        markedCards = emptyList()
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
        assertNull(viewModel.teamTurn.value)
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

        assertEquals(3, viewModel.scoreRed.value)
        assertEquals(4, viewModel.scoreBlue.value)
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

    @Test
    fun testSendMarkedCards() {
        viewModel.cardList.addAll(sampleCards)
        viewModel.cardList[0].isMarked.value = true
        viewModel.cardList[1].isMarked.value = false

        viewModel.sendMarkedCards(communication)

        verify { socket.send("SELECT:0") }
        verify(exactly = 0) { socket.send("SELECT:1") }
    }

    @Test
    fun testMarkCard() {
        viewModel.markCard(1, communication)

        verify { socket.send("MARK:1") }
    }

    @Test
    fun testUpdateMarkedCards() {
        viewModel.cardList.addAll(sampleCards)
        viewModel.updateMarkedCards(listOf(true, false))

        assertTrue(viewModel.cardList[0].isMarked.value)
        assertFalse(viewModel.cardList[1].isMarked.value)
    }

    @Test
    fun testResetState() {
        viewModel.payload.value = payload
        viewModel.teamTurn.value = TeamRole.RED
        viewModel.playerRole.value = true
        viewModel.myTeam.value = TeamRole.BLUE
        viewModel.myIsSpymaster.value = true
        viewModel.hasReset.value = true

        viewModel.resetState()

        assertNull(viewModel.payload.value)
        assertNull(viewModel.teamTurn.value)
        assertFalse(viewModel.playerRole.value)
        assertNull(viewModel.myTeam.value)
        assertFalse(viewModel.myIsSpymaster.value)
        assertFalse(viewModel.hasReset.value)
    }

    @Test
    fun testUpdatePlayerListSetsCurrentPlayer() {
        val players = listOf(
            Player(name = "Alice", team = TeamRole.RED, isSpymaster = false, isReady = false),
            Player(name = "Bob", team = TeamRole.BLUE, isSpymaster = true, isReady = true)
        )

        viewModel.ownPlayerName.value = "Bob"

        viewModel.updatePlayerList(players)

        assertEquals("Bob", viewModel.currentPlayer.value?.name)
    }

    @Test
    fun testHintTextFormatting() {
        viewModel.loadGame(payload)
        val expected = "fruit (2)"
        assertEquals(expected, viewModel.hintText)
    }

    @Test
    fun testIsPlayerTurnTrue() {
        every { gameManager.getGameState()?.gameState } returns GamePhase.OPERATIVE_TURN
        viewModel.myTeam.value = TeamRole.RED
        viewModel.teamTurn.value = TeamRole.RED
        viewModel.myIsSpymaster.value = false

        assertTrue(viewModel.isPlayerTurn)
    }

    @Test
    fun testIsPlayerTurnFalseIfNotOperative() {
        every { gameManager.getGameState()?.gameState } returns GamePhase.SPYMASTER_TURN
        viewModel.myTeam.value = TeamRole.RED
        viewModel.teamTurn.value = TeamRole.RED
        viewModel.myIsSpymaster.value = false

        assertFalse(viewModel.isPlayerTurn)
    }

    @Test
    fun testOnGameOverCallbackIsCalledWithCorrectData() {
        var capturedResult: GameEndResult? = null
        val expectedResult = GameEndResult(
            winningTeam = TeamRole.BLUE,
            isAssassinTriggered = true,
            scoreRed = 3,
            scoreBlue = 6
        )

        viewModel.onGameOver = { result -> capturedResult = result }

        // Simuliere Spielende
        viewModel.onGameOver(expectedResult)

        assertEquals(expectedResult, capturedResult)
        assertEquals(TeamRole.BLUE, capturedResult?.winningTeam)
        assertTrue(capturedResult?.isAssassinTriggered ?: false)
        assertEquals(3, capturedResult?.scoreRed)
        assertEquals(6, capturedResult?.scoreBlue)
    }
}