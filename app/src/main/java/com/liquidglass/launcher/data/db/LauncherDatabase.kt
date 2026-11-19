package com.liquidglass.launcher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        HomeItemEntity::class,
        FolderEntity::class,
        FolderItemEntity::class,
        HiddenAppEntity::class,
        LockedAppEntity::class,
        IconOverrideEntity::class,
        GestureEntity::class,
        WorkspaceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun homeItemDao(): HomeItemDao
    abstract fun folderDao(): FolderDao
    abstract fun folderItemDao(): FolderItemDao
    abstract fun hiddenAppDao(): HiddenAppDao
    abstract fun lockedAppDao(): LockedAppDao
    abstract fun iconOverrideDao(): IconOverrideDao
    abstract fun gestureDao(): GestureDao
    abstract fun workspaceDao(): WorkspaceDao
}
