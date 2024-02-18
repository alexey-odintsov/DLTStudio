package com.alekso.dltstudio.timeline

import com.alekso.dltstudio.cpu.CPUSEntry
import com.alekso.dltstudio.cpu.CPUUsageEntry
import com.alekso.dltstudio.user.UserStateEntry

class TimelineViewModel {
    var cpuUsage = mutableListOf<CPUUsageEntry>()
    var cpus = mutableListOf<CPUSEntry>()
    var userStateEntries = mutableMapOf<Int, MutableList<UserStateEntry>>()
    var userEntries = mutableMapOf<String, TimelineEntries>()
    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    val totalSeconds: Int
        get() = if (timeEnd > 0 && timeStart > 0) (timeEnd - timeStart).toInt() / 1000 else 0


}