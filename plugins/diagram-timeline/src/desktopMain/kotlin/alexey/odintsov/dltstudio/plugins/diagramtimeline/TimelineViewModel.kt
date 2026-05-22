package alexey.odintsov.dltstudio.plugins.diagramtimeline

import alexey.odintsov.charts.model.ChartData
import alexey.odintsov.charts.model.ChartEntry
import alexey.odintsov.charts.model.ChartKey
import alexey.odintsov.charts.model.DurationChartData
import alexey.odintsov.charts.model.EventsChartData
import alexey.odintsov.charts.model.MinMaxChartData
import alexey.odintsov.charts.model.PercentageChartData
import alexey.odintsov.charts.model.SingleStateChartData
import alexey.odintsov.charts.model.StateChartData
import alexey.odintsov.charts.model.TimeFrame
import alexey.odintsov.dltstudio.extraction.forEachWithProgress
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.diagramtimeline.db.RecentTimelineFilterFileEntry
import alexey.odintsov.dltstudio.plugins.diagramtimeline.db.TimelineRepository
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.AnalyzeState
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.TimeLineFilterManager
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.TimelineFilter
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.TimelineFiltersDialogCallbacks
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.predefinedTimelineFilters
import alexey.odintsov.dltstudio.uicomponents.dialogs.DialogOperation
import alexey.odintsov.dltstudio.uicomponents.dialogs.FileDialogState
import alexey.odintsov.logger.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import java.io.File


class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit,
    private val timelineRepository: TimelineRepository,
    private val messagesRepository: MessagesRepository,
) {
    @OptIn(ExperimentalSplitPaneApi::class)
    val vSplitterState = SplitPaneState(0.9f, true)
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)
    private var analyzeJob: Job? = null

    val messages = messagesRepository.getMessages()

    private var _timeFrame = MutableStateFlow(TimeFrame(0L, 1L))
    var timeFrame = _timeFrame.asStateFlow()
    private var _timeTotal = MutableStateFlow(TimeFrame(0L, 1L))
    var timeTotal = _timeTotal.asStateFlow()


    private val _filtersDialogState = MutableStateFlow(false)
    val filtersDialogState = _filtersDialogState.asStateFlow()
    private var _entriesMap = MutableStateFlow<Map<String, ChartData<LogMessage>>>(emptyMap())
    val entriesMap = _entriesMap.asStateFlow()
    var highlightedKeysMap = mutableStateMapOf<String, ChartKey?>()
    private var _selectedEntry = MutableStateFlow<ChartEntry<LogMessage>?>(null)
    var selectedEntry = _selectedEntry.asStateFlow()
    private var _hoveredEntry = MutableStateFlow<ChartEntry<LogMessage>?>(null)
    var hoveredEntry = _hoveredEntry.asStateFlow()

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
        _filtersDialogState.value = false
    }

    val toolbarCallbacks = object : ToolbarCallbacks {
        override fun onAnalyzeClicked() = startAnalyzing()

        override fun onTimelineFiltersClicked() {
            _filtersDialogState.value = true
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
            currentFilterFile?.let { fileEntry ->
                saveTimeLineFilters(File(fileEntry.path))
            }
        }

        override fun onSaveFilterAsClicked() {
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
            _timeFrame.value = _timeFrame.value.move(-100000)
        }

        override fun onRightClicked() {
            _timeFrame.value = _timeFrame.value.move(100000)
        }

        override fun onZoomInClicked() {
            _timeFrame.value = _timeFrame.value.zoom(true)
        }

        override fun onZoomOutClicked() {
            _timeFrame.value = _timeFrame.value.zoom(false)
        }

        override fun onZoomFitClicked() {
            _timeFrame.value = TimeFrame(_timeTotal.value.timeStart, _timeTotal.value.timeEnd)
        }

        override fun onDragTimeline(dx: Float) {
            _timeFrame.value = _timeFrame.value.move(dx.toLong())
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
            AnalyzeState.IDLE -> startAnalyzing(messages.value)
            AnalyzeState.ANALYZING -> stopAnalyzing()
        }
    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = AnalyzeState.IDLE
    }

    fun cleanup() {
        _entriesMap.value = emptyMap()
        highlightedKeysMap.clear()
    }

    private fun startAnalyzing(dltMessages: List<LogMessage>) {
        cleanup()
        _analyzeState.value = AnalyzeState.ANALYZING
        analyzeJob = viewModelScope.launch(Dispatchers.Default) {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val entries = mutableStateMapOf<String, ChartData<LogMessage>>()
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
                                message,
                                timelineFilter.diagramType,
                                timelineFilter.extractorType,
                                regexps[i]!!,
                                entries[timelineFilter.key]!!
                            )
                        }
                    }
                }

                _timeFrame.value = TimeFrame(timeStart, timeEnd)
                _timeTotal.value = TimeFrame(timeStart, timeEnd)
                _entriesMap.value = entries
                withContext(Main) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
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

    fun retrieveEntriesForFilter(filter: TimelineFilter): ChartData<LogMessage>? {
        return when (filter.diagramType) {
            DiagramType.Percentage -> _entriesMap.value[filter.key] as? PercentageChartData
            DiagramType.MinMaxValue -> _entriesMap.value[filter.key] as? MinMaxChartData
            DiagramType.State -> _entriesMap.value[filter.key] as? StateChartData
            DiagramType.SingleState -> _entriesMap.value[filter.key] as? SingleStateChartData
            DiagramType.Duration -> _entriesMap.value[filter.key] as? DurationChartData
            DiagramType.Events -> _entriesMap.value[filter.key] as? EventsChartData
        }
    }

    var legendSize by mutableStateOf(250f)

    fun onLegendResized(diff: Float) {
        legendSize += diff
    }

    fun onEntrySelected(chartEntry: ChartEntry<LogMessage>) {
        _selectedEntry.value = chartEntry
    }

    fun onEntryHovered(chartEntry: ChartEntry<LogMessage>?) {
        _hoveredEntry.value = chartEntry
    }

}