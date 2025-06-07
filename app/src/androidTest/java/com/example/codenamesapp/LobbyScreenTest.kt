import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.lobby.LobbyScreen
import com.example.codenamesapp.network.WebSocketClient
import org.junit.Rule
import org.junit.Test

class LobbyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTeamAndRoleSelectionEnablesStartButton() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            val gameStateViewModel = GameStateViewModel()

            val socketClient = WebSocketClient(
                gameStateViewModel = gameStateViewModel,
                navController = navController
            )

            LobbyScreen(
                playerName = "stefan",
                playerList = emptyList(),
                socketClient = socketClient,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("Button_Red").performClick()
        composeTestRule.onNodeWithTag("Button_Operative").performClick()
        composeTestRule.onNodeWithTag("StartGame").assertIsEnabled()
    }

    @Test
    fun testBlueTeamAndOperativeEnablesStartButton() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val gameStateViewModel = GameStateViewModel()

            val socketClient = WebSocketClient(
                gameStateViewModel = gameStateViewModel,
                navController = navController
            )

            LobbyScreen(
                playerName = "stefan",
                playerList = emptyList(),
                socketClient = socketClient,
                onBackToConnection = {},
                onStartGame = {},
                sendMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("Button_Blue").performClick()
        composeTestRule.onNodeWithTag("Button_Operative").performClick()
        composeTestRule.onNodeWithTag("StartGame").assertIsEnabled()
    }
}

