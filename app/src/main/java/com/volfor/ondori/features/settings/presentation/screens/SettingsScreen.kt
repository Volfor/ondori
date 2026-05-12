@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.settings.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.R
import com.volfor.ondori.features.settings.presentation.components.SnoozeMinutesPickerDialog
import com.volfor.ondori.features.settings.presentation.viewmodels.SettingsViewModel
import com.volfor.ondori.utils.OndoriPreview

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        onBack = onBack,
        loading = uiState.isLoading,
        snoozeMinutes = uiState.snoozeMinutes,
        onSnoozeMinutesChange = viewModel::setSnoozeMinutes,
    )
}

@Composable
private fun SettingsContent(
    onBack: () -> Unit,
    loading: Boolean,
    snoozeMinutes: Int,
    onSnoozeMinutesChange: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.action_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        if (loading) {
            Box(modifier = Modifier.size(0.dp))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SnoozeLength(
                    value = snoozeMinutes,
                    onValueChange = onSnoozeMinutesChange,
                )
            }
        }
    }
}

@Composable
private fun SnoozeLength(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        SnoozeMinutesPickerDialog(
            selectedValue = value,
            onDismiss = { showPicker = false },
            onSelect = { value ->
                onValueChange(value)
                showPicker = false
            },
        )
    }

    ListItem(
        modifier = Modifier
            .clickable { showPicker = true }
            .padding(horizontal = 8.dp),
        headlineContent = {
            Text("Snooze length")
        },
        supportingContent = {
            Text(
                pluralStringResource(
                    R.plurals.minutes,
                    value,
                    value,
                )
            )
        },
    )
}


@Preview
@Composable
private fun SettingsScreenPreview() {
    OndoriPreview {
        SettingsContent(
            onBack = {},
            loading = false,
            snoozeMinutes = 9,
            onSnoozeMinutesChange = {},
        )
    }
}
