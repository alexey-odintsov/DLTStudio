package com.alekso.dltparser.dlt

interface Payload {
    fun getSize(): Int
    fun asText(): String
}