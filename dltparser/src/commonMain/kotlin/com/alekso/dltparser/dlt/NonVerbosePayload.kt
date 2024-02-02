package com.alekso.dltparser.dlt

import com.alekso.dltparser.toHex

data class NonVerbosePayload(
    val data: ByteArray
) : Payload {
    override fun getSize(): Int {
        return data.size
    }

    override fun asText(): String {
        return data.toHex()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NonVerbosePayload

        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}