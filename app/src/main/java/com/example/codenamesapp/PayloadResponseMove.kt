package com.example.codenamesapp

import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponseMove(
    val gameId: String,
    val success: Boolean,
    val mess: String
):PayloadResponses
