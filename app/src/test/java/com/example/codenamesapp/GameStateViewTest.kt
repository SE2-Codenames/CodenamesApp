package com.example.codenamesapp

import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Card
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameStateViewTest {

    private lateinit var modelGameState: GameStateViewModel

    @BeforeEach
    fun setUp() {
        modelGameState = GameStateViewModel()
    }

    @Test
    fun testInitialStates() {
        assertNull(modelGameState.payload.value)
        assertNull(modelGameState.team.value)
        assertFalse(modelGameState.playerRole.value)
        assertNull(modelGameState.myTeam.value)
        assertFalse(modelGameState.myIsSpymaster.value)
    }

    @Test
    fun testSetMyTeamAndSpymaster() {
        modelGameState.myTeam.value = TeamRole.RED
        modelGameState.myIsSpymaster.value = true

        assertEquals(TeamRole.RED, modelGameState.myTeam.value)
        assertTrue(modelGameState.myIsSpymaster.value)
    }

    @Test
    fun testOnShowGameBoardCallbackIsInvoked() {
        var wasCalled = false
        modelGameState.onShowGameBoard = { wasCalled = true }

        modelGameState.onShowGameBoard?.invoke()

        assertTrue(wasCalled)
    }

}