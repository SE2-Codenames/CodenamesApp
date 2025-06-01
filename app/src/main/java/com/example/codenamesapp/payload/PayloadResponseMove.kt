package com.example.codenamesapp.model

import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponseMove(
    val score: List<Int>, // "score":[0,0]
    val teamRole: TeamRole, // "teamRole": "BLUE"
    val gameState: GamePhase, // "gameState": "SPYMASTER_TURN"
    val remainingGuesses: Int, // "remainingGuesses":0
    val hint: String? = null,
    val card: List<Card>, // "card":[]
    val isSpymaster: Boolean
)