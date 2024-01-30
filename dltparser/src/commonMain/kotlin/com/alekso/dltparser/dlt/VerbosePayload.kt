package com.alekso.dltparser.dlt

import com.alekso.dltparser.Endian
import com.alekso.dltparser.readInt
import com.alekso.dltparser.readShort
import com.alekso.dltparser.toHex


data class VerbosePayload(
    val arguments: List<Argument>
) : Payload {
    override fun getSize(): Int {
        var size = 0
        arguments.forEach { size += it.getSize() }
        return size
    }

    override fun asText(): String {
        var result = ""
        arguments.forEachIndexed { index, it ->
            result += if (index > 0) " " else ""
            result += it.getPayloadAsText()
        }
        return result
    }

    data class Argument(
        val typeInfoInt: Int,
        val typeInfo: TypeInfo,
        val additionalSize: Int,
        val payloadSize: Int,
        val payload: ByteArray,
    ) {
        fun getSize(): Int {
            return 4 + additionalSize + payloadSize
        }

        fun getPayloadAsText(): String {
            return when {
                typeInfo.typeString -> String(payload)
                typeInfo.typeUnsigned -> {
                    when (typeInfo.typeLengthBits) {
                        8 -> "${payload[0].toUInt()}"
                        16 -> "${payload.readShort(0, Endian.LITTLE).toUShort()}"
                        else -> "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                    }
                }

                typeInfo.typeSigned ->
                    when (typeInfo.typeLengthBits) {
                        8 -> "${payload[0].toInt()}"
                        16 -> "${payload.readShort(0, Endian.LITTLE).toInt()}"
                        else -> "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                    }

                typeInfo.typeBool -> if (payload[0] == 0.toByte()) "FALSE" else "TRUE"
                else -> payload.toHex() // TODO: Add other types
            }
        }

    }

    data class TypeInfo(
        val typeLengthBits: Int = 0,
        val typeBool: Boolean = false,
        val typeSigned: Boolean = false,
        val typeUnsigned: Boolean = false,
        val typeFloat: Boolean = false,
        val typeArray: Boolean = false,
        val typeRaw: Boolean = false,
        val typeString: Boolean = false,
        val variableInfo: Boolean = false,
        val fixedPoint: Boolean = false,
        val traceInfo: Boolean = false,
        val typeStruct: Boolean = false,
        val stringCoding: STRING_CODING = STRING_CODING.ASCII,
    ) {
        enum class STRING_CODING {
            ASCII,
            UTF8,
            RESERVED
        }

        override fun toString(): String {
            val result = "len: $typeLengthBits bits (${typeLengthBits / 8} bytes); "
            return "$result; ${getTypeString()}; $stringCoding"
        }

        fun getTypeString(): String {
            val typeList = mutableListOf<String>()
            if (typeBool) {
                typeList.add("BOOL")
            }
            if (typeSigned) {
                typeList.add("SIGNED")
            }
            if (typeUnsigned) {
                typeList.add("UNSIGNED")
            }
            if (typeFloat) {
                typeList.add("FLOAT")
            }
            if (typeArray) {
                typeList.add("ARRAY")
            }
            if (typeRaw) {
                typeList.add("RAW")
            }
            if (typeString) {
                typeList.add("STRING")
            }
            if (variableInfo) {
                typeList.add("VARIABLE_INFO")
            }
            if (fixedPoint) {
                typeList.add("FIXED_POINT")
            }
            if (traceInfo) {
                typeList.add("TRACE_INFO")
            }
            if (typeStruct) {
                typeList.add("STRUCT")
            }
            return typeList.toString()
        }
    }
}