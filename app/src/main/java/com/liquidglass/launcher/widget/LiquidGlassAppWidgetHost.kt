package com.liquidglass.launcher.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent

class LiquidGlassAppWidgetHost(context: Context, hostId: Int) : AppWidgetHost(context, hostId) {
    override fun onCreateView(context: Context, appWidgetId: Int, appWidget: AppWidgetProviderInfo): AppWidgetHostView {
        val view = super.onCreateView(context, appWidgetId, appWidget)
        view.setAppWidget(appWidgetId, appWidget)
        return view
    }
}

object WidgetHostManager {
    const val HOST_ID = 1024
    fun host(context: Context): LiquidGlassAppWidgetHost = LiquidGlassAppWidgetHost(context, HOST_ID)
    fun manager(context: Context): AppWidgetManager = AppWidgetManager.getInstance(context)
    fun pickIntent(): Intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
}
