package com.alekso.dltparser.dlt.extendedheader

private const val EXTENDED_HEADER_SIZE = 10

data class ExtendedHeader(
    val messageInfo: MessageInfo,
    val argumentsCount: UByte,
    val applicationId: String,
    val contextId: String,
) {

    fun getSize(): Int = EXTENDED_HEADER_SIZE
}