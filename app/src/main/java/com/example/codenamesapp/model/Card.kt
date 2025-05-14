package com.example.codenamesapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Card(
    val word: String,
    @SerialName("cardRole")
    val cardRole: CardRole,
    val revealed: Boolean = false
) {
    @Transient
    var isMarked: MutableState<Boolean> = mutableStateOf(false)
}
