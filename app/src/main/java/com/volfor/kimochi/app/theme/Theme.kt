package com.volfor.kimochi.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Yellow

data class KimochiExtraColors(
    val header: Color = Color.Unspecified,
)

val LocalExtraColors = staticCompositionLocalOf {
    KimochiExtraColors()
}

private val LightExtraColors = KimochiExtraColors(
    header = Yellow,
)
private val DarkExtraColors = KimochiExtraColors(
    header = Red,
)

@Composable
fun KimochiTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val extraColors: KimochiExtraColors
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

object KimochiTheme {
    val extraColors: KimochiExtraColors
        @Composable get() = LocalExtraColors.current
}