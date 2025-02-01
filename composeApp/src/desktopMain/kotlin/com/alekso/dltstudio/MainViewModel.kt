package com.alekso.dltstudio

import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.device.analyse.DeviceAnalyzePlugin
import com.alekso.dltstudio.device.analyse.DeviceAnalyzeViewModel
import com.alekso.dltstudio.files.FilesPlugin
import com.alekso.dltstudio.files.FilesViewModel
import com.alekso.dltstudio.logs.LogsPanelState
import com.alekso.dltstudio.logs.LogsPlugin
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.MessagesHolder
import com.alekso.dltstudio.plugins.PanelState
import com.alekso.dltstudio.plugins.PluginPanel
import com.alekso.dltstudio.timeline.TimelinePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class MainViewModel(
    private val dltParser: DLTParser,
    private val onProgressChanged: (Float) -> Unit,
    private val messagesHolder: MessagesHolder,
    private val timelineHolder: TimelineHolder, // We need it to pass Menu callbacks
) {

    val panels = mutableListOf<PluginPanel>()

    private var parseJob: Job? = null

    init {
        panels.add(
            LogsPlugin(
                viewModel = DependencyManager.getLogsViewModel(),
                state = LogsPanelState(false),
            )
        )
        panels.add(
            TimelinePlugin(
                viewModel = DependencyManager.getTimelineViewModel(),
                logMessages = messagesHolder.getMessages(),
                state = PanelState()
            )
        )
        panels.add(
            FilesPlugin(
                viewModel = FilesViewModel(onProgressChanged),
                logMessages = messagesHolder.getMessages(),
                state = PanelState()
            )
        )
        panels.add(
            DeviceAnalyzePlugin(
                DeviceAnalyzeViewModel(onProgressChanged), state = PanelState()
            )
        )
    }

    fun parseFile(dltFiles: List<File>) {
        parseJob?.cancel()
        messagesHolder.clearMessages()
        parseJob = CoroutineScope(IO).launch {
            messagesHolder.storeImages(
                dltParser.read(onProgressChanged, dltFiles).map { LogMessage(it) })
        }
    }

    fun loadColorFilters(file: File) {
        messagesHolder.loadColorFilters(file)
    }

    fun clearColorFilters() {
        messagesHolder.clearColorFilters()
    }

    fun saveColorFilters(file: File) {
        messagesHolder.saveColorFilters(file)
    }

    fun loadTimeLineFilters(file: File) {
        timelineHolder.loadTimeLineFilters(file)
    }

    fun clearTimeLineFilters() {
        timelineHolder.clearTimeLineFilters()
    }

    fun saveTimeLineFilters(file: File) {
        timelineHolder.saveTimeLineFilters(file)
    }

}