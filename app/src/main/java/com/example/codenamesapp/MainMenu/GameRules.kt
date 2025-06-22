package com.example.codenamesapp.MainMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.R
import com.example.codenamesapp.ui.theme.ButtonsGui

@Composable
fun RulesScreen(onBack: () -> Unit) {
    var isEnglish by remember { mutableStateOf(true) }
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
            painter = painterResource(if (!isSystemInDarkTheme()) R.drawable.muster_logo_black else R.drawable.muster_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.05f
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                val flagRes = if (isEnglish) R.drawable.german else R.drawable.uk
                val languageLabel = if (isEnglish) "DE" else "EN"

                Button(
                    onClick = { isEnglish = !isEnglish },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(flagRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 6.dp)
                        )
                        Text(languageLabel)
                    }
                }
            }

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text("Game Rules", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEnglish) {
                        Text(
                            "Setup:\n- Split into two teams: Red and Blue (at least 4 players).\n- Each team selects a Spymaster; the rest are Operatives.\n- Place 25 random word cards in a 5x5 grid."
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Goal:\n- Be the first team to identify all your agents.\n- Avoid selecting the assassin card!")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Gameplay:\n- Spymasters give a one-word clue followed by a number.\n- Operatives guess words on the board based on the clue.\n- Correct guesses allow more guesses (up to the number given +1).\n- A wrong guess ends the turn immediately.\n- Guessing the assassin card ends the game—you lose instantly.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Valid Clues:\n- Must be exactly one word.\n- Cannot be any visible word on the board.\n- Homonyms and proper nouns are allowed if both teams agree beforehand.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Tips:\n- Spymasters must remain expressionless.\n- Operatives must not seek non-verbal cues.\n- You may use a timer to speed up gameplay.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("End of the Game:\n- The game ends when a team finds all its agents or the assassin is guessed.")
                    } else {
                        Text(
                            "Aufbau:\n- Teilt euch in zwei Teams (Rot und Blau) mit mindestens 4 Spielern.\n- Jedes Team wählt einen Geheimdienstchef, die übrigen sind Ermittler.\n- Legt 25 zufällige Wortkarten in einem 5x5-Raster aus.",
                            modifier = Modifier.testTag("AufbauText")
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Ziel:\n- Das Team gewinnt, das zuerst alle seine Agenten findet.\n- Vermeidet es, die Attentäterkarte zu wählen!")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Spielverlauf:\n- Der Geheimdienstchef gibt einen Hinweis mit genau einem Wort und einer Zahl.\n- Die Ermittler versuchen, passende Wörter zu erraten.\n- Richtige Wörter erlauben weitere Versuche (bis zur Zahl +1).\n- Ein falsches Wort beendet die Runde sofort.\n- Wird der Attentäter erraten, endet das Spiel sofort – das Team verliert.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Gültige Hinweise:\n- Genau ein Wort.\n- Kein sichtbares Wort auf dem Spielfeld darf als Hinweis verwendet werden.\n- Homonyme und Eigennamen sind erlaubt, wenn beide Teams vorher zustimmen.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Tipps:\n- Der Geheimdienstchef darf keine Reaktionen zeigen.\n- Ermittler dürfen keine nonverbalen Hinweise beachten.\n- Ein Timer kann verwendet werden, um das Spiel zu beschleunigen.")
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Spielende:\n- Das Spiel endet, wenn ein Team alle Agenten findet oder der Attentäter erraten wird.")
                    }
                }
            }

            ButtonsGui("Go Back", onClick = onBack, Modifier.fillMaxWidth().padding(16.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(3.dp)
                .heightIn(max = (LocalConfiguration.current.screenHeightDp.dp / 2))
                .padding(end = 4.dp)
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
