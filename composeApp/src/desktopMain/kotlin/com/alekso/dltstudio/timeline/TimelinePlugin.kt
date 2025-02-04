package com.alekso.dltstudio.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.PluginPanel

class TimelinePlugin(
    private val viewModel: TimelineViewModel,
    private val logMessages: SnapshotStateList<LogMessage>,
) : PluginPanel {
    override fun getPanelName(): String = "Timeline"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        println("Recompose TimelinePlugin.renderPanel")
        val analyzeState by viewModel.analyzeState.collectAsState()

        TimeLinePanel(
            modifier = modifier,
            logMessages = logMessages,
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
            onAnalyzeClicked = viewModel::onAnalyzeClicked,
            onTimelineFilterUpdate = viewModel::onTimelineFilterUpdate,
            onTimelineFilterDelete = viewModel::onTimelineFilterDelete,
            onTimelineFilterMove = viewModel::onTimelineFilterMove,
            retrieveEntriesForFilter = viewModel::retrieveEntriesForFilter,
        )
    }
}