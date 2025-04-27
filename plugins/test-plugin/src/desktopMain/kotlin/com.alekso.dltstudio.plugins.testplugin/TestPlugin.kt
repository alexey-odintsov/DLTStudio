package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel

class TestPlugin : DLTStudioPlugin, PluginPanel {
    override fun pluginName(): String = "TestPlugin"
    override fun pluginDirectoryName(): String = "test-plugin"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = TestPlugin::class.qualifiedName.toString()
    override fun getPanelName(): String = "Test Plugin"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectory: String,
    ) {
        // do nothing
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text("Test plugin works!")
    }
}