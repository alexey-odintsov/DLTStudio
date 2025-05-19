package com.alekso.dltstudio.graphs.model

import androidx.compose.runtime.Stable

interface Key {
    val key: String
}

interface Value {
    val timestamp: Long
    val data: Any?
}

data class StringKey(
    override val key: String
) : Key

data class NumericalValue(
    val value: Float,
    override val timestamp: Long,
    override val data: Any?
) : Value

data class EventValue(
    override val timestamp: Long,
    override val data: Any?,
    val event: String,
) : Value

data class StateValue(
    override val timestamp: Long,
    override val data: Any?,
    val oldState: String,
    val newState: String,
) : Value

data class SingleStateValue(
    override val timestamp: Long,
    override val data: Any?,
    val state: String,
) : Value

data class DurationValue(
    override val timestamp: Long,
    override val data: Any?,
    val timestamp2: Long
) : Value

@Stable
interface ChartData {
    fun isEmpty(): Boolean
    fun getKeys(): List<Key>
    fun getEntries(key: Key): List<Value>?
    fun getLabels(): List<String>
}

data class FloatChartData(
    val entriesMap: MutableMap<Key, MutableList<Value>>,
) : ChartData {
    fun addEntry(key: Key, value: Value) {
        val list = entriesMap[key] ?: mutableListOf<Value>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<Key> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: Key): List<Value> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return emptyList()
    }
}

data class EventsChartData(
    val entriesMap: MutableMap<Key, MutableList<EventValue>>
) : ChartData {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: Key, label: String, value: EventValue) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf<EventValue>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<Key> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: Key): List<Value> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }

}

data class SingleStateChartData(
    val entriesMap: MutableMap<Key, MutableList<SingleStateValue>>,
) : ChartData {
    private val _labels = mutableListOf<String>()
    fun addEntry(key: Key, label: String, value: SingleStateValue) {
        if (!_labels.contains(label)) {
            _labels.add(label)
        }
        val list = entriesMap[key] ?: mutableListOf<SingleStateValue>()
        list.add(value)
        entriesMap[key] = list
    }

    override fun isEmpty(): Boolean {
        return entriesMap.isEmpty()
    }

    override fun getKeys(): List<Key> {
        return entriesMap.keys.toList()
    }

    override fun getEntries(key: Key): List<Value> {
        return entriesMap[key]?.toList() ?: emptyList()
    }

    override fun getLabels(): List<String> {
        return _labels
    }

}