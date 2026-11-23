package com.liquidglass.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.liquidglass.launcher.data.db.LauncherDatabase
import com.liquidglass.launcher.ui.theme.LiquidGlassLauncherTheme
import com.liquidglass.launcher.ui.home.HomeScreen
import com.liquidglass.launcher.ui.home.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val db = Room.databaseBuilder(applicationContext, LauncherDatabase::class.java, "launcher.db")
            .fallbackToDestructiveMigration()
            .build()
        val viewModel = HomeViewModel(
            applicationContext,
            db.workspaceDao(),
            db.folderDao(),
            db.folderItemDao(),
            db.iconOverrideDao(),
            db.homeItemDao()
        )
        setContent {
            LiquidGlassLauncherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
