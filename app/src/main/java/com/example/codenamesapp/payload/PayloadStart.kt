package com.example.codenamesapp

import kotlinx.serialization.Serializable

@Serializable
data class PayloadStart(
    val gameId: String,
    val players: List<String>,
) : Payload{
    override fun valid(): Boolean = gameId.isNotEmpty() && players.isNotEmpty()
}

