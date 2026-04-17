package com.volfor.ondori.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.volfor.ondori.app.navigation.Alarms
import com.volfor.ondori.app.navigation.alarmsDestination
import com.volfor.ondori.app.navigation.navigateToSettings
import com.volfor.ondori.app.navigation.settingsDestination
import com.volfor.ondori.app.theme.OndoriTheme

@Composable
fun OndoriApp() {
    OndoriTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Alarms,
        ) {
            alarmsDestination(
                onNavigateToSettings = {
                    navController.navigateToSettings()
                },
            )
            settingsDestination(
                onBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}