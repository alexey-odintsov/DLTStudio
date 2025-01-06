package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo

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
) : DLTMessage {
    override val payloadText: String?
        get() = if (payload != null) String(payload) else ""
}