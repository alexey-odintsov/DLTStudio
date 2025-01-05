package com.alekso.dltstudio.utils

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.PlainDLTMessage
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo

object SampleData {
    fun getSampleDltMessages(size: Int): List<DLTMessage> {
        val list = mutableListOf<DLTMessage>()

        for (i in 0..size) {
            val dltMessage = create(
                timeStampNano = System.currentTimeMillis(),
                payloadText = "Test message $i"
            )
            list.add(dltMessage)
        }

        return list
    }

    fun create(
        timeStampNano: Long = System.nanoTime(),
        messageType: MessageType? = MessageType.DLT_TYPE_LOG,
        messageTypeInfo: MessageTypeInfo? = MessageTypeInfo.DLT_LOG_DEBUG,
        ecuId: String? = "ECU1",
        applicationId: String? = "APP1",
        contextId: String? = "CTX1",
        sessionId: Int? = 1,
        payloadText: String?,
        timeStamp: UInt? = 1U
    ): DLTMessage {
        return PlainDLTMessage(
            sizeBytes = -1,
            timeStampNano = timeStampNano,
            messageType = messageType,
            messageTypeInfo = messageTypeInfo,
            ecuId = ecuId,
            applicationId = applicationId,
            contextId = contextId,
            sessionId = sessionId,
            payloadText = payloadText,
            payload = payloadText?.toByteArray(),
            timeStamp = timeStamp,
        )
    }
}