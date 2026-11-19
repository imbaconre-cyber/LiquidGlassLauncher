package com.liquidglass.launcher.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.liquidglass.launcher.ui.glass.GlassSurface

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val apps by viewModel.apps.collectAsState()
    val context = LocalContext.current
    val backdrop = rememberLayerBackdrop()
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps.chunked(25).getOrElse(page) { emptyList() }) { app ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GlassSurface(
                                backdrop = backdrop,
                                shape = RoundedCornerShape(16.dp),
                                blurRadius = 12.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = app.label.take(1), color = Color.White)
                                }
                            }
                            Text(
                                text = app.label,
                                color = Color.White,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            GlassDock(backdrop = backdrop, apps = apps.take(5))
        }

        GlassSearchPill(
            backdrop = backdrop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )
    }
}

@Composable
fun GlassDock(backdrop: com.kyant.backdrop.Backdrop, apps: List<AppInfo>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        apps.forEach { app ->
            GlassSurface(
                backdrop = backdrop,
                shape = CircleShape,
                blurRadius = 16.dp
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(text = app.label.take(1), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GlassSearchPill(backdrop: com.kyant.backdrop.Backdrop, modifier: Modifier = Modifier) {
    GlassSurface(
        backdrop = backdrop,
        shape = RoundedCornerShape(32.dp),
        blurRadius = 20.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Search apps, contacts...", color = Color.White.copy(alpha = 0.8f))
        }
    }
}
