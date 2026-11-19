package com.liquidglass.launcher.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_items")
data class HomeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val page: Int,
    val row: Int,
    val column: Int,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val type: String, // "app", "folder", "widget", "shortcut"
    val packageName: String? = null,
    val className: String? = null,
    val folderId: Long? = null,
    val widgetId: Int? = null,
    val label: String? = null
)

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverMode: String = "GRID", // GRID, STACKED, SWIPE
    val gridColumns: Int = 4,
    val gridRows: Int = 4
)

@Entity(tableName = "folder_items")
data class FolderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long,
    val packageName: String,
    val className: String,
    val position: Int
)

@Entity(tableName = "hidden_apps")
data class HiddenAppEntity(
    @PrimaryKey val packageName: String
)

@Entity(tableName = "locked_apps")
data class LockedAppEntity(
    @PrimaryKey val packageName: String
)

@Entity(tableName = "icon_overrides")
data class IconOverrideEntity(
    @PrimaryKey val packageName: String,
    val iconPackPackage: String? = null,
    val iconName: String? = null,
    val customIconUri: String? = null
)

@Entity(tableName = "gestures")
data class GestureEntity(
    @PrimaryKey val gestureKey: String,
    val action: String
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: Long = 1,
    val columns: Int = 5,
    val rows: Int = 6,
    val iconScale: Float = 1f,
    val showLabels: Boolean = true,
    val dockEnabled: Boolean = true,
    val dockSize: Int = 5,
    val pageCount: Int = 3
)
