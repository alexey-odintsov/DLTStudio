package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.extraction.forEachWithProgress
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
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
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit,
    private val timelineRepository: TimelineRepository,
    private val messagesRepository: MessagesRepository,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)
    private var analyzeJob: Job? = null

    var timeFrame by mutableStateOf(TimeFrame(0L, 1L))
    var timeTotal by mutableStateOf(TimeFrame(0L, 1L))


    val filtersDialogState = mutableStateOf(false)
    var entriesMap = mutableStateMapOf<String, ChartData>()
    var highlightedKeysMap = mutableStateMapOf<String, ChartKey?>()
    var selectedEntry by mutableStateOf<ChartEntry?>(null)

    private var _analyzeState = MutableStateFlow(AnalyzeState.IDLE)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState
    val listState = LazyListState(0, 0)

    val timelineFilters = mutableStateListOf(*predefinedTimelineFilters.toTypedArray())

    var recentTimelineFiltersFiles = mutableStateListOf<RecentTimelineFilterFileEntry>()
    var currentFilterFile by mutableStateOf<RecentTimelineFilterFileEntry?>(null)

    var fileDialogState by mutableStateOf(
        FileDialogState(
            title = "Save filter",
            operation = DialogOperation.SAVE,
            fileCallback = { saveTimeLineFilters(it[0]) },
            cancelCallback = ::closeFileDialog
        )
    )

    fun onCloseFiltersDialogClicked() {
        filtersDialogState.value = false
    }

    val toolbarCallbacks = object : ToolbarCallbacks {
        override fun onAnalyzeClicked() = startAnalyzing()

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

        override fun onLeftClicked() {
            timeFrame = timeFrame.move(-100000)
        }

        override fun onRightClicked() {
            timeFrame = timeFrame.move(100000)
        }

        override fun onZoomInClicked() {
            timeFrame = timeFrame.zoom(true)
        }

        override fun onZoomOutClicked() {
            timeFrame = timeFrame.zoom(false)
        }

        override fun onZoomFitClicked() {
            timeFrame = TimeFrame(timeTotal.timeStart, timeTotal.timeEnd)
        }

        override fun onDragTimeline(dx: Float) {
            timeFrame = timeFrame.move(dx.toLong())
        }

    }


    init {
        viewModelScope.launch {
            timelineRepository.getRecentTimelineFilters().collectLatest {
                recentTimelineFiltersFiles.clear()
                recentTimelineFiltersFiles.addAll(it)
            }
        }
    }

    private fun startAnalyzing() {
        when (_analyzeState.value) {
            AnalyzeState.IDLE -> startAnalyzing(messagesRepository.getMessages())
            AnalyzeState.ANALYZING -> stopAnalyzing()
        }
    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = AnalyzeState.IDLE
    }

    fun cleanup() {
        entriesMap.clear()
        highlightedKeysMap.clear()
    }

    private fun startAnalyzing(dltMessages: SnapshotStateList<LogMessage>) {
        cleanup()
        _analyzeState.value = AnalyzeState.ANALYZING
        analyzeJob = viewModelScope.launch(Dispatchers.Default) {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val entries = mutableStateMapOf<String, ChartData>()
                var timeStart = Long.MAX_VALUE
                var timeEnd = Long.MIN_VALUE

                Log.d("Start Timeline building .. ${dltMessages.size} messages")

                val regexps = mutableListOf<Regex?>()
                // prefill timeline data holders
                timelineFilters.forEachIndexed { index, timelineFilter ->
                    entries[timelineFilter.key] = timelineFilter.diagramType.createEntries()
                    highlightedKeysMap[timelineFilter.key] = null

                    // precompile regex in advance
                    regexps.add(index, timelineFilter.extractPattern?.toRegex())
                }

                forEachWithProgress(dltMessages, onProgressChanged) { _, message ->
                    // timeStamps
                    val ts = message.dltMessage.timeStampUs
                    if (ts > timeEnd) {
                        timeEnd = ts
                    }
                    if (ts < timeStart) {
                        timeStart = ts
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
                                entries[timelineFilter.key]!!
                            )
                        }
                    }
                }

                withContext(Main) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    entriesMap.clear()
                    entriesMap.putAll(entries)
                    timeFrame = TimeFrame(timeStart, timeEnd)
                    timeTotal = TimeFrame(timeStart, timeEnd)
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


    private fun loadTimeLineFilters(file: File) {
        timelineFilters.clear()
        viewModelScope.launch {
            TimeLineFilterManager().loadFromFile(file)?.let {
                timelineFilters.addAll(it)
            }
            val fileEntry = RecentTimelineFilterFileEntry(file.name, file.absolutePath)
            timelineRepository.addNewRecentTimelineFilter(fileEntry)
            currentFilterFile = fileEntry
        }
    }

    private fun clearTimeLineFilters() {
        currentFilterFile = null
        timelineFilters.clear()
    }

    fun retrieveEntriesForFilter(filter: TimelineFilter): ChartData? {
        return when (filter.diagramType) {
            DiagramType.Percentage -> entriesMap[filter.key] as? PercentageChartData
            DiagramType.MinMaxValue -> entriesMap[filter.key] as? MinMaxChartData
            DiagramType.State -> entriesMap[filter.key] as? StateChartData
            DiagramType.SingleState -> entriesMap[filter.key] as? SingleStateChartData
            DiagramType.Duration -> entriesMap[filter.key] as? DurationChartData
            DiagramType.Events -> entriesMap[filter.key] as? EventsChartData
        }
    }

    var legendSize by mutableStateOf(250f)

    fun onLegendResized(diff: Float) {
        legendSize += diff
    }

    fun onEntrySelected(chartKey: ChartKey, chartEntry: ChartEntry) {
        selectedEntry = chartEntry
    }

}