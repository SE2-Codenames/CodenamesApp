package com.example.codenamesapp.model

data class Player(
    val name: String,
    var team: TeamRole,
    var isSpymaster: Boolean = false
)