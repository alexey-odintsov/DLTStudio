package com.alekso.dltparser.dlt

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
            if (it.typeInfo.typeString) {
                result += String(it.payload)
            } else {
                result += "[${it.payload.toHex()}]"
            }
        }
//        arguments.forEachIndexed { index, it ->
//            result += if (index > 0) "\n" else ""
//            result += "${index}: "
//            if (it.typeInfo.typeString) {
//                result += String(it.payload)
//            } else {
//                result += "[${it.payload.toHex()}]"
//            }
//        }
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

    }

    data class TypeInfo(
        val typeLengthBits: Int,
        val typeBool: Boolean,
        val typeSigned: Boolean,
        val typeUnsigned: Boolean,
        val typeFloat: Boolean,
        val typeArray: Boolean,
        val typeRaw: Boolean,
        val typeString: Boolean,
        val variableInfo: Boolean,
        val fixedPoint: Boolean,
        val traceInfo: Boolean,
        val typeStruct: Boolean,
        val stringCoding: STRING_CODING,
    ) {
        enum class STRING_CODING {
            ASCII,
            UTF8,
            RESERVED
        }

        override fun toString(): String {
            val result = "len: $typeLengthBits bits (${typeLengthBits / 8} bytes); "
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
            return "$result; $typeList; $stringCoding"
        }
    }
}