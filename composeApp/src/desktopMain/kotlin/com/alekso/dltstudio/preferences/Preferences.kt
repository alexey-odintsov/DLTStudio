package com.alekso.dltstudio.preferences

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


private const val PREFERENCES_FILES_NAME = "dlt_studio_preferences.txt"
private const val MAX_RECENT_SEARCH = 10
private const val MAX_RECENT_COLOR_FILTER = 5
private const val MAX_RECENT_TIMELINE_FILTER = 5


data class RecentFile(
    val absolutePath: String,
    val fileName: String,
)

object Preferences {
    class State(
        var recentColorFilters: MutableList<RecentFile> = mutableListOf(),
        var recentTimelineFilters: MutableList<RecentFile> = mutableListOf(),
        var recentSearchQueries: MutableList<String> = mutableListOf(),
    )

    private var state = State()
    private val file by lazy {
        val platform = System.getProperty("os.name")
        println("Platform $platform")

        if (platform.startsWith("Mac OS")) {
            val path =
                "${System.getProperty("user.home")}/$PREFERENCES_FILES_NAME"
            println("Preferences path $path")
            File(path)
        } else { // todo: Linux/Win implementation
            println("Preferences default path directory")
            File(PREFERENCES_FILES_NAME)
        }
    }


    fun addRecentSearch(searchText: String) {
        if (state.recentSearchQueries.any { it == searchText }) {
            return
        }

        state.recentSearchQueries.add(searchText)
        if (state.recentSearchQueries.size > MAX_RECENT_SEARCH) {
            state.recentSearchQueries.removeFirst()
        }
    }

    fun addRecentColorFilter(fileName: String, filePath: String) {
        if (state.recentColorFilters.any { it.absolutePath == filePath }) {
            return
        }

        state.recentColorFilters.add(RecentFile(filePath, fileName))
        if (state.recentColorFilters.size > MAX_RECENT_COLOR_FILTER) {
            state.recentColorFilters.removeFirst()
        }
    }

    fun addRecentTimelineFilter(fileName: String, filePath: String) {
        if (state.recentTimelineFilters.any { it.absolutePath == filePath }) {
            return
        }

        state.recentTimelineFilters.add(RecentFile(filePath, fileName))
        if (state.recentTimelineFilters.size > MAX_RECENT_TIMELINE_FILTER) {
            state.recentTimelineFilters.removeFirst()
        }
    }

    fun recentColorFilters() = state.recentColorFilters

    fun recentTimelineFilters() = state.recentTimelineFilters

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
                state = State()
            } else {
                val json = FileReader(file).use {
                    it.readText()
                }
                val type: Type = object : TypeToken<State>() {}.type
                state = Gson().fromJson(json, type)
            }
        } catch (e: Exception) {
            println("Failed to load preferences: $e")
        }
    }
}