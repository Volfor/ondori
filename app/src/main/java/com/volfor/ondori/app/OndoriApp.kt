package com.volfor.ondori.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.volfor.ondori.app.navigation.Alarms
import com.volfor.ondori.app.navigation.alarmsDestination
import com.volfor.ondori.app.navigation.infoDestination
import com.volfor.ondori.app.navigation.navigateToAlarms
import com.volfor.ondori.app.navigation.navigateToInfo
import com.volfor.ondori.app.theme.OndoriTheme

@Composable
fun OndoriApp() {
    OndoriTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Alarms,
        ) {
            alarmsDestination(onNavigateToInfo = {
                navController.navigateToInfo()
            })
            infoDestination(onNavigateToAlarms = {
                navController.navigateToAlarms()
            }, onBack = {
                navController.popBackStack()
            })
        }
    }
}