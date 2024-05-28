package com.alekso.dltparser.tests

import com.alekso.dltparser.isBitSet
import org.junit.Assert
import org.junit.Test

class BitwiseOperationsTests {

    @Test
    fun `Int isBitSet test 1`() {
        val data = 0b10000000_00000000_00000000_00000000.toInt()
        val actual = data.isBitSet(0)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Int isBitSet test 2`() {
        val data = 0b00000001000000001000000010000000
        val actual = data.isBitSet(7)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Int isBitSet test 3`() {
        val data = 0b10000001000000001000000010000001.toInt()
        val actual = data.isBitSet(31)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Int isBitSet test 4`() {
        val data = 0b10000001000000001000000010000100.toInt()
        val actual = data.isBitSet(3)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Int isBitSet test 5`() {
        val data = 0b00000001000000001000000010000000
        val actual = data.isBitSet(31)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Byte isBitSet test 1`() {
        val data = 0b00000001.toByte()
        val actual = data.isBitSet(0)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Byte isBitSet test 2`() {
        val data = 0b00010001.toByte()
        val actual = data.isBitSet(4)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Byte isBitSet test 3`() {
        val data = 0b10010001.toByte()
        val actual = data.isBitSet(1)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Byte isBitSet test 4`() {
        val data = 0b00010000.toByte()
        val actual = data.isBitSet(0)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Short isBitSet test 1`() {
        val data = 0b00010001_01100011.toShort()
        val actual = data.isBitSet(0)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Short isBitSet test 2`() {
        val data = 0b00010001_01100011.toShort()
        val actual = data.isBitSet(12)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Short isBitSet test 3`() {
        val data = 0b10010001_01100011.toShort()
        val actual = data.isBitSet(15)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Short isBitSet test 4`() {
        val data = 0b10010001_01100011.toShort()
        val actual = data.isBitSet(0)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Long isBitSet test 1`() {
        val data = 0b00000001000000001000000010000000_00000001000000001000000010000000
        val actual = data.isBitSet(0)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Long isBitSet test 2`() {
        val data = 0b00000001000000001000000010000000_00000001000000001000000010000000
        val actual = data.isBitSet(63)
        Assert.assertTrue(!actual)
    }

    @Test
    fun `Long isBitSet test 3`() {
        val data = 0x4000000000000000 shl 1
        val actual = data.isBitSet(63)
        Assert.assertTrue(actual)
    }

    @Test
    fun `Long isBitSet test 4`() {
        val data = 3L
        val actual = data.isBitSet(1)
        Assert.assertTrue(actual)
    }

}