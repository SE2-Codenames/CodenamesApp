package com.example.codenamesapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Card(
    val word: String,
    val role: Role
) {
    var isRevealed by mutableStateOf(false)
    var isMarked by mutableStateOf(false)
}

