package com.alekso.dltstudio.logs.toolbar

import com.alekso.dltstudio.logs.search.SearchType

interface LogsToolbarCallbacks {
    fun onSearchButtonClicked(searchType: SearchType, text: String)
    fun updateToolbarFatalCheck(checked: Boolean)
    fun updateToolbarErrorCheck(checked: Boolean)
    fun updateToolbarWarningCheck(checked: Boolean)
    fun updateToolbarCommentsCheck(checked: Boolean)
    fun updateToolbarSearchWithMarkedCheck(checked: Boolean)
    fun updateToolbarWrapContentCheck(checked: Boolean)
    fun onSearchUseRegexChanged(checked: Boolean)
    fun onColorFiltersClicked()
    fun onTimeZoneChanged(timeZoneName: String)

    companion object {
        val Stub = object : LogsToolbarCallbacks {
            override fun onSearchButtonClicked(searchType: SearchType, text: String) = Unit
            override fun updateToolbarFatalCheck(checked: Boolean) = Unit
            override fun updateToolbarErrorCheck(checked: Boolean) = Unit
            override fun updateToolbarWarningCheck(checked: Boolean) = Unit
            override fun updateToolbarCommentsCheck(checked: Boolean) = Unit
            override fun updateToolbarSearchWithMarkedCheck(checked: Boolean) = Unit
            override fun updateToolbarWrapContentCheck(checked: Boolean) = Unit
            override fun onSearchUseRegexChanged(checked: Boolean) = Unit
            override fun onColorFiltersClicked() = Unit
            override fun onTimeZoneChanged(timeZoneName: String) = Unit
        }
    }
}