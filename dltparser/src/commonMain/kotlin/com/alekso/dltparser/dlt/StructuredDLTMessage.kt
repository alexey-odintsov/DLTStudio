package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.standardheader.StandardHeader


class StructuredDLTMessage(
    // DLT Signature is skipped for memory consumption reason and should always be the same
    /**
     * Compounded of timeStampSec and timeStampUs
     */
    override val timeStampNano: Long,
    override val ecuId: String,
    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
    override val payload: ByteArray?,
    // meta info
    override val sizeBytes: Int,
) : DLTMessage {

    override fun toString(): String {
        return "{$timeStampNano, '$ecuId'\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " '$payloadText'}\n" +
                " meta size bytes: $sizeBytes"
    }

    override val messageType: MessageType?
        get() = extendedHeader?.messageInfo?.messageType
    override val messageTypeInfo: MessageTypeInfo?
        get() = extendedHeader?.messageInfo?.messageTypeInfo
    override val applicationId: String?
        get() = standardHeader.ecuId
    override val contextId: String?
        get() = extendedHeader?.contextId
    override val sessionId: Int?
        get() = standardHeader.sessionId
    override val payloadText: String?
        get() = String(payload ?: byteArrayOf())
    override val timeStamp: UInt?
        get() = standardHeader.timeStamp

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StructuredDLTMessage

        if (timeStampNano != other.timeStampNano) return false
        if (sizeBytes != other.sizeBytes) return false
        if (ecuId != other.ecuId) return false
        if (standardHeader != other.standardHeader) return false
        if (extendedHeader != other.extendedHeader) return false
        if (!payload.contentEquals(other.payload)) return false
        if (sessionId != other.sessionId) return false
        if (messageType != other.messageType) return false
        if (messageTypeInfo != other.messageTypeInfo) return false
        if (applicationId != other.applicationId) return false
        if (contextId != other.contextId) return false
        if (payloadText != other.payloadText) return false
        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeStampNano.hashCode()
        result = 31 * result + sizeBytes
        result = 31 * result + ecuId.hashCode()
        result = 31 * result + standardHeader.hashCode()
        result = 31 * result + (extendedHeader?.hashCode() ?: 0)
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        result = 31 * result + (sessionId ?: 0)
        result = 31 * result + (messageType?.hashCode() ?: 0)
        result = 31 * result + (messageTypeInfo?.hashCode() ?: 0)
        result = 31 * result + (applicationId?.hashCode() ?: 0)
        result = 31 * result + (contextId?.hashCode() ?: 0)
        result = 31 * result + (timeStamp?.hashCode() ?: 0)
        return result
    }
}