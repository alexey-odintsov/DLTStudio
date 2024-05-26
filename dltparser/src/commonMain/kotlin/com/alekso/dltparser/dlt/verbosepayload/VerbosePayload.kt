package com.alekso.dltparser.dlt.verbosepayload

import com.alekso.dltparser.dlt.Payload


data class VerbosePayload(
    val arguments: List<Argument>
) : Payload {

    val text by lazy {
        arguments.joinToString(" ") { it.getPayloadAsText() }
    }

    override fun getSize(): Int {
        var size = 0
        arguments.forEach { size += it.getSize() }
        return size
    }

    override fun asText(): String = text

}