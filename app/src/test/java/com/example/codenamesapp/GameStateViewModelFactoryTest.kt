package com.example.codenamesapp

import androidx.lifecycle.ViewModel
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.gamelogic.GameStateViewModelFactory
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Test

class GameStateViewModelFactoryTest {
    private val mockGameManager = mockk<GameManager>(relaxed = true)
    private val factory = GameStateViewModelFactory(mockGameManager)

    @Test
    fun testCreateGameStateViewModel () {
        val viewModel = factory.create(GameStateViewModel::class.java)
        assertTrue(viewModel is GameStateViewModel)
    }

    @Test
    fun testThrowsIllegalArgumentException () {
        class UnknownViewModel : ViewModel()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            factory.create(UnknownViewModel::class.java)
        }

        assertEquals("Unknown ViewModel class", exception.message)
    }
}