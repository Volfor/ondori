package com.volfor.ondori.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.InfoScreen
import kotlinx.serialization.Serializable

@Serializable
object Info

fun NavGraphBuilder.infoDestination(
    onNavigateToAlarms: () -> Unit, onBack: () -> Unit
) {
    composable<Info> {
        InfoScreen(
            onNavigateToAlarms, onBack
        )
    }
}

fun NavController.navigateToAlarms() {
    navigate(route = Alarms)
}