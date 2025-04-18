package com.alekso.dltstudio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.db.preferences.PreferencesRepository
import com.alekso.dltstudio.db.preferences.RecentColorFilterFileEntry
import com.alekso.dltstudio.db.preferences.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.db.settings.SettingsRepositoryImpl
import com.alekso.dltstudio.logs.LogsPlugin
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.model.toSettingsLogs
import com.alekso.dltstudio.model.toSettingsLogsEntity
import com.alekso.dltstudio.model.toSettingsUI
import com.alekso.dltstudio.model.toSettingsUIEntity
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.MessagesHolder
import com.alekso.dltstudio.plugins.TimelineHolder
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.plugins.manager.PluginManager
import com.alekso.dltstudio.plugins.predefinedplugins.predefinedPlugins
import com.alekso.dltstudio.settings.SettingsDialogCallbacks
import com.alekso.dltstudio.timeline.TimelinePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
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
    private val preferencesRepository: PreferencesRepository,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    val settingsCallbacks: SettingsDialogCallbacks = object : SettingsDialogCallbacks {
        override fun onSettingsUIUpdate(settings: SettingsUI) {
            CoroutineScope(IO).launch {
                settingsRepository.updateSettingsUI(settings.toSettingsUIEntity())
            }
        }

        override fun onSettingsLogsUpdate(settings: SettingsLogs) {
            CoroutineScope(IO).launch {
                settingsRepository.updateSettingsLogs(settings.toSettingsLogsEntity())
            }
        }
    }
    val panels = mutableStateListOf<PluginPanel>()
    val panelsNames = mutableStateListOf<String>() // todo: Find way to synchronize panels and names


    var settingsDialogState by mutableStateOf(false)

    val settingsUI: StateFlow<SettingsUI> =
        settingsRepository.getSettingsUIFlow().mapNotNull { it?.toSettingsUI() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SettingsUI.Default
        )

    val settingsLogs: StateFlow<SettingsLogs> =
        settingsRepository.getSettingsLogsFlow().mapNotNull { it?.toSettingsLogs() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SettingsLogs.Default
        )

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

    private val _recentColorFiltersFiles = mutableStateListOf<RecentColorFilterFileEntry>()
    val recentColorFiltersFiles: SnapshotStateList<RecentColorFilterFileEntry>
        get() = _recentColorFiltersFiles

    private val _recentTimelineFiltersFiles = mutableStateListOf<RecentTimelineFilterFileEntry>()
    val recentTimelineFiltersFiles: SnapshotStateList<RecentTimelineFilterFileEntry>
        get() = _recentTimelineFiltersFiles


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

        viewModelScope.launch {
            preferencesRepository.getRecentColorFilters().collectLatest {
                _recentColorFiltersFiles.clear()
                _recentColorFiltersFiles.addAll(it)
            }
        }

        viewModelScope.launch {
            preferencesRepository.getRecentTimelineFilters().collectLatest {
                _recentTimelineFiltersFiles.clear()
                _recentTimelineFiltersFiles.addAll(it)
            }
        }

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
                dltParser.read(
                    dltFiles,
                    settingsLogs.value.backendType,
                    DependencyManager.onProgressUpdate,
                    true,
                ).map { LogMessage(it) })
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