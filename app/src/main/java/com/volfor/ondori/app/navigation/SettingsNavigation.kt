package com.volfor.ondori.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.features.settings.presentation.screens.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object Settings

fun NavGraphBuilder.settingsDestination(
    onBack: () -> Unit,
) {
    composable<Settings> {
        SettingsScreen(onBack = onBack)
    }
}
