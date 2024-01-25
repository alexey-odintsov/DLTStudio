package com.alekso.dltparser.dlt

import com.alekso.dltparser.DLTParser.parseMessageInfo


data class ExtendedHeader(
    val messageInfo: MessageInfo,
    val argumentsCount: Int,
    val applicationId: String,
    val contextId: String,
) {

    fun getSize(): Int = 10
}