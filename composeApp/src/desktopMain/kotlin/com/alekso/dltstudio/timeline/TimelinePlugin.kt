package com.alekso.dltstudio.com.alekso.dltstudio.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.PluginPanel
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.timeline.TimeLinePanel
import com.alekso.dltstudio.timeline.TimelineViewModel

class TimelinePlugin(
    private val viewModel: TimelineViewModel,
    private val logMessages: SnapshotStateList<LogMessage>,
) : PluginPanel {
    override fun getPanelName(): String = "Timeline"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        TimeLinePanel(
            modifier = modifier,
            timelineViewModel = viewModel,
            logMessages = logMessages,
            offsetSec = viewModel.offset.value,
            scale = viewModel.scale.value,
            offsetUpdate = viewModel.offsetUpdateCallback,
            scaleUpdate = viewModel.scaleUpdateCallback,
        )
    }
}