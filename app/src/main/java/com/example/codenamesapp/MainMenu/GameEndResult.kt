package com.example.codenamesapp.MainMenu

import com.example.codenamesapp.model.TeamRole

data class GameEndResult(
    val winningTeam: TeamRole?,
    val isAssassinTriggered: Boolean,
    val scoreRed: Int,
    val scoreBlue: Int
)
