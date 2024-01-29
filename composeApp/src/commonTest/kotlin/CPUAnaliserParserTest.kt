import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.ui.cpu.CPUAnalyzer
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import org.junit.Assert
import org.junit.Test

class CPUAnalyzerParserTest {

    @Test
    fun testUIntL() {
        val data = "cpu0: 57.3% cpu1: 13% cpu2: 9.3%"
        val timestamp = 123
        val actual = CPUAnalyzer.analyzeCPUUsage(
            DLTMessage(
                timestamp, 123, "MGUA",
                StandardHeader(
                    StandardHeader.HeaderType(3.toByte(), true, true, true, true, true, 1.toByte()),
                    21U,
                    12U, "MON", 123, 23423U
                ),
                ExtendedHeader(
                    MessageInfo(
                        1,
                        true,
                        MessageInfo.MESSAGE_TYPE.DLT_TYPE_LOG,
                        MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_INFO
                    ), 1, "MON", "CPUC"
                ),
                VerbosePayload(
                    listOf(
                        VerbosePayload.Argument(
                            1,
                            VerbosePayload.TypeInfo(
                                1,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true,
                                false,
                                false,
                                false,
                                false,
                                VerbosePayload.TypeInfo.STRING_CODING.UTF8
                            ),
                            1,
                            2,
                            data.toByteArray()
                        )
                    )
                ), 1
            )
        )
        val expected = CPUUsageEntry(timestamp, listOf(57.3f, 13f, 9.3f))
        Assert.assertTrue("$actual != $expected", actual == expected)
    }
}