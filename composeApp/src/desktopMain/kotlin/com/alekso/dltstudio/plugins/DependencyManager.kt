package com.alekso.dltstudio.plugins

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltparser.dlt.PayloadStorageType
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.com.alekso.dltstudio.plugins.PluginManager
import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepositoryImpl
import com.alekso.dltstudio.logs.LogsViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
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

    private val _pluginsManager by lazy { PluginManager() }

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
        onProgressChanged = onProgressUpdate
    )

    private val _timelineViewModel by lazy {
        TimelineViewModel(onProgressChanged = onProgressUpdate)
    }

    private val mainViewModel = MainViewModel(
        dltParser = DLTParserV2(PayloadStorageType.Binary),
        messagesHolder = logsViewModel,
        timelineHolder = _timelineViewModel,
        pluginManager = _pluginsManager,
    )

    fun getMessageHolder(): MessagesHolder {
        return logsViewModel
    }

    fun getLogsViewModel(): LogsViewModel {
        return logsViewModel
    }

    fun getMainViewModel(): MainViewModel {
        return mainViewModel
    }

    fun getTimelineViewModel(): TimelineViewModel {
        return _timelineViewModel
    }

    fun getPluginsManager(): PluginManager {
        return _pluginsManager
    }
}