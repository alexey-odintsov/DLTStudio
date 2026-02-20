package alexey.odintsov.dltmessage

import alexey.odintsov.dltmessage.extendedheader.ExtendedHeader
import alexey.odintsov.dltmessage.standardheader.StandardHeader


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