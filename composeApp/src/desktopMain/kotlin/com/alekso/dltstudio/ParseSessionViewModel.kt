package com.alekso.dltstudio

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ParseSessionViewModel(
    private val dltParser: DLTParser
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
            _dltMessages.addAll(dltParser.read({
                // todo
            }, dltFiles))
        }
    }

    fun search(searchText: String, searchUseRegex: Boolean) {
        parseJob = CoroutineScope(IO).launch {
            searchResult.clear()
            searchIndexes.clear()
            println("Searching for $searchText..")
            _dltMessages.forEachIndexed { i, dltMessage ->
                val payload = dltMessage.payload

                if (payload != null) {
                    if ((searchUseRegex && searchText.toRegex()
                            .containsMatchIn(payload.asText()))
                        || (payload.asText().contains(searchText))
                    ) {
                        searchResult.add(dltMessage)
                        searchIndexes.add(i)
                    }
//                    statusBarProgressCallback.invoke(i.toFloat() / dltMessages.size)
                }
            }
        }
    }

}