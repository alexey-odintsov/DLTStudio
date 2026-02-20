package alexey.odintsov.dltmessage


enum class PayloadStorageType(val id: Int) {
    Structured(0),
    Plain(1),
    Binary(2),
}

/**
 * DLT message simplified representations.
 * https://github.com/esrlabs/dlt-core
 */
abstract class DLTMessage (
    open val timeStampUs: Long,
    open val standardHeader: alexey.odintsov.dltmessage.standardheader.StandardHeader,
    open val extendedHeader: alexey.odintsov.dltmessage.extendedheader.ExtendedHeader?,
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
