package com.alekso.dltparser.dlt


data class ExtendedHeader(
    val messageInfo: MessageInfo,
    val argumentsCount: UByte,
    val applicationId: String,
    val contextId: String,
) {

    fun getSize(): Int = 10
}