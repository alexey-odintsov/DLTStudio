package alexey.odintsov.dltmessage


data class StructuredDLTMessage(
    override val timeStampUs: Long,
    override val standardHeader: alexey.odintsov.dltmessage.standardheader.StandardHeader,
    override val extendedHeader: alexey.odintsov.dltmessage.extendedheader.ExtendedHeader?,
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