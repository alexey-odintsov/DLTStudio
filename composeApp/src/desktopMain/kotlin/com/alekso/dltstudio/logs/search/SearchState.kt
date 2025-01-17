package com.alekso.dltstudio.logs.search


data class SearchState(
    val searchText: String = "",
    val searchUseRegex: Boolean = false,
    val state: State = State.IDLE
) {
    enum class State {
        IDLE,
        SEARCHING
    }
}

enum class SearchType {
    Text,
    MarkedRows,
    TextAndMarkedRows,
}
