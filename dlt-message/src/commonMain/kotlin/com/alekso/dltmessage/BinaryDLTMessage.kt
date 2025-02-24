package com.alekso.dltmessage

import com.alekso.datautils.Endian
import com.alekso.datautils.readInt
import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.extendedheader.MessageType
import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltmessage.nonverbosepayload.NonVerbosePayload
import com.alekso.dltmessage.standardheader.StandardHeader
import com.alekso.dltmessage.verbosepayload.VerbosePayload

data class BinaryDLTMessage(
    override val timeStampUs: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: ByteArray?,
) : DLTMessage(timeStampUs, standardHeader, extendedHeader) {
    override fun payloadText(): String = if (payload != null) parsePayload(payload) else ""

    override fun payloadBytes(): ByteArray? = payload


    fun parsePayload(payloadBytes: ByteArray): String {
        try {
            val payloadEndian =
                if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE
            if (extendedHeader?.messageInfo?.verbose == true) {
                val payload = VerbosePayload.parse(
                    payloadBytes,
                    extendedHeader.argumentsCount.toInt(),
                    payloadEndian
                )
                return payload.asText()

            } else if (extendedHeader?.messageInfo?.messageType == MessageType.DLT_TYPE_CONTROL) {
                val messageId: Int = payloadBytes.readInt(0, payloadEndian)
                var response: Int? = null
                var payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
                if (extendedHeader.messageInfo.messageTypeInfo == MessageTypeInfo.DLT_CONTROL_RESPONSE) {
                    response = payloadBytes[4].toInt()
                    payloadOffset += ControlMessagePayload.CONTROL_MESSAGE_RESPONSE_SIZE_BYTES
                }
                return ControlMessagePayload(
                    messageId,
                    response,
                    payloadBytes.copyOfRange(payloadOffset, payloadBytes.size)
                ).asText()
            } else {
                val messageId: UInt = payloadBytes.readInt(0, Endian.LITTLE).toUInt()
                val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES

                return NonVerbosePayload(
                    messageId,
                    payloadBytes.copyOfRange(payloadOffset, payloadBytes.size)
                ).asText()
            }
        } catch (e: Exception) {
            return "EXCEPTION PARSING PAYLOAD: $e"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BinaryDLTMessage

        if (timeStampUs != other.timeStampUs) return false
        if (standardHeader != other.standardHeader) return false
        if (extendedHeader != other.extendedHeader) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeStampUs.hashCode()
        result = 31 * result + standardHeader.hashCode()
        result = 31 * result + (extendedHeader?.hashCode() ?: 0)
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        return result
    }

}