package com.example.codenamesapp.gamelogic

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.CardRole
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.Communication
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codenamesapp.GameBoardScreen
import okhttp3.WebSocket
import okio.ByteString

class DummyCommunication : Communication(
    object : WebSocket {
        override fun request() = TODO()
        override fun queueSize(): Long = 0
        override fun send(text: String): Boolean = true
        override fun send(bytes: ByteString): Boolean = true
        override fun close(code: Int, reason: String?): Boolean = true
        override fun cancel() {}
    }
)

class DummyGameManager : GameManager() {
    override fun getScore(team: TeamRole): Int = 9
    // Implementiere weitere Methoden nur wenn nötig
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 600)
@Composable
fun GameBoardScreenPreview() {
    // Dummy ViewModel mit minimalen Daten
    val dummyViewModel = object : GameStateViewModel(gameManager = DummyGameManager()) {
        init {
            myTeam.value = TeamRole.RED
            myIsSpymaster.value = true
            teamTurn.value = TeamRole.RED
            cardList.addAll(
                List(25) {
                    Card(
                        word = "Word $it",
                        cardRole = CardRole.NEUTRAL,
                        revealed = true
                    )
                }
            )
        }
    }

    // Dummy Communication Objekt
    val dummyCommunication = DummyCommunication()


    CodenamesAppTheme {
        GameBoardScreen(
            viewModel = dummyViewModel, communication = dummyCommunication,
            messages = TODO()
        )
    }
}