package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

/**
 * Liquid Glass slider built on top of the AndroidLiquidGlass backdrop modifiers.
 *
 * Preserves the standard [onValueChange] / [onValueChangeFinished] callbacks and drag behavior.
 * When [backdrop] is null (e.g. used outside a layer-backed hierarchy) it falls back to a solid
 * colored track so the component still renders correctly.
 */
@Composable
fun FossifyLiquidSlider(
    value: Float,
    onValueChange: ((Float) -> Unit)?,
    onValueChangeFinished: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    trackHeight: Dp = 4.dp,
    activeTrackHeight: Dp = 6.dp,
    thumbSize: Dp = 20.dp,
    thumbColor: Color = Color.White,
    trackColor: Color = Color.White.copy(alpha = 0.20f),
    activeTrackColor: Color = Color(0xFF0A84FF),
    glass: Boolean = true,
    backdrop: Backdrop? = null,
    label: @Composable (() -> Unit)? = null
) {
    val density = LocalDensity.current
    val thumbPx = with(density) { thumbSize.toPx() }
    val halfThumb = thumbPx / 2f

    val trackWidth = remember { mutableFloatStateOf(0f) }
    val thumbOffset = remember { mutableFloatStateOf(0f) }

    val onValueChangeState = rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState = rememberUpdatedState(onValueChangeFinished)

    fun mapValueToPx(v: Float, width: Float): Float {
        val fraction = ((v - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val span = (width - 2 * halfThumb).coerceAtLeast(0f)
        return halfThumb + fraction * span
    }

    fun mapPxToValue(px: Float, width: Float): Float {
        val span = (width - 2 * halfThumb).coerceAtLeast(1f)
        val fraction = ((px - halfThumb) / span).coerceIn(0f, 1f)
        return valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
    }

    // Snap thumb to external value changes (e.g. coming back from settings).
    LaunchedEffect(value, valueRange) {
        val width = trackWidth.value
        if (width > 0) {
            thumbOffset.value = mapValueToPx(value, width)
        }
    }

    label?.invoke()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        if (!enabled) return@detectDragGestures
                        val width = trackWidth.value
                        if (width <= thumbPx) return@detectDragGestures
                        change.consume()
                        val newOffset = (thumbOffset.value + dragAmount.x).coerceIn(halfThumb, width - halfThumb)
                        thumbOffset.value = newOffset
                        onValueChangeState.value?.invoke(mapPxToValue(newOffset, width))
                    },
                    onDragEnd = {
                        if (!enabled) return@detectDragGestures
                        val width = trackWidth.value
                        if (width <= thumbPx) return@detectDragGestures
                        onValueChangeFinishedState.value?.invoke(mapPxToValue(thumbOffset.value, width))
                    }
                )
            }
            .onSizeChanged { size ->
                trackWidth.value = size.width.toFloat()
                thumbOffset.value = mapValueToPx(value, size.width.toFloat())
            }
    ) {
        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val width = trackWidth.value
        val activeWidth = (width * fraction).coerceAtLeast(0f)

        // Track
        if (glass && backdrop != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .align(Alignment.Center)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedCornerShape(trackHeight) },
                        effects = {
                            vibrancy()
                            blur(8f)
                            lens(16f, 32f, true)
                        },
                        onDrawSurface = { drawRect(trackColor) }
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .align(Alignment.Center)
                    .background(trackColor, RoundedCornerShape(trackHeight))
            )
        }

        // Active (filled) portion
        Box(
            modifier = Modifier
                .width(with(density) { activeWidth.toDp() })
                .height(activeTrackHeight)
                .align(Alignment.CenterStart)
                .background(activeTrackColor, RoundedCornerShape(activeTrackHeight))
        )

        // Thumb (absolutely positioned over the track)
        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset(x = with(density) { (thumbOffset.value - halfThumb).toDp() }, y = 0.dp)
                .align(Alignment.CenterStart)
                .background(thumbColor, CircleShape)
                .clip(CircleShape)
        )
    }
}
