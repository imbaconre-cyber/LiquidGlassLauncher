package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

/**
 * Renders a single app icon.
 *
 * @param imageBitmap resolved icon bitmap (null -> glyph fallback showing the label's first letter)
 * @param label used for the fallback glyph and content description
 * @param size icon size in dp
 * @param shape mask applied to the icon
 * @param contentDescription accessibility label
 */
@Composable
fun AppIcon(
    imageBitmap: ImageBitmap?,
    label: String,
    size: Dp,
    shape: IconShape = IconShape.SQUIRCLE,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val clip = Modifier.size(size).clip(shape.toShape())
    val backgroundColor = if (imageBitmap == null) Color.White.copy(alpha = 0.25f) else Color.Transparent

    Box(
        modifier = modifier.then(clip).background(backgroundColor)
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = label.first().uppercase().toString(),
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
