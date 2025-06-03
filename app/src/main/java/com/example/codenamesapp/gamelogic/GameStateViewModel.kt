package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codenamesapp.MainMenu.GameEndResult
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole
import kotlinx.coroutines.launch

class GameStateViewModel : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val team = mutableStateOf<TeamRole?>(null) // team whose turn it is
    val playerRole = mutableStateOf(false) // isSpymaster from server (ignore for UI)

    // OWN selections
    val myTeam = mutableStateOf<TeamRole?>(null)
    val myIsSpymaster = mutableStateOf(false)

    // Navigation callbacks
    var onShowGameBoard: (() -> Unit)? = null

    val gameEndResult = mutableStateOf<GameEndResult?>(null)

    //onGameOver callback
    var onGameOver: (GameEndResult) -> Unit = { _ -> }

    // When game over
    fun triggerGameOver(result: GameEndResult) {
        viewModelScope.launch {
            gameEndResult.value = result
            onGameOver(result)
        }
    }
}
