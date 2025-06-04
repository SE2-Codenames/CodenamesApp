package com.example.codenamesapp
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.model.PayloadResponseMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach


class GameManagerTest {

    private lateinit var gameManager: GameManager

    @BeforeEach
    fun setup() {
        gameManager = GameManager()
    }

    @Test
    fun testLoadGameStateUpdate () {
        val state = PayloadResponseMove(score = listOf(3, 5))
        gameManager.loadGameState(state)
        assertEquals(state, gameManager.getGameState())
    }

    @Test
    fun testGetGameState () {
        assertNull(gameManager.getGameState())
    }

    @Test
    fun testGetRedTeamScore () {
        val state = PayloadResponseMove(score = listOf(4,2))
        gameManager.loadGameState(state)
        assertEquals(2, gameManager.getScore(TeamRole.RED))
    }

    @Test
    fun testGetBlueTeamScore () {
        val state = PayloadResponseMove(score = listOf(4,2))
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
        val state = PayloadResponseMove(score = listOf(7))
        gameManager.loadGameState(state)
        assertEquals(7, gameManager.getScore(TeamRole.RED))
        assertEquals(0, gameManager.getScore(TeamRole.BLUE))
    }
}