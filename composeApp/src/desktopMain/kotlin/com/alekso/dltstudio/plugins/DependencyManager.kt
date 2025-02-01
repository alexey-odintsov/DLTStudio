package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepositoryImpl
import com.alekso.dltstudio.logs.LogsViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DependencyManager {
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

    fun getMessageHolder(): MessagesHolder {
        return logsViewModel
    }

    fun getLogsViewModel(): LogsViewModel {
        return logsViewModel
    }
}