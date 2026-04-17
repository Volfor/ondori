package com.volfor.ondori.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.features.alarm.presentation.screens.AlarmsScreen
import kotlinx.serialization.Serializable

@Serializable
object Alarms

fun NavGraphBuilder.alarmsDestination(
    onNavigateToSettings: () -> Unit,
) {
    composable<Alarms> {
        AlarmsScreen(
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}

fun NavController.navigateToSettings() {
    navigate(route = Settings)
}