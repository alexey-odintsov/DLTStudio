package alexey.odintsov.dltmessage


/**
 * DLTMessage implementation that holds only basic headers information but raw payload.
 */
data class PlainDLTMessage(
    override val timeStampUs: Long,
    override val standardHeader: alexey.odintsov.dltmessage.standardheader.StandardHeader,
    override val extendedHeader: alexey.odintsov.dltmessage.extendedheader.ExtendedHeader?,
    val payload: String?,
) : DLTMessage(timeStampUs, standardHeader, extendedHeader) {
    override fun payloadText(): String = payload ?: ""

    override fun payloadBytes(): ByteArray? = null
}