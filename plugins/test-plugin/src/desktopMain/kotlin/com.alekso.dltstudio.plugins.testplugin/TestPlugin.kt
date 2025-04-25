package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.PluginPanel

enum class TestEnum {
    Enum1,
    Enum2
}

class TestPlugin : DLTStudioPlugin, PluginPanel {
    private var value: TestEnum = TestEnum.Enum2

    override fun pluginName(): String = "TestPlugin"
    override fun pluginDirectoryName(): String = "test-plugin"

    override fun pluginVersion(): String = "1.0.0"

    override fun pluginClassName(): String = TestPlugin::class.qualifiedName.toString()

    override fun init(
        logs: SnapshotStateList<LogMessage>,
        onProgressUpdate: (Float) -> Unit,
        pluginDirectoryPath: String
    ) {
        val value = TestEnum.Enum1
    }

    override fun onLogsChanged() {

    }

    override fun getPanelName(): String = "Test Plugin"

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text("Test plugin works!")
        Text("value = $value")
    }

}