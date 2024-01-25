package com.alekso.dltparser.dlt

class NonVerbosePayload: Payload {
    override fun getSize(): Int {
        return 1
    }

    override fun asText(): String {
        TODO("Not yet implemented")
    }
}