package com.example.codenamesapp

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.codenamesapp.MainMenu.UnlockOrientation

import com.example.codenamesapp.ui.theme.*



@Composable
fun MainMenuScreen(
    onPlayClicked: () -> Unit,
    onRulesClicked: () -> Unit
) {
    UnlockOrientation()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainHeading()
            ButtonsContent(
                onPlayClicked,
                onRulesClicked
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainHeading()
            Spacer(modifier = Modifier.height(100.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom
        ) {
            ButtonsContent(
                onPlayClicked,
                onRulesClicked
            )
        }
    }
}

@Composable
fun MainHeading () { // Logo & Title
    // image for logo (just prototype for now)
    val image = if (!isSystemInDarkTheme()) painterResource(R.drawable.muster_logo_white) else painterResource(R.drawable.muster_logo_black)
    Box(Modifier
        .height(160.dp)
        .padding(bottom = 25.dp)) {
        Image(
            painter = image,
            contentDescription = null
        )
    }
    // title
    Text(text = "CODENAMES", style = MaterialTheme.typography.headlineLarge)

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
fun ButtonsContent ( // the 3 Buttons
    onPlayClicked: () -> Unit,
    onRulesClicked: () -> Unit
) {
    ButtonsGui(text = "Play", onClick = { onPlayClicked() }, Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp))

    Spacer(modifier = Modifier.height(16.dp))

    ButtonsGui(text = "Rules", onClick = { onRulesClicked() }, Modifier.width(250.dp).height(48.dp).padding(horizontal = 4.dp))

    Spacer(modifier = Modifier.height(16.dp))
}