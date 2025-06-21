package com.example.codenamesapp
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.payload.Request
import com.example.codenamesapp.payload.Response
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PayloadTest {
    @Test
    fun testValid() {
        val payload = PayloadMoves(cardId = "card", player = "Mihi")
        assertTrue(payload.valid())
    }

    @Test
    fun testInvalidCardEmpty() {
        val payload = PayloadMoves(cardId = "", player = "Mihi")
        assertFalse(payload.valid())
    }

    @Test
    fun testInvalidPlayerEmpty() {
        val payload = PayloadMoves(cardId = "card", player = "")
        assertFalse(payload.valid())
    }

    @Test
    fun testInvalidBothEmpty() {
        val payload = PayloadMoves(cardId = "", player = "")
        assertFalse(payload.valid())
    }

    @Test
    fun testPayloadStartNonEmpty() {
        val start = PayloadStart(gameId = "Dog", players = listOf("Mihi", "Edi"))
        assertTrue(start.valid())
    }

    @Test
    fun `PayloadStart valid returns false with empty gameId`() {
        val start = PayloadStart(gameId = "", players = listOf("Mihi", "Edi"))
        assertFalse(start.valid())
    }

    @Test
    fun `PayloadStart valid returns false with empty players list`() {
        val start = PayloadStart(gameId = "Hund", players = emptyList())
        assertFalse(start.valid())
    }

    @Test
    fun testResponseMovePayloadCorrect() {
        val score = listOf(10, 20)
        val teamRole = TeamRole.BLUE
        val gameState = GamePhase.SPYMASTER_TURN
        val cardList = listOf<Card>()

        val response = PayloadResponseMove(
            score = score,
            teamRole = teamRole,
            gameState = gameState,
            remainingGuesses = 3,
            hint = "Hinweis",
            card = cardList,
            isSpymaster = true,
            markedCards = emptyList()
        )

        assertEquals(score, response.score)
        assertEquals(teamRole, response.teamRole)
        assertEquals(gameState, response.gameState)
        assertEquals(3, response.remainingGuesses)
        assertEquals("Hinweis", response.hint)
        assertEquals(cardList, response.card)
        assertTrue(response.isSpymaster)
    }

    @Test
    fun testPayloadResponseStartCorrect() {
        val players = listOf("Mihi", "Edi")
        val responseStart = PayloadResponseStart(gameId = "Hund", players = players)

        assertEquals("Hund", responseStart.gameId)
        assertEquals(players, responseStart.players)
    }

    @Test
    fun testRequestCorrect() {
        val payload = PayloadMoves(cardId = "1", player = "Mihi")
        val request = Request(type = "MOVE", payload = payload)

        assertEquals("MOVE", request.type)
        assertEquals(payload, request.payload)
    }

    @Test
    fun testResponseCorrectly() {
        val data = PayloadResponseStart(gameId = "abc123", players = listOf("Mihi", "Edi"))
        val response = Response(stat = "OK", mess = "Success", data = data)

        assertEquals("OK", response.stat)
        assertEquals("Success", response.mess)
        assertEquals(data, response.data)
    }

    @Test
    fun testPayloadSerializable(){
        val original = PayloadMoves("dog", "Mihi")
        val json = Json.encodeToString(original)
        val restored = Json.decodeFromString<PayloadMoves>(json)
        assertEquals(original, restored)
    }


}