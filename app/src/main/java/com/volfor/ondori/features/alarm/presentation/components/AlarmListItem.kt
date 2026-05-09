@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.formatters.formattedRepeatDays
import com.volfor.ondori.utils.OndoriPreview
import com.volfor.ondori.utils.previewAlarms

@Composable
fun AlarmListItem(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

    SwipeContent(
        modifier = modifier,
        swipeState = swipeToDismissBoxState,
        alarm = alarm,
        onClick = onClick,
        onCheckedChange = onCheckedChange,
        onDelete = onDelete,
    )
}

@Composable
private fun SwipeContent(
    modifier: Modifier = Modifier,
    swipeState: SwipeToDismissBoxState,
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,

    ) {
    SwipeToDismissBox(
        modifier = modifier,
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
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.error
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }

    val alignment = when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }

    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Remove item",
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp),
            )
            .wrapContentSize(alignment)
            .padding(16.dp),
        tint = MaterialTheme.colorScheme.onError,
    )
}

@Composable
private fun AlarmItemContent(
    alarm: Alarm,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    val targetAlpha = if (alarm.enabled) 1f else 0.38f
    val animatedContentAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "contentAlpha"
    )

    val shape = RoundedCornerShape(16.dp)

    ListItem(
        onClick = onClick, modifier = Modifier, enabled = true,
        trailingContent = {
            Switch(
                modifier = Modifier.padding(top = 8.dp),
                checked = alarm.enabled,
                onCheckedChange = onCheckedChange,
            )
        },
        overlineContent = {
            if (!alarm.label.isNullOrBlank()) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .alpha(animatedContentAlpha),
                    text = alarm.label.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (alarm.enabled) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                )
            }
        },
        supportingContent = {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(animatedContentAlpha),
            ) {
                RepeatDaysLabel(alarm = alarm)
            }
        },
        shapes = ListItemDefaults.shapes(
            shape = shape,
            selectedShape = shape,
            pressedShape = shape,
            focusedShape = shape,
            hoveredShape = shape,
            draggedShape = shape,
        ),
        colors = ListItemDefaults.colors(
            containerColor = if (alarm.enabled) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceContainer
        ),
        contentPadding = PaddingValues(16.dp),
    ) {
        Box(
            modifier = Modifier.alpha(animatedContentAlpha),
        ) {
            AlarmTimeText(
                hour = alarm.hour,
                minute = alarm.minute,
                useBoldFonts = alarm.enabled,
            )
        }
    }
}

@Composable
private fun RepeatDaysLabel(alarm: Alarm) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            alarm.formattedRepeatDays().toUpperCase(Locale.current),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmListItemEnabledLight(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[1].copy(enabled = true),
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmListItem(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmListItemDisabledLight(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[2].copy(
        enabled = false,
    ),
) {
    OndoriPreview(is24HourFormat = is24HourFormat) {
        AlarmListItem(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmListItemSwipedRightLight(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.StartToEnd,
    )

    OndoriPreview {
        SwipeContent(
            swipeState = swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmListItemSwipedLeftLight(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.EndToStart,
    )

    OndoriPreview {
        SwipeContent(
            swipeState = swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmListItemEnabledDark(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[1].copy(enabled = true),
) {
    OndoriPreview(darkTheme = true, is24HourFormat = is24HourFormat) {
        AlarmListItem(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmListItemDisabledDark(
    is24HourFormat: Boolean = false,
    alarm: Alarm = previewAlarms[2].copy(
        enabled = false,
    ),
) {
    OndoriPreview(darkTheme = true, is24HourFormat = is24HourFormat) {
        AlarmListItem(
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmListItemSwipedRightDark(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.StartToEnd,
    )

    OndoriPreview(darkTheme = true) {
        SwipeContent(
            swipeState = swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmListItemSwipedLeftDark(
    alarm: Alarm = previewAlarms[0],
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.EndToStart,
    )

    OndoriPreview(darkTheme = true) {
        SwipeContent(
            swipeState = swipeToDismissBoxState,
            alarm = alarm,
            onClick = {},
            onCheckedChange = {},
            onDelete = {},
        )
    }
}