package com.example.codenamesapp.gamelogic

import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole

class GameManager(private var gameState: PayloadResponseMove? = null) {
    fun loadGameState(state: PayloadResponseMove) {
        gameState = state
    }

    fun getGameState(): PayloadResponseMove? = gameState

    fun getScore (teamRole: TeamRole): Int {
        return when (teamRole) {
            TeamRole.RED -> gameState?.score?.getOrNull(0) ?: 0
            TeamRole.BLUE -> gameState?.score?.getOrNull(1) ?: 0
        }
    }
}
