package com.example.codenamesapp

import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.ChatMessage
import com.example.codenamesapp.model.CardRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ChatMessageTest {

    @Test
    fun `create ChatMessage with all fields`() {
        val card = Card("APPLE", cardRole = CardRole.RED, false)
        val message = ChatMessage(
            type = "hint",
            hint = "fruit",
            number = 2,
            card = card,
            message = "Try guessing fruit",
            team = "red",
            score = 3
        )

        assertEquals("hint", message.type)
        assertEquals("fruit", message.hint)
        assertEquals(2, message.number)
        assertEquals(card, message.card)
        assertEquals("Try guessing fruit", message.message)
        assertEquals("red", message.team)
        assertEquals(3, message.score)
    }

    @Test
    fun `compare two equal ChatMessage instances`() {
        val message1 = ChatMessage(type = "endTurn", team = "blue")
        val message2 = ChatMessage(type = "endTurn", team = "blue")

        assertEquals(message1, message2)
    }

    @Test
    fun `copy ChatMessage and modify field`() {
        val original = ChatMessage(type = "hint", hint = "animal")
        val copy = original.copy(hint = "insect")

        assertEquals("hint", copy.type)
        assertEquals("insect", copy.hint)
        assertNotEquals(original.hint, copy.hint)
    }
}
