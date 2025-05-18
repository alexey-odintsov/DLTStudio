package com.alekso.dltstudio.plugins.testplugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.FormatterConsumer
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel

val LocalFormatter = staticCompositionLocalOf<Formatter> { Formatter.STUB }

class TestPlugin : DLTStudioPlugin, PluginPanel, FormatterConsumer {
    private lateinit var viewModel: ViewModel
    private lateinit var formatter: Formatter

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
        CompositionLocalProvider(LocalFormatter provides formatter) {
            TestPanel(
                modifier = modifier,
                entries = viewModel.entriesMap,
                onAnaliseClicked = viewModel::onAnaliseClicked,
                onDragged = viewModel::onDragged,
                onZoom = viewModel::onZoom,
                onFit = viewModel::onFit,
                totalFrame = viewModel.totalTime,
                timeFrame = viewModel.timeFrame,
            )
        }
    }

    override fun initFormatter(formatter: Formatter) {
        this.formatter = formatter
    }
}