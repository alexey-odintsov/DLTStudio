package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.ui.geometry.Rect
import kotlin.math.max
import kotlin.math.min

private val defaultPattens = listOf(
    Regex("""\{(?<id2>[a-z0-9]+)?\s?(?<flags1>[A-Z\.]+)?\s?(?<flags2>[A-Z\.]+)?\s?(?<left>\d+),(?<top>\d+)-(?<right>\d+),(?<down>\d+)\s?(?<id>#[a-f0-9]+)?\s?(?<resid>[a-zA-Z0-9\-\_]+:id/[a-zA-Z0-9\_\-]+)?\s?(?<aid>aid\=[0-9]+)?\}"""),
    Regex("""((?<left>\d+)\,\s(?<top>\d+)((\,\s)|(\s-\s))(?<right>\d+)\,\s(?<down>\d+))"""),
    Regex("""x\s*(=|:)\s*(?<left>\d+).*y\s*(=|:)\s*(?<top>\d+).*(w|width)\s*(=|:)\s*(?<width>\d+).*(h|height)\s*(=|:)\s*(?<height>\d+)"""),
    Regex("""x[=:]\s*(?<x>\d+)[,\s;]\s*y[=:]\s*(?<y>\d+)"""),
    Regex("""x=(?<x>\d+),\sy=(?<y>\d+),\swidth=(?<w>\d+),\sheight=(?<h>\d+)""")
)

class DeviceViewParser(
    private val patterns: List<Regex> = defaultPattens,
) {
    fun parse(text: String): List<DeviceView>? {
        val list = mutableListOf<DeviceView>()
        patterns.forEach { pattern ->
            val viewMatches = pattern.findAll(text)
            for (match in viewMatches) {
                val l = match.getFloatSafeOrNull("left")
                val t = match.getFloatSafeOrNull("top")
                val r = match.getFloatSafeOrNull("right")
                val d = match.getFloatSafeOrNull("down")
                val w = match.getFloatSafeOrNull("width")
                val h = match.getFloatSafeOrNull("height")
                val x = match.getFloatSafeOrNull("x")
                val y = match.getFloatSafeOrNull("y")
                val resId = match.getValueSafeOrNull("resid")

                if (l != null && t != null && r != null && d != null) {
                    list.addIfNotPresent(RectView(Rect(min(l, r), t, max(r, l), d), resId as String?))
                } else if (l != null && t != null && w != null && h != null) {
                    list.addIfNotPresent(RectView(Rect(l, t, l + w, t + h), resId as String?))
                } else if (x != null && y != null && w != null && h != null) {
                    list.addIfNotPresent(RectView(Rect(x, y, x + w, y + h), resId as String?))
                } else if (x != null && y != null) {
                    list.addIfNotPresent(PointerView(x, y))
                }
            }
        }
        return list
    }

    private fun MutableList<DeviceView>.addIfNotPresent(item: DeviceView) {
        if (contains(item).not()) {
            add(item)
        }
    }

    private fun MatchResult.getFloatSafeOrNull(key: String): Float? {
        return try {
            groups[key]?.value?.toFloat()
        } catch (_: Exception) {
            null
        }
    }
    private fun MatchResult.getValueSafeOrNull(key: String): Any? {
        return try {
            groups[key]?.value
        } catch (_: Exception) {
            null
        }
    }
}
