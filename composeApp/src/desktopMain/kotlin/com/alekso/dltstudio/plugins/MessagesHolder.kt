package com.alekso.dltstudio.plugins

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.LogMessage
import java.io.File

interface MessagesHolder {
    fun getMessages(): SnapshotStateList<LogMessage>
    fun clearMessages()
    fun storeMessages(logMessages: List<LogMessage>)
    fun loadColorFilters(file: File)
    fun clearColorFilters()
    fun saveColorFilters(file: File)
}