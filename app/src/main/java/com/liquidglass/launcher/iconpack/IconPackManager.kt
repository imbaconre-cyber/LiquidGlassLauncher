package com.liquidglass.launcher.iconpack

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class IconPackManager(private val context: Context) {
    data class IconPack(val packageName: String, val label: String)
    data class IconMapping(val component: String, val drawableName: String)

    // Standard intent actions used by third-party icon packs to advertise themselves.
    private val THEME_INTENT_ACTIONS = listOf(
        "com.novalauncher.THEME",
        "org.adw.launcher.THEMES",
        "com.htc.launcher.action.THEME_CHANGED",
        "com.gau.go.launcherex.theme",
        "com.teslacoilsw.launcher.THEME"
    )

    /**
     * Discovers installed icon packs by querying the standard theme intents.
     */
    fun listIconPacks(): List<IconPack> {
        val pm = context.packageManager
        val packs = mutableMapOf<String, IconPack>()
        for (action in THEME_INTENT_ACTIONS) {
            val intent = Intent(action)
            val resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            for (ri in resolveInfos) {
                val pkg = ri.activityInfo.packageName
                if (pkg.isNotBlank() && !packs.containsKey(pkg)) {
                    packs[pkg] = IconPack(pkg, ri.loadLabel(pm).toString())
                }
            }
        }
        return packs.values.toList()
    }

    fun isIconPackInstalled(packageName: String): Boolean =
        listIconPacks().any { it.packageName == packageName }

    /**
     * Reads an icon pack's appfilter.xml and returns component -> drawable-name mappings.
     */
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
            // ignore malformed packs
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

    /**
     * Loads a custom icon from a content URI (used for per-app custom icon overrides).
     */
    fun getIconFromUri(uriString: String): Drawable? {
        return try {
            Uri.parse(uriString).let { uri ->
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }?.let { BitmapDrawable(context.resources, it) }
            }
        } catch (e: Exception) { null }
    }
}
