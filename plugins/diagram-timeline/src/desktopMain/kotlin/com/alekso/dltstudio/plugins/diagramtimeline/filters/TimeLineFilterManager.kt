package com.alekso.dltstudio.plugins.diagramtimeline.filters

import com.alekso.logger.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class TimeLineFilterManager {
    fun saveToFile(timelineFilters: List<TimelineFilter>, file: File) {
        try {
            FileWriter(file).use {
                it.write(saveFilters(timelineFilters))
            }
        } catch (e: Exception) {
            Log.e("Failed to save filters: $e")
        }
    }

    fun loadFromFile(file: File): List<TimelineFilter>? {
        try {
            val json = FileReader(file).use {
                it.readText()
            }
            return parseFilters(json)
        } catch (e: Exception) {
            Log.e("Failed to load filters: $e")
        }
        return null
    }

    fun saveFilters(timelineFilters: List<TimelineFilter>): String {
        return Json.encodeToString(timelineFilters)
    }

    fun parseFilters(jsonContent: String): List<TimelineFilter>? {
        val migrated = jsonContent
            .replace("KeyValueNamed", "NamedGroupsOneEntry")
            .replace("KeyValueGroups", "GroupsManyEntries")
        return Json.decodeFromString<List<TimelineFilter>?>(migrated)
    }

}