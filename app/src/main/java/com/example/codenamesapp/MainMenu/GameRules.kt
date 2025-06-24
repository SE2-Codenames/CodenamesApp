package com.example.codenamesapp.MainMenu

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            painter = painterResource(if (isSystemInDarkTheme()) R.drawable.muster_logo_black else R.drawable.muster_logo_white),
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
                        RulesSelection(title = "Setup", icon = Icons.Default.Build,
                            content = "The gameboard consists of 25 random word cards in a 5x5 grid: these include team cards (in the corresponding team colors), neutral cards (gray), and one assassin card (black).\n" +
                                "The game requires a minimum of 4 players, split into two teams: Red and Blue. Each team selects exactly one Spymaster; all other members become Operatives.\n" +
                                "The starting team is chosen randomly and must guess one more card than the other team to balance the advantage.")
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "Gameplay", icon = Icons.Default.PlayArrow,
                            content = "In each turn, the Spymaster gives a one-word hint followed by a number (through the \"Give A Hint!\"-Button). Operatives guess words on the board based on the hint of the Spymaster in their team.\n" +
                                "The Operatives can make as many guesses as the number given in the Hint +1. A wrong guess ends the turn immediately. Wrong guesses may be: guessing a neutral card or guessing a card from the other team.\n" +
                                "Guessing the Assassin card ends the game with an instant loss.")
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "Expose the other Team", icon = Icons.Default.Notifications,
                            content = "The \"Expose!\"-Button allows a team to challenge a Hint if it matches or contains any visible word on the board.\n" +
                                "If the challenge is valid, a neutral card is converted into a card for the accused team. If the challenge is not valid, the neutral card is converted into a card for the accusing team.\n" +
                                "If no neutral cards remain, the accusing/accused team wins immediately - depending on the validity of the challenge.")
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "End of Game", icon = Icons.Default.Star,
                            content = "The game ends when a team correctly identifies all its cards or when a player selects the assassin card - resulting in an immediate loss. " +
                                "Exposing can also end the game if no neutral cards are left.")
                    } else {
                        RulesSelection(title = "Spielaufbau", icon = Icons.Default.Build,
                            content = "Das Spielfeld besteht aus 25 zufälligen Wortkarten in einem 5x5-Raster: Dazu gehören Teamkarten (in den entsprechenden Teamfarben), neutrale Karten (grau) und eine Assassin-Karte (schwarz).\n" +
                                    "Das Spiel erfordert mindestens 4 Spieler, aufgeteilt in zwei Teams: Rot und Blau. Jedes Team wählt genau einen Spymaster; alle anderen sind Operatives.\n" +
                                    "Das startende Team wird zufällig bestimmt und muss eine Karte mehr erraten, um den Startvorteil auszugleichen."
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "Spielverlauf", icon = Icons.Default.PlayArrow,
                            content = "In jedem Zug gibt der Spymaster einen Hinweis, bestehend aus genau einem Wort und einer Zahl (über den \"Give A Hint!\"-Button). Die Operatives seines Teams versuchen daraufhin, passende Wörter auf dem Spielfeld zu erraten.\n" +
                                    "Die Operatives dürfen so viele Wörter raten, wie die angegebene Zahl +1. Ein falscher Tipp beendet den Zug sofort. Falsche Tipps sind: das Erraten einer neutralen Karte oder einer gegnerischen Teamkarte.\n" +
                                    "Das Erraten der Assassin-Karte führt zu einem sofortigen Verlieren des Spiels."
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "Expose das andere Team", icon = Icons.Default.Notifications,
                            content = "Mit dem \"Expose!\"-Button kann ein Team einen Hinweis des gegnerischen Teams anfechten, wenn dieser ein Wort vom Spielfeld enthält oder exakt damit übereinstimmt.\n" +
                                    "Ist der Vorwurf korrekt, wird eine neutrale Karte in eine Karte des beschuldigten Teams umgewandelt. Ist der Vorwurf falsch, erhält das anklagende Team die zusätzliche Karte.\n" +
                                    "Gibt es keine neutralen Karten mehr, endet das Spiel sofort – das Team mit der korrekten Einschätzung gewinnt."
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        RulesSelection(title = "Spielende", icon = Icons.Default.Star,
                            content = "Das Spiel endet, wenn ein Team alle eigenen Karten korrekt erraten hat oder ein Spieler die Assassin-Karte auswählt – was zu einer sofortigen Niederlage führt.\n" +
                                    "Auch ein korrektes Expose kann das Spiel beenden, wenn keine neutralen Karten mehr verfügbar sind."
                        )

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

@Composable
fun RulesSelection (title: String, content: String, icon: ImageVector) {
    var expanded by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text (
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon (
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
