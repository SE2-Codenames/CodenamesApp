package com.example.codenamesapp.MainMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.codenamesapp.R
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.ui.theme.ButtonsGui
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.codenamesapp.ui.theme.CodenamesAppTheme

@Composable
fun GameOverScreen(
    navController: NavHostController,
    winningTeam: TeamRole?,
    isAssassinTriggered: Boolean = false,
    scoreRed: Int = 0,
    scoreBlue: Int = 0
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Game result text
        if (isAssassinTriggered) {
            Text(
                text = "GAME OVER",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "The assassin was triggered!",
                style = MaterialTheme.typography.headlineMedium
            )
            val losingTeam = if (winningTeam == TeamRole.RED) TeamRole.BLUE else TeamRole.RED
            Text(
                text = "${losingTeam.name} Team loses!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = if (losingTeam == TeamRole.RED) Color.Red else Color.Blue
                )
            )
        } else {
            winningTeam?.let { team ->
                Text(
                    text = "VICTORY!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = if (team == TeamRole.RED) Color.Red else Color.Blue,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${team.name} Team Wins!",
                    style = MaterialTheme.typography.headlineMedium
                )
            } ?: run {
                Text(
                    text = "GAME OVER",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Score display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Red Team",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.Red)
                )
                Text(
                    text = scoreRed.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 48.sp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Blue Team",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.Blue)
                )
                Text(
                    text = scoreBlue.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 48.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(R.drawable.muster_logo),
            contentDescription = "Game Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Back to main menu button
        ButtonsGui(
            text = "Main Menu",
            onClick = { navController.navigate("menu") { popUpTo(0) } },
            modifier = Modifier.width(250.dp).height(48.dp)
        )
    }
}
@Preview(name = "Red Team Victory", showBackground = true)
@Composable
fun PreviewRedVictory() {
    CodenamesAppTheme {
        val fakeNavController = rememberNavController()
        GameOverScreen(
            navController = fakeNavController,
            winningTeam = TeamRole.RED,
            scoreRed = 8,
            scoreBlue = 5
        )
    }
}

@Preview(name = "Blue Team Victory", showBackground = true)
@Composable
fun PreviewBlueVictory() {
    CodenamesAppTheme {
        val fakeNavController = rememberNavController()
        GameOverScreen(
            navController = fakeNavController,
            winningTeam = TeamRole.BLUE,
            scoreRed = 3,
            scoreBlue = 9
        )
    }
}

@Preview(name = "Assassin Triggered", showBackground = true)
@Composable
fun PreviewAssassinTriggered() {
    CodenamesAppTheme {
        val fakeNavController = rememberNavController()
        GameOverScreen(
            navController = fakeNavController,
            winningTeam = TeamRole.RED, // Red wins by default when assassin is triggered by blue
            isAssassinTriggered = true,
            scoreRed = 6,
            scoreBlue = 2
        )
    }
}

