package com.alekso.dltparser.dlt


/**
 * https://github.com/esrlabs/dlt-core
 */
data class DLTMessage(
    // DLT Signature is skipped for memory consumption reason and should always be the same
    val timeStampNano: Long,
    /**
     * Compounded of timeStampSec and timeStampUs
     */
    val ecuId: String,

    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
    val payload: Payload?,
    // meta info
    val sizeBytes: Int,
) {

    fun getTimeStamp(): Long {
        return timeStampNano / 1000
    }

    override fun toString(): String {
        return "{$timeStampNano, '$ecuId'\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " $payload}\n" +
                " meta size bytes: $sizeBytes"
    }
}