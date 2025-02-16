package com.alekso.dltstudio

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.logs.LogsPlugin
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.MessagesHolder
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.plugins.PluginManager
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.plugins.predefinedPlugins
import com.alekso.dltstudio.timeline.TimelinePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainViewModel(
    private val dltParser: DLTParser,
    private val messagesProvider: MessagesProvider,
    private val messagesHolder: MessagesHolder,
    private val timelineHolder: TimelineHolder, // We need it to pass Menu callbacks
    private val pluginManager: PluginManager,
) {
    val panels = mutableStateListOf<PluginPanel>()
    val panelsNames = mutableStateListOf<String>() // todo: Find way to synchronize panels and names

    private var parseJob: Job? = null

    val mainMenuCallbacks = object : MainMenuCallbacks {
        override fun onOpenDLTFiles(files: List<File>) {
            parseFile(files)
        }

        override fun onLoadColorFiltersFile(file: File) {
            loadColorFilters(file)
        }

        override fun onSaveColorFiltersFile(file: File) {
            saveColorFilters(file)
        }

        override fun onLoadTimelineFiltersFile(file: File) {
            loadTimeLineFilters(file)
        }

        override fun onSaveTimelineFiltersFile(file: File) {
            saveTimeLineFilters(file)
        }

        override fun onClearColorFilters() {
            clearColorFilters()
        }

        override fun onClearTimelineFilters() {
            clearTimeLineFilters()
        }
    }

    init {
        panels.add(
            LogsPlugin(
                viewModel = DependencyManager.provideLogsViewModel(),
            )
        )
        panels.add(
            TimelinePlugin(
                viewModel = DependencyManager.provideTimelineViewModel(),
                logMessages = messagesProvider.getMessages(),
            )
        )

        panelsNames.addAll(panels.map { it.getPanelName() })
        CoroutineScope(IO).launch {
            val pluginManager = DependencyManager.providePluginsManager()

            predefinedPlugins.forEach { plugin ->
                pluginManager.registerPredefinedPlugin(plugin)
            }

            pluginManager.loadPlugins()
            val loadedPanel = pluginManager.getPluginPanels()
            withContext(Main) {
                panels.addAll(loadedPanel)
                panelsNames.addAll(loadedPanel.map { it.getPanelName() })
            }
        }
    }

    fun parseFile(dltFiles: List<File>) {
        pluginManager.notifyLogsChanged()
        parseJob?.cancel()
        messagesHolder.clearMessages()
        parseJob = CoroutineScope(IO).launch {
            messagesHolder.storeMessages(
                dltParser.read(DependencyManager.onProgressUpdate, dltFiles).map { LogMessage(it) })
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