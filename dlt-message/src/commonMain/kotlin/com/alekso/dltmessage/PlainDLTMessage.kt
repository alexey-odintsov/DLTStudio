package com.alekso.dltmessage

import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.standardheader.StandardHeader

/**
 * DLTMessage implementation that holds only basic headers information but raw payload.
 */
data class PlainDLTMessage(
    override val timeStampUs: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: String?,
) : DLTMessage(timeStampUs, standardHeader, extendedHeader) {
    override fun payloadText(): String = payload ?: ""

    override fun payloadBytes(): ByteArray? = null
}