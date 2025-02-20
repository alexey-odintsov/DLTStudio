package com.alekso.dltstudio

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.com.alekso.dltstudio.settings.SettingsDialogCallbacks
import com.alekso.dltstudio.db.settings.SettingsRepositoryImpl
import com.alekso.dltstudio.logs.LogsPlugin
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.model.toSettingsLogs
import com.alekso.dltstudio.model.toSettingsUI
import com.alekso.dltstudio.model.toSettingsUIEntity
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.MessagesHolder
import com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.plugins.manager.PluginManager
import com.alekso.dltstudio.plugins.predefinedplugins.predefinedPlugins
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
    private val settingsRepository: SettingsRepositoryImpl,
) {
    val settingsCallbacks: SettingsDialogCallbacks = object : SettingsDialogCallbacks {
        override fun onSettingsUIUpdate(settings: SettingsUI) {
            CoroutineScope(IO).launch {
                settingsRepository.updateSettingsUI(settings.toSettingsUIEntity())
            }
        }
    }
    val panels = mutableStateListOf<PluginPanel>()
    val panelsNames = mutableStateListOf<String>() // todo: Find way to synchronize panels and names


    var settingsDialogState by mutableStateOf(false)
    private var _settingsUI = mutableStateOf(SettingsUI.Default)
    val settingsUI: State<SettingsUI> = _settingsUI

    private var _settingsLogs = mutableStateOf(SettingsLogs.Default)
    val settingsLogs: State<SettingsLogs> = _settingsLogs

    private fun observeSettings() {
        println("observeSettingsUI")
        CoroutineScope(IO).launch {
            settingsRepository.getSettingsUIFlow().collect {
                println("onUpdateUISettings($it)")

                withContext(Main) {
                    _settingsUI.value = it.toSettingsUI()
                }
            }
            settingsRepository.getSettingsLogsFlow().collect {
                println("onUpdateLogsSettings($it)")

                withContext(Main) {
                    _settingsLogs.value = it.toSettingsLogs()
                }
            }
        }
    }


    init {
        observeSettings()
    }

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

        override fun onSettingsClicked() {
            settingsDialogState = true
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

    fun closeSettingsDialog() {
        settingsDialogState = false
    }

}