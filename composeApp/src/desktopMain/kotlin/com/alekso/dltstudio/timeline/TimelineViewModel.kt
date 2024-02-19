package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.cpu.CPUAnalyzer
import com.alekso.dltstudio.cpu.CPUSEntry
import com.alekso.dltstudio.cpu.CPUUsageEntry
import com.alekso.dltstudio.user.UserAnalyzer
import com.alekso.dltstudio.user.UserStateEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit
) {
    private var analyzeJob: Job? = null

    var cpuUsage = mutableListOf<CPUUsageEntry>()
    var cpus = mutableListOf<CPUSEntry>()
    var userStateEntries = mutableMapOf<Int, MutableList<UserStateEntry>>()
    var userEntries = mutableMapOf<String, TimelineEntries>()
    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    val totalSeconds: Int
        get() = if (timeEnd > 0 && timeStart > 0) (timeEnd - timeStart).toInt() / 1000 else 0


    fun analyzeTimeline(dltMessages: List<DLTMessage>) {
        analyzeJob?.cancel()
        analyzeJob = CoroutineScope(Dispatchers.IO).launch {
            if (dltMessages.isNotEmpty()) {
                // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
                val _cpus = mutableStateListOf<CPUSEntry>()
                val _userState = mutableMapOf<Int, MutableList<UserStateEntry>>()
                val _userEntries = mutableMapOf<String, TimelineEntries>()


                println("Start Timeline building .. ${dltMessages.size} messages")

                _userEntries["CPU_PER_PID"] = TimelinePercentageEntries()
                _userEntries["MEMT"] = TimelineMinMaxEntries()

                dltMessages.forEachIndexed { index, message ->
                    // timeStamps
                    val ts = message.getTimeStamp()
                    if (ts > timeEnd) {
                        timeEnd = ts
                    }
                    if (ts < timeStart) {
                        timeStart = ts
                    }

                    analyzeCPUC(message, _cpuUsage, index)
                    analyzeCPUS(message, _cpus, index)
                    analyzeUserState(message, index, _userState)
                    val pattern = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\(".toRegex()
                    analyzeEntriesRegex(
                        message,
                        appId = "MON",
                        contextId = "CPUP",
                        regex = pattern,
                        entries = _userEntries["CPU_PER_PID"]!!
                    )

                    analyzeEntriesIndexOf(
                        message,
                        appId = "MON",
                        contextId = "MEMT",
                        valueDelimiters = Pair("MaxRSS(MB): ", " increase:"),
                        keyDelimiters = Pair("", "(cpid:"),
                        entries = _userEntries["MEMT"]!!,
                    )
                    onProgressChanged(index.toFloat() / dltMessages.size)
                }

                withContext(Dispatchers.Default) {
                    cpuUsage.clear()
                    cpuUsage.addAll(_cpuUsage)
                    cpus.clear()
                    cpus.addAll(_cpus)
                    userStateEntries = _userState
                    userEntries = _userEntries
                }
            }
        }
    }


    private fun analyzeEntriesRegex(
        message: DLTMessage,
        appId: String? = null,
        contextId: String? = null,
        regex: Regex,
        entries: TimelineEntries
    ) {
        if (message.payload !is VerbosePayload) return
        val payload = (message.payload as VerbosePayload).asText()

        try {
            if (message.extendedHeader?.applicationId == appId && message.extendedHeader?.contextId == contextId) {
                val matches = regex.find(payload)!!
                val key: String? = matches.groups["key"]?.value
                val value: String? = matches.groups["value"]?.value

                if (key != null && value != null) {
                    entries.addEntry(TimelineEntry(message.getTimeStamp(), key, value))
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun analyzeEntriesIndexOf(
        message: DLTMessage,
        appId: String? = null,
        contextId: String? = null,
        keyDelimiters: Pair<String, String>,
        valueDelimiters: Pair<String, String>,
        entries: TimelineEntries
    ) {
        if (message.payload !is VerbosePayload) return
        val payload = (message.payload as VerbosePayload).asText()

        try {
            if (message.extendedHeader?.applicationId == appId && message.extendedHeader?.contextId == contextId) {
                val key: String? = payload.substring(
                    payload.indexOf(keyDelimiters.first) + keyDelimiters.first.length,
                    payload.indexOf(keyDelimiters.second)
                )
                val value: String? = payload.substring(
                    payload.indexOf(valueDelimiters.first) + valueDelimiters.first.length,
                    payload.indexOf(valueDelimiters.second)
                )

                if (key != null && value != null) {
                    entries.addEntry(TimelineEntry(message.getTimeStamp(), key, value))
                }
            }
        } catch (e: Exception) {
            // ignore
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
}