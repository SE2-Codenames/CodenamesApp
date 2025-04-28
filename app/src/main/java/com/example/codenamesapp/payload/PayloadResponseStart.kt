package com.example.codenamesapp

import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponseStart(
    val gameId: String,
    val players: List<String>
) : PayloadResponses
