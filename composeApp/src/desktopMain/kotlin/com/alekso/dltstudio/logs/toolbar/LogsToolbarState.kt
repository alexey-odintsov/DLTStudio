package com.alekso.dltstudio.logs.toolbar

data class LogsToolbarState(
    val toolbarFatalChecked: Boolean,
    val toolbarErrorChecked: Boolean,
    val toolbarWarningChecked: Boolean,
    val toolbarCommentsChecked: Boolean,
    val toolbarSearchWithMarkedChecked: Boolean,
    val toolbarWrapContentChecked: Boolean,
) {
    companion object {
        fun updateToolbarFatalCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarFatalChecked = newValue)
        }

        fun updateToolbarErrorCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarErrorChecked = newValue)
        }

        fun updateToolbarWarnCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarWarningChecked = newValue)
        }

        fun updateToolbarSearchWithMarkedCheck(
            state: LogsToolbarState,
            newValue: Boolean
        ): LogsToolbarState {
            return state.copy(toolbarSearchWithMarkedChecked = newValue)
        }

        fun updateToolbarWrapContentCheck(
            state: LogsToolbarState,
            newValue: Boolean
        ): LogsToolbarState {
            return state.copy(toolbarWrapContentChecked = newValue)
        }

        fun updateToolbarCommentsCheck(
            state: LogsToolbarState,
            newValue: Boolean
        ): LogsToolbarState {
            return state.copy(toolbarCommentsChecked = newValue)
        }
    }
}