package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Icon shapes the user can pick independently of the icon pack.
 */
enum class IconShape {
    CIRCLE,
    ROUNDED_SQUARE,
    SQUIRCLE
}

fun IconShape.toShape(): Shape = when (this) {
    IconShape.CIRCLE -> CircleShape
    IconShape.ROUNDED_SQUARE -> RoundedCornerShape(16.dp)
    IconShape.SQUIRCLE -> RoundedCornerShape(28.dp)
}
