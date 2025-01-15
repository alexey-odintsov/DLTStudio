package com.alekso.dltstudio.logs.colorfilters

import com.alekso.logger.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ColorFilterManager {
    fun saveToFile(colorFilters: List<ColorFilter>, file: File) {
        try {
            FileWriter(file).use {
                it.write(saveFilters(colorFilters))
            }
        } catch (e: Exception) {
            Log.e("Failed to save filters: $e")
        }
    }

    fun loadFromFile(file: File): List<ColorFilter>? {
        var filters: List<ColorFilter>? = null
        try {
            val json = FileReader(file).use {
                it.readText()
            }
            filters = parseFilters(json)
        } catch (e: Exception) {
            Log.e("Failed to load filters: $e")
        }
        return filters
    }

    fun saveFilters(timelineFilters: List<ColorFilter>): String {
        return Json.encodeToString(timelineFilters)
    }

    fun parseFilters(jsonContent: String): List<ColorFilter>? {
        return Json.decodeFromString<List<ColorFilter>?>(jsonContent)
    }

}