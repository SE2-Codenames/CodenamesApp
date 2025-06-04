package com.example.codenamesapp

import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.network.Communication
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameStateViewTest {

    private lateinit var viewModel: GameStateViewModel
    private lateinit var gameManager: GameManager
    private lateinit var communication: Communication

    private val sampleCards = listOf(
        Card("Apple", CardRole.RED, false),
        Card("Sky", CardRole.BLUE, false)
    )

    @BeforeEach
    fun setUp() {
        gameManager = mockk(relaxed = true)
        communication = mockk(relaxed = true)
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

}