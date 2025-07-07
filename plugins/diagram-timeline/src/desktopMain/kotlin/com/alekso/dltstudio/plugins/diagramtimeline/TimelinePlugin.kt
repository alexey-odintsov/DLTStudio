package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.FormatterConsumer
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.plugins.diagramtimeline.db.DBFactory
import com.alekso.dltstudio.plugins.diagramtimeline.db.TimelineRepository
import com.alekso.dltstudio.plugins.diagramtimeline.db.TimelineRepositoryImpl
import com.alekso.dltstudio.uicomponents.dialogs.FileDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }

class TimelinePlugin : DLTStudioPlugin, PluginPanel, FormatterConsumer {
    private lateinit var messagesRepository: MessagesRepository
    private lateinit var timelineRepository: TimelineRepository
    private lateinit var viewModel: TimelineViewModel
    private lateinit var formatter: Formatter

    override fun getPanelName(): String = "Timeline"
    override fun pluginName(): String = "Diagram timeline"
    override fun pluginDirectoryName(): String = "diagram-timeline"
    override fun pluginVersion(): String = "0.9.0"
    override fun pluginClassName(): String = TimelinePlugin::class.simpleName.toString()
    override fun author(): String = "Alexey Odintsov"
    override fun pluginLink(): String? = null
    override fun description(): String = "Builds custom charts on timeline. All charts are fully customizable and can be exported/imported as files."

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String
    ) {
        timelineRepository = TimelineRepositoryImpl(
            database = DBFactory().createDatabase("${pluginFilesPath}/timeline.db"),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )

        this.messagesRepository = messagesRepository
        viewModel = TimelineViewModel(onProgressUpdate, timelineRepository, messagesRepository)
    }

    override fun initFormatter(formatter: Formatter) {
        this.formatter = formatter
    }

    override fun onLogsChanged() {
        viewModel.cleanup()
    }

    @OptIn(ExperimentalSplitPaneApi::class)
    @Composable
    override fun renderPanel(modifier: Modifier) {
        val analyzeState by viewModel.analyzeState.collectAsState()

        CompositionLocalProvider(LocalFormatter provides formatter) {
            if (viewModel.fileDialogState.visible) {
                FileDialog(viewModel.fileDialogState)
            }

            TimeLinePanel(
                modifier = modifier,
                timeTotal = viewModel.timeTotal,
                timeFrame = viewModel.timeFrame,
                listState = viewModel.listState,
                analyzeState = analyzeState,
                timelineFilters = viewModel.timelineFilters,
                entriesMap = viewModel.entriesMap,
                highlightedKeysMap = viewModel.highlightedKeysMap,
                legendSize = viewModel.legendSize,
                filtersDialogCallbacks = viewModel.timelineFiltersDialogCallbacks,
                retrieveEntriesForFilter = viewModel::retrieveEntriesForFilter,
                currentFilterFile = viewModel.currentFilterFile,
                onLegendResized = viewModel::onLegendResized,
                recentFiltersFiles = viewModel.recentTimelineFiltersFiles,
                toolbarCallbacks = viewModel.toolbarCallbacks,
                filtersDialogState = viewModel.filtersDialogState.value,
                onCloseFiltersDialog = viewModel::onCloseFiltersDialogClicked,
                selectedEntry = viewModel.selectedEntry,
                hoveredEntry = viewModel.hoveredEntry,
                onEntrySelected = viewModel::onEntrySelected,
                onEntryHovered = viewModel::onEntryHovered,
                vSplitterState = viewModel.vSplitterState,
            )
        }
    }
}