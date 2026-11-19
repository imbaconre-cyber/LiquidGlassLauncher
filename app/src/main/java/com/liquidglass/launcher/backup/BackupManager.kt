package com.liquidglass.launcher.backup

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class BackupManager(private val context: Context) {
    data class Backup(
        val version: Int = 1,
        val workspace: JSONObject,
        val hiddenApps: JSONArray,
        val lockedApps: JSONArray,
        val gestures: JSONObject,
        val iconOverrides: JSONArray
    )

    fun toJson(backup: Backup): String = JSONObject().apply {
        put("version", backup.version)
        put("workspace", backup.workspace)
        put("hiddenApps", backup.hiddenApps)
        put("lockedApps", backup.lockedApps)
        put("gestures", backup.gestures)
        put("iconOverrides", backup.iconOverrides)
    }.toString(2)

    fun fromJson(text: String): Backup {
        val o = JSONObject(text)
        return Backup(
            version = o.optInt("version", 1),
            workspace = o.optJSONObject("workspace") ?: JSONObject(),
            hiddenApps = o.optJSONArray("hiddenApps") ?: JSONArray(),
            lockedApps = o.optJSONArray("lockedApps") ?: JSONArray(),
            gestures = o.optJSONObject("gestures") ?: JSONObject(),
            iconOverrides = o.optJSONArray("iconOverrides") ?: JSONArray()
        )
    }

    fun exportTo(uri: Uri, backup: Backup): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { os ->
                OutputStreamWriter(os).use { w -> w.write(toJson(backup)) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importFrom(uri: Uri): Backup? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { ins ->
                BufferedReader(InputStreamReader(ins)).use { r ->
                    val sb = StringBuilder()
                    var line: String?
                    while (r.readLine().also { line = it } != null) sb.append(line).append('\n')
                    fromJson(sb.toString())
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
