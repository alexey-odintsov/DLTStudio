package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.NonNamedEntriesExtractor
import com.alekso.dltstudio.timeline.filters.NamedEntriesExtractor
import com.alekso.dltstudio.timeline.filters.TimeLineFilterManager
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File

private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit
) {
    private var analyzeJob: Job? = null

    var userEntriesMap = mutableStateMapOf<String, TimeLineEntries<*>>()
    var highlightedKeysMap = mutableStateMapOf<String, String?>()

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


    fun analyzeTimeline(logMessages: SnapshotStateList<LogMessage>) {
        when (_analyzeState.value) {
            AnalyzeState.IDLE -> startAnalyzing(logMessages)
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
        userEntriesMap.clear()
        highlightedKeysMap.clear()
    }

    private fun startAnalyzing(dltMessages: SnapshotStateList<LogMessage>) {
        cleanup()
        _analyzeState.value = AnalyzeState.ANALYZING
        analyzeJob = CoroutineScope(Dispatchers.IO).launch {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val _userEntries = mutableStateMapOf<String, TimeLineEntries<*>>()
                var _timeStart = Long.MAX_VALUE
                var _timeEnd = Long.MIN_VALUE

                Log.d("Start Timeline building .. ${dltMessages.size} messages")

                val regexps = mutableListOf<Regex?>()
                // prefill timeline data holders
                timelineFilters.forEachIndexed { index, timelineFilter ->
                    _userEntries[timelineFilter.key] = timelineFilter.diagramType.createEntries()
                    highlightedKeysMap[timelineFilter.key] = null

                    // precompile regex in advance
                    regexps.add(index, timelineFilter.extractPattern?.toRegex())
                }

                var prevTs  = System.currentTimeMillis()
                dltMessages.forEachIndexed { index, message ->
                    yield()
                    // timeStamps
                    val ts = message.dltMessage.timeStampNano
                    if (ts > _timeEnd) {
                        _timeEnd = ts
                    }
                    if (ts < _timeStart) {
                        _timeStart = ts
                    }

                    timelineFilters.forEachIndexed { i, timelineFilter ->
                        if (timelineFilter.enabled && regexps[i] != null) {
                            analyzeEntriesRegex(
                                message.dltMessage,
                                timelineFilter,
                                regexps[i]!!,
                                _userEntries[timelineFilter.key]!!
                            )
                        }
                    }
                    val nowTs = System.currentTimeMillis()
                    if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                        prevTs = nowTs
                        onProgressChanged(index.toFloat() / dltMessages.size)
                    }
                }

                withContext(Dispatchers.Default) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    userEntriesMap.clear()
                    userEntriesMap.putAll(_userEntries)
                    timeStart = _timeStart
                    timeEnd = _timeEnd
                    _analyzeState.value = AnalyzeState.IDLE
                }
                onProgressChanged(1f)
            }
            Log.d("Done analyzing timeline ${System.currentTimeMillis() - start}ms")
        }
    }


    private fun analyzeEntriesRegex(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        if (filter.extractPattern == null) return
        val nonNamedExtractor = NonNamedEntriesExtractor()
        val namedExtractor = NamedEntriesExtractor()

        try {
            if (TimelineFilter.assessFilter(filter, message)) {
                when (filter.extractorType) {
                    TimelineFilter.ExtractorType.KeyValueNamed -> namedExtractor.extractEntry(
                        message,
                        filter,
                        regex,
                        entries
                    )

                    TimelineFilter.ExtractorType.KeyValueGroups -> nonNamedExtractor.extractEntry(
                        message,
                        filter,
                        regex,
                        entries
                    )
                }
            }
        } catch (e: Exception) {
            // ignore
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
        }
    }


    fun saveTimeLineFilters(file: File) {
        TimeLineFilterManager().saveToFile(timelineFilters, file)
        Preferences.addRecentTimelineFilter(file.name, file.absolutePath)
    }

    fun loadTimeLineFilters(file: File) {
        timelineFilters.clear()
        TimeLineFilterManager().loadFromFile(file)?.let {
            timelineFilters.addAll(it)
        }
        Preferences.addRecentTimelineFilter(file.name, file.absolutePath)
    }

    fun clearTimeLineFilters() {
        timelineFilters.clear()
    }

    fun retrieveEntriesForFilter(filter: TimelineFilter): TimeLineEntries<*>? {
        return when (filter.diagramType) {
            TimelineFilter.DiagramType.Percentage -> userEntriesMap[filter.key] as? TimeLinePercentageEntries
            TimelineFilter.DiagramType.MinMaxValue -> userEntriesMap[filter.key] as? TimeLineMinMaxEntries
            TimelineFilter.DiagramType.State -> userEntriesMap[filter.key] as? TimeLineStateEntries
            TimelineFilter.DiagramType.SingleState -> userEntriesMap[filter.key] as? TimeLineSingleStateEntries
            TimelineFilter.DiagramType.Duration -> userEntriesMap[filter.key] as? TimeLineDurationEntries
            TimelineFilter.DiagramType.Events -> userEntriesMap[filter.key] as? TimeLineEventEntries
        }
    }
}