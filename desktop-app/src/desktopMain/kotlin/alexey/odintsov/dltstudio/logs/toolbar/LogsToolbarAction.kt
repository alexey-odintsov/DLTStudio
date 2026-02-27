package alexey.odintsov.dltstudio.logs.toolbar

import alexey.odintsov.dltstudio.logs.search.SearchType

sealed interface LogsToolbarAction {
    class ClickSearch(val searchType: SearchType, val text: String) : LogsToolbarAction
    object ToggleFatal : LogsToolbarAction
    object ToggleError : LogsToolbarAction
    object ToggleWarning : LogsToolbarAction
    object ToggleComments : LogsToolbarAction
    object ToggleSearchWithMarked : LogsToolbarAction
    object ToggleWrapContent : LogsToolbarAction
    object ToggleSearchRegex : LogsToolbarAction
    object ClickColorFilters : LogsToolbarAction
    object ClickChangeOrder : LogsToolbarAction
    class ChangeTimeZone(val timeZoneName: String) : LogsToolbarAction
    object ClickNextMark : LogsToolbarAction
    object ClickPrevMark : LogsToolbarAction
}