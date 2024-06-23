package com.alekso.dltstudio.model

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.LogTypeIndicator

data class LogMessage(
    val dltMessage: DLTMessage,
    val marked: Boolean = false,
    val comment: String? = "This is a test comment",
) {
    fun getMessageText(): String {
        return "${dltMessage.standardHeader.ecuId} " +
                "${dltMessage.standardHeader.sessionId} " +
                "${dltMessage.extendedHeader?.applicationId} " +
                "${dltMessage.extendedHeader?.contextId} " +
                "${LogTypeIndicator.fromMessageType(dltMessage.extendedHeader?.messageInfo?.messageTypeInfo)?.logTypeSymbol ?: ""} " +
                dltMessage.payload
    }
}
