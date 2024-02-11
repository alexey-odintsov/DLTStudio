package com.alekso.dltstudio.timeline

data class TimelineEntry(
    val timestamp: Long,
    val key: String,
    val value: String
)

interface TimelineEntries {
    fun getEntriesMap(): MutableMap<String, MutableList<TimelineEntry>>

    fun addEntry(entry: TimelineEntry)
}

class TimelinePercentageEntries : TimelineEntries {
    val entries = mutableMapOf<String, MutableList<TimelineEntry>>()
    override fun getEntriesMap(): MutableMap<String, MutableList<TimelineEntry>> {
        return entries
    }

    override fun addEntry(entry: TimelineEntry) {
        if (!entries.containsKey(entry.key)) {
            entries[entry.key] = mutableListOf()
        }
        entries[entry.key]?.add(entry)
    }
}

class TimelineMinMaxEntries : TimelineEntries {
    val entries = mutableMapOf<String, MutableList<TimelineEntry>>()
    var minValue: Float = 0f
    var maxValue: Float = 0f
    override fun getEntriesMap(): MutableMap<String, MutableList<TimelineEntry>> {
        return entries
    }

    override fun addEntry(entry: TimelineEntry) {
        if (!entries.containsKey(entry.key)) {
            entries[entry.key] = mutableListOf()
        }
        entries[entry.key]?.add(entry)

        val entryValue = entry.value.toFloat()
        if (entryValue < minValue) {
            minValue = entryValue
        }

        if (entryValue > maxValue) {
            maxValue = entryValue
        }
    }
}