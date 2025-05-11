package com.example.codenamesapp

import kotlinx.serialization.Serializable

@Serializable
data class PayloadMoves(
    val cardId: String,
    val player: String
) : Payload {
    override fun valid(): Boolean = cardId.isNotEmpty() && player.isNotEmpty()
}
