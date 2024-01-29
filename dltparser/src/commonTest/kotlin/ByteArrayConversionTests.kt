import com.alekso.dltparser.Endian
import com.alekso.dltparser.readInt
import com.alekso.dltparser.readUShort
import org.junit.Assert
import org.junit.Test

class ByteArrayConversionTests {

    @Test
    fun testUIntL() {
        val data = byteArrayOf(0x9b.toByte(), 0x13.toByte(), 0x00.toByte(), 0x00.toByte())
        val actual = data.readInt(0, Endian.LITTLE).toUInt()
        val expected = 5019U
        Assert.assertTrue("$actual != $expected", actual == expected)
    }

    @Test
    fun testUShortB() {
        val data = byteArrayOf(0x00.toByte(), 0xa4.toByte(), 0x00.toByte(), 0x00.toByte())
        val actual = data.readUShort(0, Endian.BIG)
        val expected = 164U.toUShort()
        Assert.assertTrue("$actual != $expected", actual == expected)
    }
}