package com.alekso.dltparser.dlt

import com.alekso.dltparser.toHex

data class NonVerbosePayload(
    val messageId: UInt,
    val data: ByteArray
) : Payload {
    override fun getSize(): Int {
        return MESSAGE_ID_SIZE_BYTES + data.size
    }

    override fun asText(): String {
        return "[$messageId] ${String(data)} | ${data.toHex()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NonVerbosePayload

        if (messageId != other.messageId) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }

    companion object {
        const val MESSAGE_ID_SIZE_BYTES = 4
    }
}