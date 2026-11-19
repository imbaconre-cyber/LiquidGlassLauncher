package com.liquidglass.launcher.search

import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class SearchEngine(private val context: Context) {
    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

    data class Result(
        val id: String,
        val title: String,
        val subtitle: String,
        val type: Type,
        val score: Int,
        val launch: () -> Unit
    )

    enum class Type { APP, SHORTCUT, CONTACT, SETTING, CALC, UNIT, WEB }

    private val bangs = mapOf(
        "!g" to "https://www.google.com/search?q=",
        "!yt" to "https://www.youtube.com/results?search_query=",
        "!gh" to "https://github.com/search?q=",
        "!ddg" to "https://duckduckgo.com/?q="
    )

    suspend fun search(query: String): List<Result> = withContext(Dispatchers.Default) {
        if (query.isBlank()) return@withContext emptyList()
        val results = mutableListOf<Result>()

        // Bangs
        for ((bang, url) in bangs) {
            if (query.startsWith(bang, ignoreCase = true)) {
                val term = query.removePrefix(bang).trim()
                if (term.isNotEmpty()) {
                    results.add(Result("web_$bang", "Search $term", url + term, Type.WEB, 100) {
                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url + term)))
                    })
                }
            }
        }

        // Calculator
        try {
            val expr = query.replace(" ", "")
            if (expr.matches(Regex("[0-9+\\-*/().]+"))) {
                val value = eval(expr)
                results.add(Result("calc", "= $value", "Calculator", Type.CALC, 95) {})
            }
        } catch (_: Exception) { }

        val profiles = userManager.userProfiles ?: emptyList()
        // Apps
        for (profile in profiles) {
            val activities = launcherApps.getActivityList(null, profile) ?: emptyList()
            for (info in activities) {
                val label = info.label.toString()
                val score = fuzzyScore(label, query)
                if (score > 0) {
                    results.add(Result(
                        "app_${info.componentName.flattenToString()}",
                        label,
                        info.applicationInfo.packageName,
                        Type.APP,
                        score
                    ) {
                        launcherApps.startMainActivity(info.componentName, profile, null, null)
                    })
                }
            }
        }

        // Shortcuts
        val sq = LauncherApps.ShortcutQuery()
        sq.setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED)
        for (profile in profiles) {
            val shortcuts = launcherApps.getShortcuts(sq, profile) ?: emptyList()
            for (sc in shortcuts) {
                val score = fuzzyScore(sc.shortLabel?.toString() ?: sc.id, query)
                if (score > 0) {
                    results.add(Result(
                        "sc_${sc.id}",
                        sc.shortLabel?.toString() ?: sc.id,
                        sc.activity?.packageName ?: "",
                        Type.SHORTCUT,
                        score - 5
                    ) {
                        launcherApps.startShortcut(sc.activity?.packageName ?: "", sc.id, null, null, profile)
                    })
                }
            }
        }

        results.sortByDescending { it.score }
        results.take(50)
    }

    private fun fuzzyScore(text: String, query: String): Int {
        val t = text.lowercase()
        val q = query.lowercase()
        if (t == q) return 100
        if (t.startsWith(q)) return 80
        if (t.contains(q)) return 60
        // subsequence
        var i = 0
        for (c in t) {
            if (i < q.length && c == q[i]) i++
        }
        return if (i == q.length) 40 else 0
    }

    private fun eval(expr: String): Double {
        // simple recursive descent
        return object {
            var pos = 0
            val s = expr
            fun parse(): Double = parseExpr()
            fun parseExpr(): Double {
                var v = parseTerm()
                while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
                    val op = s[pos++]
                    val t = parseTerm()
                    v = if (op == '+') v + t else v - t
                }
                return v
            }
            fun parseTerm(): Double {
                var v = parseFactor()
                while (pos < s.length && (s[pos] == '*' || s[pos] == '/')) {
                    val op = s[pos++]
                    val f = parseFactor()
                    v = if (op == '*') v * f else v / f
                }
                return v
            }
            fun parseFactor(): Double {
                if (pos < s.length && s[pos] == '(') {
                    pos++
                    val v = parseExpr()
                    if (pos < s.length && s[pos] == ')') pos++
                    return v
                }
                val start = pos
                while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) pos++
                return s.substring(start, pos).toDoubleOrNull() ?: 0.0
            }
        }.parse()
    }
}
