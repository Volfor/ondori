package com.volfor.ondori.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.features.alarm.presentation.screens.AlarmsScreen
import kotlinx.serialization.Serializable

@Serializable
object Alarms

fun NavGraphBuilder.alarmsDestination(
    onNavigateToInfo: () -> Unit,
) {
    composable<Alarms> {
        AlarmsScreen(
            onNavigateToInfo
        )
    }
}

fun NavController.navigateToInfo() {
    navigate(route = Info)
}