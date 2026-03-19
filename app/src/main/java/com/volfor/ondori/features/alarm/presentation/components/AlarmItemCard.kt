package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.models.AlarmUiModel
import com.volfor.ondori.features.alarm.presentation.models.toUiModel

@Composable
fun AlarmItemCard(
    alarm: AlarmUiModel,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    SwipeContent(
        swipeToDismissBoxState,
        alarm,
        onCheckedChange,
        onDelete,
    )
}

@Composable
private fun SwipeContent(
    swipeState: SwipeToDismissBoxState,
    alarm: AlarmUiModel,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    SwipeToDismissBox(
        state = swipeState,
        onDismiss = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onDelete()
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                }

                SwipeToDismissBoxValue.Settled -> {
                    // no action
                }
            }
        },
        backgroundContent = {
            BackgroundContent(
                dismissDirection = swipeState.dismissDirection
            )
        },
    ) {
        AlarmItemContent(alarm, onCheckedChange)
    }
}

@Composable
private fun BackgroundContent(dismissDirection: SwipeToDismissBoxValue) {
    val color = when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Red
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    val alignment = when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }

    Card {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove item",
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .wrapContentSize(alignment)
                .padding(16.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun AlarmItemContent(alarm: AlarmUiModel, onCheckedChange: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = alarm.timeText,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                        )
                    }
                    if (alarm.labelText != null) {
                        Text(alarm.labelText, fontSize = 12.sp)
                    }
                    Text("Mon, Tue, Wed", fontSize = 12.sp)
                }
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = onCheckedChange,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardEnabled() {
    OndoriTheme {
        AlarmItemCard(
            Alarm(
                id = 0,
                hour = 12,
                minute = 15,
                enabled = true,
                label = "Enabled alarm",
            ).toUiModel(),
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardDisabled() {
    OndoriTheme {
        AlarmItemCard(
            Alarm(
                id = 0,
                hour = 1,
                minute = 0,
                enabled = false,
            ).toUiModel(),
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemSwipedRight() {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.StartToEnd,
    )

    OndoriTheme {
        SwipeContent(
            swipeToDismissBoxState,
            Alarm(
                id = 0,
                hour = 1,
                minute = 0,
                enabled = false,
            ).toUiModel(),
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemSwipedLeft() {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.EndToStart,
    )

    OndoriTheme {
        SwipeContent(
            swipeToDismissBoxState,
            Alarm(
                id = 0,
                hour = 1,
                minute = 0,
                enabled = false,
            ).toUiModel(),
            onCheckedChange = {},
            onDelete = {},
        )
    }
}