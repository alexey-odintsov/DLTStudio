package com.alekso.dltstudio.plugins

import java.io.File

interface TimelineHolder {
    fun loadTimeLineFilters(file: File)
    fun clearTimeLineFilters()
    fun saveTimeLineFilters(file: File)
}