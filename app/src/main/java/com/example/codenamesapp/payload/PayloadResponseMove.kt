package com.example.codenamesapp.model

import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponseMove(
    val score: List<Int>,
    val teamRole: TeamRole,
    val gameState: GamePhase,
    val remainingGuesses: Int,
    val hint: String? = null,
    val card: List<Card>,
    val isSpymaster: Boolean
)