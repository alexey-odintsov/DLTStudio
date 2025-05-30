package com.alekso.dltparser.tests.parserv1

import com.alekso.datautils.Endian
import com.alekso.datautils.readInt
import com.alekso.dltmessage.verbosepayload.Argument
import com.alekso.dltmessage.verbosepayload.TypeInfo
import com.alekso.dltmessage.verbosepayload.VerbosePayload
import org.junit.Assert
import org.junit.Test

class VerbosePayloadParsingTest {

    @Test
    fun `test TypeInfo parsing`() {
        val data = byteArrayOf(0x00, 0x02, 0x00, 0x00)
        val expected = TypeInfo(typeString = true)
        val actual = TypeInfo.parseVerbosePayloadTypeInfo(
            true,
            data.readInt(0, Endian.LITTLE),
            Endian.LITTLE
        )
        Assert.assertTrue("actual  : $actual\nexpected: $expected", actual == expected)
    }

    @Test
    fun `test TypeInfo parsing 2`() {
        val data = byteArrayOf(0x43, 0x00, 0x00, 0x00)
        val expected = TypeInfo(typeLengthBits = 32, typeUnsigned = true)
        val actual = TypeInfo.parseVerbosePayloadTypeInfo(
            true,
            data.readInt(0, Endian.LITTLE),
            Endian.LITTLE
        )
        Assert.assertTrue("actual  : $actual\nexpected:$expected", actual == expected)
    }


    @Test
    fun parseVerbosePayload1() {
        // @formatter:off
        val raw = listOf(
        0x00, 0x02, 0x00 , 0x00 , 0xa4 , 0x00 , 0x4f , 0x6e  , 0x6c , 0x69 , 0x6e , 0x65 , 0x43 , 0x61 , 0x6c , 0x69,
        0x62, 0x72, 0x61 , 0x74 , 0x69 , 0x6f , 0x6e , 0x2e  , 0x63 , 0x70 , 0x70 , 0x20 , 0x6f , 0x6e , 0x4c , 0x6f,
        0x67, 0x3a, 0x31 , 0x31 , 0x34 , 0x20 , 0x5b , 0x46  , 0x52 , 0x41 , 0x4d , 0x45 , 0x2d , 0x49 , 0x4e , 0x46,
        0x4f, 0x5d, 0x20 , 0x53 , 0x69 , 0x67 , 0x6e , 0x61  , 0x6c , 0x73 , 0x20 , 0x6e , 0x6f , 0x74 , 0x20 , 0x69,
        0x6e, 0x20, 0x74 , 0x68 , 0x72 , 0x65 , 0x73 , 0x68  , 0x6f , 0x6c , 0x64 , 0x2c , 0x20 , 0x54 , 0x69 , 0x6d,
        0x65, 0x73, 0x74 , 0x61 , 0x6d , 0x70 , 0x3a , 0x20  , 0x33 , 0x31 , 0x30 , 0x33 , 0x31 , 0x38 , 0x34 , 0x2c,
        0x20, 0x50, 0x69 , 0x74 , 0x63 , 0x68 , 0x52 , 0x61  , 0x74 , 0x65 , 0x3a , 0x20 , 0x30 , 0x2c , 0x20 , 0x59,
        0x61, 0x77, 0x52 , 0x61 , 0x74 , 0x65 , 0x3a , 0x20  , 0x34 , 0x2e , 0x39 , 0x38 , 0x35 , 0x2c , 0x20 , 0x52,
        0x6f, 0x6c, 0x6c , 0x52 , 0x61 , 0x74 , 0x65 , 0x3a  , 0x20 , 0x30 , 0x2c , 0x20 , 0x53 , 0x70 , 0x65 , 0x65,
        0x64, 0x3a, 0x20 , 0x32 , 0x32 , 0x2e , 0x37 , 0x39  , 0x36 , 0x39 , 0x2c , 0x20 , 0x4d , 0x69 , 0x6c , 0x65,
        0x61, 0x67, 0x65 , 0x3a , 0x20 , 0x4e , 0x2f , 0x41  , 0x0a , 0x00
        )
        // @formatter:on

        // @formatter:on
        val data = raw.map { it.toByte() }.toByteArray()
        val actual = VerbosePayload.parseVerbosePayload(0, data, Endian.LITTLE)
        val expected = Argument(
            0x00000200,
            TypeInfo(
                typeString = true
            ),
            6,
            164,
            "OnlineCalibration.cpp onLog:114 [FRAME-INFO] Signals not in threshold, Timestamp: 3103184, PitchRate: 0, YawRate: 4.985, RollRate: 0, Speed: 22.7969, Mileage: N/A${0x0a.toChar()}${0x00.toChar()}".toByteArray()
        )
        Assert.assertTrue("actual  : $actual\nexpected: $expected", actual == expected)
    }
}