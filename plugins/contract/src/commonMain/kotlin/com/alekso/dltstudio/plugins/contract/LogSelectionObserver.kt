package com.alekso.dltstudio.plugins.contract

import com.alekso.dltstudio.model.contract.LogMessage

interface LogSelectionObserver {
    fun onMessageSelected(logMessage: LogMessage)
}