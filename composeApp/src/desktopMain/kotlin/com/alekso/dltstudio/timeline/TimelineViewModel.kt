package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.cpu.CPUAnalyzer
import com.alekso.dltstudio.cpu.CPUSEntry
import com.alekso.dltstudio.cpu.CPUUsageEntry
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.user.UserAnalyzer
import com.alekso.dltstudio.user.UserStateEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit
) {
    private var analyzeJob: Job? = null

    var userEntries = mutableStateListOf<TimelineEntries>()
    var highlightedKeys = mutableStateListOf<String?>()

    private var _analyzeState: MutableStateFlow<AnalyzeState> = MutableStateFlow(AnalyzeState.IDLE)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState


    val timelineFilters = mutableStateListOf<TimelineFilter>(
        TimelineFilter(
            name = "CPUC",
            enabled = true,
            extractPattern = "(cpu0):\\s*(\\d+[.\\d+]*)%.*(cpu1):\\s*(\\d+[.\\d+]*)%.*(cpu2):\\s*(\\d+[.\\d+]*)%.*(cpu3):\\s*(\\d+[.\\d+]*)%.*(cpu4):\\s*(\\d+[.\\d+]*)%.*(cpu5):\\s*(\\d+[.\\d+]*)%.*(cpu6):\\s*(\\d+[.\\d+]*)%.*(cpu7):\\s*(\\d+[.\\d+]*)%.*",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUC", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage
        ),
        TimelineFilter(
            name = "CPUS",
            enabled = true,
            extractPattern = "(cpu):(\\d+[.\\d+]*)%.*(us):\\s(\\d+[.\\d+]*)%.*(sy):\\s(\\d+[.\\d+]*)%.*(io):\\s*(\\d+[.\\d+]*).*(irq):\\s(\\d+[.\\d+]*)%.*(softirq):\\s(\\d+[.\\d+]*)%.*(ni):\\s(\\d+[.\\d+]*)%.*(st):\\s(\\d+[.\\d+]*)%.*(g):\\s(\\d+[.\\d+]*)%.*(gn):\\s(\\d+[.\\d+]*)%.*(avgcpu):\\s*(\\d+[.\\d+]*)%.*(thread):\\s*(\\d+[.\\d+]*)%.*(kernelthread):\\s*(\\d+[.\\d+]*)%",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUS", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage
        ),
        TimelineFilter(
            name = "CPUP",
            enabled = true,
            extractPattern = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\(",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUP", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage
        ),
        TimelineFilter(
            name = "GPU Load",
            enabled = true,
            extractPattern = "(GPU Load:)\\s+(?<value>\\d+.\\d+)%(?<key>)", // we use empty 'key' group to ignore key
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("GPU", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage
        ),
    )

    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    val totalSeconds: Int
        get() = if (timeEnd > 0 && timeStart > 0) ((timeEnd - timeStart) / 1000000).toInt() else 0


    fun analyzeTimeline(dltMessages: List<DLTMessage>) {
        when (_analyzeState.value) {
            AnalyzeState.IDLE -> startAnalyzing(dltMessages)
            AnalyzeState.ANALYZING -> stopAnalyzing()
        }

    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = AnalyzeState.IDLE
    }

    private fun cleanup() {
        timeStart = Long.MAX_VALUE
        timeEnd = Long.MIN_VALUE
        userEntries.clear()
        highlightedKeys.clear()
    }

    private fun startAnalyzing(dltMessages: List<DLTMessage>) {
        cleanup()
        _analyzeState.value = AnalyzeState.ANALYZING
        analyzeJob = CoroutineScope(Dispatchers.IO).launch {
            if (dltMessages.isNotEmpty()) {
                val _userEntries = mutableStateListOf<TimelineEntries>()

                println("Start Timeline building .. ${dltMessages.size} messages")

                timelineFilters.forEachIndexed { index, timelineFilter ->
                    val filteredEntries = when (timelineFilter.diagramType) {
                        TimelineFilter.DiagramType.MinMaxValue -> TimelineMinMaxEntries()
                        TimelineFilter.DiagramType.Percentage -> TimelinePercentageEntries()
                        else -> TimelinePercentageEntries()
                    }
                    _userEntries.add(filteredEntries)
                    highlightedKeys.add(index, null)
                }

                dltMessages.forEachIndexed { index, message ->
                    yield()
                    // timeStamps
                    val ts = message.timeStampNano
                    if (ts > timeEnd) {
                        timeEnd = ts
                    }
                    if (ts < timeStart) {
                        timeStart = ts
                    }

//                    analyzeCPUC(message, _cpuUsage, index)
//                    analyzeCPUS(message, _cpus, index)
//                    analyzeUserState(message, index, _userState)

                    timelineFilters.forEachIndexed { i, timelineFilter ->
                        analyzeEntriesRegex(
                            message,
                            timelineFilter,
                            _userEntries[i]
                        )
                    }
//                    analyzeEntriesIndexOf(
//                        message,
//                        appId = "MON",
//                        contextId = "MEMT",
//                        valueDelimiters = Pair("MaxRSS(MB): ", " increase:"),
//                        keyDelimiters = Pair("", "(cpid:"),
//                        entries = _userEntries["MEMT"]!!,
//                    )
                    onProgressChanged(index.toFloat() / dltMessages.size)
                }

                withContext(Dispatchers.Default) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    userEntries.clear()
                    userEntries.addAll(_userEntries)
                    _analyzeState.value = AnalyzeState.IDLE
                }
            }
        }
    }


    private fun analyzeEntriesRegex(
        message: DLTMessage,
        filter: TimelineFilter,
        entries: TimelineEntries
    ) {
        if (message.payload !is VerbosePayload) return
        if (filter.extractPattern == null) return

        val payload = (message.payload as VerbosePayload).asText()
        val regex = filter.extractPattern.toRegex()

        try {
            if (TimelineFilter.assessFilter(filter, message)) {
                val matches = regex.find(payload)!!
                try {
                    val key: String? = matches.groups["key"]?.value ?: "key"
                    val value: String? = matches.groups["value"]?.value
                    if (key != null && value != null) {
                        entries.addEntry(TimelineEntry(message.timeStampNano, key, value))
                        return
                    }
                } catch (e: Exception) {
                    if (matches.groups.size > 2) { // TODO: find better way to support multi-groups
                        for (i in 1..<matches.groups.size step 2) {
                            val key = matches.groups[i]?.value
                            val value = matches.groups[i + 1]?.value
                            if (key != null && value != null) {
                                entries.addEntry(TimelineEntry(message.timeStampNano, key, value))
                            }
                        }
                    }
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
                    entries.addEntry(TimelineEntry(message.timeStampNano, key, value))
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

    fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter) {
        if (index < 0 || index > timelineFilters.size) {
            timelineFilters.add(filter)
        } else timelineFilters[index] = filter
    }

    fun onTimelineFilterDelete(index: Int) {
        timelineFilters.removeAt(index)
    }

    fun onTimelineFilterMove(index: Int, offset: Int) {
        if (index + offset in 0..<timelineFilters.size) {
            val temp = timelineFilters[index]
            timelineFilters[index] = timelineFilters[index + offset]
            timelineFilters[index + offset] = temp

            val temp2 = userEntries[index]
            userEntries[index] = userEntries[index + offset]
            userEntries[index + offset] = temp2


        }
    }

}