package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel

class TestPlugin : DLTStudioPlugin, PluginPanel {
    private lateinit var viewModel: ViewModel

    override fun pluginName(): String = "TestPlugin"
    override fun pluginDirectoryName(): String = "test-plugin"
    override fun pluginVersion(): String = "1.0.0"
    override fun pluginClassName(): String = TestPlugin::class.qualifiedName.toString()
    override fun getPanelName(): String = "Test Plugin"

    override fun init(
        messagesRepository: MessagesRepository,
        onProgressUpdate: (Float) -> Unit,
        pluginFilesPath: String,
    ) {
        viewModel = ViewModel()
    }

    override fun onLogsChanged() {
        // do nothing
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        TestPanel(modifier = modifier,
            entries = viewModel.entries,
            onAnaliseClicked = viewModel::onAnaliseClicked,
            onDragged = viewModel::onDragged,
            totalFrame = viewModel.totalTime,
            timeFrame = viewModel.timeFrame,
            )
    }
}