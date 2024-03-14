package com.alekso.dltstudio.timeline


open class TimeLineEntry<T>(
    open val timestamp: Long,
    open val key: String,
    open val value: T
)


data class TimeLineStateEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: Pair<String, String>
) : TimeLineEntry<Pair<String, String>>(timestamp, key, value)


abstract class TimeLineEntries<T> {
    val map: MutableMap<String, MutableList<T>> = mutableMapOf()
    abstract fun addEntry(entry: T)
}

class TimeLineStateEntries : TimeLineEntries<TimeLineStateEntry>() {
    val states = mutableListOf<String>()
    override fun addEntry(entry: TimeLineStateEntry) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)

        if (!states.contains(entry.value.first)) {
            states.add(entry.value.first)
        }
        if (!states.contains(entry.value.second)) {
            states.add(entry.value.second)
        }
    }
}

class TimeLinePercentageEntries : TimeLineEntries<TimeLineEntry<Float>>() {
    override fun addEntry(entry: TimeLineEntry<Float>) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)
    }
}

class TimeLineMinMaxEntries : TimeLineEntries<TimeLineEntry<Float>>() {
    var minValue: Float = 0f
    var maxValue: Float = 0f
    override fun addEntry(entry: TimeLineEntry<Float>) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)
        val entryValue = entry.value
        if (entryValue < minValue) {
            minValue = entryValue
        }

        if (entryValue > maxValue) {
            maxValue = entryValue
        }
    }
}

private class Test {

    val list = mutableListOf<TimeLineEntries<*>>()

    fun run() {
        list.add(0, TimeLinePercentageEntries())
        list.add(1, TimeLineMinMaxEntries())
        list.add(2, TimeLineStateEntries())

        val percentageEntries = list[1] as TimeLinePercentageEntries
        percentageEntries.addEntry(TimeLineEntry<Float>(0L, "a", 1f))
    }

}