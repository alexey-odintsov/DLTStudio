package com.alekso.dltstudio.ui.cpu

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload


data class CPUUsageEntry(
    val timestamp: Int,
    val cpuUsage: List<Float>,
)

object CPUAnalyzer {
    fun analyzeCPUUsage(dltMessage: DLTMessage): CPUUsageEntry {
        val cpuUsageList = mutableListOf<Float>()

        val payload = (dltMessage.payload as VerbosePayload).asText()
        var idx = 0
        var i = 0

        while (i < payload.length) {
            val searchString = "cpu$idx: "
            i = payload.indexOf(searchString, i)
            if (i == -1) {
                break
            }
            i += searchString.length
            val j = payload.indexOf("%", i)
            cpuUsageList.add(payload.substring(i, j).toFloat())
            idx++
        }

        return CPUUsageEntry(dltMessage.timeStampSec, cpuUsageList)
    }
}