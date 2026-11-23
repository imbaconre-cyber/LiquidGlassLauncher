package com.liquidglass.launcher.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liquidglass.launcher.ui.glass.FossifyLiquidSlider
import com.liquidglass.launcher.ui.glass.IconShape

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val columns by viewModel.homeColumns.collectAsState()
    val rows by viewModel.homeRows.collectAsState()
    val iconScale by viewModel.iconScale.collectAsState()
    val dockSize by viewModel.dockSize.collectAsState()
    val pageCount by viewModel.pageCount.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val dockEnabled by viewModel.dockEnabled.collectAsState()
    val selectedIconPack by viewModel.selectedIconPack.collectAsState()
    val iconShapeName by viewModel.iconShape.collectAsState()
    val iconPacks by viewModel.iconPacks.collectAsState()

    val dockExpanded = remember { mutableStateOf(false) }
    val packExpanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(com.liquidglass.launcher.R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = "Tweak your Liquid Glass launcher.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            // ---------- Home grid ----------
            section(stringResource(com.liquidglass.launcher.R.string.section_home_grid))
            choiceRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_columns),
                selected = columns,
                options = listOf(4 to "4", 5 to "5", 6 to "6"),
                onSelect = { viewModel.setHomeColumns(it) }
            )
            choiceRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_rows),
                selected = rows,
                options = listOf(4 to "4", 5 to "5", 6 to "6", 7 to "7", 8 to "8"),
                onSelect = { viewModel.setHomeRows(it) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            // ---------- Dock ----------
            section(stringResource(com.liquidglass.launcher.R.string.section_dock))
            toggleRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_dock_enabled),
                checked = dockEnabled,
                onChecked = { viewModel.setDockEnabled(it) }
            )
            choiceRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_dock_size),
                selected = dockSize,
                options = listOf(4 to "4", 5 to "5", 6 to "6"),
                onSelect = { viewModel.setDockSize(it) }
            )
            choiceRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_page_count),
                selected = pageCount,
                options = listOf(1 to "1", 2 to "2", 3 to "3", 4 to "4", 5 to "5"),
                onSelect = { viewModel.setPageCount(it) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            // ---------- Appearance ----------
            section(stringResource(com.liquidglass.launcher.R.string.section_appearance))

            Text(
                text = "${stringResource(com.liquidglass.launcher.R.string.label_icon_size)} (${"%.1f".format(iconScale)}x)",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            FossifyLiquidSlider(
                value = iconScale,
                onValueChange = { viewModel.setIconScale(it) },
                onValueChangeFinished = { viewModel.setIconScale(it) },
                valueRange = 0.5f..2.0f,
                trackHeight = 6.dp,
                activeTrackHeight = 8.dp,
                thumbSize = 24.dp,
                backdrop = null, // settings screen has no layer backdrop; solid fallback renders
            )

            choiceRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_icon_shape),
                selected = -1, // shape handled by name below
                options = emptyList(),
                onSelect = {}
            )
            shapeRow(
                selectedName = iconShapeName,
                onSelect = { viewModel.setIconShape(it) }
            )

            packRow(
                selectedPackage = selectedIconPack,
                packs = iconPacks,
                expanded = packExpanded,
                onSelect = { viewModel.selectIconPack(it) }
            )

            toggleRow(
                label = stringResource(com.liquidglass.launcher.R.string.label_show_labels),
                checked = showLabels,
                onChecked = { viewModel.setShowLabels(it) }
            )
        }
    }
}

@Composable
private fun section(title: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun choiceRow(
    label: String,
    selected: Int,
    options: List<Pair<Int, String>>,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.width(110.dp)
        )
        options.forEach { (value, text) ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.1f),
                        androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        }
        Text(
            text = "current: $selected",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun shapeRow(selectedName: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(com.liquidglass.launcher.R.string.label_icon_shape),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.width(110.dp)
        )
        listOf(
            IconShape.CIRCLE to stringResource(com.liquidglass.launcher.R.string.label_shape_circle),
            IconShape.ROUNDED_SQUARE to stringResource(com.liquidglass.launcher.R.string.label_shape_rounded_square),
            IconShape.SQUIRCLE to stringResource(com.liquidglass.launcher.R.string.label_shape_squircle)
        ).forEach { (shape, text) ->
            val isSelected = shape.name == selectedName
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.1f),
                        androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(shape.name) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun toggleRow(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@Composable
private fun packRow(
    selectedPackage: String?,
    packs: List<com.liquidglass.launcher.iconpack.IconPackManager.IconPack>,
    expanded: MutableState<Boolean>,
    onSelect: (String?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(com.liquidglass.launcher.R.string.label_icon_pack),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        val currentLabel = packs.firstOrNull { it.packageName == selectedPackage }?.label
            ?: stringResource(com.liquidglass.launcher.R.string.label_none)
        OutlinedButton(
            onClick = { expanded.value = !expanded.value },
            modifier = Modifier.width(140.dp)
        ) {
            Text(text = currentLabel, color = Color.White)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(com.liquidglass.launcher.R.string.label_none), color = Color.White) },
                onClick = { onSelect(null); expanded.value = false }
            )
            packs.forEach { pack ->
                DropdownMenuItem(
                    text = { Text(text = pack.label, color = Color.White) },
                    onClick = { onSelect(pack.packageName); expanded.value = false }
                )
            }
        }
    }
}
