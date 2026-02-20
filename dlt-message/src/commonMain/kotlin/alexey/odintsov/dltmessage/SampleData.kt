package alexey.odintsov.dltmessage

import alexey.odintsov.dltmessage.extendedheader.MessageInfo

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
        messageType: alexey.odintsov.dltmessage.extendedheader.MessageType = alexey.odintsov.dltmessage.extendedheader.MessageType.DLT_TYPE_LOG,
        messageTypeInfo: alexey.odintsov.dltmessage.extendedheader.MessageTypeInfo = alexey.odintsov.dltmessage.extendedheader.MessageTypeInfo.DLT_LOG_DEBUG,
        ecuId: String? = "ECU1",
        applicationId: String = "APP1",
        contextId: String = "CTX1",
        sessionId: Int? = 1,
        payloadText: String?,
        timeStamp: UInt? = 1U
    ): DLTMessage {
        return PlainDLTMessage(
            timeStampUs = timeStampUs,
            standardHeader = _root_ide_package_.alexey.odintsov.dltmessage.standardheader.StandardHeader(
                _root_ide_package_.alexey.odintsov.dltmessage.standardheader.HeaderType(
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
            extendedHeader = _root_ide_package_.alexey.odintsov.dltmessage.extendedheader.ExtendedHeader(
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