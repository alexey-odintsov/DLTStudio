package com.alekso.dltstudio.ui

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.ui.cpu.CPUSEntry
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import java.io.File

class ParseSession(val progressCallback: (Float) -> Unit, val file: File) {
    val dltMessages = mutableStateListOf<DLTMessage>()
    var cpuUsage = mutableListOf<CPUUsageEntry>()
    var cpus = mutableListOf<CPUSEntry>()


    suspend fun start() {
        dltMessages.addAll(DLTParser.read(progressCallback, file.inputStream()))
    }
}