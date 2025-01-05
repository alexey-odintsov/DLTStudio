package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo

/**
 * DLTMessage implementation that holds only basic headers information but raw payload.
 */
class PlainDLTMessage(
    override val sizeBytes: Int,
    override val timeStampNano: Long,
    override val messageType: MessageType?,
    override val messageTypeInfo: MessageTypeInfo?,
    override val ecuId: String?,
    override val applicationId: String?,
    override val contextId: String?,
    override val sessionId: Int?,
    override val payloadText: String?,
    override val payload: ByteArray?,
    override val timeStamp: UInt?
) : DLTMessage {

}