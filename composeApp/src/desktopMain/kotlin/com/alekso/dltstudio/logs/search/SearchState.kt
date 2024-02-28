package com.alekso.dltstudio.logs.search


data class SearchState(
    val searchText: String = "",
    val searchUseRegex: Boolean = false,
    val state: State = State.IDLE
) {
    companion object {
        enum class State {
            IDLE,
            SEARCHING
        }

        fun updateSearchText(
            currentState: SearchState,
            searchText: String,
        ): SearchState {
            return if (searchText == currentState.searchText) {
                currentState
            } else {
                currentState.copy(searchText = searchText)
            }
        }

        fun updateSearchUseRegex(
            currentState: SearchState,
            searchUseRegex: Boolean,
        ): SearchState {
            return currentState.copy(searchUseRegex = searchUseRegex)
        }
    }
}
