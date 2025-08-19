package com.alekso.dltstudio

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltmessage.DLTMessage
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.db.preferences.PreferencesRepository
import com.alekso.dltstudio.db.preferences.RecentColorFilterFileEntry
import com.alekso.dltstudio.db.preferences.SearchEntity
import com.alekso.dltstudio.db.settings.PluginStateEntity
import com.alekso.dltstudio.db.settings.SettingsRepositoryImpl
import com.alekso.dltstudio.logs.ColumnsContextMenuCallbacks
import com.alekso.dltstudio.logs.LogsPlugin
import com.alekso.dltstudio.logs.RemoveLogsDialogState
import com.alekso.dltstudio.logs.RowContextMenuCallbacks
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterManager
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialogCallbacks
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.logs.toolbar.LogsToolbarCallbacks
import com.alekso.dltstudio.logs.toolbar.LogsToolbarState
import com.alekso.dltstudio.model.Column
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.PluginState
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.model.SettingsPlugins
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.model.contract.filtering.FilterCriteria
import com.alekso.dltstudio.model.contract.filtering.FilterParameter
import com.alekso.dltstudio.model.contract.filtering.checkTextCriteria
import com.alekso.dltstudio.model.toPluginState
import com.alekso.dltstudio.model.toPluginStateEntity
import com.alekso.dltstudio.model.toSettingsLogs
import com.alekso.dltstudio.model.toSettingsLogsEntity
import com.alekso.dltstudio.model.toSettingsUI
import com.alekso.dltstudio.model.toSettingsUIEntity
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginLogPreview
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.dltstudio.plugins.manager.PluginManager
import com.alekso.dltstudio.plugins.predefinedplugins.predefinedPlugins
import com.alekso.dltstudio.settings.SettingsDialogCallbacks
import com.alekso.dltstudio.settings.SettingsPluginsCallbacks
import com.alekso.dltstudio.uicomponents.dialogs.DialogOperation
import com.alekso.dltstudio.uicomponents.dialogs.FileDialogState
import com.alekso.dltstudio.uicomponents.dialogs.FileTypeSelection
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import java.io.File
import kotlin.math.max

enum class LogRemoveContext {
    ApplicationId, ContextId, EcuId, SessionId, BeforeTimestamp, AfterTimestamp, Payload
}

data class LogSelection(
    /**
     * Logs list focus index
     */
    val logsIndex: Int,
    /**
     * Search list focus index
     */
    val searchIndex: Int,
)

