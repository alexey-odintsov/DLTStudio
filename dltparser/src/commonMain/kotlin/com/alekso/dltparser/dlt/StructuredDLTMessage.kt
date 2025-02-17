package com.alekso.dltparser.dlt

import com.alekso.dltmessage.Payload
import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.standardheader.StandardHeader


data class StructuredDLTMessage(
    override val timeStampNano: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: Payload?,
) : DLTMessage(timeStampNano, standardHeader, extendedHeader) {

    override fun toString(): String {
        return "{$timeStampNano\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " '${payload?.asText()}'}"
    }

    override fun payloadText(): String = payload?.asText() ?: ""

    override fun payloadBytes(): ByteArray? = null


}