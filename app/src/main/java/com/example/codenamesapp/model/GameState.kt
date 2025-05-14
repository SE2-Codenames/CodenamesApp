package com.example.codenamesapp.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    @Contextual val board: List<Card>,
    var currentTeam: CardRole = CardRole.RED,
    var isGameOver: Boolean = false,
    var winner: CardRole? = null
)

