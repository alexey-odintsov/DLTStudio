package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.standardheader.StandardHeader

/**
 * DLTMessage implementation that holds only basic headers information but raw payload.
 */
data class PlainDLTMessage(
    override val timeStampNano: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: String?,
) : DLTMessage(timeStampNano, standardHeader, extendedHeader) {
    override fun payloadText(): String = payload ?: ""

    override fun payloadBytes(): ByteArray? = null
}