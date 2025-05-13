package com.example.codenamesapp

import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.TeamRole
import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponseMove(
    val gameState: GamePhase,
    val teamRole: TeamRole,
    val card: List<Card>,
    val score: Array<Int>,
    val hint: String,
    val remainingGuesses: Int

):PayloadResponses
