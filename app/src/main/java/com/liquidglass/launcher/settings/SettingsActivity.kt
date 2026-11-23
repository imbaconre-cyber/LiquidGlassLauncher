package com.liquidglass.launcher.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.liquidglass.launcher.iconpack.IconPackManager
import com.liquidglass.launcher.ui.theme.LiquidGlassLauncherTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiquidGlassLauncherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        viewModel = SettingsViewModel(
                            settings = LauncherSettings(applicationContext),
                            iconPackManager = IconPackManager(applicationContext)
                        )
                    )
                }
            }
        }
    }
}
