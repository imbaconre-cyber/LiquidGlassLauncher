package com.liquidglass.launcher.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.launcher.iconpack.IconPackManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settings: LauncherSettings,
    private val iconPackManager: IconPackManager
) : ViewModel() {

    val homeColumns: StateFlow<Int> = settings.homeColumnsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)
    val homeRows: StateFlow<Int> = settings.homeRowsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)
    val iconScale: StateFlow<Float> = settings.iconScaleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1f)
    val dockSize: StateFlow<Int> = settings.dockSizeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)
    val pageCount: StateFlow<Int> = settings.pageCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)
    val showLabels: StateFlow<Boolean> = settings.showLabelsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val dockEnabled: StateFlow<Boolean> = settings.dockEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val selectedIconPack: StateFlow<String?> = settings.selectedIconPackFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val iconShape: StateFlow<String> = settings.iconShapeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SQUIRCLE")

    private val _iconPacks = MutableStateFlow<List<IconPackManager.IconPack>>(emptyList())
    val iconPacks: StateFlow<List<IconPackManager.IconPack>> = _iconPacks.asStateFlow()

    init {
        viewModelScope.launch {
            _iconPacks.value = withContext(Dispatchers.Default) {
                iconPackManager.listIconPacks()
            }
        }
    }

    /* ---------- grid ---------- */
    fun setHomeColumns(value: Int) = viewModelScope.launch { settings.setHomeColumns(value) }
    fun setHomeRows(value: Int) = viewModelScope.launch { settings.setHomeRows(value) }

    /* ---------- icon size ---------- */
    fun setIconScale(value: Float) = viewModelScope.launch { settings.setIconScale(value) }

    /* ---------- dock ---------- */
    fun setDockSize(value: Int) = viewModelScope.launch { settings.setDockSize(value) }
    fun setPageCount(value: Int) = viewModelScope.launch { settings.setPageCount(value) }
    fun setShowLabels(value: Boolean) = viewModelScope.launch { settings.setShowLabels(value) }
    fun setDockEnabled(value: Boolean) = viewModelScope.launch { settings.setDockEnabled(value) }

    /* ---------- appearance ---------- */
    fun selectIconPack(packageName: String?) = viewModelScope.launch { settings.setSelectedIconPack(packageName) }
    fun setIconShape(shapeName: String) = viewModelScope.launch { settings.setIconShape(shapeName) }
}
