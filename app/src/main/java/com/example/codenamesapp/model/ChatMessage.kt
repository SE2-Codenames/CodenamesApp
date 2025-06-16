package com.example.codenamesapp.model

data class ChatMessage(
    val type: String,
    val hint: String? = null,
    val number: Int? = null,
    val card: Card? = null,
    val message: String? = null,
    val team: String? = null,
    val score: Int? = null
)
