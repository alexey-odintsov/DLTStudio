package com.alekso.dltstudio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.db.preferences.PreferencesRepository
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
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.settings.SettingsDialogCallbacks
import com.alekso.dltstudio.timeline.TimelinePlugin
import com.alekso.dltstudio.uicomponents.dialogs.DialogOperation
import com.alekso.dltstudio.uicomponents.dialogs.FileDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    var fileDialogState by mutableStateOf(
        FileDialogState(
            title = "Open DLT file(s)",
            isMultiSelectionEnabled = true,
            operation = DialogOperation.OPEN,
            fileCallback = { onOpenDLTFiles(it) },
            cancelCallback = ::closeFileDialog
        )
    )

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    private fun closeFileDialog() {
        fileDialogState = fileDialogState.copy(visible = false)
    }

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
    val menuItems = mutableStateListOf<MainMenuItem>(
        MainMenuItem(
            "File",
            children = mutableStateListOf(
                ChildMenuItem("Open") {
                    fileDialogState = FileDialogState(
                        visible = true,
                        title = "Open DLT file(s)",
                        isMultiSelectionEnabled = true,
                        operation = DialogOperation.OPEN,
                        fileCallback = { onOpenDLTFiles(it) },
                        cancelCallback = ::closeFileDialog,
                    )
                },
                AppChildMenuSeparator(),
                ChildMenuItem("Settings") {
                    settingsDialogState = true
                },
            )
        ),
        MainMenuItem(
            "Color filters",
            children = mutableStateListOf(
//                Preferences.recentColorFilters().forEach {
//                    ChildMenuItem("Open") {
//                        callbacks.onLoadColorFiltersFile(File(it.absolutePath))
//                    })
//                }
//                if (Preferences.recentColorFilters().isNotEmpty()) {
//                    AppChildMenuSeparator()
//                }
                ChildMenuItem("Open") {
                    fileDialogState = FileDialogState(
                        visible = true,
                        title = "Open Color filter file",
                        isMultiSelectionEnabled = false,
                        operation = DialogOperation.OPEN,
                        fileCallback = { loadColorFilters(it[0]) },
                        cancelCallback = ::closeFileDialog,
                    )
                },
                ChildMenuItem("Save") {
                    fileDialogState = FileDialogState(
                        visible = true,
                        title = "Save Color filter file",
                        isMultiSelectionEnabled = false,
                        operation = DialogOperation.SAVE,
                        fileCallback = { saveColorFilters(it[0]) },
                        cancelCallback = ::closeFileDialog,
                    )
                },
                ChildMenuItem("Clear") {
                    clearColorFilters()
                },
            )
        ),
        MainMenuItem(
            "Timeline filters",
            children = mutableStateListOf(
                ChildMenuItem("Open") {
                    fileDialogState = FileDialogState(
                        visible = true,
                        title = "Open Timeline filter file",
                        isMultiSelectionEnabled = false,
                        operation = DialogOperation.OPEN,
                        fileCallback = { loadTimeLineFilters(it[0]) },
                        cancelCallback = ::closeFileDialog,
                    )
                },
                ChildMenuItem("Save") {
                    fileDialogState = FileDialogState(
                        visible = true,
                        title = "Save Timeline filter file",
                        isMultiSelectionEnabled = false,
                        operation = DialogOperation.SAVE,
                        fileCallback = { saveTimeLineFilters(it[0]) },
                        cancelCallback = ::closeFileDialog,
                    )
                },
                ChildMenuItem("Clear") {
                    clearTimeLineFilters()
                },
            )
        ),
    )

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

    fun onOpenDLTFiles(files: List<File>) {
        fileDialogState = fileDialogState.copy(visible = false)
        parseFile(files)
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
                dltParser.read(
                    dltFiles,
                    settingsLogs.value.backendType,
                    DependencyManager.onProgressUpdate
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