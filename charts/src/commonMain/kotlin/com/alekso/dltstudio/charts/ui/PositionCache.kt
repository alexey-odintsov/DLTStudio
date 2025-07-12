package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.geometry.Offset
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import kotlin.math.hypot

class PositionCache<T> {
    private val cache = mutableMapOf<Pair<ChartKey, Long>, Pair<Offset, ChartEntry<T>>>()

    fun put(key: ChartKey, timestamp: Long, offset: Offset, entry: ChartEntry<T>) {
        cache[Pair(key, timestamp)] = Pair(offset, entry)
    }

    fun get(key: ChartKey, timestamp: Long): Pair<Offset, ChartEntry<T>>? {
        return cache[Pair(key, timestamp)]
    }

    fun getNearestEntry(position: Offset): ChartEntry<T>? {
        val pixelThreshold = 15f
        val distances = mutableMapOf<Float, ChartEntry<T>>()
        for (key in cache.keys) {
            val entryPosition = cache[key] ?: continue
            val distance =
                hypot(position.x - entryPosition.first.x, position.y - entryPosition.first.y)
            if (distance <= pixelThreshold) {
                distances[distance] = entryPosition.second
            }
        }
        if (distances.isNotEmpty()) {
            val pair = distances.minBy { it.key }
            return pair.value
        }
        return null
    }
}