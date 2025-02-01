package com.alekso.dltstudio.plugins

import com.alekso.dltparser.DLTParserV2
import com.alekso.dltparser.dlt.PayloadStorageType
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepositoryImpl
import com.alekso.dltstudio.logs.LogsViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DependencyManager {
    private val onProgressUpdate = { progress: Float -> }

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
        onProgressChanged = { progress -> } // TODO: Pass on progress callback
    )

    private val _timelineViewModel by lazy {
        TimelineViewModel(onProgressChanged = onProgressUpdate)
    }

    private val mainViewModel = MainViewModel(
        dltParser = DLTParserV2(PayloadStorageType.Binary),
        onProgressChanged = onProgressUpdate,
        messagesHolder = logsViewModel,
        timelineHolder = _timelineViewModel,
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
}