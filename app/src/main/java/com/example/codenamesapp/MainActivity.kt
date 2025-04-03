package com.example.codenamesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { startActivity(Intent(this@MainActivity, LobbyActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Connecting ")
            }
            Button(
                onClick = { startActivity(Intent(this@MainActivity, MainActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Tutorial")
            }
            Button(
                onClick = { startActivity(Intent(this@MainActivity, MainActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Settings")
            }
        }
    }
}
