package alexey.odintsov.dltmessage

interface Payload {
    fun getSize(): Int
    fun asText(): String
}