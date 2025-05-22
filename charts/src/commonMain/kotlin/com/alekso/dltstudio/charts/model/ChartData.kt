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

data class PercentageChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<PercentageEntry>> = mutableMapOf(),
) : ChartData {
    private var minValue = 0f
    private var maxValue = 100f

    fun addEntry(key: ChartKey, value: PercentageEntry) {
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

    override fun getEntries(key: ChartKey): List<PercentageEntry> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return emptyList()
    }

    fun getMaxValue() = maxValue
    fun getMinValue() = minValue
}

data class MinMaxChartData(
    val entriesMap: MutableMap<ChartKey, MutableList<MinMaxEntry>> = mutableMapOf(),
) : ChartData {
    private var minValue = 0f
    private var maxValue = 0f

    fun addEntry(key: ChartKey, value: MinMaxEntry) {
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

    override fun getEntries(key: ChartKey): List<MinMaxEntry> {
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

    fun addEntry(key: ChartKey, value: StateEntry) {
        if (!_labels.contains(value.oldState)) {
            _labels.add(value.oldState)
        }
        if (!_labels.contains(value.newState)) {
            _labels.add(value.newState)
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

    override fun getEntries(key: ChartKey): List<StateEntry> {
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
    fun addEntry(key: ChartKey, value: SingleStateEntry) {
        if (!_labels.contains(value.state)) {
            _labels.add(value.state)
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