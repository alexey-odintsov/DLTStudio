package com.alekso.dltStudio.predefinedplugins

import com.alekso.dltstudio.plugins.DLTStudioPlugin
import com.alekso.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import com.alekso.dltstudio.plugins.filesviewer.FilesPlugin

var predefinedPlugins = listOf<DLTStudioPlugin>(
    FilesPlugin(),
    DeviceAnalyzePlugin()
)