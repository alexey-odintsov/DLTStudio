package com.alekso.dltstudio.logs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceEntity
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepository
import com.alekso.dltstudio.db.virtualdevice.toVirtualDevice
import com.alekso.dltstudio.db.virtualdevice.toVirtualDeviceEntity
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterManager
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialogCallbacks
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.checkTextCriteria
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.logs.insights.LogInsight
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.logs.toolbar.LogsToolbarCallbacks
import com.alekso.dltstudio.logs.toolbar.LogsToolbarState
import com.alekso.dltstudio.model.VirtualDevice
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.MessagesHolder
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import java.io.File

private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

enum class LogRemoveContext {
    ApplicationId, ContextId, EcuId, SessionId, BeforeTimestamp, AfterTimestamp, Payload
}

interface RowContextMenuCallbacks {
    fun onCopyClicked(text: AnnotatedString)
    fun onMarkClicked(i: Int, message: LogMessage)
    fun onRemoveClicked(context: LogRemoveContext, filter: String)
    fun onRemoveDialogClicked(message: LogMessage)
}

class LogsViewModel(
    private val formatter: Formatter,
    private val insightsRepository: InsightsRepository,
    private val virtualDeviceRepository: VirtualDeviceRepository,
    private val onProgressChanged: (Float) -> Unit,
) : MessagesHolder, MessagesProvider {
    private val _logMessages = mutableStateListOf<LogMessage>()
    val logMessages: SnapshotStateList<LogMessage>
        get() = _logMessages

    private val _virtualDevices = mutableStateListOf<VirtualDevice>()
    val virtualDevices: SnapshotStateList<VirtualDevice>
        get() = _virtualDevices

    private var _searchState = MutableStateFlow<SearchState>(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

    private var searchJob: Job? = null

    val logsListState = LazyListState()
    var logsListSelectedRow = mutableStateOf(0)

    val searchListState = LazyListState()
    var searchListSelectedRow = mutableStateOf(0)
    val logInsights = mutableStateListOf<LogInsight>()

    private var _searchResults = mutableStateListOf<LogMessage>()
    val searchResults: SnapshotStateList<LogMessage>
        get() = _searchResults

    private val _searchIndexes = mutableStateListOf<Int>()
    val searchIndexes: SnapshotStateList<Int>
        get() = _searchIndexes

    private val _searchAutocomplete = mutableStateListOf<String>()
    val searchAutocomplete: SnapshotStateList<String>
        get() = _searchAutocomplete


    fun onSearchClicked(searchType: SearchType, searchText: String) {
        when (_searchState.value.state) {
            SearchState.State.IDLE -> startSearch(searchType, searchText)
            SearchState.State.SEARCHING -> stopSearch()
        }
    }

    private fun stopSearch() {
        searchJob?.cancel()
        _searchState.value = _searchState.value.copy(
            state = SearchState.State.IDLE
        )
    }

    val devicePreviewsDialogState = mutableStateOf(false)
    val removeLogsDialogState = mutableStateOf(
        RemoveLogsDialogState(
            visible = false, message = null
        )
    )

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
            toolbarCommentsChecked = false,
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


    init {
        CoroutineScope(IO).launch {
            virtualDeviceRepository.getAllAsFlow().collect {
                withContext(Main) {
                    _virtualDevices.clear()
                    _virtualDevices.addAll(it.map(VirtualDeviceEntity::toVirtualDevice))
                }
            }
        }
    }

    fun onVirtualDeviceUpdate(device: VirtualDevice) {
        CoroutineScope(IO).launch {
            virtualDeviceRepository.insert(
                if (device.id >= 0) {
                    VirtualDeviceEntity(
                        id = device.id,
                        title = device.name,
                        width = device.width,
                        height = device.height
                    )
                } else {
                    VirtualDeviceEntity(
                        title = device.name, width = device.width, height = device.height
                    )
                }
            )
        }
    }

    fun onVirtualDeviceDelete(device: VirtualDevice) {
        CoroutineScope(IO).launch {
            virtualDeviceRepository.delete(device.toVirtualDeviceEntity())
        }
    }

    fun onLogsRowSelected(index: Int, rowId: Int) {
        CoroutineScope(IO).launch {
            logsListSelectedRow.value = rowId
            onLogSelected(_logMessages[rowId])
        }
    }

    private fun onLogSelected(logMessage: LogMessage) {
        try {
            logInsights.clear()
            logInsights.addAll(insightsRepository.findInsight(logMessage))
        } catch (e: Exception) {
            Log.e(e.toString())
        }
    }

    fun onSearchRowSelected(index: Int, rowId: Int) {
        CoroutineScope(Main).launch {
            if (searchListSelectedRow.value == index) { // simulate second click
                try {
                    logsListSelectedRow.value = rowId
                    onLogSelected(_logMessages[rowId])
                    logsListState.scrollToItem(rowId)
                } catch (e: Exception) {
                    Log.e("Failed to select row $rowId: $e")
                }
            } else {
                searchListSelectedRow.value = index
            }
        }
    }

    private fun startSearch(searchType: SearchType, searchText: String) {
        Preferences.addRecentSearch(searchText)

        _searchState.value = _searchState.value.copy(
            searchText = searchText, state = SearchState.State.SEARCHING
        )
        searchJob = CoroutineScope(IO).launch {
            var prevTs = System.currentTimeMillis()
            if (!_searchAutocomplete.contains(searchText)) {
                _searchAutocomplete.add(searchText)
            }
            _searchResults.clear()
            _searchIndexes.clear()
            val startMs = System.currentTimeMillis()
            Log.d("Start searching for $searchType '$searchText'")

            val searchRegex = if (_searchState.value.searchUseRegex) searchText.toRegex() else null

            _logMessages.forEachIndexed { i, logMessage ->
                yield()
                val payload = logMessage.getMessageText()

                val matches = when (searchType) {
                    SearchType.Text -> {
                        ((searchRegex != null && searchRegex.containsMatchIn(payload)) || (payload.contains(
                            searchText
                        )))
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

                if (matches) {
                    withContext(Dispatchers.Swing) {
                        _searchResults.add(logMessage)
                        _searchIndexes.add(i)
                    }
                }

                val nowTs = System.currentTimeMillis()
                if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                    prevTs = nowTs
                    onProgressChanged(i.toFloat() / logMessages.size)
                }
            }

            _searchState.value = _searchState.value.copy(
                searchText = searchText, state = SearchState.State.IDLE
            )
            onProgressChanged(1f)
            Log.d("Search complete in ${(System.currentTimeMillis() - startMs) / 1000} sec.")
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

    override fun saveColorFilters(file: File) {
        ColorFilterManager().saveToFile(colorFilters, file)
        Preferences.addRecentColorFilter(file.name, file.absolutePath)
    }

    override fun loadColorFilters(file: File) {
        colorFilters.clear()
        ColorFilterManager().loadFromFile(file)?.let {
            colorFilters.addAll(it)
        }
        Preferences.addRecentColorFilter(file.name, file.absolutePath)
    }

    override fun clearColorFilters() {
        colorFilters.clear()
    }

    fun removeMessagesByFilters(filters: Map<FilterParameter, FilterCriteria>) {
        CoroutineScope(IO).launch {
            Log.d("start removing by '$filters'")
            var prevTs = System.currentTimeMillis()
            val filtered = _logMessages.filterIndexed { index, logMessage ->
                val message = logMessage.dltMessage
                val nowTs = System.currentTimeMillis()
                if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                    prevTs = nowTs
                    onProgressChanged(index.toFloat() / _logMessages.size)
                }

                !assessFilter(filters, message)
            }

            withContext(Dispatchers.Swing) {
                _logMessages.clear()
                _logMessages.addAll(filtered)
            }
            onProgressChanged(1f)
            Log.d("done removing by filters '$filters'")
        }
    }

    fun removeMessages(type: LogRemoveContext, filter: String) {
        CoroutineScope(IO).launch {
            Log.d("start removing '$filter' $type")
            var prevTs = System.currentTimeMillis()
            val filtered = _logMessages.filterIndexed { index, logMessage ->
                val message = logMessage.dltMessage
                val nowTs = System.currentTimeMillis()
                if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                    prevTs = nowTs
                    onProgressChanged(index.toFloat() / _logMessages.size)
                }

                when (type) {
                    LogRemoveContext.ContextId -> message.extendedHeader?.contextId != filter
                    LogRemoveContext.ApplicationId -> message.extendedHeader?.applicationId != filter
                    LogRemoveContext.EcuId -> message.standardHeader.ecuId != filter
                    LogRemoveContext.SessionId -> message.standardHeader.sessionId.toString() != filter
                    LogRemoveContext.BeforeTimestamp -> message.timeStampNano >= filter.toLong()
                    LogRemoveContext.AfterTimestamp -> message.timeStampNano <= filter.toLong()
                    LogRemoveContext.Payload -> {
                        true
                    }
                }
            }

            withContext(Dispatchers.Swing) {
                _logMessages.clear()
                _logMessages.addAll(filtered)
            }
            onProgressChanged(1f)

            // TODO: update searchIndexes as well otherwise they will be broken
//            val filteredSearch = searchResult.filterIndexed { index, message ->
//                val nowTs = System.currentTimeMillis()
//                if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
//                    prevTs = nowTs
//                    onProgressChanged(index.toFloat() / dltMessages.size)
//                }
//
//                when (type) {
//                    "context" -> message.extendedHeader?.contextId != filter
//                    "app" -> message.extendedHeader?.applicationId != filter
//                    else -> false
//                }
//            }
//
//            searchResult.clear()
//            searchResult.addAll(filteredSearch)
            onProgressChanged(1f)
            Log.d("done removing '$filter'")
        }
    }

    fun markMessage(i: Int, message: LogMessage) {
        val updatedMessage = message.copy(marked = message.marked.not())
        val logMessageIndex = logMessages.indexOf(message)
        val searchMessageIndex = _searchResults.indexOf(message)

        if (logMessageIndex != -1) {
            _logMessages[logMessageIndex] = updatedMessage
        }
        if (searchMessageIndex != -1) {
            _searchResults[searchMessageIndex] = updatedMessage
        }
    }

    fun updateComment(message: LogMessage, comment: String?) {
        val updatedMessage = message.copy(comment = comment)
        val logMessageIndex = logMessages.indexOf(message)
        val searchMessageIndex = _searchResults.indexOf(message)

        if (logMessageIndex != -1) {
            _logMessages[logMessageIndex] = updatedMessage
        }
        if (searchMessageIndex != -1) {
            _searchResults[searchMessageIndex] = updatedMessage
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

    override fun getMessages(): SnapshotStateList<LogMessage> = logMessages
    override fun clearMessages() {
        _logMessages.clear()
    }

    override fun storeMessages(logMessages: List<LogMessage>) {
        _logMessages.clear()
        _logMessages.addAll(logMessages)
    }
}