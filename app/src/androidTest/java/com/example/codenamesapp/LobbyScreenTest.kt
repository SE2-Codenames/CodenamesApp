import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.codenamesapp.gamelogic.GameManager
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.model.Player
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient
import org.junit.Rule
import org.junit.Test

class LobbyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTeamAndRoleSelectionEnablesStartButton() {
        val playerList = mutableStateListOf(
            Player("stefan", TeamRole.RED, false, isReady = false)
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            val gameManager = GameManager()
            val gameStateViewModel = GameStateViewModel(gameManager)
            val socketClient = WebSocketClient(gameStateViewModel, navController)

            LobbyScreen(
                playerName = "stefan",
                playerList = playerList,
                socketClient = socketClient,
                gameStateViewModel = gameStateViewModel,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("Button_Red").performClick()
        composeTestRule.onNodeWithTag("Button_Operative").performClick()

        composeTestRule.onNodeWithTag("ReadyButton").assertIsEnabled()

        composeTestRule.runOnIdle {
            val index = playerList.indexOfFirst { it.name == "stefan" }
            if (index != -1) {
                playerList[index] = playerList[index].copy(isReady = true)
            }
        }

        composeTestRule.onNodeWithTag("ReadyButton").assertIsNotEnabled()
    }

    @Test
    fun testOnlyOneSpymasterPerTeam() {
        val playerList = listOf(
            Player("stefan", TeamRole.RED, false),
            Player("anna", TeamRole.RED, true)
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            val gameManager = GameManager()
            val gameStateViewModel = GameStateViewModel(gameManager)
            val socketClient = WebSocketClient(gameStateViewModel, navController)

            LobbyScreen(
                playerName = "stefan",
                playerList = playerList,
                socketClient = socketClient,
                gameStateViewModel = gameStateViewModel,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("Button_Red").performClick()
        composeTestRule.onNodeWithTag("Button_Spymaster").assertIsNotEnabled()
    }

    @Test
    fun testYouLabelDisplayed() {
        val playerList = listOf(Player("stefan", TeamRole.RED, false))

        composeTestRule.setContent {
            val navController = rememberNavController()
            val gameStateViewModel = GameStateViewModel(GameManager())
            val socketClient = WebSocketClient(gameStateViewModel, navController)

            LobbyScreen(
                playerName = "stefan",
                playerList = playerList,
                socketClient = socketClient,
                gameStateViewModel = gameStateViewModel,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithText("stefan (You)").assertExists()
    }

    @Test
    fun testStartGameButtonEnabledWhenAllPlayersReady() {
        val playerList = listOf(
            Player(name = "stefan", team = TeamRole.RED, isSpymaster = false, isReady = true)
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            val gameStateViewModel = GameStateViewModel(GameManager())
            val socketClient = WebSocketClient(gameStateViewModel, navController)

            LobbyScreen(
                playerName = "stefan",
                playerList = playerList,
                socketClient = socketClient,
                gameStateViewModel = gameStateViewModel,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("StartGame").assertIsEnabled()
    }




}
