package com.liquidglass.launcher.ui.home

import android.content.Context
import android.content.pm.PackageManager
import android.os.UserManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.launcher.data.db.WorkspaceDao
import com.liquidglass.launcher.data.db.WorkspaceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppInfo(
    val packageName: String,
    val className: String,
    val label: String,
    val userHandle: android.os.UserHandle
)

class HomeViewModel(
    private val context: Context,
    private val workspaceDao: WorkspaceDao
) : ViewModel() {

    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as android.content.pm.LauncherApps
    private val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    val workspace: StateFlow<WorkspaceEntity?> = workspaceDao.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
            val profiles = userManager.userProfiles
            val list = mutableListOf<AppInfo>()
            for (profile in profiles) {
                val activities = launcherApps.getActivityList(null, profile)
                for (info in activities) {
                    list.add(
                        AppInfo(
                            packageName = info.applicationInfo.packageName,
                            className = info.componentName.className,
                            label = info.label.toString(),
                            userHandle = profile
                        )
                    )
                }
            }
            _apps.value = list.sortedBy { it.label.lowercase() }
        }
    }

    fun refresh() = loadApps()
}
