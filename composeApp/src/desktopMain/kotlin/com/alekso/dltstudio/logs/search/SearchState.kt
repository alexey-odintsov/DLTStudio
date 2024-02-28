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
    companion object {

        fun updateSearchText(
            currentState: SearchState,
            searchText: String,
        ): SearchState {
            return currentState.copy(searchText = searchText)
        }

        fun updateState(
            currentState: SearchState,
            state: State,
        ): SearchState {
            return currentState.copy(state = state)
        }

        fun updateSearchUseRegex(
            currentState: SearchState,
            searchUseRegex: Boolean,
        ): SearchState {
            return currentState.copy(searchUseRegex = searchUseRegex)
        }
    }
}
