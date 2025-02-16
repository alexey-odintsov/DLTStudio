package com.alekso.dltstudio.predefinedplugins

import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import com.alekso.dltstudio.plugins.filesviewer.FilesPlugin

public var predefinedPlugins = listOf<DLTStudioPlugin>(
    FilesPlugin(),
    DeviceAnalyzePlugin()
)