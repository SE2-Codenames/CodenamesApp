package com.example.codenamesapp.model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val board: List<Card>,
    var currentTeam: Role = Role.RED, // team red starts first!
    var isGameOver: Boolean = false,
    var winner: Role? = null
)
