package com.example.codenamesapp.model

data class Card(
    val word: String,
    val role: Role,
    var isRevealed: Boolean = false
)
