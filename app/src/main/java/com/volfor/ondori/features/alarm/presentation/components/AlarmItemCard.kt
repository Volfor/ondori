package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.formatters.formattedRepeatDays
import com.volfor.ondori.utils.OndoriPreview
import com.volfor.ondori.utils.previewAlarms

@Composable
fun AlarmItemCard(
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

    SwipeContent(
        swipeState = swipeToDismissBoxState,
        alarm = alarm,
        onClick = onClick,
        onCheckedChange = onCheckedChange,
        onDelete = onDelete,
    )
}

@Composable
private fun SwipeContent(
    swipeState: SwipeToDismissBoxState,
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    SwipeToDismissBox(
        state = swipeState,
        onDismiss = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                }

                else -> {
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
        AlarmItemContent(alarm, onClick, onCheckedChange)
    }
}

@Composable
private fun BackgroundContent(dismissDirection: SwipeToDismissBoxValue) {
    val color = when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Red
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        else -> Color.Transparent
    }

    val alignment = when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),

        ) {
        Box(
            contentAlignment = alignment, modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove item",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AlarmItemContent(
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    val backgroundColor =
        if (alarm.enabled) MaterialTheme.colorScheme.surfaceVariant else OndoriTheme.extraColors.alarmDisabledBackground

    val animatedContainerColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "cardContainerColor"
    )
    val targetAlpha = if (alarm.enabled) 1f else 0.6f
    val animatedContentAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "contentAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor,
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1.0f)
                    .alpha(animatedContentAlpha),
            ) {
                if (alarm.label != null && alarm.label.trim().isNotEmpty()) {
                    Text(
                        alarm.label.toUpperCase(Locale.current),
                        style = MaterialTheme.typography.titleSmall,
                        color = OndoriTheme.extraColors.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                    )
                }
                AlarmTimeText(alarm.hour, alarm.minute)
                RepeatDaysLabel(alarm)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = alarm.enabled,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun RepeatDaysLabel(alarm: Alarm) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            alarm.formattedRepeatDays().toUpperCase(Locale.current),
            fontSize = 10.sp,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardEnabled(
    is24HourFormat: Boolean = true,
    alarm: Alarm = previewAlarms[0],
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmItemCard(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardDisabled(
    is24HourFormat: Boolean = true,
    alarm: Alarm = previewAlarms[2],
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmItemCard(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemSwipedRight(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.StartToEnd,
    )

    OndoriPreview {
        SwipeContent(
            swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemSwipedLeft(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.EndToStart,
    )

    OndoriPreview {
        SwipeContent(
            swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardAmPmEnabled(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[0].copy(enabled = false),
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmItemCard(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmItemCardAmPmDisabled(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[2].copy(enabled = true),
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmItemCard(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}