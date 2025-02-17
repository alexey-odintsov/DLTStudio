package com.alekso.dltstudio.utils

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltmessage.PlainDLTMessage
import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.extendedheader.MessageInfo
import com.alekso.dltmessage.extendedheader.MessageType
import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltmessage.standardheader.HeaderType
import com.alekso.dltmessage.standardheader.StandardHeader

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
        messageType: MessageType = MessageType.DLT_TYPE_LOG,
        messageTypeInfo: MessageTypeInfo = MessageTypeInfo.DLT_LOG_DEBUG,
        ecuId: String? = "ECU1",
        applicationId: String = "APP1",
        contextId: String = "CTX1",
        sessionId: Int? = 1,
        payloadText: String?,
        timeStamp: UInt? = 1U
    ): DLTMessage {
        return PlainDLTMessage(
            timeStampNano = timeStampNano,
            standardHeader = StandardHeader(
                HeaderType(
                    originalByte = 0.toByte(),
                    useExtendedHeader = true,
                    payloadBigEndian = true,
                    withEcuId = ecuId != null,
                    withSessionId = sessionId != null,
                    withTimestamp = timeStamp != null,
                    versionNumber = 1.toByte(),
                ),
                messageCounter = 1U,
                length = 1U
            ),
            extendedHeader = ExtendedHeader(
                messageInfo = MessageInfo(
                    originalByte = 0.toByte(),
                    verbose = true,
                    messageType = messageType,
                    messageTypeInfo = messageTypeInfo
                ),
                argumentsCount = 1U,
                applicationId = applicationId,
                contextId = contextId,
            ),
            payload = payloadText,
        )
    }
}