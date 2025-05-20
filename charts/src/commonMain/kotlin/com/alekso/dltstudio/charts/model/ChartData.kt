package com.alekso.dltstudio.charts.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf

@Stable
interface ChartData {
    fun isEmpty(): Boolean
    fun getKeys(): List<ChartKey>
    fun getEntries(key: ChartKey): List<ChartEntry>?
    fun getLabels(): List<String>
}

data class FloatChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<NumericalEntry>> = mutableMapOf(),
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
        val list = entriesMap[key] ?: mutableListOf()
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
    val entriesMap: MutableMap<ChartKey, MutableList<EventEntry>> = mutableStateMapOf()
) : ChartData {
    private val _labels = mutableListOf<String>()

    fun addEntry(key: ChartKey, value: EventEntry) {
        if (!_labels.contains(value.event)) {
            _labels.add(value.event)
        }
        val list = entriesMap[key] ?: mutableListOf()
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

data class StateChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<StateEntry>> = mutableMapOf(),
) : ChartData {
    private val _labels = mutableListOf<String>()

    fun addEntry(key: ChartKey, label: String, value: StateEntry) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf()
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

data class SingleStateChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<SingleStateEntry>> = mutableStateMapOf(),
) : ChartData {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: ChartKey, label: String, value: SingleStateEntry) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<ChartKey> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: ChartKey): List<SingleStateEntry> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }
}