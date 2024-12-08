package com.alekso.dltstudio.timeline


abstract class TimeLineEntry<T>(
    open val timestamp: Long,
    open val key: String,
    open val value: T
)

data class TimeLineFloatEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: Float
) : TimeLineEntry<Float>(timestamp, key, value)

/**
 * State that requires new and old values
 */
data class TimeLineStateEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: Pair<String, String>
) : TimeLineEntry<Pair<String, String>>(timestamp, key, value)

/**
 * State that requires only one current value
 */
data class TimeLineSingleStateEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: String,
) : TimeLineEntry<String>(timestamp, key, value)


data class TimeLineDurationEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: Pair<String?, String?>,
) : TimeLineEntry<Pair<String?, String?>>(timestamp, key, value)

data class TimeLineEvent(
    val event: String,
    val info: String?
)

data class TimeLineEventEntry(
    override val timestamp: Long,
    override val key: String,
    override val value: TimeLineEvent
) : TimeLineEntry<TimeLineEvent>(timestamp, key, value)


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

class TimeLineSingleStateEntries : TimeLineEntries<TimeLineSingleStateEntry>() {
    val states = mutableListOf<String>()
    override fun addEntry(entry: TimeLineSingleStateEntry) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)

        if (!states.contains(entry.value)) {
            states.add(entry.value)
        }
    }
}

class TimeLineDurationEntries : TimeLineEntries<TimeLineDurationEntry>() {
    val states = mutableListOf<String>()
    override fun addEntry(entry: TimeLineDurationEntry) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)

        if (!states.contains(entry.key)) {
            states.add(entry.key)
        }
    }
}

class TimeLinePercentageEntries : TimeLineEntries<TimeLineFloatEntry>() {
    override fun addEntry(entry: TimeLineFloatEntry) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)
    }
}

class TimeLineEventEntries : TimeLineEntries<TimeLineEventEntry>() {
    val states = mutableListOf<String>()
    override fun addEntry(entry: TimeLineEventEntry) {
        if (!map.containsKey(entry.key)) {
            map[entry.key] = mutableListOf()
        }
        map[entry.key]?.add(entry)
        if (!states.contains(entry.value.event)) {
            states.add(entry.value.event)
        }
    }
}

class TimeLineMinMaxEntries : TimeLineEntries<TimeLineFloatEntry>() {
    var minValue: Float = 0f
    var maxValue: Float = 0f
    override fun addEntry(entry: TimeLineFloatEntry) {
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
