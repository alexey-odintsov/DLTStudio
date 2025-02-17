package com.alekso.dltparser.tests

import com.alekso.dltparser.DLTParserV2
import com.alekso.dltmessage.PayloadStorageType
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import java.io.File

@Ignore("Ignore test for CI")
class PayloadStorageTypeMemoryTests {
    val payloadStorageType = PayloadStorageType.Binary
    val parser = DLTParserV2(payloadStorageType)

    @Test
    fun `PayloadStorageType memory usage test`() = runBlocking {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val beforeMemory = runtime.totalMemory() - runtime.freeMemory()
        println("Memory before parsing $beforeMemory")
        val messages = parser.read({}, listOf(File("${System.getProperty("user.home")}/Downloads/dlt/937mb_trace.dlt")))
        runtime.gc()
        val afterMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = afterMemory - beforeMemory
        println("PayloadStorageType: $payloadStorageType")
        println("Memory used: $memoryUsed bytes; Messages: ${messages.size}; Bytes/message: ${memoryUsed / messages.size}")
    }

}