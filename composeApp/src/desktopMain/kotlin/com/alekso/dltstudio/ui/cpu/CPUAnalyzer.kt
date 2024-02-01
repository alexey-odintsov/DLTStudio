package com.alekso.dltstudio.ui.cpu

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload


data class CPUUsageEntry(
    val index: Int,
    val timestamp: Long,
    val cpuUsage: List<Float>,
)

enum class CPUS_ENTRY {
    CPU, USER, SYSTEM, IO, IRQ, SOFT_IRQ, NI, ST, G, GN, AVG_CPU, THREAD, KERNEL_THREAD
}

data class CPUSEntry(
    val index: Int, val timestamp: Long, val entry: List<Float>
)

object CPUAnalyzer {
    fun analyzeCPUUsage(index: Int, dltMessage: DLTMessage): CPUUsageEntry {
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

        return CPUUsageEntry(index, dltMessage.getTimeStamp(), cpuUsageList)
    }

    fun analyzeCPUS(index: Int, dltMessage: DLTMessage): CPUSEntry {
        val payload = (dltMessage.payload as VerbosePayload).asText()
        val entries = mutableListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        entries[CPUS_ENTRY.CPU.ordinal] =
            payload.substring(payload.indexOf("cpu:") + 4, payload.indexOf("% us:")).toFloat()
        entries[CPUS_ENTRY.USER.ordinal] =
            payload.substring(payload.indexOf(" us: ") + 5, payload.indexOf("% sy:")).toFloat()
        entries[CPUS_ENTRY.SYSTEM.ordinal] =
            payload.substring(payload.indexOf(" sy: ") + 5, payload.indexOf("% io:")).toFloat()
        entries[CPUS_ENTRY.IO.ordinal] =
            payload.substring(payload.indexOf(" io: ") + 5, payload.indexOf("% irq:")).toFloat()
        entries[CPUS_ENTRY.IRQ.ordinal] =
            payload.substring(payload.indexOf(" irq: ") + 6, payload.indexOf("% softirq:"))
                .toFloat()
        entries[CPUS_ENTRY.SOFT_IRQ.ordinal] =
            payload.substring(payload.indexOf(" softirq: ") + 10, payload.indexOf("% ni:"))
                .toFloat()
        entries[CPUS_ENTRY.NI.ordinal] =
            payload.substring(payload.indexOf(" ni: ") + 5, payload.indexOf("% st:")).toFloat()
        entries[CPUS_ENTRY.ST.ordinal] =
            payload.substring(payload.indexOf(" st: ") + 5, payload.indexOf("% g:")).toFloat()
        entries[CPUS_ENTRY.G.ordinal] =
            payload.substring(payload.indexOf(" g: ") + 4, payload.indexOf("% gn:")).toFloat()
        entries[CPUS_ENTRY.GN.ordinal] =
            payload.substring(payload.indexOf(" gn: ") + 5, payload.indexOf("% avgcpu:")).toFloat()
        entries[CPUS_ENTRY.AVG_CPU.ordinal] =
            payload.substring(payload.indexOf(" avgcpu:") + 8, payload.indexOf("% thread:"))
                .toFloat()
        entries[CPUS_ENTRY.THREAD.ordinal] =
            payload.substring(payload.indexOf(" thread: ") + 9, payload.indexOf("% kernelthread:"))
                .toFloat()
        entries[CPUS_ENTRY.KERNEL_THREAD.ordinal] =
            payload.substring(payload.indexOf(" kernelthread: ") + 15, payload.lastIndexOf("%"))
                .toFloat()

        return CPUSEntry(index, dltMessage.getTimeStamp(), entries)
    }
}