package com.alekso.dltstudio.memory

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload


data class MemoryUsageEntry(
    val index: Int,
    val timestamp: Long,
    val name: String,
    val pid: Int,
    val cpid: Int,
    val status: String,
    val maxRSS: Int,
    val increaseKb: Int,
)


object MemoryAnalyzer {
    fun analyzeMemoryUsage(index: Int, dltMessage: DLTMessage): MemoryUsageEntry {
        val payload = (dltMessage.payload as VerbosePayload).asText()

        val name: String = payload.substring(0, payload.indexOf(" pid:"))
        val pid = payload.substring(payload.indexOf(" pid:") + 5, payload.indexOf("(cpid:")).toInt()
        val cpid =
            payload.substring(payload.indexOf("cpid:") + 5, payload.indexOf(" android/")).toInt()
        val status =
            payload.substring(payload.indexOf(" android/") + 9, payload.indexOf(") MaxRSS(MB):"))
        val maxRSS =
            payload.substring(payload.indexOf("MaxRSS(MB):") + 12, payload.indexOf(" increase: "))
                .toInt()
        val increaseKb =
            payload.substring(payload.indexOf(" increase: ") + 11, payload.indexOf(" KB")).toInt()

        return MemoryUsageEntry(
            index, dltMessage.getTimeStamp(),
            name = name,
            pid = pid,
            cpid = cpid,
            status = status,
            maxRSS = maxRSS,
            increaseKb = increaseKb
        )
    }
}