package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.cpu.CPUAnalyzer
import com.alekso.dltstudio.cpu.CPUSEntry
import com.alekso.dltstudio.cpu.CPUUsageEntry
import com.alekso.dltstudio.memory.MemoryAnalyzer
import com.alekso.dltstudio.memory.MemoryUsageEntry
import com.alekso.dltstudio.user.UserAnalyzer
import com.alekso.dltstudio.user.UserStateEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object TimelineAnalyzer {

    fun analyzeEntries(
        message: DLTMessage,
        appId: String? = null,
        contextId: String? = null,
        regex: Regex,
        map: MutableMap<String, MutableList<TimelineEntry>>
    ) {
        if (message.payload !is VerbosePayload) return
        val payload = (message.payload as VerbosePayload).asText()

        try {
            if (message.extendedHeader?.applicationId == appId && message.extendedHeader?.contextId == contextId) {
                val matches = regex.find(payload)!!
                val key: String? = matches.groups["key"]?.value
                val value: String? = matches.groups["value"]?.value

                if (key != null && value != null) {
                    val entry = TimelineEntry(message.getTimeStamp(), key, value)
                    if (!map.containsKey(key)) {
                        map[key] = mutableListOf()
                    }
                    (map[key] as MutableList).add(entry)
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    suspend fun analyzeTimeline(dltSession: ParseSession, progressCallback: (Float) -> Unit) {
        val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
        val _cpus = mutableStateListOf<CPUSEntry>()
        val _memt = mutableMapOf<String, MutableList<MemoryUsageEntry>>()
        val _userState = mutableMapOf<Int, MutableList<UserStateEntry>>()
        val _userEntries = mutableMapOf<String, MutableList<TimelineEntry>>()

        withContext(Dispatchers.IO) {

            println("Start Timeline building .. ${dltSession.dltMessages.size} messages")

            dltSession.dltMessages.forEachIndexed { index, message ->
                // timeStamps
                val ts = message.getTimeStamp()
                if (ts > dltSession.timeEnd) {
                    dltSession.timeEnd = ts
                }
                if (ts < dltSession.timeStart) {
                    dltSession.timeStart = ts
                }

                analyzeCPUC(message, _cpuUsage, index)
                analyzeCPUS(message, _cpus, index)
                analyzeMemory(message, index, _memt)
                analyzeUserState(message, index, _userState)
                progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
            }

            // todo: should be user defined and stored on user side
            // todo: try split approach - regexp is too slow
//            val patters = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\("
//            dltSession.dltMessages.forEachIndexed { index, message ->
//                analyzeEntries(
//                    message,
//                    appId = "MON",
//                    contextId = "CPUP",
//                    regex = patters.toRegex(),
//                    map = _userEntries
//                )
//                progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
//
//            }
        }
        withContext(Dispatchers.Default) {
            dltSession.cpuUsage.clear()
            dltSession.cpuUsage.addAll(_cpuUsage)
            dltSession.cpus.clear()
            dltSession.cpus.addAll(_cpus)
            dltSession.memt.clear()
            dltSession.memt = _memt
            dltSession.userStateEntries = _userState
            dltSession.userEntries = _userEntries
        }
    }

    private fun analyzeCPUC(
        message: DLTMessage,
        _cpuUsage: SnapshotStateList<CPUUsageEntry>,
        index: Int
    ) {
        if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                "MON"
            ) == true &&
            message.extendedHeader?.contextId == "CPUC"
        ) {
            try {
                _cpuUsage.add(CPUAnalyzer.analyzeCPUUsage(index, message))
            } catch (e: Exception) {
                // skip
            }
        }
    }

    private fun analyzeCPUS(
        message: DLTMessage,
        _cpus: SnapshotStateList<CPUSEntry>,
        index: Int
    ) {
        if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                "MON"
            ) == true &&
            message.extendedHeader?.contextId == "CPUS"
        ) {
            try {
                _cpus.add(CPUAnalyzer.analyzeCPUS(index, message))
            } catch (e: Exception) {
                // skip
            }
        }
    }

    private fun analyzeUserState(
        message: DLTMessage,
        index: Int,
        _userState: MutableMap<Int, MutableList<UserStateEntry>>
    ) {
        if (message.ecuId == "MGUA"
            && message.extendedHeader?.applicationId?.startsWith("ALD") == true
            && message.extendedHeader?.contextId == "SYST"
            && message.payload?.asText()?.contains("state changed from") == true
        ) {
            try {
                val userState =
                    UserAnalyzer.analyzeUserStateChanges(index, message)
                if (!_userState.containsKey(userState.uid)) {
                    _userState[userState.uid] = mutableListOf()
                }
                (_userState[userState.uid] as MutableList).add(userState)
            } catch (e: Exception) {
                // skip
            }
        }
    }

    private fun analyzeMemory(
        message: DLTMessage,
        index: Int,
        _memt: MutableMap<String, MutableList<MemoryUsageEntry>>
    ) {
        if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                "MON"
            ) == true &&
            message.extendedHeader?.contextId == "MEMT"
        ) {
            try {
                val memt = MemoryAnalyzer.analyzeMemoryUsage(index, message)
                if (!_memt.containsKey(memt.name)) {
                    _memt[memt.name] = mutableListOf()
                }
                (_memt[memt.name] as MutableList).add(memt)
            } catch (e: Exception) {
                // skip
            }
        }
    }
}