package com.alekso.dltstudio.preferences

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


private const val PREFERENCES_FILES_NAME = "dlt_studio_preferences.txt"

object Preferences {
    class State(
        var recentColorFilters: MutableList<String> = mutableListOf(),
        var recentTimelineFilters: MutableList<String> = mutableListOf(),
        var recentSearchQueries: MutableList<String> = mutableListOf(),
    )

    private var state = State()
    private val file by lazy {
        val platform = System.getProperty("os.name")
        println("Platform $platform")

        if (platform.startsWith("Mac OS")) {
            val path =
                "${System.getProperty("user.home")}/Library/Preferences/$PREFERENCES_FILES_NAME"
            println("Save preferences to $path")
            File(path)
        } else { // todo: Linux/Win implementation
            println("Save preferences to default directory")
            File(PREFERENCES_FILES_NAME)
        }
    }


    fun addRecentSearch(searchText: String) {
        state.recentSearchQueries.add(searchText)
        if (state.recentSearchQueries.size > 10) {
            state.recentSearchQueries.removeLast()
        }
    }

    fun saveToFile() {

        try {
            FileWriter(file).use {
                it.write(Gson().toJson(state))
            }
        } catch (e: Exception) {
            println("Failed to save preferences: $e")
        }
    }

    fun loadFromFile() {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val json = FileReader(file).use {
                it.readText()
            }
            val type: Type = object : TypeToken<State>() {}.type
            state = Gson().fromJson(json, type)
        } catch (e: Exception) {
            println("Failed to load preferences: $e")
        }
    }
}