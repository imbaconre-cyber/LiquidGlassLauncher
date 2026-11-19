package com.liquidglass.launcher.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeItemDao {
    @Query("SELECT * FROM home_items WHERE page = :page ORDER BY row, column")
    fun observePage(page: Int): Flow<List<HomeItemEntity>>

    @Query("SELECT * FROM home_items")
    fun observeAll(): Flow<List<HomeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HomeItemEntity): Long

    @Update
    suspend fun update(item: HomeItemEntity)

    @Delete
    suspend fun delete(item: HomeItemEntity)

    @Query("DELETE FROM home_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM home_items")
    suspend fun clearAll()
}

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders")
    fun observeAll(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun get(id: Long): FolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: FolderEntity): Long

    @Update
    suspend fun update(folder: FolderEntity)

    @Delete
    suspend fun delete(folder: FolderEntity)
}

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM folder_items WHERE folderId = :folderId ORDER BY position")
    fun observe(folderId: Long): Flow<List<FolderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FolderItemEntity): Long

    @Delete
    suspend fun delete(item: FolderItemEntity)

    @Query("DELETE FROM folder_items WHERE folderId = :folderId")
    suspend fun clearFolder(folderId: Long)
}

@Dao
interface HiddenAppDao {
    @Query("SELECT * FROM hidden_apps")
    fun observeAll(): Flow<List<HiddenAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HiddenAppEntity)

    @Delete
    suspend fun delete(entity: HiddenAppEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM hidden_apps WHERE packageName = :pkg)")
    suspend fun isHidden(pkg: String): Boolean
}

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps")
    fun observeAll(): Flow<List<LockedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LockedAppEntity)

    @Delete
    suspend fun delete(entity: LockedAppEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM locked_apps WHERE packageName = :pkg)")
    suspend fun isLocked(pkg: String): Boolean
}

@Dao
interface IconOverrideDao {
    @Query("SELECT * FROM icon_overrides")
    fun observeAll(): Flow<List<IconOverrideEntity>>

    @Query("SELECT * FROM icon_overrides WHERE packageName = :pkg")
    suspend fun get(pkg: String): IconOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: IconOverrideEntity)

    @Delete
    suspend fun delete(entity: IconOverrideEntity)
}

@Dao
interface GestureDao {
    @Query("SELECT * FROM gestures")
    fun observeAll(): Flow<List<GestureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GestureEntity)

    @Query("SELECT action FROM gestures WHERE gestureKey = :key")
    suspend fun actionFor(key: String): String?
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces WHERE id = 1")
    fun observe(): Flow<WorkspaceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkspaceEntity)

    @Query("SELECT * FROM workspaces WHERE id = 1")
    suspend fun get(): WorkspaceEntity?
}
