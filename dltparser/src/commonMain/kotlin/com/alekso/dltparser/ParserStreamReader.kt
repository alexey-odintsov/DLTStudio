package com.alekso.dltparser

import java.io.DataInput
import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream

class ParserInputStream(inputStream: InputStream): DataInputStream(inputStream), DataInput {
    fun readIntLittle(): Int {
        val var1 = `in`.read()
        val var2 = `in`.read()
        val var3 = `in`.read()
        val var4 = `in`.read()
        if ((var1 or var2 or var3 or var4) < 0) {
            throw EOFException()
        } else {
            return (var4 shl 24) + (var3 shl 16) + (var2 shl 8) + (var1 shl 0)
        }
    }

    fun readUnsignedShortLittle(): Int {
        val var1 = `in`.read()
        val var2 = `in`.read()
        if ((var1 or var2) < 0) {
            throw EOFException()
        } else {
            return ((var2 shl 8) + (var1 shl 0)).toShort().toInt()
        }
    }
}