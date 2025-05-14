package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole

class GameStateViewModel : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val team = mutableStateOf<TeamRole?>(null) // team whose turn it is
    val playerRole = mutableStateOf(false) // isSpymaster from server (ignore for UI)

    // OWN selections
    val myTeam = mutableStateOf<TeamRole?>(null)
    val myIsSpymaster = mutableStateOf(false)

    var onShowGameBoard: (() -> Unit)? = null
}
