package com.alekso.dltstudio.plugins.contract

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage

interface PluginLogPreview {
    @Composable
    fun renderPreview(modifier: Modifier, logMessage: LogMessage?, messageIndex: Int)

    fun getPanelName(): String

}