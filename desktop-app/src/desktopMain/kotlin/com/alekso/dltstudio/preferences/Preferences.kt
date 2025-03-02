package com.alekso.dltstudio.preferences

import com.alekso.dltstudio.Env
import com.alekso.logger.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter


private const val MAX_RECENT_SEARCH = 10
private const val MAX_RECENT_COLOR_FILTER = 5
private const val MAX_RECENT_TIMELINE_FILTER = 5

@Serializable
data class RecentFile(
    val absolutePath: String,
    val fileName: String,
)

object Preferences {
    @Serializable
    class State(
        var recentColorFilters: MutableList<RecentFile> = mutableListOf(),
        var recentTimelineFilters: MutableList<RecentFile> = mutableListOf(),
    )

    private var state = State()
    private val file = File(Env.getPreferencesPath())

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
                it.write(Json.encodeToString(state))
            }
        } catch (e: Exception) {
            Log.e("Failed to save preferences: $e")
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
                state = Json.decodeFromString(json)
            }
        } catch (e: Exception) {
            Log.e("Failed to load preferences: $e")
        }
    }
}