package com.example.codenamesapp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RulesScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
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

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Go Back")
        }
    }
}

