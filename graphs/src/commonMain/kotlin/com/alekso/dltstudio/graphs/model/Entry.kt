package com.alekso.dltstudio.graphs.model

import androidx.compose.runtime.Stable

interface ChartKey {
    val key: String
}

interface ChartEntry {
    val timestamp: Long
    val data: Any?
}

data class StringKey(
    override val key: String
) : ChartKey

data class NumericalEntry(
    val value: Float,
    override val timestamp: Long,
    override val data: Any?
) : ChartEntry

data class EventEntry(
    override val timestamp: Long,
    override val data: Any?,
    val event: String,
) : ChartEntry

data class StateEntry(
    override val timestamp: Long,
    override val data: Any?,
    val oldState: String,
    val newState: String,
) : ChartEntry

data class SingleStateEntry(
    override val timestamp: Long,
    override val data: Any?,
    val state: String,
) : ChartEntry

data class DurationEntry(
    override val timestamp: Long,
    override val data: Any?,
    val timestamp2: Long
) : ChartEntry

@Stable
interface ChartData {
    fun isEmpty(): Boolean
    fun getKeys(): List<ChartKey>
    fun getEntries(key: ChartKey): List<ChartEntry>?
    fun getLabels(): List<String>
}

data class FloatChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<NumericalEntry>>,
) : ChartData {
    private var minValue = 0f
    private var maxValue = 0f

    fun addEntry(key: ChartKey, value: NumericalEntry) {
        if (value.value > maxValue) {
            maxValue = value.value
        }
        if (value.value < minValue) {
            minValue = value.value
        }
        val list = entriesMap[key] ?: mutableListOf<NumericalEntry>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<ChartKey> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: ChartKey): List<NumericalEntry> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return emptyList()
    }

    fun getMaxValue() = maxValue
    fun getMinValue() = minValue
}

data class EventsChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<EventEntry>>
) : ChartData {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: ChartKey, label: String, value: EventEntry) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf<EventEntry>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<ChartKey> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: ChartKey): List<EventEntry> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }

}

data class SingleStateChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<SingleStateEntry>>,
) : ChartData {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: ChartKey, label: String, value: SingleStateEntry) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf<SingleStateEntry>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<ChartKey> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: ChartKey): List<ChartEntry> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }

}