package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.volfor.ondori.app.theme.LocalOndoriDarkTheme
import com.volfor.ondori.app.theme.PenaltyLevelColors
import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel

@Composable
fun penaltyLevelTint(level: PenaltyLevel): Color {
    val isDarkTheme = LocalOndoriDarkTheme.current
    return PenaltyLevelColors.color(level, isDark = isDarkTheme)
}

