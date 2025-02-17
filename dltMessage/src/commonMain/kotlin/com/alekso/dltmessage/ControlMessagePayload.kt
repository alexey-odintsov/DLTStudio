package com.alekso.dltmessage

import com.alekso.datautils.toHex

data class ControlMessagePayload(
    val messageId: Int,
    val response: Int? = null, // for DLT_CONTROL_RESPONSE messages
    val data: ByteArray,
) : Payload {

    override fun getSize(): Int = data.size + CONTROL_MESSAGE_ID_SIZE_BYTES

    override fun asText(): String {
        val messageIdString = messageIdsMap[messageId] ?: "0x${messageId.toHex(2)}"
        val responseString = if (response != null) {
            responseMap[response] ?: "0x${response.toHex(1)}"
        } else ""
        val dataString = if (data.isNotEmpty()) {
            "${String(data)} | ${data.toHex()}"
        } else ""
        return "[${messageIdString}${if (responseString != "") " " else ""}$responseString] $dataString"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ControlMessagePayload

        if (messageId != other.messageId) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = messageId.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        const val CONTROL_MESSAGE_ID_SIZE_BYTES = 4
        const val CONTROL_MESSAGE_RESPONSE_SIZE_BYTES = 1

        val responseMap = mapOf<Int, String>(
            0x00 to "ok",
            0x01 to "not_supported",
            0x02 to "error",
            0x08 to "no_matching_context_id",
        )

        val messageIdsMap = mapOf<Int, String>(
            0x01 to "set_log_level",
            0x02 to "set_trace_status",
            0x03 to "get_log_info",
            0x04 to "get_default_log_level",
            0x05 to "store_configuration",
            0x06 to "restore_to_factory_default",
            0x07 to "set_com_interface_status",
            0x08 to "set_com_interface_max_bandwidth",
            0x09 to "set_verbose_mode",
            0x10 to "set_use_extended_header",
            0x0A to "set_message_filtering",
            0x0B to "set_timing_packets",
            0x0C to "get_local_time",
            0x0D to "set_use_ecuid",
            0x0E to "set_use_session_id",
            0x0F to "set_use_timestamp",
            0x11 to "set_default_log_level",
            0x12 to "set_default_trace_status",
            0x13 to "get_software_version",
            0x14 to "message_buffer_overflow",
            0x15 to "get_default_trace_status",
            0x16 to "get_com_interfacel_status",
            0x17 to "get_log_channel_names",
            0x18 to "get_com_interface_max_bandwidth",
            0x19 to "get_verbose_mode_status",
            0x1A to "get_message_filtering_status",
            0x1B to "get_use_ecuid",
            0x1C to "get_use_session_id",
            0x1D to "get_use_timestamp",
            0x1E to "get_use_extended_header",
            0x1F to "get_trace_status",
            0x20 to "set_log_channel_assignment",
            0x21 to "set_log_channel_threshold",
            0x22 to "get_log_channel_threshold",
            0x23 to "buffer_overflow_notification",

            0x0f00 to "user_service_id",
            0x0f01 to "unregister_context",
            0x0f02 to "connection_info",
            0x0f03 to "timezone",
            0x0f04 to "marker",
            0x0f05 to "offline_logstorage",
            0x0f06 to "passive_node_connect",
            0x0f07 to "passive_node_connection_status",
            0x0f08 to "set_all_log_level",
            0x0f09 to "set_all_trace_status",
        )
    }
}