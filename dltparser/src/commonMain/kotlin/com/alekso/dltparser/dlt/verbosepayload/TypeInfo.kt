package com.alekso.dltparser.dlt.verbosepayload

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
    val stringCoding: StringCoding = StringCoding.ASCII,
) {
    enum class StringCoding {
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