package com.alekso.dltparser.tests

import com.alekso.dltparser.Endian
import com.alekso.dltparser.readInt
import com.alekso.dltparser.readUShort
import org.junit.Assert
import org.junit.Test

class ByteArrayConversionTests {

    @Test
    fun testUIntL() {
        val data = listOf(0x9b, 0x13, 0x00, 0x00).map { it.toByte() }.toByteArray()
        val actual = data.readInt(0, Endian.LITTLE).toUInt()
        val expected = 5019U
        Assert.assertTrue("$actual != $expected", actual == expected)
    }

    @Test
    fun testUShortB() {
        val data = listOf(0x00, 0xa4, 0x00, 0x00).map { it.toByte() }.toByteArray()
        val actual = data.readUShort(0, Endian.BIG)
        val expected = 164U.toUShort()
        Assert.assertTrue("$actual != $expected", actual == expected)
    }
}