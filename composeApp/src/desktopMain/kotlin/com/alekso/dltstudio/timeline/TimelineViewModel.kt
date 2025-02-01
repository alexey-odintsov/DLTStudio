package com.alekso.dltstudio.timeline

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.TimeLineFilterManager
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
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
): TimelineHolder {
    private var analyzeJob: Job? = null

    var offset = mutableStateOf(0f)
    var scale = mutableStateOf(1f)
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset.value = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale.value = if (newScale > 0f) newScale else 1f }


    var entriesMap = mutableStateMapOf<String, TimeLineEntries<*>>()
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
            diagramType = DiagramType.State,
            extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries,
            testClause = "User 10 state changed from LOCKED to UNLOCKED"
        ),
        TimelineFilter(
            name = "Crashes",
            enabled = true,
            extractPattern = "Crash \\((?<value>.*)\\) detected.*Process:\\s(?<key>.*). Exception: (?<info>.*) Crash ID:",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("RMAN", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CRSH", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.Events,
            extractorType = EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            testClause = "Crash (ANR) detected Process: myapp. Exception: NPE Crash ID:123"
        ),
        TimelineFilter(
            name = "CPUC",
            enabled = true,
            extractPattern = "(cpu0):\\s*(\\d+[.\\d+]*)%.*(cpu1):\\s*(\\d+[.\\d+]*)%.*(cpu2):\\s*(\\d+[.\\d+]*)%.*(cpu3):\\s*(\\d+[.\\d+]*)%.*(cpu4):\\s*(\\d+[.\\d+]*)%.*(cpu5):\\s*(\\d+[.\\d+]*)%.*(cpu6):\\s*(\\d+[.\\d+]*)%.*(cpu7):\\s*(\\d+[.\\d+]*)%.*",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUC", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.Percentage,
            extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries,
            testClause = "cpu0: 10% cpu1: 45% cpu2: 23% cpu3: 2% cpu4: 23% cpu5: 78% cpu6: 1% cpu7: 12%"
        ),
        TimelineFilter(
            name = "CPUS",
            enabled = false,
            extractPattern = "(cpu):(\\d+[.\\d+]*)%.*(us):\\s(\\d+[.\\d+]*)%.*(sy):\\s(\\d+[.\\d+]*)%.*(io):\\s*(\\d+[.\\d+]*).*(irq):\\s(\\d+[.\\d+]*)%.*(softirq):\\s(\\d+[.\\d+]*)%.*(ni):\\s(\\d+[.\\d+]*)%.*(st):\\s(\\d+[.\\d+]*)%.*(g):\\s(\\d+[.\\d+]*)%.*(gn):\\s(\\d+[.\\d+]*)%.*(avgcpu):\\s*(\\d+[.\\d+]*)%.*(thread):\\s*(\\d+[.\\d+]*)%.*(kernelthread):\\s*(\\d+[.\\d+]*)%",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUS", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.Percentage,
            extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries
        ),
        TimelineFilter(
            name = "CPUP",
            enabled = false,
            extractPattern = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\(",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("CPUP", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.Percentage,
            extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries
        ),
        TimelineFilter(
            name = "MEMT",
            enabled = false,
            extractPattern = "(.*)\\(cpid.*MaxRSS\\(MB\\):\\s(\\d+).*increase",
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("MEMT", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.MinMaxValue,
            extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries
        ),
        TimelineFilter(
            name = "GPU Load",
            enabled = false,
            extractPattern = "(GPU Load:)\\s+(?<value>\\d+.\\d+)%(?<key>)", // we use empty 'key' group to ignore key
            filters = mapOf(
                FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("GPU", TextCriteria.PlainText),
            ),
            diagramType = DiagramType.Percentage,
            extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries
        ),
    )

    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    val totalSeconds: Int
        get() = if (timeEnd > 0 && timeStart > 0) ((timeEnd - timeStart) / 1000000).toInt() else 0


    fun onAnalyzeClicked(logMessages: SnapshotStateList<LogMessage>) {
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
        entriesMap.clear()
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

                var prevTs = System.currentTimeMillis()
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
                        if (timelineFilter.enabled && regexps[i] != null && TimelineFilter.assessFilter(
                                timelineFilter,
                                message.dltMessage
                            )
                        ) {
                            EntriesExtractor.analyzeEntriesRegex(
                                message.dltMessage,
                                timelineFilter.diagramType,
                                timelineFilter.extractorType,
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
                    entriesMap.clear()
                    entriesMap.putAll(_userEntries)
                    timeStart = _timeStart
                    timeEnd = _timeEnd
                    _analyzeState.value = AnalyzeState.IDLE
                }
                onProgressChanged(1f)
            }
            Log.d("Done analyzing timeline ${System.currentTimeMillis() - start}ms")
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

    override fun saveTimeLineFilters(file: File) {
        TimeLineFilterManager().saveToFile(timelineFilters, file)
        Preferences.addRecentTimelineFilter(file.name, file.absolutePath)
    }

    override fun loadTimeLineFilters(file: File) {
        timelineFilters.clear()
        TimeLineFilterManager().loadFromFile(file)?.let {
            timelineFilters.addAll(it)
        }
        Preferences.addRecentTimelineFilter(file.name, file.absolutePath)
    }

    override fun clearTimeLineFilters() {
        timelineFilters.clear()
    }

    fun retrieveEntriesForFilter(filter: TimelineFilter): TimeLineEntries<*>? {
        return when (filter.diagramType) {
            DiagramType.Percentage -> entriesMap[filter.key] as? TimeLinePercentageEntries
            DiagramType.MinMaxValue -> entriesMap[filter.key] as? TimeLineMinMaxEntries
            DiagramType.State -> entriesMap[filter.key] as? TimeLineStateEntries
            DiagramType.SingleState -> entriesMap[filter.key] as? TimeLineSingleStateEntries
            DiagramType.Duration -> entriesMap[filter.key] as? TimeLineDurationEntries
            DiagramType.Events -> entriesMap[filter.key] as? TimeLineEventEntries
        }
    }
}