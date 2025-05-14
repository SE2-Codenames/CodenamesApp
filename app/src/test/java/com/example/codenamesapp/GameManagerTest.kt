package com.example.codenamesapp
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.gamelogic.GameManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach


class GameManagerTest {

    private lateinit var wordProvider: () -> List<String>

    @BeforeEach
    fun setup() {
        wordProvider = { List(100) { i -> "Word$i" } }
    }

    @Test
    fun testWordList() {
        var callCount = 0
        val wordProvider = {
            callCount++
            listOf("a", "b", "c")
        }

        val manager = GameManager(wordProvider)

        manager.getRandomWords()
        manager.getRandomWords()

        assertEquals(1, callCount)
    }

    @Test
    fun testWordListWhenAccessed() {
        var called = false
        val wordProvider = {
            called = true
            listOf("a", "b", "c")
        }

        val manager = GameManager(wordProvider)
        assertFalse(called)

        manager.getRandomWords()
        
        assertTrue(called)
    }
}