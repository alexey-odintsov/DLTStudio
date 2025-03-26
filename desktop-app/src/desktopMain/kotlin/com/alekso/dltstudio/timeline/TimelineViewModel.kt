package com.alekso.dltstudio.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.db.preferences.PreferencesRepositoryImpl
import com.alekso.dltstudio.db.preferences.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.timeline.filters.TimeLineFilterManager
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.timeline.filters.TimelineFiltersDialogCallbacks
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.filters.predefinedTimelineFilters
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File

private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit,
    private val preferencesRepository: PreferencesRepositoryImpl,
) : TimelineHolder {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    private var analyzeJob: Job? = null

    var offset = mutableStateOf(0f)
    var scale = mutableStateOf(1f)
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset.value = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale.value = if (newScale > 0f) newScale else 1f }


    var entriesMap = mutableStateMapOf<String, TimeLineEntries<*>>()
    var highlightedKeysMap = mutableStateMapOf<String, String?>()

    private var _analyzeState = MutableStateFlow<AnalyzeState>(AnalyzeState.IDLE)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState

    val timelineFilters =
        mutableStateListOf<TimelineFilter>(*predefinedTimelineFilters.toTypedArray())

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
                    val ts = message.dltMessage.timeStampUs
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

    val timelineFiltersDialogCallbacks = object : TimelineFiltersDialogCallbacks {
        override fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter) {
            if (index < 0 || index > timelineFilters.size) {
                timelineFilters.add(filter)
            } else timelineFilters[index] = filter
        }

        override fun onTimelineFilterDelete(index: Int) {
            timelineFilters.removeAt(index)
        }

        override fun onTimelineFilterMove(index: Int, offset: Int) {
            if (index + offset in 0..<timelineFilters.size) {
                val temp = timelineFilters[index]
                timelineFilters[index] = timelineFilters[index + offset]
                timelineFilters[index + offset] = temp
            }
        }
    }

    override fun saveTimeLineFilters(file: File) {
        viewModelScope.launch {
            TimeLineFilterManager().saveToFile(timelineFilters, file)
            preferencesRepository.addNewRecentTimelineFilter(
                RecentTimelineFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
    }

    override fun loadTimeLineFilters(file: File) {
        timelineFilters.clear()
        viewModelScope.launch {
            TimeLineFilterManager().loadFromFile(file)?.let {
                timelineFilters.addAll(it)
            }
            preferencesRepository.addNewRecentTimelineFilter(
                RecentTimelineFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
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

    var legendSize by mutableStateOf(250f)

    fun onLegendResized(diff: Float) {
        legendSize += diff
    }
}