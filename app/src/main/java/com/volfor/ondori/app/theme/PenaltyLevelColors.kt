package com.volfor.ondori.app.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel

object PenaltyLevelColors {

    fun color(level: PenaltyLevel, isNotification: Boolean = false, isDark: Boolean): Color =
        when (level) {
            PenaltyLevel.Zero -> {
                if (isNotification) {
                    if (isDark) surfaceDimDark else surfaceDimLight
                } else {
                    if (isDark) surfaceDark else surfaceLight
                }
            }

            PenaltyLevel.Low, PenaltyLevel.ModerateLow -> if (isDark) penaltyLowDark else penaltyLowLight
            PenaltyLevel.ModerateHigh, PenaltyLevel.Medium -> if (isDark) penaltyMediumDark else penaltyMediumLight
            PenaltyLevel.High -> if (isDark) penaltyHighDark else penaltyHighLight
            PenaltyLevel.Critical -> if (isDark) penaltyCriticalDark else penaltyCriticalLight
            PenaltyLevel.Extreme -> if (isDark) penaltyExtremeDark else penaltyExtremeLight
        }

    fun argb(level: PenaltyLevel, isNotification: Boolean, isDark: Boolean): Int =
        color(level, isNotification, isDark).toArgb()

    fun argb(level: PenaltyLevel, isNotification: Boolean, context: Context): Int =
        argb(level, isNotification, context.resources.configuration.isNightUiMode)
}

private val Configuration.isNightUiMode: Boolean
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
