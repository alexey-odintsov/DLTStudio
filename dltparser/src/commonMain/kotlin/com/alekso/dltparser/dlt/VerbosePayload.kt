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
            return 4 + additionalSize + payload.size
        }

        fun getPayloadAsText(): String {
            return when {
                typeInfo.typeString -> String(payload).replace("\u0000", "")
                typeInfo.typeUnsigned -> {
                    when (typeInfo.typeLengthBits) {
                        8 -> "${payload[0].toUInt()}"
                        16 -> "${payload.readShort(0, Endian.LITTLE).toUShort()}"
                        else -> try {
                            "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                        } catch (e: Exception) {
                            e.toString()
                        }
                    }
                }

                typeInfo.typeSigned ->
                    when (typeInfo.typeLengthBits) {
                        8 -> "${payload[0].toInt()}"
                        16 -> "${payload.readShort(0, Endian.LITTLE).toInt()}"
                        else -> try {
                            "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                        } catch (e: Exception) {
                            e.toString()
                        }
                    }

                typeInfo.typeBool -> if (payload[0] == 0.toByte()) "FALSE" else "TRUE"
                else -> payload.toHex() // TODO: Add other types
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Argument

            if (typeInfoInt != other.typeInfoInt) return false
            if (typeInfo != other.typeInfo) return false
            if (additionalSize != other.additionalSize) return false
            if (payloadSize != other.payloadSize) return false
            return payload.contentEquals(other.payload)
        }

        override fun hashCode(): Int {
            var result = typeInfoInt
            result = 31 * result + typeInfo.hashCode()
            result = 31 * result + additionalSize
            result = 31 * result + payloadSize
            result = 31 * result + payload.contentHashCode()
            return result
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