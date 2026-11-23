package com.liquidglass.launcher.ui.home

import com.liquidglass.launcher.ui.glass.IconShape

/**
 * User-configurable launcher layout & appearance. Sourced from WorkspaceEntity (DB) merged
 * with DataStore preferences (icon pack, icon shape).
 */
data class LauncherConfig(
    val columns: Int = 5,
    val rows: Int = 6,
    val iconScale: Float = 1.0f,
    val dockSize: Int = 5,
    val pageCount: Int = 3,
    val showLabels: Boolean = true,
    val dockEnabled: Boolean = true,
    val selectedIconPack: String? = null,
    val iconShape: IconShape = IconShape.SQUIRCLE
)
