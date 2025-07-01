package com.alekso.dltstudio.charts.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf

@Stable
interface ChartData<T> {
    fun isEmpty(): Boolean
    fun getKeys(): List<ChartKey>
    fun getEntries(key: ChartKey): List<ChartEntry<T>>?
    fun getLabels(): List<String>
}

data class PercentageChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<PercentageEntry<T>>> = mutableMapOf(),
) : ChartData<T> {
    private var minValue = 0f
    private var maxValue = 100f

    fun addEntry(key: ChartKey, value: PercentageEntry<T>) {
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

    override fun getEntries(key: ChartKey): List<PercentageEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return emptyList()
    }

    fun getMaxValue() = maxValue
    fun getMinValue() = minValue
}

data class MinMaxChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<MinMaxEntry<T>>> = mutableMapOf(),
) : ChartData<T> {
    private var minValue = 0f
    private var maxValue = 0f

    fun addEntry(key: ChartKey, value: MinMaxEntry<T>) {
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

    override fun getEntries(key: ChartKey): List<MinMaxEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return emptyList()
    }

    fun getMaxValue() = maxValue
    fun getMinValue() = minValue
}

data class EventsChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<EventEntry<T>>> = mutableStateMapOf()
) : ChartData<T> {
    private val _labels = mutableListOf<String>()

    fun addEntry(key: ChartKey, value: EventEntry<T>) {
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

    override fun getEntries(key: ChartKey): List<EventEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }
}

data class StateChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<StateEntry<T>>> = mutableMapOf(),
) : ChartData<T> {
    private val _labels = mutableListOf<String>()

    fun addEntry(key: ChartKey, value: StateEntry<T>) {
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

    override fun getEntries(key: ChartKey): List<StateEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }
}

data class SingleStateChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<SingleStateEntry<T>>> = mutableStateMapOf(),
) : ChartData<T> {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: ChartKey, value: SingleStateEntry<T>) {
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

    override fun getEntries(key: ChartKey): List<SingleStateEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }
}

data class DurationChartData<T>(
    private val entriesMap: MutableMap<ChartKey, MutableList<DurationEntry<T>>> = mutableStateMapOf(),
) : ChartData<T> {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: ChartKey, value: DurationEntry<T>) {
        if (!_labels.contains(key.key)) {
            _labels.add(key.key)
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

    override fun getEntries(key: ChartKey): List<DurationEntry<T>> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }
}