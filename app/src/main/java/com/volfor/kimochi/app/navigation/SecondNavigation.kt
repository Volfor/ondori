package com.volfor.kimochi.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volfor.kimochi.SecondScreen
import kotlinx.serialization.Serializable


@Serializable
object Second

fun NavGraphBuilder.secondDestination(
    onNavigateToFirst: () -> Unit,
) {
    composable<Second> {
        SecondScreen(
            onNavigateToFirst,
        )
    }
}

fun NavController.navigateToFirst() {
    navigate(route = First)
}