package com.alekso.dltstudio.plugins

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltparser.dlt.PayloadStorageType
import com.alekso.dltstudio.AppFormatter
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepositoryImpl
import com.alekso.dltstudio.logs.LogsViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DependencyManager {
    private var _progress = mutableStateOf(0f)
    val progress: State<Float>
        get() = _progress

    val onProgressUpdate = { p: Float ->
        _progress.value = p
    }

    private val formatter by lazy {
        AppFormatter()
    }

    private val pluginsManager by lazy {
        PluginManager(
            formatter = formatter,
            messagesProvider = provideMessagesProvider(),
            onProgressUpdate = onProgressUpdate,
        )
    }

    private val virtualDeviceRepository by lazy {
        VirtualDeviceRepositoryImpl(
            database = DBFactory().createDatabase(),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }
    private val insightsRepository by lazy { InsightsRepository() }

    private val logsViewModel = LogsViewModel(
        insightsRepository = insightsRepository,
        virtualDeviceRepository = virtualDeviceRepository,
        onProgressChanged = onProgressUpdate,
        formatter = formatter
    )

    private val timelineViewModel by lazy {
        TimelineViewModel(onProgressChanged = onProgressUpdate)
    }

    private val mainViewModel = MainViewModel(
        dltParser = DLTParserV2(PayloadStorageType.Binary),
        messagesHolder = provideMessageHolder(),
        messagesProvider = provideMessagesProvider(),
        timelineHolder = provideTimelineViewModel(),
        pluginManager = providePluginsManager(),
    )

    fun provideMessageHolder(): MessagesHolder {
        return logsViewModel
    }

    fun provideMessagesProvider(): MessagesProvider {
        return logsViewModel
    }

    fun provideLogsViewModel(): LogsViewModel {
        return logsViewModel
    }

    fun provideMainViewModel(): MainViewModel {
        return mainViewModel
    }

    fun provideTimelineViewModel(): TimelineViewModel {
        return timelineViewModel
    }

    fun providePluginsManager(): PluginManager {
        return pluginsManager
    }

    fun provideFormatter(): Formatter {
        return formatter
    }
}