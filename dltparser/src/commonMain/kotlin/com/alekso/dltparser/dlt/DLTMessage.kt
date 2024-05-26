package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.standardheader.StandardHeader


/**
 * https://github.com/esrlabs/dlt-core
 */
data class DLTMessage(
    // DLT Signature is skipped for memory consumption reason and should always be the same
    /**
     * Compounded of timeStampSec and timeStampUs
     */
    val timeStampNano: Long,
    val ecuId: String,
    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
    val payload: String,
    // meta info
    val sizeBytes: Int,
) {

    override fun toString(): String {
        return "{$timeStampNano, '$ecuId'\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " $payload}\n" +
                " meta size bytes: $sizeBytes"
    }
}