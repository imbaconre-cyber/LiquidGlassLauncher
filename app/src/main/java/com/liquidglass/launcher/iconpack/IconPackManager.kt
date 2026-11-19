package com.liquidglass.launcher.iconpack

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class IconPackManager(private val context: Context) {
    data class IconPack(val packageName: String, val label: String)
    data class IconMapping(val component: String, val drawableName: String)

    fun listIconPacks(): List<IconPack> {
        val pm = context.packageManager
        val intent = android.content.Intent("com.novalauncher.THEME")
        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
        return resolveInfos.map { ri ->
            IconPack(ri.activityInfo.packageName, ri.loadLabel(pm).toString())
        }.distinctBy { it.packageName }
    }

    fun loadMappings(iconPackPackage: String): Map<String, String> {
        val pm = context.packageManager
        val appInfo = try { pm.getApplicationInfo(iconPackPackage, 0) } catch (e: Exception) { return emptyMap() }
        val res = pm.getResourcesForApplication(appInfo)
        val map = mutableMapOf<String, String>()
        try {
            val xmlId = res.getIdentifier("appfilter", "xml", iconPackPackage)
            if (xmlId == 0) return emptyMap()
            val parser = res.getXml(xmlId)
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG && parser.name == "item") {
                    val component = parser.getAttributeValue(null, "component")
                    val drawable = parser.getAttributeValue(null, "drawable")
                    if (component != null && drawable != null) {
                        map[component] = drawable
                    }
                }
                event = parser.next()
            }
        } catch (e: Exception) {
            // ignore
        }
        return map
    }

    fun getIcon(iconPackPackage: String, drawableName: String): Drawable? {
        val pm = context.packageManager
        val appInfo = try { pm.getApplicationInfo(iconPackPackage, 0) } catch (e: Exception) { return null }
        val res = pm.getResourcesForApplication(appInfo)
        val id = res.getIdentifier(drawableName, "drawable", iconPackPackage)
        if (id == 0) return null
        return try { res.getDrawable(id, null) } catch (e: Exception) { null }
    }
}
