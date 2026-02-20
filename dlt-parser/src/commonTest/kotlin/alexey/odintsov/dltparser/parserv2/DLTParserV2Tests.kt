package alexey.odintsov.dltparser.parserv2

import alexey.odintsov.dltmessage.PayloadStorageType
import alexey.odintsov.dltparser.DLTParserV2
import alexey.odintsov.dltparser.ParserInputStream
import alexey.odintsov.dltparser.testdata.TestData
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class DLTParserV2Tests {
    val payloadStorageType = PayloadStorageType.Plain
    val parser = DLTParserV2()

    @Test
    fun `DLTParserV2 parse DLT_MESSAGE_1`() {
        val data = TestData.DLT_MESSAGE_1.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = parser.parseDLTMessage(pis, 0, payloadStorageType).first
        val expected = TestData.DLT_MESSAGE_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Test
    fun `DLTParserV2 parse DLT_MESSAGE_2`() {
        val data = TestData.DLT_MESSAGE_2.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = parser.parseDLTMessage(pis, 0, payloadStorageType).first
        val expected = TestData.DLT_MESSAGE_2_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Ignore
    @Test
    fun `DLTParserV2 parse DLT_MESSAGE_BROKEN_1`() {
        val data = TestData.DLT_MESSAGE_BROKEN_1.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = parser.parseDLTMessage(pis, 0, payloadStorageType).first
        val expected = TestData.DLT_MESSAGE_BROKEN_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }
}