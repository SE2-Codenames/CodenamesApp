package com.example.codenamesapp.MainMenu

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
    LockLandscapeOrientation()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GameResultContent(winningTeam, isAssassinTriggered)

        Spacer(modifier = Modifier.height(32.dp))

        ScoreDisplay(scoreRed, scoreBlue)

        Spacer(modifier = Modifier.height(if (isLandscape) 20.dp else 48.dp))

        Image(
            painter = painterResource(R.drawable.muster_logo),
            contentDescription = "Game Logo",
            modifier = Modifier.size(if (isLandscape) 100.dp else 120.dp)
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp))

        ButtonsGui(
            text = "Main Menu",
            onClick = { navController.navigate("menu") { popUpTo(0)}},
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
        )
    }
}

@Composable
private fun GameResultContent(
    winningTeam: TeamRole?,
    isAssassinTriggered: Boolean
) {
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
            style = MaterialTheme.typography.headlineSmall
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
}

@Composable
private fun ScoreDisplay(scoreRed: Int, scoreBlue: Int) {
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
}

@Composable
fun LockLandscapeOrientation() {
    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

@Preview(
    name = "Game Over Landscape Preview",
    showBackground = true,
    widthDp = 800,
    heightDp = 480
)
@Composable
fun PreviewGameOverLandscape() {
    CodenamesAppTheme {
        val fakeNavController = rememberNavController()

        // Even though this won't affect preview, it's good to include
        LockLandscapeOrientation()

        GameOverScreen(
            navController = fakeNavController,
            winningTeam = TeamRole.RED,
            scoreRed = 9,
            scoreBlue = 6
        )
    }
}