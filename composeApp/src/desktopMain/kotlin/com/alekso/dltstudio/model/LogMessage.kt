package com.alekso.dltstudio.model

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.LogTypeIndicator
import java.util.concurrent.atomic.AtomicInteger

data class LogMessage(
    val dltMessage: DLTMessage,
    val key: String = "${counter.getAndIncrement()}",
    val marked: Boolean = false,
    val comment: String? = null,
) {
    fun getMessageText(): String {
        return "${dltMessage.ecuId} " +
                "${dltMessage.sessionId} " +
                "${dltMessage.applicationId} " +
                "${dltMessage.contextId} " +
                "${LogTypeIndicator.fromMessageType(dltMessage.messageTypeInfo)?.logTypeSymbol ?: ""} " +
                dltMessage.payload
    }

    companion object {
        @Volatile
        var counter = AtomicInteger(0)
    }
}
