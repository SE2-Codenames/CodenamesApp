package com.example.codenamesapp.MainMenu
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.R
import com.example.codenamesapp.ui.theme.ButtonsGui


@Composable
fun RulesScreen(onBack: () -> Unit) {
    val scrollState = rememberLazyListState()
    val scrollProgress by remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount.coerceAtLeast(1)
            val visibleIndex = scrollState.firstVisibleItemIndex
            val visibleOffset = scrollState.firstVisibleItemScrollOffset
            val approxProgress = (visibleIndex + (visibleOffset / 100f)) / totalItems.toFloat()
            approxProgress.coerceIn(0f, 1f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.muster_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.05f
        )

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                item {
                    Text("Game Rules", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Setup:\n- Split into 2 teams (Red & Blue) with at least 4 players.\n- Each team chooses a Spymaster; the others are Operatives.\n- Lay 25 random words in a 5x5 grid.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Goal:\n- Be the first team to identify all your agents.\n- Avoid finding the assassin card!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Gameplay:\n- Spymasters give 1-word clues and a number.\n- Operatives try to guess related words.\n- Correct guesses = more chances. Wrong guesses = end of turn.\n- If the assassin is guessed, your team instantly loses.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Valid Clues:\n- One word only!\n- Must not be a visible word on the board!\n- Homonyms and proper names are okay if allowed by both teams.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tips:\n- Spymasters must stay expressionless.\n- Field Operatives must avoid looking for nonverbal cues.\n- Use the timer optionally to speed up turns.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ending:\n- The game ends when all agents of one team are found or the assassin is revealed.")
                }
            }

            ButtonsGui("Go Back", onClick = onBack, Modifier.fillMaxWidth().padding(16.dp))
        }


        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(3.dp)
                .heightIn(max = (LocalConfiguration.current.screenHeightDp.dp / 2))
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(scrollProgress)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            )
        }

    }
}



