import com.alekso.dltparser.toInt32l
import org.junit.Assert
import org.junit.Test

class ByteArrayConversionTests {

    @Test
    fun testUIntL() {
        val data = byteArrayOf(0x9b.toByte(), 0x13.toByte(), 0x00.toByte(), 0x00.toByte())
        val actual = data.toInt32l().toUInt()
        val expected = 5019U
        Assert.assertTrue("$actual != $expected", actual == expected)
    }
}