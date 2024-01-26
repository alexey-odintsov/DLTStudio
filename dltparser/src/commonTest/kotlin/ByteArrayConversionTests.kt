import com.alekso.dltparser.Endian
import com.alekso.dltparser.readInt
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
}