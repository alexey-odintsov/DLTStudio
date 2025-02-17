package com.alekso.dltstudio.model.contract

import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltmessage.DLTMessage
import java.util.concurrent.atomic.AtomicInteger

data class LogMessage(
    val dltMessage: DLTMessage,
    val key: String = "${counter.getAndIncrement()}",
    val marked: Boolean = false,
    val comment: String? = null,
) {
    fun getMessageText(): String {
        return "${dltMessage.standardHeader.ecuId} " +
                "${dltMessage.standardHeader.sessionId} " +
                "${dltMessage.extendedHeader?.applicationId} " +
                "${dltMessage.extendedHeader?.contextId} " +
                "${getMessageTypeIndicatorSymbol(dltMessage.extendedHeader?.messageInfo?.messageTypeInfo)} " +
                dltMessage.payloadText()
    }

    companion object {
        @Volatile
        var counter = AtomicInteger(0)

        fun getMessageTypeIndicatorSymbol(messageTypeInfo: MessageTypeInfo?): Char {
            return when (messageTypeInfo) {
                MessageTypeInfo.DLT_LOG_FATAL -> 'F'
                MessageTypeInfo.DLT_LOG_DLT_ERROR -> 'E'
                MessageTypeInfo.DLT_LOG_WARN -> 'W'
                MessageTypeInfo.DLT_LOG_INFO -> 'I'
                MessageTypeInfo.DLT_LOG_DEBUG -> 'D'
                MessageTypeInfo.DLT_LOG_VERBOSE -> 'V'
                else -> ' '
            }
        }
    }
}