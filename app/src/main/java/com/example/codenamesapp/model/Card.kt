package com.example.codenamesapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val word: String,
<<<<<<< HEAD
    val role: Role
) {
    var isRevealed by mutableStateOf(false)
    var isMarked by mutableStateOf(false)
}

=======
    val role: Role,
    var isRevealed: Boolean = false
)
>>>>>>> 875d12e71b1fd5e365d4d15f337e70bd31b79a13
