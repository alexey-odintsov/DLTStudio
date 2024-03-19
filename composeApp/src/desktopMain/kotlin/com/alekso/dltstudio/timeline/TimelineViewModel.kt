package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.TimelineFilter
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

    var userEntries = mutableStateListOf<TimeLineEntries<*>>()
    var highlightedKeys = mutableStateListOf<String?>()

    private var _analyzeState: MutableStateFlow<AnalyzeState> = MutableStateFlow(AnalyzeState.IDLE)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState


    val timelineFilters = mutableStateListOf<TimelineFilter>(
        TimelineFilter(
            name = "User state",
            enabled = true,
            extractPattern = "User\\s(\\d+)\\sstate changed from (.*) to (.*)",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("SYST", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.State,
            extractorType = TimelineFilter.ExtractorType.KeyValueGroups
        ),
        TimelineFilter(
            name = "Crashes",
            enabled = true,
            extractPattern = "Crash \\((?<value>.*)\\) detected.*Process:\\s(?<key>.*). Exception: (?<info>.*) Crash ID:",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("RMAN", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CRSH", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Events,
            extractorType = TimelineFilter.ExtractorType.KeyValueNamed
        ),
        TimelineFilter(
            name = "CPUC",
            enabled = true,
            extractPattern = "(cpu0):\\s*(\\d+[.\\d+]*)%.*(cpu1):\\s*(\\d+[.\\d+]*)%.*(cpu2):\\s*(\\d+[.\\d+]*)%.*(cpu3):\\s*(\\d+[.\\d+]*)%.*(cpu4):\\s*(\\d+[.\\d+]*)%.*(cpu5):\\s*(\\d+[.\\d+]*)%.*(cpu6):\\s*(\\d+[.\\d+]*)%.*(cpu7):\\s*(\\d+[.\\d+]*)%.*",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUC", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage,
            extractorType = TimelineFilter.ExtractorType.KeyValueGroups
        ),
        TimelineFilter(
            name = "CPUS",
            enabled = false,
            extractPattern = "(cpu):(\\d+[.\\d+]*)%.*(us):\\s(\\d+[.\\d+]*)%.*(sy):\\s(\\d+[.\\d+]*)%.*(io):\\s*(\\d+[.\\d+]*).*(irq):\\s(\\d+[.\\d+]*)%.*(softirq):\\s(\\d+[.\\d+]*)%.*(ni):\\s(\\d+[.\\d+]*)%.*(st):\\s(\\d+[.\\d+]*)%.*(g):\\s(\\d+[.\\d+]*)%.*(gn):\\s(\\d+[.\\d+]*)%.*(avgcpu):\\s*(\\d+[.\\d+]*)%.*(thread):\\s*(\\d+[.\\d+]*)%.*(kernelthread):\\s*(\\d+[.\\d+]*)%",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUS", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage,
            extractorType = TimelineFilter.ExtractorType.KeyValueGroups
        ),
        TimelineFilter(
            name = "CPUP",
            enabled = false,
            extractPattern = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\(",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUP", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage,
            extractorType = TimelineFilter.ExtractorType.KeyValueNamed
        ),
        TimelineFilter(
            name = "MEMT",
            enabled = false,
            extractPattern = "(.*)\\(cpid.*MaxRSS\\(MB\\):\\s(\\d+).*increase",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("MEMT", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.MinMaxValue,
            extractorType = TimelineFilter.ExtractorType.KeyValueGroups
        ),
        TimelineFilter(
            name = "GPU Load",
            enabled = false,
            extractPattern = "(GPU Load:)\\s+(?<value>\\d+.\\d+)%(?<key>)", // we use empty 'key' group to ignore key
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("GPU", TextCriteria.PlainText),
            ),
            diagramType = TimelineFilter.DiagramType.Percentage,
            extractorType = TimelineFilter.ExtractorType.KeyValueNamed
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
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val _userEntries = mutableStateListOf<TimeLineEntries<*>>()
                var _timeStart = Long.MAX_VALUE
                var _timeEnd = Long.MIN_VALUE

                println("Start Timeline building .. ${dltMessages.size} messages")

                val regexps = mutableListOf<Regex?>()
                // prefill timeline data holders
                timelineFilters.forEachIndexed { index, timelineFilter ->
                    _userEntries.add(timelineFilter.diagramType.createEntries())
                    highlightedKeys.add(index, null)

                    // precompile regex in advance
                    regexps.add(index, timelineFilter.extractPattern?.toRegex())
                }

                dltMessages.forEachIndexed { index, message ->
                    yield()
                    // timeStamps
                    val ts = message.timeStampNano
                    if (ts > _timeEnd) {
                        _timeEnd = ts
                    }
                    if (ts < _timeStart) {
                        _timeStart = ts
                    }

                    timelineFilters.forEachIndexed { i, timelineFilter ->
                        if (timelineFilter.enabled && regexps[i] != null) {
                            analyzeEntriesRegex(
                                message,
                                timelineFilter,
                                regexps[i]!!,
                                _userEntries[i]
                            )
                        }
                    }
                    onProgressChanged(index.toFloat() / dltMessages.size)
                }

                withContext(Dispatchers.Default) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    userEntries.clear()
                    userEntries.addAll(_userEntries)
                    timeStart = _timeStart
                    timeEnd = _timeEnd
                    _analyzeState.value = AnalyzeState.IDLE
                }
            }
            println("Done analyzing timeline ${System.currentTimeMillis() - start}ms")
        }
    }


    private fun analyzeEntriesRegex(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        if (message.payload !is VerbosePayload) return
        if (filter.extractPattern == null) return

        try {
            if (TimelineFilter.assessFilter(filter, message)) {
                val payload = (message.payload as VerbosePayload).asText()
                filter.diagramType.extractEntry(regex, payload, entries, message, filter)
            }
        } catch (e: Exception) {
            // ignore
        }
    }


//    private fun analyzeEntriesIndexOf(
//        message: DLTMessage,
//        appId: String? = null,
//        contextId: String? = null,
//        keyDelimiters: Pair<String, String>,
//        valueDelimiters: Pair<String, String>,
//        entries: TimelineEntriesHolder
//    ) {
//        if (message.payload !is VerbosePayload) return
//        val payload = (message.payload as VerbosePayload).asText()
//
//        try {
//            if (message.extendedHeader?.applicationId == appId && message.extendedHeader?.contextId == contextId) {
//                val key: String? = payload.substring(
//                    payload.indexOf(keyDelimiters.first) + keyDelimiters.first.length,
//                    payload.indexOf(keyDelimiters.second)
//                )
//                val value: String? = payload.substring(
//                    payload.indexOf(valueDelimiters.first) + valueDelimiters.first.length,
//                    payload.indexOf(valueDelimiters.second)
//                )
//
//                if (key != null && value != null) {
//                    entries.addEntry(TimeLineSimpleEntry(message.timeStampNano, key, value))
//                }
//            }
//        } catch (e: Exception) {
//            // ignore
//        }
//    }


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