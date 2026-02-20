package alexey.odintsov.dltstudio.plugins.contract

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import alexey.odintsov.dltstudio.model.contract.LogMessage

interface PluginLogPreview {
    @Composable
    fun renderPreview(modifier: Modifier, logMessage: LogMessage?)

    fun getPanelName(): String

}