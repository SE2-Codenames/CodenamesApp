package com.example.codenamesapp.model

data class Player(
    val name: String,
    var team: TeamRole? = null,
    var isSpymaster: Boolean = false
)