package com.alekso.dltparser.dlt


data class StandardHeader(
    val headerType: HeaderType,
    val messageCounter: UByte,
    val length: Int,
    val ecuId: String?,
    val sessionId: Int?,
    val timeStamp: Int?,
) {

    fun getSize(): Int {
        var size = 4
        if (headerType.withEcuId) size += 4
        if (headerType.withSessionId) size += 4
        if (headerType.withTimestamp) size += 4
        return size
    }

    data class HeaderType(
        val originalByte: Byte,
        val useExtendedHeader: Boolean,
        val payloadLittleEndian: Boolean,
        val withEcuId: Boolean,
        val withSessionId: Boolean,
        val withTimestamp: Boolean,
        val versionNumber: Byte // 3 bits
    ) {
    }
}