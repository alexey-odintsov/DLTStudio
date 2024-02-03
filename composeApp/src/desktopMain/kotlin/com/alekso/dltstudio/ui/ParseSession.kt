package com.alekso.dltstudio.ui

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.ui.cpu.CPUSEntry
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import com.alekso.dltstudio.ui.memory.MemoryUsageEntry
import com.alekso.dltstudio.ui.user.UserStateEntry
import java.io.File

class ParseSession(private val progressCallback: (Float) -> Unit, val files: List<File>) {
    val dltMessages = mutableStateListOf<DLTMessage>()
    var cpuUsage = mutableListOf<CPUUsageEntry>()
    var cpus = mutableListOf<CPUSEntry>()
    var memt = mutableMapOf<String, MutableList<MemoryUsageEntry>>()
    var userStateEntries = mutableMapOf<Int, MutableList<UserStateEntry>>()
    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    var totalSeconds: Int = 0

    suspend fun start() {
        dltMessages.addAll(DLTParser.read(progressCallback, files))
    }
}