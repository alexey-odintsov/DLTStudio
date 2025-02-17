package com.alekso.dltmessage

import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.standardheader.StandardHeader

enum class PayloadStorageType {
    Structured,
    Plain,
    Binary,
}

/**
 * DLT message simplified representations.
 * https://github.com/esrlabs/dlt-core
 */
abstract class DLTMessage (
    open val timeStampNano: Long,
    open val standardHeader: StandardHeader,
    open val extendedHeader: ExtendedHeader?,
) {
    /**
     * Payload textual representation - is used by search and timeline parsing.
     */
    abstract fun payloadText(): String

    /**
     * Payload raw bytes - can be used to parse binary data
     */
    abstract fun payloadBytes(): ByteArray?

}
