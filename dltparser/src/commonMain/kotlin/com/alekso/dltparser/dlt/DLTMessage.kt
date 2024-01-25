package com.alekso.dltparser.dlt


/**
 * https://github.com/esrlabs/dlt-core
 */
data class DLTMessage(
    val signature: String,
    val timeStampSec: Int,
    val timeStampUs: Int,
    val ecuId: String,

    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
    val payload: Payload?,
) {

    override fun toString(): String {
        return "{'$signature'; $timeStampUs, $timeStampUs, '$ecuId'\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " $payload}\n"
    }
}