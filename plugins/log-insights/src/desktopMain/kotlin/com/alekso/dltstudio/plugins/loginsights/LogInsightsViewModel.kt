package com.alekso.dltstudio.plugins.loginsights

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.logger.Log

class LogInsightsViewModel(
    private val insightsRepository: InsightsRepository,
) {
    val logInsights = mutableStateListOf<LogInsight>()

    fun loadInsights(logMessage: LogMessage?) {
        try {
            logInsights.clear()
            if (logMessage != null) {
                logInsights.addAll(insightsRepository.findInsight(logMessage))
            }
        } catch (e: Exception) {
            Log.e(e.toString())
        }
    }
}