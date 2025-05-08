package com.example.codenamesapp.payload

import com.example.codenamesapp.PayloadResponses

data class Response(
    val stat: String,
    val mess: String,
    val data: PayloadResponses? //für optionale Daten bei Antwort
)