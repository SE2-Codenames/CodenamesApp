package com.example.codenamesapp.MainMenu

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.codenamesapp.R
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.network.WebSocketClient
import com.example.codenamesapp.gamelogic.GameStateViewModel
import com.example.codenamesapp.ui.theme.CodenamesAppTheme
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GameOverScreen(
    navController: NavHostController,
    winningTeam: TeamRole?,
    isAssassinTriggered: Boolean = false,
    scoreRed: Int = 0,
    scoreBlue: Int = 0,
    currentTeam: MutableState<TeamRole?>,
    socketClient: WebSocketClient,
    viewModel: GameStateViewModel
) {
    LockLandscapeOrientation()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val backgroundImageId = if (isLandscape) {
        R.drawable.gameoverscreen_landscape
    } else {
        R.drawable.codenamesgameoverscreen
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImageId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().alpha(1f)
        )

        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(if (isLandscape) 85.dp else 0.dp))
                GameResultContent(winningTeam, isAssassinTriggered, isLandscape, currentTeam)
                ScoreDisplay(scoreRed, scoreBlue, isLandscape)

                if (!isLandscape) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(R.drawable.muster_logo_black),
                        contentDescription = "Game Logo",
                        modifier = Modifier.size(130.dp)
                    )
                }

                ButtonsGui(
                    text = "Main Menu",
                    onClick = {
                        viewModel.resetState()
                        viewModel.gameEndResult.value = null
                        socketClient.close()
                        navController.navigate("menu") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.width(200.dp).height(50.dp),
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            }
        }
    }
}

@Composable
fun ButtonsGui(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GameResultContent(
    winningTeam: TeamRole?,
    isAssassinTriggered: Boolean,
    isLandscape: Boolean,
    currentTeam: MutableState<TeamRole?>
) {
    if (isAssassinTriggered) {
        Spacer(modifier = Modifier.height(if (!isLandscape) 55.dp else 5.dp))
        Text(
            text = "GAME OVER!",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)
            )
        )
        Spacer(modifier = Modifier.height(if (!isLandscape) 100.dp else 50.dp))
        Text(
            text = "The assassin was triggered!",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)
            )
        )
        val losingTeam = if (winningTeam == TeamRole.RED) TeamRole.BLUE else TeamRole.RED
        Spacer(modifier = Modifier.height(if (!isLandscape) 20.dp else 10.dp))
        Text(
            text = "${losingTeam.name} Team loses!",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)
            )
        )
    } else {
        winningTeam?.let { team ->
            val resultText = if (currentTeam.value == team) "VICTORY!" else "DEFEAT!"
            Text(
                text = resultText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)
                )
            )
            Spacer(modifier = Modifier.height(if (!isLandscape) 80.dp else 85.dp))
            Text(
                text = "${team.name} Team Wins!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)
                )
            )
        } ?: Text(text = "GAME OVER", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
private fun ScoreDisplay(scoreRed: Int, scoreBlue: Int, isLandscape: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Red Team", style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)))
            Text("$scoreRed", style = MaterialTheme.typography.displayMedium.copy(
                color = Color.White, fontSize = 50.sp,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)))
        }

        if (isLandscape) {
            Image(
                painter = painterResource(R.drawable.muster_logo_black),
                contentDescription = "Game Logo",
                modifier = Modifier.size(130.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Blue Team", style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Black, Offset(5f, 5f), 4f)))
            Text("$scoreBlue", style = MaterialTheme.typography.displayMedium.copy(
                color = Color.White, fontSize = 50.sp,
                shadow = Shadow(Color.Black, Offset(4f, 4f), 4f)))
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

@Composable
fun UnlockOrientation() {
    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
