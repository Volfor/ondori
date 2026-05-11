package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.volfor.ondori.utils.OndoriPreview

@Composable
fun OndoriAlertDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = content,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}

@Preview
@Composable
fun PreviewOndoriAlertDialogLight() {
    OndoriPreview {
        OndoriAlertDialog(
            onConfirm = {},
            onDismiss = {},
            title = "Dialog title",
            content = { Text("A dialog is a type of modal window that appears in front of app content to provide critical information, or prompt for a decision to be made.") },
        )
    }
}

@Preview
@Composable
fun PreviewOndoriAlertDialogDark() {
    OndoriPreview(darkTheme = true) {
        OndoriAlertDialog(
            onConfirm = {},
            onDismiss = {},
            title = "Dialog title",
            content = { Text("A dialog is a type of modal window that appears in front of app content to provide critical information, or prompt for a decision to be made.") },
        )
    }
}