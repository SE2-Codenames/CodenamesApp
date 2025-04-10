package com.example.codenamesapp
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.model.Role
import org.junit.jupiter.api.BeforeEach
import android.content.Context
import android.content.res.Resources

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class GameLogicTest {

    private lateinit var gameManager: GameManager
    private lateinit var context: Context
    private lateinit var resources: Resources

    @BeforeEach
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
        assertEquals(25, distinctWords.size, "Words on board should be unique")
    }

    @Test
    fun checkIfCardsAreUnrevealed() {
        val unrevealed = gameManager.gameState.board.all { !it.isRevealed }
        Assertions.assertTrue(unrevealed, "All cards should be unrevealed at start")
    }

    @Test
    fun checkStartingTeam() {
        assertEquals(Role.RED, gameManager.gameState.currentTeam, "RED team always start first")
    }

    @Test
    fun setGameOverRule() {
        val assassinCard = gameManager.gameState.board.firstOrNull { it.role == Role.ASSASSIN }
        assassinCard?.isRevealed = true
        gameManager.gameState.isGameOver = assassinCard?.isRevealed == true
        Assertions.assertTrue(
            gameManager.gameState.isGameOver,
            "Game ends when assassin card is revealed"
        )
    }

    @Test
    fun checkNumberOfAssassinCards() {
        val count = gameManager.gameState.board.count { it.role == Role.ASSASSIN }
        assertEquals(1, count, "It must be only one assassin card")
    }

    @Test
    fun resetGame() {
        val oldBoardWords = gameManager.gameState.board.map { it.word }
        gameManager.startNewGame()
        val newBoardWords = gameManager.gameState.board.map { it.word }
        Assertions.assertNotEquals(oldBoardWords, newBoardWords, "Board should be reset")
    }
}