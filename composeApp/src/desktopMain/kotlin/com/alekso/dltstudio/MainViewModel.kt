package com.alekso.dltstudio

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterManager
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria
import com.alekso.dltstudio.logs.search.SearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
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
        _searchState.value = _searchState.value.copy(
            searchText = searchText,
            state = SearchState.State.SEARCHING
        )
        searchJob = CoroutineScope(IO).launch {
            searchResult.clear()
            searchIndexes.clear()

            _dltMessages.forEachIndexed { i, dltMessage ->
                yield()
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
            _searchState.value = _searchState.value.copy(
                searchText = searchText,
                state = SearchState.State.IDLE
            )
        }
    }


    val colorFilters = mutableStateListOf<ColorFilter>(
        ColorFilter(
            "SIP",
            mapOf(FilterParameter.ContextId to FilterCriteria("TC", TextCriteria.PlainText)),
            CellStyle(backgroundColor = Color.Green, textColor = Color.White),
            enabled = false,
        ),
        ColorFilter(
            "Logcat",
            mapOf(
                FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
                FilterParameter.ContextId to FilterCriteria("LCAT", TextCriteria.PlainText)
            ),
            CellStyle(backgroundColor = Color.Magenta, textColor = Color.White),
            enabled = true,
        ),
        ColorFilter(
            "Regex test",
            mapOf(
                FilterParameter.Payload to FilterCriteria("(\\d+%)", TextCriteria.Regex)
            ),
            CellStyle(backgroundColor = Color.Blue, textColor = Color.White),
            enabled = true,
        ),
    )

    fun onColorFilterUpdate(index: Int, updatedFilter: ColorFilter) {
        println("onFilterUpdate $index $updatedFilter")
        if (index < 0 || index > colorFilters.size) {
            colorFilters.add(updatedFilter)
        } else colorFilters[index] = updatedFilter
    }

    fun onColorFilterDelete(index: Int) {
        colorFilters.removeAt(index)
    }

    fun saveColorFilters(file: File) {
        ColorFilterManager().saveToFile(colorFilters, file)
    }

    fun loadColorFilters(file: File) {
        colorFilters.clear()
        ColorFilterManager().loadFromFile(file)?.let {
            colorFilters.addAll(it)
        }
    }
}