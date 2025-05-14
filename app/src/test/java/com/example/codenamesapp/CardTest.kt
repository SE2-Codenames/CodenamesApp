package com.example.codenamesapp

import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.CardRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CardTest {
    @Test
    fun testCard() {
        val card = Card(word = "Dog", cardRole = CardRole.RED)
        assertEquals("Dog", card.word)
        assertEquals(CardRole.RED, card.cardRole)
        assertFalse(card.revealed)
        assertFalse(card.isMarked.value)
    }

    @Test
    fun testMarked() {
        val card = Card("Dog", CardRole.BLUE)
        card.isMarked.value = true
        assertTrue(card.isMarked.value)
    }
}