package com.volfor.ondori.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Yellow

data class OndoriExtraColors(
    val header: Color = Color.Unspecified,
)

val LocalExtraColors = staticCompositionLocalOf {
    OndoriExtraColors()
}

private val LightExtraColors = OndoriExtraColors(
    header = Yellow,
)
private val DarkExtraColors = OndoriExtraColors(
    header = Red,
)

@Composable
fun OndoriTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val extraColors: OndoriExtraColors
    if (isDarkTheme) {
        extraColors = DarkExtraColors
    } else {
        extraColors = LightExtraColors
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            content = content,
        )
    }
}

object OndoriTheme {
    val extraColors: OndoriExtraColors
        @Composable get() = LocalExtraColors.current
}