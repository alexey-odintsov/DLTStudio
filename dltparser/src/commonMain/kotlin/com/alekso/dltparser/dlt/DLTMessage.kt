package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.standardheader.StandardHeader

interface DLTMessage {
    val sizeBytes: Int
    val timeStampNano: Long
    val messageType: MessageType?
    val messageTypeInfo: MessageTypeInfo?
    val ecuId: String?
    val applicationId: String?
    val contextId: String?
    val sessionId: Int?
    val payloadText: String?
    val payload: ByteArray?
    val timeStamp: UInt?
}

/**
 * https://github.com/esrlabs/dlt-core
 */
data class StringDLTMessage(
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
                " '$payload'}\n" +
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
}