class MainViewModel(
    private val dltParser: DLTParser,
    private val messagesRepository: MessagesRepository,
    private val pluginManager: PluginManager,
    private val settingsRepository: SettingsRepositoryImpl,
    private val preferencesRepository: PreferencesRepository,
    private val onProgressChanged: (Float) -> Unit,
    private val formatter: AppFormatter,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    val settingsCallbacks: SettingsDialogCallbacks = object : SettingsDialogCallbacks {
        override fun onSettingsUIUpdate(settings: SettingsUI) {
            viewModelScope.launch(IO) {
                settingsRepository.updateSettingsUI(settings.toSettingsUIEntity())
            }
        }

        override fun onSettingsLogsUpdate(settings: SettingsLogs) {
            viewModelScope.launch(IO) {
                settingsRepository.updateSettingsLogs(settings.toSettingsLogsEntity())
            }
        }

        override fun onOpenDefaultLogsFolderClicked() {
            fileDialogState = FileDialogState(
                operation = DialogOperation.OPEN,
                title = "Select default logs location",
                visible = true,
                directory = settingsLogs.value.defaultLogsFolder,
                fileTypeSelection = FileTypeSelection.DIRECTORIES_ONLY,
                fileCallback = { file ->
                    viewModelScope.launch(IO) {
                        settingsRepository.updateSettingsLogs(
                            settingsLogs.value.copy(
                                defaultLogsFolderPath = file.first().path
                            ).toSettingsLogsEntity()
                        )
                    }
                },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onOpenDefaultColorFiltersFolderClicked() {
            fileDialogState = FileDialogState(
                operation = DialogOperation.OPEN,
                title = "Select default color filters location",
                visible = true,
                directory = settingsLogs.value.defaultColorFiltersFolder,
                fileTypeSelection = FileTypeSelection.DIRECTORIES_ONLY,
                fileCallback = { file ->
                    viewModelScope.launch(IO) {
                        settingsRepository.updateSettingsLogs(
                            settingsLogs.value.copy(
                                defaultColorFiltersFolderPath = file.first().path
                            ).toSettingsLogsEntity()
                        )
                    }
                },
                cancelCallback = ::closeFileDialog
            )
        }
    }

    val settingsPluginsCallbacks: SettingsPluginsCallbacks = object : SettingsPluginsCallbacks {
        override fun onPluginClicked(plugin: DLTStudioPlugin) {
            selectedPlugin.value = plugin.pluginClassName()
        }

        override fun onUpdatePluginState(pluginState: PluginState) {
            viewModelScope.launch(IO) {
                settingsRepository.updatePluginState(pluginState.toPluginStateEntity())
            }
        }
    }

    val rowContextMenuCallbacks = object : RowContextMenuCallbacks {
        override fun onMarkClicked(i: Int, message: LogMessage) {
            messagesRepository.toggleMark(message.id)
        }

        override fun onRemoveClicked(
            context: LogRemoveContext, filter: String
        ) {
            removeMessages(context, filter)
        }

        override fun onRemoveDialogClicked(message: LogMessage) {
            removeLogsDialogState.value = RemoveLogsDialogState(true, message)
        }
    }

    private var currentFolder: File? = null
    var filesPath by mutableStateOf("")
    val panels = mutableStateListOf<PluginPanel>()
    val previewPanels = mutableStateListOf<PluginLogPreview>()

    private var _searchState = mutableStateOf<SearchState>(SearchState())
    val searchState: State<SearchState> = _searchState

    private var searchJob: Job? = null

    val logsListState = LazyListState()
    val searchListState = LazyListState()

    var logSelection by mutableStateOf(LogSelection(0, 0))
        private set


    private val _searchAutocomplete = mutableStateListOf<String>()
    val searchAutocomplete: SnapshotStateList<String>
        get() = _searchAutocomplete

    fun onSearchClicked(searchType: SearchType, searchText: String) {
        when (_searchState.value.state) {
            SearchState.State.IDLE -> startSearch(searchType, searchText)
            SearchState.State.SEARCHING -> stopSearch()
        }
    }

    @OptIn(ExperimentalSplitPaneApi::class)
    val vSplitterState = SplitPaneState(0.8f, true)

    @OptIn(ExperimentalSplitPaneApi::class)
    val hSplitterState = SplitPaneState(0.78f, true)
    val colorFiltersDialogState = mutableStateOf(false)
    var logsToolbarState by mutableStateOf(
        LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
            toolbarSearchWithMarkedChecked = false,
            toolbarWrapContentChecked = false,
            toolbarCommentsChecked = true,
        )
    )

    val logsToolbarCallbacks = object : LogsToolbarCallbacks {
        override fun onSearchButtonClicked(searchType: SearchType, text: String) {
            if (logsToolbarState.toolbarSearchWithMarkedChecked && searchType == SearchType.Text) {
                onSearchClicked(SearchType.TextAndMarkedRows, text)
            } else {
                onSearchClicked(searchType, text)
            }
        }

        override fun updateToolbarFatalCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarFatalCheck(logsToolbarState, checked)
        }

        override fun updateToolbarErrorCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarErrorCheck(logsToolbarState, checked)
        }

        override fun updateToolbarWarningCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarWarnCheck(logsToolbarState, checked)
        }

        override fun updateToolbarCommentsCheck(checked: Boolean) {
            logsToolbarState =
                LogsToolbarState.updateToolbarCommentsCheck(logsToolbarState, checked)
        }

        override fun updateToolbarSearchWithMarkedCheck(checked: Boolean) {
            logsToolbarState =
                LogsToolbarState.updateToolbarSearchWithMarkedCheck(logsToolbarState, checked)
        }

        override fun updateToolbarWrapContentCheck(checked: Boolean) {
            logsToolbarState =
                LogsToolbarState.updateToolbarWrapContentCheck(logsToolbarState, checked)
        }

        override fun onSearchUseRegexChanged(checked: Boolean) {
            _searchState.value = _searchState.value.copy(searchUseRegex = checked)
        }

        override fun onColorFiltersClicked() {
            colorFiltersDialogState.value = true
        }

        override fun onTimeZoneChanged(timeZoneName: String) {
            try {
                formatter.setTimeZone(TimeZone.of(timeZoneName))
            } catch (ignored: Exception) {
                // parsing will fail while typing timeZoneName
            }
        }
    }


    val previewPlugins = mutableStateListOf<PluginLogPreview>()
    private fun stopSearch() {
        searchJob?.cancel()
        _searchState.value = _searchState.value.copy(
            state = SearchState.State.IDLE
        )
    }

    val removeLogsDialogState = mutableStateOf(
        RemoveLogsDialogState(
            visible = false, message = null
        )
    )

    internal val columnParams = mutableStateListOf<ColumnParams>(
        *ColumnParams.DefaultParams.toTypedArray()
    )
    val columnsContextMenuCallbacks = object : ColumnsContextMenuCallbacks {
        override fun onToggleColumnVisibility(key: Column, checked: Boolean) {
            val index = columnParams.indexOfFirst { it.column == key }
            val updatedColumnParams = columnParams[index].copy(visible = checked)
            viewModelScope.launch(IO) {
                preferencesRepository.updateColumnParams(updatedColumnParams)
            }
        }

        override fun onResetParams() {
            viewModelScope.launch(IO) {
                preferencesRepository.resetColumnsParams()
                columnParams.clear()
                columnParams.addAll(ColumnParams.DefaultParams)
            }
        }
    }

    var fileDialogState by mutableStateOf(
        FileDialogState(
            title = "Save file",
            operation = DialogOperation.SAVE,
            fileCallback = { saveColorFilters(it[0]) },
            cancelCallback = ::closeFileDialog
        )
    )

    private fun closeFileDialog() {
        fileDialogState = fileDialogState.copy(visible = false)
    }

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

    private val pluginState: StateFlow<List<PluginStateEntity>> =
        settingsRepository.getPluginsStatesFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    var selectedPlugin: MutableStateFlow<String?> = MutableStateFlow(null)

    var settingsPlugins: Flow<SettingsPlugins> =
        pluginState.combineTransform(selectedPlugin) { list, pluginClassName ->
            emit(
                SettingsPlugins(
                    selectedPlugin = pluginClassName,
                    pluginsState = list.map { it.toPluginState() },
                )
            )
        }

    private var parseJob: Job? = null

    val mainMenuCallbacks = object : MainMenuCallbacks {
        override fun onClearColorFiltersClicked() {
            clearColorFilters()
        }

        override fun onSettingsClicked() {
            settingsDialogState = true
        }

        override fun onOpenFileClicked() {
            fileDialogState = FileDialogState(
                title = "Open DLT file(s)",
                visible = true,
                directory = currentFolder
                    ?: settingsLogs.value.defaultLogsFolder,
                isMultiSelectionEnabled = true,
                operation = DialogOperation.OPEN,
                fileCallback = { parseFile(it) },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onOpenFiltersClicked() {
            fileDialogState = FileDialogState(
                title = "Open filters",
                visible = true,
                directory = settingsLogs.value.defaultColorFiltersFolder,
                operation = DialogOperation.OPEN,
                fileCallback = { loadColorFilters(it[0]) },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onSaveColorFilterClicked() {
            fileDialogState = FileDialogState(
                title = "Save filters",
                visible = true,
                directory = settingsLogs.value.defaultColorFiltersFolder,
                operation = DialogOperation.SAVE,
                fileCallback = { saveColorFilters(it[0]) },
                cancelCallback = ::closeFileDialog
            )
        }

        override fun onRecentColorFilterClicked(file: File) {
            loadColorFilters(file)
        }
    }

    private val _recentColorFiltersFiles = mutableStateListOf<RecentColorFilterFileEntry>()
    val recentColorFiltersFiles: SnapshotStateList<RecentColorFilterFileEntry>
        get() = _recentColorFiltersFiles


    init {
        val logsPlugin = LogsPlugin(
            viewModel = this,
            messagesRepository = DependencyManager.provideMessageRepository(),
        )
        panels.add(logsPlugin)

        viewModelScope.launch {
            preferencesRepository.getRecentColorFilters().collectLatest {
                _recentColorFiltersFiles.clear()
                _recentColorFiltersFiles.addAll(it)
            }
        }

        viewModelScope.launch(IO) {
            val pluginManager = DependencyManager.providePluginsManager()

            predefinedPlugins.forEach { plugin ->
                pluginManager.registerPredefinedPlugin(plugin)
            }

            pluginManager.loadPlugins()
            val loadedPanels = pluginManager.getPluginPanels()
            withContext(Main) {
                panels.addAll(loadedPanels)
            }
            val loadedPreviewPanels = pluginManager.getPluginLogPreviews()
            withContext(Main) {
                previewPanels.addAll(loadedPreviewPanels)
            }
        }

        viewModelScope.launch(IO) {
            preferencesRepository.getRecentSearch().collectLatest {
                _searchAutocomplete.clear()
                _searchAutocomplete.addAll(it.map { it.value })
            }
        }

        viewModelScope.launch(IO) {
            preferencesRepository.getColumnParams().collectLatest { params ->
                params.forEach { param ->
                    val index = columnParams.indexOfFirst { it.column.name == param.key }
                    if (index >= 0) {
                        columnParams[index] = columnParams[index].copy(
                            visible = param.visible, size = param.size
                        )
                    }
                }
            }
        }

        viewModelScope.launch(Default) {
            val logPreviewPlugins = pluginManager.getPluginLogPreviews()
            previewPlugins.clear()
            previewPlugins.addAll(logPreviewPlugins)
        }
    }

    fun parseFile(dltFiles: List<File>) {
        viewModelScope.launch(Default) {
            pluginManager.notifyLogsChanged()
        }
        parseJob?.cancel()
        if (dltFiles.isEmpty()) return

        parseJob = viewModelScope.launch(IO) {
            filesPath = if (dltFiles.size < 2) {
                " – ${dltFiles[0].absolutePath}"
            } else {
                " – ${dltFiles[0].parentFile.absolutePath}/ ${dltFiles.size} file(s)"
            }
            currentFolder = dltFiles.first().parentFile
            while (currentFolder?.isDirectory == false) {
                currentFolder = currentFolder!!.parentFile
            }
            clearMessages()
            LogMessage.resetCounter()
            messagesRepository.storeMessages(
                dltParser.read(
                    dltFiles,
                    settingsLogs.value.backendType,
                    DependencyManager.onProgressUpdate
                ).map { LogMessage(it) })
        }
    }


    fun clearColorFilters() {
        colorFilters.clear()
    }

    fun saveColorFilters(file: File) {
        viewModelScope.launch {
            ColorFilterManager().saveToFile(colorFilters, file)
            preferencesRepository.addNewRecentColorFilter(
                RecentColorFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
    }

    fun closeSettingsDialog() {
        settingsDialogState = false
    }

    private suspend fun clearMessages() {
        withContext(Main) {
            messagesRepository.clearMessages()
        }
    }


    fun onColumnResized(columnKey: String, delta: Float) {
        val index = columnParams.indexOfFirst { it.column.name == columnKey }
        if (index >= 0) {
            val params = columnParams[index]
            val newSize = max(params.size + delta, ColumnParams.MIN_SIZE)
            columnParams[index] = params.copy(size = newSize)
        }
    }

    private fun assessFilter(
        filters: Map<FilterParameter, FilterCriteria>, message: DLTMessage
    ): Boolean {
        return filters.all {
            val criteria = it.value
            return@all when (it.key) {
                FilterParameter.MessageType -> {
                    checkTextCriteria(
                        criteria, message.extendedHeader?.messageInfo?.messageType?.name
                    )
                }

                FilterParameter.MessageTypeInfo -> {
                    checkTextCriteria(
                        criteria, message.extendedHeader?.messageInfo?.messageTypeInfo?.name
                    )
                }

                FilterParameter.EcuId -> {
                    checkTextCriteria(criteria, message.standardHeader.ecuId)
                }

                FilterParameter.ContextId -> {
                    checkTextCriteria(criteria, message.extendedHeader?.contextId)
                }

                FilterParameter.AppId -> {
                    checkTextCriteria(criteria, message.extendedHeader?.applicationId)
                }

                FilterParameter.SessionId -> {
                    criteria.value.isNotEmpty() && message.standardHeader.sessionId == criteria.value.toInt()
                }

                FilterParameter.Payload -> {
                    checkTextCriteria(criteria, message.payloadText())
                }
            }
        }
    }

    private fun removeMessages(type: LogRemoveContext, filter: String) {
        viewModelScope.launch(IO) {
            Log.d("start removing messages by $type '$filter'")
            val duration = messagesRepository.removeMessages(onProgressChanged) {
                shouldRemove(type, it.dltMessage, filter)
            }
            Log.d("done removing messages by $type '$filter' $duration ms")
        }
    }

    private fun shouldRemove(
        type: LogRemoveContext,
        message: DLTMessage,
        filter: String
    ) = when (type) {
        LogRemoveContext.ContextId -> message.extendedHeader?.contextId == filter
        LogRemoveContext.ApplicationId -> message.extendedHeader?.applicationId == filter
        LogRemoveContext.EcuId -> message.standardHeader.ecuId == filter
        LogRemoveContext.SessionId -> message.standardHeader.sessionId.toString() == filter
        LogRemoveContext.BeforeTimestamp -> message.timeStampUs < filter.toLong()
        LogRemoveContext.AfterTimestamp -> message.timeStampUs > filter.toLong()
        LogRemoveContext.Payload -> false
    }

    fun removeMessagesByFilters(filters: Map<FilterParameter, FilterCriteria>) {
        viewModelScope.launch(IO) {
            Log.d("start removing messages by filter '$filters'")
            val duration = messagesRepository.removeMessages(onProgressChanged) {
                assessFilter(filters, it.dltMessage)
            }
            Log.d("done removing messages by filter in $duration ms")
        }
    }

    private fun loadColorFilters(file: File) {
        colorFilters.clear()
        viewModelScope.launch {
            ColorFilterManager().loadFromFile(file)?.let {
                colorFilters.addAll(it)
            }
            preferencesRepository.addNewRecentColorFilter(
                RecentColorFilterFileEntry(
                    file.name,
                    file.absolutePath
                )
            )
        }
    }

    fun onLogsRowSelected(listIndex: Int, key: Int) {
        viewModelScope.launch(IO) {
            selectLogRow(listIndex, key)
        }
    }

    private suspend fun selectLogRow(listIndex: Int, key: Int) {
        messagesRepository.selectMessage(key)
        logSelection = logSelection.copy(logsIndex = listIndex)
    }

    fun onSearchRowSelected(listIndex: Int, id: Int) {
        viewModelScope.launch(Main) {
            selectSearchRow(listIndex, id)
        }
    }

    private suspend fun selectSearchRow(listIndex: Int, id: Int) {
        if (logSelection.searchIndex == listIndex) { // simulate second click
            try {
                val index = messagesRepository.getMessages().indexOfFirst { it.id == id }
                selectLogRow(index, id)
                logsListState.scrollToItem(index)
            } catch (e: Exception) {
                Log.e("Failed to select $listIndex-$id: $e")
            }
        } else {
            messagesRepository.selectMessage(id)
            logSelection = logSelection.copy(searchIndex = listIndex)
        }
    }

    private fun shouldSaveSearch(text: String): Boolean {
        return text.length >= 3
    }

    private fun startSearch(searchType: SearchType, searchText: String) {
        _searchState.value = _searchState.value.copy(
            searchText = searchText, state = SearchState.State.SEARCHING
        )
        searchJob = viewModelScope.launch(IO) {
            if (shouldSaveSearch(searchText)) {
                preferencesRepository.addNewSearch(SearchEntity(searchText))
            }
            Log.d("Start searching for $searchType '$searchText'")

            val searchRegex = if (_searchState.value.searchUseRegex) searchText.toRegex() else null

            val duration = messagesRepository.searchMessages(onProgressChanged) {
                matchSearch(searchType, searchRegex, searchText, it)
            }

            _searchState.value = _searchState.value.copy(
                searchText = searchText, state = SearchState.State.IDLE
            )
            Log.d("Search complete in $duration ms.")
        }
    }

    private fun matchSearch(
        searchType: SearchType,
        searchRegex: Regex?,
        searchText: String,
        logMessage: LogMessage
    ): Boolean {
        val payload = logMessage.getMessageText()
        return when (searchType) {

            SearchType.Text -> {
                ((searchRegex != null && searchRegex.containsMatchIn(payload))
                        || (payload.contains(searchText)))
            }

            SearchType.MarkedRows -> {
                logMessage.marked
            }

            SearchType.TextAndMarkedRows -> {
                logMessage.marked || ((searchRegex != null && searchRegex.containsMatchIn(
                    payload
                )) || (payload.contains(searchText)))
            }
        }
    }

    val colorFilters = mutableStateListOf<ColorFilter>()

    val colorFiltersDialogCallbacks = object : ColorFiltersDialogCallbacks {
        override fun onColorFilterUpdate(position: Int, filter: ColorFilter) {
            Log.d("onFilterUpdate $position $filter")
            if (position < 0 || position > colorFilters.size) {
                colorFilters.add(filter)
            } else colorFilters[position] = filter
        }

        override fun onColorFilterDelete(position: Int) {
            colorFilters.removeAt(position)
        }

        override fun onColorFilterMove(index: Int, offset: Int) {
            if (index + offset in 0..<colorFilters.size) {
                val temp = colorFilters[index]
                colorFilters[index] = colorFilters[index + offset]
                colorFilters[index + offset] = temp
            }

        }
    }


}