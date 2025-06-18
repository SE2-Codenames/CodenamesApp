package com.example.codenamesapp
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.PayloadResponseMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach


class GameManagerTest {

    private lateinit var gameManager: GameManager

    private fun createPayload(score: List<Int>) : PayloadResponseMove {
        return PayloadResponseMove(
            score = score,
            teamRole = TeamRole.BLUE,
            gameState = GamePhase.SPYMASTER_TURN,
            remainingGuesses = 0,
            card = emptyList(),
            isSpymaster = false,
            markedCards = emptyList()
        )
    }

    @BeforeEach
    fun setup() {
        gameManager = GameManager()
    }

    @Test
    fun testLoadGameStateUpdate () {
        val state = createPayload(score = listOf(3, 5))
        gameManager.loadGameState(state)
        assertEquals(state, gameManager.getGameState())
    }

    @Test
    fun testGetGameState () {
        assertNull(gameManager.getGameState())
    }

    @Test
    fun testGetRedTeamScore () {
        val state = createPayload(score = listOf(4,2))
        gameManager.loadGameState(state)
        assertEquals(4, gameManager.getScore(TeamRole.RED))
    }

    @Test
    fun testGetBlueTeamScore () {
        val state = createPayload(score = listOf(4,2))
        gameManager.loadGameState(state)
        assertEquals(2, gameManager.getScore(TeamRole.BLUE))
    }

    @Test
    fun testGetScore () {
        assertEquals(0, gameManager.getScore(TeamRole.RED))
        assertEquals(0, gameManager.getScore(TeamRole.BLUE))
    }

    @Test
    fun testGetScoreIncomplete () {
        val state = createPayload(score = listOf(7))
        gameManager.loadGameState(state)
        assertEquals(7, gameManager.getScore(TeamRole.RED))
        assertEquals(0, gameManager.getScore(TeamRole.BLUE))
    }

    //miising test for full coverage
    @Test
    fun testGetScoreWhenScoreIsEmptyList() {
        val state = createPayload(score = emptyList()) // Explicit empty list
        gameManager.loadGameState(state)
        assertEquals(0, gameManager.getScore(TeamRole.RED)) // getOrNull(0) → null → 0
        assertEquals(0, gameManager.getScore(TeamRole.BLUE)) // getOrNull(1) → null → 0
    }

    @Test
    fun testGetScoreWhenGameStateIsNull() {
        // gameState is null (initial state)
        assertEquals(0, gameManager.getScore(TeamRole.RED))
        assertEquals(0, gameManager.getScore(TeamRole.BLUE))
    }
}