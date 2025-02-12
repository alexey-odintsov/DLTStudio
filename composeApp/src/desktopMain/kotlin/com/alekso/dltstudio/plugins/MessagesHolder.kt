package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.model.contract.LogMessage
import java.io.File

interface MessagesHolder {
    fun clearMessages()
    fun storeMessages(logMessages: List<LogMessage>)
    fun loadColorFilters(file: File)
    fun clearColorFilters()
    fun saveColorFilters(file: File)
}