package com.alekso.dltstudio

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.search.SearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val dltParser: DLTParser,
    private val onProgressChanged: (Float) -> Unit
) {

    private val _dltMessages = mutableStateListOf<DLTMessage>()
    val dltMessages: SnapshotStateList<DLTMessage>
        get() = _dltMessages

    val searchResult = mutableStateListOf<DLTMessage>()
    val searchIndexes = mutableStateListOf<Int>()

    private var parseJob: Job? = null
    private var searchJob: Job? = null

    fun parseFile(dltFiles: List<File>) {
        searchJob?.cancel()
        parseJob?.cancel()

        searchResult.clear()
        searchIndexes.clear()
        _dltMessages.clear()

        parseJob = CoroutineScope(IO).launch {
            _dltMessages.addAll(dltParser.read(onProgressChanged, dltFiles))
        }
    }


    private var _searchState: MutableStateFlow<SearchState> = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

    fun onSearchUseRegexChanged(checked: Boolean) {
        _searchState.value = SearchState.updateSearchUseRegex(_searchState.value, checked)
    }

    fun onSearchClicked(searchText: String) {
        _searchState.value = SearchState.updateSearchText(_searchState.value, searchText)
        parseJob = CoroutineScope(IO).launch {
            searchResult.clear()
            searchIndexes.clear()

            _dltMessages.forEachIndexed { i, dltMessage ->
                val payload = dltMessage.payload

                if (payload != null) {
                    if ((_searchState.value.searchUseRegex && searchText.toRegex()
                            .containsMatchIn(payload.asText()))
                        || (payload.asText().contains(searchText))
                    ) {
                        searchResult.add(dltMessage)
                        searchIndexes.add(i)
                    }
                    onProgressChanged(i.toFloat() / dltMessages.size)
                }
            }
        }
    }

}