package com.liquidglass.launcher.iconpack

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.liquidglass.launcher.data.db.IconOverrideEntity
import com.liquidglass.launcher.ui.home.AppInfo

object IconResolver {
    /**
     * Resolves the best available drawable for an app:
     * 1) explicit per-app override (custom URI or icon-pack+icon name)
     * 2) selected icon pack via its appfilter mapping
     * 3) the package's default launcher icon
     */
    fun resolveDrawable(
        context: Context,
        iconPackManager: IconPackManager,
        selectedPack: String?,
        overrides: List<IconOverrideEntity>,
        app: AppInfo
    ): Drawable? {
        val pm = context.packageManager
        val component = ComponentName(app.packageName, app.className)

        // 1. Per-app override (database icon_overrides table)
        val override = overrides.find { it.packageName == app.packageName }
        if (override != null) {
            override.customIconUri?.let { uri ->
                return try {
                    Uri.parse(uri).let { u ->
                        context.contentResolver.openInputStream(u)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }?.let { BitmapDrawable(context.resources, it) }
                    }
                } catch (e: Exception) { null }
            }
            override.iconPackPackage?.let { pack ->
                override.iconName?.let { name ->
                    val icon = iconPackManager.getIcon(pack, name)
                    if (icon != null) return icon
                }
            }
        }

        // 2. Selected icon pack (appfilter.xml component mapping)
        selectedPack?.let { pack ->
            val mappings = iconPackManager.loadMappings(pack)
            val drawableName =
                mappings[component.flattenToString()] ?: mappings["${app.packageName}/${app.className}"] ?: mappings[app.packageName]
            if (drawableName != null) {
                val icon = iconPackManager.getIcon(pack, drawableName)
                if (icon != null) return icon
            }
        }

        // 3. Package default launcher icon
        return try {
            pm.getActivityIcon(component)
        } catch (e: Exception) {
            try { pm.getApplicationIcon(app.packageName) } catch (e2: Exception) { null }
        }
    }

    fun drawableToBitmap(drawable: Drawable?, sizePx: Int): Bitmap? {
        if (drawable == null) return null
        val size = sizePx.coerceAtLeast(1)
        if (drawable is BitmapDrawable) {
            val b = drawable.bitmap
            return if (b.width == size && b.height == size) b
            else Bitmap.createScaledBitmap(b, size, size, true)
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    fun drawableToImageBitmap(drawable: Drawable?, sizeDp: Float, density: Float): ImageBitmap? {
        val sizePx = (sizeDp * density).coerceAtLeast(1).toInt()
        return drawableToBitmap(drawable, sizePx)?.let { ImageBitmap.imageBitmap(it) }
    }
}
