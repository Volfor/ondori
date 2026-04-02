package com.volfor.ondori.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Red,
    secondary = Yellow,
    tertiary = Teal,
    primaryContainer = Red,
    secondaryContainer = SoftPink.copy(alpha = 0.3f),
    onSecondaryContainer = Red,
    tertiaryContainer = Yellow.copy(alpha = 0.4f),
    surface = BackgroundGray,
    onSurface = Charcoal,
    surfaceVariant = White,
    onSurfaceVariant = Charcoal,
    surfaceContainerHighest = LightGray,
    background = BackgroundGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = Unknown,
    secondary = Unknown,
    tertiary = Unknown,
    primaryContainer = Unknown,
    secondaryContainer = Unknown,
    onSecondaryContainer = Unknown,
    tertiaryContainer = Unknown,
    surface = Unknown,
    onSurface = Unknown,
    surfaceVariant = Unknown,
    onSurfaceVariant = Unknown,
    surfaceContainerHighest = Unknown,
    background = Unknown,
)

data class OndoriExtraColors(
    val header: Color = Color.Unspecified,
    val title: Color = Color.Unspecified,
    val alarmDisabledBackground: Color = Color.Unspecified,
    val secondaryIconTint: Color = Color.Unspecified,
)

val LocalExtraColors = staticCompositionLocalOf {
    OndoriExtraColors()
}

private val LightExtraColors = OndoriExtraColors(
    header = Red,
    title = MutedBrown,
    alarmDisabledBackground = LightGray,
    secondaryIconTint = DarkAmber,
)
private val DarkExtraColors = OndoriExtraColors(
    header = Unknown,
    title = Unknown,
    alarmDisabledBackground = Unknown,
    secondaryIconTint = Unknown,
)

private val shapes: Shapes
    @Composable get() = MaterialTheme.shapes.copy(
        large = CircleShape, //TODO:
    )

@Composable
fun OndoriTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme: ColorScheme
    val extraColors: OndoriExtraColors
    if (isDarkTheme) {
        colorScheme = DarkColorScheme
        extraColors = DarkExtraColors
    } else {
        colorScheme = LightColorScheme
        extraColors = LightExtraColors
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = shapes,
            content = content,
        )
    }
}

object OndoriTheme {
    val extraColors: OndoriExtraColors
        @Composable get() = LocalExtraColors.current
}