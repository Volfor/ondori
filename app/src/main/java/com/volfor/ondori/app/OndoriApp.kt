package com.volfor.ondori.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.volfor.ondori.R
import com.volfor.ondori.app.navigation.First
import com.volfor.ondori.app.navigation.firstDestination
import com.volfor.ondori.app.navigation.navigateToFirst
import com.volfor.ondori.app.navigation.navigateToSecond
import com.volfor.ondori.app.navigation.secondDestination
import com.volfor.ondori.app.theme.OndoriTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun OndoriApp() {
    OndoriTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val backStackEntry by navController.currentBackStackEntryAsState()
        val canNavigateBack = backStackEntry?.destination?.hasRoute<First>() != true

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.action_settings)
                            )
                        }
                    })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Replace with your own action",
                                actionLabel = "Action",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                // handle action click
                            }
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Email, contentDescription = "Add"
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = First,
                modifier = Modifier.padding(padding)
            ) {
                firstDestination(onNavigateToSecond = {
                    navController.navigateToSecond()
                })
                secondDestination(
                    onNavigateToFirst = { navController.navigateToFirst() })
            }
        }
    }
}