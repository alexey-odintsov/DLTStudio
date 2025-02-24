package com.alekso.dltmessage

import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.standardheader.StandardHeader


data class StructuredDLTMessage(
    override val timeStampUs: Long,
    override val standardHeader: StandardHeader,
    override val extendedHeader: ExtendedHeader?,
    val payload: Payload?,
) : DLTMessage(timeStampUs, standardHeader, extendedHeader) {

    override fun toString(): String {
        return "{$timeStampUs\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " '${payload?.asText()}'}"
    }

    override fun payloadText(): String = payload?.asText() ?: ""

    override fun payloadBytes(): ByteArray? = null


}