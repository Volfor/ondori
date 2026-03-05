package com.volfor.ondori.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.ondori.FirstScreen
import kotlinx.serialization.Serializable

@Serializable
object First

fun NavGraphBuilder.firstDestination(
    onNavigateToSecond: () -> Unit
) {
    composable<First> {
        FirstScreen(
            onNavigateToSecond
        )
    }
}

fun NavController.navigateToSecond() {
    navigate(route = Second)
}