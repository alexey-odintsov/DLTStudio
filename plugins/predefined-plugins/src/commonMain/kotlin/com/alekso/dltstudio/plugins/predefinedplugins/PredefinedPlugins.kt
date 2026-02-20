package com.alekso.dltstudio.plugins.predefinedplugins

import alexey.odintsov.dltstudio.plugins.contract.DLTStudioPlugin
import alexey.odintsov.dltstudio.plugins.deviceplugin.DeviceAnalyzePlugin
import alexey.odintsov.dltstudio.plugins.diagramtimeline.TimelinePlugin
import com.alekso.dltstudio.plugins.filesviewer.FilesPlugin
import com.alekso.dltstudio.plugins.virtualdevice.VirtualDevicePlugin
import com.alekso.dltstudio.plugins.dltdetailedview.DLTDetailedViewPlugin
import com.alekso.dltstudio.plugins.loginfoview.LogInfoViewPlugin
import com.alekso.dltstudio.plugins.loginsights.LogInsightsPlugin

var predefinedPlugins = listOf<DLTStudioPlugin>(
    TimelinePlugin(),
    FilesPlugin(),
    DeviceAnalyzePlugin(),
    LogInfoViewPlugin(),
    DLTDetailedViewPlugin(),
    VirtualDevicePlugin(),
    LogInsightsPlugin(),
)