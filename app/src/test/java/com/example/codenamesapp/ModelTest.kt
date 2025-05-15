package com.example.codenamesapp

import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModelTest {
    @Test
    fun testPlayerConstructor() {
        val player = Player("Mihi")

        assertEquals("Mihi", player.name)
        assertNull(player.team)
        assertFalse(player.isSpymaster)
    }

    @Test
    fun testSpymasterTeam() {
        val player = Player("Mihi", TeamRole.BLUE, true)

        assertEquals("Mihi", player.name)
        assertEquals(TeamRole.BLUE, player.team)
        assertTrue(player.isSpymaster)
    }

    @Test
    fun testSpymasterTeamBlue() {
        val player = Player("Mihi")
        player.team = TeamRole.RED
        player.isSpymaster = true

        assertEquals(TeamRole.RED, player.team)
        assertTrue(player.isSpymaster)
    }

    @Test
    fun testGameStateWithCards() {
        val cards = listOf(
            Card("Hund", CardRole.RED, false),
            Card("Katze", CardRole.BLUE, true)
        )

        val state = GameState(board = cards)

        assertEquals(cards, state.board)
        assertEquals(CardRole.RED, state.currentTeam)
        assertFalse(state.isGameOver)
        assertNull(state.winner)
    }

    @Test
    fun testGameState() {
        val state = GameState(board = emptyList())

        state.currentTeam = CardRole.BLUE
        state.isGameOver = true
        state.winner = CardRole.BLUE

        assertEquals(CardRole.BLUE, state.currentTeam)
        assertTrue(state.isGameOver)
        assertEquals(CardRole.BLUE, state.winner)
    }
}