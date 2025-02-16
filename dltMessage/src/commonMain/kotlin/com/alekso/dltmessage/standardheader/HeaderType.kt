package com.alekso.dltmessage.standardheader

data class HeaderType(
    val originalByte: Byte,
    val useExtendedHeader: Boolean,
    val payloadBigEndian: Boolean,
    val withEcuId: Boolean,
    val withSessionId: Boolean,
    val withTimestamp: Boolean,
    val versionNumber: Byte // 3 bits
)