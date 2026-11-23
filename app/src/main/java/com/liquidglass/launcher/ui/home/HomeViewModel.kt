package com.liquidglass.launcher.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.UserManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.launcher.data.db.FolderDao
import com.liquidglass.launcher.data.db.FolderEntity
import com.liquidglass.launcher.data.db.FolderItemDao
import com.liquidglass.launcher.data.db.FolderItemEntity
import com.liquidglass.launcher.data.db.HomeItemDao
import com.liquidglass.launcher.data.db.HomeItemEntity
import com.liquidglass.launcher.data.db.IconOverrideDao
import com.liquidglass.launcher.data.db.IconOverrideEntity
import com.liquidglass.launcher.data.db.WorkspaceDao
import com.liquidglass.launcher.data.db.WorkspaceEntity
import com.liquidglass.launcher.iconpack.IconPackManager
import com.liquidglass.launcher.iconpack.IconResolver
import com.liquidglass.launcher.settings.LauncherSettings
import com.liquidglass.launcher.ui.glass.IconShape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppInfo(
    val packageName: String,
    val className: String,
    val label: String,
    val userHandle: android.os.UserHandle
)

class HomeViewModel(
    private val context: Context,
    private val workspaceDao: WorkspaceDao,
    private val folderDao: FolderDao,
    private val folderItemDao: FolderItemDao,
    private val iconOverrideDao: IconOverrideDao,
    private val homeItemDao: HomeItemDao
) : ViewModel() {

    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as android.content.pm.LauncherApps
    private val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
    private val settings = LauncherSettings(context)
    private val iconPackManager = IconPackManager(context)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    /** packageName/className -> AppInfo */
    private val _appsByComponent = MutableStateFlow<Map<String, AppInfo>>(emptyMap())
    val appsByComponent: StateFlow<Map<String, AppInfo>> = _appsByComponent.asStateFlow()

    val workspace: StateFlow<WorkspaceEntity?> = workspaceDao.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val folders: StateFlow<List<FolderEntity>> = folderDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overrides: StateFlow<List<IconOverrideEntity>> = iconOverrideDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedIconPack: StateFlow<String?> = settings.selectedIconPackFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val iconShape: StateFlow<String> = settings.iconShapeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SQUIRCLE")

    /**
     * Merged user configuration: grid columns/rows, icon scale, dock size, page count,
     * plus icon pack & shape. Sourced from DataStore (the settings UI writes here), so changes
     * are reflected live in the home screen.
     */
    val config: StateFlow<LauncherConfig> = combine(
        settings.homeColumnsFlow,
        settings.homeRowsFlow,
        settings.iconScaleFlow,
        settings.dockSizeFlow,
        settings.pageCountFlow,
        settings.showLabelsFlow,
        settings.dockEnabledFlow,
        selectedIconPack,
        iconShape
    ) { columns, rows, iconScale, dockSize, pageCount, showLabels, dockEnabled, pack, shape ->
        LauncherConfig(
            columns = columns.coerceIn(1, 12),
            rows = rows.coerceIn(1, 12),
            iconScale = iconScale.coerceIn(0.5f, 2.5f),
            dockSize = dockSize.coerceIn(1, 10),
            pageCount = pageCount.coerceIn(1, 10),
            showLabels = showLabels,
            dockEnabled = dockEnabled,
            selectedIconPack = pack,
            iconShape = shapeToIconShape(shape)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LauncherConfig())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadApps()
        viewModelScope.launch {
            if (workspaceDao.get() == null) {
                workspaceDao.upsert(WorkspaceEntity())
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profiles = userManager.userProfiles
                val list = mutableListOf<AppInfo>()
                val byComponent = mutableMapOf<String, AppInfo>()
                for (profile in profiles) {
                    val activities = launcherApps.getActivityList(null, profile)
                    for (info in activities ?: emptyList()) {
                        val app = AppInfo(
                            packageName = info.applicationInfo.packageName,
                            className = info.componentName.className,
                            label = info.label.toString(),
                            userHandle = profile
                        )
                        list.add(app)
                        byComponent["${app.packageName}/${app.className}"] = app
                    }
                }
                _apps.value = list.sortedBy { it.label.lowercase() }
                _appsByComponent.value = byComponent
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadApps()

    fun launch(app: AppInfo) {
        launcherApps.startMainActivity(
            ComponentName(app.packageName, app.className),
            app.userHandle,
            null,
            null
        )
    }

    /* -------------------- settings -------------------- */

    suspend fun setHomeColumns(value: Int) = settings.setHomeColumns(value)
    suspend fun setHomeRows(value: Int) = settings.setHomeRows(value)
    suspend fun setIconScale(value: Float) = settings.setIconScale(value)
    suspend fun setDockSize(value: Int) = settings.setDockSize(value)
    suspend fun setPageCount(value: Int) = settings.setPageCount(value)
    suspend fun setShowLabels(value: Boolean) = settings.setShowLabels(value)
    suspend fun setDockEnabled(value: Boolean) = settings.setDockEnabled(value)

    suspend fun selectIconPack(packageName: String?) = settings.setSelectedIconPack(packageName)
    suspend fun setIconShape(shape: IconShape) = settings.setIconShape(shape.name)

    /**
     * Per-app icon override. Pass iconPackPackage + iconName for a pack icon, or
     * customIconUri for a content URI; set both to null to clear the override.
     */
    suspend fun setIconOverride(
        packageName: String,
        iconPackPackage: String? = null,
        iconName: String? = null,
        customIconUri: String? = null
    ) {
        iconOverrideDao.insert(
            IconOverrideEntity(
                packageName = packageName,
                iconPackPackage = iconPackPackage,
                iconName = iconName,
                customIconUri = customIconUri
            )
        )
    }

    /* -------------------- icon resolution -------------------- */

    /**
     * Resolves the best drawable for an app (override -> icon pack -> default).
     * Call from a coroutine; convert to Compose ImageBitmap in the UI layer.
     */
    suspend fun resolveDrawable(app: AppInfo): Drawable? =
        IconResolver.resolveDrawable(
            context = context,
            iconPackManager = iconPackManager,
            selectedPack = selectedIconPack.value,
            overrides = overrides.value,
            app = app
        )

    /**
     * AppInfos belonging to a folder, ordered by position.
     */
    suspend fun folderApps(folderId: Long): List<AppInfo> {
        val items = folderItemDao.observe(folderId).first()
        return items
            .mapNotNull { it ->
                _appsByComponent.value["${it.packageName}/${it.className}"]
            }
    }

    private fun shapeToIconShape(shape: String): IconShape =
        IconShape.values().firstOrNull { it.name == shape } ?: IconShape.SQUIRCLE

    /**
     * Home items on a page, ordered by row then column (respects spanX/spanY loosely).
     */
    fun observePage(page: Int): Flow<List<HomeItemEntity>> = homeItemDao.observePage(page)

    /**
     * Resolve an AppInfo from a home item, if it is an app shortcut.
     */
    fun appInfoFor(item: HomeItemEntity): AppInfo? =
        _appsByComponent.value["${item.packageName}/${item.className}"]
}
