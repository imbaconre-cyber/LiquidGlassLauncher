package com.liquidglass.launcher.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.liquidglass.launcher.ui.glass.AppIcon
import com.liquidglass.launcher.ui.glass.FossifyLiquidDock
import com.liquidglass.launcher.ui.glass.GlassFolder
import com.liquidglass.launcher.ui.glass.GlassSurface
import com.liquidglass.launcher.ui.glass.IconShape
import com.liquidglass.launcher.ui.glass.toShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val config by viewModel.config.collectAsState()
    val apps by viewModel.apps.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val backdrop = rememberLayerBackdrop()

    val pageCount = config.pageCount.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val cellsPerPage = config.columns * config.rows
    // Base icon size scaled by the user-configured icon scale.
    val iconSize: Dp = (56f * config.iconScale).coerceIn(32f, 120f).dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .layerBackdrop(backdrop)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val pageItems by viewModel.observePage(page).collectAsState(initial = emptyList())
                LazyVerticalGrid(
                    columns = GridCells.Fixed(config.columns.coerceAtLeast(1)),
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pageItems, key = { it.id }) { item ->
                        when (item.type) {
                            "folder" -> {
                                val folderId = item.folderId
                                if (folderId != null) {
                                    val folderAppsState = remember(folderId) { mutableStateOf<List<AppInfo>>(emptyList()) }
                                    LaunchedEffect(folderId) {
                                        viewModel.folderApps(folderId).let { list ->
                                            folderAppsState.value = list
                                        }
                                    }
                                    GlassFolder(
                                        backdrop = backdrop,
                                        apps = folderAppsState.value,
                                        iconFor = { app -> appIconBitmap(viewModel, app, 30f, density.density) },
                                        onAppClick = { app -> viewModel.launch(app) },
                                        size = iconSize,
                                        iconSize = iconSize * 0.6f,
                                        shape = config.iconShape
                                    )
                                }
                            }
                            "app" -> {
                                viewModel.appInfoFor(item)?.let { app ->
                                    val bitmap = appIconBitmap(viewModel, app, with(density) { iconSize.toPx() }, density.density)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable { viewModel.launch(app) }
                                    ) {
                                        GlassSurface(
                                            backdrop = backdrop,
                                            shape = config.iconShape.toShape(),
                                            blurRadius = 12.dp,
                                            tint = Color.White.copy(alpha = 0.14f),
                                            modifier = Modifier.size(iconSize + 12.dp)
                                        ) {
                                            AppIcon(
                                                imageBitmap = bitmap,
                                                label = app.label,
                                                size = iconSize,
                                                shape = config.iconShape,
                                                contentDescription = app.label
                                            )
                                        }
                                        if (config.showLabels) {
                                            Text(
                                                text = app.label,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {
                                // widget / shortcut placeholders render as a glass chip
                                Box(
                                    modifier = Modifier
                                        .size(iconSize)
                                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }
                    // Fill remaining cells of the page so the last row aligns.
                    val remainder = cellsPerPage - (pageItems.size % cellsPerPage)
                    if (remainder > 0 && remainder < cellsPerPage) {
                        items(remainder) { Box(modifier = Modifier.size(iconSize)) }
                    }
                }
            }

            if (config.dockEnabled) {
                FossifyLiquidDock(
                    backdrop = backdrop,
                    apps = apps.take(config.dockSize.coerceAtLeast(1)),
                    iconFor = { app -> appIconBitmap(viewModel, app, with(density) { iconSize.toPx() }, density.density) },
                    onAppClick = { app -> viewModel.launch(app) },
                    onAppLongPress = { app -> /* TODO: open edit/rename dialog */ },
                    maxItems = config.dockSize.coerceAtLeast(1),
                    iconSize = 44.dp,
                    containerBlur = 16.dp,
                    containerTint = Color.White.copy(alpha = 0.16f)
                )
            }
        }

        GlassSearchPill(
            backdrop = backdrop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            onClick = {
                context.startActivity(
                    android.content.Intent(context, com.liquidglass.launcher.settings.SettingsActivity::class.java)
                )
            }
        )
    }
}

/**
 * Glass search pill that also doubles as the settings entry point (tap to open settings).
 */
@Composable
fun GlassSearchPill(
    backdrop: com.kyant.backdrop.Backdrop,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    GlassSurface(
        backdrop = backdrop,
        shape = RoundedCornerShape(32.dp),
        blurRadius = 20.dp,
        modifier = clickableModifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Search apps, contacts...", color = Color.White.copy(alpha = 0.8f))
        }
    }
}

/**
 * Resolves and caches an app icon as a Compose [ImageBitmap], re-resolving when the selected
 * icon pack or per-app overrides change.
 */
@Composable
private fun appIconBitmap(
    viewModel: HomeViewModel,
    app: AppInfo,
    sizeDp: Float,
    density: Float
): ImageBitmap? {
    val state = remember { mutableStateOf<ImageBitmap?>(null) }
    val pack = viewModel.selectedIconPack.value
    val ovCount = viewModel.overrides.value.size
    LaunchedEffect(app, pack, ovCount) {
        viewModel.resolveDrawable(app)?.let { drawable ->
            state.value = com.liquidglass.launcher.iconpack.IconResolver.drawableToImageBitmap(
                drawable, sizeDp, density
            )
        }
    }
    return state.value
}
