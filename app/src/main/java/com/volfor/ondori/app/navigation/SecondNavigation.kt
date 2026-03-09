package com.volfor.ondori.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.features.alarm.presentation.screens.AlarmsScreen
import kotlinx.serialization.Serializable

@Serializable
object Second

fun NavGraphBuilder.secondDestination(
    onNavigateToFirst: () -> Unit,
) {
    composable<Second> {
        AlarmsScreen(
            onNavigateToFirst
        )
    }
}

fun NavController.navigateToFirst() {
    navigate(route = First)
}