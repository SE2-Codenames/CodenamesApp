package com.example.codenamesapp.model

import kotlinx.serialization.Serializable

@Serializable
enum class GamePhase {
    LOBBY,
    SPYMASTER_TURN,
    OPERATIVE_TURN,
    GAME_OVER
}