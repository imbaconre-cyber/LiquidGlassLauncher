package com.liquidglass.launcher.ui.glass

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.liquidglass.launcher.ui.home.AppInfo

/**
 * Liquid Glass dock. Functions as an app-shortcut container with tap and long-press behavior
 * identical to the original Fossify dock, rendered inside a glass container using the same
 * backdrop/vibrancy/blur/lens approach as the library's [LiquidBottomTabs] demo.
 */
@Composable
fun FossifyLiquidDock(
    backdrop: Backdrop,
    apps: List<AppInfo>,
    iconFor: @Composable (AppInfo) -> ImageBitmap?,
    onAppClick: (AppInfo) -> Unit,
    onAppLongPress: ((AppInfo) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxItems: Int = 5,
    iconSize: Dp = 44.dp,
    containerBlur: Dp = 16.dp,
    containerTint: Color = Color.White.copy(alpha = 0.16f),
    labelFontSize: TextUnit = 11.sp
) {
    val visible = apps.take(maxItems)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        visible.forEach { app ->
            FossifyLiquidDockItem(
                backdrop = backdrop,
                app = app,
                iconFor = iconFor,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongPress?.invoke(app) },
                iconSize = iconSize,
                containerBlur = containerBlur,
                containerTint = containerTint,
                labelFontSize = labelFontSize
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FossifyLiquidDockItem(
    backdrop: Backdrop,
    app: AppInfo,
    iconFor: @Composable (AppInfo) -> ImageBitmap?,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    iconSize: Dp,
    containerBlur: Dp,
    containerTint: Color,
    labelFontSize: TextUnit
) {
    val image = iconFor(app)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        GlassSurface(
            backdrop = backdrop,
            shape = RoundedCornerShape(20.dp),
            blurRadius = containerBlur,
            tint = containerTint,
            modifier = Modifier.size(iconSize + 14.dp)
        ) {
            AppIcon(
                imageBitmap = image,
                label = app.label,
                size = iconSize,
                shape = IconShape.SQUIRCLE,
                modifier = Modifier.size(iconSize),
                contentDescription = app.label
            )
        }
        Text(
            text = app.label,
            color = Color.White,
            fontSize = labelFontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
