package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole

class GameStateViewModel : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val team = mutableStateOf<TeamRole?>(null)
    val playerRole = mutableStateOf<Boolean?>(null)

    var onShowGameBoard: (() -> Unit)? = null
}
