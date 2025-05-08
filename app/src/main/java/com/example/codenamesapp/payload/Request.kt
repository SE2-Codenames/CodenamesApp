package com.example.codenamesapp.payload

import com.example.codenamesapp.Payload
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val type: String,
    val payload: Payload
)