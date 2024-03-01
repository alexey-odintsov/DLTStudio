package com.alekso.dltstudio.logs.colorfilters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


class ColorFilterManager {
    fun saveToFile(colorFilters: List<ColorFilter>, file: File) {
        FileWriter(file).use {
            it.write(Gson().toJson(colorFilters))
        }
    }

    fun loadFromFile(file: File): List<ColorFilter>? {
        val json = FileReader(file).use {
            it.readText()
        }
        val type: Type = object : TypeToken<List<ColorFilter?>?>() {}.type
        val filters: List<ColorFilter>? = Gson().fromJson(json, type)
        return filters

    }


}