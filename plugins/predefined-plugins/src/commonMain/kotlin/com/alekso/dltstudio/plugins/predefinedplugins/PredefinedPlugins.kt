package com.alekso.dltstudio.plugins.predefinedplugins

import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import alexey.odintsov.dltstudio.plugins.diagramtimeline.TimelinePlugin
import alexey.odintsov.dltstudio.plugins.filesviewer.FilesPlugin
import com.alekso.dltstudio.plugins.virtualdevice.VirtualDevicePlugin
import alexey.odintsov.dltstudio.plugins.dltdetailedview.DLTDetailedViewPlugin
import alexey.odintsov.dltstudio.plugins.loginfoview.LogInfoViewPlugin
import alexey.odintsov.dltstudio.plugins.loginsights.LogInsightsPlugin

var predefinedPlugins = listOf<DLTStudioPlugin>(
    TimelinePlugin(),
    FilesPlugin(),
    DeviceAnalyzePlugin(),
    LogInfoViewPlugin(),
    DLTDetailedViewPlugin(),
    VirtualDevicePlugin(),
    LogInsightsPlugin(),
)