import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.Endian
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltparser.readInt
import org.junit.Assert
import org.junit.Test

class VerbosePayloadParsingTest {

    @Test
    fun `test TypeInfo parsing`() {
        val data = byteArrayOf(0x00, 0x02, 0x00, 0x00)
        val expected = VerbosePayload.TypeInfo(typeString = true)
        val actual = DLTParser.parseVerbosePayloadTypeInfo(true, data.readInt(0, Endian.LITTLE), Endian.LITTLE)
        Assert.assertTrue("actual  : $actual\nexpected: $expected", actual == expected)
    }

//    @Test
//    fun `test TypeInfo parsing 2`() {
//        val data = byteArrayOf(0x43, 0x00, 0x00, 0x00)
//        val expected = VerbosePayload.TypeInfo(typeUnsigned = true)
//        val actual = DLTParser.parseVerbosePayloadTypeInfo(true, data.readInt(0, Endian.LITTLE), Endian.LITTLE)
//        Assert.assertTrue("actual  : $actual\nexpected:$expected", actual == expected)
//    }


    @Test
    fun parseVerbosePayload1() {
        // @formatter:off
        val data = byteArrayOf(
            // typeInfoInt
            0x00, 0x02, 0x00, 0x00,
            // payloadSize
            0x3f, 0x00,
            // Payload
            0x50, 0x61, 0x67, 0x65, 0x20, 0x66, 0x6c, 0x69, 0x70, 0x20, 0x65, 0x6e, 0x71, 0x75, 0x65,
            0x75, 0x65, 0x64, 0x20, 0x6f, 0x6e, 0x20, 0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x6f,
            0x72, 0x20, 0x32, 0x36, 0x30, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x68, 0x61, 0x6e, 0x64,
            0x6c, 0x65, 0x72, 0x20, 0x30, 0x78, 0x37, 0x33, 0x64, 0x38, 0x30, 0x30, 0x35, 0x37, 0x34,
            0x30, 0x0a, 0x00,
        )
        // @formatter:on

        val actual = DLTParser.parseVerbosePayload(false, 0, data, 0, Endian.BIG)
        val expected = VerbosePayload.Argument(
            131072,
            VerbosePayload.TypeInfo(
                typeString = true
            ),
            2,
            63,
            "Page flip enqueued on connector 260 with handler 0x73d8005740".toByteArray()
        )
        Assert.assertTrue("$actual != \n$expected", actual == expected)
    }
}