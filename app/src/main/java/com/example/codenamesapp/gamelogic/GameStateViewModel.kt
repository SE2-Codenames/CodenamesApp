package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication

class GameStateViewModel(private val gameManager : GameManager) : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val team = mutableStateOf<TeamRole?>(null) // team whose turn it is
    val playerRole = mutableStateOf(false) // isSpymaster from server (ignore for UI)

    // OWN selections
    val myTeam = mutableStateOf<TeamRole?>(null)
    val myIsSpymaster = mutableStateOf(false)

    var onShowGameBoard: (() -> Unit)? = null

    fun loadGame() {
        //gameState.value = gameManager.initGame()
    }

    fun handleCardClock (index: Int, communication: Communication) {
        communication.giveCard(index)
    }

    fun sendHint (hintWord: String, hintNumber: Int, communication: Communication) {
        communication.giveHint(hintWord, hintNumber)
    }
}
