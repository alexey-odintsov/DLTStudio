package com.alekso.dltstudio.plugins.loginsights

import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltmessage.SampleData
import org.junit.Test
import kotlin.test.assertEquals

class InsightsTest {

    private val insightsRepository = InsightsRepository()

    @Test
    fun `Test insight parsing`() {
        val text = "34534 34543 1251529728 456"
        val insights = insightsRepository.findInsight(LogMessage(SampleData.create(payloadText = text)))
        val actual = insights[0].text
        val expected = "Possible TimeStamp found: 1251529728"
        assertEquals(expected, actual)
    }

}