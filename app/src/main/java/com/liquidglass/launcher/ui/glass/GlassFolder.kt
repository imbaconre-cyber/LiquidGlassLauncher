package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.liquidglass.launcher.ui.home.AppInfo

/**
 * Glass-like home screen folder container.
 */
@Composable
fun GlassFolder(
    backdrop: Backdrop?,
    apps: List<AppInfo>,
    iconFor: (AppInfo) -> ImageBitmap?,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    iconSize: Dp = 34.dp,
    shape: IconShape = IconShape.SQUIRCLE
) {
    GlassSurface(
        backdrop = backdrop,
        shape = shape.toShape(),
        blurRadius = 14.dp,
        tint = Color.White.copy(alpha = 0.20f),
        modifier = modifier.size(size)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(apps) { app ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppIcon(
                        imageBitmap = iconFor(app),
                        label = app.label,
                        size = iconSize,
                        shape = shape,
                        modifier = Modifier.size(iconSize),
                        contentDescription = app.label
                    )
                    Text(
                        text = app.label,
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                .clickable { onAppClick(app) }
            }
        }
    }
}

/**
 * Subtle glass tint behind a widget (or any content) for visual cohesion.
 */
@Composable
fun GlassWidgetBackground(
    backdrop: Backdrop?,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    blurRadius: Dp = 10.dp,
    tint: Color = Color.White.copy(alpha = 0.10f),
    content: @Composable () -> Unit
) {
    GlassSurface(
        backdrop = backdrop,
        shape = shape,
        blurRadius = blurRadius,
        tint = tint,
        modifier = modifier,
        content = content
    )
}
