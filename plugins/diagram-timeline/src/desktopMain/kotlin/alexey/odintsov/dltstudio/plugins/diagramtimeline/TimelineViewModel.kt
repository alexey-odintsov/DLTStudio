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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import java.io.File


class TimelineViewModel(
    private val onProgressChanged: (Float) -> Unit,
    private val timelineRepository: TimelineRepository,
    messagesRepository: MessagesRepository,
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
    private var _highlightedKeysMap = MutableStateFlow<Map<String, ChartKey?>>(emptyMap())
    var highlightedKeysMap = _highlightedKeysMap.asStateFlow()
    private var _selectedEntry = MutableStateFlow<ChartEntry<LogMessage>?>(null)
    var selectedEntry = _selectedEntry.asStateFlow()
    private var _hoveredEntry = MutableStateFlow<ChartEntry<LogMessage>?>(null)
    var hoveredEntry = _hoveredEntry.asStateFlow()

    private var _analyzeState = MutableStateFlow(AnalyzeState.IDLE)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState
    val listState = LazyListState(0, 0)

    private val _timelineFilters = MutableStateFlow(predefinedTimelineFilters)
    val timelineFilters = _timelineFilters.asStateFlow()

    private var _recentTimelineFiltersFiles = MutableStateFlow<List<RecentTimelineFilterFileEntry>>(emptyList())
    var recentTimelineFiltersFiles = _recentTimelineFiltersFiles.asStateFlow()
    private var _currentFilterFile = MutableStateFlow<RecentTimelineFilterFileEntry?>(null)
    var currentFilterFile = _currentFilterFile.asStateFlow()

    private var _fileDialogState = MutableStateFlow(
        FileDialogState(
            title = "Save filter",
            operation = DialogOperation.SAVE,
            fileCallback = { saveTimeLineFilters(it[0]) },
            cancelCallback = ::closeFileDialog
        )
    )
    val fileDialogState = _fileDialogState.asStateFlow()

    fun onCloseFiltersDialogClicked() {
        _filtersDialogState.value = false
    }

    fun handleToolbarAction(action: ToolbarAction) {
        when (action) {
            ToolbarAction.AnalyzeClicked -> startAnalyzing()
            ToolbarAction.ClearFilterClicked -> clearTimeLineFilters()
            is ToolbarAction.DragTimeline -> dragTimeline(action.dx)
            ToolbarAction.LeftClicked -> move(-100000)
            ToolbarAction.LoadFilterClicked -> loadFilterClicked()
            is ToolbarAction.RecentFilterClicked -> loadTimeLineFilters(File(action.path))
            ToolbarAction.RightClicked -> move(100000)
            ToolbarAction.SaveFilterAsClicked -> saveFilterAsClicked()
            ToolbarAction.SaveFilterClicked -> saveFilterClicked()
            ToolbarAction.TimelineFiltersClicked -> timelineFiltersClicked()
            ToolbarAction.ZoomFitClicked -> zoomFit()
            ToolbarAction.ZoomInClicked -> zoom(true)
            ToolbarAction.ZoomOutClicked -> zoom(false)
        }
    }

    private fun loadFilterClicked() {
        _fileDialogState.value = FileDialogState(
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

    private fun timelineFiltersClicked() {
        _filtersDialogState.value = true
    }

    private fun saveFilterAsClicked() {
        _fileDialogState.value = FileDialogState(
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

    private fun saveFilterClicked() {
        _currentFilterFile.value?.let { fileEntry ->
            saveTimeLineFilters(File(fileEntry.path))
        }
    }

    private fun move(dx: Long) {
        _timeFrame.value = _timeFrame.value.move(dx)
    }

    private fun dragTimeline(dx: Float) {
        _timeFrame.value = _timeFrame.value.move(dx.toLong())
    }

    private fun zoom(zoomIn: Boolean) {
        _timeFrame.value = _timeFrame.value.zoom(zoomIn)
    }

    private fun zoomFit() {
        _timeFrame.value = TimeFrame(_timeTotal.value.timeStart, _timeTotal.value.timeEnd)
    }

    init {
        viewModelScope.launch {
            timelineRepository.getRecentTimelineFilters().collectLatest {
                _recentTimelineFiltersFiles.value = it
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
        _highlightedKeysMap.value = emptyMap()
    }

    private fun startAnalyzing(dltMessages: List<LogMessage>) {
        cleanup()
        _analyzeState.value = AnalyzeState.ANALYZING
        analyzeJob = viewModelScope.launch(Dispatchers.Default) {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val entries = mutableMapOf<String, ChartData<LogMessage>>()
                var timeStart = Long.MAX_VALUE
                var timeEnd = Long.MIN_VALUE

                Log.d("Start Timeline building .. ${dltMessages.size} messages")

                val regexps = mutableListOf<Regex?>()
                // prefill timeline data holders
                _timelineFilters.value.forEachIndexed { index, timelineFilter ->
                    entries[timelineFilter.key] = timelineFilter.diagramType.createEntries()
                    _highlightedKeysMap.update {
                        it.toMutableMap().apply {
                            set(timelineFilter.key, null)
                        }
                    }

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

                    _timelineFilters.value.forEachIndexed { i, timelineFilter ->
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
                _analyzeState.value = AnalyzeState.IDLE
            }
            Log.d("Done analyzing timeline ${System.currentTimeMillis() - start}ms")
        }
    }

    fun updateHighlightedKey(filterKey: String, key: ChartKey?) {
        _highlightedKeysMap.update {
            it.toMutableMap().apply {
                set(filterKey, key)
            }
        }
    }

    val timelineFiltersDialogCallbacks = object : TimelineFiltersDialogCallbacks {
        override fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter) {
            _timelineFilters.update {
                it.toMutableList().apply {
                    if (index < 0 || index > it.size) {
                        add(filter)
                    } else set(index, filter)
                }
            }
        }

        override fun onTimelineFilterDelete(index: Int) {
            _timelineFilters.update {
                it.toMutableList().apply { removeAt(index) }
            }
        }

        override fun onTimelineFilterMove(index: Int, offset: Int) {
            _timelineFilters.update {
                it.toMutableList().apply {
                    if (index + offset in 0..<it.size) {
                        val temp = it[index]
                        set(index, it[index + offset])
                        set(index + offset, temp)
                    }
                }
            }
        }
    }


    private fun closeFileDialog() {
        _fileDialogState.value = _fileDialogState.value.copy(visible = false)
    }

    private fun saveTimeLineFilters(file: File) {
        viewModelScope.launch {
            TimeLineFilterManager().saveToFile(_timelineFilters.value, file)
            timelineRepository.addNewRecentTimelineFilter(
                RecentTimelineFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
    }


    private fun loadTimeLineFilters(file: File) {
        _timelineFilters.value = emptyList()
        viewModelScope.launch {
            TimeLineFilterManager().loadFromFile(file)?.let { list ->
                _timelineFilters.update {
                    it.toMutableList().apply {
                        addAll((list))
                    }
                }
            }
            val fileEntry = RecentTimelineFilterFileEntry(file.name, file.absolutePath)
            timelineRepository.addNewRecentTimelineFilter(fileEntry)
            _currentFilterFile.value = fileEntry
        }
    }

    private fun clearTimeLineFilters() {
        _currentFilterFile.value = null
        _timelineFilters.value = emptyList()
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

    private var _legendSize = MutableStateFlow(250f)
    val legendSize = _legendSize.asStateFlow()

    fun onLegendResized(diff: Float) {
        _legendSize.value += diff
    }

    fun onEntrySelected(chartEntry: ChartEntry<LogMessage>) {
        _selectedEntry.value = chartEntry
    }

    fun onEntryHovered(chartEntry: ChartEntry<LogMessage>?) {
        _hoveredEntry.value = chartEntry
    }

}