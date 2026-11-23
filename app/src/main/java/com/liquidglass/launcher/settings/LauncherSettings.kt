package com.liquidglass.launcher.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object LauncherSettingsKeys {
    val HOME_COLUMNS = intPreferencesKey("home_columns")
    val HOME_ROWS = intPreferencesKey("home_rows")
    val ICON_SCALE = floatPreferencesKey("icon_scale")
    val DOCK_SIZE = intPreferencesKey("dock_size")
    val PAGE_COUNT = intPreferencesKey("page_count")
    val SHOW_LABELS = booleanPreferencesKey("show_labels")
    val DOCK_ENABLED = booleanPreferencesKey("dock_enabled")
    val SELECTED_ICON_PACK = stringPreferencesKey("selected_icon_pack")
    val ICON_SHAPE = stringPreferencesKey("icon_shape")
}

/**
 * Persisted launcher preferences backed by DataStore Preferences.
 */
class LauncherSettings(private val context: Context) {
    private val dataStore = preferencesDataStore(context, "launcher_settings")

    /* ---------- Home grid ---------- */
    suspend fun setHomeColumns(value: Int) = dataStore.edit { it[LauncherSettingsKeys.HOME_COLUMNS] = value }
    suspend fun getHomeColumns(): Int = dataStore.data.first()[LauncherSettingsKeys.HOME_COLUMNS] ?: 5
    val homeColumnsFlow: Flow<Int> = dataStore.data.map { it[LauncherSettingsKeys.HOME_COLUMNS] ?: 5 }

    suspend fun setHomeRows(value: Int) = dataStore.edit { it[LauncherSettingsKeys.HOME_ROWS] = value }
    suspend fun getHomeRows(): Int = dataStore.data.first()[LauncherSettingsKeys.HOME_ROWS] ?: 6
    val homeRowsFlow: Flow<Int> = dataStore.data.map { it[LauncherSettingsKeys.HOME_ROWS] ?: 6 }

    /* ---------- Icon size ---------- */
    suspend fun setIconScale(value: Float) = dataStore.edit { it[LauncherSettingsKeys.ICON_SCALE] = value }
    suspend fun getIconScale(): Float = dataStore.data.first()[LauncherSettingsKeys.ICON_SCALE] ?: 1.0f
    val iconScaleFlow: Flow<Float> = dataStore.data.map { it[LauncherSettingsKeys.ICON_SCALE] ?: 1.0f }

    /* ---------- Dock ---------- */
    suspend fun setDockSize(value: Int) = dataStore.edit { it[LauncherSettingsKeys.DOCK_SIZE] = value }
    suspend fun getDockSize(): Int = dataStore.data.first()[LauncherSettingsKeys.DOCK_SIZE] ?: 5
    val dockSizeFlow: Flow<Int> = dataStore.data.map { it[LauncherSettingsKeys.DOCK_SIZE] ?: 5 }

    suspend fun setPageCount(value: Int) = dataStore.edit { it[LauncherSettingsKeys.PAGE_COUNT] = value }
    suspend fun getPageCount(): Int = dataStore.data.first()[LauncherSettingsKeys.PAGE_COUNT] ?: 3
    val pageCountFlow: Flow<Int> = dataStore.data.map { it[LauncherSettingsKeys.PAGE_COUNT] ?: 3 }

    suspend fun setShowLabels(value: Boolean) = dataStore.edit { it[LauncherSettingsKeys.SHOW_LABELS] = value }
    suspend fun getShowLabels(): Boolean = dataStore.data.first()[LauncherSettingsKeys.SHOW_LABELS] ?: true
    val showLabelsFlow: Flow<Boolean> = dataStore.data.map { it[LauncherSettingsKeys.SHOW_LABELS] ?: true }

    suspend fun setDockEnabled(value: Boolean) = dataStore.edit { it[LauncherSettingsKeys.DOCK_ENABLED] = value }
    suspend fun getDockEnabled(): Boolean = dataStore.data.first()[LauncherSettingsKeys.DOCK_ENABLED] ?: true
    val dockEnabledFlow: Flow<Boolean> = dataStore.data.map { it[LauncherSettingsKeys.DOCK_ENABLED] ?: true }

    /* ---------- Icon pack / shape ---------- */
    suspend fun setSelectedIconPack(value: String?) = dataStore.edit { it[LauncherSettingsKeys.SELECTED_ICON_PACK] = value }
    suspend fun getSelectedIconPack(): String? = dataStore.data.first()[LauncherSettingsKeys.SELECTED_ICON_PACK]
    val selectedIconPackFlow: Flow<String?> = dataStore.data.map { it[LauncherSettingsKeys.SELECTED_ICON_PACK] }

    suspend fun setIconShape(value: String) = dataStore.edit { it[LauncherSettingsKeys.ICON_SHAPE] = value }
    suspend fun getIconShape(): String = dataStore.data.first()[LauncherSettingsKeys.ICON_SHAPE] ?: "SQUIRCLE"
    val iconShapeFlow: Flow<String> = dataStore.data.map { it[LauncherSettingsKeys.ICON_SHAPE] ?: "SQUIRCLE" }
}
