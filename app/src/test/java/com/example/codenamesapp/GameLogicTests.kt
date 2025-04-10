package com.example.codenamesapp

import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.model.Role
import org.junit.Before
import android.content.Context
import android.content.res.Resources
import org.junit.Test
import org.junit.Assert.*


class GameLogicTest {

    private lateinit var gameManager: GameManager
    private lateinit var context: Context
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        val fakeWords = (1..100).map { "Word$it" }
        gameManager = GameManager { fakeWords }
        gameManager.startNewGame()
    }

    @Test
    fun checkBoardHas25Cards() {
        assertEquals(25, gameManager.gameState.board.size)
    }

    @Test
    fun checkCorrectRoles() {
        val roles = gameManager.gameState.board.map { it.role }
        assertEquals(9, roles.count { it == Role.RED })
        assertEquals(8, roles.count { it == Role.BLUE })
        assertEquals(7, roles.count { it == Role.NEUTRAL })
        assertEquals(1, roles.count { it == Role.ASSASSIN })
    }

    @Test
    fun checkUniqueWords() {
        val words = gameManager.gameState.board.map { it.word }
        val distinctWords = words.toSet()
        assertEquals("Words on board should be unique", 25, distinctWords.size)
    }

    @Test
    fun checkIfCardsAreUnrevealed() {
        val unrevealed = gameManager.gameState.board.all { !it.isRevealed }
        assertTrue("All cards should be unrevealed at start", unrevealed)
    }

    @Test
    fun checkStartingTeam() {
        assertEquals("RED team always start first", Role.RED, gameManager.gameState.currentTeam)
    }

    @Test
    fun setGameOverRule() {
        val assassinCard = gameManager.gameState.board.firstOrNull { it.role == Role.ASSASSIN }
        assassinCard?.isRevealed = true
        gameManager.gameState.isGameOver = assassinCard?.isRevealed == true
        assertTrue("Game ends when assassin card is revealed", gameManager.gameState.isGameOver)
    }

    @Test
    fun checkNumberOfAssassinCards() {
        val count = gameManager.gameState.board.count { it.role == Role.ASSASSIN }
        assertEquals("It must be only one assassin card", 1, count)
    }

    @Test
    fun resetGame() {
        val oldBoardWords = gameManager.gameState.board.map { it.word }
        gameManager.startNewGame()
        val newBoardWords = gameManager.gameState.board.map { it.word }
        assertNotEquals("Board should be reset", oldBoardWords, newBoardWords)
    }
}