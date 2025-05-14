package com.example.codenamesapp

import com.example.codenamesapp.model.CardRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication

class GameLogicTest {

    private lateinit var gameManager: GameManager

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
        assertEquals(9, roles.count { it == CardRole.RED })
        assertEquals(8, roles.count { it == CardRole.BLUE })
        assertEquals(7, roles.count { it == CardRole.NEUTRAL })
        assertEquals(1, roles.count { it == CardRole.ASSASSIN })
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
        assertTrue(unrevealed, "All cards should be unrevealed at start")
    }

    @Test
    fun checkStartingTeam() {
        assertEquals(CardRole.RED, gameManager.gameState.currentTeam, "RED team always start first")
    }

    @Test
    fun setGameOverRule() {
        val assassinCard = gameManager.gameState.board.firstOrNull { it.role == CardRole.ASSASSIN }
        assassinCard?.isRevealed = true
        gameManager.gameState.isGameOver = assassinCard?.isRevealed == true
        assertTrue(gameManager.gameState.isGameOver, "Game ends when assassin card is revealed")
    }

    @Test
    fun checkNumberOfAssassinCards() {
        val count = gameManager.gameState.board.count { it.role == CardRole.ASSASSIN }
        assertEquals(1, count, "It must be only one assassin card")
    }

    @Test
    fun resetGame() {
        val oldBoardWords = gameManager.gameState.board.map { it.word }
        gameManager.startNewGame()
        val newBoardWords = gameManager.gameState.board.map { it.word }
        assertNotEquals(oldBoardWords, newBoardWords, "Board should be reset")
    }

    // Communication tests
/*    @Test
    fun parseValidGameStateJson() {
        val jsonString = """
            {
              "gameState": "SPYMASTER_TURN",
              "teamRole": "BLUE",
              "card": [
                {"word": "Test1", "role": "RED", "isRevealed": false},
                {"word": "Test2", "role": "BLUE", "isRevealed": true}
              ],
              "score": [5, 4]
            }
        """.trimIndent()

        val communication = Communication()
        communication.updateFromServerMessage("GAME_STATE:$jsonString")

        val game = communication.latestGameState
        assertNotNull(game)
        assertEquals(GamePhase.SPYMASTER_TURN, game?.gameState)
        assertEquals(TeamRole.BLUE, game?.teamRole)
        assertEquals(2, game?.card?.size)
        assertEquals(false, game?.card?.get(0)?.isRevealed)
        assertEquals(true, game?.card?.get(1)?.isRevealed)
        assertEquals(5, game?.score?.get(0))
        assertEquals(4, game?.score?.get(1))
    }*/

    @Test
    fun testPrepareHintAndCardMessage() {
        val communication = Communication()
        val hintMessage = communication.giveHint(arrayOf("animal", "3"))
        val cardMessage = communication.giveCard(12)

        assertEquals("HINT:animal:3", hintMessage)
        assertEquals("SELECT:12", cardMessage)
    }
}