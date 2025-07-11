package com.example.codenamesapp.ui.theme

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
* primary = main text color
* onPrimary = main background color
* secondary = diverse grey
* onSecondary = diverse peach for marked cards
* teritary = diverse blue
* error = diverse red
* */
/*
DarkColorScheme
primary = Color.White,
onPrimary = CustomBlack,
secondary = DarkGrey,
tertiary = DarkBlue,
error = DarkRed

LightColorScheme
primary = CustomBlack,
onPrimary = Color.White,
secondary = LightGrey,
tertiary = LightBlue,
error = LightRed
*/

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = CustomBlack,
    secondary = DarkGrey,
    onSecondary = DarkPeach,
    tertiary = DarkBlue,
    error = DarkRed

)

private val LightColorScheme = lightColorScheme(
    primary = CustomBlack,
    onPrimary = Color.White,
    secondary = LightGrey,
    onSecondary = LightPeach,
    tertiary = LightBlue,
    error = LightRed

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CodenamesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ButtonsGui (text: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean = true) { // Design of the Buttons

    val buttonColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Button( onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Transparent),
        shape = RectangleShape,
        border = BorderStroke(1.dp, buttonColor),
        enabled = enabled
    ) {
        Text(text, fontSize = 22.sp, color = buttonColor)
    }
}