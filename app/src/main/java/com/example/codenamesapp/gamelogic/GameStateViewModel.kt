package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codenamesapp.MainMenu.GameEndResult
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication

class GameStateViewModel(private val gameManager : GameManager) : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val player = mutableStateOf(null)
    val teamTurn = mutableStateOf<TeamRole?>(null) // team whose turn it is
    val playerRole = mutableStateOf(false) // isSpymaster from server (ignore for UI)
    var onShowGameBoard: (() -> Unit)? = null
    val gameState: GamePhase?
        get() = gameManager.getGameState()?.gameState


    var onResetGame: (() -> Unit)? = null
    // OWN selections
    val myTeam = mutableStateOf<TeamRole?>(null)
    val myIsSpymaster = mutableStateOf(false)
    val isPlayerTurn = !myIsSpymaster.value && (myTeam == teamTurn)

    // Scores for Red and Blue Team
    val scoreRed : Int
        get() = gameManager.getScore(TeamRole.RED)
    val scoreBlue : Int
        get() = gameManager.getScore(TeamRole.BLUE)

    fun resetState() {
        payload.value = null
        teamTurn.value = null
        playerRole.value = false
        myTeam.value = null
        myIsSpymaster.value = false
        hasReset.value = false
    }
    val hasReset = mutableStateOf(false)
    // creating Card-List
    val cardList = mutableListOf<Card>()

    fun loadCardsFromGameState (gameState: PayloadResponseMove) {
        println("Empfangene Karten:")
        val preparedCards = gameState.card.mapIndexed { index, card ->
            println(" ${card.word}, role=${card.cardRole}, revealed=${card.revealed}")
            val isMarkedValue = gameState.markedCards?.getOrNull(index) ?: false
            card.apply { isMarked = mutableStateOf(isMarkedValue) }
        }
        cardList.clear()
        cardList.addAll(preparedCards)
    }

    val hintText: String
        get() = payload.value?.let {
            "${it.hint ?: "–"} (${it.remainingGuesses})"
        } ?: "–"

    fun loadGame(state: PayloadResponseMove) {
        gameManager.loadGameState(state)
        payload.value = state
        loadCardsFromGameState(state)
    }

    fun handleCardClick (index: Int, communication: Communication) {
        communication.giveCard(index)
    }

    fun sendHint (hintWord: String, hintNumber: Int, communication: Communication) {
        communication.giveHint(hintWord, hintNumber)
    }

    fun sendMarkedCards(communication: Communication) {
        cardList.forEachIndexed { index, card ->
            if (card.isMarked.value) {
                communication.giveCard(index)
            }
        }
    }

    fun markCard(index: Int, communication: Communication) {
        communication.markCard(index)
    }

    val gameEndResult = mutableStateOf<GameEndResult?>(null)

    //onGameOver callback
    var onGameOver: (GameEndResult) -> Unit = { _ -> }

}
