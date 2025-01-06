package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo

enum class DLTStorageType {
    Structured,
    Plain,
    Binary,
}

/**
 * DLT message simplified representations.
 * https://github.com/esrlabs/dlt-core
 */
interface DLTMessage {
    /**
     * Internal metadata for parsers.
     * todo: try to remove it
     */
    val sizeBytes: Int

    /**
     * Message timestamp in nanoseconds
     */
    val timeStampNano: Long
    val messageType: MessageType?
    val messageTypeInfo: MessageTypeInfo?
    val ecuId: String?
    val applicationId: String?
    val contextId: String?
    val sessionId: Int?

    /**
     * Payload textual representation - is used by search and timeline parsing.
     */
    val payloadText: String?

    /**
     * Payload raw bytes - can be used to parse binary data
     */
    val payload: ByteArray?

    /**
     * Time passed since ECU start
     */
    val timeStamp: UInt?
}
