package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.PluginPanel

class TestPlugin : DLTStudioPlugin, PluginPanel {
    override fun pluginName(): String = "TestPlugin"

    override fun pluginVersion(): String = "1.0.0"

    override fun pluginClassName(): String = "com.alekso.dltstudio.plugins.testplugin.TestPlugin"

    override fun init(logs: SnapshotStateList<LogMessage>, onProgressUpdate: (Float) -> Unit) {

    }

    override fun onLogsChanged() {

    }

    override fun getPanelName(): String = "Test Plugin"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text("Test plugin works!")
    }

}