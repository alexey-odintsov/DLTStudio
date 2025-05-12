package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.db.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.plugins.diagramtimeline.db.TimelineRepository
import com.alekso.dltstudio.plugins.diagramtimeline.filters.AnalyzeState
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimeLineFilterManager
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFilter
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFiltersDialogCallbacks
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.predefinedTimelineFilters
import com.alekso.dltstudio.uicomponents.dialogs.DialogOperation
import com.alekso.dltstudio.uicomponents.dialogs.FileDialogState
import com.alekso.dltstudio.uicomponents.forEachWithProgress
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

interface ToolbarCallbacks {
    fun onTimelineFiltersClicked()
    fun onLoadFilterClicked()
    fun onSaveFilterClicked()
    fun onClearFilterClicked()
    fun onRecentFilterClicked(path: String)

    object Stub : ToolbarCallbacks {
        override fun onTimelineFiltersClicked() = Unit
        override fun onLoadFilterClicked() = Unit
        override fun onSaveFilterClicked() = Unit
        override fun onClearFilterClicked() = Unit
        override fun onRecentFilterClicked(path: String) = Unit
    }
}

class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit,
    private val timelineRepository: TimelineRepository,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    private var analyzeJob: Job? = null

    var offset = mutableStateOf(0f)
    var scale = mutableStateOf(1f)
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset.value = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale.value = if (newScale > 0f) newScale else 1f }

    val filtersDialogState = mutableStateOf(false)

    fun onCloseFiltersDialogClicked() {
        filtersDialogState.value = false
    }

    val toolbarCallbacks = object : ToolbarCallbacks {
        override fun onTimelineFiltersClicked() {
            filtersDialogState.value = true
        }

        override fun onLoadFilterClicked() {
            fileDialogState = FileDialogState(
                title = "Load filter",
                visible = true,
                operation = DialogOperation.OPEN,
                fileCallback = {
                    closeFileDialog()
                    loadTimeLineFilters(it[0])
                },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onSaveFilterClicked() {
            fileDialogState = FileDialogState(
                title = "Save filter",
                visible = true,
                operation = DialogOperation.SAVE,
                fileCallback = {
                    closeFileDialog()
                    saveTimeLineFilters(it[0])
                },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onClearFilterClicked() {
            clearTimeLineFilters()
        }

        override fun onRecentFilterClicked(path: String) {
            loadTimeLineFilters(File(path))
        }

    }

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

    private val _recentTimelineFiltersFiles = mutableStateListOf<RecentTimelineFilterFileEntry>()
    val recentTimelineFiltersFiles: SnapshotStateList<RecentTimelineFilterFileEntry>
        get() = _recentTimelineFiltersFiles


    init {
        viewModelScope.launch {
            timelineRepository.getRecentTimelineFilters().collectLatest {
                _recentTimelineFiltersFiles.clear()
                _recentTimelineFiltersFiles.addAll(it)
            }
        }
    }

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
        analyzeJob = viewModelScope.launch(IO) {
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

                forEachWithProgress(dltMessages, onProgressChanged) { _, message ->
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
                }

                withContext(Main) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    entriesMap.clear()
                    entriesMap.putAll(_userEntries)
                    timeStart = _timeStart
                    timeEnd = _timeEnd
                    _analyzeState.value = AnalyzeState.IDLE
                }
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

    var fileDialogState by mutableStateOf(
        FileDialogState(
            title = "Save filter",
            operation = DialogOperation.SAVE,
            fileCallback = { saveTimeLineFilters(it[0]) },
            cancelCallback = ::closeFileDialog
        )
    )

    private fun closeFileDialog() {
        fileDialogState = fileDialogState.copy(visible = false)
    }

    private fun saveTimeLineFilters(file: File) {
        viewModelScope.launch {
            TimeLineFilterManager().saveToFile(timelineFilters, file)
            timelineRepository.addNewRecentTimelineFilter(
                RecentTimelineFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
    }

    private val _currentFilterFile = mutableStateOf<RecentTimelineFilterFileEntry?>(null)
    val currentFilterFile: State<RecentTimelineFilterFileEntry?> = _currentFilterFile

    private fun loadTimeLineFilters(file: File) {
        timelineFilters.clear()
        viewModelScope.launch {
            TimeLineFilterManager().loadFromFile(file)?.let {
                timelineFilters.addAll(it)
            }
            val fileEntry = RecentTimelineFilterFileEntry(file.name, file.absolutePath)
            timelineRepository.addNewRecentTimelineFilter(fileEntry)
            _currentFilterFile.value = fileEntry
        }
    }

    private fun clearTimeLineFilters() {
        _currentFilterFile.value = null
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