package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codenamesapp.MainMenu.GameEndResult
import com.example.codenamesapp.R
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.PayloadResponseMove
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication

open class GameStateViewModel(private val gameManager : GameManager) : ViewModel() {
    val payload = mutableStateOf<PayloadResponseMove?>(null)
    val player = mutableStateOf<String?>(null)
    val teamTurn = mutableStateOf<TeamRole?>(null)
    val playerRole = mutableStateOf(false)
    var onShowGameBoard: (() -> Unit)? = null
    val gameState: GamePhase?
        get() = gameManager.getGameState()?.gameState

    var onResetGame: (() -> Unit)? = null

    val myTeam = mutableStateOf<TeamRole?>(null)
    val myIsSpymaster = mutableStateOf(false)
    val isPlayerTurn: Boolean
        get() = !myIsSpymaster.value &&
                (myTeam.value == teamTurn.value) &&
                (gameState == GamePhase.OPERATIVE_TURN)


    val playerList = mutableStateOf<List<Player>>(emptyList())

    val ownPlayerName = mutableStateOf<String?>(null)
    val currentPlayer = mutableStateOf<Player?>(null)

    val scoreRed = mutableStateOf(0)
    val scoreBlue = mutableStateOf(0)

    fun resetState() {
        payload.value = null
        teamTurn.value = null
        playerRole.value = false
        myTeam.value = null
        myIsSpymaster.value = false
        hasReset.value = false
        onGameOver = {}
    }

    val hasReset = mutableStateOf(false)
    val cardList = mutableListOf<Card>()

    // image list for card background
    val redCards = listOf(R.drawable.card_red1, R.drawable.card_red2)
    val blueCards = listOf(R.drawable.card_blue1, R.drawable.card_blue2)
    val neutralCards = listOf(R.drawable.card_neutral1, R.drawable.card_neutral2)
    val assasinCard = R.drawable.card_black

    fun loadCardsFromGameState (gameState: PayloadResponseMove) {
        val preparedCards = gameState.card.mapIndexed { index, card ->
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

        scoreRed.value = gameManager.getScore(TeamRole.RED)
        scoreBlue.value = gameManager.getScore(TeamRole.BLUE)
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

    fun updateMarkedCards(markedCards: List<Boolean>) {
        if (cardList.isEmpty()) return
        for (i in markedCards.indices) {
            if (i < cardList.size) {
                cardList[i].isMarked.value = markedCards[i]
            }
        }
    }

    val gameEndResult = mutableStateOf<GameEndResult?>(null)
    var onGameOver: (GameEndResult) -> Unit = { _ -> }

    fun updatePlayerList(newList: List<Player>) {
        playerList.value = newList
        val name = ownPlayerName.value
        if (name != null) {
            currentPlayer.value = newList.find { it.name.trim().equals(name.trim(), ignoreCase = true) }
        }
    }
}
