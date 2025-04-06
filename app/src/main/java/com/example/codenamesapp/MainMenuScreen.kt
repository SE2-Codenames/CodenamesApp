package com.example.codenamesapp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onPlayClicked: () -> Unit,
    onRulesClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CODENAMES", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onPlayClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Play")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRulesClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Rules")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSettingsClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }

        Spacer(modifier = Modifier.height(48.dp))

        // TODO - add icon for Codenames logo (as on GUI prototype)
    }
}
