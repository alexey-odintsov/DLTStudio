package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.deviceanalyze.DeviceAnalyzePlugin
import com.alekso.dltstudio.plugins.filesviewer.FilesPlugin

public var predefinedPlugins = listOf<DLTStudioPlugin>(
    FilesPlugin(),
    DeviceAnalyzePlugin()
)