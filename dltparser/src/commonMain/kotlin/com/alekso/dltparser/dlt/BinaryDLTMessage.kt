package com.alekso.dltparser.dlt

import com.alekso.dltparser.Endian
import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.nonverbosepayload.NonVerbosePayload
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltparser.dlt.verbosepayload.VerbosePayload
import com.alekso.dltparser.readInt

data class BinaryDLTMessage(
    override val timeStampNano: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: ByteArray?,
) : DLTMessage(timeStampNano, standardHeader, extendedHeader) {
    override fun payloadText(): String = if (payload != null) parsePayload(payload) else ""

    override fun payloadBytes(): ByteArray? = payload


    fun parsePayload(payload: ByteArray): String {
        val payloadString = StringBuilder()
        val payloadEndian =
            if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE
        if (extendedHeader?.messageInfo?.verbose == true) {
            val payload = VerbosePayload.parse(
                payload,
                extendedHeader.argumentsCount.toInt(),
                payloadEndian
            )
            return payload.asText()

        } else if (extendedHeader?.messageInfo?.messageType == MessageType.DLT_TYPE_CONTROL) {
            val messageId: Int = payload.readInt(0, payloadEndian)
            var response: Int? = null
            var payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
            if (extendedHeader.messageInfo.messageTypeInfo == MessageTypeInfo.DLT_CONTROL_RESPONSE) {
                response = payload[4].toInt()
                payloadOffset += ControlMessagePayload.CONTROL_MESSAGE_RESPONSE_SIZE_BYTES
            }
            return ControlMessagePayload(
                messageId,
                response,
                payload.copyOfRange(payloadOffset, payload.size)
            ).asText()
        } else {
            val messageId: UInt = payload.readInt(0, Endian.LITTLE).toUInt()
            val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES

            return NonVerbosePayload(
                messageId,
                payload.copyOfRange(payloadOffset, payload.size)
            ).asText()
        }
        return payloadString.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BinaryDLTMessage

        if (timeStampNano != other.timeStampNano) return false
        if (standardHeader != other.standardHeader) return false
        if (extendedHeader != other.extendedHeader) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeStampNano.hashCode()
        result = 31 * result + standardHeader.hashCode()
        result = 31 * result + (extendedHeader?.hashCode() ?: 0)
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        return result
    }

}