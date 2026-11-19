package com.liquidglass.launcher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiquidGlassLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
