package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.volfor.ondori.app.theme.OndoriTheme
import kotlin.math.roundToInt

private const val COMPLETE_FRACTION = 0.75f
private const val THUMB_PRESSED_SCALE = 1.12f

@Composable
fun SwipeToStopSlider(
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbSize = 56.dp
    val horizontalPadding = 8.dp
    val trackHeight = 72.dp
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragged) THUMB_PRESSED_SCALE else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "swipeThumbScale",
    )

    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    val thumbPx = remember(density) { with(density) { thumbSize.toPx() } }
    val paddingPx = remember(density) { with(density) { horizontalPadding.toPx() } }
    val extraHalfWidthPx = if (thumbScale > 1f) thumbScale * thumbPx * 0.5f else 0f
    val maxOffsetPx = remember(trackWidthPx, thumbPx, paddingPx, thumbScale) {
        (trackWidthPx - thumbPx - 2f * paddingPx - extraHalfWidthPx).coerceAtLeast(0f)
    }
    var thumbOffsetPx by remember { mutableFloatStateOf(0f) }
    val maxOffsetUpdated by rememberUpdatedState(maxOffsetPx)

    LaunchedEffect(maxOffsetPx) {
        thumbOffsetPx = thumbOffsetPx.coerceIn(0f, maxOffsetPx)
    }

    val draggableState = rememberDraggableState { delta ->
        val max = maxOffsetUpdated
        if (max <= 0) return@rememberDraggableState
        thumbOffsetPx = (thumbOffsetPx + delta).coerceIn(0f, max)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .semantics {
                contentDescription = "Slide to the right to stop the alarm"
            }
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .clip(RoundedCornerShape(trackHeight / 2))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            text = "Slide to stop",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = horizontalPadding)
                .size(thumbSize)
                .scale(thumbScale)
                .offset {
                    IntOffset(thumbOffsetPx.roundToInt(), 0)
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    enabled = maxOffsetPx > 0f,
                    interactionSource = interactionSource,
                    startDragImmediately = true,
                    onDragStopped = { _ ->
                        val max = maxOffsetPx + extraHalfWidthPx
                        if (max <= 0) return@draggable
                        val start = thumbOffsetPx
                        val threshold = max * COMPLETE_FRACTION
                        val target = if (start >= threshold) max else 0f
                        val anim = Animatable(start)

                        anim.animateTo(
                            targetValue = target,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        ) {
                            thumbOffsetPx = value
                        }
                        if (target == max) {
                            onStop()
                        }
                    },
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSwipeToStopSlider() {
    OndoriTheme {
        SwipeToStopSlider(
            onStop = {},
        )
    }
}

