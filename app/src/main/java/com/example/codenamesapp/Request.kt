package com.example.codenamesapp

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val type: String,
    val payload: Payload
)
