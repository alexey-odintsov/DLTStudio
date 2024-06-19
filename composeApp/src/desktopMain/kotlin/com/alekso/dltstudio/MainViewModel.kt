package com.alekso.dltstudio

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.DLTParser
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterManager
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.preferences.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File

private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class MainViewModel(
    private val dltParser: DLTParser,
    private val onProgressChanged: (Float) -> Unit
) {

    private val _logMessages = mutableStateListOf<LogMessage>()
    val logMessages: SnapshotStateList<LogMessage>
        get() = _logMessages

    val searchResult = mutableStateListOf<LogMessage>()
    val searchIndexes = mutableStateListOf<Int>()
    val searchAutocomplete = mutableStateListOf<String>()

    private var parseJob: Job? = null
    private var searchJob: Job? = null

    val logsListState = LazyListState()
    var logsListSelectedRow = mutableStateOf(0)

    val searchListState = LazyListState()
    var searchListSelectedRow = mutableStateOf(0)


    fun onLogsRowSelected(coroutineScope: CoroutineScope, index: Int, rowId: Int) {
        coroutineScope.launch {
            logsListSelectedRow.value = rowId
        }
    }

    fun onSearchRowSelected(coroutineScope: CoroutineScope, index: Int, rowId: Int) {
        coroutineScope.launch {
            if (searchListSelectedRow.value == index) { // simulate second click
                logsListSelectedRow.value = rowId
                logsListState.scrollToItem(rowId)
            } else {
                searchListSelectedRow.value = index
            }
        }
    }

    fun parseFile(dltFiles: List<File>) {
        searchJob?.cancel()
        parseJob?.cancel()

        searchResult.clear()
        searchIndexes.clear()
        _logMessages.clear()

        parseJob = CoroutineScope(IO).launch {
            _logMessages.addAll(dltParser.read(onProgressChanged, dltFiles).map { LogMessage(it) })
        }
    }


    private var _searchState: MutableStateFlow<SearchState> = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

    fun onSearchUseRegexChanged(checked: Boolean) {
        _searchState.value = _searchState.value.copy(searchUseRegex = checked)
    }

    fun onSearchClicked(searchText: String) {
        when (_searchState.value.state) {
            SearchState.State.IDLE -> startSearch(searchText)
            SearchState.State.SEARCHING -> stopSearch()
        }
    }

    private fun stopSearch() {
        searchJob?.cancel()
        _searchState.value = _searchState.value.copy(
            state = SearchState.State.IDLE
        )
    }

    private fun startSearch(searchText: String) {
        Preferences.addRecentSearch(searchText)

        _searchState.value = _searchState.value.copy(
            searchText = searchText,
            state = SearchState.State.SEARCHING
        )
        searchJob = CoroutineScope(IO).launch {
            var prevTs  = System.currentTimeMillis()
            if (!searchAutocomplete.contains(searchText)) {
                searchAutocomplete.add(searchText)
            }
            searchResult.clear()
            searchIndexes.clear()
            val startMs = System.currentTimeMillis()
            println("Start searching for '$searchText'")

            _logMessages.forEachIndexed { i, dltMessage ->
                yield()
                val payload = dltMessage.getMessageText()

                if ((_searchState.value.searchUseRegex && searchText.toRegex()
                        .containsMatchIn(payload))
                    || (payload.contains(searchText))
                ) {
                    searchResult.add(dltMessage)
                    searchIndexes.add(i)
                }
                val nowTs = System.currentTimeMillis()
                if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                    prevTs = nowTs
                    onProgressChanged(i.toFloat() / logMessages.size)
                }
            }
            _searchState.value = _searchState.value.copy(
                searchText = searchText,
                state = SearchState.State.IDLE
            )
            onProgressChanged(1f)
            println("Search complete in ${(System.currentTimeMillis() - startMs) / 1000} sec.")
        }
    }


    val colorFilters = mutableStateListOf<ColorFilter>()

    fun onColorFilterUpdate(index: Int, updatedFilter: ColorFilter) {
        println("onFilterUpdate $index $updatedFilter")
        if (index < 0 || index > colorFilters.size) {
            colorFilters.add(updatedFilter)
        } else colorFilters[index] = updatedFilter
    }

    fun onColorFilterMove(index: Int, offset: Int) {
        if (index + offset in 0..<colorFilters.size) {
            val temp = colorFilters[index]
            colorFilters[index] = colorFilters[index + offset]
            colorFilters[index + offset] = temp
        }
    }

    fun onColorFilterDelete(index: Int) {
        colorFilters.removeAt(index)
    }

    fun saveColorFilters(file: File) {
        ColorFilterManager().saveToFile(colorFilters, file)
        Preferences.addRecentColorFilter(file.name, file.absolutePath)
    }

    fun loadColorFilters(file: File) {
        colorFilters.clear()
        ColorFilterManager().loadFromFile(file)?.let {
            colorFilters.addAll(it)
        }
        Preferences.addRecentColorFilter(file.name, file.absolutePath)
    }

    fun clearColorFilters() {
        colorFilters.clear()
    }
}