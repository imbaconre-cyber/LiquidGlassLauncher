package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun GlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Dp = 12.dp,
    tint: Color = Color.White.copy(alpha = 0.35f),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val mode = remember { GlassCapability.detect(context) }
    val base = modifier.clip(shape)
    val glassModifier = when (mode) {
        GlassCapability.Mode.FULL -> base.drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                vibrancy()
                blur(blurRadius.toPx())
                lens(16.dp.toPx(), 32.dp.toPx(), true)
            },
            onDrawSurface = { drawRect(tint) }
        )
        GlassCapability.Mode.FROSTED -> base.background(tint.copy(alpha = 0.55f))
        GlassCapability.Mode.SOLID -> base.background(tint.copy(alpha = 0.85f))
    }
    androidx.compose.foundation.layout.Box(modifier = glassModifier) { content() }
}
