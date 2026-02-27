package alexey.odintsov.dltstudio.logs.toolbar

import alexey.odintsov.dltstudio.logs.search.SearchType


interface LogsToolbarCallbacks {
    fun onColorFiltersClicked()
    fun onChangeOrderClicked()
    fun onTimeZoneChanged(timeZoneName: String)
    fun onPrevMarkedLog()
    fun onNextMarkedLog()
}