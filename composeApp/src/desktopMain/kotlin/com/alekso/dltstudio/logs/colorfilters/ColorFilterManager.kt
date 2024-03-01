package com.alekso.dltstudio.logs.colorfilters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


class ColorFilterManager {
    fun saveToFile(colorFilters: List<ColorFilter>, file: File) {
        try {
            FileWriter(file).use {
                it.write(Gson().toJson(colorFilters))
            }
        } catch (e: Exception) {
            println("Failed to save filters: $e")
        }
    }

    fun loadFromFile(file: File): List<ColorFilter>? {
        var filters: List<ColorFilter>? = null
        try {
            val json = FileReader(file).use {
                it.readText()
            }
            val type: Type = object : TypeToken<List<ColorFilter?>?>() {}.type
            filters = Gson().fromJson(json, type)
        } catch (e: Exception) {
            println("Failed to load filters: $e")
        }
        return filters
    }

}