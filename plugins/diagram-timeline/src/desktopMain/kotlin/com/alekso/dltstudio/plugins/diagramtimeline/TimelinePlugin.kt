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

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }

class TimelinePlugin : DLTStudioPlugin, PluginPanel, FormatterConsumer {
    private lateinit var messagesRepository: MessagesRepository
    private lateinit var viewModel: TimelineViewModel
    private lateinit var formatter: Formatter

    override fun getPanelName(): String = "Timeline"
    override fun pluginName(): String = "Diagram timeline"
    override fun pluginDirectoryName(): String = "diagram-timeline"
    override fun pluginVersion(): String = "0.9.0"
    override fun pluginClassName(): String = TimelinePlugin::class.simpleName.toString()

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String
    ) {
        this.messagesRepository = messagesRepository
        viewModel = TimelineViewModel(onProgressUpdate)
    }

    override fun initFormatter(formatter: Formatter) {
        this.formatter = formatter
    }

    override fun onLogsChanged() {
        // viewModel.clearState() //todo: Clear state
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        println("Recompose TimelinePlugin.renderPanel")
        val analyzeState by viewModel.analyzeState.collectAsState()

        CompositionLocalProvider(LocalFormatter provides formatter) {
            TimeLinePanel(
                modifier = modifier,
                logMessages = messagesRepository.getMessages(),
                offsetSec = viewModel.offset.value,
                scale = viewModel.scale.value,
                offsetUpdate = viewModel.offsetUpdateCallback,
                scaleUpdate = viewModel.scaleUpdateCallback,
                analyzeState = analyzeState,
                totalSeconds = viewModel.totalSeconds.toFloat(),
                timelineFilters = viewModel.timelineFilters,
                timeStart = viewModel.timeStart,
                timeEnd = viewModel.timeEnd,
                entriesMap = viewModel.entriesMap,
                highlightedKeysMap = viewModel.highlightedKeysMap,
                legendSize = viewModel.legendSize,
                onAnalyzeClicked = viewModel::onAnalyzeClicked,
                filtersDialogCallbacks = viewModel.timelineFiltersDialogCallbacks,
                retrieveEntriesForFilter = viewModel::retrieveEntriesForFilter,
                onLegendResized = viewModel::onLegendResized,
            )
        }
    }
}