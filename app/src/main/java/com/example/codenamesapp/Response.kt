package com.example.codenamesapp

data class Response(
    val stat: String,
    val mess: String,
    val data: PayloadResponses? //für optionale Daten bei Antwort
)
