package alexey.odintsov.dltmessage

import alexey.odintsov.dltmessage.extendedheader.ExtendedHeader
import alexey.odintsov.dltmessage.extendedheader.MessageInfo
import alexey.odintsov.dltmessage.extendedheader.MessageType
import alexey.odintsov.dltmessage.extendedheader.MessageTypeInfo
import alexey.odintsov.dltmessage.standardheader.HeaderType
import alexey.odintsov.dltmessage.standardheader.StandardHeader

object SampleData {
    fun getSampleDltMessages(size: Int): List<DLTMessage> {
        val list = mutableListOf<DLTMessage>()

        for (i in 0..size) {
            val dltMessage = create(
                timeStampUs = System.currentTimeMillis() * 1000,
                payloadText = "Test message $i"
            )
            list.add(dltMessage)
        }

        return list
    }

    fun create(
        timeStampUs: Long = System.currentTimeMillis() * 1000,
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
            timeStampUs = timeStampUs,
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
                sessionId = sessionId,
                ecuId = ecuId,
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