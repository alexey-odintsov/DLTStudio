package com.alekso.dltmessage

interface Payload {
    fun getSize(): Int
    fun asText(): String
}