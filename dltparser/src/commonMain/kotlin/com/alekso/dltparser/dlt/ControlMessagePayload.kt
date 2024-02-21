package com.alekso.dltparser.dlt

import com.alekso.dltparser.toHex

data class ControlMessagePayload(
    val messageId: UByte,
    val data: ByteArray,
) : Payload {

    override fun getSize(): Int = data.size + CONTROL_MESSAGE_ID_SIZE_BYTES

    override fun asText(): String {
        return "[$messageId: ${messageIdsMap[messageId]}] ${String(data)} | ${data.toHex()}"
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
        const val CONTROL_MESSAGE_ID_SIZE_BYTES = 1

        val messageIdsMap = mapOf<UByte, String>(
            0x01.toUByte() to "set_log_level",
            0x02.toUByte() to "set_trace_status",
            0x03.toUByte() to "get_log_info",
            0x04.toUByte() to "get_default_log_level",
            0x05.toUByte() to "store_configuration",
            0x06.toUByte() to "restore_to_factory_default",
            0x07.toUByte() to "set_com_interface_status",
            0x08.toUByte() to "set_com_interface_max_bandwidth",
            0x09.toUByte() to "set_verbose_mode",
            0x10.toUByte() to "set_use_extended_header",
            0x0A.toUByte() to "set_message_filtering",
            0x0B.toUByte() to "set_timing_packets",
            0x0C.toUByte() to "get_local_time",
            0x0D.toUByte() to "set_use_ecuid",
            0x0E.toUByte() to "set_use_session_id",
            0x0F.toUByte() to "set_use_timestamp",
            0x11.toUByte() to "set_default_log_level",
            0x12.toUByte() to "set_default_trace_status",
            0x13.toUByte() to "get_software_version",
            0x14.toUByte() to "message_buffer_overflow",
            0x15.toUByte() to "get_default_trace_status",
            0x16.toUByte() to "get_com_interfacel_status",
            0x17.toUByte() to "get_log_channel_names",
            0x18.toUByte() to "get_com_interface_max_bandwidth",
            0x19.toUByte() to "get_verbose_mode_status",
            0x1A.toUByte() to "get_message_filtering_status",
            0x1B.toUByte() to "get_use_ecuid",
            0x1C.toUByte() to "get_use_session_id",
            0x1D.toUByte() to "get_use_timestamp",
            0x1E.toUByte() to "get_use_extended_header",
            0x1F.toUByte() to "get_trace_status",
            0x20.toUByte() to "set_log_channel_assignment",
            0x21.toUByte() to "set_log_channel_threshold",
            0x22.toUByte() to "get_log_channel_threshold",
            0x23.toUByte() to "buffer_overflow_notification",
        )
    }
}