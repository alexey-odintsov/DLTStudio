package com.alekso.dltstudio.timeline.filters

import com.alekso.logger.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


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
        return Gson().toJson(timelineFilters)
    }

    fun parseFilters(jsonContent: String): List<TimelineFilter>? {
        val type: Type = object : TypeToken<List<TimelineFilter?>?>() {}.type
        return Gson().fromJson(jsonContent, type)
    }

}