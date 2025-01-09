package com.alekso.dltparser.dlt

import com.alekso.dltparser.Endian
import com.alekso.dltparser.dlt.extendedheader.MessageInfo
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.verbosepayload.VerbosePayload

class BinaryDLTMessage(
    override val timeStampNano: Long,
    override val messageType: MessageType?,
    override val messageTypeInfo: MessageTypeInfo?,
    override val ecuId: String?,
    override val applicationId: String?,
    override val contextId: String?,
    override val sessionId: Int?,
    override val timeStamp: UInt?,
    override val payload: ByteArray? = null,
    val payloadEndian: Endian,
    val messageInfo: MessageInfo? = null,
    val argumentsCount: UByte? = 0U,
) : DLTMessage {
    override val payloadText: String?
        get() = if (payload != null) parsePayload(payload) else ""

    fun parsePayload(payload: ByteArray): String {
        val payloadString = StringBuilder()
        if (messageInfo?.verbose == true && argumentsCount != null) {
            val payload = VerbosePayload.parse(payload, argumentsCount.toInt(), payloadEndian)
            return payload.asText()

        } else if (messageInfo?.messageType == MessageType.DLT_TYPE_CONTROL) {
//            val messageId: Int = payload.readInt(0, payloadEndian)
//            var response: Int? = null
//            var payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
//            if (messageInfo.messageTypeInfo == MessageTypeInfo.DLT_CONTROL_RESPONSE && (payloadSize - payloadOffset) > 0) {
//                response = stream.readByte().toInt()
//                payloadOffset += ControlMessagePayload.CONTROL_MESSAGE_RESPONSE_SIZE_BYTES
//            }
//            payloadString.append(
//                ControlMessagePayload(
//                    messageId,
//                    response,
//                    stream.readNBytes(payloadSize - payloadOffset)
//                ).asText()
//            )
            return String(payload)
        } else {
//            val messageId: UInt = payload.readInt(0, Endian.LITTLE).toUInt()
//            val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES
//
//            payloadString.append(
//                NonVerbosePayload(
//                    messageId,
//                    stream.readNBytes(payloadSize - payloadOffset)
//                ).asText()
//            )
            return String(payload)
        }
        return payloadString.toString()
    }

}