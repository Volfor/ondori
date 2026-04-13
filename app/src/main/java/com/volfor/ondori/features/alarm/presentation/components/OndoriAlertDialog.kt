package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.volfor.ondori.app.theme.OndoriTheme

@Composable
fun OndoriAlertDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.labelMedium) },
        containerColor = MaterialTheme.colorScheme.surface,
        text = content,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Preview
@Composable
fun PreviewOndoriAlertDialog() {
    OndoriTheme {
        OndoriAlertDialog(
            onConfirm = {},
            onDismiss = {},
            title = "Title",
            content = { Text("Content") },
        )
    }
}