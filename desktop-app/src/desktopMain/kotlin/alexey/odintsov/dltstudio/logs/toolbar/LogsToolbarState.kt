package alexey.odintsov.dltstudio.logs.toolbar

data class LogsToolbarState(
    val toolbarFatalChecked: Boolean,
    val toolbarErrorChecked: Boolean,
    val toolbarWarningChecked: Boolean,
    val toolbarCommentsChecked: Boolean,
    val toolbarSearchWithMarkedChecked: Boolean,
    val toolbarWrapContentChecked: Boolean,
)