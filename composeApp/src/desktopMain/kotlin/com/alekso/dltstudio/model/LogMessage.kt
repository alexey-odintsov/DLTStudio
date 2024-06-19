package com.alekso.dltstudio.model

import com.alekso.dltparser.dlt.DLTMessage

data class LogMessage(
    val dltMessage: DLTMessage,
    val marked: Boolean = false,
    val comment: String? = null,
) {
    fun getMessageText(): String {
        return "${dltMessage.standardHeader.ecuId} " +
                "${dltMessage.standardHeader.sessionId} " +
                "${dltMessage.extendedHeader?.applicationId} " +
                "${dltMessage.extendedHeader?.contextId} " +
                dltMessage.payload
    }
}
