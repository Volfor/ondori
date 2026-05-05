package com.volfor.ondori.app.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel

object PenaltyLevelColors {

    private val ZeroLight = BackgroundGray
    private val LowLight = PaleYellow
    private val MediumLight = SoftOrange
    private val HighLight = WarmPeach
    private val CriticalLight = LightCoral
    private val ExtremeLight = MutedRed

    private val ZeroDark = Unknown
    private val LowDark = AmberYellow
    private val MediumDark = VividOrange
    private val HighDark = DeepOrange
    private val CriticalDark = WarningRed
    private val ExtremeDark = DeepRed

    fun color(level: PenaltyLevel, isDark: Boolean): Color = when (level) {
        PenaltyLevel.Zero -> if (isDark) ZeroDark else ZeroLight
        PenaltyLevel.Low, PenaltyLevel.ModerateLow -> if (isDark) LowDark else LowLight
        PenaltyLevel.ModerateHigh, PenaltyLevel.Medium -> if (isDark) MediumDark else MediumLight
        PenaltyLevel.High -> if (isDark) HighDark else HighLight
        PenaltyLevel.Critical -> if (isDark) CriticalDark else CriticalLight
        PenaltyLevel.Extreme -> if (isDark) ExtremeDark else ExtremeLight
    }

    fun argb(level: PenaltyLevel, isDark: Boolean): Int = color(level, isDark).toArgb()

    fun argb(level: PenaltyLevel, context: Context): Int =
        argb(level, context.resources.configuration.isNightUiMode)
}

private val Configuration.isNightUiMode: Boolean
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
